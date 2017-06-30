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
package com.mohiva.play.xmlcompressor.java;

import akka.stream.Materializer;
import akka.util.ByteString;
import com.mohiva.play.compressor.Helper;
import com.mohiva.play.xmlcompressor.XMLCompressorFilter;
import com.mohiva.play.xmlcompressor.fixtures.RequestHandler;
import com.mohiva.play.xmlcompressor.fixtures.java.CustomXMLCompressorFilter;
import com.mohiva.play.xmlcompressor.fixtures.java.DefaultFilter;
import com.mohiva.play.xmlcompressor.fixtures.java.WithGzipFilter;
import org.apache.commons.io.IOUtils;
import org.junit.Test;
import play.Application;
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
 * Test case for the [[com.mohiva.play.xmlcompressor.java.XMLCompressorFilter]] class.
 */
public class XMLCompressorFilterTest {

    /**
     * Test if the default filter compress an XML page.
     */
    @Test
    public void defaultFilterCompressXMLDocument() {
        running(defaultApp(), () -> {
            Result result = route(defaultApp(), fakeRequest(GET, "/action"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("application/xml"));
            assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
        });
    }

    /**
     * Test if the default filter compress an async XML document.
     */
    @Test
    public void defaultFilterCompressAsyncXMLDocument() {
        running(defaultApp(), () -> {
            Result result = route(defaultApp(), fakeRequest(GET, "/asyncAction"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("application/xml"));
            assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
        });
    }

    /**
     * Test if the default filter compress a static XML asset.
     */
    @Test
    public void defaultFilterCompressStaticAssets() {
        running(defaultApp(), () -> {
            InputStream is = new Environment(Mode.TEST).resourceAsStream("static.xml");
            String file = "";
            try {
                file = IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Result result = route(defaultApp(), fakeRequest(GET, "/static"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("application/xml"));
            assertThat(contentAsString(result, defaultApp().injector().instanceOf(Materializer.class))).startsWith("<?xml version=\"1.0\"?><node><subnode>");
            assertThat(result.header(CONTENT_LENGTH)).isNotEqualTo(String.valueOf(file.length()));
        });
    }

    /**
     * Test if the default filter does not compress a non XML result.
     */
    @Test
    public void defaultFilterNotCompressNonXMLPage() {
        running(defaultApp(), () -> {
            Result result = route(defaultApp(), fakeRequest(GET, "/nonXML"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("text/plain"));
            assertThat(contentAsString(result)).startsWith("  <html/>");
        });
    }

    /**
     * Test if the default filter does not compress chunked XML result.
     */
    @Test
    public void defaultFilterNotCompressChunkedXMLPage() {
        running(defaultApp(), () -> {
            Result result = route(defaultApp(), fakeRequest(GET, "/chunked"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("application/xml"));
            assertThat(result.header(CONTENT_LENGTH)).isEqualTo(Optional.empty());
        });
    }

    /**
     * Test if the custom filter compress an XML page.
     */
    @Test
    public void customFilterCompressXMLPage() {
        running(customApp(), () -> {
            Result result = route(customApp(), fakeRequest(GET, "/action"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("application/xml"));
            assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
        });
    }

    /**
     * Test if the custom filter compress an async XML page.
     */
    @Test
    public void customFilterCompressAsyncXMLPage() {
        running(customApp(), () -> {
            Result result = route(customApp(), fakeRequest(GET, "/asyncAction"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("application/xml"));
            assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
        });
    }

    /**
     * Test if the custom filter does not compress a non XML page.
     */
    @Test
    public void customFilterNotCompressNonXMLPage() {
        running(customApp(), () -> {
            Result result = route(customApp(), fakeRequest(GET, "/nonXML"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("text/plain"));
            assertThat(contentAsString(result)).startsWith("  <html/>");
        });
    }

    /**
     * Test if the custom filter compress a static XML asset.
     */
    @Test
    public void customFilterCompressStaticAssets() {
        running(customApp(), () -> {
            InputStream is = new Environment(Mode.TEST).resourceAsStream("static.xml");
            String file = "";
            try {
                file = IOUtils.toString(is, "UTF-8");
            } catch (IOException e) {
                e.printStackTrace();
            }
            Result result = route(customApp(), fakeRequest(GET, "/static"));

            assertThat(result.status()).isEqualTo(OK);
            assertThat(result.contentType()).isEqualTo(Optional.of("application/xml"));
            assertThat(contentAsString(result, defaultApp().injector().instanceOf(Materializer.class))).startsWith("<?xml version=\"1.0\"?><node><subnode>");
            assertThat(result.header(CONTENT_LENGTH)).isNotEqualTo(String.valueOf(file.length()));
        });
    }

    /**
     * Test that a result is first XML compressed and then gzipped
     */
    @Test
    public void defaultWithGzipFilterXmlCompressesAndThenGzipsResult() {
        running(gzipApp(), () -> {
            Result original = route(gzipApp(), fakeRequest(GET, "/action"));
            Result gzipped  = route(gzipApp(), fakeRequest(GET, "/action").header(ACCEPT_ENCODING, "gzip"));

            assertThat(gzipped.status()).isEqualTo(OK);
            assertThat(gzipped.contentType()).isEqualTo(Optional.of("application/xml"));
            assertThat(gzipped.header(CONTENT_ENCODING)).isEqualTo(Optional.of("gzip"));
            assertThat(Helper.gunzip(contentAsBytes(gzipped))).isEqualTo(contentAsBytes(original));
        });
    }

    /**
     * Test if result is not compressed if it's already gzipped
     *
     * given static.xml.gz == gzip(static.xml)
     * when /static.xml is requested
     * then Assets controller responds with static.xml.gz
     * we don't want to further pass this through XML Compressor
     */
    @Test
    public void defaultWithGzipFilterNotCompressGzippedResult() {
        running(gzipApp(), () -> {
            try {
                ByteString original = ByteString.fromArray(IOUtils.toByteArray(new Environment(Mode.TEST).resourceAsStream("static.xml")));
                Result result = route(gzipApp(), fakeRequest(GET, "/gzipped").header(ACCEPT_ENCODING, "gzip"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo(Optional.of("application/xml"));
                assertThat(result.header(CONTENT_ENCODING)).isEqualTo(Optional.of("gzip"));
                assertThat(Helper.gunzip(contentAsBytes(result, gzipApp().injector().instanceOf(Materializer.class)))).isEqualTo(original);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });
    }

    /**
     * An app with the default XML compressor filter.
     */
    private Application defaultApp() {
        return new GuiceApplicationBuilder()
            .in(new Environment(Mode.TEST))
            .configure("play.http.filters", DefaultFilter.class.getCanonicalName())
            .configure("play.http.requestHandler", RequestHandler.class.getCanonicalName())
            .build();
    }

    /**
     * An app with the custom XML compressor filter.
     */
    private Application customApp() {
        return new GuiceApplicationBuilder()
            .in(new Environment(Mode.TEST))
            .overrides(bind(XMLCompressorFilter.class).to(CustomXMLCompressorFilter.class))
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
