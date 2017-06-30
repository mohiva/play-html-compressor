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
package com.mohiva.play.htmlcompressor.java;

import akka.stream.Materializer;
import akka.util.ByteString;
import com.mohiva.play.compressor.Helper;
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter;
import com.mohiva.play.htmlcompressor.fixtures.RequestHandler;
import com.mohiva.play.htmlcompressor.fixtures.java.CustomHTMLCompressorFilter;
import com.mohiva.play.htmlcompressor.fixtures.java.DefaultFilter;
import com.mohiva.play.htmlcompressor.fixtures.java.WithGzipFilter;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import play.Application;
import play.api.inject.guice.GuiceInjector;
import play.Environment;
import play.inject.guice.GuiceApplicationBuilder;
import play.Mode;
import play.mvc.Result;

import java.io.IOException;
import java.io.InputStream;
import java.util.Optional;

import static org.fest.assertions.Assertions.assertThat;
import static play.inject.Bindings.bind;
import static play.test.Helpers.*;

/**
 * Test case for the [[com.mohiva.play.htmlcompressor.java.HTMLCompressorFilter]] class.
 */
public class HTMLCompressorFilterTest {

    /**
     * Test if the default filter compress an HTML page.
     */
    @Test
    public void defaultFilterCompressHTMLPage() {

        running(defaultApp(), () -> {
            Result result = route(defaultApp(), fakeRequest(GET, "/action"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().get()).isEqualTo("text/html");
            assertThat(contentAsString(result, defaultApp().injector().instanceOf(Materializer.class))).startsWith("<!DOCTYPE html> <html> <head>");
        });
    }

    /**
     * Test if the default filter compress an async HTML page.
     */
    @Test
    public void defaultFilterCompressAsyncHTMLPage() {
        running(defaultApp(), () -> {
            Result result = route(defaultApp(), fakeRequest(GET, "/asyncAction"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().get()).isEqualTo("text/html");
            assertThat(contentAsString(result)).startsWith("<!DOCTYPE html> <html> <head>");
        });
    }

    /**
     * Test if the default filter does not compress a non HTML page.
     */
    @Test
    public void defaultFilterNotCompressNonHTMLPage() {
        running(defaultApp(), () -> {
            Result result = route(defaultApp(), fakeRequest(GET, "/nonHTML"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().get()).isEqualTo("text/plain");
            assertThat(contentAsString(result)).startsWith("  <html/>");
        });
    }

    /**
     * Test if the default filter compress a static HTML asset.
     */
    @Test
    public void defaultFilterCompressStaticAssets() {
        running(defaultApp(), () -> {
            InputStream is = new Environment(Mode.TEST).resourceAsStream("static.html");
            String file = "";
            try {
                file = IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Result result = route(defaultApp(), fakeRequest(GET, "/static"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().orElse("")).isEqualTo("text/html");
            assertThat(contentAsString(result, defaultApp().injector().instanceOf(Materializer.class))).startsWith("<!DOCTYPE html> <html> <head>");
            assertThat(result.header(CONTENT_LENGTH)).isNotEqualTo(String.valueOf(file.length()));
        });
    }


    /**
     * Test if result is not compressed when transfer encoding is set to chunked
     */
    @Test
    public void defaultFilterNotCompressChunkedResult() {
        running(defaultApp(), () -> {
            Result result = route(defaultApp(), fakeRequest(GET, "/chunked"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().get()).isEqualTo("text/html");
            assertThat(result.header(CONTENT_LENGTH)).isEqualTo(Optional.empty());
        });
    }

    /**
     * Test if the custom filter compress an HTML page.
     */
    @Test
    public void customFilterCompressHTMLPage() {
        running(customApp(), () -> {
            Result result = route(customApp(), fakeRequest(GET, "/action"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().get()).isEqualTo("text/html");
            assertThat(contentAsString(result)).startsWith("<!DOCTYPE html><html><head>");
        });
    }

    /**
     * Test if the custom filter compress an async HTML page.
     */
    @Test
    public void customFilterCompressAsyncHTMLPage() {
        running(customApp(), () -> {
            Result result = route(customApp(), fakeRequest(GET, "/asyncAction"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().get()).isEqualTo("text/html");
            assertThat(contentAsString(result)).startsWith("<!DOCTYPE html><html><head>");
        });
    }

    /**
     * Test if the custom filter does not compress a non HTML page.
     */
    @Test
    public void customFilterNotCompressNonHTMLPage() {
        running(customApp(), () -> {
            Result result = route(customApp(), fakeRequest(GET, "/nonHTML"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().get()).isEqualTo("text/plain");
            assertThat(contentAsString(result)).startsWith("  <html/>");
        });
    }

    /**
     * Test if the custom filter compress a static HTML asset.
     */
    @Test
    public void customFilterCompressStaticAssets() {
        running(customApp(), () -> {
            InputStream is = new Environment(Mode.TEST).resourceAsStream("static.html");
            String file = "";
            try {
                file = IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Result result = route(customApp(), fakeRequest(GET, "/static"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType().orElse("")).isEqualTo("text/html");
            assertThat(contentAsString(result, customApp().injector().instanceOf(Materializer.class))).startsWith("<!DOCTYPE html><html><head>");
            assertThat(result.header(CONTENT_LENGTH)).isNotEqualTo(String.valueOf(file.length()));
        });
    }

    /**
     * Test that a result is first HTML compressed and then gzipped
     */
    @Test
    public void defaultWithGzipFilterHtmlCompressesAndThenGzipsResult() {
        running(gzipApp(), () -> {
            Result original = route(gzipApp(), fakeRequest(GET, "/action"));
            Result gzipped  = route(gzipApp(), fakeRequest(GET, "/action").header(ACCEPT_ENCODING, "gzip"));

            assertThat(gzipped.status()).isEqualTo(OK);
            assertThat(gzipped.contentType().get()).isEqualTo("text/html");
            assertThat(gzipped.header(CONTENT_ENCODING)).isEqualTo(Optional.of("gzip"));
            assertThat(Helper.gunzip(contentAsBytes(gzipped))).isEqualTo(contentAsBytes(original));
        });
    }

    /**
     * Test if result is not compressed if it's already gzipped
     *
     * given static.html.gz == gzip(static.html)
     * when /static.html is requested
     * then Assets controller responds with static.html.gz
     * we don't want to further pass this through HTML Compressor
     */
    @Test
    public void defaultWithGzipFilterNotCompressGzippedResult() {
        running(gzipApp(), () -> {
            try {
                ByteString original = ByteString.fromArray(IOUtils.toByteArray(new Environment(Mode.TEST).resourceAsStream("static.html")));
                Result result = route(gzipApp(), fakeRequest(GET, "/gzipped").header(ACCEPT_ENCODING, "gzip"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType().get()).isEqualTo("text/html");
                assertThat(result.header(CONTENT_ENCODING)).isEqualTo(Optional.of("gzip"));
                assertThat(Helper.gunzip(contentAsBytes(result, gzipApp().injector().instanceOf(Materializer.class)))).isEqualTo(original);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }


    /**
     * An app with the default HTML compressor filter.
     */
    private Application defaultApp() {
        return new GuiceApplicationBuilder()
            .in(new Environment(Mode.TEST))
            .configure("play.http.filters", DefaultFilter.class.getCanonicalName())
            .configure("play.http.requestHandler", RequestHandler.class.getCanonicalName())
            .build();
    }

    /**
     * An app with the custom HTML compressor filter.
     */
    private Application customApp() {
        return new GuiceApplicationBuilder()
            .in(new Environment(Mode.TEST))
            .overrides(bind(HTMLCompressorFilter.class).to(CustomHTMLCompressorFilter.class))
            .configure("play.http.filters", DefaultFilter.class.getCanonicalName())
            .configure("play.http.requestHandler", RequestHandler.class.getCanonicalName())
            .build();
    }

    /**
     * An app with the gzip filter in place.
     */
    private Application gzipApp() {
        return new GuiceApplicationBuilder()
            .in(new Environment(Mode.TEST))
            .configure("play.http.filters", WithGzipFilter.class.getCanonicalName())
            .configure("play.http.requestHandler", RequestHandler.class.getCanonicalName())
            .build();
    }
}
