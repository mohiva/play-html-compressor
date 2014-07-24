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

import org.junit.Test;

import play.GlobalSettings;
import play.Play;
import play.api.mvc.EssentialFilter;
import play.mvc.*;
import static play.test.Helpers.*;
import static org.fest.assertions.Assertions.*;

/**
 * Test case for the [[com.mohiva.play.xmlcompressor.java.XMLCompressorFilter]] class.
 *
 * @author Christian Kaps `christian.kaps@mohiva.com`
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

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("application/xml");
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

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("application/xml");
                assertThat(contentAsString(result)).startsWith("<?xml version=\"1.0\"?><node><subnode>");
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

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("text/plain");
                assertThat(contentAsString(result)).startsWith("  <html/>");
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

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("application/xml");
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

                assertThat(status(result)).isEqualTo(OK);
                assertThat(contentType(result)).isEqualTo("application/xml");
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
                return new com.mohiva.play.xmlcompressor.fixtures.Application().action();
            } if (request.method().equals("GET") && request.path().equals("/asyncAction")) {
                return new com.mohiva.play.xmlcompressor.fixtures.Application().asyncAction();
            } if (request.method().equals("GET") && request.path().equals("/nonXML")) {
                return new com.mohiva.play.xmlcompressor.fixtures.Application().nonXML();
            } else {
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
        public CustomXMLCompressorFilter() {
            super(new CustomXMLCompressorBuilder());
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
}
