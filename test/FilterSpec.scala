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
import play.api.templates.Html
import play.api.mvc.{Filters, Action}
import play.api.mvc.Results.Ok
import play.api.mvc.Results.Async
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter
import org.specs2.specification.Scope

/**
 * Test case for the [[com.mohiva.play.htmlcompressor.HTMLCompressorFilter]] class.
 *
 * @author Christian Kaps `christian.kaps@mohiva.com`
 */
class FilterSpec extends Specification {

  "Filter" should {

    "compress an HTML page" in new WithApplication with Context {
      val action = Filters(Action(Ok(template)), HTMLCompressorFilter())
      val result = action(FakeRequest()).run

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith("<!DOCTYPE html><html><head>")
    }

    "compress an async HTML page" in new WithApplication with Context {
      val action = Filters(Action.async(Future(Ok(template))), HTMLCompressorFilter())
      val result = action(FakeRequest()).run

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/html")
      contentAsString(result) must startWith ("<!DOCTYPE html><html><head>")
    }

    "not compress a non HTML result" in new WithApplication {
      val action = Filters(Action(Ok("  <html/>")), HTMLCompressorFilter())
      val result = action(FakeRequest()).run

      status(result) must equalTo(OK)
      contentType(result) must beSome("text/plain")
      contentAsString(result) must startWith ("  <html/>")
    }
  }

  trait Context extends Scope {
    /**
     * The template to compress.
     */
    val template = Html("""

      <!DOCTYPE html>
        <html>
          <head>
            <title>@title</title>
          </head>
          <body>
            I'm a play app
          </body>
        </html>
    """)
  }
}
