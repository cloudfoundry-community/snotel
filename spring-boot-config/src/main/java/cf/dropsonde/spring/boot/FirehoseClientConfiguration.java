package cf.dropsonde.spring.boot;

import cf.dropsonde.firehose.Firehose;
import cf.dropsonde.firehose.FirehoseBuilder;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Responsible for all bean definitions to set up a Firehose client
 */
@Configuration
@EnableConfigurationProperties(FirehoseClientProperties.class)
public class FirehoseClientConfiguration {
    @Bean
    public Firehose firehose(FirehoseClientProperties firehoseClientProperties) {
        return FirehoseBuilder.create(
                firehoseClientProperties.getEndpoint(),
                firehoseClientProperties.getAuthToken()
        ).skipTlsValidation(firehoseClientProperties.isSkipTlsValidation())
                .build();
    }
}
