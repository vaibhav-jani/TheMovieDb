package com.themoviedb.repositories;

import com.themoviedb.models.DiscoverModel;
import com.themoviedb.models.MovieDetailModel;

import io.reactivex.Observable;

public interface IMovieRepository {

    Observable<MovieDetailModel> getMovieDetail(int id);

    Observable<DiscoverModel> getSimilarMovies(int movieId);

    Observable<DiscoverModel> discover(int page);

    Observable<DiscoverModel> discover(int yearStart, int yearEnd, int page);
}
