package uk.co.ashtonbrsc.intentexplode;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import uk.co.ashtonbrsc.android.intentintercept.R;

public class StartupActivity extends AppCompatActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings);

        getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new SettingsFragment()).commit();
    }
}
