package com.themoviedb.moviedetails.view;

import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.themoviedb.BaseApplication;
import com.themoviedb.R;
import com.themoviedb.base.BaseActivity;
import com.themoviedb.glide.GlideApp;
import com.themoviedb.models.GenreModel;
import com.themoviedb.models.MovieDetailModel;
import com.themoviedb.models.MovieModel;
import com.themoviedb.models.ProductionCountryModel;
import com.themoviedb.models.SpokenLanguageModel;
import com.themoviedb.moviedetails.MovieDetailContract;

import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by vaibhav on 6/10/17.
 */

public class MovieDetailActivity extends BaseActivity implements MovieDetailContract.IMovieDetailView {

    public static final String EXTRA_MOVIE_ID = "extra_movie_id";
    public static final String EXTRA_MOVIE_NAME = "extra_movie_name";

    @Inject
    MovieDetailContract.IMovieDetailPresenter presenter;

    private ImageView ivThumbnail;
    private View loadingProgressView;
    private View detailsView;
    private TextView tvGenre;
    private TextView tvLanguages;
    private TextView tvCountries;
    private TextView tvOverView;
    private TextView tvReleaseDate;
    private TextView tvRatings;
    private TextView tvVotes;
    private TextView tvWebSite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        supportPostponeEnterTransition();

        BaseApplication.getInstance().getApplicationComponent().inject(this);

        setContentView(R.layout.activity_movie_detail);

        initViews();
        setNavigationAndToolBar();

        int movieId = getIntent().getIntExtra(EXTRA_MOVIE_ID, 0);
        presenter.attachView(this);
        presenter.fetchMovie(movieId);
    }

    private void initViews() {
        ivThumbnail = findViewById(R.id.ivThumbnail);
        loadingProgressView = findViewById(R.id.loadingProgressView);
        detailsView = findViewById(R.id.detailsView);
        tvGenre = findViewById(R.id.tvGenre);
        tvLanguages = findViewById(R.id.tvLanguages);
        tvCountries = findViewById(R.id.tvCountries);
        tvOverView = findViewById(R.id.tvOverView);
        tvReleaseDate = findViewById(R.id.tvReleaseDate);
        tvRatings = findViewById(R.id.tvRatings);
        tvVotes = findViewById(R.id.tvVotes);
        tvWebSite = findViewById(R.id.tvWebSite);
    }

    private void setNavigationAndToolBar() {

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        CollapsingToolbarLayout collapsingToolbar = findViewById(R.id.collapsingToolbar);
        //collapsingToolbar.setContentScrimColor(ContextCompat.getColor(this, R.color.colorPrimary));
        collapsingToolbar.setTitleEnabled(true);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int width = displayMetrics.widthPixels;
        ViewGroup.LayoutParams layoutParams = collapsingToolbar.getLayoutParams();
        layoutParams.height = (int) (1.5 * width);
        collapsingToolbar.invalidate();
        collapsingToolbar.requestLayout();

        ActionBar supportActionBar = getSupportActionBar();
        if (supportActionBar != null) {
            String movieName = getIntent().getStringExtra(EXTRA_MOVIE_NAME);
            collapsingToolbar.setTitle(movieName);
            supportActionBar.setDisplayHomeAsUpEnabled(true);
            Drawable drawable = ContextCompat.getDrawable(this, R.drawable.back_arrow);
            drawable.setColorFilter(ContextCompat.getColor(this, R.color.white), PorterDuff.Mode.SRC_IN);
            supportActionBar.setHomeAsUpIndicator(drawable);
        }
    }

    @Override
    public void showMovieDetail(@NonNull MovieDetailModel model) {

        if (isFinishing()) {
            exit();
            Toast.makeText(this, R.string.no_movie_detail, Toast.LENGTH_LONG).show();
            return;
        }

        hideLoadingProgress();

        detailsView.setVisibility(View.VISIBLE);

        MovieModel movie = model.getMovie();

        if (movie != null) {
            String thumbnail = movie.getLargePosterPath();

            /*GlideApp.with(this).load(thumbnail)
                    .into(ivThumbnail);*/

            GlideApp.with(this)
                    .load(thumbnail)
                    .centerCrop()
                    .error(R.drawable.thumb_place_holder)
                    .placeholder(R.drawable.thumb_place_holder)
                    .dontAnimate()
                    .listener(new RequestListener<Drawable>() {
                        @Override
                        public boolean onLoadFailed(@Nullable GlideException e,
                                                    Object model,
                                                    Target<Drawable> target,
                                                    boolean isFirstResource) {
                            supportStartPostponedEnterTransition();
                            //AppBarLayout appBarLayout = findViewById(R.id.appbar);
                            //appBarLayout.setExpanded(false);
                            return false;
                        }

                        @Override
                        public boolean onResourceReady(Drawable resource,
                                                       Object model,
                                                       Target<Drawable> target,
                                                       DataSource dataSource,
                                                       boolean isFirstResource) {
                            supportStartPostponedEnterTransition();
                            return false;
                        }
                    })
                    .into(ivThumbnail);
        }

        String separator = ", ";

        List<GenreModel> genres = model.getGenres();
        if (genres != null) {
            StringBuilder builder = new StringBuilder();
            int size = genres.size();
            for (int i = 0; i < size; i++) {
                GenreModel genreModel = genres.get(i);
                String name = genreModel.getName();
                if (name != null && name.trim().length() > 0) {
                    builder.append(name);
                    if (i < size - 1) {
                        builder.append(separator);
                    }
                }
            }
            if (builder.length() > 0) {
                tvGenre.setText(builder.toString());
            }
        }

        List<SpokenLanguageModel> languages = model.getSpokenLanguages();
        if (languages != null) {
            StringBuilder builder = new StringBuilder();
            int size = languages.size();
            for (int i = 0; i < size; i++) {
                SpokenLanguageModel languageModel = languages.get(i);
                String name = languageModel.getName();
                if (name != null && name.trim().length() > 0) {
                    builder.append(name);
                    if (i < size - 1) {
                        builder.append(separator);
                    }
                }
            }
            if (builder.length() > 0) {
                tvLanguages.setText(builder.toString());
            }
        }

        List<ProductionCountryModel> countries = model.getProductionCountries();
        StringBuilder builder;
        if (countries != null) {
            builder = new StringBuilder();
            int size = countries.size();
            for (int i = 0; i < size; i++) {
                ProductionCountryModel countryModel = countries.get(i);
                String name = countryModel.getName();
                if (name != null && name.trim().length() > 0) {
                    builder.append(name);
                    if (i < size - 1) {
                        builder.append(separator);
                    }
                }
            }
            if (builder.length() > 0) {
                tvCountries.setText(builder.toString());
            }
        }

        if (movie != null) {

            String overview = movie.getOverview();
            if (overview != null && overview.trim().length() > 0) {
                tvOverView.setText(overview);
            }

            String releaseDate = movie.getReleaseDate();
            if (releaseDate != null && releaseDate.trim().length() > 0) {
                tvReleaseDate.setText(releaseDate);
            }

            tvRatings.setText(String.valueOf(movie.getVoteAverage()));
            tvVotes.setText(String.valueOf(movie.getVoteCount()));

            String homepage = model.getHomepage();
            if (homepage != null && homepage.trim().length() > 0) {
                tvWebSite.setText(homepage);
            }
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
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                exit();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void exit() {
        ActivityCompat.finishAfterTransition(this);
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
            Snackbar.make(detailsView,
                          getString(R.string.check_network_connection) + " : " + throwable.getMessage(),
                          Snackbar.LENGTH_LONG).show();
            return;
        }
        Snackbar.make(detailsView,
                      getString(R.string.something_went_wrong) + " : " + throwable.getMessage(),
                      Snackbar.LENGTH_LONG).show();
    }
}
