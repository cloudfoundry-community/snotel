package cf.dropsonde.spring.boot;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Holder for the properties relevant to a Firehose client
 */

@ConfigurationProperties("cf.firehose")
@Data
public class FirehoseClientProperties {
    private String endpoint;
    private String authToken;
    private boolean skipTlsValidation = false;
}
