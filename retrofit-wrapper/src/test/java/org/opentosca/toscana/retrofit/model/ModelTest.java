package org.opentosca.toscana.retrofit.model;

import java.io.InputStream;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

import org.opentosca.toscana.retrofit.model.embedded.CsarResources;
import org.opentosca.toscana.retrofit.model.embedded.PlatformResources;
import org.opentosca.toscana.retrofit.model.embedded.TransformationResources;
import org.opentosca.toscana.retrofit.model.validation.CsarsValidator;
import org.opentosca.toscana.retrofit.model.validation.ErrorValidator;
import org.opentosca.toscana.retrofit.model.validation.GetPropertiesValidator;
import org.opentosca.toscana.retrofit.model.validation.HealthValidator;
import org.opentosca.toscana.retrofit.model.validation.IModelValidator;
import org.opentosca.toscana.retrofit.model.validation.LogValidator;
import org.opentosca.toscana.retrofit.model.validation.MetricsValidator;
import org.opentosca.toscana.retrofit.model.validation.PlatformsValidator;
import org.opentosca.toscana.retrofit.model.validation.TransformationsValidator;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class ModelTest {

    private static final Logger logger = LoggerFactory.getLogger(ModelTest.class);
    
    private String resourcePath;
    private Class<?> resultClass;
    private IModelValidator modelValidator;

    private ObjectMapper mapper;

    @SuppressWarnings("unused")
    public ModelTest(
        String resourcePath,
        Class<?> resultClass,
        IModelValidator modelValidator,
        String name
    ) {
        this.resourcePath = resourcePath;
        this.resultClass = resultClass;
        this.modelValidator = modelValidator;
    }

    @Parameters(name = "{index}: {3}")
    public static Collection<Object[]> data() {
        return Arrays.asList(new Object[][] {
            {"json/get_properties.json", TransformationProperties.class, new GetPropertiesValidator(), "Get Properties"},
            {"json/logs.json", TransformationLogs.class, new LogValidator(), "Log"},
            {"json/health.json", TransformerStatus.class, new HealthValidator(), "Health"},
            {"json/metrics.json", Map.class, new MetricsValidator(), "Metrics Model"},
            {"json/transformations.json", TransformationResources.class, new TransformationsValidator(), "Transformations"},
            {"json/platforms.json", PlatformResources.class, new PlatformsValidator(), "Platforms"},
            {"json/csars.json", CsarResources.class, new CsarsValidator(), "Csars Model"},
            {"json/regular_error.json", ServerError.class, new ErrorValidator(false), "Regular Error"},
            {"json/parse_error.json", ServerError.class, new ErrorValidator(true), "Parsing Error"}
        });
    }

    @Before
    public void setUp() throws Exception {
        logger.info("Creating Mapper");
        this.mapper = new ObjectMapper();
    }

    @Test
    public void validateModel() throws Exception {
        logger.info("Loading Resource {}",resourcePath);
        InputStream in = getClass().getClassLoader().getResourceAsStream(resourcePath);
        logger.info("Mapping");
        Object o = mapper.readValue(in, resultClass);
        logger.info("Validating");
        modelValidator.validate(o);
        logger.info("Done");
    }
}
