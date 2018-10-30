package com.themoviedb.moviedetails;

import com.themoviedb.base.BasePresenter;
import com.themoviedb.base.BaseView;
import com.themoviedb.home.HomeContract;
import com.themoviedb.models.MovieDetailModel;

public interface MovieDetailsContract {

    interface IMovieDetailView extends BaseView {

        void showMovieDetail(MovieDetailModel movieDetailModel);

        void showLoadingProgress();

        void hideLoadingProgress();

        void onError(Throwable e);
    }

    interface IMovieDetailPresenter extends BasePresenter<MovieDetailsContract.IMovieDetailView> {
        void fetchMovie(int id);
    }
}
