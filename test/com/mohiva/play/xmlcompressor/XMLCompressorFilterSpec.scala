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
package com.mohiva.play.xmlcompressor

import org.specs2.mutable._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.GlobalSettings
import com.googlecode.htmlcompressor.compressor.XmlCompressor

/**
 * Test case for the [[com.mohiva.play.xmlcompressor.XMLCompressorFilter]] class.
 */
class XMLCompressorFilterSpec extends Specification {

  "The default filter" should {
    "compress an XML document" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/action"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/xml")
      contentAsString(result) must startWith("<?xml version=\"1.0\"?><node><subnode>")
    }

    "compress an async XML document" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/asyncAction"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/xml")
      contentAsString(result) must startWith("<?xml version=\"1.0\"?><node><subnode>")
    }

    "not compress a non XML result" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/nonXML"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must startWith("  <html/>")
    }
  }

  "The custom filter" should {
    "compress an XML document" in new CustomCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/action"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/xml")
      contentAsString(result) must startWith("<?xml version=\"1.0\"?><node><subnode>")
    }

    "compress an async XML document" in new CustomCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/asyncAction"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/xml")
      contentAsString(result) must startWith("<?xml version=\"1.0\"?><node><subnode>")
    }

    "not compress a non XML result" in new CustomCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/nonXML"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must startWith("  <html/>")
    }
  }

  /**
   * Defines the routes for the test.
   */
  trait RouteSettings extends GlobalSettings {

    /**
     * Specify custom routes for this test.
     *
     * @param request The HTTP request header.
     * @return An action to handle this request.
     */
    override def onRouteRequest(request: RequestHeader): Option[Handler] = {
      (request.method, request.path) match {
        case ("GET", "/action") => Some(new com.mohiva.play.xmlcompressor.fixtures.Application().action)
        case ("GET", "/asyncAction") => Some(new com.mohiva.play.xmlcompressor.fixtures.Application().asyncAction)
        case ("GET", "/nonXML") => Some(new com.mohiva.play.xmlcompressor.fixtures.Application().nonXML)
        case _ => None
      }
    }
  }

  /**
   * A custom global object with the default XML compressor filter.
   */
  class DefaultCompressorGlobal
    extends WithApplication(FakeApplication(withGlobal = Some(new WithFilters(XMLCompressorFilter()) with RouteSettings)))

  /**
   * A custom global object with a custom XML compressor filter.
   */
  class CustomCompressorGlobal
    extends WithApplication(FakeApplication(withGlobal = Some(new WithFilters(CustomXMLCompressorFilter()) with RouteSettings)))

  /**
   * Custom implementation of the XML compressor filter.
   */
  object CustomXMLCompressorFilter {

    /**
     * Creates the XML compressor filter.
     *
     * @return The XML compressor filter.
     */
    def apply(): XMLCompressorFilter = new XMLCompressorFilter({
      val compressor = new XmlCompressor()
      compressor.setRemoveComments(false)
      compressor
    })
  }
}
