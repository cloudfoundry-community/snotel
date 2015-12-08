package cf.dropsonde.spring.boot;

import org.springframework.context.annotation.Import;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Import(MetronClientConfiguration.class)
@Documented
public @interface EnableMetronClient {
}
