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

import akka.stream.Materializer
import com.googlecode.htmlcompressor.compressor.XmlCompressor
import com.mohiva.play.compressor.CompressorFilter
import play.api.inject.Module
import play.api.mvc._
import play.api.{ Configuration, Environment }

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
    lazy val contentTypeXml = result.body.contentType.exists(_.contains("xml"))
    super.isCompressible(result) && contentTypeXml
  }
}

/**
 * The default implementation of the [[XMLCompressorFilter]].
 *
 * @param configuration The Play configuration.
 */
class DefaultXMLCompressorFilter @Inject() (val configuration: Configuration, val mat: Materializer) extends XMLCompressorFilter {

  /**
   * The compressor instance.
   */
  override val compressor: XmlCompressor = {
    val c = new XmlCompressor()
    c.setRemoveComments(
      configuration
        .getOptional[Boolean]("play.filters.compressor.xml.removeComments")
        .getOrElse(true)
    )
    c.setRemoveIntertagSpaces(
      configuration
        .getOptional[Boolean]("play.filters.compressor.xml.removeIntertagSpaces")
        .getOrElse(true)
    )
    c
  }
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

  def mat: Materializer

  lazy val xmlCompressorFilter: XMLCompressorFilter = new DefaultXMLCompressorFilter(configuration, mat)
}
