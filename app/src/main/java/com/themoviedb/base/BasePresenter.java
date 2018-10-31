package com.themoviedb.base;

public interface BasePresenter<V extends BaseView, S extends BaseState> {

    void attachView(V view);

    void detachView();

    void updateView(S state);
}
