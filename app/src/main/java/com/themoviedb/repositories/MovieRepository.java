package com.themoviedb.repositories;

import android.util.Log;

import com.themoviedb.apis.entity.responses.DiscoverResponseParser;
import com.themoviedb.apis.entity.responses.MovieDetailParser;
import com.themoviedb.apis.request.DiscoveryRequest;
import com.themoviedb.apis.retrofit.RestClient;
import com.themoviedb.models.DiscoverModel;
import com.themoviedb.models.MovieDetailModel;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.annotations.NonNull;
import io.reactivex.functions.Function;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Response;

/**
 * Created by vaibhav on 3/10/17.
 */

public class MovieRepository implements IMovieRepository {

    private static final String API_KEY = "1e457e1a40b0d8133c7ef44f74260961";

    @Override
    public Observable<MovieDetailModel> getMovieDetail(int id) {

        Observable<Response<MovieDetailParser>> observable = RestClient.get().getMovieDetail(id, API_KEY);

        observable = observable.subscribeOn(Schedulers.newThread());
        observable = observable.observeOn(AndroidSchedulers.mainThread());

        return observable.map(new Function<Response<MovieDetailParser>, MovieDetailModel>() {

            @Override
            public MovieDetailModel apply(@NonNull Response<MovieDetailParser> response) throws Exception {

                if (response.isSuccessful()) {
                    MovieDetailParser parser = response.body();
                    return new MovieDetailModel(parser);
                }

                return null;
            }
        });
    }

    @Override
    public Observable<DiscoverModel> getSimilarMovies(int movieId) {

        Observable<Response<DiscoverResponseParser>> discoverObservable =
                RestClient.get().getSimilarMovies(movieId, API_KEY);

        discoverObservable = discoverObservable.subscribeOn(Schedulers.newThread());
        discoverObservable = discoverObservable.observeOn(AndroidSchedulers.mainThread());

        return discoverObservable.map(new Function<Response<DiscoverResponseParser>, DiscoverModel>() {

            @Override
            public DiscoverModel apply(@NonNull Response<DiscoverResponseParser> response) throws Exception {

                if (response.isSuccessful()) {
                    DiscoverResponseParser parser = response.body();
                    return new DiscoverModel(parser);
                }

                return null;
            }
        });
    }

    @Override
    public Observable<DiscoverModel> discover(int page) {

        String sortBy = "release_date.desc";
        String releaseDateGte = "1900-01-01";
        String withOriginalLanguage = "en";

        Date date = new Date();
        String releaseDateLte = new SimpleDateFormat("yyyy-MM-dd", Locale.US).format(date);
        //String releaseDateLte = "2017-10-02";

        DiscoveryRequest request = new DiscoveryRequest();
        request.setPage(page);
        request.setReleaseDateGte(releaseDateGte);
        request.setReleaseDateLte(releaseDateLte);
        request.setSortBy(sortBy);
        request.setWithOriginalLanguage(withOriginalLanguage);

        return discover(request);
    }

    @Override
    public Observable<DiscoverModel> discover(int yearStart, int yearEnd, int page) {

        String sortBy = "release_date.asc";

        if (yearStart > yearEnd) {
            int temp = yearStart;
            yearStart = yearEnd;
            yearEnd = temp;
            sortBy = "release_date.desc";
        }

        String releaseDateLte = yearEnd + "-12-31";
        String releaseDateGte = yearStart + "-01-01";
        String withOriginalLanguage = "en";

        DiscoveryRequest request = new DiscoveryRequest();
        request.setPage(page);
        request.setReleaseDateGte(releaseDateGte);
        request.setReleaseDateLte(releaseDateLte);
        request.setSortBy(sortBy);
        request.setWithOriginalLanguage(withOriginalLanguage);

        return discover(request);
    }

    private Observable<DiscoverModel> discover(DiscoveryRequest discoveryRequest) {

        String sortBy = discoveryRequest.getSortBy();
        String releaseDateLte = discoveryRequest.getReleaseDateLte();
        String releaseDateGte = discoveryRequest.getReleaseDateGte();
        String withOriginalLanguage = discoveryRequest.getWithOriginalLanguage();
        int page = discoveryRequest.getPage();

        Log.d("DiscoveryRequest", "DiscoveryRequest" +
                "\npage : " + page +
                "\nreleaseDateLte : " + releaseDateLte +
                "\nreleaseDateGte : " + releaseDateGte +
                "\nwithOriginalLanguage : " + withOriginalLanguage +
                "\nsortBy : " + sortBy);

        Observable<Response<DiscoverResponseParser>> discoverObservable = RestClient.get()
                .discover(page, sortBy, releaseDateLte, releaseDateGte, withOriginalLanguage, API_KEY);

        discoverObservable = discoverObservable.subscribeOn(Schedulers.newThread());
        discoverObservable = discoverObservable.observeOn(AndroidSchedulers.mainThread());

        return discoverObservable.map(new Function<Response<DiscoverResponseParser>, DiscoverModel>() {

            @Override
            public DiscoverModel apply(@NonNull Response<DiscoverResponseParser> response) throws Exception {

                if (response.isSuccessful()) {
                    DiscoverResponseParser parser = response.body();
                    return new DiscoverModel(parser);
                }

                return null;
            }
        });
    }
}
