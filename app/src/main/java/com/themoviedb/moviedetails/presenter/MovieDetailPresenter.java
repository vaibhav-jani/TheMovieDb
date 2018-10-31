package com.themoviedb.moviedetails.presenter;

import android.util.Log;

import com.themoviedb.BaseApplication;
import com.themoviedb.models.MovieDetailModel;
import com.themoviedb.moviedetails.MovieDetailContract;
import com.themoviedb.repositories.IMovieRepository;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.annotations.Nullable;
import io.reactivex.disposables.Disposable;

/**
 * Created by vaibhav on 3/10/17.
 */

public class MovieDetailPresenter implements MovieDetailContract.IMovieDetailPresenter {

    @Inject
    IMovieRepository repository;

    @Nullable
    private MovieDetailContract.IMovieDetailView view;

    private MovieDetailContract.MovieDetailState state = new MovieDetailContract.MovieDetailState();

    public MovieDetailPresenter() {

        BaseApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void attachView(MovieDetailContract.IMovieDetailView view) {
        this.view = view;
    }

    @Override
    public void fetchMovie(final int id) {

        if (state.getMovieId() == id && state.getMovieDetail() != null) {
            Log.d("MovieDetailPresenter", "Showing cashed ");
            updateView(state);
            return;
        }

        Log.d("MovieDetailPresenter", "Fetching from API");
        Observable<MovieDetailModel> observable = repository.getMovieDetail(id);
        observable.subscribe(new Observer<MovieDetailModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                state = new MovieDetailContract.MovieDetailState();
                state.setLoading(true);
                updateView(state);
            }

            @Override
            public void onNext(@NonNull MovieDetailModel model) {

                if (model == null) {
                    return;
                }
                state = new MovieDetailContract.MovieDetailState(model, id, false);
                updateView(state);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                state.setLoading(false);
                state.setError(e);
                updateView(state);
            }

            @Override
            public void onComplete() {
                state.setLoading(false);
                updateView(state);
            }
        });
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void updateView(MovieDetailContract.MovieDetailState state) {

        if (view == null) {
            return;
        }

        MovieDetailModel movieDetail = state.getMovieDetail();
        if (movieDetail != null) {
            view.showMovieDetail(movieDetail);
        }

        if (state.isLoading()) {
            view.showLoadingProgress();
        } else {
            view.hideLoadingProgress();
        }

        Throwable error = state.getError();
        if (error != null) {
            view.onError(error);
        }
    }

}
