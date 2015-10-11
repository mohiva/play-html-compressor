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
package com.mohiva.play.htmlcompressor.fixtures

import javax.inject.Inject

import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import play.api.{ Environment, Mode, Configuration }
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import play.filters.gzip.GzipFilter

/**
 * A custom HTML compressor filter.
 */
class CustomHTMLCompressorFilter @Inject() (val configuration: Configuration, environment: Environment)
    extends HTMLCompressorFilter {

  override val compressor: HtmlCompressor = {
    val c = new HtmlCompressor()
    if (environment.mode == Mode.Dev) {
      c.setPreserveLineBreaks(true)
    }

    c.setRemoveComments(true)
    c.setRemoveIntertagSpaces(true)
    c.setRemoveHttpProtocol(true)
    c.setRemoveHttpsProtocol(true)
    c
  }
}

/**
 * Provides the default HTML compressor filter.
 */
class DefaultFilter @Inject() (htmlCompressorFilter: HTMLCompressorFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(htmlCompressorFilter)
}

/**
 * Provides the default HTML compressor filter with a Gzip filter.
 */
class WithGzipFilter @Inject() (htmlCompressorFilter: HTMLCompressorFilter, gzipFilter: GzipFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(gzipFilter, htmlCompressorFilter)
}
