package uk.co.ashtonbrsc.intentexplode;

import uk.co.ashtonbrsc.android.intentintercept.R;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;

import com.actionbarsherlock.app.SherlockPreferenceActivity;

public class Settings extends SherlockPreferenceActivity implements
		OnPreferenceChangeListener, OnPreferenceClickListener {

	private static final CharSequence INTERCEPT_ENABLED = "interceptEnabled";
	private Preference interceptEnabledPreference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.settings);
		addPreferencesFromResource(R.xml.more_settings);

		interceptEnabledPreference = findPreference(INTERCEPT_ENABLED);

		interceptEnabledPreference.setOnPreferenceChangeListener(this);

		for (int i = 0; i < getPreferenceScreen().getPreferenceCount(); i++) {
			getPreferenceScreen().getPreference(i)
					.setOnPreferenceClickListener(this);
		}

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

	public boolean onPreferenceClick(Preference preference) {
		Uri uri = null;
		String preferenceKey = preference.getKey();
		if (preferenceKey.equals("visitGooglePlus")) {
			uri = Uri
					.parse("https://plus.google.com/114248840147347624819/posts");
		} else if (preferenceKey.equals("visitWebsite")) {
			uri = Uri.parse("http://www.intrications.com");
		} else if (preferenceKey.equals("viewMyOtherApps")) {
			uri = Uri.parse("https://play.google.com/store/apps/developer?id=Intrications");
		} else if (preferenceKey.equals("rateThisApp")) {
			uri = Uri.parse("https://play.google.com/store/apps/details?id="
					+ getPackageName());
		} else if (preferenceKey.equals("licence")) {
			uri = Uri.parse("http://www.apache.org/licenses/LICENSE-2.0.html");
		}

		startActivity(new Intent(Intent.ACTION_VIEW, uri));
		return false;
	}

}
