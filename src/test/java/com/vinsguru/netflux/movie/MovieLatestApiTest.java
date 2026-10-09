package com.vinsguru.netflux.movie;

import com.vinsguru.netflux.movie.dto.MovieSummary;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.resttestclient.autoconfigure.AutoConfigureRestTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ProblemDetail;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.client.RestTestClient;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@Sql(scripts = {"/sql/insert-movies.sql", "/sql/insert-bulk-movies.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MovieLatestApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return movies sorted by releaseDate descending with nulls last")
    void shouldSortByReleaseDateDescending() {
        var movies = getLatest("/api/movies/latest?limit=30");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .startsWith(4L, 2L, 1L)
                .endsWith(3L, 5L);
    }

    @Test
    @DisplayName("Should apply default limit of 20")
    void shouldApplyDefaultLimit() {
        var movies = getLatest("/api/movies/latest");

        assertThat(movies).hasSize(20);
    }

    @Test
    @DisplayName("Should apply requested limit")
    void shouldApplyRequestedLimit() {
        var movies = getLatest("/api/movies/latest?limit=2");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(4L, 2L);
    }

    @Test
    @DisplayName("Should accept limit of 100")
    void shouldAcceptMaxLimit() {
        var movies = getLatest("/api/movies/latest?limit=100");

        assertThat(movies).hasSize(30);
    }

    @Test
    @DisplayName("Should return 400 when limit is out of range or not a number")
    void shouldReturn400WhenLimitInvalid() {
        for (var limit : List.of("0", "-1", "101", "abc")) {
            var problem = restTestClient.get()
                    .uri("/api/movies/latest?limit={limit}", limit)
                    .exchange()
                    .expectStatus().isBadRequest()
                    .expectBody(ProblemDetail.class)
                    .returnResult()
                    .getResponseBody();

            assertThat(problem).isNotNull();
            assertThat(problem.getStatus()).isEqualTo(400);
        }
    }

    private List<MovieSummary> getLatest(String uri) {
        return restTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();
    }
}
