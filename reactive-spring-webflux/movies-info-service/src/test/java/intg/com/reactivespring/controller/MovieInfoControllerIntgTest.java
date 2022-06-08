package com.reactivespring.controller;

import com.reactivespring.domain.MovieInfo;
import com.reactivespring.repository.MovieInfoRepository;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.mongo.embedded.EmbeddedMongoAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@AutoConfigureWebTestClient
@ImportAutoConfiguration(exclude = EmbeddedMongoAutoConfiguration.class)
class MovieInfoControllerIntgTest {

    public static final String MOVIE_INFO_URL = "/v1/movieinfos";
    
    @Autowired
    private MovieInfoRepository movieInfoRepository;

    @Autowired
    WebTestClient webTestClient;
    


    @BeforeEach
    void setUp() {

        var movieinfos = List.of(new MovieInfo(null,"Batman Begins",
                        2005,List.of("Christian Bale","Michael Cane"), LocalDate.parse("2005-06-15")),
                new MovieInfo(null,"The Dark Knight",
                        2005,List.of("Christian Bale","HeathLedger"), LocalDate.parse("2008-07-18")),
                new MovieInfo("abc","Dark Knight Rises",
                        2005,List.of("Christian Bale","Tom Hardy"), LocalDate.parse("2012-07-20")));
        movieInfoRepository.saveAll(movieinfos)
                .blockLast();
    }

    @AfterEach
    void tearDown() {
        movieInfoRepository.deleteAll().block();
    }

    @Test
    void addMovieInfo() {
        var movieInfo = new MovieInfo(null,"Batman Begins1",
                2005,List.of("Christian Bale","Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.post()
                .uri(MOVIE_INFO_URL)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isCreated()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var savedMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert savedMovieInfo!=null;
                    assert savedMovieInfo.getMovieInfoId()!=null;
                });


    }

    @Test
    void getAllMoviesInfos() {
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
        var movieInfoInd = "abc";
        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/{id}",movieInfoInd)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody()
                .jsonPath("$.name")
                .isEqualTo("Dark Knight Rises");

                /*.expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var movieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assertNotNull(movieInfo);
                    assertEquals(movieInfoInd,movieInfo.getMovieInfoId());
        });*/
    }

    @Test
    void updateMovieInfo() {
        var movieInfoInd = "abc";
        var movieInfo = new MovieInfo(null,"Dark Knight Rises1",
                2005,List.of("Christian Bale","Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                .uri(MOVIE_INFO_URL+"/{id}",movieInfoInd)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .is2xxSuccessful()
                .expectBody(MovieInfo.class)
                .consumeWith(movieInfoEntityExchangeResult -> {
                    var updateMovieInfo = movieInfoEntityExchangeResult.getResponseBody();
                    assert updateMovieInfo!=null;
                    assert updateMovieInfo.getMovieInfoId()!=null;
                    assertEquals("Dark Knight Rises1",updateMovieInfo.getName());
                });


    }

    @Test
    void deleteMovieInfo() {
        var movieInfoInd = "abc";

        webTestClient.delete()
                .uri(MOVIE_INFO_URL+"/{id}",movieInfoInd)
                .exchange()
                .expectStatus()
                .isNoContent();

    }

    @Test
    void updateMovieInfo_notFound() {
        var movieInfoInd = "def";
        var movieInfo = new MovieInfo(null,"Dark Knight Rises1",
                2005,List.of("Christian Bale","Michael Cane"), LocalDate.parse("2005-06-15"));

        webTestClient.put()
                .uri(MOVIE_INFO_URL+"/{id}",movieInfoInd)
                .bodyValue(movieInfo)
                .exchange()
                .expectStatus()
                .isNotFound();


    }

    @Test
    void getMovieInfoById_notFound() {
        var movieInfoInd = "def";
        webTestClient.get()
                .uri(MOVIE_INFO_URL+"/{id}",movieInfoInd)
                .exchange()
                .expectStatus()
                .isNotFound();

    }


}