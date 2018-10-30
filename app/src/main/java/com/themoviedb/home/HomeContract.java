package com.themoviedb.home;

import com.themoviedb.base.BasePresenter;
import com.themoviedb.base.BaseView;
import com.themoviedb.models.MovieModel;

import java.util.List;

public class HomeContract {

    public interface IHomeView extends BaseView {

        void showMovies(List<MovieModel> movies, int minYear, int maxYear);

        void notifyMoviesListChanged();

        void showLoadingProgress();

        void hideLoadingProgress();

        void onError(Throwable e);
    }

    public interface IHomePresenter extends BasePresenter<IHomeView> {

        void fetchFirstPage();

        void fetchNextPage();

        void filterMovieList(int startYear, int endYear);

        boolean isLoading();
    }
}
