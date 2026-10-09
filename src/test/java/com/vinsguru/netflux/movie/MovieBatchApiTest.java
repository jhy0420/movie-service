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
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestcontainersConfiguration.class)
@AutoConfigureRestTestClient
@Sql(scripts = "/sql/insert-movies.sql", executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
@Sql(scripts = "/sql/delete-movies.sql", executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
class MovieBatchApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should return movies in requested order")
    void shouldReturnMoviesInRequestedOrder() {
        var movies = getBatch("3,1,2");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(3L, 1L, 2L);
        assertThat(movies.getFirst().title()).isEqualTo("The Godfather");
        assertThat(movies.getFirst().genres()).isEqualTo(List.of("Crime", "Drama"));
    }

    @Test
    @DisplayName("Should remove duplicate IDs")
    void shouldRemoveDuplicateIds() {
        var movies = getBatch("2,1,2,1");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(2L, 1L);
    }

    @Test
    @DisplayName("Should skip IDs that do not exist")
    void shouldSkipUnknownIds() {
        var movies = getBatch("999,1,888");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L);
    }

    @Test
    @DisplayName("Should return 400 when ids parameter is missing")
    void shouldReturn400WhenIdsMissing() {
        expectBadRequest("/api/movies/batch");
    }

    @Test
    @DisplayName("Should return 400 when ids parameter is empty")
    void shouldReturn400WhenIdsEmpty() {
        expectBadRequest("/api/movies/batch?ids=");
    }

    @Test
    @DisplayName("Should return 400 when ids contain a non-numeric value")
    void shouldReturn400WhenIdsNotNumeric() {
        expectBadRequest("/api/movies/batch?ids=1,abc");
    }

    @Test
    @DisplayName("Should return 400 when more than 100 ids are requested")
    void shouldReturn400WhenTooManyIds() {
        var ids = IntStream.rangeClosed(1, 101)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        expectBadRequest("/api/movies/batch?ids=" + ids);
    }

    @Test
    @DisplayName("Should accept exactly 100 ids")
    void shouldAcceptHundredIds() {
        var ids = IntStream.rangeClosed(1, 100)
                .mapToObj(String::valueOf)
                .collect(Collectors.joining(","));

        var movies = getBatch(ids);

        assertThat(movies).hasSize(5);
    }

    private List<MovieSummary> getBatch(String ids) {
        return restTestClient.get()
                .uri("/api/movies/batch?ids={ids}", ids)
                .exchange()
                .expectStatus().isOk()
                .expectBody(new ParameterizedTypeReference<List<MovieSummary>>() {})
                .returnResult()
                .getResponseBody();
    }

    private void expectBadRequest(String uri) {
        var problem = restTestClient.get()
                .uri(uri)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(400);
    }
}
