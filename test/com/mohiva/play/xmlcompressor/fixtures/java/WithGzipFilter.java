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
package com.mohiva.play.xmlcompressor.fixtures.java;

import com.mohiva.play.xmlcompressor.XMLCompressorFilter;
import play.mvc.EssentialFilter;
import play.filters.gzip.GzipFilter;
import play.http.DefaultHttpFilters;

import java.util.LinkedList;
import java.util.List;
import javax.inject.Inject;

/**
 * Provides the default XML compressor filter with a Gzip filter.
 */
public class WithGzipFilter extends DefaultHttpFilters {

    private XMLCompressorFilter xmlCompressorFilter;
    private GzipFilter gzip;

    @Inject
    public WithGzipFilter(XMLCompressorFilter xmlCompressorFilter, GzipFilter gzip) {
        this.xmlCompressorFilter = xmlCompressorFilter;
        this.gzip = gzip;
    }

    @Override
    public List<EssentialFilter> getFilters() {
        List<EssentialFilter> l = new LinkedList<EssentialFilter>();
        l.add(gzip.asJava());
        l.add(xmlCompressorFilter.asJava());
        return l;
    }
}
