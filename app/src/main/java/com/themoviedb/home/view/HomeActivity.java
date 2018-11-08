package com.themoviedb.home.view;

import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.NumberPicker;

import com.themoviedb.BaseApplication;
import com.themoviedb.R;
import com.themoviedb.about.AboutAppActivity;
import com.themoviedb.apis.request.DiscoveryRequest;
import com.themoviedb.base.BaseActivity;
import com.themoviedb.home.HomeContract;
import com.themoviedb.home.HomeContract.IHomePresenter;
import com.themoviedb.models.MovieModel;
import com.themoviedb.moviedetails.view.MovieDetailActivity;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

public class HomeActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener, HomeContract.IHomeView {

    @Inject
    IHomePresenter presenter;

    private DrawerLayout drawer;
    private View loadingProgressView;
    private View filterView;
    private View emptyView;
    private NumberPicker numberPicker1;
    private NumberPicker numberPicker2;
    private MovieListAdapter movieListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        BaseApplication.getInstance().getPresenterComponent().inject(this);

        setContentView(R.layout.activity_home);

        initViews();
        setNavigationAndToolBar();

        presenter.attachView(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void initViews() {
        drawer = findViewById(R.id.drawer_layout);
        RecyclerView rvMovies = findViewById(R.id.rvMovies);

        int spanCount = 2;
        if (getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE) {
            spanCount = 3;
        }
        final GridLayoutManager layoutManager = new GridLayoutManager(this, spanCount);
        rvMovies.setLayoutManager(layoutManager);

        movieListAdapter = new MovieListAdapter(this, R.layout.row_movie);
        movieListAdapter.setMovieSelectionListener(new MovieListAdapter.OnMovieSelectionListener() {
            @Override
            public void onMovieSelected(MovieModel model, View view, int position) {
                movieSelected(model, view, position);
            }
        });
        rvMovies.setAdapter(movieListAdapter);

        RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int pastVisibleItems = layoutManager.findFirstVisibleItemPosition();
                if (pastVisibleItems + visibleItemCount >= totalItemCount - 10) {
                    fetchNext();
                }
            }
        };
        rvMovies.addOnScrollListener(scrollListener);

        loadingProgressView = findViewById(R.id.loadingProgressView);

        filterView = findViewById(R.id.filterView);
        emptyView = findViewById(R.id.emptyView);

        numberPicker1 = findViewById(R.id.numberPicker1);
        numberPicker2 = findViewById(R.id.numberPicker2);

        numberPicker1.setMinValue(DiscoveryRequest.MIN_YEAR);
        numberPicker2.setMinValue(DiscoveryRequest.MIN_YEAR);
        numberPicker1.setMaxValue(DiscoveryRequest.MAX_YEAR);
        numberPicker2.setMaxValue(DiscoveryRequest.MAX_YEAR);

        Button btnFilterDone = findViewById(R.id.btnDone);
        btnFilterDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                filter();
            }
        });

        Button btnResetFilters = findViewById(R.id.btnReset);
        btnResetFilters.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetFilters();
            }
        });
    }

    private void resetFilters() {
        hideFilterMenu();
        presenter.fetchFirstPage();
    }

    private void fetchNext() {
        presenter.fetchNextPage();
    }

    private void filter() {
        hideFilterMenu();
        int startYear = numberPicker1.getValue();
        int endYear = numberPicker2.getValue();
        presenter.filterMovieList(startYear, endYear);
    }

    private void movieSelected(MovieModel model, View view, int position) {
        if (isFinishing()) {
            return;
        }
        Intent intent = new Intent(this, MovieDetailActivity.class);
        intent.putExtra(MovieDetailActivity.EXTRA_MOVIE_ID, model.getId());
        ActivityOptionsCompat options = ActivityOptionsCompat.
                makeSceneTransitionAnimation(this,
                                             view,
                                             ViewCompat.getTransitionName(view));
        startActivity(intent, options.toBundle());
        showLoadingProgress();
        //startActivity(intent);
    }

    @Override
    protected void onStop() {
        super.onStop();
        hideLoadingProgress();
    }

    private void setNavigationAndToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else if (filterView.getVisibility() == View.VISIBLE) {
            hideFilterMenu();
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_filter) {
            showFilterMenu();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.about_app) {
            showAboutApp();
        }

        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showFilterMenu() {
        filterView.setVisibility(View.VISIBLE);
    }

    private void hideFilterMenu() {
        filterView.setVisibility(View.GONE);
    }

    private void showAboutApp() {
        startActivity(new Intent(this, AboutAppActivity.class));
    }

    @Override
    public void showMovies(List<MovieModel> movies, int minYear, int maxYear) {
        if (isFinishing()) {
            return;
        }
        if (movies == null || movies.size() == 0) {
            showEmptyView();
            return;
        }

        hideEmptyView();
        movieListAdapter.setMovies(movies);

        numberPicker1.setValue(minYear);
        numberPicker2.setValue(maxYear);
    }

    private void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }

    private void showEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void showLoadingProgress() {
        if (isFinishing()) {
            return;
        }
        loadingProgressView.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideLoadingProgress() {
        if (isFinishing()) {
            return;
        }
        loadingProgressView.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
    }

    @Override
    public void onError(Throwable throwable) {
        throwable.printStackTrace();
        hideLoadingProgress();
        if (throwable instanceof UnknownHostException || throwable instanceof SocketTimeoutException) {
            Snackbar.make(drawer,
                          getString(R.string.check_network_connection) + " : " + throwable.getMessage(),
                          Snackbar.LENGTH_LONG).show();
            return;
        }
        Snackbar.make(drawer,
                      getString(R.string.something_went_wrong) + " : " + throwable.getMessage(),
                      Snackbar.LENGTH_LONG).show();
    }

}
