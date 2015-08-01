package com.ataulm.wutson.jabber;

import android.content.Context;

import com.ataulm.wutson.repository.DataRepository;
import com.ataulm.wutson.repository.ShowRepository;
import com.ataulm.wutson.repository.TrackedShowsRepository;
import com.ataulm.wutson.repository.WutsonDataRepository;
import com.ataulm.wutson.repository.persistence.JsonRepository;
import com.ataulm.wutson.repository.persistence.SqliteJsonRepository;
import com.ataulm.wutson.shows.discover.DiscoverShowsRepository;
import com.ataulm.wutson.shows.myshows.SearchRepository;
import com.ataulm.wutson.trakt.TraktApi;
import com.google.gson.Gson;

final class Repositories {

    private final Context context;
    private final TraktApi traktApi;

    private DiscoverShowsRepository discoverShows;
    private SearchRepository searchRepository;
    private JsonRepository jsonRepository;
    private Gson gson;

    @Deprecated // TODO no more super repo! Aim to get rid of this in favour of a more specific repos - MyShowsRepository, TraktRepo?
    private WutsonDataRepository dataRepository;

    static Repositories newInstance(Context context, TraktApi traktApi) {
        return new Repositories(context.getApplicationContext(), traktApi);
    }

    private Repositories(Context context, TraktApi traktApi) {
        this.context = context;
        this.traktApi = traktApi;
    }

    public DiscoverShowsRepository discoverShows() {
        if (discoverShows == null) {
            JsonRepository jsonRepository = jsonRepository();
            Gson gson = gson();
            discoverShows = new DiscoverShowsRepository(traktApi, jsonRepository, gson);
        }
        return discoverShows;
    }

    public SearchRepository search() {
        if (searchRepository == null) {
            searchRepository = new SearchRepository();
        }
        return searchRepository;
    }

    private JsonRepository jsonRepository() {
        if (jsonRepository == null) {
            jsonRepository = new SqliteJsonRepository(context.getContentResolver());
        }
        return jsonRepository;
    }

    private Gson gson() {
        if (gson == null) {
            gson = new Gson();
        }
        return gson;
    }

    public DataRepository dataRepository() {
        if (dataRepository == null) {
            Gson gson = gson();
            JsonRepository jsonRepository = jsonRepository();

            TrackedShowsRepository trackedShowsRepo = new TrackedShowsRepository(gson);
            ShowRepository showRepo = new ShowRepository(traktApi, jsonRepository, gson);

            dataRepository = new WutsonDataRepository(trackedShowsRepo, showRepo);
        }
        return dataRepository;
    }

}
