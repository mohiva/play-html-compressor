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
package com.mohiva.play.htmlcompressor.java

import com.mohiva.play.htmlcompressor.{ HTMLCompressorFilter => ScalaHTMLCompressorFilter }
import com.googlecode.htmlcompressor.compressor.HtmlCompressor

/**
 * Implementation of the HTML compressor filter which can be used in Java.
 *
 * @param builder A builder instance which provides a configured HTML compressor instance.
 */
class HTMLCompressorFilter(val builder: HTMLCompressorBuilder) extends ScalaHTMLCompressorFilter(builder.build) {

  /**
   * Builds the default HTML compressor filter.
   *
   * @return The default HTML compressor filter.
   */
  def this() = this(new HTMLCompressorBuilder {
    override def build = ScalaHTMLCompressorFilter.default
  })
}

/**
 * Builds the Google HTML compressor instance.
 */
trait HTMLCompressorBuilder {

  /**
   * Gets the configured HTML compressor instance.
   *
   * @return The configured HTML compressor instance.
   */
  def build: HtmlCompressor
}
