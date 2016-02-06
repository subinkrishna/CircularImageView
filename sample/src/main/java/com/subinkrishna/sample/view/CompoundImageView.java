package com.subinkrishna.sample.view;

import android.content.Context;
import android.support.annotation.ColorInt;
import android.support.annotation.StringRes;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.Checkable;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.subinkrishna.sample.R;
/*
 * Copyright (C) 2016 Subinkrishna Gopi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * @author Subinkrishna Gopi
 */
public class CompoundImageView
        extends FrameLayout
        implements Checkable {

    @Bind(R.id.text) TextView mText;
    @Bind(R.id.image) ImageView mImage;
    @Bind(R.id.check) ImageView mCheckIcon;
    @Bind(R.id.cloud) ImageView mCloudIcon;

    private boolean mIsChecked = false;

    public CompoundImageView(Context context) {
        super(context);
        configure(context);
    }

    public CompoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        configure(context);
    }

    public CompoundImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        configure(context);
    }

    private void configure(Context context) {
        LayoutInflater.from(context).inflate(R.layout.item_compound_image_view, this, true);
        ButterKnife.bind(this, this);
    }

    public void recycle(int imageRes,
                        @StringRes int textRes,
                        @ColorInt int textBackgroundColor,
                        boolean isSelected,
                        boolean isFromCloud) {
        if (imageRes != -1) {
            mImage.setImageResource(imageRes);
            mText.setVisibility(GONE);
        } else {
            mImage.setImageDrawable(null);
            mText.setText(textRes);
            mText.setBackgroundColor(textBackgroundColor);
            mText.setVisibility(VISIBLE);
        }

        mCloudIcon.setVisibility(isFromCloud ? VISIBLE : GONE);
        setChecked(isSelected);
    }

    @Override
    public void setChecked(boolean checked) {
        if (mIsChecked != checked) {
            mIsChecked = checked;
            mCheckIcon.setVisibility(mIsChecked ? VISIBLE : GONE);
        }
    }

    @Override
    public boolean isChecked() {
        return mIsChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mIsChecked);
    }
}
