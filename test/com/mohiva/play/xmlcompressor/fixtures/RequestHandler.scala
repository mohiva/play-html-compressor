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

import javax.inject.Inject

import controllers.AssetsMetadata
import play.api.http.{ DefaultHttpRequestHandler, HttpConfiguration, HttpErrorHandler, HttpFilters }
import play.api.mvc.{ ControllerComponents, Handler, RequestHeader }
import play.api.routing.Router

/**
 * Request handler which defines the routes for the tests.
 */
class RequestHandler @Inject() (
  router: Router,
  errorHandler: HttpErrorHandler,
  configuration: HttpConfiguration,
  filters: HttpFilters,
  components: ControllerComponents,
  meta: AssetsMetadata)
  extends DefaultHttpRequestHandler(router, errorHandler, configuration, filters) {

  /**
   * Specify custom routes for this test.
   *
   * @param request The HTTP request header.
   * @return An action to handle this request.
   */
  override def routeRequest(request: RequestHeader): Option[Handler] = {
    lazy val controller = new TestController(components, meta)
    (request.method, request.path) match {
      case ("GET", "/action") => Some(controller.action)
      case ("GET", "/asyncAction") => Some(controller.asyncAction)
      case ("GET", "/nonXML") => Some(controller.nonXML)
      case ("GET", "/static") => Some(controller.staticAsset)
      case ("GET", "/chunked") => Some(controller.chunked)
      case ("GET", "/gzipped") => Some(controller.gzipped)
      case _ => None
    }
  }
}
