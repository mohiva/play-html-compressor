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
package com.mohiva.play.htmlcompressor.fixtures.java;

import com.mohiva.play.htmlcompressor.HTMLCompressorFilter;
import play.mvc.EssentialFilter;
import play.http.HttpFilters;

import javax.inject.Inject;

/**
 * Provides the default HTML compressor filter.
 */
public class DefaultFilter implements HttpFilters {

    private HTMLCompressorFilter htmlCompressorFilter;

    @Inject
    public DefaultFilter(HTMLCompressorFilter htmlCompressorFilter) {
        this.htmlCompressorFilter = htmlCompressorFilter;
    }

    @Override
    public EssentialFilter[] filters() {
        return new EssentialFilter[] {
            htmlCompressorFilter.asJava()
        };
    }
}
