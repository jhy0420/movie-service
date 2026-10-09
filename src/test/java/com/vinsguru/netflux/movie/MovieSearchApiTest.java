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
class MovieSearchApiTest {

    @Autowired
    RestTestClient restTestClient;

    @Test
    @DisplayName("Should match title partially and ignoring case")
    void shouldMatchTitlePartiallyIgnoringCase() {
        var movies = search("/api/movies/search?title=NCEPT");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L);
    }

    @Test
    @DisplayName("Should match director partially and ignoring case")
    void shouldMatchDirectorPartiallyIgnoringCase() {
        var movies = search("/api/movies/search?director=nolan");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L, 2L, 4L);
    }

    @Test
    @DisplayName("Should match genre exactly and case-sensitively")
    void shouldMatchGenreExactly() {
        var movies = search("/api/movies/search?genre=Action");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(1L);
    }

    @Test
    @DisplayName("Should combine criteria with AND")
    void shouldCombineCriteriaWithAnd() {
        var movies = search("/api/movies/search?director=nolan&genre=Sci-Fi&title=inter");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(2L);
    }

    @Test
    @DisplayName("Should sort results by popularity descending")
    void shouldSortByPopularityDescending() {
        var movies = search("/api/movies/search?genre=Drama");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(2L, 3L, 5L);
    }

    @Test
    @DisplayName("Should apply default limit of 20")
    void shouldApplyDefaultLimit() {
        var movies = search("/api/movies/search?genre=Bulk");

        assertThat(movies).hasSize(20);
    }

    @Test
    @DisplayName("Should apply requested limit")
    void shouldApplyRequestedLimit() {
        var movies = search("/api/movies/search?genre=Bulk&limit=3");

        assertThat(movies)
                .extracting(MovieSummary::id)
                .containsExactly(125L, 124L, 123L);
    }

    @Test
    @DisplayName("Should return empty list when nothing matches")
    void shouldReturnEmptyListWhenNothingMatches() {
        var movies = search("/api/movies/search?title=nothing-matches-this");

        assertThat(movies).isEmpty();
    }

    @Test
    @DisplayName("Should return 400 when no search criteria are provided")
    void shouldReturn400WhenNoCriteria() {
        expectBadRequest("/api/movies/search");
        expectBadRequest("/api/movies/search?limit=5");
    }

    @Test
    @DisplayName("Should return 400 when search criteria are blank")
    void shouldReturn400WhenCriteriaBlank() {
        var problem = restTestClient.get()
                .uri("/api/movies/search?title={title}&genre=&director={director}", " ", "  ")
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody(ProblemDetail.class)
                .returnResult()
                .getResponseBody();

        assertThat(problem).isNotNull();
        assertThat(problem.getStatus()).isEqualTo(400);
    }

    @Test
    @DisplayName("Should return 400 when limit is out of range or not a number")
    void shouldReturn400WhenLimitInvalid() {
        expectBadRequest("/api/movies/search?title=a&limit=0");
        expectBadRequest("/api/movies/search?title=a&limit=101");
        expectBadRequest("/api/movies/search?title=a&limit=abc");
    }

    private List<MovieSummary> search(String uri) {
        return restTestClient.get()
                .uri(uri)
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
