package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.when;

@WebFluxTest(controllers = MovieInfoController.class)
@AutoConfigureWebTestClient
public class MovieInfoControllerUnitTest {

    public static final String MOVIE_INFO_URL = "/v1/movieinfos";

    @Autowired
    private WebTestClient webTestClient;

    @MockBean
    private MovieInfoService movieInfoService;

    @Test
    void getAllMoviesInfo() {
        var movieinfos = List.of(new MovieInfo(null,"Batman Begins",
                        2005,List.of("Christian Bale","Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null,"The Dark Knight",
                        2005,List.of("Christian Bale","HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc","Dark Knight Rises",
                        2005,List.of("Christian Bale","Tom Hardy"), LocalDate.parse("2012-07-20")));

        when(movieInfoService.getAllMovieInfos()).thenReturn(Flux.fromIterable(movieinfos));

        webTestClient.get()
                .uri(MOVIE_INFO_URL)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBodyList(MovieInfo.class)
                .hasSize(3);
    }
}
