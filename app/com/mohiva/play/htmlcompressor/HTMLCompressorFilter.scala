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

import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.play.compressor.CompressorFilter
import play.api.http.{ HeaderNames, MimeTypes }
import play.api.inject.Module
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.api.{ Environment, Configuration, Mode }
import play.twirl.api.Html

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
    lazy val contentTypeHtml = result.header.headers.get(HeaderNames.CONTENT_TYPE).exists(_.contains(MimeTypes.HTML))
    lazy val htmlEnumerator = manifest[Enumerator[Html]].runtimeClass.isInstance(result.body)
    super.isCompressible(result) && contentTypeHtml && htmlEnumerator
  }
}

/**
 * The default implementation of the [[HTMLCompressorFilter]].
 *
 * @param configuration The Play configuration.
 * @param environment The Play environment.
 */
class DefaultHTMLCompressorFilter @Inject() (val configuration: Configuration, environment: Environment)
    extends HTMLCompressorFilter {

  /**
   * The compressor instance.
   */
  override val compressor: HtmlCompressor = {
    val c = new HtmlCompressor()
    if (environment.mode == Mode.Dev) {
      c.setPreserveLineBreaks(true)
    }

    c.setRemoveComments(true)
    c.setRemoveIntertagSpaces(false)
    c.setRemoveHttpProtocol(true)
    c.setRemoveHttpsProtocol(true)
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

  lazy val filter: HTMLCompressorFilter = new DefaultHTMLCompressorFilter(configuration, environment)
}
