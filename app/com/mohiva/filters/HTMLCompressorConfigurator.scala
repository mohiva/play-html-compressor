/**
 * Play HTML Compressor
 *
 * LICENSE
 *
 * This source file is subject to the new BSD license that is bundled
 * with this package in the file LICENSE.textile.
 * It is also available through the world-wide-web at this URL:
 * https://github.com/mohiva/play-html-compressor/blob/master/LICENSE.textile
 */
package com.mohiva.filters

import com.googlecode.htmlcompressor.compressor.HtmlCompressor

/**
 * Configures the HTML compressor instance.
 *
 * @author Christian Kaps `christian.kaps@mohiva.com`
 */
trait HTMLCompressorConfigurator {

  /**
   * Configures the HTML compressor instance.
   *
   * @return The configured HTML compressor instance.
   */
  def configure: HtmlCompressor
}
