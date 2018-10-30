package com.themoviedb.modules;

import com.themoviedb.home.HomeContract;
import com.themoviedb.home.presenter.HomePresenter;
import com.themoviedb.moviedetails.MovieDetailsContract;
import com.themoviedb.moviedetails.presenter.MovieDetailPresenter;
import com.themoviedb.repositories.MovieRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vaibhav on 3/10/17.
 */

@Module
public class ApplicationModule {

    @Provides
    @Singleton
    public HomeContract.IHomePresenter providesHomePresenter() {
        return new HomePresenter();
    }

    @Provides
    @Singleton
    public MovieDetailsContract.IMovieDetailPresenter providesMovieDetailPresenter() {
        return new MovieDetailPresenter();
    }

    @Provides
    @Singleton
    public MovieRepository providesMoviesRepository() {
        return new MovieRepository();
    }

}
