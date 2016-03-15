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

import akka.stream.Materializer;
import com.googlecode.htmlcompressor.compressor.HtmlCompressor;
import com.mohiva.play.htmlcompressor.HTMLCompressorFilter;
import play.Environment;
import play.Mode;
import play.api.Configuration;

import javax.inject.Inject;

/**
 * Custom implementation of the HTML compressor filter.
 */
public class CustomHTMLCompressorFilter extends HTMLCompressorFilter {

    private Configuration configuration;
    private Environment environment;
    private Materializer mat;

    @Inject
    public CustomHTMLCompressorFilter(Configuration configuration, Environment environment, Materializer mat) {
        this.configuration = configuration;
        this.environment = environment;
        this.mat = mat;
    }

    @Override
    public Configuration configuration() {
        return configuration;
    }

    @Override
    public HtmlCompressor compressor() {
        HtmlCompressor compressor = new HtmlCompressor();
        if (environment.mode() == Mode.DEV) {
            compressor.setPreserveLineBreaks(true);
        }

        compressor.setRemoveComments(true);
        compressor.setRemoveIntertagSpaces(true);
        compressor.setRemoveHttpProtocol(true);
        compressor.setRemoveHttpsProtocol(true);

        return compressor;
    }

    @Override
    public Materializer mat() {
        return mat;
    }
}
