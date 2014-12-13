package org.hisp.dhis.mobile.datacapture.ui.views;

import android.app.Dialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import org.hisp.dhis.mobile.datacapture.R;

public abstract class AbsSelectorView extends LinearLayout {
    private FontTextView mTextView;
    private Dialog mDialog;

    public AbsSelectorView(Context context) {
        super(context);
        init(context, null);
    }

    public AbsSelectorView(Context context, AttributeSet attributes) {
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

        View contentView = onCreateDialogView(LayoutInflater.from(getContext()), this);
        mDialog = new Dialog(context);
        mDialog.setContentView(contentView);

        mTextView = new FontTextView(context);
        mTextView.setBackgroundResource(R.drawable.spinner_background_holo_light);
        mTextView.setFont(getContext().getString(R.string.regular_font_name));
        mTextView.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                if (mDialog != null) {
                    mDialog.show();
                }
            }
        });

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
    }

    public void show() {
        if (mDialog != null) {
            mDialog.show();
        }
    }

    public void setText(CharSequence sequence) {
        if (mTextView != null) {
            mTextView.setText(sequence);
        }
    }

    public void dismiss() {
        if (mDialog != null) {
            mDialog.dismiss();
        }
    }

    private float calculatePixels(int dps) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dps,
                getResources().getDisplayMetrics());
    }

    protected Dialog getDialog() {
        return mDialog;
    }

    protected FontTextView getTextView() {
        return mTextView;
    }

    public abstract View onCreateDialogView(LayoutInflater inflater, ViewGroup container);
}
