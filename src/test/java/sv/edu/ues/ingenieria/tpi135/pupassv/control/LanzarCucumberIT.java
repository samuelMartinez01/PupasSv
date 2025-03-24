package sv.edu.ues.ingenieria.tpi135.pupassv.control;

import static io.cucumber.core.options.Constants.*;
import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

/**
 * Clase para lanzar cucumber
 */
@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource(value = "rest/orden/bdd")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "sv.edu.ues.ingenieria.tpi135.pupassv.boundary.rest.cucumber")
public class LanzarCucumberIT {

}
