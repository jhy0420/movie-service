package com.vinsguru.netflux.movie.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

import java.time.LocalDate;
import java.util.List;

@Entity
@Table(name = "movie")
public class Movie {

    @Id
    private Long id;

    private String title;

    @Column(name = "vote_average")
    private Double voteAverage;

    @Column(name = "vote_count")
    private Integer voteCount;

    @Column(name = "release_date")
    private LocalDate releaseDate;

    private Long revenue;

    private Integer runtime;

    @Column(name = "backdrop_path")
    private String backdropPath;

    private Long budget;

    private String homepage;

    private String overview;

    private Double popularity;

    @Column(name = "poster_path")
    private String posterPath;

    private String director;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> genres;

    @JdbcTypeCode(SqlTypes.ARRAY)
    @Column(columnDefinition = "text[]")
    private List<String> themes;

    public Long getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public Double getVoteAverage() {
        return voteAverage;
    }

    public Integer getVoteCount() {
        return voteCount;
    }

    public LocalDate getReleaseDate() {
        return releaseDate;
    }

    public Long getRevenue() {
        return revenue;
    }

    public Integer getRuntime() {
        return runtime;
    }

    public String getBackdropPath() {
        return backdropPath;
    }

    public Long getBudget() {
        return budget;
    }

    public String getHomepage() {
        return homepage;
    }

    public String getOverview() {
        return overview;
    }

    public Double getPopularity() {
        return popularity;
    }

    public String getPosterPath() {
        return posterPath;
    }

    public String getDirector() {
        return director;
    }

    public List<String> getGenres() {
        return genres;
    }

    public List<String> getThemes() {
        return themes;
    }
}
