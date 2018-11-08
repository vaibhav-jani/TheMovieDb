package com.themoviedb;

import android.app.Application;

import com.themoviedb.components.DaggerDataComponent;
import com.themoviedb.components.DaggerPresenterComponent;
import com.themoviedb.components.DataComponent;
import com.themoviedb.components.PresenterComponent;
import com.themoviedb.modules.DataModule;
import com.themoviedb.modules.PresenterModule;

/**
 * Created by vaibhav on 3/10/17.
 */

public class BaseApplication extends Application {

    private static BaseApplication instance;

    private DataComponent dataComponent;
    private PresenterComponent presenterComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        dataComponent = DaggerDataComponent
                .builder()
                .dataModule(new DataModule())
                .build();

        presenterComponent = DaggerPresenterComponent
                .builder()
                .presenterModule(new PresenterModule())
                .build();
    }

    public static BaseApplication getInstance() {
        return instance;
    }

    public DataComponent getDataComponent() {
        return dataComponent;
    }

    public PresenterComponent getPresenterComponent() {
        return presenterComponent;
    }
}
