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

import com.googlecode.htmlcompressor.compressor.XmlCompressor;
import com.mohiva.play.xmlcompressor.XMLCompressorFilter;
import play.api.Configuration;

import javax.inject.Inject;

/**
 * Custom implementation of the XML compressor filter.
 */
public class CustomXMLCompressorFilter extends XMLCompressorFilter {

    private Configuration configuration;

    @Inject
    public CustomXMLCompressorFilter(Configuration configuration) {
        this.configuration = configuration;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public XmlCompressor compressor() {
        XmlCompressor compressor = new XmlCompressor();
        compressor.setRemoveComments(false);

        return compressor;
    }
}
