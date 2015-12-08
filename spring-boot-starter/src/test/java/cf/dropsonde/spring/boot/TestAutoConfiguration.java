package cf.dropsonde.spring.boot;

import cf.dropsonde.metron.MetronClient;
import org.hamcrest.Matchers;
import org.junit.Test;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class TestAutoConfiguration {

	@Configuration
	@EnableAutoConfiguration
	static class AutoConfiguration {}

	@Test
	public void testAutoConfiguration() {
		try (ConfigurableApplicationContext context = new SpringApplication(AutoConfiguration.class).run()) {
			assertThat(context, notNullValue());
			assertThat(context.getBean(MetronClient.class), notNullValue());
		}
	}

	@Test
	public void disableAutoConfiguration() {
		SpringApplication springApplication = new SpringApplication(AutoConfiguration.class);
		springApplication.setAdditionalProfiles("disableAutoConfig");
		try (ConfigurableApplicationContext context = springApplication.run()) {
			assertThat(context.getBeansOfType(MetronClient.class).entrySet(), Matchers.iterableWithSize(0));
		}
	}

}
