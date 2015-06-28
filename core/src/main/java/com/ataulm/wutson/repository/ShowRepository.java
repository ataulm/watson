package com.ataulm.wutson.repository;

import com.ataulm.wutson.repository.persistence.LocalDataRepository;
import com.ataulm.wutson.tmdb.Configuration;
import com.ataulm.wutson.model.*;
import com.ataulm.wutson.tmdb.TmdbApi;
import com.ataulm.wutson.tmdb.gson.GsonCredits;
import com.ataulm.wutson.tmdb.gson.GsonTvShow;
import com.google.gson.Gson;

import java.lang.*;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func2;

import static com.ataulm.wutson.rx.Function.ignoreEmptyStrings;
import static com.ataulm.wutson.rx.Function.jsonTo;

public class ShowRepository {

    private final TmdbApi api;
    private final LocalDataRepository localDataRepository;
    private final ConfigurationRepository configurationRepository;
    private final Gson gson;

    public ShowRepository(TmdbApi api, LocalDataRepository localDataRepository, ConfigurationRepository configurationRepository, Gson gson) {
        this.api = api;
        this.localDataRepository = localDataRepository;
        this.configurationRepository = configurationRepository;
        this.gson = gson;
    }

    public Observable<Show> getShowDetails(ShowId showId) {
        Observable<Configuration> configurationObservable = configurationRepository.getConfiguration();
        Observable<GsonTvShow> gsonTvShowObservable = fetchJsonTvShowFrom(localDataRepository, showId)
                .filter(ignoreEmptyStrings())
                .map(jsonTo(GsonTvShow.class, gson))
                .switchIfEmpty(api.getTvShow(showId.toString()).doOnNext(saveTo(localDataRepository, gson, showId)));

        return Observable.zip(configurationObservable, gsonTvShowObservable, asShow(showId));
    }

    private static Action1<GsonTvShow> saveTo(final LocalDataRepository localDataRepository, final Gson gson, final ShowId tmdbShowId) {
        return new Action1<GsonTvShow>() {

            @Override
            public void call(GsonTvShow gsonTvShow) {
                String json = gson.toJson(gsonTvShow, GsonTvShow.class);
                localDataRepository.writeJsonShowDetails(tmdbShowId, json);
            }

        };
    }

    private static Observable<String> fetchJsonTvShowFrom(final LocalDataRepository repository, final ShowId showId) {
        return Observable.create(new Observable.OnSubscribe<String>() {

            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(repository.readJsonShowDetails(showId));
                subscriber.onCompleted();
            }

        });
    }

    private static Func2<Configuration, GsonTvShow, Show> asShow(final ShowId showId) {
        return new Func2<Configuration, GsonTvShow, Show>() {

            @Override
            public Show call(Configuration configuration, GsonTvShow gsonTvShow) {
                List<com.ataulm.wutson.model.Character> characters = getCharacters(configuration, gsonTvShow);

                String name = gsonTvShow.name;
                String overview = gsonTvShow.overview;
                URI posterUri = configuration.completePoster(gsonTvShow.posterPath);
                URI backdropUri = configuration.completeBackdrop(gsonTvShow.backdropPath);
                Cast cast = new Cast(characters);

                List<Show.SeasonSummary> seasonSummaries = getSeasons(configuration, gsonTvShow);
                ShowId id = new ShowId(gsonTvShow.id);
                return new Show(id, name, overview, posterUri, backdropUri, cast, seasonSummaries);
            }

            private List<com.ataulm.wutson.model.Character> getCharacters(Configuration configuration, GsonTvShow gsonTvShow) {
                List<com.ataulm.wutson.model.Character> characters = new ArrayList<>();
                for (GsonCredits.Cast.Entry entry : gsonTvShow.gsonCredits.cast) {
                    Actor actor = new Actor(entry.actorName, configuration.completeProfile(entry.profilePath));
                    characters.add(new com.ataulm.wutson.model.Character(entry.name, actor));
                }
                return characters;
            }

            private List<Show.SeasonSummary> getSeasons(Configuration configuration, GsonTvShow gsonTvShow) {
                List<Show.SeasonSummary> seasonSummaries = new ArrayList<>();
                for (GsonTvShow.Season season : gsonTvShow.seasons) {
                    String id = season.id;
                    String showName = gsonTvShow.name;
                    int seasonNumber = season.seasonNumber;
                    int episodeCount = season.episodeCount;
                    URI posterPath = configuration.completePoster(season.posterPath);
                    seasonSummaries.add(new Show.SeasonSummary(id, showId, showName, seasonNumber, episodeCount, posterPath));
                }
                return seasonSummaries;
            }

        };
    }

}
