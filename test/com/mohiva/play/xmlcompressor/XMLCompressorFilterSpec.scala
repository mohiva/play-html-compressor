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

import _root_.java.io.ByteArrayInputStream
import _root_.java.util.zip.GZIPInputStream

import com.mohiva.play.xmlcompressor.fixtures.Application
import org.apache.commons.io.IOUtils
import org.specs2.mutable._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.{ Play, GlobalSettings }
import com.googlecode.htmlcompressor.compressor.XmlCompressor
import play.filters.gzip.GzipFilter

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

    "not compress chunked XML result" in new DefaultCompressorGlobal {
      val Some(result) = route(FakeRequest(GET, "/chunked"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/xml")
      header(CONTENT_LENGTH, result) must beNone
    }

    "compress static XML assets" in new CustomCompressorGlobal {
      val file = scala.io.Source.fromInputStream(Play.resourceAsStream("static.xml").get).mkString
      val Some(result) = route(FakeRequest(GET, "/static"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/xml")
      contentAsString(result) must startWith("<?xml version=\"1.0\"?><node><subnode>")
      header(CONTENT_LENGTH, result) must not beSome file.length.toString
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

    "compress static XML assets" in new CustomCompressorGlobal {
      val file = scala.io.Source.fromInputStream(Play.resourceAsStream("static.xml").get).mkString
      val Some(result) = route(FakeRequest(GET, "/static"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("application/xml")
      contentAsString(result) must startWith("<?xml version=\"1.0\"?><node><subnode>")
      header(CONTENT_LENGTH, result) must not beSome file.length.toString
    }
  }

  "The default filter with Gzip Filter" should {
    "first compress then gzip result" in new DefaultWithGzipGlobal {
      val Some(original) = route(FakeRequest(GET, "/action"))
      val Some(gzipped) = route(FakeRequest(GET, "/action").withHeaders(ACCEPT_ENCODING -> "gzip"))

      status(gzipped) must beEqualTo(OK)
      contentType(gzipped) must beSome("application/xml")
      header(CONTENT_ENCODING, gzipped) must beSome("gzip")
      gunzip(contentAsBytes(gzipped)) must_== contentAsBytes(original)
    }

    "not compress already gzipped result" in new DefaultWithGzipGlobal {
      // given static.html.gz == gzip(static.html)
      // when /static.html is requested
      // then Assets controller responds with static.html.gz
      // we don't want to further pass this through HTML Compressor

      val original = IOUtils.toByteArray(Play.resourceAsStream("static.xml").get)
      val Some(result) = route(FakeRequest(GET, "/gzipped").withHeaders(ACCEPT_ENCODING -> "gzip"))

      status(result) must beEqualTo(OK)
      contentType(result) must beSome("application/xml")
      header(CONTENT_ENCODING, result) must beSome("gzip")
      gunzip(contentAsBytes(result)) must_== original
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
        case ("GET", "/nonXML") => Some(application.nonXML)
        case ("GET", "/static") => Some(application.staticAsset)
        case ("GET", "/chunked") => Some(application.chunked)
        case ("GET", "/gzipped") => Some(application.gzipped)
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

  /**
   * A custom global object with default HTML compressor filter and Default Gzip Filter.
   */
  class DefaultWithGzipGlobal
    extends WithApplication(FakeApplication(withGlobal = Some(new WithFilters(new GzipFilter(), XMLCompressorFilter()) with RouteSettings)))

  def gunzip(bs: Array[Byte]): Array[Byte] = {
    val bis = new ByteArrayInputStream(bs)
    val gzis = new GZIPInputStream(bis)
    IOUtils.toByteArray(gzis)
  }
}
