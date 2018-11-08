package com.themoviedb.components;

import com.themoviedb.home.view.HomeActivity;
import com.themoviedb.modules.PresenterModule;
import com.themoviedb.moviedetails.view.MovieDetailActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { PresenterModule.class })
public interface PresenterComponent {

    void inject(HomeActivity homeActivity);

    void inject(MovieDetailActivity movieDetailActivity);

}
