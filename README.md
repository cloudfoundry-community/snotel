# Snotel

[![Gitter](https://badges.gitter.im/Join%20Chat.svg)](https://gitter.im/cloudfoundry-community/snotel?utm_source=badge&utm_medium=badge&utm_campaign=pr-badge&utm_content=badge)

Snotel is a Java based Doppler Firehose and Metron client. Its name follows the [Loggregator]
(https://github.com/cloudfoundry/loggregator) naming convention relating to meteorology (Doppler, Metron, NOAA, etc.) More
importantly, the name Snotel was derived from the
fact that it was initially created close to the home of [the greatest snow on earth]
(http://www.onthesnow.com/news/a/9312/utah-claims-the-greatest-snow-on-earth).

Snotel is still in an early state and the API will change over time.

## Firehose client

The Firehose client is built on [Netty](http://netty.io/) and [RxJava](https://github.com/ReactiveX/RxJava).

An example of using the Firehose client:

```java
try (final Firehose firehose = FirehoseBuilder.create("wss://doppler.cf.teal.springapps.io:443", "bearer eyJh... this is not a valid token")
		.skipTlsValidation(true)
		.build()) {
	firehose
		.open()
		.toBlocking()
		.forEach(System.out::println);
}
```

## Metron client

The Metron client can be used to emit [Dropsonde](https://github.com/cloudfoundry/dropsonde) events to Loggregator via the
Metron agent.

An example of using the Metron client:

```java
final MetronClient metronClient = MetronClientBuilder
		.create("myorigin")
		.build();
metronClient.emitValueMetric("application.uptime", 1, "s");
metronClient.emitCounterEvent("mikes.counter", 5);
metronClient.emitError("mikes.computer", 54321, "Run for the hills! It's about to blow!");
```
