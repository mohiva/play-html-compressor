package com.mohiva.play.compressor

import play.twirl.api.Content
import play.api.mvc._
import play.api.Play
import play.api.Play.current
import play.api.libs.iteratee.{ Enumerator, Iteratee }
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.googlecode.htmlcompressor.compressor.Compressor
import scala.reflect.ClassTag

abstract class AbstractCompressorFilter[C <: Compressor, T <: Content : ClassTag](f: => C) extends Filter {

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
    next(rh).map(result => compressResult(result))
  }

  /**
   * Compress the result.
   *
   * @param result The result to compress.
   * @return The compressed result.
   */
  private def compressResult(result: Result) = if (isCompressible(result)) {
    result.copy(body = Enumerator.flatten(
      Iteratee.flatten(result.body.apply(bodyAsString)).run.map { str =>
        Enumerator(compressor.compress(str.trim).getBytes(charset))
      }
    ))
  } else result

  /**
   * Check if the given result is a compressible result.
   *
   * @param result The result to check.
   * @return True if the result is a compressible result, false otherwise.
   */
  protected def isCompressible(result: Result): Boolean

  /**
   * Converts the body of a result as string.
   *
   * @return The body of a result as string.
   */
  private def bodyAsString[A] = Iteratee.fold[A, String]("") { (str, body) =>
    body match {
      case string: String => str + string
      case template: T => str + template.body
      case bytes: Array[Byte] => str + new String(bytes, charset)
      case _ => throw new Exception("Unexpected body: " + body)
    }
  }
}
