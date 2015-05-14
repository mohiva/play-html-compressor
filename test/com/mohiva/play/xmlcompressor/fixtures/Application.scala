package com.mohiva.play.xmlcompressor.fixtures

import controllers.AssetsBuilder
import play.api.http.DefaultHttpErrorHandler
import play.api.libs.iteratee.Enumerator
import play.api.mvc._
import play.twirl.api.Xml

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
 * Test controller.
 */
class Application extends AssetsBuilder(DefaultHttpErrorHandler) {

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

  /**
   * Action with chunked transfer encoding
   */
  def chunked = Action {
    val parts = List(" <node> ", " <subnode> ", " text", " </subnode> ", " </node> ").map(Xml.apply)
    Ok.chunked(Enumerator.enumerate(parts))
  }
}
