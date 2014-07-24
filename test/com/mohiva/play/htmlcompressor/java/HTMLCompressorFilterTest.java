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

import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import org.junit.Test;
import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;

import play.mvc.*;

import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

/**
 * Test case for the [[com.mohiva.play.htmlcompressor.java.HTMLCompressorFilter]] class.
 */
public class HTMLCompressorFilterTest {

    /**
     * Test if the default filter compress an HTML page.
     */
    @Test
    public void defaultFilterCompressHTMLPage() {
        running(fakeApplication(new DefaultCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/action"));

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(contentAsString(result)).startsWith("<!DOCTYPE html><html><head>");
            }
        });
    }

    /**
     * Test if the default filter compress an async HTML page.
     */
    @Test
    public void defaultFilterCompressAsyncHTMLPage() {
        running(fakeApplication(new DefaultCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/asyncAction"));

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(contentAsString(result)).startsWith("<!DOCTYPE html><html><head>");
            }
        });
    }

    /**
     * Test if the default filter does not compress a non HTML page.
     */
    @Test
    public void defaultFilterNotCompressNonHTMLPage() {
        running(fakeApplication(new DefaultCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/nonHTML"));

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/plain");
                assertThat(contentAsString(result)).startsWith("  <html/>");
            }
        });
    }

    /**
     * Test if the custom filter compress an HTML page.
     */
    @Test
    public void customFilterCompressHTMLPage() {
        running(fakeApplication(new CustomCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/action"));

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(contentAsString(result)).startsWith("<!DOCTYPE html><html><head>");
            }
        });
    }

    /**
     * Test if the custom filter compress an async HTML page.
     */
    @Test
    public void customFilterCompressAsyncHTMLPage() {
        running(fakeApplication(new CustomCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/asyncAction"));

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/html");
                assertThat(contentAsString(result)).startsWith("<!DOCTYPE html><html><head>");
            }
        });
    }

    /**
     * Test if the custom filter does not compress a non HTML page.
     */
    @Test
    public void customFilterNotCompressNonHTMLPage() {
        running(fakeApplication(new CustomCompressorGlobal()), new Runnable() {
            public void run() {
                Result result = route(fakeRequest(GET, "/nonHTML"));

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/plain");
                assertThat(contentAsString(result)).startsWith("  <html/>");
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
            if (request.method().equals("GET") && request.path().equals("/action")) {
                return new com.mohiva.play.htmlcompressor.fixtures.Application().action();
            } if (request.method().equals("GET") && request.path().equals("/asyncAction")) {
                return new com.mohiva.play.htmlcompressor.fixtures.Application().asyncAction();
            } if (request.method().equals("GET") && request.path().equals("/nonHTML")) {
                return new com.mohiva.play.htmlcompressor.fixtures.Application().nonHTML();
            } else {
                return null;
            }
        }
    }

    /**
     * A custom global object with the default HTML compressor filter.
     */
    public class DefaultCompressorGlobal extends RouteSettings {

        /**
         * Get the filters that should be used to handle each request.
         */
        @SuppressWarnings("unchecked")
        public <T extends EssentialFilter> Class<T>[] filters() {
            return new Class[]{HTMLCompressorFilter.class};
        }
    }

    /**
     * A custom global object with the default HTML compressor filter.
     */
    public class CustomCompressorGlobal extends RouteSettings {

        /**
         * Get the filters that should be used to handle each request.
         */
        @SuppressWarnings("unchecked")
        public <T extends EssentialFilter> Class<T>[] filters() {
            return new Class[]{CustomHTMLCompressorFilter.class};
        }
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
