package com.learnreactiveprogramming.service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Duration;
import java.util.List;
import java.util.Random;

public class FluxAndMonoGeneratorService {

    public Flux<String> nameFlux(){
        return Flux.fromIterable(List.of("alex","ben","chloe")).log();
    }

    public Flux<String> nameFlux_map(int stringLength){
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .filter(name -> name.length() > stringLength)
                .map(name -> name.length() + "_" +name.toUpperCase())
                .log();
    }

    public Flux<String> nameFlux_flatmap(int stringLength){
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .filter(name -> name.length() > stringLength)
                .flatMap(name -> splitString(name.toUpperCase()))
                .log();
    }

    public Flux<String> nameFlux_flatmap_async(int stringLength){
        return Flux.fromIterable(List.of("alex","ben","chloe"))
                .filter(name -> name.length() > stringLength)
                .flatMap(name -> splitString_withDelay(name.toUpperCase()))
                .log();
    }

    public Flux<String> splitString(String name){
        var chaArry = name.split("");
        return Flux.fromArray(chaArry);

    }

    public Flux<String> splitString_withDelay(String name){
        var chaArry = name.split("");
        var delay = new Random().nextInt(1000);
        return Flux.fromArray(chaArry)
                .delayElements(Duration.ofMillis(delay));

    }

    public Flux<String> nameFlux_immutability(){
        var nameFlux =  Flux.fromIterable(List.of("alex","ben","chloe"));
        nameFlux.map(String::toUpperCase);
        return nameFlux;

    }

    public Mono<String> nameMono(){
        return Mono.just("Nishtha").log();
    }

    public static void main(String[] args) {

        FluxAndMonoGeneratorService fluxAndMonoGeneratorService = new FluxAndMonoGeneratorService();

        fluxAndMonoGeneratorService.nameFlux().subscribe(name -> System.out.println(name));
        fluxAndMonoGeneratorService.nameMono().subscribe(name -> System.out.println("Mono name is " +name));

    }

}
