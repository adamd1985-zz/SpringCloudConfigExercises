package ad.tutorials.cloud.IT;

import ad.tutorials.cloud.SpringConfigServerApplication;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.cloud.config.server.test.ConfigServerTestUtils;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.util.StringUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.regex.Pattern;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfigServerApplication.class)
@WebAppConfiguration
@IntegrationTest({"server.port=8888",
        "spring.cloud.config.enabled=true",
        "cloud.config.server.native.search-locations: " +
                "classpath:/test-config-repo/, " +
                "classpath:/test-config-repo/{application}, " +
                "classpath:/test-config-repo/{application}/{profile}/{label}, " +
                "classpath:/test-config-repo/{profile}/{label}"})
@ActiveProfiles({"test", "native"})
public class SpringConfigServerApplicationIT {

    @Value("${local.server.port}")
    private int configServerPort = -1;

    @Autowired
    private ConfigurableApplicationContext springContext;


    // ========================================================================


    @Test
    public void shouldStartApplication() {
        Assert.assertTrue(configServerPort > -1);
        Assert.assertNotNull(springContext);
    }

    // This is the most important as an application will ask for an environment to start from.
    @Test
    public void shouldLoadEnvironmentOnApplicationStart() {
        Environment environment = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/app/cloud/test/", Environment.class);

        Assert.assertFalse(environment.getPropertySources().isEmpty());
        Assert.assertFalse(environment.getPropertySources().get(0).getName().isEmpty());
        Assert.assertFalse(environment.getPropertySources().get(0).getSource().toString().isEmpty());
    }


    @Test
    public void shouldAccessDefaultConfiguration() throws IOException {
        // Retrieve from server.
        String serverConfigContent = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/app/cloud/test1/application.yaml", String.class);

        // Load actual file from file system.
        Resource resource = springContext.getResource("classpath:/test-config-repo/application.yaml");
        String expectedContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        Assert.assertEquals(normalizeConfigFileText(expectedContent),
                normalizeConfigFileText(serverConfigContent));

    }

    @Test
    public void shouldAccessSpecificConfiguration() throws IOException {

        // Retrieve from server.
        String serverConfigContent = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/app/cloud/test2/other-application.yaml", String.class);

        // Load actual file from file system.
        Resource resource = springContext.getResource("classpath:/test-config-repo/other-application.yaml");
        String expectedContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        Assert.assertEquals(normalizeConfigFileText(expectedContent),
                normalizeConfigFileText(serverConfigContent));
    }

    @Test
    public void shouldAccessProfiledAndLabeledConfiguration() throws IOException {
        // Retrieve from server.
        String serverConfigContent = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/application/profiles/label/application.yaml", String.class);

        // Load actual file from file system.
        Resource resource = springContext.getResource("classpath:/test-config-repo/profiles/label/application.yaml");
        String expectedContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        Assert.assertEquals(normalizeConfigFileText(expectedContent),
                normalizeConfigFileText(serverConfigContent));
    }

    @Test
    public void shouldAccessHyphenatedProfiledAndLabeledConfiguration() throws IOException {
        // Retrieve from server.
        String serverConfigContent = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/label/application-profiles.yml", String.class);

        // Load actual file from file system.
        Resource resource = springContext.getResource("classpath:/test-config-repo/profiles/label/application.yaml");
        String expectedContent = new String(Files.readAllBytes(Paths.get(resource.getURI())));

        Assert.assertEquals(normalizeConfigFileText(expectedContent),
                normalizeConfigFileText(serverConfigContent));
    }

    @Test
    public void shouldGetServerDetails() {
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + configServerPort + "/admin/env", Map.class);

        Assert.assertEquals(HttpStatus.OK.toString(), entity.getStatusCode().toString());
        Assert.assertTrue(entity.getBody().size() > 0);
    }

    // ------------------------------------------------------------------------

    /**
     * Used only to trim any with spaces, set the encoding to UTF8 and replace any windows endings to linux
     * endings since I cannot assure that server and filesystem will encode the files equally.
     *
     * @return A normalized string.
     */
    private String normalizeConfigFileText(String configText) {
        configText = configText.replaceAll("\\r\\n", "\n");
        configText = configText.replaceAll("\\r", "\n");

        return configText.trim();
    }
}