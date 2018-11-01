package com.themoviedb.base;

public interface BasePresenter<V extends BaseView> {

    void attachView(V view);

    void detachView();

    void updateView(BaseState state);
}
