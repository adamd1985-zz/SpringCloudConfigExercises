package ad.tutorials.cloud;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@SpringBootApplication
@RestController
public class SpringConfigClientApplication {

    @Value("${test.name}")
    private String nameConfig = "N/A";

    // ========================================================================

    @RequestMapping("/")
    public String home() {
        return "Hello my name is: " + nameConfig;
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringConfigClientApplication.class, args);
    }
}
