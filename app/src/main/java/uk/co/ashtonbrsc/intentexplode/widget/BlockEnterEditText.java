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

package uk.co.ashtonbrsc.intentexplode.widget;

import android.content.Context;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.widget.EditText;

public class BlockEnterEditText extends EditText {

	public BlockEnterEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public BlockEnterEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public BlockEnterEditText(Context context) {
		super(context);
	}

	@Override
	public boolean onKeyDown(int keyCode, @NonNull KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_ENTER) {
			// Just ignore the [Enter] key
			return true;
		}
		// Handle all other keys in the default way
		return super.onKeyDown(keyCode, event);
	}

}
