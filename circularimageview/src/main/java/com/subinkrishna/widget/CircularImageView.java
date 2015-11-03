/*
 * Copyright (C) 2015 Subinkrishna Gopi
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

package com.subinkrishna.widget;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.CallSuper;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.Checkable;
import android.widget.ImageView;

import java.util.Locale;

/**
 * Circular Image View.
 *
 * @author Subinkrishna Gopi
 */
public class CircularImageView
        extends ImageView
        implements Checkable {

    /** Log tag */
    private static final String TAG = CircularImageView.class.getSimpleName();

    /** Default colors */
    private static final int DEFAULT_BORDER_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFFDDDDDD;
    private static final int DEFAULT_TEXT_COLOR = 0xFF000000;
    private static final int DEFAULT_CHECKED_BACKGROUND_COLOR = 0xFFBBBBBB;
    private static final int DEFAULT_CHECK_STROKE_COLOR = 0xFFFFFFFF;

    private static final float DEFAULT_CHECK_STROKE_WIDTH_IN_DP = 3f;

    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private Paint mBackgroundPaint;
    private Paint mCheckMarkPaint;
    private Paint mCheckedBackgroundPaint;
    private Paint mTextPaint;
    private int mWidth, mHeight, mRadius;
    private int mCheckStrokeWidth, mLongStrokeHeight;
    private Path mPath = new Path();

    // Configurations
    private int mBorderWidth;
    private int mBorderColor;
    private int mBackgroundColor;
    private int mCheckedBackgroundColor;
    private int mTextColor;
    private String mText;
    private int mTextSize;
    private boolean mChecked;

    public CircularImageView(Context context) {
        super(context);
        init(context, null);
    }

    public CircularImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CircularImageView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context,
                      AttributeSet attrs) {
        TypedArray t = null;
        if (null != attrs) {
            t = context.getTheme().obtainStyledAttributes(attrs,
                    R.styleable.CircularImageView, 0, 0);
        }

        // Extract configurations
        mBorderColor = DEFAULT_BORDER_COLOR;
        mBackgroundColor = DEFAULT_BACKGROUND_COLOR;
        mCheckedBackgroundColor = DEFAULT_CHECKED_BACKGROUND_COLOR;

        if (null != t) {
            mBorderWidth = t.getDimensionPixelSize(R.styleable.CircularImageView_ci_borderWidth, 0);
            mBorderColor = t.getColor(R.styleable.CircularImageView_ci_borderColor,
                    DEFAULT_BORDER_COLOR);
            mBackgroundColor = t.getColor(R.styleable.CircularImageView_ci_placeholderBackgroundColor,
                    DEFAULT_BACKGROUND_COLOR);
            mText = t.getString(R.styleable.CircularImageView_ci_placeholderText);
            mTextColor = t.getColor(R.styleable.CircularImageView_ci_placeholderTextColor,
                    DEFAULT_TEXT_COLOR);
            mTextSize = t.getDimensionPixelSize(R.styleable.CircularImageView_ci_placeholderTextSize, 0);

            mChecked = t.getBoolean(R.styleable.CircularImageView_ci_checked, false);
            mCheckedBackgroundColor = t.getColor(R.styleable.CircularImageView_ci_checkedStateBackgroundColor,
                    DEFAULT_CHECKED_BACKGROUND_COLOR);

            t.recycle();
        }

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setBorderInternal(mBorderWidth, mBorderColor, false);
        setPlaceholderTextInternal(mText,
                mTextColor, mTextSize, false);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setAntiAlias(true);
        mBackgroundPaint.setColor(mBackgroundColor);
        mBackgroundPaint.setStyle(Paint.Style.FILL);

        mCheckedBackgroundPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCheckedBackgroundPaint.setColor(mCheckedBackgroundColor);
        mCheckedBackgroundPaint.setStyle(Paint.Style.FILL);

        mCheckStrokeWidth = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_CHECK_STROKE_WIDTH_IN_DP,
                getResources().getDisplayMetrics());

        mCheckMarkPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mCheckMarkPaint.setColor(DEFAULT_CHECK_STROKE_COLOR);
        mCheckMarkPaint.setStyle(Paint.Style.STROKE);
        mCheckMarkPaint.setStrokeWidth(mCheckStrokeWidth);
    }

    /**
     * Sets the border paint (and configs).
     *
     * @param rawSize
     * @param color
     * @param invalidate
     */
    private void setBorderInternal(int rawSize,
                                   @ColorInt int color,
                                   boolean invalidate) {
        mBorderWidth = rawSize;
        mBorderColor = color;

        if ((null == mBorderPaint) && (rawSize > 0)) {
            mBorderPaint = new Paint();
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStyle(Paint.Style.STROKE);
        }

        if (null != mBorderPaint) {
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth); // in pixels
        }

        // Invalidate the view if asked
        if (invalidate) {
            invalidate();
        }
    }

    /**
     * Sets placeholder text paint and configs.
     * @param text
     * @param color
     * @param textSize
     * @param invalidate
     */
    private void setPlaceholderTextInternal(String text,
                                            @ColorInt int color,
                                            int textSize,
                                            boolean invalidate) {
        // Takes only the first character as the place holder
        mText = formatPlaceholderText(text);
        mTextColor = color;
        mTextSize = textSize;

        if ((null == mTextPaint) &&
            (textSize > 0) &&
            !TextUtils.isEmpty(mText)) {
            mTextPaint = getTextPaint();
        }

        if (null != mTextPaint) {
            mTextPaint.setColor(color);
            mTextPaint.setTextSize(textSize);
        }

        // Invalidate the view if asked
        if (invalidate) {
            invalidate();
        }
    }

    @Override
    @CallSuper
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRadius = Math.min(w, h) / 2;

        // Check stroke
        mLongStrokeHeight = mRadius;

        if (null != mBitmapPaint) {
            updateBitmapShader();
        }
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (null != mBitmapPaint) {
            updateBitmapShader();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (null != mBitmapPaint) {
            updateBitmapShader();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (null != mBitmapPaint) {
            updateBitmapShader();
        }
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
        // NOTE: All other variations of ImageView#setColorFilter will
        // end up calling ImageView#setColorFilter(ColorFilter).
        if (null != mBitmapPaint) {
            mBitmapPaint.setColorFilter(cf);
        }
    }

    /**
     * Sets the border width.
     *
     * @param unit The desired dimension unit.
     * @param size
     */
    public final void setBorderWidth(int unit,
                                     int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Border width cannot be less than zero.");
        }

        int scaledSize = (int) TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics());
        setBorderInternal(scaledSize, mBorderColor, true);
    }

    /**
     * Sets the border color.
     *
     * @param color
     */
    public final void setBorderColor(@ColorInt int color) {
        if (color != mBorderColor) {
            setBorderInternal(mWidth, color, true);
        }
    }

    /**
     * Sets the check state background color.
     *
     * @param backgroundColor
     */
    public final void setCheckedStateBackgroundColor(@ColorInt int backgroundColor) {
        if ((backgroundColor != mCheckedBackgroundColor) &&
            (null != mCheckedBackgroundPaint)) {
            mCheckedBackgroundPaint.setColor(backgroundColor);
            if (isChecked()) {
                invalidate();
            }
        }

        mCheckedBackgroundColor = backgroundColor;
    }

    /**
     * Sets the placeholder text.
     *
     * @param text
     */
    public final void setPlaceholder(String text) {
        if (!text.equalsIgnoreCase(mText)) {
            setPlaceholderTextInternal(text, mTextColor, mTextSize, true);
        }
    }

    /**
     * Sets the placeholder text size.
     *
     * @param unit The desired dimension unit.
     * @param size
     */
    public final void setPlaceholderTextSize(int unit,
                                             int size) {
        if (size < 0) {
            throw new IllegalArgumentException("Text size cannot be less than zero.");
        }

        int scaledSize = (int) TypedValue.applyDimension(unit, size, getResources().getDisplayMetrics());
        setPlaceholderTextInternal(mText, mTextColor, scaledSize, true);
    }

    /**
     * Set the placeholder text and colors.
     *
     * @param text
     * @param backgroundColor
     * @param textColor
     */
    public final void setPlaceholder(String text,
                                     @ColorInt int backgroundColor,
                                     @ColorInt int textColor) {
        boolean invalidate = false;
        // Set the placeholder background color
        if (backgroundColor != mBackgroundColor) {
            mBackgroundColor = backgroundColor;
            if (null != mBackgroundPaint) {
                mBackgroundPaint.setColor(backgroundColor);
                invalidate = true;
            }
        }
        // Set the placeholder text color
        if (!text.equalsIgnoreCase(mText) ||
            (backgroundColor != mBackgroundColor) ||
            (textColor != mTextColor)) {
            setPlaceholderTextInternal(text, textColor, mTextSize, false);
            invalidate = true;
        }

        if (invalidate) {
            invalidate();
        }
    }

    /**
     * Default implementation of the placeholder text formatting.
     *
     * @param text
     * @return
     */
    protected String formatPlaceholderText(String text) {
        String formattedText = (null != text) ? text.trim() : null;
        int length = (null != formattedText) ? formattedText.length() : 0;
        if (length > 0) {
            return formattedText.substring(0, Math.min(2, length)).toUpperCase(Locale.getDefault());
        }
        return null;
    }

    /**
     * Default implementation of text {@code Paint} creation.
     *
     * @return
     */
    protected Paint getTextPaint() {
        Paint textPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        textPaint.setStyle(Paint.Style.FILL);
        textPaint.setTypeface(Typeface.DEFAULT);
        textPaint.setTextAlign(Paint.Align.CENTER);
        return textPaint;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = mWidth / 2;
        int y = mHeight / 2;

        // Is checked?
        if (mChecked) {
            drawCheckedState(canvas, mWidth, mHeight);
        }
        else {
            if (null != getDrawable()) {
                // Draws the bitmap if available
                canvas.drawCircle(x, y, mRadius - mBorderWidth, mBitmapPaint);
            } else {
                // Placeholder background
                canvas.drawCircle(x, y, mRadius - mBorderWidth, mBackgroundPaint);
                // Placeholder character
                if ((null != mTextPaint) && !TextUtils.isEmpty(mText)) {
                    int ty = (int) ((mHeight - (mTextPaint.ascent() + mTextPaint.descent())) * 0.5f);
                    canvas.drawText(mText, x, ty, mTextPaint);
                }
            }

            // Draw the border
            if ((null != mBorderPaint) && (mBorderWidth > 0)) {
                canvas.drawCircle(x, y, mRadius - (mBorderWidth * 0.5f), mBorderPaint);
            }
        }
    }

    /**
     * Draws the checked state.
     *
     * @param canvas
     * @param w
     * @param h
     */
    protected void drawCheckedState(Canvas canvas, int w, int h) {
        int x = w / 2;
        int y = h / 2;

        canvas.drawCircle(x, y, mRadius, mCheckedBackgroundPaint);
        canvas.save();

        int shortStrokeHeight = (int) (mLongStrokeHeight * .4f);
        int halfH = (int) (mLongStrokeHeight * .5f);
        int offset = (int)(shortStrokeHeight * .3f);
        int sx = x + offset;
        int sy = y - offset;
        mPath.reset();
        mPath.moveTo(sx, sy - halfH);
        mPath.lineTo(sx, sy + halfH); // draw long stroke
        mPath.moveTo(sx + (mCheckStrokeWidth * .5f), sy + halfH);
        mPath.lineTo(sx - shortStrokeHeight, sy + halfH); // draw short stroke

        // Rotates the canvas to draw an angled check mark
        canvas.rotate(45f, x, y);
        canvas.drawPath(mPath, mCheckMarkPaint);
        // Restore the canvas to previously saved state
        canvas.restore();
    }

    /**
     * Updates BitmapShader to draw the bitmap in CENTER_CROP mode.
     */
    private void updateBitmapShader() {
        Drawable drawable = getDrawable();
        Bitmap bitmap = null;
        if ((null != drawable) && (drawable instanceof BitmapDrawable)) {
            bitmap = ((BitmapDrawable) drawable).getBitmap();
        }

        // Clear the shader & abort is the new bitmap is null
        if (null == bitmap) {
            mBitmapPaint.setShader(null);
            return;
        }

        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        float x = 0, y = 0;
        int diameter = mRadius * 2;
        float scale = (float) (diameter - mBorderWidth) / (float) Math.min(bitmapHeight, bitmapWidth);

        x = (mWidth - bitmapWidth * scale) * 0.5f;
        y = (mHeight - bitmapHeight * scale) * 0.5f;

        // Apply scale and translation to matrix
        Matrix matrix = new Matrix();
        matrix.setScale(scale, scale);
        matrix.postTranslate(Math.round(x), Math.round(y));

        // Create the BitmapShader and apply the Matrix
        BitmapShader shader = new BitmapShader(bitmap,
                Shader.TileMode.CLAMP,
                Shader.TileMode.CLAMP);
        shader.setLocalMatrix(matrix);
        mBitmapPaint.setShader(shader);
    }

    @Override
    public void setChecked(boolean checked) {
        // TODO: Add configurations to enable animations, duration (?), stroke color & width
        final int duration = 150;
        final Interpolator interpolator = new DecelerateInterpolator();
        ObjectAnimator animation = ObjectAnimator.ofFloat(this, "scaleX", 1f, 0f);
        animation.setDuration(duration);
        animation.setInterpolator(interpolator);
        animation.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                mChecked = !mChecked;
                invalidate();
                ObjectAnimator reverse = ObjectAnimator.ofFloat(CircularImageView.this, "scaleX", 0f, 1f);
                reverse.setDuration(duration);
                reverse.setInterpolator(interpolator);
                reverse.start();
            }
        });
        animation.start();
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void toggle() {
        setChecked(!mChecked);
    }
}
