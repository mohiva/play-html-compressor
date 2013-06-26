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
package com.mohiva.play.htmlcompressor.test.controllers

import play.api.mvc._
import com.mohiva.play.htmlcompressor.test.views
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

object Application extends Controller {

  def index = Action {
    Ok(views.html.app("Mohiva"))
  }

  def async = Action {
    Async(Future(Ok(views.html.app("Mohiva"))))
  }

  def text = Action {
    Ok("  <html/>")
  }
}
