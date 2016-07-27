package uk.co.ashtonbrsc.intentexplode;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.support.v4.app.ShareCompat;

import uk.co.ashtonbrsc.android.intentintercept.R;

public class SettingsUtil {

    private SettingsUtil() {
    }

    public static void setupSettings(final Activity activity, PreferenceManager preferenceManager) {

        final Preference interceptEnabledPreference = preferenceManager
                .findPreference(activity.getString(R.string.pref_intercept_enabled));

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
                .findPreference(activity.getString(R.string.pref_send_test_intent));
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
