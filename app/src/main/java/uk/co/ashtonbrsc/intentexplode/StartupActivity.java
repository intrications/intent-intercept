package uk.co.ashtonbrsc.intentexplode;

import android.app.Activity;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

public class StartupActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH_MR1) {
            startActivity(new Intent(this, Pre15SettingsActivity.class));
        } else {
            startActivity(new Intent(this, SettingsActivity.class));
        }
        finish();
    }
}
