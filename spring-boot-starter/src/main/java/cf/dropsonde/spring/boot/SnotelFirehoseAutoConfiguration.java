package cf.dropsonde.spring.boot;

import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

@Configuration
@ConditionalOnProperty(prefix = "cf.firehose", name = "enabled", matchIfMissing = false)
@Import(FirehoseClientConfiguration.class)
public class SnotelFirehoseAutoConfiguration {}
