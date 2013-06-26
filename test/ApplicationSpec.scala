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
package test

import org.specs2.mutable._

import play.api.test._
import play.api.test.Helpers._

/**
 * Test the filter.
 *
 * @author Christian Kaps `christian.kaps@mohiva.com`
 */
class ApplicationSpec extends Specification {

  "Application" should {

    "compress a HTML page" in new WithApplication {
      val Some(result) = route(FakeRequest(GET, "/"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html><html><head>")
    }

    "compress a async HTML page" in new WithApplication {
      val Some(result) = route(FakeRequest(GET, "/async"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith ("<!DOCTYPE html><html><head>")
    }

    "not compress a non HTML result" in new WithApplication {
      val Some(result) = route(FakeRequest(GET, "/text"))

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must startWith ("  <html/>")
    }
  }
}
