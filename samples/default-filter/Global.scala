package default_filter

import com.mohiva.filters.HTMLCompressorFilter
import play.api.mvc.WithFilters

/**
 * Uses the default implementation of the HTML compressor filter.
 */
object Global extends WithFilters(HTMLCompressorFilter())
