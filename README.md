# Google's HTML Compressor for Play Framework 2.2(Scala)[![Build Status](https://travis-ci.org/mohiva/play-html-compressor.png)](https://travis-ci.org/mohiva/play-html-compressor)

## Installation

Note: The module is currently only available as snapshot.

In your project/Build.scala:
```scala
libraryDependencies ++= Seq(
  "com.mohiva" %% "play-html-compressor" % "0.2"
)
```

If you want to use the latest snapshot, add the following instead:
```scala
resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-html-compressor" % "0.2-SNAPSHOT"
)
```

## How to use

The filter comes with a built-in `HtmlCompressor` configuration, but it can also be used with a user-defined configuration. The follwing two examples shows how to define the filter with the default and the user-defined configuration.

### Default filter

The default filter has the same configuration as the user-defined filter below.

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
 * Uses a user-defined implementation of the HTML compressor filter.
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
