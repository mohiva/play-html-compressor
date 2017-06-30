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
package com.mohiva.play.xmlcompressor.fixtures

import akka.stream.scaladsl.Source
import akka.util.ByteString
import controllers.{ AssetsBuilder, AssetsMetadata }
import play.api.http.DefaultHttpErrorHandler
import play.api.mvc._

import scala.concurrent.Future

/**
 * Test controller.
 */
class TestController(components: ControllerComponents, meta: AssetsMetadata) extends AbstractController(components) {

  /**
   * The template to compress.
   */
  val template = """

    <?xml version="1.0"?>
    <node>
      <subnode>
        Some text
      </subnode>
    </node>
    """

  /**
   * A default action.
   */
  def action = Action {
    Ok(template).as("application/xml")
  }

  /**
   * A async action.
   */
  def asyncAction = Action.async {
    Future.successful(Ok(template).as("application/xml"))
  }

  /**
   * A non XML action.
   */
  def nonXML = Action {
    Ok("  <html/>")
  }

  /**
   * Loads a static asset.
   */
  val assets = new AssetsBuilder(DefaultHttpErrorHandler, meta)
  def staticAsset = assets.at("/", "static.xml")

  /**
   * Action with chunked transfer encoding.
   */
  def chunked = Action {
    val parts = List(" <node> ", " <subnode> ", " text", " </subnode> ", " </node> ").map(xml => ByteString(xml))
    Ok.chunked(Source(parts)).as("application/xml")
  }

  /**
   * Action with gzipped asset.
   */
  def gzipped = staticAsset
}
