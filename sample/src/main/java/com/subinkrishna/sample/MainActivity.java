package com.subinkrishna.sample;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.subinkrishna.widget.CircularImageView;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    /** Log tag */
    private static final String TAG = MainActivity.class.getSimpleName();

    @Bind(R.id.toolbar) Toolbar mToolbar;
    @Bind(R.id.toolbar_title) TextView mTitleTextView;
    @Bind(R.id.code_container) ViewGroup mCodeContainer;
    @Bind(R.id.code) TextView mCodeTextView;
    @Bind(R.id.image_container) ViewGroup mImageContainer;

    private boolean mIsAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset to default theme once the Activity is "cold started"
        setTheme(R.style.AppTheme);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Typeface mono = Typeface.createFromAsset(getAssets(), "fonts/roboto/Mono-Regular.ttf");
        mTitleTextView.setTypeface(mono);
        mCodeTextView.setTypeface(mono);

        setToolbar();
        setImageContainerBackground();

        findViewById(R.id.civ).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((CircularImageView) v).toggle();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_settings:
                return true;
            case R.id.action_code:
                if (!mIsAnimating) toggleCode();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("NewApi")
    private void toggleCode() {
        final boolean isCodeVisible = mCodeContainer.getVisibility() == View.VISIBLE;
        int w = mImageContainer.getWidth(),
            h = mImageContainer.getHeight();

        mIsAnimating = true;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            // Reveal only for SDK version 21 or above
            float startRadius = isCodeVisible ? (float) w * 1.3f : 0;
            float endRadius = isCodeVisible ? 0 : (float) w * 1.3f;
            Animator anim = ViewAnimationUtils.createCircularReveal(mCodeContainer,
                    w, 0, startRadius, endRadius);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    if (isCodeVisible) {
                        mCodeContainer.setVisibility(View.GONE);
                    }
                    mIsAnimating = false;
                }
            });

            if (!isCodeVisible) {
                mCodeContainer.setVisibility(View.VISIBLE);
            }

            anim.start();
        } else {
            // Else slide
            if (!isCodeVisible) {
                mCodeContainer.setTranslationY(h);
                mCodeContainer.setVisibility(View.VISIBLE);
            }

            int startY = isCodeVisible ? 0 : h;
            int endY = isCodeVisible ? h : 0;
            ObjectAnimator anim = ObjectAnimator.ofFloat(mCodeContainer, "translationY", startY, endY);
            anim.setInterpolator(new DecelerateInterpolator());
            anim.setDuration(getResources().getInteger(android.R.integer.config_mediumAnimTime));

            anim.addListener(new AnimatorListenerStub() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isCodeVisible) {
                        mCodeContainer.setVisibility(View.GONE);
                    }
                    mIsAnimating = false;
                }
            });

            anim.start();
        }
    }

    private void setImageContainerBackground() {
        Resources res = getResources();
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.grid);
        int cellH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, res.getDisplayMetrics());
        bitmap = Bitmap.createScaledBitmap(bitmap, cellH, cellH, true);

        BitmapDrawable bitmapDrawable = new BitmapDrawable(res, bitmap);
        bitmapDrawable.setTileModeX(Shader.TileMode.REPEAT);
        bitmapDrawable.setTileModeY(Shader.TileMode.REPEAT);
        mImageContainer.setBackgroundDrawable(bitmapDrawable);
    }

    private void setToolbar() {
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
        }
    }

}
