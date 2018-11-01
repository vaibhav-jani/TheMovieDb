package com.themoviedb.models;

import com.themoviedb.apis.entity.responses.GenreParser;
import com.themoviedb.apis.entity.responses.MovieDetailParser;
import com.themoviedb.apis.entity.responses.ProductionCompanyParser;
import com.themoviedb.apis.entity.responses.ProductionCountryParser;
import com.themoviedb.apis.entity.responses.SpokenLanguageParser;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by vaibhav on 5/10/17.
 */
public class MovieDetailModel {

    private int id;

    private MovieModel movie;

    private int budget;

    private List<GenreModel> genres;

    private String homepage;

    private List<ProductionCompanyModel> productionCompanies = null;

    private List<ProductionCountryModel> productionCountries = null;

    private int revenue;

    private int runtime;

    private List<SpokenLanguageModel> spokenLanguages = null;

    private String status;

    private String tagline;

    public MovieDetailModel() {
    }

    public MovieDetailModel(MovieDetailParser parser) {

        if (parser == null) {
            return;
        }

        this.id = parser.getId();
        this.movie = new MovieModel();
        this.movie.setId(parser.getId());
        this.movie.setVideo(parser.isVideo());
        this.movie.setVoteCount(parser.getVoteCount());
        this.movie.setVoteAverage(parser.getVoteAverage());
        this.movie.setTitle(parser.getTitle());
        this.movie.setPopularity(parser.getPopularity());
        this.movie.setPosterPath(parser.getPosterPath());
        this.movie.setOriginalLanguage(parser.getOriginalLanguage());
        this.movie.setAdult(parser.isAdult());
        this.movie.setOverview(parser.getOverview());
        this.movie.setReleaseDate(parser.getReleaseDate());

        this.budget = parser.getBudget();
        this.genres = new ArrayList<>();
        List<GenreParser> genreParsers = parser.getGenres();
        for (GenreParser genreParser : genreParsers) {
            genres.add(new GenreModel(genreParser));
        }

        this.homepage = parser.getHomepage();

        this.productionCompanies = new ArrayList<>();
        List<ProductionCompanyParser> productionCompanyParsers = parser.getProductionCompanies();
        for (ProductionCompanyParser productionCompanyParser : productionCompanyParsers) {
            productionCompanies.add(new ProductionCompanyModel(productionCompanyParser));
        }

        this.productionCountries = new ArrayList<>();
        List<ProductionCountryParser> productionCountryParsers = parser.getProductionCountries();
        for (ProductionCountryParser countryParser : productionCountryParsers) {
            productionCountries.add(new ProductionCountryModel(countryParser));
        }

        this.revenue = parser.getRevenue();
        this.runtime = parser.getRuntime();

        this.spokenLanguages = new ArrayList<>();
        List<SpokenLanguageParser> spokenLanguageParsers = parser.getSpokenLanguages();
        for (SpokenLanguageParser spokenLanguageParser : spokenLanguageParsers) {
            spokenLanguages.add(new SpokenLanguageModel(spokenLanguageParser));
        }

        this.status = parser.getStatus();
        this.tagline = parser.getTagline();
    }

    public MovieModel getMovie() {
        return movie;
    }

    public void setMovie(MovieModel movie) {
        this.movie = movie;
    }

    public int getBudget() {
        return budget;
    }

    public void setBudget(int budget) {
        this.budget = budget;
    }

    public List<GenreModel> getGenres() {
        return genres;
    }

    public void setGenres(List<GenreModel> genres) {
        this.genres = genres;
    }

    public String getHomepage() {
        return homepage;
    }

    public void setHomepage(String homepage) {
        this.homepage = homepage;
    }

    public List<ProductionCompanyModel> getProductionCompanies() {
        return productionCompanies;
    }

    public void setProductionCompanies(List<ProductionCompanyModel> productionCompanies) {
        this.productionCompanies = productionCompanies;
    }

    public List<ProductionCountryModel> getProductionCountries() {
        return productionCountries;
    }

    public void setProductionCountries(List<ProductionCountryModel> productionCountries) {
        this.productionCountries = productionCountries;
    }

    public int getRevenue() {
        return revenue;
    }

    public void setRevenue(int revenue) {
        this.revenue = revenue;
    }

    public int getRuntime() {
        return runtime;
    }

    public void setRuntime(int runtime) {
        this.runtime = runtime;
    }

    public List<SpokenLanguageModel> getSpokenLanguages() {
        return spokenLanguages;
    }

    public void setSpokenLanguages(List<SpokenLanguageModel> spokenLanguages) {
        this.spokenLanguages = spokenLanguages;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getTagline() {
        return tagline;
    }

    public void setTagline(String tagline) {
        this.tagline = tagline;
    }

    public int getId() {
        return id;
    }
}
