package com.themoviedb.modules;

import com.themoviedb.home.HomeContract;
import com.themoviedb.home.presenter.HomePresenter;
import com.themoviedb.moviedetails.MovieDetailContract;
import com.themoviedb.moviedetails.presenter.MovieDetailPresenter;
import com.themoviedb.repositories.IMovieRepository;
import com.themoviedb.repositories.MovieRepository;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

/**
 * Created by vaibhav on 3/10/17.
 */

@Module
public class DataModule {
    
    @Provides
    @Singleton
    public IMovieRepository providesMoviesRepository() {
        return new MovieRepository();
    }

}
