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

import javax.inject.Inject

import akka.stream.Materializer
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.play.compressor.CompressorFilter
import play.api.http.MimeTypes
import play.api.inject.Module
import play.api.mvc._
import play.api.{ Configuration, Environment, Mode }

/**
 * Uses Google's HTML Processor to compress the HTML code of a response.
 */
abstract class HTMLCompressorFilter extends CompressorFilter[HtmlCompressor] {

  /**
   * Check if the given result is a HTML result.
   *
   * @param result The result to check.
   * @return True if the result is a HTML result, false otherwise.
   */
  override protected def isCompressible(result: Result): Boolean = {
    val contentTypeHtml = result.body.contentType.exists {
      _.contains(MimeTypes.HTML)
    }
    super.isCompressible(result) && contentTypeHtml
  }
}

/**
 * The default implementation of the [[HTMLCompressorFilter]].
 *
 * @param configuration The Play configuration.
 * @param environment   The Play environment.
 */
class DefaultHTMLCompressorFilter @Inject() (val configuration: Configuration, environment: Environment, val mat: Materializer)
  extends HTMLCompressorFilter {

  /**
   * The compressor instance.
   */
  override val compressor: HtmlCompressor = {
    val c = new HtmlCompressor()
    c.setPreserveLineBreaks(
      configuration
        .getOptional[Boolean]("play.filters.compressor.html.preserveLineBreaks")
        .getOrElse(environment.mode == Mode.Dev)
    )
    c.setRemoveComments(
      configuration
        .getOptional[Boolean]("play.filters.compressor.html.removeComments")
        .getOrElse(true)
    )
    c.setRemoveIntertagSpaces(
      configuration
        .getOptional[Boolean]("play.filters.compressor.html.removeIntertagSpaces")
        .getOrElse(false)
    )
    c.setRemoveHttpProtocol(
      configuration
        .getOptional[Boolean]("play.filters.compressor.html.removeHttpProtocol")
        .getOrElse(true)
    )
    c.setRemoveHttpsProtocol(
      configuration
        .getOptional[Boolean]("play.filters.compressor.html.removeHttpsProtocol")
        .getOrElse(true)
    )
    c
  }
}

/**
 * Play module for providing the HTML compressor filter.
 */
class HTMLCompressorFilterModule extends Module {
  def bindings(environment: Environment, configuration: Configuration) = {
    Seq(
      bind[HTMLCompressorFilter].to[DefaultHTMLCompressorFilter]
    )
  }
}

/**
 * Injection helper for the HTML compressor filter.
 */
trait HTMLCompressorFilterComponents {

  def configuration: Configuration

  def environment: Environment

  def mat: Materializer

  lazy val htmlCompressorFilter: HTMLCompressorFilter = new DefaultHTMLCompressorFilter(configuration, environment, mat)
}
