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

import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ShareCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.SwitchPreferenceCompat;
import uk.co.ashtonbrsc.android.intentintercept.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        addPreferencesFromResource(R.xml.settings);
        final FragmentActivity activity = requireActivity();
        final SwitchPreferenceCompat interceptEnabledPreference = findPreference(activity.getString(R.string.pref_intercept_enabled));
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

        Preference sendTestIntentPreference = findPreference(activity.getString(R.string.pref_send_test_intent));
        sendTestIntentPreference.setOnPreferenceClickListener(new Preference
                .OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = ShareCompat.IntentBuilder.from(activity).setChooserTitle
                        (activity.getString(R.string.send_test_intent_chooser_title)).setType(activity
                        .getString(R.string.mime_type_text_plain))
                        .setText(activity.getString(R.string.send_test_intent_content)).createChooserIntent();
                activity.startActivity(intent);
                return true;
            }
        });
    }
}