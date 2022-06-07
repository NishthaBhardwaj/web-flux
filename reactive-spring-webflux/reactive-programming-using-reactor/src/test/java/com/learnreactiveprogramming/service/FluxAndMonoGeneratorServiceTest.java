package com.learnreactiveprogramming.service;

import org.junit.jupiter.api.Test;
import reactor.test.StepVerifier;

import static org.junit.jupiter.api.Assertions.*;

class FluxAndMonoGeneratorServiceTest {

    FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

    @Test
    void namesFlux(){
        //given
        //when
        var nameFlux = fluxAndMonoGeneratorService.nameFlux();

        //then
        StepVerifier.create(nameFlux)
                //.expectNext("alex","ben","chloe")
                //.expectNextCount(3)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void nameFlux_map() {
        //given
        int stringLength = 3;

        //when
        var mapFluxNam = fluxAndMonoGeneratorService.nameFlux_map(stringLength);

        //then
        StepVerifier.create(mapFluxNam).expectNext("4_ALEX","5_CHLOE")
                //.expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void nameFlux_immutability() {
        //given
        //when
        var flux = fluxAndMonoGeneratorService.nameFlux_immutability();
        //then
        StepVerifier.create(flux)
                .expectNext("alex")
                .expectNextCount(2)
                .verifyComplete();
    }

    @Test
    void nameFlux_flatmap() {
        //given
        int stringLength = 3;
        //when
        var flux = fluxAndMonoGeneratorService.nameFlux_flatmap(stringLength);
        //then
        StepVerifier.create(flux)
                .expectNext("A","L","E","X","C","H","L","O","E")
                .verifyComplete();

    }

    @Test
    void nameFlux_flatmap_async() {
        //given
        int stringLength = 3;
        //when
        var flux = fluxAndMonoGeneratorService.nameFlux_flatmap_async(stringLength);
        //then
        StepVerifier.create(flux)
                //.expectNext("A","L","E","X","C","H","L","O","E")
                .expectNextCount(9)
                .verifyComplete();
    }
}