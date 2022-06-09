package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.validation.Valid;

@RestController
@RequestMapping("/v1")
@Slf4j
public class MovieInfoController {

    private MovieInfoService movieInfoService;

    public MovieInfoController(MovieInfoService movieInfoService) {
        this.movieInfoService = movieInfoService;
    }


    @GetMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> getMovieInfoById(@PathVariable("id") String id){
        return movieInfoService.getMovieById(id)
                .map(movieInfo -> {
                    return ResponseEntity.ok().body(movieInfo);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();

    }

    @GetMapping("/movieinfos")
    public Flux<MovieInfo> getAllMovieInfos(@RequestParam(value = "year", required = false) Integer year
    ,@RequestParam(value = "name" , required = false) String name){
        log.info("Year is {} ", year);
        if(year!=null){
            return movieInfoService.getMovieInfoByYear(year).log();
        }
        if(name!=null){
            return movieInfoService.getMovieInfoByName(name);
        }
        return movieInfoService.getAllMovieInfos();

    }

    @PostMapping("/movieinfos")
    @ResponseStatus(HttpStatus.CREATED)
    public Mono<MovieInfo> addMovieInfo(@RequestBody @Valid MovieInfo movieInfo){
        return movieInfoService.addMovieInfo(movieInfo);

    }

    @PutMapping("/movieinfos/{id}")
    public Mono<ResponseEntity<MovieInfo>> updateMovieInfo(@RequestBody MovieInfo updatedMovieInfo, @PathVariable String id){
        return movieInfoService.updateMovieInfo(updatedMovieInfo,id)
                .map(movieInfo -> {
                    return ResponseEntity.ok(movieInfo);
                })
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()))
                .log();

    }

    @DeleteMapping("/movieinfos/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public Mono<Void> deleteMovieInfo(@PathVariable String id){
        return movieInfoService.deleteMovieInfo(id);

    }

    public Flux<ResponseEntity<MovieInfo>>  getMovieInfosByName(@RequestParam(value = "name") String name){
        log.info("Movie Name is {}", name);
        return movieInfoService.getMovieInfoByName(name)
                .map(movieInfo -> {
                    return ResponseEntity.ok().body(movieInfo);
                }).switchIfEmpty(Flux.just(ResponseEntity.notFound().build()));



    }



}
