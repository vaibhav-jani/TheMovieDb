package com.themoviedb.home.view;

import android.app.Activity;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.ListPreloader;
import com.bumptech.glide.RequestBuilder;
import com.themoviedb.R;
import com.themoviedb.glide.GlideApp;
import com.themoviedb.glide.GlideRequest;
import com.themoviedb.glide.GlideRequests;
import com.themoviedb.models.MovieModel;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by vaibhav on 5/10/17.
 */

public class MovieListAdapter extends RecyclerView.Adapter<MovieListAdapter.MovieViewHolder>
        implements ListPreloader.PreloadSizeProvider<MovieModel>,
        ListPreloader.PreloadModelProvider<MovieModel> {

    private List<MovieModel> movies = new ArrayList<>();

    private final LayoutInflater inflater;

    private Activity activity;

    private OnMovieSelectionListener movieSelectionListener;

    private GlideRequest<Drawable> requestBuilder;

    private int[] actualDimensions;

    private int rawResourceId;

    public MovieListAdapter(Activity activity, int rawResourceId) {

        this.activity = activity;
        inflater = LayoutInflater.from(activity);
        this.rawResourceId = rawResourceId;

        GlideRequests requestManager = GlideApp.with(activity);
        requestBuilder = requestManager.asDrawable().fitCenter();

        setHasStableIds(true);
    }

    @NonNull
    @Override
    public MovieViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        final View view = inflater.inflate(rawResourceId, parent, false);

        if (actualDimensions == null) {
            view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    if (actualDimensions == null) {
                        actualDimensions = new int[]{view.getWidth(), view.getHeight()};
                    }
                    view.getViewTreeObserver().removeOnPreDrawListener(this);
                    return true;
                }
            });
        }

        return new MovieViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final MovieViewHolder holder, final int position) {

        final MovieModel model = movies.get(position);

        String name = model.getTitle();
        holder.tvName.setText(name);

        String thumbnail = model.getPosterPath();
        requestBuilder.clone()
                .load(thumbnail)
                .placeholder(R.drawable.thumb_place_holder)
                .error(R.drawable.thumb_place_holder)
                .into(holder.ivThumbnail);

        GlideApp.with(activity).load(model.getLargePosterPath()).preload();

        String releasedOn = model.getReleaseDate();
        if (releasedOn != null && releasedOn.contains("-")) {
            holder.tvYear.setText("[ " + releasedOn.split("-")[0] + " ]");
        } else {
            holder.tvYear.setText(R.string.unknown);
        }

        holder.view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(movieSelectionListener != null) {
                    movieSelectionListener.onMovieSelected(model, holder.ivThumbnail, position);
                }
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return movies.size();
    }

    public OnMovieSelectionListener getMovieSelectionListener() {
        return movieSelectionListener;
    }

    public void setMovieSelectionListener(OnMovieSelectionListener movieSelectionListener) {
        this.movieSelectionListener = movieSelectionListener;
    }

    public List<MovieModel> getMovies() {
        return movies;
    }

    public void setMovies(List<MovieModel> movies) {
        if (movies == null) {
            return;
        }
        this.movies = movies;
        notifyDataSetChanged();
    }

    public void notifyMoviesListChanged() {
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public List<MovieModel> getPreloadItems(int position) {
        return Collections.singletonList(movies.get(position));
    }

    @Nullable
    @Override
    public RequestBuilder getPreloadRequestBuilder(MovieModel item) {

        return requestBuilder
                .clone()
                .load(item.getPosterPath());
    }

    @Nullable
    @Override
    public int[] getPreloadSize(MovieModel item, int adapterPosition, int perItemPosition) {
        return actualDimensions;
    }

    public static class MovieViewHolder extends RecyclerView.ViewHolder {

        public View view;
        public TextView tvName;
        public TextView tvYear;
        public ImageView ivThumbnail;

        private MovieViewHolder(View view) {
            super(view);
            this.view = view;
            this.ivThumbnail = view.findViewById(R.id.ivThumbnail);
            this.tvName = view.findViewById(R.id.tvName);
            this.tvYear = view.findViewById(R.id.tvYear);
        }
    }

    public interface OnMovieSelectionListener {

        void onMovieSelected(MovieModel model, View view, int position);
    }
}
