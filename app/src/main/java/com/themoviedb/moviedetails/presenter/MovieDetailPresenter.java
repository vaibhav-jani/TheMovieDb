package com.themoviedb.moviedetails.presenter;

import android.util.Log;

import com.themoviedb.BaseApplication;
import com.themoviedb.base.BaseState;
import com.themoviedb.models.DiscoverModel;
import com.themoviedb.models.MovieDetailModel;
import com.themoviedb.models.MovieModel;
import com.themoviedb.moviedetails.MovieDetailContract;
import com.themoviedb.moviedetails.MovieDetailContract.IMovieDetailView;
import com.themoviedb.moviedetails.MovieDetailContract.MovieDetailState;
import com.themoviedb.moviedetails.MovieDetailContract.SimilarMovieListState;
import com.themoviedb.repositories.IMovieRepository;

import java.util.List;

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
    private IMovieDetailView view;

    private MovieDetailState detailState = new MovieDetailState();

    private SimilarMovieListState similarListState = new SimilarMovieListState();

    public MovieDetailPresenter() {

        BaseApplication.getInstance().getDataComponent().inject(this);
    }

    @Override
    public void attachView(IMovieDetailView view) {
        this.view = view;
    }

    @Override
    public void fetchMovie(final int id) {

        if (detailState.getMovieId() == id && detailState.getMovieDetail() != null) {
            Log.d("MovieDetailPresenter", "Showing cashed ");
            updateView(detailState);
            return;
        }

        Log.d("MovieDetailPresenter", "Fetching from API");
        Observable<MovieDetailModel> observable = repository.getMovieDetail(id);
        observable.subscribe(new Observer<MovieDetailModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                detailState = new MovieDetailState();
                detailState.setLoading(true);
                updateView(detailState);
            }

            @Override
            public void onNext(@NonNull MovieDetailModel model) {

                if (model == null) {
                    return;
                }
                detailState = new MovieDetailState(model, id, false);
                updateView(detailState);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                detailState.setLoading(false);
                detailState.setError(e);
                updateView(detailState);
            }

            @Override
            public void onComplete() {
                detailState.setLoading(false);
                updateView(detailState);
            }
        });
    }

    @Override
    public void fetchSimilarMovie(final int id) {

        if (similarListState.getMovieId() == id && !similarListState.getMovies().isEmpty()) {
            Log.d("MovieDetailPresenter", "Showing cashed similar list");
            updateView(similarListState);
            return;
        }

        Log.d("MovieDetailPresenter", "Fetching similar movies from API");
        repository.getSimilarMovies(id).subscribe(new Observer<DiscoverModel>() {
            @Override
            public void onSubscribe(Disposable d) {
                similarListState.setLoading(true);
                updateView(similarListState);
            }

            @Override
            public void onNext(DiscoverModel discoverModel) {

                if (discoverModel == null) {
                    return;
                }

                List<MovieModel> movies = discoverModel.getMovies();
                similarListState = new SimilarMovieListState(movies, id, false);
                updateView(similarListState);
            }

            @Override
            public void onError(Throwable e) {
                similarListState.setLoading(false);
                similarListState.setError(e);
                updateView(similarListState);
            }

            @Override
            public void onComplete() {
                similarListState.setLoading(false);
                updateView(similarListState);
            }
        });
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void updateView(@NonNull BaseState state) {

        if (view == null) {
            return;
        }

        if (state instanceof MovieDetailState) {
            updateMovieDetailView((MovieDetailState) state);
        }

        if (state instanceof SimilarMovieListState) {
            updateSimilarMoviesListView((SimilarMovieListState) state);
        }
    }

    private void updateSimilarMoviesListView(SimilarMovieListState state) {

        if (view == null) {
            return;
        }

        List<MovieModel> movies = state.getMovies();
        view.showSimilarMovies(movies);
    }

    private void updateMovieDetailView(@NonNull MovieDetailState state) {

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
