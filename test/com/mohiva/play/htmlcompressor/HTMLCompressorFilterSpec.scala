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
package com.mohiva.play.htmlcompressor

import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import org.specs2.mutable._
import play.api.test.FakeApplication
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import play.api.{ GlobalSettings, Play }
import play.api.Play.current

/**
 * Test case for the [[com.mohiva.play.htmlcompressor.HTMLCompressorFilter]] class.
 *
 * @author Christian Kaps `christian.kaps@mohiva.com`
 */
class HTMLCompressorFilterSpec extends Specification {

  "The default filter" should {
    "compress an HTML page" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/action"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html><html><head>")
    }

    "compress an async HTML page" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/asyncAction"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html><html><head>")
    }

    "not compress a non HTML result" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/nonHTML"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must startWith("  <html/>")
    }
  }

  "The custom filter" should {
    "compress an HTML page" in new CustomCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/action"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html><html><head>")
    }

    "compress an async HTML page" in new CustomCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/asyncAction"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html><html><head>")
    }

    "not compress a non HTML result" in new CustomCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/nonHTML"))

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
        case ("GET", "/action") => Some(new com.mohiva.play.htmlcompressor.fixtures.Application().action)
        case ("GET", "/asyncAction") => Some(new com.mohiva.play.htmlcompressor.fixtures.Application().asyncAction)
        case ("GET", "/nonHTML") => Some(new com.mohiva.play.htmlcompressor.fixtures.Application().nonHTML)
        case _ => None
      }
    }
  }

  /**
   * A custom global object with the default HTML compressor filter.
   */
  class DefaultCompressorGlobal
    extends WithApplication(FakeApplication(withGlobal = Some(new WithFilters(HTMLCompressorFilter()) with RouteSettings)))

  /**
   * A custom global object with a custom HTML compressor filter.
   */
  class CustomCompressorGlobal
    extends WithApplication(FakeApplication(withGlobal = Some(new WithFilters(CustomHTMLCompressorFilter()) with RouteSettings)))

  /**
   * Custom implementation of the HTML compressor filter.
   */
  object CustomHTMLCompressorFilter {

    /**
     * Creates the HTML compressor filter.
     *
     * @return The HTML compressor filter.
     */
    def apply(): HTMLCompressorFilter = new HTMLCompressorFilter({
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
}
