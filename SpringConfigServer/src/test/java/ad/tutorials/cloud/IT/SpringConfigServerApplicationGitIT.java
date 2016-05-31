package ad.tutorials.cloud.IT;

import ad.tutorials.cloud.SpringConfigServerApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.boot.test.TestRestTemplate;
import org.springframework.cloud.config.environment.Environment;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(classes = SpringConfigServerApplication.class)
@WebAppConfiguration
@IntegrationTest({"server.port=0",
        "spring.cloud.config.enabled=true",
        "spring.cloud.config.server.git.uri=file:///${user.dir}/src/test/resources/test-git-repo"})
@ActiveProfiles({"test"})
public class SpringConfigServerApplicationGitIT {

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
    public void shouldAccessApplicationDefaultConfiguration() {
        String serverConfigContent = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/SpringConfigClient/default/", String.class);

        Assert.assertTrue(serverConfigContent.contains("client-default"));
    }


    @Test
    public void shouldAccessAllApplicationsDefaultConfiguration() throws IOException {
        String serverConfigContent = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/app/default/", String.class);

        Assert.assertTrue(serverConfigContent.contains("test-default"));
    }


    @Test
    public void shouldAccessProfiledAndLabeledConfiguration() throws IOException {
        // Retrieve from server.
        String serverConfigContent = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/application/profile/master/application.yaml", String.class);

        Assert.assertTrue(serverConfigContent.contains("test-profile-default"));
    }

    @Test
    public void shouldAccessHyphenatedProfiledAndLabeledConfiguration() throws IOException {
        // Retrieve from server.
        String serverConfigContent = new TestRestTemplate().getForObject(
                "http://localhost:" + configServerPort + "/master/application-profile.yml", String.class);

        Assert.assertTrue(serverConfigContent.contains("test-profile-default"));
    }

    @Test
    public void shouldGetServerDetails() {
        ResponseEntity<Map> entity = new TestRestTemplate().getForEntity(
                "http://localhost:" + configServerPort + "/admin/env", Map.class);

        Assert.assertEquals(HttpStatus.OK.toString(), entity.getStatusCode().toString());
        Assert.assertTrue(entity.getBody().size() > 0);
    }
}