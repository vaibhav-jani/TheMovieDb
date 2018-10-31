package com.themoviedb.home.presenter;

import android.util.Log;

import com.themoviedb.BaseApplication;
import com.themoviedb.home.HomeContract;
import com.themoviedb.models.DiscoverModel;
import com.themoviedb.models.MovieModel;
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

public class HomePresenter implements HomeContract.IHomePresenter {

    @Inject
    IMovieRepository repository;

    @Nullable
    private HomeContract.IHomeView view;

    private HomeContract.HomeViewState state = new HomeContract.HomeViewState();

    public HomePresenter() {
        BaseApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void attachView(HomeContract.IHomeView homeView) {
        this.view = homeView;
        updateView(state);

        if (state.getMovies().isEmpty() && !state.isLoading()) {
            fetchFirstPage();
        }
    }

    @Override
    public void fetchFirstPage() {
        resetFilters();
        fetchMovies(state.getMinYear(), state.getMaxYear(), state.getPage());
    }

    @Override
    public void fetchNextPage() {
        int nextPage = state.getPage() + 1;
        if (nextPage <= state.getTotalPages()) {
            fetchMovies(state.getMinYear(), state.getMaxYear(), nextPage);
        }
    }

    @Override
    public void filterMovieList(int startYear, int endYear) {

        if (state.getMinYear() == startYear && state.getMaxYear() == endYear) {
            return;
        }

        fetchMovies(startYear, endYear, state.getPage());
    }

    private void resetFilters() {
        state = new HomeContract.HomeViewState();
        fetchMovies(state.getMinYear(), state.getMaxYear(), state.getPage());
    }

    private void fetchMovies(final int minYear, final int maxYear, final int nextPage) {

        Log.d("HomePresenter",
              "Fetching from API : minYear : " + minYear + ", maxYear : " + maxYear + ", page : " + nextPage);

        if (state.isLoading()) {
            return;
        }

        Observable<DiscoverModel> observable = repository.discover(minYear, maxYear, nextPage);

        observable.subscribe(new Observer<DiscoverModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                state.setLoading(true);
                updateView(state);
            }

            @Override
            public void onNext(@NonNull DiscoverModel discoverModel) {

                if (discoverModel == null) {
                    return;
                }
                int page = discoverModel.getPage();
                int totalPages = discoverModel.getTotalPages();

                int currentMinYear = state.getMinYear();
                int currentMaxYear = state.getMaxYear();

                List<MovieModel> existingMovies = state.getMovies();
                List<MovieModel> newList = discoverModel.getMovies();
                if (newList != null) {
                    boolean isMovieListEmpty = existingMovies == null || existingMovies.size() == 0;
                    boolean isRangeChanged = currentMaxYear != maxYear || currentMinYear != minYear;
                    if (isMovieListEmpty || isRangeChanged) {
                        existingMovies = newList;
                    } else {
                        existingMovies.addAll(newList);
                    }
                }

                state = new HomeContract.HomeViewState(page, minYear, maxYear, totalPages, existingMovies, false);
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

    public boolean isLoading() {
        return state.isLoading();
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void updateView(HomeContract.HomeViewState state) {
        if (view == null) {
            return;
        }

        List<MovieModel> movies = state.getMovies();
        int minYear = state.getMinYear();
        int maxYear = state.getMaxYear();
        int page = state.getPage();

        if (movies != null && movies.size() > 0) {
            Log.d("HomePresenter",
                  "Showing cashed minYear : " + minYear + ", maxYear : " + maxYear + ", page : " + page);
            view.showMovies(movies, minYear, maxYear);
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
