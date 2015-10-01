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

import _root_.java.io.ByteArrayInputStream
import _root_.java.util.zip.GZIPInputStream

import com.mohiva.play.htmlcompressor.fixtures.Application
import org.apache.commons.io.IOUtils
import org.specs2.mutable._
import play.api.mvc._
import play.api.test._
import play.api.test.Helpers._
import play.api.test.FakeApplication
import play.api.{ GlobalSettings, Play }
import com.googlecode.htmlcompressor.compressor.HtmlCompressor
import play.filters.gzip.GzipFilter

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

  "The default filter with Gzip Filter" should {
    "first compress then gzip result" in new DefaultWithGzipGlobal {
      val Some(original) = route(FakeRequest(GET, "/action"))
      val Some(gzipped) = route(FakeRequest(GET, "/action").withHeaders(ACCEPT_ENCODING -> "gzip"))

      status(gzipped) must beEqualTo(OK)
      contentType(gzipped) must beSome("text/html")
      header(CONTENT_ENCODING, gzipped) must beSome("gzip")
      gunzip(contentAsBytes(gzipped)) must_== contentAsBytes(original)
    }

    "not compress already gzipped result" in new DefaultWithGzipGlobal {
      // given static.html.gz == gzip(static.html)
      // when /static.html is requested
      // then Assets controller responds with static.html.gz
      // we don't want to further pass this through HTML Compressor

      val original = IOUtils.toByteArray(Play.resourceAsStream("static.html").get)
      val Some(result) = route(FakeRequest(GET, "/gzipped").withHeaders(ACCEPT_ENCODING -> "gzip"))

      status(result) must beEqualTo(OK)
      contentType(result) must beSome("text/html")
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
        case ("GET", "/nonHTML") => Some(application.nonHTML)
        case ("GET", "/static") => Some(application.staticAsset)
        case ("GET", "/chunked") => Some(application.chunked)
        case ("GET", "/gzipped") => Some(application.gzipped)
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

  /**
   * A custom global object with default HTML compressor filter and Default Gzip Filter.
   */
  class DefaultWithGzipGlobal
    extends WithApplication(FakeApplication(withGlobal = Some(new WithFilters(new GzipFilter(), HTMLCompressorFilter()) with RouteSettings)))

  def gunzip(bs: Array[Byte]): Array[Byte] = {
    val bis = new ByteArrayInputStream(bs)
    val gzis = new GZIPInputStream(bis)
    IOUtils.toByteArray(gzis)
  }
}
