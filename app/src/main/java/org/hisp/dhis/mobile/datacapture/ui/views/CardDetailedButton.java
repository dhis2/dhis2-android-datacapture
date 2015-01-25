package org.hisp.dhis.mobile.datacapture.ui.views;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.LinearLayout;

import org.hisp.dhis.mobile.datacapture.R;

public class CardDetailedButton extends CardView {
    private static final float WEIGHT_SUM = 1.0f;
    private static final float TEXT_VIEW_CONTAINER_WEIGHT = 0.95f;
    private static final float IMAGE_VIEW_WEIGHT = 0.05f;

    private static final int BIG_TEXT_SIZE = 17;
    private static final int SMALL_TEXT_SIZE = 13;

    private FontTextView mFirstLine;
    private FontTextView mSecondLine;
    private FontTextView mThirdLine;

    private LinearLayout mContainer;

    public CardDetailedButton(Context context) {
        super(context);
        init();
    }

    public CardDetailedButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CardDetailedButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        // configure CardView first
        ViewGroup.LayoutParams cardViewParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT
        );
        setLayoutParams(cardViewParams);

        // configure container with contents
        ViewGroup.LayoutParams containerParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        int containerPadding = getResources().getDimensionPixelSize(
                R.dimen.card_detail_button_padding);

        mContainer = new LinearLayout(getContext());
        mContainer.setId(getId());
        mContainer.setBackgroundResource(R.drawable.transparent_selector);
        mContainer.setLayoutParams(containerParams);
        mContainer.setOrientation(LinearLayout.HORIZONTAL);
        mContainer.setPadding(containerPadding, containerPadding,
                containerPadding, containerPadding);
        mContainer.setWeightSum(WEIGHT_SUM);
        mContainer.setClickable(true);

        // configure text views
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
        );

        int textViewMargin = getResources().getDimensionPixelSize(
                R.dimen.card_detail_text_view_margin);
        int navyBlueColor = getResources().getColor(R.color.navy_blue);

        textViewParams.setMargins(0, 0, 0, textViewMargin);

        mFirstLine = new FontTextView(getContext());
        mSecondLine = new FontTextView(getContext());
        mThirdLine = new FontTextView(getContext());

        mFirstLine.setSingleLine(true);
        mFirstLine.setEllipsize(TextUtils.TruncateAt.END);
        mFirstLine.setFont(getResources().getString(R.string.regular_font_name));
        mFirstLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, BIG_TEXT_SIZE);

        mSecondLine.setSingleLine(true);
        mSecondLine.setEllipsize(TextUtils.TruncateAt.END);
        mSecondLine.setFont(getResources().getString(R.string.light_font_name));
        mSecondLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, SMALL_TEXT_SIZE);
        mSecondLine.setTextColor(navyBlueColor);

        mThirdLine.setSingleLine(true);
        mThirdLine.setEllipsize(TextUtils.TruncateAt.END);
        mThirdLine.setFont(getResources().getString(R.string.light_font_name));
        mThirdLine.setTextSize(TypedValue.COMPLEX_UNIT_SP, SMALL_TEXT_SIZE);
        mThirdLine.setTextColor(navyBlueColor);

        // configure container with text views
        LinearLayout.LayoutParams textViewContainerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                TEXT_VIEW_CONTAINER_WEIGHT
        );
        LinearLayout textViewsContainer = new LinearLayout(getContext());
        textViewsContainer.setLayoutParams(textViewContainerParams);
        textViewsContainer.setOrientation(LinearLayout.VERTICAL);
        textViewsContainer.addView(mFirstLine, textViewParams);
        textViewsContainer.addView(mSecondLine);
        textViewsContainer.addView(mThirdLine, textViewParams);

        // configure image view
        LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT,
                IMAGE_VIEW_WEIGHT
        );
        imgViewParams.gravity = Gravity.CENTER_VERTICAL;

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_next);
        imageView.setLayoutParams(imgViewParams);

        // attach both text view container and image view to main container
        mContainer.addView(textViewsContainer);
        mContainer.addView(imageView);

        // attach main container to card view
        addView(mContainer);
    }

    public void setFirstLineText(CharSequence text) {
        mFirstLine.setText(text);
    }

    public void setSecondLineText(CharSequence text) {
        mSecondLine.setText(text);
    }

    public void setThirdLineText(CharSequence text) {
        mThirdLine.setText(text);
    }

    public void setOnClickListener(OnClickListener clickListener) {
        mContainer.setOnClickListener(clickListener);
    }

    public void show(boolean withAnim) {
        if (getVisibility() != View.VISIBLE) {
            if (withAnim) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.in_up);
                startAnimation(animation);
            }
            setVisibility(View.VISIBLE);
        }
    }

    public void hide(boolean withAnim) {
        if (getVisibility() == View.VISIBLE) {
            if (withAnim) {
                Animation animation = AnimationUtils.loadAnimation(getContext(), R.anim.out_down);
                startAnimation(animation);
            }
            setVisibility(View.INVISIBLE);
        }
    }
}
