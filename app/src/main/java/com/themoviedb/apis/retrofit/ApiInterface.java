package com.themoviedb.apis.retrofit;

import com.themoviedb.apis.entity.responses.DiscoverResponseParser;
import com.themoviedb.apis.entity.responses.MovieDetailParser;

import io.reactivex.Observable;
import retrofit2.Response;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;


public interface ApiInterface {

    @GET("discover/movie")
    Observable<Response<DiscoverResponseParser>> discover(@Query("page") int page,
                                                          @Query("sort_by") String sortBy,
                                                          @Query("primary_release_date.lte") String releaseDateLte,
                                                          @Query("primary_release_date.gte") String releaseDateGte,
                                                          @Query("with_original_language") String withOriginalLanguage,
                                                          @Query("api_key") String apiKey);

    @GET("movie/{id}")
    Observable<Response<MovieDetailParser>> getMovieDetail(@Path("id") int id,
                                                           @Query("api_key") String apiKey);

    @GET("movie/{id}/similar")
    Observable<Response<DiscoverResponseParser>> getSimilarMovies(@Path("id") int movieId,
                                                             @Query("api_key") String apiKey);

}