# This project is not maintained anymore.

A fork can be found under: https://github.com/fkoehler/play-html-compressor



# Google's HTML (and XML) Compressor for Play Framework 2 [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.mohiva/play-html-compressor_2.11/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.mohiva/play-html-compressor_2.11) [![Build Status](https://travis-ci.org/mohiva/play-html-compressor.png)](https://travis-ci.org/mohiva/play-html-compressor)
[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/mohiva/play-html-compressor?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

## Installation

In your project/Build.scala:
```scala
libraryDependencies ++= Seq(
  "com.mohiva" %% "play-html-compressor" % "0.7.1"
)
```

### History

* For Play Framework 2.6 use version 0.7.1
* For Play Framework 2.5 use version 0.6.3
* For Play Framework 2.4 use version 0.5.0
* For Play Framework 2.3 use version 0.3.1
* For Play Framework 2.2 use version 0.2.1
* For Play Framework 2.1 use version 0.1-SNAPSHOT

## How to use

The filter comes with built-in `HtmlCompressor` and `XmlCompressor`
configurations, but it can also be used with user-defined configurations. The
following two examples shows how to define the filters with the default and the
user-defined configurations.

To provide the filters for your application you must define it as described in the Play
Documentation ([Scala](https://www.playframework.com/documentation/2.6.x/ScalaHttpFilters#Using-filters), [Java](https://www.playframework.com/documentation/2.6.x/JavaHttpFilters#Using-filters)).

### Provide filters

#### For Scala users

```scala
import javax.inject.Inject

import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import com.mohiva.play.xmlcompressor.XMLCompressorFilter
import play.api.http.HttpFilters
import play.api.mvc.EssentialFilter

class Filters @Inject() (
  htmlCompressorFilter: HTMLCompressorFilter,
  xmlCompressorFilter: XMLCompressorFilter)
  extends HttpFilters {

  override def filters: Seq[EssentialFilter] = Seq(
    htmlCompressorFilter,
    xmlCompressorFilter
  )
}
```

#### For Java users

```java
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter;
import com.mohiva.play.xmlcompressor.XMLCompressorFilter;
import play.mvc.EssentialFilter;
import play.http.HttpFilters;

import javax.inject.Inject;

public class DefaultFilter implements HttpFilters {

    private HTMLCompressorFilter htmlCompressorFilter;
    private XMLCompressorFilter xmlCompressorFilter;

    @Inject
    public DefaultFilter(
        HTMLCompressorFilter htmlCompressorFilter,
        XMLCompressorFilter xmlCompressorFilter) {

        this.htmlCompressorFilter = htmlCompressorFilter;
        this.xmlCompressorFilter = xmlCompressorFilter;
    }

    @Override
    public EssentialFilter[] filters() {
        return new EssentialFilter[] {
            htmlCompressorFilter.asJava(),
            xmlCompressorFilter.asJava()
        };
    }
}

```

### Default filter

For the default filters we provide DI modules which will be automatically enabled if you
pull in the dependency. You must only provide your instance of `HttpFilters` as described
above.

### User-defined filter

For user defined filters there is a little bit mor to do. First you must create your instances of
the filter. As next you must provide your instance of `HttpFilters` as described above. At last
you must provide the bindings for you created filter and disable the default DI modules.

#### Implement filters

##### For Scala users

```scala
import javax.inject.Inject

import akka.stream.Materializer
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import play.api.{Configuration, Environment, Mode}

class CustomHTMLCompressorFilter @Inject() (
  val configuration: Configuration, environment: Environment, val mat: Materializer)
  extends HTMLCompressorFilter {

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

```

##### For Java users

```java
import akka.stream.Materializer;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter;
import play.Environment;
import play.Mode;
import play.api.Configuration;

import javax.inject.Inject;

public class CustomHTMLCompressorFilter extends HTMLCompressorFilter {

    private Configuration configuration;
    private Environment environment;
    private Materializer mat;

    @Inject
    public CustomHTMLCompressorFilter(
        Configuration configuration, Environment environment, Materializer mat) {

        this.configuration = configuration;
        this.environment = environment;
        this.mat = mat;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public HtmlCompressor compressor() {
        HtmlCompressor compressor = new HtmlCompressor();
        if (environment.mode() == Mode.DEV) {
            compressor.setPreserveLineBreaks(true);
        }

        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        compressor.setRemoveHttpProtocol(true);
        compressor.setRemoveHttpsProtocol(true);

        return compressor;
    }

    @Override
    public Materializer mat() {
        return mat;
    }

}

```

#### Provide bindings

To provide your bindings for your user defined filter you must either create a new module
or you can add the binding to your default DI module. This process is detailed documented
for [Scala](https://www.playframework.com/documentation/2.6.x/ScalaDependencyInjection) and
[Java](https://www.playframework.com/documentation/2.6.x/JavaDependencyInjection) users. So
please refer to this documentation.

##### Disable default modules

To disable the default modules you must append the modules to the `play.modules.disabled` property in `application.conf`:

```scala
play.modules.disabled += "com.mohiva.play.htmlcompressor.HTMLCompressorFilterModule"
play.modules.disabled += "com.mohiva.play.xmlcompressor.XMLCompressorFilterModule"
```

### Customize filter behaviour

You have the possibility to customize filter behaviour without using class inheritance. For
that, you could adding the following keys on your `application.conf` file :

```scala
play.filters {

  # Mohiva Compressor
  # ~~~~~
  # https://github.com/mohiva/play-html-compressor
  compressor {
    html {
      preserveLineBreaks = false
      removeComments = true
      removeIntertagSpaces = false
      removeHttpProtocol = true
      removeHttpsProtocol = true
    }

    xml {
      removeComments = true
      removeIntertagSpaces = true
    }
  }
}
```
