package com.themoviedb.home.presenter;

import android.util.Log;

import com.themoviedb.BaseApplication;
import com.themoviedb.base.BaseState;
import com.themoviedb.home.HomeContract;
import com.themoviedb.home.HomeContract.HomeFeedsViewState;
import com.themoviedb.home.HomeContract.IHomeView;
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
    private IHomeView view;

    private HomeFeedsViewState feedViewState = new HomeFeedsViewState();

    public HomePresenter() {
        BaseApplication.getInstance().getDataComponent().inject(this);
    }

    @Override
    public void attachView(IHomeView homeView) {
        this.view = homeView;
        updateView(feedViewState);

        if (feedViewState.getMovies().isEmpty() && !feedViewState.isLoading()) {
            fetchFirstPage();
        }
    }

    @Override
    public void fetchFirstPage() {
        resetFilters();
        fetchMovies(feedViewState.getMinYear(), feedViewState.getMaxYear(), feedViewState.getPage());
    }

    @Override
    public void fetchNextPage() {
        int nextPage = feedViewState.getPage() + 1;
        if (nextPage <= feedViewState.getTotalPages()) {
            fetchMovies(feedViewState.getMinYear(), feedViewState.getMaxYear(), nextPage);
        }
    }

    @Override
    public void filterMovieList(int startYear, int endYear) {

        if (feedViewState.getMinYear() == startYear && feedViewState.getMaxYear() == endYear) {
            return;
        }

        fetchMovies(startYear, endYear, feedViewState.getPage());
    }

    private void resetFilters() {
        feedViewState = new HomeFeedsViewState();
        fetchMovies(feedViewState.getMinYear(), feedViewState.getMaxYear(), feedViewState.getPage());
    }

    private void fetchMovies(final int minYear, final int maxYear, final int nextPage) {

        Log.d("HomePresenter",
              "Fetching from API : minYear : " + minYear + ", maxYear : " + maxYear + ", page : " + nextPage);

        if (feedViewState.isLoading()) {
            return;
        }

        Observable<DiscoverModel> observable = repository.discover(minYear, maxYear, nextPage);

        observable.subscribe(new Observer<DiscoverModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                feedViewState.setLoading(true);
                updateView(feedViewState);
            }

            @Override
            public void onNext(@NonNull DiscoverModel discoverModel) {

                if (discoverModel == null) {
                    return;
                }
                int page = discoverModel.getPage();
                int totalPages = discoverModel.getTotalPages();

                int currentMinYear = feedViewState.getMinYear();
                int currentMaxYear = feedViewState.getMaxYear();

                List<MovieModel> existingMovies = feedViewState.getMovies();
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

                feedViewState = new HomeFeedsViewState(page, minYear, maxYear, totalPages, existingMovies, false);
                updateView(feedViewState);
            }

            @Override
            public void onError(@NonNull Throwable e) {
                feedViewState.setLoading(false);
                feedViewState.setError(e);
                updateView(feedViewState);
            }

            @Override
            public void onComplete() {
                feedViewState.setLoading(false);
                updateView(feedViewState);
            }
        });
    }

    public boolean isLoading() {
        return feedViewState.isLoading();
    }

    @Override
    public void detachView() {
        view = null;
    }

    @Override
    public void updateView(BaseState state) {

        if (view == null) {
            return;
        }

        if (state instanceof HomeFeedsViewState) {
            updateHomeView((HomeFeedsViewState) state);
        }
    }

    private void updateHomeView(HomeFeedsViewState state) {

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
