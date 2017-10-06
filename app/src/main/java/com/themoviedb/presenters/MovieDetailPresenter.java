package com.themoviedb.presenters;

import android.util.Log;

import com.themoviedb.BaseApplication;
import com.themoviedb.apis.request.DiscoveryRequest;
import com.themoviedb.models.DiscoverModel;
import com.themoviedb.models.MovieDetailModel;
import com.themoviedb.models.MovieModel;
import com.themoviedb.repositories.MovieRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

import static android.R.attr.endYear;
import static android.R.attr.startYear;

/**
 * Created by vaibhav on 3/10/17.
 */

public class MovieDetailPresenter {

    @Inject
    MovieRepository repository;

    private MovieDetailView movieDetailView;

    private MovieDetailModel movieDetail;

    private int movieId;

    public MovieDetailPresenter() {

        BaseApplication.getInstance().getApplicationComponent().inject(this);
    }

    public void startNow(MovieDetailView view, int movieId) {
        this.movieDetailView = view;
        this.movieId = movieId;
        if (movieDetail != null) {
            Log.d("MovieDetailPresenter", "Showing cashed ");
            view.showMovieDetail(movieDetail);
            return;
        }
        fetchMovie(movieId);
    }

    public void fetchMovie(int id) {

        Log.d("MovieDetailPresenter", "Fetching from API");

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

    public void cleanUp() {
        movieId = 0;
        movieDetail = null;
    }

    public interface MovieDetailView {

        void showMovieDetail(MovieDetailModel movieDetailModel);

        void showLoadingProgress();

        void hideLoadingProgress();

        void onError(Throwable e);
    }
}
