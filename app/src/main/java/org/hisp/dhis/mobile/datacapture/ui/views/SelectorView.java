package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.LinearLayout;

import org.hisp.dhis.mobile.datacapture.R;

public class SelectorView extends LinearLayout {
    private SelectorView mChildSelectorView;
    private FontTextView mTextView;

    public SelectorView(Context context) {
        super(context);
        init(context, null);
    }

    public SelectorView(Context context, AttributeSet attributes) {
        super(context, attributes);

        if (!isInEditMode()) {
            TypedArray attrs = context.obtainStyledAttributes(attributes, R.styleable.SelectorHint);
            init(context, attrs.getString(R.styleable.SelectorHint_hint));
            attrs.recycle();
        } else {
            init(context, null);
        }
    }

    private void init(Context context, CharSequence sequence) {
        setBackgroundResource(R.drawable.card_background);
        setOrientation(LinearLayout.VERTICAL);

        int pxs = getResources().getDimensionPixelSize(R.dimen.selector_view_padding);
        setPadding(pxs, pxs, pxs, pxs);

        mTextView = new FontTextView(context);
        mTextView.setClickable(true);
        mTextView.setId(getId());
        mTextView.setBackgroundResource(R.drawable.spinner_background_holo_light);
        mTextView.setFont(getContext().getString(R.string.regular_font_name));

        LayoutParams textViewParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        mTextView.setLayoutParams(textViewParams);

        if (sequence != null) {
            mTextView.setText(sequence);
        }

        addView(mTextView);
    }

    @Override
    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);

        if (mTextView != null) {
            mTextView.setEnabled(enabled);
        }

        if (mChildSelectorView != null) {
            mChildSelectorView.setEnabled(enabled);
        }
    }

    public void setText(CharSequence sequence) {
        if (mTextView != null) {
            mTextView.setText(sequence);
        }
    }

    public void setOnClickListener(OnClickListener listener) {
        mTextView.setOnClickListener(listener);
    }

    public void chainWith(SelectorView childSelectorView) {
        mChildSelectorView = childSelectorView;
    }

    protected FontTextView getTextView() {
        return mTextView;
    }
}
