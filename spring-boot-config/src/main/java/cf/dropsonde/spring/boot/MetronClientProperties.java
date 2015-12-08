package cf.dropsonde.spring.boot;

import cf.dropsonde.metron.MetronClientBuilder;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("metron")
@Data
public class MetronClientProperties {

	private String origin;
	private String metronAgentHost = MetronClientBuilder.DEFAULT_METRON_AGENT_HOST;
	private int metronAgentPort = MetronClientBuilder.DEFAULT_METRON_AGENT_PORT;

}
