package com.themoviedb.moviedetails.presenter;

import android.util.Log;

import com.themoviedb.BaseApplication;
import com.themoviedb.home.HomeContract;
import com.themoviedb.models.MovieDetailModel;
import com.themoviedb.moviedetails.MovieDetailsContract;
import com.themoviedb.repositories.MovieRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by vaibhav on 3/10/17.
 */

public class MovieDetailPresenter implements MovieDetailsContract.IMovieDetailPresenter {

    @Inject
    MovieRepository repository;

    private MovieDetailsContract.IMovieDetailView movieDetailView;

    private MovieDetailModel movieDetail;

    private int movieId;

    public MovieDetailPresenter() {

        BaseApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void attachView(MovieDetailsContract.IMovieDetailView view) {
        this.movieDetailView = view;
    }

    @Override
    public void fetchMovie(int id) {

        if (movieId == id && movieDetail != null) {
            Log.d("MovieDetailPresenter", "Showing cashed ");
            movieDetailView.showMovieDetail(movieDetail);
            return;
        }

        Log.d("MovieDetailPresenter", "Fetching from API");

        this.movieId = movieId;
        Observable<MovieDetailModel> observable = repository.getMovieDetail(id);
        observable.subscribe(new Observer<MovieDetailModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                movieDetailView.showLoadingProgress();
            }

            @Override
            public void onNext(@NonNull MovieDetailModel model) {

                if (model == null) {
                    return;
                }
                movieDetail = model;
                movieDetailView.showMovieDetail(model);
            }

            @Override
            public void onError(@NonNull Throwable e) {

                movieDetailView.onError(e);
            }

            @Override
            public void onComplete() {
                movieDetailView.hideLoadingProgress();
            }
        });
    }

    @Override
    public void detachView() {
        movieId = 0;
        movieDetail = null;
    }
}
