package cf.dropsonde.samples;

import cf.dropsonde.firehose.Firehose;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.stereotype.Component;

@SpringBootApplication
public class SnotelFirehoseClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(SnotelFirehoseClientApplication.class, args);
    }

}


@Component
class SampleCommandLineRunner implements CommandLineRunner {

    private final Firehose firehose;

    @Autowired
    public SampleCommandLineRunner(Firehose firehose) {
        this.firehose = firehose;
    }

    @Override
    public void run(String... args) throws Exception {
        this.firehose
                .open()
                .toBlocking()
                .forEach(envelope -> {
                    System.out.println(envelope);
                });
    }
}
