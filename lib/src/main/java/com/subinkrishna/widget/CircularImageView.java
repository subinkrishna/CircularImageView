package com.subinkrishna.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Shader;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.support.annotation.ColorInt;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

/**
 * @author Subinkrishna Gopi
 */
public class CircularImageView extends ImageView {

    /** Log tag */
    private static final String TAG = CircularImageView.class.getSimpleName();

    @ColorInt private static final int DEFAULT_BORDER_COLOR = 0xFFFFFFFF;
    @ColorInt private static final int DEFAULT_BACKGROUND_COLOR = 0xFFDDDDDD;

    private Paint mBorderPaint;
    private Paint mBitmapPaint;
    private Paint mPlaceHolderBgPaint;
    private int mWidth, mHeight, mRadius;

    // Configurations
    private int mBorderWidth;
    @ColorInt private int mBorderColor;
    @ColorInt private int mPlaceHolderBgColor;
    private int mLabelTextSize;

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
        mPlaceHolderBgColor = DEFAULT_BACKGROUND_COLOR;

        if (null != t) {
            mBorderWidth = t.getDimensionPixelSize(R.styleable.CircularImageView_borderWidth, 0);
            mBorderColor = t.getColor(R.styleable.CircularImageView_borderColor,
                    DEFAULT_BORDER_COLOR);
            mPlaceHolderBgColor = t.getColor(R.styleable.CircularImageView_placeholderBackgroundColor,
                    DEFAULT_BACKGROUND_COLOR);
        }

        // Bitmap
        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        // Border
        setBorderInternal(mBorderWidth, mBorderColor, false);

        // Placeholder background
        mPlaceHolderBgPaint = new Paint();
        mPlaceHolderBgPaint.setAntiAlias(true);
        mPlaceHolderBgPaint.setColor(mPlaceHolderBgColor);
        mPlaceHolderBgPaint.setStyle(Paint.Style.FILL);
    }

    /**
     * Sets the border paint (and configs).
     *
     * @param width
     * @param color
     * @param invalidate
     */
    private void setBorderInternal(int width,
                                   @ColorInt int color,
                                   boolean invalidate) {
        mBorderWidth = width;
        mBorderColor = color;

        // Create a new paint only if the width is greater than 0
        if ((null == mBorderPaint) && (width > 0)) {
            Log.d(TAG, "new border paint");
            mBorderPaint = new Paint();
            mBorderPaint.setAntiAlias(true);
            mBorderPaint.setStyle(Paint.Style.STROKE);
        }

        // Set the new config
        if (null != mBorderPaint) {
            mBorderPaint.setColor(mBorderColor);
            mBorderPaint.setStrokeWidth(mBorderWidth); // in pixels
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
        updateShader();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (null != mBitmapPaint) {
            updateShader();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (null != mBitmapPaint) {
            updateShader();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (null != mBitmapPaint) {
            updateShader();
        }
    }

    /**
     * Sets the border width.
     *
     * @param widthInPixels
     */
    public void setBorderWidth(int widthInPixels) {
        if (widthInPixels <= 0) {
            throw new IllegalArgumentException("Border width cannot be less than zero.");
        }
        setBorderInternal(widthInPixels, mBorderColor, true);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int x = mWidth / 2;
        int y = mHeight / 2;

        if (null != getDrawable()) {
            // Draws the bitmap if available
            canvas.drawCircle(x, y, mRadius - mBorderWidth, mBitmapPaint);
        } else {
            // Draw the place holder if no drawable
            // Label character

            // Background
            canvas.drawCircle(x, y, mRadius - mBorderWidth, mPlaceHolderBgPaint);
        }

        // Draw the border
        if ((null != mBorderPaint) && (mBorderWidth > 0)) {
            canvas.drawCircle(x, y, mRadius - (mBorderWidth * 0.5f), mBorderPaint);
        }
    }

    /**
     * Updates BitmapShader to draw the bitmap in CENTER_CROP mode.
     */
    private void updateShader() {
        Log.d(TAG, "Update shader");

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
