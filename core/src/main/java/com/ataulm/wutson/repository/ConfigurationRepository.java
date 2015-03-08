package com.ataulm.wutson.repository;

import com.ataulm.wutson.rx.Function;
import com.ataulm.wutson.tmdb.gson.GsonConfiguration;
import com.ataulm.wutson.tmdb.TmdbApi;

import rx.Observable;
import rx.schedulers.Schedulers;
import rx.subjects.BehaviorSubject;

public class ConfigurationRepository {

    private final TmdbApi api;
    private final BehaviorSubject<GsonConfiguration> subject;

    ConfigurationRepository(TmdbApi api) {
        this.api = api;
        this.subject = BehaviorSubject.create();
    }

    public Observable<GsonConfiguration> getConfiguration() {
        if (!subject.hasValue()) {
            refreshConfiguration();
        }
        return subject;
    }

    private void refreshConfiguration() {
        api.getConfiguration()
                .lift(Function.<GsonConfiguration>swallowOnCompleteEvents())
                .subscribeOn(Schedulers.io())
                .subscribe(subject);
    }

}
