package com.ataulm.wutson.discover;

import com.ataulm.wutson.repository.ConfigurationRepository;
import com.ataulm.wutson.rx.Function;
import com.ataulm.wutson.tmdb.TmdbApi;
import com.ataulm.wutson.tmdb.gson.GsonConfiguration;
import com.ataulm.wutson.tmdb.gson.GsonDiscoverTv;
import com.ataulm.wutson.tmdb.gson.GsonGenres;

import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class ShowsInGenreRepository {

    private final TmdbApi api;
    private final GenresRepository genresRepository;
    private final ConfigurationRepository configurationRepository;
    private final BehaviorSubject<List<ShowsInGenre>> subject;

    public ShowsInGenreRepository(TmdbApi api, ConfigurationRepository configurationRepository) {
        this.api = api;
        this.configurationRepository = configurationRepository;

        this.genresRepository = new GenresRepository(api);
        this.subject = BehaviorSubject.create();
    }

    public Observable<List<ShowsInGenre>> getShowsSeparatedByGenre() {
        if (!subject.hasValue()) {
            refreshBrowseShows();
        }
        return subject;
    }

    private void refreshBrowseShows() {
        Observable<GsonGenres.Genre> genreObservable = genresRepository.getGenres().flatMap(Function.<GsonGenres.Genre>emitEachElement());
        Observable<GsonGenreAndGsonDiscoverTvShows> discoverTvShowsObservable = genreObservable.flatMap(fetchDiscoverTvShows());

        Observable<ShowsInGenre> showsInGenreObservable = Observable.combineLatest(configurationObservable(), discoverTvShowsObservable, new Func2<GsonConfiguration, GsonGenreAndGsonDiscoverTvShows, ShowsInGenre>() {

            @Override
            public ShowsInGenre call(GsonConfiguration configuration, GsonGenreAndGsonDiscoverTvShows discoverTvShows) {
                GsonGenres.Genre genre = discoverTvShows.genre;
                List<Show> shows = new ArrayList<>(discoverTvShows.size());
                for (GsonDiscoverTv.Shows.Show discoverTvShow : discoverTvShows.gsonDiscoverTv) {
                    String id = discoverTvShow.id;
                    String name = discoverTvShow.name;
                    URI posterUri = URI.create(configuration.getCompletePosterPath(discoverTvShow.posterPath));
                    URI backdropUri = URI.create(configuration.getCompleteBackdropPath(discoverTvShow.backdropPath));

                    shows.add(new Show(id, name, posterUri, backdropUri));
                }
                return new ShowsInGenre(genre, shows);
            }

        });

        showsInGenreObservable.toList()
                .lift(Function.<List<ShowsInGenre>>swallowOnCompleteEvents())
                .subscribeOn(Schedulers.io())
                .subscribe(subject);
    }

    private Func1<GsonGenres.Genre, Observable<GsonGenreAndGsonDiscoverTvShows>> fetchDiscoverTvShows() {
        return new Func1<GsonGenres.Genre, Observable<GsonGenreAndGsonDiscoverTvShows>>() {

            @Override
            public Observable<GsonGenreAndGsonDiscoverTvShows> call(final GsonGenres.Genre genre) {
                return api.getShowsMatchingGenre(genre.id).flatMap(new Func1<GsonDiscoverTv, Observable<GsonGenreAndGsonDiscoverTvShows>>() {

                    @Override
                    public Observable<GsonGenreAndGsonDiscoverTvShows> call(GsonDiscoverTv gsonDiscoverTv) {
                        return Observable.just(new GsonGenreAndGsonDiscoverTvShows(genre, gsonDiscoverTv));
                    }

                });
            }

        };
    }

    private Observable<GsonConfiguration> configurationObservable() {
        return configurationRepository.getConfiguration().first();
    }

    private static class GsonGenreAndGsonDiscoverTvShows {

        final GsonGenres.Genre genre;
        final GsonDiscoverTv gsonDiscoverTv;

        GsonGenreAndGsonDiscoverTvShows(GsonGenres.Genre genre, GsonDiscoverTv gsonDiscoverTv) {
            this.genre = genre;
            this.gsonDiscoverTv = gsonDiscoverTv;
        }

        int size() {
            return gsonDiscoverTv.shows.size();
        }

    }

}