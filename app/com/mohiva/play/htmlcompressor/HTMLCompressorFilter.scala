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
package com.mohiva.play.htmlcompressor

import play.api.Play
import play.api.Play.current
import play.api.mvc._
import play.api.templates.Html
import play.api.http.{MimeTypes, HeaderNames}
import play.api.libs.iteratee.{Enumerator, Iteratee}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.googlecode.htmlcompressor.compressor.HtmlCompressor

/**
 * Uses Google's HTML Processor to compress the HTML code of a response.
 *
 * @param f Function which returns the configured HTML compressor.
 *
 * @see http://jazzy.id.au/default/2013/02/16/understanding_the_play_filter_api.html
 * @see http://stackoverflow.com/questions/14154671/is-it-possible-to-prettify-scala-templates-using-play-framework-2
 * @author Christian Kaps `christian.kaps@mohiva.com`
 */
class HTMLCompressorFilter(f: => HtmlCompressor) extends Filter {

  /**
   * The charset used by Play.
   */
  lazy val charset = Play.configuration.getString("default.charset").getOrElse("utf-8")

  /**
   * The HTML compressor instance.
   */
  lazy val compressor = f

  /**
   * Apply the filter.
   *
   * @param next The action to filter.
   * @return The filtered action.
   */
  def apply(nextFilter: (RequestHeader) => Future[SimpleResult])
           (requestHeader: RequestHeader): Future[SimpleResult] = {
    nextFilter(requestHeader).map { result => compressResult(result) }
  }

  /**
   * Compress the result.
   *
   * It compresses only HTML templates.
   *
   * @param result The result to compress.
   * @return The compressed result.
   */
  private def compressResult(result: SimpleResult): SimpleResult = result match {
    case simple @ SimpleResult(header, bodyEnumerator, connection) if isHtml(simple) => SimpleResult(
      header,
      Enumerator.flatten(
        Iteratee.flatten(bodyEnumerator.apply(bodyAsString)).run.map { str =>
          Enumerator(compressor.compress(str.trim).getBytes(charset))
        }),
      connection)
    case _ => result
  }

  /**
   * Check if the given result is a HTML result.
   *
   * @param result The result to check.
   * @return True if the result is a HTML result, false otherwise.
   */
  private def isHtml(result: SimpleResult) = {
    result.header.headers.contains(HeaderNames.CONTENT_TYPE) &&
    result.header.headers.apply(HeaderNames.CONTENT_TYPE).contains(MimeTypes.HTML) &&
    manifest[Enumerator[Html]].runtimeClass.isInstance(result.body)
  }

  /**
   * Converts the body of a result as string.
   *
   * @return The body of a result as string.
   */
  private def bodyAsString[A] = Iteratee.fold[A, String]("") { (str, body) => body match {
    case string: String => str + string
    case template: Html => str + template.body
    case bytes: Array[Byte] => str + new String(bytes, charset)
    case _ => throw new Exception("Unexpected body: " + body)
  }}
}

/**
 * Default implementation of the HTML compressor filter.
 */
object HTMLCompressorFilter {

  /**
   * Creates the HTML compressor filter.
   *
   * @return The HTML compressor filter.
   */
  def apply(): HTMLCompressorFilter = new HTMLCompressorFilter({
    val compressor = new HtmlCompressor()
    if (Play.isDev) {
      compressor.setPreserveLineBreaks(true)
    }

    compressor.setRemoveComments(true)
    compressor.setRemoveIntertagSpaces(true)
    compressor.setRemoveHttpProtocol(true)
    compressor.setRemoveHttpsProtocol(true)
    compressor
  })
}
