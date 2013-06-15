package user_defined_filter

import play.api.mvc.WithFilters
import play.api.Play
import play.api.Play.current
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.filters.{HTMLCompressorFilter, HTMLCompressorConfigurator}

/**
 * Uses a user defined implementation of the HTML compressor filter.
 */
object Global extends WithFilters(HTMLCompressorFilter())

/**
 * Defines a user defined  HTML compressor filter.
 */
object HTMLCompressorFilter {

  /**
   * Creates the HTML compressor filter.
   *
   * @return The HTML compressor filter.
   */
  def apply() = new HTMLCompressorFilter()

  /**
   * Creates the HTML compressor configurator.
   *
   * @return The HTML compressor configurator.
   */
  implicit def configurator: HTMLCompressorConfigurator = new HTMLCompressorConfigurator {
    def configure: HtmlCompressor = {
      val compressor = new HtmlCompressor()
      if (Play.isDev) {
        compressor.setPreserveLineBreaks(true)
      }

      compressor.setRemoveComments(true)
      compressor.setRemoveIntertagSpaces(true)
      compressor.setRemoveHttpProtocol(true)
      compressor.setRemoveHttpsProtocol(true)
      compressor
    }
  }
}
