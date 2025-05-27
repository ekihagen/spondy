package no.spond.club;

import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.TestPropertySource;

@TestConfiguration
@Profile("test")
@TestPropertySource(locations = "classpath:application-test.yml")
public class TestConfig {

    @Bean
    @Primary
    public String testMarker() {
        return "test-environment";
    }
} 