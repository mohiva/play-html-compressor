/**
 * Play HTML Compressor
 *
 * LICENSE
 *
 * This source file is subject to the new BSD license that is bundled
 * with this package in the file LICENSE.md.
 * It is also available through the world-wide-web at this URL:
 * https://github.com/mohiva/play-html-compressor/blob/master/LICENSE.md
 */
package com.mohiva.play.compressor

import akka.stream.Materializer
import akka.stream.scaladsl._
import akka.util.ByteString
import com.googlecode.htmlcompressor.compressor.Compressor
import play.api.Configuration
import play.api.http.HeaderNames._
import play.api.http.{ HttpEntity, HttpProtocol }
import play.api.mvc._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Base implementation of a filter which makes it possible to compress either HTML or XML with the
 * help of Google's HTML Processor.
 *
 * @see http://jazzy.id.au/default/2013/02/16/understanding_the_play_filter_api.html
 * @see http://stackoverflow.com/questions/14154671/is-it-possible-to-prettify-scala-templates-using-play-framework-2
 */
abstract class CompressorFilter[C <: Compressor] extends Filter {

  /**
   * The compressor instance.
   */
  val compressor: C

  /**
   * The Play configuration instance.
   */
  val configuration: Configuration

  /**
   * The charset used by Play.
   */
  lazy val charset = configuration.getOptional[String]("default.charset").getOrElse("utf-8")

  /**
   * Materializer for the Filter.
   */
  override implicit val mat: Materializer

  /**
   * Apply the filter.
   *
   * @param next The action to filter.
   * @return The filtered action.
   */
  def apply(next: (RequestHeader) => Future[Result])(rh: RequestHeader) = {
    next(rh).flatMap(result =>
      compressResult(result)
    )
  }

  /**
   * Check if the given result is a compressible result.
   *
   * @param result The result to check.
   * @return True if the result is a compressible result, false otherwise.
   */
  protected def isCompressible(result: Result): Boolean = {
    val isChunked = result.header.headers.get(TRANSFER_ENCODING).contains(HttpProtocol.CHUNKED)
    val isGzipped = result.header.headers.get(CONTENT_ENCODING).contains("gzip")
    val ret = !isChunked && !isGzipped
    ret
  }

  /**
   * Compress the result.
   *
   * @param result The result to compress.
   * @return The compressed result.
   */
  private def compressResult(result: Result): Future[Result] = {

    def compress(data: ByteString) = compressor.compress(data.decodeString(charset).trim).getBytes(charset)

    if (isCompressible(result)) {
      result.body match {
        case body0: HttpEntity.Strict =>
          Future.successful(
            result.copy(
              body = body0.copy(
                data = ByteString(compress(body0.data))
              )
            )
          )
        case body0: HttpEntity.Streamed =>
          for {
            bytes <- body0.data.toMat(Sink.fold(ByteString())(_ ++ _))(Keep.right).run()
          } yield {
            val compressed = compress(bytes)
            val length = compressed.length
            result.copy(
              body = body0.copy(
                data = Source.single(ByteString(compressed)),
                contentLength = Some(length.toLong)
              )
            )
          }
        case _ =>
          Future.successful(result)
      }
    } else {
      Future.successful(result)
    }
  }

}
