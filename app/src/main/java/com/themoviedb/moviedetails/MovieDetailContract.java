package com.themoviedb.moviedetails;

import com.themoviedb.base.BasePresenter;
import com.themoviedb.base.BaseState;
import com.themoviedb.base.BaseView;
import com.themoviedb.models.MovieDetailModel;

public interface MovieDetailContract {

    interface IMovieDetailView extends BaseView {

        void showMovieDetail(MovieDetailModel movieDetailModel);

        void showLoadingProgress();

        void hideLoadingProgress();

        void onError(Throwable e);
    }

    interface IMovieDetailPresenter extends BasePresenter<IMovieDetailView, MovieDetailState> {

        void fetchMovie(int id);
    }

    class MovieDetailState implements BaseState {

        private MovieDetailModel movieDetail;

        private int movieId;

        private boolean isLoading;

        private Throwable error;

        public MovieDetailState() {
        }

        public MovieDetailState(MovieDetailModel movieDetail, int movieId, boolean isLoading) {
            this.movieDetail = movieDetail;
            this.movieId = movieId;
            this.isLoading = isLoading;
        }

        public MovieDetailModel getMovieDetail() {
            return movieDetail;
        }

        public void setMovieDetail(MovieDetailModel movieDetail) {
            this.movieDetail = movieDetail;
        }

        public int getMovieId() {
            return movieId;
        }

        public void setMovieId(int movieId) {
            this.movieId = movieId;
        }

        public boolean isLoading() {
            return isLoading;
        }

        public void setLoading(boolean loading) {
            isLoading = loading;
        }

        public Throwable getError() {
            return error;
        }

        public void setError(Throwable error) {
            this.error = error;
        }
    }
}