//   Copyright 2012 Intrications (intrications.com)
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
		OnPreferenceChangeListener {

	private static final CharSequence INTERCEPT_ENABLED = "interceptEnabled";
	private Preference interceptEnabledPreference;

	@SuppressWarnings("deprecation")
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
