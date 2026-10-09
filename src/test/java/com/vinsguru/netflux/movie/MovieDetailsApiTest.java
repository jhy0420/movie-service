package com.vinsguru.netflux.movie;

import com.vinsguru.netflux.movie.dto.MovieDetails;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MovieDetailsApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return full movie details when ID exists")
    void shouldReturnMovieDetails() {
        var movie = restTestClient.get()
                .uri("/api/movies/{id}", 1)
                .exchange()
                .expectStatus().isOk()
                .expectBody(MovieDetails.class)
                .returnResult()
                .getResponseBody();

        assertThat(movie).isNotNull();
        assertThat(movie.id()).isEqualTo(1L);
        assertThat(movie.title()).isEqualTo("Inception");
        assertThat(movie.voteAverage()).isEqualTo(8.4);
        assertThat(movie.voteCount()).isEqualTo(3000);
        assertThat(movie.releaseDate()).isEqualTo(LocalDate.of(2010, 7, 16));
        assertThat(movie.revenue()).isEqualTo(836800000L);
        assertThat(movie.runtime()).isEqualTo(148);
        assertThat(movie.backdropPath()).isEqualTo("/inception-backdrop.jpg");
        assertThat(movie.budget()).isEqualTo(160000000L);
        assertThat(movie.homepage()).isEqualTo("https://inception.example.com");
        assertThat(movie.overview()).isEqualTo("A thief who steals secrets through dreams.");
        assertThat(movie.popularity()).isEqualTo(90.0);
        assertThat(movie.posterPath()).isEqualTo("/inception-poster.jpg");
        assertThat(movie.director()).isEqualTo("Christopher Nolan");
        assertThat(movie.genres()).isEqualTo(List.of("Action", "Sci-Fi"));
        assertThat(movie.themes()).isEqualTo(List.of("dreams", "heist"));
    }

    @Test
    @DisplayName("Should return 404 ProblemDetail when ID does not exist")
    void shouldReturn404WhenNotFound() {
        var problem = restTestClient.get()
                .uri("/api/movies/{id}", 999)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(404);
        assertThat(problem.getTitle()).isEqualTo("Not Found");
    }

    @Test
    @DisplayName("Should return 400 ProblemDetail when ID is not a number")
    void shouldReturn400WhenIdIsNotNumber() {
        var problem = restTestClient.get()
                .uri("/api/movies/{id}", "abc")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(400);
    }
}
