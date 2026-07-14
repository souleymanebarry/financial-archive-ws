package sync;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import sync.config.RsaKeyProperties;

@EnableConfigurationProperties(RsaKeyProperties.class)
@SpringBootApplication
public class FinancialArchiveServiceApplication {

    public static void main(String[] args) {
        SpringApplication.run(FinancialArchiveServiceApplication.class, args);
    }

}
