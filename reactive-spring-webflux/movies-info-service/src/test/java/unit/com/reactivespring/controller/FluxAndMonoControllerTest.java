package com.reactivespring.controller;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebFlux;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.test.StepVerifier;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

//this annotation is going to give you access to all the endpoints that is available in this class
@WebFluxTest(controllers = FluxAndMonoController.class)
@AutoConfigureWebTestClient
class FluxAndMonoControllerTest {

    @Autowired
    WebTestClient webTestClient;

    @Test
    void flux() {
        webTestClient.
                get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .hasSize(5);

    }

    @Test
    void flux_approach2() {
        var flux = webTestClient.
                get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Integer.class)
                .getResponseBody();

        StepVerifier.create(flux).
                expectNext(1,2,3,4,5)
                .verifyComplete();


    }

    @Test
    void flux_approach3() {
        webTestClient.
                get()
                .uri("/flux")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(Integer.class)
                .consumeWith(listEntityExchangeResult -> {

                    var resposeBody = listEntityExchangeResult.getResponseBody();
                    assert (Objects.requireNonNull(resposeBody).size() == 5);
                });

    }

    @Test
    void mono() {
        webTestClient.
                get()
                .uri("/mono")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(String.class)
                .consumeWith(stringEntityExchangeResult -> {

                    var responseBody = stringEntityExchangeResult.getResponseBody();
                    assertEquals("Hello World Mono",responseBody);
                });

    }

    @Test
    void stream() {
        var flux = webTestClient.
                get()
                .uri("/stream")
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .returnResult(Long.class)
                .getResponseBody();

        StepVerifier.create(flux).
                expectNext(0L,1L,2L,3L,4L)
                .thenCancel()
                .verify();


    }
}