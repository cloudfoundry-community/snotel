package cf.dropsonde.spring.boot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

/**
 * Annotation to turn on consuming a Cloud Foundry Firehose Websocket Stream
 */

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(FirehoseClientConfiguration.class)
@Documented
public @interface EnableFirehoseClient {
}
