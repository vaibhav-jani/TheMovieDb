package com.themoviedb.components;

import com.themoviedb.home.presenter.HomePresenter;
import com.themoviedb.home.view.HomeActivity;
import com.themoviedb.modules.ApplicationModule;
import com.themoviedb.moviedetails.presenter.MovieDetailPresenter;
import com.themoviedb.moviedetails.view.MovieDetailActivity;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { ApplicationModule.class })
public interface ApplicationComponent {

    void inject(HomeActivity homeActivity);

    void inject(HomePresenter homePresenter);

    void inject(MovieDetailPresenter movieDetailPresenter);

    void inject(MovieDetailActivity movieDetailActivity);
}
