package org.opentosca.toscana.plugins.util;

import org.opentosca.toscana.core.BaseUnitTest;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.Assert.assertTrue;

public class ReadmeBuilderTest extends BaseUnitTest {

    private static final Logger logger = LoggerFactory.getLogger(ReadmeBuilderTest.class);

    @Test
    public void testBuilderWithMarkdownString() {
        logger.info("Initilaizing Readme Builder");
        ReadmeBuilder builder = new ReadmeBuilder("# Hello World", "Hello");
        logger.info("Rendering");
        String result = builder.toString();
        logger.info("Rendered: {}", result);
        assertTrue(result.contains("<h1>Hello World</h1>"));
        assertTrue(result.contains("<title>Hello</title>"));
    }

    @Test
    public void testBuilderWithResourcePath() {
        logger.info("Initilaizing Readme Builder");
        ReadmeBuilder builder = ReadmeBuilder.fromMarkdownResource("Hello", "/readme-builder/sample.md", null);
        logger.info("Rendering");
        String result = builder.toString();
        logger.info("Rendered: {}", result);
        assertTrue(result.contains("<h1>This is a Test</h1>"));
        assertTrue(result.contains("<title>Hello</title>"));
    }
}
