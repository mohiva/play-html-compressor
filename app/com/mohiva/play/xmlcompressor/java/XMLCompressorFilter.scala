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
package com.mohiva.play.xmlcompressor.java

import com.mohiva.play.xmlcompressor.{ XMLCompressorFilter => ScalaXMLCompressorFilter }
import com.googlecode.htmlcompressor.compressor.XmlCompressor

/**
 * Implementation of the XML compressor filter which can be used in Java.
 *
 * @param builder A builder instance which provides a configured HTML compressor instance.
 * @author Christian Kaps `christian.kaps@mohiva.com`
 */
class HTMLCompressorFilter(val builder: XMLCompressorBuilder) extends ScalaXMLCompressorFilter(builder.build) {

  /**
   * Builds the default HTML compressor filter.
   *
   * @return The default HTML compressor filter.
   */
  def this() = this(new XMLCompressorBuilder {
    override def build = ScalaXMLCompressorFilter.default
  })
}

/**
 * Builds the Google HTML compressor instance.
 */
trait XMLCompressorBuilder {

  /**
   * Gets the configured HTML compressor instance.
   *
   * @return The configured HTML compressor instance.
   */
  def build: XmlCompressor
}
