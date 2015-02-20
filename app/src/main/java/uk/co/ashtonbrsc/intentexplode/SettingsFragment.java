//   Copyright 2012-2014 Intrications (intrications.com)
//
//   Licensed under the Apache License, Version 2.0 (the "License");
//   you may not use this file except in compliance with the License.
//   You may obtain a copy of the License at
//
//       http://www.apache.org/licenses/LICENSE-2.0
//
//   Unless required by applicable law or agreed to in writing, software
//   distributed under the License is distributed on an "AS IS" BASIS,
//   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
//   See the License for the specific language governing permissions and
//   limitations under the License.

package uk.co.ashtonbrsc.intentexplode;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.support.v4.app.ShareCompat;

import uk.co.ashtonbrsc.android.intentintercept.R;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class SettingsFragment extends PreferenceFragment {

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    public void onCreate(final Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.enable_disable_settings);
        addPreferencesFromResource(R.xml.settings);
        setupSettings(getActivity(), getPreferenceManager());
    }

    public static void setupSettings(final Activity activity, PreferenceManager preferenceManager) {

        final Preference interceptEnabledPreference = preferenceManager
                .findPreference(activity.getString(R.string.intercept_enable_pref));

        interceptEnabledPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                if (preference == interceptEnabledPreference) {

                    Boolean enabled = (Boolean) newValue;
                    int flag = (enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
                            : PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
                    ComponentName component = new ComponentName(activity, Explode.class);

                    activity.getPackageManager().setComponentEnabledSetting(component, flag,
                            PackageManager.DONT_KILL_APP);
                }
                return true;
            }
        });

        Preference sendTestIntentPreference = preferenceManager
                .findPreference(activity.getString(R.string.send_test_intent_pref));
        sendTestIntentPreference.setOnPreferenceClickListener(new Preference
                .OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = ShareCompat.IntentBuilder.from(activity).setChooserTitle
                        ("Select Intent Intercept").setType("plain/text")
                        .setText("Test Intent").createChooserIntent();
                activity.startActivity(intent);
                return true;
            }
        });
    }
}