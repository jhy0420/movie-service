package com.vinsguru.netflux.movie.service;

import com.vinsguru.netflux.movie.dto.MovieDetails;
import com.vinsguru.netflux.movie.dto.MovieSearchRequest;
import com.vinsguru.netflux.movie.dto.MovieSummary;
import com.vinsguru.netflux.movie.entity.Movie;
import com.vinsguru.netflux.movie.exceptions.MovieNotFoundException;
import com.vinsguru.netflux.movie.mapper.MovieMapper;
import com.vinsguru.netflux.movie.repository.MovieRepository;
import com.vinsguru.netflux.movie.repository.MovieSpecifications;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class MovieService {

    /**
     * Orders movies by vote count, highest first. Movies without a vote count are placed last.
     */
    private static final Sort POPULAR_SORT = Sort.by(Sort.Order.desc("voteCount").nullsLast());
    private static final Sort LATEST_SORT = Sort.by(Sort.Order.desc("releaseDate").nullsLast());
    private static final Sort SEARCH_SORT = Sort.by(Sort.Order.desc("popularity").nullsLast());

    private final MovieRepository repository;

    public MovieService(MovieRepository repository) {
        this.repository = repository;
    }

    public MovieDetails findById(Long id) {
        return repository.findById(id)
                .map(MovieMapper::toDetails)
                .orElseThrow(() -> new MovieNotFoundException(id));
    }

    public List<MovieSummary> findByIds(List<Long> ids) {
        var distinctIds = ids.stream()
                .distinct()
                .toList();
        var moviesById = repository.findAllById(distinctIds).stream()
                .collect(Collectors.toMap(Movie::getId, Function.identity()));
        return distinctIds.stream()
                .map(moviesById::get)
                .filter(Objects::nonNull)
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> findPopular(int limit) {
        return repository.findAll(PageRequest.of(0, limit, POPULAR_SORT)).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> findLatest(int limit) {
        return repository.findAll(PageRequest.of(0, limit, LATEST_SORT)).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    public List<MovieSummary> search(MovieSearchRequest request) {
        var spec = Specification.<Movie>where(MovieSpecifications.titleContains(normalize(request.title()).orElse(null)))
                .and(MovieSpecifications.hasGenre(normalize(request.genre()).orElse(null)))
                .and(MovieSpecifications.directorContains(normalize(request.director()).orElse(null)));
        return repository.findAll(spec, PageRequest.of(0, request.limit(), SEARCH_SORT)).stream()
                .map(MovieMapper::toSummary)
                .toList();
    }

    private Optional<String> normalize(String value) {
        return Optional.ofNullable(value)
                .filter(StringUtils::hasText);
    }
}
