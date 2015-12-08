package cf.dropsonde.spring.boot;

import cf.dropsonde.metron.MetronClient;
import cf.dropsonde.metron.MetronClientBuilder;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
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
	@ConditionalOnProperty("metron.enabled")
	public MetronClient metronClient(MetronClientProperties properties) {
		return MetronClientBuilder
				.create(properties.getOrigin())
				.metronAgent(InetSocketAddress.createUnresolved(properties.getMetronAgentHost(), properties.getMetronAgentPort()))
				.eventLoopGroup(metronEventLoopGroup(), NioSocketChannel.class)
				.build();
	}

	@Bean(destroyMethod = "shutdownGracefully")
	@Lazy
	@Qualifier("cf.dropsonde")
	EventLoopGroup metronEventLoopGroup() {
		return new NioEventLoopGroup();
	}

}
