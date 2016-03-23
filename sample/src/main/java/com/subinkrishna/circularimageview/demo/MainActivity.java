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

package com.subinkrishna.circularimageview.demo;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.MemoryPolicy;
import com.squareup.picasso.Picasso;
import com.subinkrishna.circularimageview.demo.view.CompoundImageView;
import com.subinkrishna.widget.CircularImageView;

import butterknife.Bind;
import butterknife.BindDimen;
import butterknife.ButterKnife;

public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener {

    /** Log tag */
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbar_title) TextView mTitleTextView;
    @Bind(R.id.code_container) ScrollView mCodeContainer;
    @Bind(R.id.code_xml) TextView mXmlCodeTextView;
    @Bind(R.id.code_java) TextView mJavaCodeTextView;
    @Bind(R.id.image_container) ViewGroup mImageContainer;
    @Bind(R.id.civ) CircularImageView mImage;
    @Bind(R.id.fab) FloatingActionButton mFab;
    @Bind(R.id.enable_shadow) CheckBox mEnableShadowCheckBox;
    @Bind(R.id.enable_border) CheckBox mEnableBorderCheckBox;

    @BindDimen(R.dimen.civ_size) int mCivSize;
    @BindDimen(R.dimen.reveal_offset) int mRevealOffset;

    // Thumbnails
    @Bind(R.id.thumbnail_1) CompoundImageView mThumbnail1;
    @Bind(R.id.thumbnail_2) CompoundImageView mThumbnail2;
    @Bind(R.id.thumbnail_from_cloud) CompoundImageView mThumbnail3;
    @Bind(R.id.thumbnail_no_image) CompoundImageView mThumbnail4;

    private boolean mIsAnimating = false;
    private CompoundImageView mSelectedThumbnail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset to default theme once the Activity is "cold started"
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Log.d(TAG, "Offset: " + mRevealOffset);

        Typeface mono = Typeface.createFromAsset(getAssets(), "fonts/roboto/Mono-Regular.ttf");
        //mTitleTextView.setTypeface(mono);
        //mJavaCodeTextView.setTypeface(mono);
        //mXmlCodeTextView.setTypeface(mono);

        // Init Picasso
        Picasso.with(this).setLoggingEnabled(true);

        mImage.allowCheckStateShadow(true);
        mEnableShadowCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mImage.setShadowRadius(isChecked ? 5.0f : 0.0f);
            }
        });

        mEnableBorderCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView,
                                         boolean isChecked) {
                mImage.setBorderWidth(TypedValue.COMPLEX_UNIT_DIP, isChecked ? 5 : 0);
            }
        });

        setupThumbnails();
        setToolbar();
        setupImageContainer();
        updateImage(mThumbnail1);

        // Toggle CircularImageView
        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CircularImageView) v).toggle();
            }
        });

        mFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "Animate!");
                if (!mIsAnimating) toggleCode();
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (!mIsAnimating) {
            if (mCodeContainer.getVisibility() == View.VISIBLE) {
                toggleCode();
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressLint("NewApi")
    private void toggleCode() {
        final boolean isCodeVisible = mCodeContainer.getVisibility() == View.VISIBLE;
        int w = mImageContainer.getWidth(),
            h = mImageContainer.getHeight();

        mIsAnimating = true;

        // Reveal only for SDK version 21 or above
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            float startRadius = isCodeVisible ? (float) w * 1.5f : 0;
            float endRadius = isCodeVisible ? 0 : (float) w * 1.5f;
            Animator anim = ViewAnimationUtils.createCircularReveal(mCodeContainer,
                    w - mRevealOffset,
                    h - mRevealOffset,
                    startRadius,
                    endRadius);
            anim.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    onCodeContainerAnimationEnd(isCodeVisible);
                }
            });

            if (!isCodeVisible) {
                mCodeContainer.setVisibility(View.VISIBLE);
            }

            anim.start();
        }

        // Else slide
        else {
            if (!isCodeVisible) {
                mCodeContainer.setTranslationY(h);
                mCodeContainer.setVisibility(View.VISIBLE);
            }

            int startY = isCodeVisible ? 0 : h;
            int endY = isCodeVisible ? h : 0;
            ObjectAnimator anim = ObjectAnimator.ofFloat(mCodeContainer, "translationY", startY, endY);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    onCodeContainerAnimationEnd(isCodeVisible);
                }
            });

            anim.start();
        }
    }

    /**
     * Updates the view on animation end.
     *
     * @param isCodeVisible
     */
    private void onCodeContainerAnimationEnd(boolean isCodeVisible) {
        if (isCodeVisible) {
            mCodeContainer.setVisibility(View.GONE);
            mFab.setImageResource(R.drawable.ic_code_black_24dp);
            mCodeContainer.scrollTo(0, 0);
        } else {
            mFab.setImageResource(R.drawable.ic_close_black_24dp);
        }

        mIsAnimating = false;
    }

    /**
     * Setup thumbnails
     */
    private void setupThumbnails() {
        int defaultTextBgColor = 0xFFAAAAAA;
        int textBgColorForNoImage = 0xFF87170B;

        mThumbnail1.recycle(R.drawable.c1, R.string.image1, defaultTextBgColor, true, false);
        mThumbnail2.recycle(R.drawable.c2, R.string.image2, defaultTextBgColor, false, false);
        mThumbnail3.recycle(R.drawable.c3, R.string.image_from_cloud, defaultTextBgColor, false, true);
        mThumbnail4.recycle(-1, R.string.no_image, textBgColorForNoImage, false, false);

        mThumbnail1.setOnClickListener(this);
        mThumbnail2.setOnClickListener(this);
        mThumbnail3.setOnClickListener(this);
        mThumbnail4.setOnClickListener(this);
    }

    private void setToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
        }
    }

    /**
     * Setup image container
     */
    private void setupImageContainer() {
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grid);
        int cellH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, res.getDisplayMetrics());
        bitmap = Bitmap.createScaledBitmap(bitmap, cellH, cellH, true);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(res, bitmap);
        bitmapDrawable.setTileModeX(Shader.TileMode.REPEAT);
        bitmapDrawable.setTileModeY(Shader.TileMode.REPEAT);
        mImageContainer.setBackgroundDrawable(bitmapDrawable);
    }

    /**
     * Updates image (CircularImageView) on selecting a thumbnail
     *
     * @param v
     */
    private void updateImage(CompoundImageView v) {
        if (v == mSelectedThumbnail)
            return;

        if (null != mSelectedThumbnail) {
            mSelectedThumbnail.setChecked(false);
        }

        mImage.setChecked(false);
        mSelectedThumbnail = v;
        mSelectedThumbnail.setChecked(true);

        switch (mSelectedThumbnail.getId()) {
            case R.id.thumbnail_1:
                mImage.setImageResource(R.drawable.c1);
                updateCode(getString(R.string.java_code_template, "R.drawable.c1"));
                break;
            case R.id.thumbnail_2:
                mImage.setImageResource(R.drawable.c2);
                updateCode(getString(R.string.java_code_template, "R.drawable.c2"));
                break;
            case R.id.thumbnail_no_image:
                mImage.setImageDrawable(null);
                updateCode(getString(R.string.java_code_template, "null"));
                break;
            case R.id.thumbnail_from_cloud:
                Toast.makeText(this, R.string.loading_remote_image, Toast.LENGTH_SHORT).show();
                Picasso.with(this)
                        .load("https://raw.githubusercontent.com/subinkrishna/CircularImageView/master/art/cat_original.jpg")
                        .noFade()
                        .placeholder(R.drawable.placeholder)
                        .resize(mCivSize, mCivSize)
                        .memoryPolicy(MemoryPolicy.NO_CACHE)
                        .centerCrop()
                        .into(mImage);
                updateCode(getString(R.string.picasso_code_template));
                break;
        }
    }

    /**
     * Updates code
     *
     * @param java
     */
    private void updateCode(String java) {
        mJavaCodeTextView.setText(java);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.thumbnail_1:
            case R.id.thumbnail_2:
            case R.id.thumbnail_no_image:
            case R.id.thumbnail_from_cloud:
                updateImage((CompoundImageView) v);
                break;

        }
    }
}
