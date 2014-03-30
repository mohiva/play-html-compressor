# Google's HTML Compressor for Play Framework 2[![Build Status](https://travis-ci.org/mohiva/play-html-compressor.png)](https://travis-ci.org/mohiva/play-html-compressor)

## Installation

Note: The module is currently only available as snapshot.

In your project/Build.scala:
```scala
libraryDependencies ++= Seq(
  "com.mohiva" %% "play-html-compressor" % "0.2.1"
)
```

If you want to use the latest snapshot, add the following instead:
```scala
resolvers += "Sonatype Snapshots" at "http://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-html-compressor" % "0.2.1-SNAPSHOT"
)
```

For Play Framework 2.1 use the 0.1 snapshot.

## How to use

The filter comes with a built-in `HtmlCompressor` configuration, but it can also be used with a user-defined configuration. The following two examples shows how to define the filter with the default and the user-defined configuration.

### Default filter

The default filter has the same configuration as the user-defined filter below.

#### For Scala users

```scala
import play.api.mvc.WithFilters
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter

/**
 * Uses the default implementation of the HTML compressor filter.
 */
object Global extends WithFilters(HTMLCompressorFilter())
```

#### For Java users

```java
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import com.mohiva.play.htmlcompressor.java.HTMLCompressorFilter;

/**
 * Uses the default implementation of the HTML compressor filter.
 */
public class Global extends GlobalSettings {

    /**
     * Get the filters that should be used to handle each request.
     */
    @SuppressWarnings("unchecked")
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{HTMLCompressorFilter.class};
    }
}
```

### User-defined filter

#### For Scala users

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

#### For Java users

```java
import play.Play;
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import com.mohiva.play.htmlcompressor.java.HTMLCompressorFilter;
import com.mohiva.play.htmlcompressor.java.HTMLCompressorBuilder;

/**
 * Uses a user-defined implementation of the HTML compressor filter.
 */
public class Global extends GlobalSettings {

    /**
     * Get the filters that should be used to handle each request.
     */
    @SuppressWarnings("unchecked")
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[]{CustomHTMLCompressorFilter.class};
    }

    /**
     * Custom implementation of the HTML compressor filter.
     */
    public static class CustomHTMLCompressorFilter extends HTMLCompressorFilter {
        public CustomHTMLCompressorFilter() {
            super(new CustomHTMLCompressorBuilder());
        }
    }

    /**
     * The builder for the custom HTML compressor.
     */
    public static class CustomHTMLCompressorBuilder implements HTMLCompressorBuilder {
        public HtmlCompressor build() {
            HtmlCompressor compressor = new HtmlCompressor();
            if (Play.isDev()) {
                compressor.setPreserveLineBreaks(true);
            }

            compressor.setRemoveComments(true);
            compressor.setRemoveIntertagSpaces(true);
            compressor.setRemoveHttpProtocol(true);
            compressor.setRemoveHttpsProtocol(true);
            return compressor;
        }
    }
}
```
