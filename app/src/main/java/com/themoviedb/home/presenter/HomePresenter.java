package com.themoviedb.home.presenter;

import android.util.Log;

import com.themoviedb.BaseApplication;
import com.themoviedb.apis.request.DiscoveryRequest;
import com.themoviedb.home.HomeContract;
import com.themoviedb.models.DiscoverModel;
import com.themoviedb.models.MovieModel;
import com.themoviedb.repositories.MovieRepository;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.annotations.NonNull;
import io.reactivex.disposables.Disposable;

/**
 * Created by vaibhav on 3/10/17.
 */

public class HomePresenter implements HomeContract.IHomePresenter {

    @Inject
    MovieRepository repository;

    private int page = 1;

    private int minYear = DiscoveryRequest.THIS_YEAR;

    private int maxYear = DiscoveryRequest.MIN_YEAR;

    private int totalPages = 1;

    private HomeContract.IHomeView view;

    private List<MovieModel> movies = new ArrayList<>();

    private boolean loading;

    public HomePresenter() {
        BaseApplication.getInstance().getApplicationComponent().inject(this);
    }

    @Override
    public void attachView(HomeContract.IHomeView homeView) {
        this.view = homeView;
        if (movies != null && movies.size() > 0) {
            Log.d("HomePresenter", "Showing cashed minYear : " + minYear + ", maxYear : " + maxYear + ", page : " + page);
            view.showMovies(movies, minYear, maxYear);
            return;
        }
        fetchFirstPage();
    }

    @Override
    public void fetchFirstPage() {
        resetFilters();
        fetchMovies(minYear, maxYear, page);
    }

    @Override
    public void fetchNextPage() {
        int nextPage = ++page;
        if (nextPage <= totalPages) {
            fetchMovies(minYear, maxYear, nextPage);
        }
    }

    @Override
    public void filterMovieList(int startYear, int endYear) {

        if(this.minYear == startYear && this.maxYear == endYear) {
            return;
        }

        resetFilters();
        this.minYear = startYear;
        this.maxYear = endYear;
        fetchMovies(startYear, endYear, page);
    }

    private void resetFilters() {
        page = 1;

        page = 1;
        minYear = DiscoveryRequest.THIS_YEAR;
        maxYear = DiscoveryRequest.MIN_YEAR;

        movies.clear();
        view.notifyMoviesListChanged();
    }

    private void fetchMovies(final int minYear, final int maxYear, final int nextPage) {

        Log.d("HomePresenter", "Fetching from API : minYear : " + minYear + ", maxYear : " + maxYear + ", page : " + nextPage);

        Observable<DiscoverModel> observable = repository.discover(minYear, maxYear, nextPage);

        observable.subscribe(new Observer<DiscoverModel>() {
            @Override
            public void onSubscribe(@NonNull Disposable d) {
                loading = true;
                view.showLoadingProgress();
            }

            @Override
            public void onNext(@NonNull DiscoverModel discoverModel) {

                if (discoverModel == null) {
                    return;
                }
                page = discoverModel.getPage();
                totalPages = discoverModel.getTotalPages();

                List<MovieModel> newList = discoverModel.getMovies();
                if (newList != null) {
                    if (movies == null || movies.size() == 0) {
                        movies = newList;
                        view.showMovies(movies, minYear, maxYear);
                    } else {
                        movies.addAll(newList);
                        view.notifyMoviesListChanged();
                    }
                }
            }

            @Override
            public void onError(@NonNull Throwable e) {

                view.onError(e);
            }

            @Override
            public void onComplete() {
                loading = false;
                view.hideLoadingProgress();
            }
        });
    }

    @Override
    public boolean isLoading() {
        return loading;
    }

    @Override
    public void detachView() {
        view = null;
        /*page = 1;
        minYear = 1;
        minYear = DiscoveryRequest.THIS_YEAR;
        maxYear = DiscoveryRequest.MIN_YEAR;
        totalPages = 1;
        loading = false;
        movies.clear();*/
    }
}
