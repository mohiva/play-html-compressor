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
package com.mohiva.play.htmlcompressor.fixtures

import play.api.http.DefaultHttpErrorHandler
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.twirl.api.Html
import scala.concurrent.Future
import controllers.AssetsBuilder
import scala.concurrent.ExecutionContext.Implicits.global

/**
 * Test controller.
 */
class TestController extends AssetsBuilder(DefaultHttpErrorHandler) {

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
    """
  )

  /**
   * A default action.
   */
  def action = Action {
    Ok(template)
  }

  /**
   * A async action.
   */
  def asyncAction = Action.async {
    Future.successful(Ok(template))
  }

  /**
   * A non HTML action.
   */
  def nonHTML = Action {
    Ok("  <html/>")
  }

  /**
   * Loads a static asset.
   */
  def staticAsset = at("/", "static.html")

  /**
   * Action with chunked transfer encoding.
   */
  def chunked = Action {
    val parts = List(" <html> ", " <body> ", " <h1> Title </h1>", " </body> ", " </html> ").map(Html.apply)
    Ok.chunked(Enumerator.enumerate(parts))
  }

  /**
   * Action with gzipped asset.
   */
  def gzipped = staticAsset
}
