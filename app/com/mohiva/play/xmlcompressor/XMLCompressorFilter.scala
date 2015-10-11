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

import javax.inject.Inject

import play.api.{ Environment, Configuration }
import play.api.inject.Module
import play.twirl.api.Xml
import play.api.mvc._
import play.api.http.HeaderNames
import play.api.libs.iteratee.Enumerator
import com.googlecode.htmlcompressor.compressor.XmlCompressor
import com.mohiva.play.compressor.CompressorFilter

/**
 * Uses Google's XML Processor to compress the XML code of a response.
 */
abstract class XMLCompressorFilter extends CompressorFilter[XmlCompressor] {

  /**
   * Check if the given result is a XML result.
   *
   * @param result The result to check.
   * @return True if the result is a XML result, false otherwise.
   */
  override protected def isCompressible(result: Result) = {
    // We cannot simply look for MimeTypes.XML because of things like "application/atom+xml".
    lazy val contentTypeXml = result.header.headers.get(HeaderNames.CONTENT_TYPE).exists(_.contains("xml"))
    lazy val xmlEnumerator = manifest[Enumerator[Xml]].runtimeClass.isInstance(result.body)
    super.isCompressible(result) && contentTypeXml && xmlEnumerator
  }
}

/**
 * The default implementation of the [[XMLCompressorFilter]].
 *
 * @param configuration The Play configuration.
 */
class DefaultXMLCompressorFilter @Inject() (val configuration: Configuration) extends XMLCompressorFilter {

  /**
   * The compressor instance.
   */
  override val compressor: XmlCompressor = new XmlCompressor()
}

/**
 * Play module for providing the XML compressor filter.
 */
class XMLCompressorFilterModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = {
    Seq(
      bind[XMLCompressorFilter].to[DefaultXMLCompressorFilter]
    )
  }
}

/**
 * Injection helper for the XML compressor filter.
 */
trait XMLCompressorFilterComponents {

  def configuration: Configuration

  lazy val filter: XMLCompressorFilter = new DefaultXMLCompressorFilter(configuration)
}
