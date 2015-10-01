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

import com.googlecode.htmlcompressor.compressor.XmlCompressor;

import com.mohiva.play.xmlcompressor.fixtures.Application;
import org.apache.commons.io.IOUtils;
import org.junit.Test;

import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;
import play.api.mvc.RequestHeader;
import play.api.mvc.ResponseHeader;
import play.filters.gzip.Gzip;
import play.filters.gzip.GzipFilter;
import play.mvc.*;
import scala.runtime.AbstractFunction2;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import static org.fest.assertions.Assertions.*;
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
        running(fakeApplication(new DefaultCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/action"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("application/xml");
                assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
            }
        });
    }

    /**
     * Test if the default filter compress an async XML document.
     */
    @Test
    public void defaultFilterCompressAsyncXMLDocument() {
        running(fakeApplication(new DefaultCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/asyncAction"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("application/xml");
                assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
            }
        });
    }

    /**
     * Test if the default filter compress a static XML asset.
     */
    @Test
    public void defaultFilterCompressStaticAssets() {
        running(fakeApplication(new DefaultCompressorGlobal()), new Runnable() {
            public void run() {
                InputStream is = Play.application().resourceAsStream("static.xml");
                String file = "";
                try {
                    file = IOUtils.toString(is, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Result result = route(fakeRequest(GET, "/static"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("application/xml");
                assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
                assertThat(result.header(CONTENT_LENGTH)).isNotEqualTo(String.valueOf(file.length()));
            }
        });
    }

    /**
     * Test if the default filter does not compress a non XML result.
     */
    @Test
    public void defaultFilterNotCompressNonXMLPage() {
        running(fakeApplication(new DefaultCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/nonXML"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("text/plain");
                assertThat(contentAsString(result)).startsWith("  <html/>");
            }
        });
    }

    /**
     * Test if the default filter does not compress chunked XML result.
     */
    @Test
    public void defaultFilterNotCompressChunkedXMLPage() {
        running(fakeApplication(new DefaultCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/chunked"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("application/xml");
                assertThat(result.header(CONTENT_LENGTH)).isNull();
            }
        });
    }

    /**
     * Test if the custom filter compress an XML page.
     */
    @Test
    public void customFilterCompressXMLPage() {
        running(fakeApplication(new CustomCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/action"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("application/xml");
                assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
            }
        });
    }

    /**
     * Test if the custom filter compress an async XML page.
     */
    @Test
    public void customFilterCompressAsyncXMLPage() {
        running(fakeApplication(new CustomCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/asyncAction"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("application/xml");
                assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
            }
        });
    }

    /**
     * Test if the custom filter does not compress a non XML page.
     */
    @Test
    public void customFilterNotCompressNonXMLPage() {
        running(fakeApplication(new CustomCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/nonXML"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("text/plain");
                assertThat(contentAsString(result)).startsWith("  <html/>");
            }
        });
    }

    /**
     * Test if the custom filter compress a static XML asset.
     */
    @Test
    public void customFilterCompressStaticAssets() {
        running(fakeApplication(new CustomCompressorGlobal()), new Runnable() {
            public void run() {
                InputStream is = Play.application().resourceAsStream("static.xml");
                String file = "";
                try {
                    file = IOUtils.toString(is, "UTF-8");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Result result = route(fakeRequest(GET, "/static"));

                assertThat(result.status()).isEqualTo(OK);
                assertThat(result.contentType()).isEqualTo("application/xml");
                assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
                assertThat(result.header(CONTENT_LENGTH)).isNotEqualTo(String.valueOf(file.length()));
            }
        });
    }

    /**
     * Test that a result is first HTML compressed and then gzipped
     */
    @Test
    public void defaultWithGzipFilterHtmlCompressesAndThenGzipsResult() {
        running(fakeApplication(new DefaultWithGzipGlobal()), new Runnable() {
            @Override
            public void run() {
                try {
                    Result original = route(fakeRequest(GET, "/action"));
                    Result gzipped  = route(fakeRequest(GET, "/action").header(ACCEPT_ENCODING, "gzip"));

                    assertThat(gzipped.status()).isEqualTo(OK);
                    assertThat(gzipped.contentType()).isEqualTo("application/xml");
                    assertThat(gzipped.header(CONTENT_ENCODING)).isEqualTo("gzip");
                    assertThat(gunzip(contentAsBytes(gzipped))).isEqualTo(contentAsBytes(original));
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
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
        running(fakeApplication(new DefaultWithGzipGlobal()), new Runnable() {
            @Override
            public void run() {
                try {
                    byte[] original = IOUtils.toByteArray(Play.application().resourceAsStream("static.xml"));
                    Result result = route(fakeRequest(GET, "/gzipped").header(ACCEPT_ENCODING, "gzip"));

                    assertThat(result.status()).isEqualTo(OK);
                    assertThat(result.contentType()).isEqualTo("application/xml");
                    assertThat(result.header(CONTENT_ENCODING)).isEqualTo("gzip");
                    assertThat(gunzip(contentAsBytes(result))).isEqualTo(original);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
    }

    /**
     * Defines the routes for the test.
     */
    public class RouteSettings extends GlobalSettings {

        /**
         * Specify custom routes for this test.
         *
         * @param request The HTTP request header.
         * @return An action to handle this request.
         */
        public play.api.mvc.Handler onRouteRequest(Http.RequestHeader request) {
            final boolean getRequest = request.method().equals("GET");
            if (!getRequest) return null;
            String path = request.path();
            switch (path) {
                case "/action":
                    return new Application().action();
                case "/asyncAction":
                    return new Application().asyncAction();
                case "/nonXML":
                    return new Application().nonXML();
                case "/static":
                    return new Application().staticAsset();
                case "/chunked":
                    return new Application().chunked();
                case "/gzipped":
                    return new Application().gzipped();
                default:
                    return null;
            }
        }
    }

    /**
     * A custom global object with the default XML compressor filter.
     */
    public class DefaultCompressorGlobal extends RouteSettings {

        /**
         * Get the filters that should be used to handle each request.
         */
        @SuppressWarnings("unchecked")
        public <T extends EssentialFilter> Class<T>[] filters() {
            return new Class[]{XMLCompressorFilter.class};
        }
    }

    /**
     * A custom global object with the default XML compressor filter.
     */
    public class CustomCompressorGlobal extends RouteSettings {

        /**
         * Get the filters that should be used to handle each request.
         */
        @SuppressWarnings("unchecked")
        public <T extends EssentialFilter> Class<T>[] filters() {
            return new Class[]{CustomXMLCompressorFilter.class};
        }
    }

    /**
     * Custom implementation of the XML compressor filter.
     */
    public static class CustomXMLCompressorFilter extends XMLCompressorFilter {
        private final static CustomXMLCompressorBuilder customXMLCompressorBuilder = new CustomXMLCompressorBuilder();

        public CustomXMLCompressorFilter() {
            super(customXMLCompressorBuilder);
        }

        @Override
        public XMLCompressorBuilder builder() {
            return customXMLCompressorBuilder;
        }
    }

    /**
     * The builder for the custom XML compressor.
     */
    public static class CustomXMLCompressorBuilder implements XMLCompressorBuilder {
        public XmlCompressor build() {
            XmlCompressor compressor = new XmlCompressor();

            compressor.setRemoveComments(false);
            return compressor;
        }
    }


    /**
     * A custom global object with default HTML compressor filter and Default Gzip Filter.
     */
    public class DefaultWithGzipGlobal extends RouteSettings {
        @SuppressWarnings("unchecked")
        public <T extends EssentialFilter> Class<T>[] filters() {
            return new Class[]{ JavaGzipFilter.class, XMLCompressorFilter.class };
        }
    }

    /**
     * Can't just pass GzipFilter.class since Play 2.4 expects the
     * filters to be dependency injected. Instead, this class provides
     * the no-arg constructor that GlobalSettings.filters() expects.
     */
    public static class JavaGzipFilter extends GzipFilter {
        public JavaGzipFilter() {
            super(Gzip.gzip(8192),
                    102400,
                    new AbstractFunction2<RequestHeader, ResponseHeader, Object>() {
                        @Override
                        public Object apply(RequestHeader req, ResponseHeader resp) {
                            return true;
                        }
                    }
            );
        }
    }

    public byte[] gunzip(byte[] bs) throws IOException {
        InputStream bis = new ByteArrayInputStream(bs);
        InputStream gzis = new GZIPInputStream(bis);
        return IOUtils.toByteArray(gzis);
    }
}
