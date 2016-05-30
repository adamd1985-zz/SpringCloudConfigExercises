package ad.tutorials.cloud.IT;

import ad.tutorials.cloud.SpringConfigClientApplication;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.IntegrationTest;
import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;


@RunWith(SpringJUnit4ClassRunner.class)
@SpringApplicationConfiguration(SpringConfigClientApplication.class)
@WebAppConfiguration
@IntegrationTest("server.port=0")
public class SpringConfigClientApplicationIT {

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
}