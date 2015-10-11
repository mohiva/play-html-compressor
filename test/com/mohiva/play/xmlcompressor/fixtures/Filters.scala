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
package com.mohiva.play.xmlcompressor.fixtures

import javax.inject.Inject

import com.googlecode.htmlcompressor.compressor.XmlCompressor
import com.mohiva.play.xmlcompressor.XMLCompressorFilter
import play.api.Configuration
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter
import play.filters.gzip.GzipFilter

/**
 * A custom XML compressor filter.
 */
class CustomXMLCompressorFilter @Inject() (val configuration: Configuration) extends XMLCompressorFilter {
  override val compressor = {
    val c = new XmlCompressor()
    c.setRemoveComments(false)
    c
  }
}

/**
 * Provides the default XML compressor filter.
 */
class DefaultFilter @Inject() (xmlCompressorFilter: XMLCompressorFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(xmlCompressorFilter)
}

/**
 * Provides the default XML compressor filter with a Gzip filter.
 */
class WithGzipFilter @Inject() (xmlCompressorFilter: XMLCompressorFilter, gzipFilter: GzipFilter) extends HttpFilters {
  override def filters: Seq[EssentialFilter] = Seq(gzipFilter, xmlCompressorFilter)
}
