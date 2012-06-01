package uk.co.ashtonbrsc.intentexplode;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import uk.co.ashtonbrsc.android.intentintercept.R;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.text.ClipboardManager;
import android.text.Html;
import android.view.View;
import android.webkit.WebView;
import android.widget.Toast;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuItem;

//TODO add icon -which icon - app icons???
//TODO add flags to display
//TODO add bitmaps/images (from intent extras?)

public class Explode extends SherlockActivity {

	private String intentDetailsHtml;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.explode);
		WebView webView = (WebView) findViewById(R.id.webView);

		StringBuilder stringBuilder = new StringBuilder();

		Intent intent = getIntent();

		intent.setComponent(null);

		stringBuilder.append("<b><u>Action:</u></b> ")
				.append(intent.getAction()).append("<br>");
		stringBuilder.append("<b><u>Data:</u></b> ").append(intent.getData())
				.append("<br>");
		stringBuilder.append("<b><u>Uri:</u></b> ")
				.append(intent.getDataString()).append("<br>");
		stringBuilder.append("<b><u>Type:</u></b> ").append(intent.getType())
				.append("<br>");
		Set<String> categories = intent.getCategories();
		if (categories != null) {
			stringBuilder.append("<b><u>Categories:</u></b><br>");
			for (String category : categories) {
				stringBuilder.append(category).append("<br>");
			}
		}
		// stringBuilder.append(intent.getFlags().append("<br>");
		Bundle intentBundle = intent.getExtras();
		if (intentBundle != null) {
			Set<String> keySet = intentBundle.keySet();
			stringBuilder.append("<br><b><u>Bundle:</u></b><br>");
			int count = 0;

			for (String key : keySet) {
				count++;
				Object thisObject = intentBundle.get(key);
				stringBuilder.append("<u>Object ").append(count)
						.append(":</u><br>");
				String thisClass = thisObject.getClass().getName();
				if (thisClass != null) {
					stringBuilder.append("Class: ").append(thisClass)
							.append("<br>");
				}
				stringBuilder.append("Key: ").append(key).append("<br>");
				if (thisObject instanceof String) {
					stringBuilder.append(intentBundle.get(key)).append("<br>");
				} else if (thisObject instanceof java.util.ArrayList) {
					ArrayList thisArrayList = (ArrayList) thisObject;
					for (Object thisArrayListObject : thisArrayList) {
						stringBuilder.append(thisArrayListObject)
								.append("<br>");
					}
				}
			}
		}

		PackageManager pm = getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentActivities(intent, 0);
		stringBuilder.append("<br><b><u>" + resolveInfo.size()
				+ " activities match this intent:</u></b><br>");
		for (int i = 0; i < resolveInfo.size(); i++) {
			ResolveInfo info = resolveInfo.get(i);
			ActivityInfo activityinfo = info.activityInfo;
			if (!activityinfo.packageName.equals(getPackageName())) {
				stringBuilder.append(activityinfo.packageName + "<br>");
			}
		}

		// resolveInfo = pm.queryIntentServices(intent, 0);
		// stringBuilder.append("<br><b><u>" + resolveInfo.size()
		// + " services match this intent:</u></b><br>");
		// for (int i = 0; i < resolveInfo.size(); i++) {
		// ResolveInfo info = resolveInfo.get(i);
		// ActivityInfo activityinfo = info.activityInfo;
		// stringBuilder.append(activityinfo.packageName + "<br>");
		// }

		intentDetailsHtml = stringBuilder.toString();
		webView.loadData(intentDetailsHtml, "text/html", "UTF-8");

	}

	public void onResendIntent(View v) {
		startActivity(getIntent());
	}

	public void copyIntentDetails() {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboard.setText(getIntentDetailsString());
		Toast.makeText(this, R.string.intent_details_copied_to_clipboard,
				Toast.LENGTH_SHORT).show();
	}

	public void shareIntentDetails() {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType("text/plain");
		share.putExtra(Intent.EXTRA_TEXT, getIntentDetailsString());
		startActivity(Intent.createChooser(share, getString(R.string.share_intent_details)));
	}

	private String getIntentDetailsString() {
		return Html.fromHtml(intentDetailsHtml).toString();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getSupportMenuInflater().inflate(R.menu.menu, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.copy:
			copyIntentDetails();
			break;
		case R.id.share:
			shareIntentDetails();
			break;
		}
		return super.onOptionsItemSelected(item);
	}
}
