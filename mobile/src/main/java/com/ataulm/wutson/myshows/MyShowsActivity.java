package com.ataulm.wutson.myshows;

import android.annotation.TargetApi;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.ViewPager;

import com.ataulm.wutson.Jabber;
import com.ataulm.wutson.R;
import com.ataulm.wutson.discover.OnShowClickListener;
import com.ataulm.wutson.model.EpisodesByDate;
import com.ataulm.wutson.model.ShowSummaries;
import com.ataulm.wutson.model.ShowSummary;
import com.ataulm.wutson.navigation.NavigationDrawerItem;
import com.ataulm.wutson.navigation.WutsonTopLevelActivity;
import com.ataulm.wutson.rx.LoggingObserver;
import com.novoda.landingstrip.LandingStrip;

import java.util.Set;

import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;
import rx.subscriptions.CompositeSubscription;

public class MyShowsActivity extends WutsonTopLevelActivity implements OnShowClickListener {

    private CompositeSubscription subscriptions;
    private MyShowsPagerAdapter pagerAdapter;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideTitleWhileWeCheckForTrackedShows();
        setContentView(R.layout.activity_my_shows);

        ViewPager viewPager = (ViewPager) findViewById(R.id.my_shows_pager);
        pagerAdapter = MyShowsPagerAdapter.newInstance(this, this, Jabber.toastDisplayer());
        viewPager.setAdapter(pagerAdapter);

        LandingStrip tabStrip = (LandingStrip) findViewById(R.id.tab_strip);
        tabStrip.attach(viewPager);

        subscriptions = new CompositeSubscription(
                Jabber.dataRepository().getMyShows()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new TrackedShowsObserver()),
                Jabber.dataRepository().getUpcomingEpisodes()
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribeOn(Schedulers.io())
                        .subscribe(new UpcomingEpisodesObserver())
        );
    }

    private void hideTitleWhileWeCheckForTrackedShows() {
        setTitle(null);
    }

    @Override
    protected void onDestroy() {
        subscriptions.clear();
        super.onDestroy();
    }

    @Override
    protected NavigationDrawerItem getNavigationDrawerItem() {
        return NavigationDrawerItem.MY_SHOWS;
    }

    @Override
    public void onClick(ShowSummary showSummary) {
        navigate().toShowDetails(showSummary.getId(), showSummary.getName(), showSummary.getBackdropUri().toString());
    }

    private class TrackedShowsObserver extends LoggingObserver<ShowSummaries> {

        private boolean firstLoad = true;

        @Override
        public void onNext(ShowSummaries showSummaries) {
            super.onNext(showSummaries);
            if (nothingToSeeHere(showSummaries)) {
                navigate().toDiscover();
            } else {
                // TODO this is broken - open with no shows -> discover, backs into this activity (should close app)
                firstLoad = false;
                setTitle(R.string.my_shows_label);
                pagerAdapter.update(showSummaries);
            }
        }

        private boolean nothingToSeeHere(ShowSummaries showSummaries) {
            return showSummaries.size() == 0 && activityWasOpenedFromLauncher() && firstLoad;
        }

        private boolean activityWasOpenedFromLauncher() {
            Set<String> categories = getIntent().getCategories();
            return categories != null && categoryLauncherIsIn(categories);
        }

        private boolean categoryLauncherIsIn(Set<String> categories) {
            return categories.contains(Intent.CATEGORY_LAUNCHER) || categoryLeanbackLauncherIsIn(categories);
        }

        @TargetApi(Build.VERSION_CODES.LOLLIPOP)
        private boolean categoryLeanbackLauncherIsIn(Set<String> categories) {
            return Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP && categories.contains(Intent.CATEGORY_LEANBACK_LAUNCHER);
        }

    }

    private class UpcomingEpisodesObserver extends LoggingObserver<EpisodesByDate> {

        @Override
        public void onNext(EpisodesByDate episodesByDate) {
            super.onNext(episodesByDate);
            pagerAdapter.update(episodesByDate);
        }

    }

}
