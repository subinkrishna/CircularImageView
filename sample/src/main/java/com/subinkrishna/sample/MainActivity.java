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

public class MainActivity extends AppCompatActivity {

    /** Log tag */
    private static final String TAG = MainActivity.class.getSimpleName();

    private Toolbar mToolbar;
    private TextView mCodeTextView;
    private ViewGroup mImageContainer, mCodeContainer;
    private boolean mIsAnimating = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Reset to default theme once the Activity is "cold started"
        setTheme(R.style.AppTheme);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mImageContainer = (ViewGroup) findViewById(R.id.image_container);
        mCodeContainer = (ViewGroup) findViewById(R.id.code_container);

        // Set custom typeface to TextViews
        TextView title = (TextView) findViewById(R.id.toolbar_title);
        mCodeTextView = (TextView) findViewById(R.id.code);
        Typeface mono = Typeface.createFromAsset(getAssets(), "fonts/roboto/Mono-Regular.ttf");
        title.setTypeface(mono);
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
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

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

            anim.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    if (isCodeVisible) {
                        mCodeContainer.setVisibility(View.GONE);
                    }
                    mIsAnimating = false;
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });

            anim.start();
        }
    }

    private void setImageContainerBackground() {
        Resources res = getResources();
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.grid);
        int cellH = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 22, res.getDisplayMetrics());
        b = Bitmap.createScaledBitmap(b, cellH, cellH, true);

        BitmapDrawable bd = new BitmapDrawable(res, b);
        bd.setTileModeX(Shader.TileMode.REPEAT);
        bd.setTileModeY(Shader.TileMode.REPEAT);
        findViewById(R.id.image_container).setBackgroundDrawable(bd);
    }

    private void setToolbar() {
        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (null != mToolbar) {
            setSupportActionBar(mToolbar);
            getSupportActionBar().setTitle("");
        }
    }

}
