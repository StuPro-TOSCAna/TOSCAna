package org.opentosca.toscana.plugins.util;

import java.io.IOException;
import java.io.InputStream;

import org.opentosca.toscana.core.transformation.TransformationContext;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StreamUtils;

public class ReadmeBuilder {
    private static final String README_TEMPLATE_PATH = "/readme-builder/template.html";
    private static final String README_TEMPLATE_TITLE_KEY = "#PAGETITLE#";
    private static final String README_TEMPLATE_BODY_KEY = "#PAGEBODY#";

    private static final Logger logger = LoggerFactory.getLogger(ReadmeBuilder.class);

    private static String TEMPLATE;

    static {
        InputStream templateInput = ReadmeBuilder.class.getResourceAsStream(README_TEMPLATE_PATH);
        try {
            byte[] templateData = StreamUtils.copyToByteArray(templateInput);
            TEMPLATE = new String(templateData);
        } catch (IOException e) {
            logger.error("Loading the Readme Template has Failed", e);
        }
    }

    private String markdownText;
    private String pageTitle;

    public ReadmeBuilder(String markdownText, String pageTitle) {
        this.markdownText = markdownText;
        this.pageTitle = pageTitle;
    }

    public static ReadmeBuilder fromMarkdownResource(String title, String path, TransformationContext context) {
        Logger log = null;
        if (context != null) {
            log = context.getLogger(ReadmeBuilder.class);
        } else {
            log = logger;
        }
        
        InputStream templateInput = ReadmeBuilder.class.getResourceAsStream(path);
        try {
            byte[] templateData = StreamUtils.copyToByteArray(templateInput);
            String rawMarkdown = new String(templateData);
            return new ReadmeBuilder(rawMarkdown, title);
        } catch (IOException e) {
            log.error("Loading the Readme resource {} has Failed", path, e);
            return null;
        }
    }

    @Override
    public String toString() {
        Parser markdownParser = Parser.builder().build();
        Node markdownDocument = markdownParser.parse(this.markdownText);
        HtmlRenderer renderer = HtmlRenderer.builder().build();

        return TEMPLATE.replace(README_TEMPLATE_TITLE_KEY, this.pageTitle)
            .replace(README_TEMPLATE_BODY_KEY, renderer.render(markdownDocument));
    }
}
