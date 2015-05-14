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

import com.mohiva.play.htmlcompressor.fixtures.Application
import org.specs2.mutable._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.{ GlobalSettings, Play }
import com.googlecode.htmlcompressor.compressor.HtmlCompressor

/**
 * Test case for the [[com.mohiva.play.htmlcompressor.HTMLCompressorFilter]] class.
 */
class HTMLCompressorFilterSpec extends Specification {

  "The default filter" should {
    "compress an HTML page" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/action"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html> <html> <head>")
    }

    "compress an async HTML page" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/asyncAction"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html> <html> <head>")
    }

    "not compress a non HTML result" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/nonHTML"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must startWith("  <html/>")
    }

    "compress static assets" in new DefaultCompressorGlobal {
      val file = scala.io.Source.fromInputStream(Play.resourceAsStream("static.html").get).mkString
      val Some(result) = route(FakeRequest(GET, "/static"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html> <html> <head>")
      header(CONTENT_LENGTH, result) must not beSome file.length.toString
    }

    "not compress result with chunked HTML result" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/chunked"))
      status(result) must beEqualTo(OK)
      contentType(result) must beSome("text/html")
      header(CONTENT_LENGTH, result) must beNone
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

    "compress static assets" in new CustomCompressorGlobal {
      val file = scala.io.Source.fromInputStream(Play.resourceAsStream("static.html").get).mkString
      val Some(result) = route(FakeRequest(GET, "/static"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html><html><head>")
      header(CONTENT_LENGTH, result) must not beSome file.length.toString
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
      lazy val application = new Application()
      (request.method, request.path) match {
        case ("GET", "/action") => Some(application.action)
        case ("GET", "/asyncAction") => Some(application.asyncAction)
        case ("GET", "/nonHTML") => Some(application.nonHTML)
        case ("GET", "/static") => Some(application.staticAsset)
        case ("GET", "/chunked") => Some(application.chunked)
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
      compressor.setRemoveComments(true)
      compressor.setRemoveIntertagSpaces(true)
      compressor.setRemoveHttpProtocol(true)
      compressor.setRemoveHttpsProtocol(true)
      compressor
    })
  }
}
