package com.ataulm.wutson.settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;

import com.ataulm.wutson.BuildConfig;
import com.ataulm.wutson.R;
import com.ataulm.wutson.ToastDisplayer;
import com.ataulm.wutson.navigation.WutsonActivity;

public class SettingsActivity extends WutsonActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        applyColorFilterToAppBarIcons();
    }

    private void applyColorFilterToAppBarIcons() {
        Drawable navigationIcon = getToolbar().getNavigationIcon();
        if (navigationIcon != null) {
            navigationIcon.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
        }
    }

    public static class SettingsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            configureAboutCategory();
        }

        private void configureAboutCategory() {
            addPreferencesFromResource(R.xml.preference_about);
            PreferenceCategory category = (PreferenceCategory) findPreference(getString(R.string.settings_pref_category_key_about));

            Preference dataSourcesPreference = category.findPreference(getString(R.string.settings_pref_key_data_sources));
            dataSourcesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    DialogFragment dialogFragment = DataSourceDialogFragment.newInstance();
                    dialogFragment.show(getFragmentManager(), "notag");
                    return true;
                }

            });

            Preference softwareLicensesPreference = category.findPreference(getString(R.string.settings_pref_key_oss));
            softwareLicensesPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference preference) {
                    startActivity(new Intent(getActivity(), SoftwareLicensesActivity.class));
                    return true;
                }

            });

            Preference versionPreference = category.findPreference(getString(R.string.settings_pref_key_version));
            versionPreference.setSummary(BuildConfig.VERSION_NAME);
            versionPreference.setOnPreferenceClickListener(EasterEggPreferenceClickListener.newInstanceNoHints(getActivity(),
                    new EasterEggPreferenceClickListener.EasterEgg() {

                        private final ToastDisplayer toastDisplayer = new ToastDisplayer(getActivity());

                        @Override
                        public void onClickSpammed() {
                            String text = "Version code: " + BuildConfig.VERSION_CODE + "\n" + BuildConfig.BUILD_TIME;
                            toastDisplayer.display(text);
                        }

                    })
            );
        }
    }

    public static class DataSourceDialogFragment extends DialogFragment {

        public static DataSourceDialogFragment newInstance() {
            DataSourceDialogFragment frag = new DataSourceDialogFragment();
            Bundle args = new Bundle();
            frag.setArguments(args);
            return frag;
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new AlertDialog.Builder(getActivity())
                    .setTitle(R.string.data_sources)
                    .setMessage(R.string.trakt_attribution)
                    .create();
        }

    }

}
