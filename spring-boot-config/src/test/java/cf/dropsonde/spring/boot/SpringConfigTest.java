package cf.dropsonde.spring.boot;

import cf.dropsonde.metron.MetronClient;
import org.hamcrest.MatcherAssert;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.hamcrest.Matchers.notNullValue;

public class SpringConfigTest {

	@Configuration
	@EnableMetronClient
	static class TestConfiguration {}

	@Test
	public void testSpringConfig() {
		try (ConfigurableApplicationContext context = new SpringApplication(TestConfiguration.class).run("--debug")) {
			MetronClient metronClient = context.getBean(MetronClient.class);
			MatcherAssert.assertThat(metronClient, notNullValue());
		}
	}

}
