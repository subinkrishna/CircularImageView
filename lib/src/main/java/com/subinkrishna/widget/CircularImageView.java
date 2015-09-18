package com.subinkrishna.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
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
import android.widget.ImageView;

/**
 * @author Subinkrishna Gopi
 */
public class CircularImageView extends ImageView {

    /** Log tag */
    private static final String TAG = CircularImageView.class.getSimpleName();

    /** Default colors */
    @ColorInt private static final int DEFAULT_BORDER_COLOR = 0xFFFFFFFF;
    @ColorInt private static final int DEFAULT_BACKGROUND_COLOR = 0xFFDDDDDD;
    @ColorInt private static final int DEFAULT_PLACEHOLDER_TEXT_COLOR = 0xFF000000;

    private Paint mBorderPaint;
    private Paint mBitmapPaint;
    private Paint mPlaceHolderBgPaint;
    private Paint mPlaceHolderTextPaint;
    private int mWidth, mHeight, mRadius;

    // Configurations
    private int mBorderWidth;
    @ColorInt private int mBorderColor;
    @ColorInt private int mPlaceHolderBgColor;
    @ColorInt private int mPlaceHolderTextColor;
    private String mPlaceHolderText;
    private int mPlaceHolderTextSize;

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
            mPlaceHolderText = t.getString(R.styleable.CircularImageView_placeholderCharacter);
            mPlaceHolderTextColor = t.getColor(R.styleable.CircularImageView_placeholderCharacterColor,
                    DEFAULT_PLACEHOLDER_TEXT_COLOR);
            mPlaceHolderTextSize = t.getDimensionPixelSize(R.styleable.CircularImageView_placeholderCharacterSize, 0);

            t.recycle();
        }

        mBitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        setBorderInternal(mBorderWidth, mBorderColor, false);
        setPlaceHolderTextInternal(mPlaceHolderText,
                mPlaceHolderTextColor, mPlaceHolderTextSize, false);

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

        if ((null == mBorderPaint) && (width > 0)) {
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
     *
     * @param text
     * @param color
     * @param size
     * @param invalidate
     */
    private void setPlaceHolderTextInternal(String text,
                                            @ColorInt int color,
                                            int size,
                                            boolean invalidate) {
        // Takes only the first character as the place holder
        mPlaceHolderText = (null != text) ? text.trim() : null;
        mPlaceHolderText = !TextUtils.isEmpty(mPlaceHolderText)
                ? String.valueOf(mPlaceHolderText.charAt(0)) : null;
        mPlaceHolderTextColor = color;
        mPlaceHolderTextSize = size;

        if ((null == mPlaceHolderTextPaint) &&
            (size > 0) &&
            !TextUtils.isEmpty(text)) {
            mPlaceHolderTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
            mPlaceHolderTextPaint.setStyle(Paint.Style.FILL);
            mPlaceHolderTextPaint.setTypeface(Typeface.DEFAULT);
            mPlaceHolderTextPaint.setTextAlign(Paint.Align.CENTER);
        }

        if (null != mPlaceHolderTextPaint) {
            mPlaceHolderTextPaint.setColor(color);
            mPlaceHolderTextPaint.setTextSize(size);
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
        update();
    }

    @Override
    public void setImageDrawable(Drawable drawable) {
        super.setImageDrawable(drawable);
        if (null != mBitmapPaint) {
            update();
        }
    }

    @Override
    public void setImageResource(int resId) {
        super.setImageResource(resId);
        if (null != mBitmapPaint) {
            update();
        }
    }

    @Override
    public void setImageURI(Uri uri) {
        super.setImageURI(uri);
        if (null != mBitmapPaint) {
            update();
        }
    }

    /**
     * Sets the border width.
     *
     * @param widthInPixels
     */
    public void setBorderWidth(int widthInPixels) {
        if (widthInPixels < 0) {
            throw new IllegalArgumentException("Border width cannot be less than zero.");
        }

        if (widthInPixels != mBorderWidth) {
            setBorderInternal(widthInPixels, mBorderColor, true);
        }
    }

    /**
     * Sets the border color.
     *
     * @param color
     */
    public void setBorderColor(@ColorInt int color) {
        if (color != mBorderColor) {
            setBorderInternal(mWidth, color, true);
        }
    }

    public void setPlaceholder(char character) {
        String str = String.valueOf(character);
        if (!str.equalsIgnoreCase(mPlaceHolderText)) {
            setPlaceHolderTextInternal(str, mPlaceHolderTextColor, mPlaceHolderTextSize, true);
        }
    }

    public void setPlaceholder(char character,
                               @ColorInt int backgroundColor,
                               @ColorInt int textColor) {
        boolean invalidate = false;
        // Set the placeholder background color
        if (backgroundColor != mPlaceHolderBgColor) {
            mPlaceHolderBgColor = backgroundColor;
            if (null != mPlaceHolderBgPaint) {
                mPlaceHolderBgPaint.setColor(backgroundColor);
                invalidate = true;
            }
        }
        // Set the placeholder text color
        String str = String.valueOf(character);
        if (!str.equalsIgnoreCase(mPlaceHolderText) ||
            (backgroundColor != mPlaceHolderBgColor) ||
            (textColor != mPlaceHolderTextColor)) {
            setPlaceHolderTextInternal(str, textColor, mPlaceHolderTextSize, false);
            invalidate = true;
        }

        if (invalidate) {
            invalidate();
        }
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
            canvas.drawCircle(x, y, mRadius - mBorderWidth, mPlaceHolderBgPaint);
            // Placeholder character
            if ((null != mPlaceHolderTextPaint) && !TextUtils.isEmpty(mPlaceHolderText)) {
                int ty = (int) ((mHeight - (mPlaceHolderTextPaint.ascent() + mPlaceHolderTextPaint.descent())) * 0.5f);
                canvas.drawText(mPlaceHolderText, x, ty, mPlaceHolderTextPaint);
            }
        }

        // Draw the border
        if ((null != mBorderPaint) && (mBorderWidth > 0)) {
            canvas.drawCircle(x, y, mRadius - (mBorderWidth * 0.5f), mBorderPaint);
        }
    }

    private void update() {
        updateBitmapShader();
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
