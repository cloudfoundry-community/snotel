package cf.dropsonde.spring.boot;

import cf.dropsonde.metron.MetronClient;
import cf.dropsonde.metron.MetronClientBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioDatagramChannel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;

import java.net.InetSocketAddress;

@Configuration
@EnableConfigurationProperties(MetronClientProperties.class)
public class MetronClientConfiguration {

	@Bean
	@ConditionalOnMissingBean
	public MetronClient metronClient(MetronClientProperties properties) {
		return MetronClientBuilder
				.create(properties.getOrigin())
				.metronAgent(new InetSocketAddress(properties.getMetronAgentHost(), properties.getMetronAgentPort()))
				.eventLoopGroup(metronEventLoopGroup(), NioDatagramChannel.class)
				.build();
	}

	@Bean(destroyMethod = "shutdownGracefully")
	@Lazy
	@Qualifier("cf.dropsonde")
	EventLoopGroup metronEventLoopGroup() {
		return new NioEventLoopGroup();
	}

}
