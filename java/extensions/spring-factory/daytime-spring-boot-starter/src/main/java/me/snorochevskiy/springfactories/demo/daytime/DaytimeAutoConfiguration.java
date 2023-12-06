package me.snorochevskiy.springfactories.demo.daytime;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
@EnableConfigurationProperties
@ConditionalOnProperty(value = "daytime.service.enabled", matchIfMissing = false)
public class DaytimeAutoConfiguration {

    @Autowired
    private ApplicationContext applicationContext;

    @Bean
    public DaytimeService daytimeService() {
        Environment env = applicationContext.getEnvironment();
        String host = env.getProperty("daytime.service.host", "time.nist.gov");
        String port = env.getProperty("daytime.service.port", "13");
        return new DaytimeService(host, Integer.parseInt(port));
    }
}
