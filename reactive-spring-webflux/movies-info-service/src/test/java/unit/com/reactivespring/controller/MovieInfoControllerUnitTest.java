package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.service.MovieInfoService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.autoconfigure.web.reactive.WebFluxTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.reactive.server.EntityExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.*;
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

    @Test
    void getMovieInfoById() {
        var movieInfo = new MovieInfo("abc","Dark Knight Rises_abc",
                2005,List.of("Christian Bale","Tom Hardy"), LocalDate.parse("2012-07-20"));

        when(movieInfoService.getMovieById(anyString())).thenReturn(Mono.just(movieInfo));
        var movieInfoInd = "abc";
        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/{id}",movieInfoInd)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfoReturn = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfoReturn);
                    assertEquals(movieInfo.getMovieInfoId(),movieInfoReturn.getMovieInfoId());
        });
    }

    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo("mock", "Batman Begins1",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoService.addMovieInfo(isA(MovieInfo.class))).thenReturn(Mono.just(movieInfo));
        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {

                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(savedMovieInfo);
                    assertEquals(movieInfo.getMovieInfoId(),savedMovieInfo.getMovieInfoId());

                });
    }

    @Test
    void updateMovieInfo() {
        var movieInfoInd = "abc";
        var movieInfo = new MovieInfo(movieInfoInd, "Dark Knight Rises",
                2005, List.of("Christian Bale", "Michael Cane"), LocalDate.parse("2005-06-15"));

        when(movieInfoService.updateMovieInfo(isA(MovieInfo.class),isA(String.class))).thenReturn(Mono.just(movieInfo));

        webTestClient.put()
                .uri(MOVIE_INFO_URL+"/{}",movieInfo)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updatedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    System.out.println(updatedMovieInfo);
                    assert updatedMovieInfo!=null;
                    assert updatedMovieInfo.getMovieInfoId()!=null;
                    assertEquals("Dark Knight Rises",updatedMovieInfo.getName());

                });

    }

    @Test
    void deleteMovieInfo() {
        var movieInfoInd = "abc";

        when(movieInfoService.deleteMovieInfo(isA(String.class))).thenReturn(Mono.empty());

        webTestClient.delete()
                .uri(MOVIE_INFO_URL+"/{id}",movieInfoInd)
                .exchange()
                .expectStatus()
                .isNoContent();

    }

    @Test
    void addMovieInfo_validation() {
        var movieInfo = new MovieInfo("mock", "",
                -2005, List.of(""), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isBadRequest()
                .expectBody(String.class)
                .consumeWith(stringInfoEntityExchangeResult -> {

                    var responseBody = stringInfoEntityExchangeResult.getResponseBody();
                    var expectedErrorMessage = "movieInfo.cast must be present,movieInfo.name must be present," +
                            "movieInfo.year must be Positive value";
                    assert responseBody!=null;
                    assertEquals(expectedErrorMessage,responseBody);
                    System.out.println(responseBody);

                });
    }



}
