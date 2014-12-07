package com.mohiva.play.compressor

import play.api.http.HttpProtocol
import play.twirl.api.Content
import play.api.mvc._
import play.api.Play
import play.api.Play.current
import play.api.http.HeaderNames._
import play.api.libs.iteratee.{ Enumerator, Iteratee }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.googlecode.htmlcompressor.compressor.Compressor

/**
 * Base implementation of a filter which makes it possible to compress either HTML or XML with the
 * help of Google's HTML Processor.
 *
 * @see http://jazzy.id.au/default/2013/02/16/understanding_the_play_filter_api.html
 * @see http://stackoverflow.com/questions/14154671/is-it-possible-to-prettify-scala-templates-using-play-framework-2
 */
abstract class CompressorFilter[C <: Compressor](f: => C) extends Filter {

  /**
   * The charset used by Play.
   */
  lazy val charset = Play.configuration.getString("default.charset").getOrElse("utf-8")

  /**
   * The compressor instance.
   */
  lazy val compressor = f

  /**
   * Apply the filter.
   *
   * @param next The action to filter.
   * @return The filtered action.
   */
  def apply(next: (RequestHeader) => Future[Result])(rh: RequestHeader) = {
    next(rh).flatMap(result => compressResult(result))
  }

  /**
   * Check if the given result is a compressible result.
   *
   * @param result The result to check.
   * @return True if the result is a compressible result, false otherwise.
   */
  protected def isCompressible(result: Result): Boolean = {
    !result.header.headers.get(TRANSFER_ENCODING).exists(_ == HttpProtocol.CHUNKED)
  }

  /**
   * Compress the result.
   *
   * @param result The result to compress.
   * @return The compressed result.
   */
  private def compressResult(result: Result): Future[Result] = if (isCompressible(result)) {
    Iteratee.flatten(result.body.apply(bodyAsString)).run.map { str =>
      val compressed = compressor.compress(str.trim).getBytes(charset)
      val length = compressed.length
      length -> Enumerator(compressed)
    }.map {
      case (length, content) =>
        result.copy(
          header = result.header.copy(headers = result.header.headers ++ Map(CONTENT_LENGTH -> length.toString)),
          body = Enumerator.flatten(Future.successful(content))
        )
    }
  } else Future.successful(result)

  /**
   * Converts the body of a result as string.
   *
   * @return The body of a result as string.
   */
  private def bodyAsString[A] = Iteratee.fold[A, String]("") { (str, body) =>
    body match {
      case string: String => str + string
      case template: Content => str + template.body
      case bytes: Array[Byte] => str + new String(bytes, charset)
      case _ => throw new Exception("Unexpected body: " + body)
    }
  }
}
