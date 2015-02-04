package com.ataulm.wutson.navigation;

import android.content.Intent;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewGroup;

import com.ataulm.wutson.R;
import com.ataulm.wutson.browseshows.BrowseShowsActivity;
import com.ataulm.wutson.settings.SettingsActivity;

public abstract class WutsonTopLevelActivity extends WutsonActivity {

    private DrawerLayout drawerLayout;

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(R.layout.activity_top_level);
        ViewGroup container = (ViewGroup) findViewById(R.id.top_level_container_activity_layout);
        container.removeAllViews();
        getLayoutInflater().inflate(layoutResID, container);

        populateNavigationDrawer();
    }

    private void populateNavigationDrawer() {
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        final NavigationDrawerView navigationDrawerView = (NavigationDrawerView) drawerLayout.findViewById(R.id.drawer_list);
        navigationDrawerView.setupWithListener(new NavigationDrawerView.OnNavigationClickListener() {

            @Override
            public void onNavigationClick(TopLevelNavigationItem item) {
                switch (item) {
                    case DISCOVER_SHOWS:
                        startActivity(new Intent(WutsonTopLevelActivity.this, BrowseShowsActivity.class));
                        break;
                    case SETTINGS:
                        startActivity(new Intent(WutsonTopLevelActivity.this, SettingsActivity.class));
                        break;
                    default:
                        onNotImplementedActionFor(item);
                }
            }

            private void onNotImplementedActionFor(TopLevelNavigationItem item) {
                String title = item.getTitle();
                getToaster().display(title);
                drawerLayout.closeDrawer(navigationDrawerView);
            }

        });
    }

}
