package cf.dropsonde.spring.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "metron", name = "enabled", matchIfMissing = true)
@Import(MetronClientConfiguration.class)
public class SnotelAutoConfiguration {

}
