package com.themoviedb.home;

import com.themoviedb.apis.request.DiscoveryRequest;
import com.themoviedb.base.BasePresenter;
import com.themoviedb.base.BaseState;
import com.themoviedb.base.BaseView;
import com.themoviedb.models.MovieModel;

import java.util.ArrayList;
import java.util.List;

public interface HomeContract {

    interface IHomeView extends BaseView {

        void showMovies(List<MovieModel> movies, int minYear, int maxYear);

        void showLoadingProgress();

        void hideLoadingProgress();

        void onError(Throwable e);
    }

    interface IHomePresenter extends BasePresenter<IHomeView> {

        void fetchFirstPage();

        void fetchNextPage();

        void filterMovieList(int startYear, int endYear);
    }

    class HomeFeedsViewState implements BaseState {

        private int page = 1;

        private int minYear = DiscoveryRequest.THIS_YEAR;

        private int maxYear = DiscoveryRequest.MIN_YEAR;

        private int totalPages = 1;

        private List<MovieModel> movies = new ArrayList<>();

        private Throwable error;

        private boolean loading;

        public HomeFeedsViewState() {

        }

        public HomeFeedsViewState(int page,
                                  int minYear,
                                  int maxYear,
                                  int totalPages,
                                  List<MovieModel> movies, boolean loading) {
            this.page = page;
            this.minYear = minYear;
            this.maxYear = maxYear;
            this.totalPages = totalPages;
            this.movies = movies;
            this.loading = loading;
        }

        public int getPage() {
            return page;
        }

        public int getMinYear() {
            return minYear;
        }

        public int getMaxYear() {
            return maxYear;
        }

        public int getTotalPages() {
            return totalPages;
        }

        public List<MovieModel> getMovies() {
            return movies;
        }

        public boolean isLoading() {
            return loading;
        }

        public void setPage(int page) {
            this.page = page;
        }

        public void setMinYear(int minYear) {
            this.minYear = minYear;
        }

        public void setMaxYear(int maxYear) {
            this.maxYear = maxYear;
        }

        public void setTotalPages(int totalPages) {
            this.totalPages = totalPages;
        }

        public void setMovies(List<MovieModel> movies) {
            this.movies = movies;
        }

        public void setLoading(boolean loading) {
            this.loading = loading;
        }

        public Throwable getError() {
            return error;
        }

        public void setError(Throwable error) {
            this.error = error;
        }
    }

}
