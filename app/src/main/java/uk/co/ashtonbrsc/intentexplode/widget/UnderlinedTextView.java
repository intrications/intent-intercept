// Code from slide 64 at
// https://speakerdeck.com/u/cyrilmottier/p/optimizing-android-ui-pro-tips-for-creating-smooth-and-responsive-apps

package uk.co.ashtonbrsc.intentexplode.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatTextView;

public class UnderlinedTextView extends AppCompatTextView {

	private final Paint mPaint = new Paint();
	private int mUnderlineHeight = 2;

	public UnderlinedTextView(Context context) {
		super(context);
		initColor();
	}

	public UnderlinedTextView(Context context, AttributeSet attrs) {
		super(context, attrs);
		initColor();
	}

	public UnderlinedTextView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initColor();
	}

	private void initColor() {
		mPaint.setColor(getResources().getColor(android.R.color.holo_blue_light));
	}

	@Override
	public void setPadding(int left, int top, int right, int bottom) {
		super.setPadding(left, top, right, mUnderlineHeight + bottom);
	}

	public void setUnderlineHeight(int underlineHeight) {
		if (underlineHeight < 0)
			underlineHeight = 0;
		if (underlineHeight != mUnderlineHeight) {
			mUnderlineHeight = underlineHeight;
			setPadding(getPaddingLeft(), getPaddingTop(), getPaddingRight(),
					getPaddingBottom() + mUnderlineHeight);
		}
	}

	public void setUnderlineColor(int underlineColor) {
		if (mPaint.getColor() != underlineColor) {
			mPaint.setColor(underlineColor);
			invalidate();
		}
	}

	@Override
	protected void onDraw(@NonNull Canvas canvas) {
		super.onDraw(canvas);
		canvas.drawRect(0, getHeight() - mUnderlineHeight, getWidth(),
				getHeight(), mPaint);
	}

}
