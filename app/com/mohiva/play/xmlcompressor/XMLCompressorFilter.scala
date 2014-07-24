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
package com.mohiva.play.xmlcompressor

import play.twirl.api.Xml
import play.api.mvc._
import play.api.http.HeaderNames
import play.api.libs.iteratee.Enumerator
import com.googlecode.htmlcompressor.compressor.XmlCompressor
import com.mohiva.play.compressor.CompressorFilter

/**
 * Uses Google's XML Processor to compress the XML code of a response.
 *
 * @param f Function which returns the configured XML compressor.
 */
class XMLCompressorFilter(f: => XmlCompressor) extends CompressorFilter[XmlCompressor](f) {

  /**
   * Check if the given result is a XML result.
   *
   * @param result The result to check.
   * @return True if the result is a XML result, false otherwise.
   */
  protected def isCompressible(result: Result) = {
    result.header.headers.contains(HeaderNames.CONTENT_TYPE) &&
      // We cannot simply look for MimeTypes.XML because of things like "application/atom+xml".
      result.header.headers.apply(HeaderNames.CONTENT_TYPE).contains("xml") &&
      manifest[Enumerator[Xml]].runtimeClass.isInstance(result.body)
  }
}

/**
 * Default implementation of the XML compressor filter.
 */
object XMLCompressorFilter {

  /**
   * Gets the default Google XML compressor instance.
   */
  lazy val default = {
    // All XmlCompressor options default to true - so nothing to do here...
    new XmlCompressor()
  }

  /**
   * Creates the XML compressor filter.
   *
   * @return The XML compressor filter.
   */
  def apply(): XMLCompressorFilter = new XMLCompressorFilter(default)
}
