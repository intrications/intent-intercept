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

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.ClipboardManager;
import android.text.Editable;
import android.text.Html;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextWatcher;
import android.text.style.LeadingMarginSpan;
import android.text.style.ParagraphStyle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import uk.co.ashtonbrsc.android.intentintercept.R;

//TODO add icon -which icon - app icons???
//TODO add bitmaps/images (from intent extras?)
//TODO add getCallingActivity() - will only give details for startActivityForResult();

/**
 * Should really be called IntentDetailsActivity but this may cause problems with launcher
 * shortcuts and the enabled/disabled state of interception.
 */
public class Explode extends AppCompatActivity {
	private static final String INTENT_EDITED = "intent_edited";
	private static final int STANDARD_INDENT_SIZE_IN_DIP = 10;
	private static final String NEWLINE = "\n<br>";
	private static final String BLANK = " ";

	private static final String NEWSEGMENT = NEWLINE + "------------" + NEWLINE;

	private static final String BOLD_START = "<b><u>";
	private static final String BOLD_END_BLANK = "</u></b>" + BLANK;
	private static final String BOLD_END_NL = "</u></b>" + NEWLINE;

	private abstract class IntentUpdateTextWatcher implements TextWatcher {
        private final TextView textView;

        IntentUpdateTextWatcher(TextView textView) {
            this.textView = textView;
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before,
                int count) {
            if (textWatchersActive) {
                try {
                    String modifiedContent = textView.getText().toString();
                    onUpdateIntent(modifiedContent);
                    showTextViewIntentData(textView);
                    showResetIntentButton(true);
                    refreshUI();
                } catch (Exception e) {
                    Toast.makeText(Explode.this, e.getMessage(),
                            Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }
        }

        abstract protected void onUpdateIntent(String modifiedContent);

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count,
                int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

	private ShareActionProvider shareActionProvider; // api-14 or compat
	private EditText action;
	private EditText data;
	private EditText type;
    private EditText uri;
	private TextView categoriesHeader;
	private LinearLayout categoriesLayout;
	private LinearLayout flagsLayout;
	private LinearLayout extrasLayout;
	private LinearLayout activitiesLayout;
	private TextView activitiesHeader;
	private Button resendIntentButton;
	private Button resetIntentButton;
	private float density;

    /** String representation of intent as uri */
	private String originalIntent;

    /** Bugfix #14: extras that are lost in the intent <-> string conversion */
    private Bundle additionalExtras;

    private Intent editableIntent;

    // support for onActivityResult
    private Integer lastResultCode = null;
    private Intent lastResultIntent = null;

    /** false: text-change-events are not active. */
	private boolean textWatchersActive;

	private static final Map<Integer, String> FLAGS_MAP = new HashMap<Integer, String>() {
		{
			put(Integer.valueOf(Intent.FLAG_GRANT_READ_URI_PERMISSION),
					"FLAG_GRANT_READ_URI_PERMISSION");
			put(Integer.valueOf(Intent.FLAG_GRANT_WRITE_URI_PERMISSION),
					"FLAG_GRANT_WRITE_URI_PERMISSION");
			put(Integer.valueOf(Intent.FLAG_FROM_BACKGROUND),
					"FLAG_FROM_BACKGROUND");
			put(Integer.valueOf(Intent.FLAG_DEBUG_LOG_RESOLUTION),
					"FLAG_DEBUG_LOG_RESOLUTION");
			put(Integer.valueOf(Intent.FLAG_EXCLUDE_STOPPED_PACKAGES),
					"FLAG_EXCLUDE_STOPPED_PACKAGES");
			put(Integer.valueOf(Intent.FLAG_INCLUDE_STOPPED_PACKAGES),
					"FLAG_INCLUDE_STOPPED_PACKAGES");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_NO_HISTORY),
					"FLAG_ACTIVITY_NO_HISTORY");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_SINGLE_TOP),
					"FLAG_ACTIVITY_SINGLE_TOP");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_NEW_TASK),
					"FLAG_ACTIVITY_NEW_TASK");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_MULTIPLE_TASK),
					"FLAG_ACTIVITY_MULTIPLE_TASK");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_CLEAR_TOP),
					"FLAG_ACTIVITY_CLEAR_TOP");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_FORWARD_RESULT),
					"FLAG_ACTIVITY_FORWARD_RESULT");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_PREVIOUS_IS_TOP),
					"FLAG_ACTIVITY_PREVIOUS_IS_TOP");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS),
					"FLAG_ACTIVITY_EXCLUDE_FROM_RECENTS");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT),
					"FLAG_ACTIVITY_BROUGHT_TO_FRONT");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED),
					"FLAG_ACTIVITY_RESET_TASK_IF_NEEDED");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY),
					"FLAG_ACTIVITY_LAUNCHED_FROM_HISTORY");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET),
					"FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_NO_USER_ACTION),
					"FLAG_ACTIVITY_NO_USER_ACTION");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT),
					"FLAG_ACTIVITY_REORDER_TO_FRONT");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_NO_ANIMATION),
					"FLAG_ACTIVITY_NO_ANIMATION");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_CLEAR_TASK),
					"FLAG_ACTIVITY_CLEAR_TASK");
			put(Integer.valueOf(Intent.FLAG_ACTIVITY_TASK_ON_HOME),
					"FLAG_ACTIVITY_TASK_ON_HOME");
			put(Integer.valueOf(Intent.FLAG_RECEIVER_REGISTERED_ONLY),
					"FLAG_RECEIVER_REGISTERED_ONLY");
			put(Integer.valueOf(Intent.FLAG_RECEIVER_REPLACE_PENDING),
					"FLAG_RECEIVER_REPLACE_PENDING");
			put(Integer.valueOf(Intent.FLAG_RECEIVER_FOREGROUND),
					"FLAG_RECEIVER_FOREGROUND");
			put(Integer.valueOf(0x08000000),
					"FLAG_RECEIVER_REGISTERED_ONLY_BEFORE_BOOT");
			put(Integer.valueOf(0x04000000), "FLAG_RECEIVER_BOOT_UPGRADE");
		}
	};

    @Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.explode);

        rememberIntent(getIntent());

        final boolean isVisible = savedInstanceState != null
                && savedInstanceState.getBoolean(INTENT_EDITED);
        showInitialIntent(isVisible);
	}

    private void rememberIntent(Intent original) {
        this.originalIntent = getUri(original);

        Intent copy = cloneIntent(this.originalIntent);

        final Bundle originalExtras = original.getExtras();

        if (originalExtras != null) {
            // bugfix #14: collect extras that are lost in the intent <-> string conversion
            Bundle additionalExtrasBundle = new Bundle(originalExtras);
            for (String key : originalExtras.keySet()) {
                if (copy.hasExtra(key)) {
                    additionalExtrasBundle.remove(key);
                }
            }

            if (!additionalExtrasBundle.isEmpty()) {
                additionalExtras = additionalExtrasBundle;
            }
        }

    }

    /**
     * creates a clone of originalIntent and displays it for editing
     * @param isVisible
     */
    private void showInitialIntent(boolean isVisible) {
        editableIntent = cloneIntent(this.originalIntent);

        editableIntent.setComponent(null);

        setupVariables();

        setupTextWatchers();

        showAllIntentData(null);

        showResetIntentButton(isVisible);
    }

    /** textViewToIgnore is not updated so current selected char in that textview will not change */
    private void showAllIntentData(TextView textViewToIgnore) {
        showTextViewIntentData(textViewToIgnore);

        categoriesLayout.removeAllViews();
        Set<String> categories = editableIntent.getCategories();
		if (categories != null) {
            categoriesHeader.setVisibility(View.VISIBLE);
			for (String category : categories) {
				TextView categoryTextView = new TextView(this);
				categoryTextView.setText(category);
				categoryTextView.setTextAppearance(this, R.style.TextFlags);
				categoriesLayout.addView(categoryTextView);
			}
		} else {
			categoriesHeader.setVisibility(View.GONE);
			// addTextToLayout("NONE", Typeface.NORMAL, categoriesLayout);
		}

        flagsLayout.removeAllViews();
		ArrayList<String> flagsStrings = getFlags();
		if (!flagsStrings.isEmpty()) {
			for (String thisFlagString : flagsStrings) {
				addTextToLayout(thisFlagString, Typeface.NORMAL, flagsLayout);
			}
		} else {
			addTextToLayout(getString(R.string.no_items), Typeface.NORMAL, flagsLayout);
		}

        extrasLayout.removeAllViews();
		try {

			Bundle intentBundle = editableIntent.getExtras();
			if (intentBundle != null) {
				Set<String> extraKeys = intentBundle.keySet();
				int count = 0;

				for (String extraKey : extraKeys) {
					count++;
					Object extraItem = intentBundle.get(extraKey);
					if (extraItem != null) {
						String extraItemTypeName = extraItem.getClass().getName();

						addTextToLayout("" + count, Typeface.BOLD, extrasLayout);

						if (extraItemTypeName != null) {
							addTextToLayout(getString(R.string.extra_item_type_name_title) + BLANK + extraItemTypeName,
									Typeface.ITALIC,
									STANDARD_INDENT_SIZE_IN_DIP, extrasLayout);
						}

						addTextToLayout(getString(R.string.extra_item_key_title) + BLANK + extraKey, Typeface.ITALIC,
								STANDARD_INDENT_SIZE_IN_DIP, extrasLayout);

						if (extraItem instanceof ArrayList) {
							addTextToLayout(getString(R.string.extra_item_type_name_list), Typeface
									.ITALIC, extrasLayout);
							ArrayList thisArrayList = (ArrayList) extraItem;
							for (Object thisArrayListObject : thisArrayList) {
								addTextToLayout(thisArrayListObject.toString(),
										Typeface.ITALIC, STANDARD_INDENT_SIZE_IN_DIP,
										extrasLayout);
							}
						} else {
							addTextToLayout(getString(R.string.extra_item_value_title) + BLANK + extraItem
											.toString(),
									Typeface.ITALIC, STANDARD_INDENT_SIZE_IN_DIP,
									extrasLayout);

						}
					}
				}
			} else {
				addTextToLayout(getString(R.string.no_items), Typeface.NORMAL,
						extrasLayout);
			}
		} catch (Exception e) {
			// TODO Should make this red to highlight error
			addTextToLayout(getString(R.string.error_extracting_extras), Typeface.NORMAL, extrasLayout);
			e.printStackTrace();
		}

		refreshUI();
	}

    /** textViewToIgnore is not updated so current selected char in that textview will not change */
    private void showTextViewIntentData(TextView textViewToIgnore) {
        textWatchersActive = false;
        if (textViewToIgnore != action) action.setText(editableIntent.getAction());
        if ((textViewToIgnore != data) && (editableIntent.getDataString() != null)) {
            data.setText(editableIntent.getDataString());
        }
        if (textViewToIgnore != type) type.setText(editableIntent.getType());
        if (textViewToIgnore != uri) uri.setText(getUri(editableIntent));
        textWatchersActive = true;
    }

    private ArrayList<String> getFlags() {
		ArrayList<String> flagsStrings = new ArrayList<>();
		int flags = editableIntent.getFlags();
		Set<Entry<Integer, String>> set = FLAGS_MAP.entrySet();
		Iterator<Entry<Integer, String>> i = set.iterator();
		while (i.hasNext()) {
			Entry<Integer, String> thisFlag = (Entry<Integer, String>) i.next();
			if ((flags & thisFlag.getKey()) != 0) {
				flagsStrings.add(thisFlag.getValue());
			}
		}
		return flagsStrings;
	}

	private void checkAndShowMatchingActivites() {

		activitiesLayout.removeAllViews();
		PackageManager pm = getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentActivities(
                editableIntent, 0);

		activitiesHeader.setText(getString(R.string.intent_matching_activities_title));

		// Remove Intent Intercept from matching activities
		int numberOfMatchingActivities = resolveInfo.size() - 1;

		if (numberOfMatchingActivities < 1) {
            resendIntentButton.setEnabled(false);
			addTextToLayout(getString(R.string.no_items), Typeface.NORMAL, activitiesLayout);

		} else {
            resendIntentButton.setEnabled(true);
			for (int i = 0; i <= numberOfMatchingActivities; i++) {
				ResolveInfo info = resolveInfo.get(i);
				ActivityInfo activityinfo = info.activityInfo;
				if (!activityinfo.packageName.equals(getPackageName())) {
					addTextToLayout(activityinfo.loadLabel(pm) + " ("
							+ activityinfo.packageName + " - "
							+ activityinfo.name + ")", Typeface
									.NORMAL,
							activitiesLayout);
				}
			}
		}
	}

	private void addTextToLayout(String text, int typeface, int paddingLeft,
			LinearLayout layout) {
		TextView textView = new TextView(this);
		ParagraphStyle style_para = new LeadingMarginSpan.Standard(0,
				(int) (STANDARD_INDENT_SIZE_IN_DIP * density));
		SpannableString styledText = new SpannableString(text);
		styledText.setSpan(style_para, 0, styledText.length(),
				Spanned.SPAN_INCLUSIVE_EXCLUSIVE);
		textView.setText(styledText);
		textView.setTextAppearance(this, R.style.TextFlags);
		textView.setTypeface(null, typeface);
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
			textView.setTextIsSelectable(true);
		}
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.WRAP_CONTENT);
		params.setMargins((int) (paddingLeft * density), 0, 0, 0);
		layout.addView(textView, params);
	}

	private void addTextToLayout(String text, int typeface, LinearLayout layout) {
		addTextToLayout(text, typeface, 0, layout);
	}

	private void setupVariables() {
		action = (EditText) findViewById(R.id.action_edit);
		data = (EditText) findViewById(R.id.data_edit);
		type = (EditText) findViewById(R.id.type_edit);
        uri = (EditText) findViewById(R.id.uri_edit);
		categoriesHeader = (TextView) findViewById(R.id.intent_categories_header);
		categoriesLayout = (LinearLayout) findViewById(R.id.intent_categories_layout);
		flagsLayout = (LinearLayout) findViewById(R.id.intent_flags_layout);
		extrasLayout = (LinearLayout) findViewById(R.id.intent_extras_layout);
		activitiesHeader = (TextView) findViewById(R.id.intent_matching_activities_header);
		activitiesLayout = (LinearLayout) findViewById(R.id.intent_matchin_activities_layout);
		resendIntentButton = (Button) findViewById(R.id.resend_intent_button);
		resetIntentButton = (Button) findViewById(R.id.reset_intent_button);

		DisplayMetrics metrics = new DisplayMetrics();
		WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
		wm.getDefaultDisplay().getMetrics(metrics);
		density = metrics.density;
	}

	private void setupTextWatchers() {
		action.addTextChangedListener(new IntentUpdateTextWatcher(action) {
			@Override
            protected void onUpdateIntent(String modifiedContent) {
                editableIntent.setAction(modifiedContent);
            }
		});
		data.addTextChangedListener(new IntentUpdateTextWatcher(data) {
            @Override
            protected void onUpdateIntent(String modifiedContent) {
                // setData clears type so we save it
                String savedType = editableIntent.getType();
                editableIntent.setDataAndType(Uri.parse(modifiedContent), savedType);
            }
		});
		type.addTextChangedListener(new IntentUpdateTextWatcher(type) {
            @Override
            protected void onUpdateIntent(String modifiedContent) {
                // setData clears type so we save it
                String dataString = editableIntent.getDataString();
                editableIntent.setDataAndType(Uri.parse(dataString), modifiedContent);
            }
		});
        uri.addTextChangedListener(new IntentUpdateTextWatcher(uri) {
            @Override
            protected void onUpdateIntent(String modifiedContent) {
                Intent newIntent = cloneIntent(modifiedContent);

                // no error yet so continue
                editableIntent = newIntent;
                // this time must update all content since extras/flags may have been changed
                showAllIntentData(uri);
            }
        });
	}

    private void showResetIntentButton(boolean visible) {
		resendIntentButton.setText(R.string.button_title_send_edited_intent);
		resetIntentButton.setVisibility((visible) ? View.VISIBLE : View.GONE);
	}

	public void onSendIntent(View v) {
		try {
			startActivityForResult(Intent.createChooser(editableIntent,resendIntentButton.getText()), 1);
		} catch (Exception e) {
			Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
			e.printStackTrace();
		}
	}

	public void onResetIntent(View v) {
        // this would break onActivityResult
		// startActivity(this.originalIntent); // reload this with original data
		// finish();
        textWatchersActive = false;
        showInitialIntent(false);
        textWatchersActive = true;

        refreshUI();
	}

	private void copyIntentDetails() {
		ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
		clipboard.setText(getIntentDetailsString());
		Toast.makeText(this, R.string.message_intent_details_copied_to_clipboard,
				Toast.LENGTH_SHORT).show();
	}

	private void refreshUI() {
		// if (!intent.getAction().equals(getIntent().getAction())
		// || (intent.getDataString() != null && !intent.getDataString()
		// .equals(getIntent().getDataString()))
		// || !intent.getType().equals(getIntent().getType())) {
		//
		// }
		checkAndShowMatchingActivites();
		if (shareActionProvider != null) {
			Intent share = createShareIntent();
			shareActionProvider.setShareIntent(share);
		}
	}

	private Intent createShareIntent() {
		Intent share = new Intent(Intent.ACTION_SEND);
		share.setType(getString(R.string.mime_type_text_plain));
		share.putExtra(Intent.EXTRA_TEXT, getIntentDetailsString());
		return share;
	}

	private Spanned getIntentDetailsString() {
		StringBuilder result = new StringBuilder();

        // k3b so intent can be reloaded using
        // Intent.parseUri("Intent:....", Intent.URI_INTENT_SCHEME)
        result.append(getUri(editableIntent))
				.append(NEWSEGMENT);

		appendIntentDetails(result, editableIntent, true)
				.append(NEWSEGMENT);

		PackageManager pm = getPackageManager();
		List<ResolveInfo> resolveInfo = pm.queryIntentActivities(
				editableIntent, 0);

		// Remove Intent Intercept from matching activities
		int numberOfMatchingActivities = resolveInfo.size() - 1;

		appendHeader(result, R.string.intent_matching_activities_title);
		if (numberOfMatchingActivities < 1) {
			appendHeader(result, R.string.no_items);
		} else {
			for (int i = 0; i <= numberOfMatchingActivities; i++) {
				ResolveInfo info = resolveInfo.get(i);
				ActivityInfo activityinfo = info.activityInfo;
				if (!activityinfo.packageName.equals(getPackageName())) {
					result.append(BOLD_START).append(activityinfo.loadLabel(pm))
							.append(BOLD_END_BLANK).append(" (")
							.append(activityinfo.packageName)
							.append(" - ")
							.append(activityinfo.name)
							.append(")").append(NEWLINE);
				}
			}
		}
		
		// support for onActivityResult
		if (this.lastResultCode != null) {
			result.append(NEWSEGMENT);
			appendHeader(result, R.string.last_result_header_title);
			appendNameValue(result, R.string.last_result_code_title, this.lastResultCode);

			if (this.lastResultIntent != null) {
				appendIntentDetails(result, lastResultIntent, false);
			}
		}
		
		return Html.fromHtml(result.toString());
	}

	private StringBuilder appendIntentDetails(StringBuilder result, Intent intent, boolean detailed) {
		if (detailed) appendNameValue(result, R.string.intent_action_title, intent.getAction());

		appendNameValue(result, R.string.intent_data_title, intent.getData());
		appendNameValue(result, R.string.intent_mime_type_title, intent.getType());
		appendNameValue(result, R.string.intent_uri_title, getUri(intent));

		Set<String> categories = intent.getCategories();
		if ((categories != null) && (categories.size() > 0)) {
			appendHeader(result, R.string.intent_categories_title);
			for (String category : categories) {
				result.append(category).append(NEWLINE);
			}
		}

		if (detailed) {
			appendHeader(result, R.string.intent_flags_title);
			ArrayList<String> flagsStrings = getFlags();
			if (!flagsStrings.isEmpty()) {
				for (String thisFlagString : flagsStrings) {
					result.append(thisFlagString).append(NEWLINE);
				}
			} else {
				result.append(getString(R.string.no_items)).append(NEWLINE);
			}
		}

		try {
			Bundle intentBundle = intent.getExtras();
			if (intentBundle != null) {
				Set<String> keySet = intentBundle.keySet();
				appendHeader(result, R.string.intent_extras_title);
				int count = 0;

				for (String key : keySet) {
					count++;
					Object thisObject = intentBundle.get(key);
					result.append(BOLD_START).append(count).append(BOLD_END_BLANK);
					String thisClass = thisObject.getClass().getName();
					if (thisClass != null) {
						result.append(getString(R.string.extra_item_type_name_title)).append(BLANK)
								.append(thisClass).append(NEWLINE);
					}
					result.append(getString(R.string.extra_item_key_title)).append(BLANK)
							.append(key).append(NEWLINE);

					if (thisObject instanceof String || thisObject instanceof Long
							|| thisObject instanceof Integer
							|| thisObject instanceof Boolean
							|| thisObject instanceof Uri) {
						result.append(getString(R.string.extra_item_value_title)).append(BLANK)
								.append(thisObject.toString())
								.append(NEWLINE);
					} else if (thisObject instanceof ArrayList) {
						result.append(getString(R.string.extra_item_type_name_list)).append(NEWLINE);
						ArrayList thisArrayList = (ArrayList) thisObject;
						for (Object thisArrayListObject : thisArrayList) {
							result.append(thisArrayListObject.toString()).append(NEWLINE);
						}
					}
				}
			}
		} catch (Exception e) {
			appendHeader(result, R.string.intent_extras_title);
			result.append("<font color='red'>").append(getString(R.string.error_extracting_extras)).append("</font>").append(NEWLINE);
			e.printStackTrace();
		}
		return result;
	}

	private StringBuilder appendNameValue(StringBuilder result, int keyId, Object value) {
		if (value  != null) {
			result.append(BOLD_START).append(getString(keyId)).append(BOLD_END_BLANK)
					.append(value).append(NEWLINE);
		}
		return result;
	}

	private StringBuilder appendHeader(StringBuilder result, int keyId) {
		result.append(BOLD_START).append(getString(keyId)).append(BOLD_END_NL);
		return result;
	}


	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		MenuItem actionItem = menu.findItem(R.id.menu_share);

        shareActionProvider = (ShareActionProvider) MenuItemCompat.getActionProvider(actionItem);

        if (shareActionProvider == null) {
            shareActionProvider = new ShareActionProvider(this);
            MenuItemCompat.setActionProvider(actionItem, shareActionProvider);
        }

        shareActionProvider
                    .setShareHistoryFileName(ShareActionProvider.DEFAULT_SHARE_HISTORY_FILE_NAME);
		refreshUI();
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_copy:
			copyIntentDetails();
			break;
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onPause() {
		super.onPause();
		textWatchersActive = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		overridePendingTransition(0, 0); // inhibit new activity animation when
											// resetting intent details
		textWatchersActive = true;
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(INTENT_EDITED,
				resetIntentButton.getVisibility() == View.VISIBLE);
	}

    // support for onActivityResult
    // OriginatorActivity -> IntentIntercept -> resendIntentActivity
    // Forward result of sub-activity {resendIntentActivity}
    // to caller of this activity {OriginatorActivity}.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        this.lastResultCode = Integer.valueOf(resultCode);
        this.lastResultIntent = data;
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        refreshUI();

		Uri uri = (data == null) ? null : data.getData();
		Toast.makeText(Explode.this,
				getString(R.string.last_result_message, getString(R.string.last_result_header_title), ""+requestCode, uri),
				Toast.LENGTH_LONG).show();
	}

    private static String getUri(Intent src) {
		return (src != null) ? src.toUri(Intent.URI_INTENT_SCHEME) : null;
    }
    private Intent cloneIntent(String intentUri) {
        if (intentUri != null) {
            try {
                Intent clone = Intent.parseUri(intentUri, Intent.URI_INTENT_SCHEME);

                // bugfix #14: restore extras that are lost in the intent <-> string conversion
                if (additionalExtras != null) {
                    clone.putExtras(additionalExtras);
                }

                return clone;
            } catch (URISyntaxException e) {
                e.printStackTrace();
            }
        }
        return null;
    }
}