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
import play.filters.gzip.GzipFilter;
import play.http.DefaultHttpFilters;

import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;

/**
 * Provides the default HTML compressor filter with a Gzip filter.
 */
public class WithGzipFilter extends DefaultHttpFilters {

    private HTMLCompressorFilter htmlCompressor;
    private GzipFilter gzip;

    @Inject
    public WithGzipFilter(HTMLCompressorFilter htmlCompressor, GzipFilter gzip) {
        this.htmlCompressor = htmlCompressor;
        this.gzip = gzip;
    }

    @Override
    public List<EssentialFilter> getFilters() {
        List<EssentialFilter> l = new LinkedList<EssentialFilter>();
        l.add(gzip.asJava());
        l.add(htmlCompressor.asJava());
        return l;
    }
}
