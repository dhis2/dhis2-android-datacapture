package org.dhis2.ehealthMobile.ui.activities;

import android.content.res.AssetManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;

import org.dhis2.ehealthMobile.R;
import org.dhis2.ehealthMobile.utils.CustomTypefaceSpan;
import org.dhis2.ehealthMobile.utils.TypefaceManager;

public class BaseActivity extends AppCompatActivity {
    private Typeface mCustomTypeFace;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mCustomTypeFace = getTypeFace();
    }

    @Override
    public void setTitle(CharSequence sequence) {
        if (getSupportActionBar() == null) {
            return;
        }

        if (sequence != null && mCustomTypeFace != null) {
            CustomTypefaceSpan typefaceSpan = new CustomTypefaceSpan(mCustomTypeFace);
            SpannableString title = new SpannableString(sequence);
            title.setSpan(typefaceSpan, 0, title.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            getSupportActionBar().setTitle(title);
        } else {
            getSupportActionBar().setTitle(sequence);
        }
    }

    private Typeface getTypeFace() {
        AssetManager manager = getAssets();
        String fontName = getString(R.string.regular_font_name);
        if (manager != null && !TextUtils.isEmpty(fontName)) {
            return TypefaceManager.getTypeface(getAssets(), fontName);
        } else {
            return null;
        }
    }
}
