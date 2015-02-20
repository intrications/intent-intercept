package uk.co.ashtonbrsc.intentexplode;

import android.os.Bundle;
import android.preference.PreferenceActivity;

import uk.co.ashtonbrsc.android.intentintercept.R;

public class Pre15SettingsActivity extends PreferenceActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        addPreferencesFromResource(R.xml.enable_disable_settings);
        addPreferencesFromResource(R.xml.settings);
        SettingsUtil.setupSettings(this, getPreferenceManager());
    }
}
