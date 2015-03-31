/*
 * Copyright (c) 2015, University of Oslo
 *
 * All rights reserved.
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 * Redistributions of source code must retain the above copyright notice, this
 * list of conditions and the following disclaimer.
 *
 * Redistributions in binary form must reproduce the above copyright notice,
 * this list of conditions and the following disclaimer in the documentation
 * and/or other materials provided with the distribution.
 * Neither the name of the HISP project nor the names of its contributors may
 * be used to endorse or promote products derived from this software without
 * specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

package org.dhis2.mobile.ui.views;

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

import org.dhis2.mobile.R;

public class CardDetailedButton extends CardView {
    private static final float WEIGHT_SUM = 1.0f;
    private static final float TEXT_VIEW_CONTAINER_WEIGHT = 0.95f;
    private static final float ADDITIONAL_VIEW_WEIGHT = 0.05f;

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

        mFirstLine = initTextView(R.string.regular_font_name, BIG_TEXT_SIZE, true);
        mSecondLine = initTextView(R.string.light_font_name, SMALL_TEXT_SIZE, false);
        mThirdLine = initTextView(R.string.light_font_name, SMALL_TEXT_SIZE, true);
        mContainer = initContainer();

        LinearLayout textViewsContainer = initTextViewContainer();
        textViewsContainer.addView(mFirstLine);
        textViewsContainer.addView(mSecondLine);
        textViewsContainer.addView(mThirdLine);

        View view = initImageView();
        if (view != null) {
            LinearLayout.LayoutParams viewParams = (LinearLayout.LayoutParams)
                    view.getLayoutParams();
            viewParams.weight = ADDITIONAL_VIEW_WEIGHT;
        }

        // attach both text view container and image view to main container
        mContainer.addView(textViewsContainer);
        if (view != null) {
            mContainer.addView(view);
        }

        // attach main container to card view
        addView(mContainer);
    }

    private LinearLayout initContainer() {
        // configure container with contents
        ViewGroup.LayoutParams containerParams = new ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.MATCH_PARENT
        );
        int containerPadding = getResources().getDimensionPixelSize(
                R.dimen.card_detail_button_padding);

        LinearLayout container = new LinearLayout(getContext());
        container.setId(getId());
        container.setBackgroundResource(R.drawable.transparent_selector);
        container.setLayoutParams(containerParams);
        container.setOrientation(LinearLayout.HORIZONTAL);
        container.setPadding(containerPadding, containerPadding,
                containerPadding, containerPadding);
        container.setWeightSum(WEIGHT_SUM);
        container.setClickable(true);

        return container;
    }

    private FontTextView initTextView(int fontId, int textSize, boolean margin) {
        int color = getResources().getColor(R.color.grey);

        FontTextView textView = new FontTextView(getContext());
        textView.setSingleLine(true);
        textView.setEllipsize(TextUtils.TruncateAt.END);
        textView.setFont(getResources().getString(fontId));
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, textSize);
        textView.setTextColor(color);

        if (margin) {
            LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT
            );
            int textViewMargin = getResources().getDimensionPixelSize(
                    R.dimen.card_detail_text_view_margin);
            textViewParams.setMargins(0, 0, 0, textViewMargin);
            textView.setLayoutParams(textViewParams);
        }

        return textView;
    }

    private LinearLayout initTextViewContainer() {
        // configure container with text views
        LinearLayout.LayoutParams textViewContainerParams = new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT,
                TEXT_VIEW_CONTAINER_WEIGHT
        );
        LinearLayout textViewsContainer = new LinearLayout(getContext());
        textViewsContainer.setLayoutParams(textViewContainerParams);
        textViewsContainer.setOrientation(LinearLayout.VERTICAL);
        return textViewsContainer;
    }

    private ImageView initImageView() {
        // configure image view
        LinearLayout.LayoutParams imgViewParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        imgViewParams.gravity = Gravity.CENTER_VERTICAL;

        ImageView imageView = new ImageView(getContext());
        imageView.setImageResource(R.drawable.ic_next);
        imageView.setLayoutParams(imgViewParams);
        return imageView;
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

    @Override
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
