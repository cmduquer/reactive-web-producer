package com.reactive.web.controller;

import java.time.Duration;
import java.time.Instant;
import java.util.stream.Stream;

import org.reactivestreams.Publisher;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import com.reactive.web.model.Greeting;

import reactor.core.publisher.Flux;
import reactor.util.function.Tuple2;

@RestController
public class GreetReactiveController {  
    @GetMapping("/greetings")
    public Publisher<Greeting> greetingPublisher() {
    	//Calling Flux.generate() will create a never ending stream of the Greeting object.
        Flux<Greeting> greetingFlux = Flux.<Greeting>generate(sink -> sink.next(new Greeting("Hello"))).take(50);
        //The take() method, as the name suggests, will only take first 50 values from the stream.
        //It's important to note that the return type of the method is the asynchronous type Publisher<Greeting>.
        return greetingFlux;
    }
    
    @GetMapping(value = "/greetings/sse", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    Flux<Greeting> events() {  
        Flux<Greeting> greetingFlux = Flux.fromStream(Stream.generate(() -> new Greeting("Hello @" + Instant.now().toString())));
        Flux<Long> durationFlux = Flux.interval(Duration.ofSeconds(1));
        return Flux.zip(greetingFlux, durationFlux).map(Tuple2::getT1);
    }
}
