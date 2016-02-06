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

package com.subinkrishna.customviews.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.TypedValue;

import com.subinkrishna.widget.CircularImageView;

/**
 * Extension of CircularImageView that overrides checkmark color, width
 * and animation.
 *
 * @author Subinkrishna Gopi
 */
public class AvatarView extends CircularImageView {

    public AvatarView(Context context) {
        this(context, null, 0);
    }

    public AvatarView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AvatarView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        // Disabling check state animations all together
        allowCheckStateAnimation(false);
    }

    @Override
    protected boolean shouldDrawBorder() {
        // Draw border only when no drawable is set
        return null != getDrawable();
    }

    @Override
    protected Paint getCheckMarkPaint() {
        Paint paint = super.getCheckMarkPaint();
        // Overriding stroke color
        paint.setColor(Color.YELLOW);
        return paint;
    }

    @Override
    protected int getCheckMarkStrokeWidthInPixels() {
        // Overriding stroke width to 5dp (default is 3dp)
        return (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                5, // DIPs
                getResources().getDisplayMetrics());
    }

}
