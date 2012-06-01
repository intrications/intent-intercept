package uk.co.ashtonbrsc.intentexplode;

import uk.co.ashtonbrsc.android.intentintercept.R;
import android.content.ComponentName;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Settings extends SherlockPreferenceActivity implements
		OnPreferenceChangeListener {

	private static final CharSequence INTERCEPT_ENABLED = "interceptEnabled";
	private Preference interceptEnabledPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);

		interceptEnabledPreference = findPreference(INTERCEPT_ENABLED);

		interceptEnabledPreference.setOnPreferenceChangeListener(this);

	}

	public boolean onPreferenceChange(Preference preference, Object newValue) {
		if (preference == interceptEnabledPreference) {

			Boolean enabled = (Boolean) newValue;
			int flag = (enabled ? PackageManager.COMPONENT_ENABLED_STATE_ENABLED
					: PackageManager.COMPONENT_ENABLED_STATE_DISABLED);
			ComponentName component = new ComponentName(Settings.this,
					Explode.class);

			getPackageManager().setComponentEnabledSetting(component, flag,
					PackageManager.DONT_KILL_APP);
		}
		return true;
	}

}
