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

import akka.stream.scaladsl.Source
import akka.util.ByteString
import play.api.http.{ DefaultHttpErrorHandler }
import play.api.mvc._
import play.twirl.api.Html
import scala.concurrent.Future
import controllers.{ AssetsBuilder, AssetsMetadata }

/**
 * Test controller.
 */
class TestController(components: ControllerComponents, meta: AssetsMetadata) extends AbstractController(components) {

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
    Ok(template).as("text/html")
  }

  /**
   * A async action.
   */
  def asyncAction = Action.async {
    Future.successful(Ok(template).as("text/html"))
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

  val assets = new AssetsBuilder(DefaultHttpErrorHandler, meta)
  def staticAsset = assets.at("/", "static.html")

  /**
   * Action with chunked transfer encoding.
   */
  def chunked = Action {
    val parts = List(" <html> ", " <body> ", " <h1> Title </h1>", " </body> ", " </html> ").map(html => ByteString(html))
    Ok.chunked(Source(parts)).as("text/html")
  }

  /**
   * Action with gzipped asset.
   */
  def gzipped = staticAsset
}
