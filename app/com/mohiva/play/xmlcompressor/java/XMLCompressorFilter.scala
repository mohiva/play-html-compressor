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
 * @param builder A builder instance which provides a configured XML compressor instance.
 */
class XMLCompressorFilter(val builder: XMLCompressorBuilder) extends ScalaXMLCompressorFilter(builder.build) {

  /**
   * Builds the default XML compressor filter.
   *
   * @return The default XML compressor filter.
   */
  def this() = this(new XMLCompressorBuilder {
    override def build = ScalaXMLCompressorFilter.default
  })
}

/**
 * Builds the Google XML compressor instance.
 */
trait XMLCompressorBuilder {

  /**
   * Gets the configured XML compressor instance.
   *
   * @return The configured XML compressor instance.
   */
  def build: XmlCompressor
}
