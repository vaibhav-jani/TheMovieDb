package com.themoviedb.components;

import com.themoviedb.home.presenter.HomePresenter;
import com.themoviedb.modules.DataModule;
import com.themoviedb.moviedetails.presenter.MovieDetailPresenter;

import javax.inject.Singleton;

import dagger.Component;

@Singleton
@Component(modules = { DataModule.class })
public interface DataComponent {

    void inject(HomePresenter homePresenter);

    void inject(MovieDetailPresenter movieDetailPresenter);
}
