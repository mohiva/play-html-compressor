# Google's HTML Compressor for Play Framework 2.1(Scala)


## How to use

The filter comes with a built-in `HtmlCompressor` configuration, but it can also be used with a user-defined configuration. The follwing two examples shows how to define the filter with the default and the user-defined configuration.

### Default filter

```scala
import play.api.mvc.WithFilters
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter

/**
 * Uses the default implementation of the HTML compressor filter.
 */
object Global extends WithFilters(HTMLCompressorFilter())
```

### User-defined filter

```scala

import play.api.mvc.WithFilters
import play.api.Play
import play.api.Play.current
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter

/**
 * Uses a user defined implementation of the HTML compressor filter.
 */
object Global extends WithFilters(HTMLCompressorFilter())

/**
 * Defines a user-defined HTML compressor filter.
 */
object HTMLCompressorFilter {

  /**
   * Creates the HTML compressor filter.
   *
   * @return The HTML compressor filter.
   */
  def apply() = new HTMLCompressorFilter({
    val compressor = new HtmlCompressor()
    if (Play.isDev) {
      compressor.setPreserveLineBreaks(true)
    }

    compressor.setRemoveComments(true)
    compressor.setRemoveIntertagSpaces(true)
    compressor.setRemoveHttpProtocol(true)
    compressor.setRemoveHttpsProtocol(true)
    compressor
  })
}

```
