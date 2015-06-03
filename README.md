# Google's HTML (and XML) Compressor for Play Framework 2[![Build Status](https://travis-ci.org/mohiva/play-html-compressor.png)](https://travis-ci.org/mohiva/play-html-compressor)
[![Gitter](https://badges.gitter.im/Join Chat.svg)](https://gitter.im/mohiva/play-html-compressor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Installation

In your project/Build.scala:
```scala
libraryDependencies ++= Seq(
  "com.mohiva" %% "play-html-compressor" % "0.3.1"
)
```

If you want to use the latest snapshot, add the following instead:
```scala
resolvers += "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"

libraryDependencies ++= Seq(
  "com.mohiva" %% "play-html-compressor" % "0.4.1-SNAPSHOT"
)
```

### History

* For Play Framework 2.4 use version 0.4.1-SNAPSHOT
* For Play Framework 2.3 use version 0.3.1
* For Play Framework 2.2 use version 0.2.1
* For Play Framework 2.1 use version 0.1-SNAPSHOT

## How to use

The filter comes with built-in `HtmlCompressor` and `XmlCompressor`
configurations, but it can also be used with user-defined configurations. The
following two examples shows how to define the filters with the default and the
user-defined configurations.

### Default filter

The default HTMLCompressorFilter has the same configuration as the user-defined filter below.

#### For Scala users

```scala
import play.api.mvc.WithFilters
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import com.mohiva.play.xmlcompressor.XMLCompressorFilter

/**
 * Uses the default implementation of the HTML and XML compressor filters.
 */
object Global extends WithFilters(HTMLCompressorFilter(), XMLCompressorFilter())
```

#### For Java users

```java
import play.GlobalSettings;
import play.api.mvc.EssentialFilter;
import com.mohiva.play.htmlcompressor.java.HTMLCompressorFilter;
import com.mohiva.play.xmlcompressor.java.XMLCompressorFilter;

/**
 * Uses the default implementations of the HTML and XML compressor filters.
 */
public class Global extends GlobalSettings {

    /**
     * Get the filters that should be used to handle each request.
     */
    @SuppressWarnings("unchecked")
    public <T extends EssentialFilter> Class<T>[] filters() {
        return new Class[] {
            HTMLCompressorFilter.class,
            XMLCompressorFilter.class
        };
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
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
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

#### User-defined XMLCompressorFilter

You can also use a user defined XMLCompressorFilter. The approach is analogical
to the examples given above.

### HTMLCompressorFilter & GzipFilter

Be careful when using HTMLCompressorFilter in combination with the Play
GzipFilter. HTMLCompressorFilter can not work on source that has already been
gzipped.

Unfortunately, there is no official way to control the order in which filters
will be applied to responses.

Fortunately, Play Framework will apply the filters in reverse order of
appearance in the `WithFilters` constructor. So this code will work as expected
(applying HTMLCompressorFilter first and GzipFilter on the compressed HTML):

```scala
object Global extends WithFilters(new GzipFilter(), HTMLCompressorFilter()) {
  ...
}
```

This code will _not_ work, resulting in an empty response body (or worse):
```scala
object Global extends WithFilters(HTMLCompressorFilter(), new GzipFilter()) {
  ...
}
```
