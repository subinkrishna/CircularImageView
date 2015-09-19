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

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

/**
 * Circular Image View.
 *
 * @author Subinkrishna Gopi
 */
public class CircularImageView extends ImageView {

    /** Log tag */
    private static final String TAG = CircularImageView.class.getSimpleName();

    /** Default colors */
    private static final int DEFAULT_BORDER_COLOR = 0xFFFFFFFF;
    private static final int DEFAULT_BACKGROUND_COLOR = 0xFFDDDDDD;
    private static final int DEFAULT_TEXT_COLOR = 0xFF000000;

    private Paint mBitmapPaint;
    private Paint mBorderPaint;
    private Paint mBackgroundPaint;
    private Paint mTextPaint;
    private int mWidth, mHeight, mRadius;

    // Configurations
    private int mBorderWidth;
    private int mBorderColor;
    private int mBackgroundColor;
    private int mTextColor;
    private String mText;
    private int mTextSize;

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

        if (null != t) {
            mBorderWidth = t.getDimensionPixelSize(R.styleable.CircularImageView_borderWidth, 0);
            mBorderColor = t.getColor(R.styleable.CircularImageView_borderColor,
                    DEFAULT_BORDER_COLOR);
            mBackgroundColor = t.getColor(R.styleable.CircularImageView_placeholderBackgroundColor,
                    DEFAULT_BACKGROUND_COLOR);
            mText = t.getString(R.styleable.CircularImageView_placeholderText);
            mTextColor = t.getColor(R.styleable.CircularImageView_placeholderTextColor,
                    DEFAULT_TEXT_COLOR);
            mTextSize = t.getDimensionPixelSize(R.styleable.CircularImageView_placeholderTextSize, 0);

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
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mRadius = Math.min(w, h) / 2;
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
            return formattedText.substring(0, Math.min(2, length)).toUpperCase();
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
}
