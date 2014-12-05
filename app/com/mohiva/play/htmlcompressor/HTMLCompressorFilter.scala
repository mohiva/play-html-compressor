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
import play.api.libs.iteratee.Enumerator
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.play.compressor.CompressorFilter

/**
 * Uses Google's HTML Processor to compress the HTML code of a response.
 *
 * @param f Function which returns the configured HTML compressor.
 */
class HTMLCompressorFilter(f: => HtmlCompressor) extends CompressorFilter[HtmlCompressor](f) {

  /**
   * Check if the given result is a HTML result.
   *
   * @param result The result to check.
   * @return True if the result is a HTML result, false otherwise.
   */
  override protected def isCompressible(result: Result): Boolean = {
    lazy val contentTypeHtml = result.header.headers.get(HeaderNames.CONTENT_TYPE).exists(_.contains(MimeTypes.HTML))
    lazy val htmlEnumerator = manifest[Enumerator[Html]].runtimeClass.isInstance(result.body)
    super.isCompressible(result) && contentTypeHtml && htmlEnumerator
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
    compressor.setRemoveIntertagSpaces(false)
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
