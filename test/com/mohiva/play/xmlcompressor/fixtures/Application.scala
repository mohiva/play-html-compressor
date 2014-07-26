package com.mohiva.play.xmlcompressor.fixtures

import play.api.mvc._
import scala.concurrent.Future
import controllers.AssetsBuilder

/**
 * Test controller.
 */
class Application extends AssetsBuilder {

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
  def staticAsset = at("/", "static.xml")
}
