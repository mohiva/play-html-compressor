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

import play.twirl.api.Html
import play.api.mvc._
import play.api.Play
import play.api.Play.current
import play.api.http.{ MimeTypes, HeaderNames }
import play.api.libs.iteratee.{ Enumerator, Iteratee }
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.play.compressor.AbstractCompressorFilter

/**
 * Uses Google's HTML Processor to compress the HTML code of a response.
 *
 * @param f Function which returns the configured HTML compressor.
 *
 * @see http://jazzy.id.au/default/2013/02/16/understanding_the_play_filter_api.html
 * @see http://stackoverflow.com/questions/14154671/is-it-possible-to-prettify-scala-templates-using-play-framework-2
 * @author Christian Kaps `christian.kaps@mohiva.com`
 */
class HTMLCompressorFilter(f: => HtmlCompressor) extends AbstractCompressorFilter[HtmlCompressor, Html](f) {

  /**
   * Check if the given result is a HTML result.
   *
   * @param result The result to check.
   * @return True if the result is a HTML result, false otherwise.
   */
  protected def isCompressible(result: Result): Boolean = {
    result.header.headers.contains(HeaderNames.CONTENT_TYPE) &&
      result.header.headers.apply(HeaderNames.CONTENT_TYPE).contains(MimeTypes.HTML) &&
      manifest[Enumerator[Html]].runtimeClass.isInstance(result.body)
  }
}

/**
 * Default implementation of the HTML compressor filter.
 */
object HTMLCompressorFilter {

  /**
   * Gets the default Google HTML compressor instance.
   */
  lazy val default = {
    val compressor = new HtmlCompressor()
    if (Play.isDev) {
      compressor.setPreserveLineBreaks(true)
    }

    compressor.setRemoveComments(true)
    compressor.setRemoveIntertagSpaces(true)
    compressor.setRemoveHttpProtocol(true)
    compressor.setRemoveHttpsProtocol(true)
    compressor
  }

  /**
   * Creates the HTML compressor filter.
   *
   * @return The HTML compressor filter.
   */
  def apply(): HTMLCompressorFilter = new HTMLCompressorFilter(default)
}
