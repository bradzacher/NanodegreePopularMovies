/*
 * Copyright 2014 Chris Banes
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
/**
 * TAKEN FROM https://gist.github.com/chrisbanes/91ac8a20acfbdc410a68
 */
package au.com.zacher.popularmovies.activity.layout;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.view.ViewCompat;
import android.support.v7.widget.Toolbar;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;

import au.com.zacher.popularmovies.R;

public class CollapsingTitleLayout extends FrameLayout {

    // Pre-JB-MR2 doesn't support HW accelerated canvas scaled text so we will workaround it
    // by using our own texture
    private static final boolean USE_SCALING_TEXTURE = Build.VERSION.SDK_INT < 18;

    private static final boolean DEBUG_DRAW = false;
    private static final Paint DEBUG_DRAW_PAINT;
    static {
        //noinspection ConstantConditions
        DEBUG_DRAW_PAINT = DEBUG_DRAW ? new Paint() : null;
        //noinspection ConstantConditions
        if (DEBUG_DRAW_PAINT != null) {
            DEBUG_DRAW_PAINT.setAntiAlias(true);
            DEBUG_DRAW_PAINT.setColor(Color.MAGENTA);
        }
    }

    private Toolbar mToolbar;
    private View mDummyView;

    private float mScrollOffset;

    private final Rect mToolbarContentBounds;

    private float mExpandedMarginLeft;
    private float mExpandedMarginRight;
    private float mExpandedMarginBottom;

    private int mRequestedExpandedTitleTextSize;
    private int mExpandedTitleTextSize;
    private int mCollapsedTitleTextSize;

    private float mExpandedTop;
    private float mCollapsedTop;

    private String mTitle;
    private String mTitleToDraw;
    private boolean mUseTexture;
    private Bitmap mExpandedTitleTexture;

    private float mTextLeft;
    private float mTextRight;
    private float mTextTop;

    private float mScale;

    private final TextPaint mTextPaint;
    private Paint mTexturePaint;

    private Interpolator mTextSizeInterpolator;

    public CollapsingTitleLayout(Context context) {
        this(context, null);
    }

    public CollapsingTitleLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CollapsingTitleLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        this.mTextPaint = new TextPaint();
        this.mTextPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.CollapsingTitleLayout);

        this.mExpandedMarginLeft = this.mExpandedMarginRight = this.mExpandedMarginBottom = a.getDimensionPixelSize(R.styleable.CollapsingTitleLayout_expandedMargin, 0);

        final boolean isRtl = ViewCompat.getLayoutDirection(this)
                == ViewCompat.LAYOUT_DIRECTION_RTL;
        if (a.hasValue(R.styleable.CollapsingTitleLayout_expandedMarginStart)) {
            final int marginStart = a.getDimensionPixelSize(
                    R.styleable.CollapsingTitleLayout_expandedMarginStart, 0);
            if (isRtl) {
                this.mExpandedMarginRight = marginStart;
            } else {
                this.mExpandedMarginLeft = marginStart;
            }
        }
        if (a.hasValue(R.styleable.CollapsingTitleLayout_expandedMarginEnd)) {
            final int marginEnd = a.getDimensionPixelSize(
                    R.styleable.CollapsingTitleLayout_expandedMarginEnd, 0);
            if (isRtl) {
                this.mExpandedMarginLeft = marginEnd;
            } else {
                this.mExpandedMarginRight = marginEnd;
            }
        }
        if (a.hasValue(R.styleable.CollapsingTitleLayout_expandedMarginBottom)) {
            this.mExpandedMarginBottom = a.getDimensionPixelSize(
                    R.styleable.CollapsingTitleLayout_expandedMarginBottom, 0);
        }

        final int tp = a.getResourceId(R.styleable.CollapsingTitleLayout_android_textAppearance,
                android.R.style.TextAppearance);
        this.setTextAppearance(tp);

        if (a.hasValue(R.styleable.CollapsingTitleLayout_collapsedTextSize)) {
            this.mCollapsedTitleTextSize = a.getDimensionPixelSize(
                    R.styleable.CollapsingTitleLayout_collapsedTextSize, 0);
        }

        this.mRequestedExpandedTitleTextSize = a.getDimensionPixelSize(
                R.styleable.CollapsingTitleLayout_expandedTextSize, this.mCollapsedTitleTextSize);

        final int interpolatorId = a
                .getResourceId(R.styleable.CollapsingTitleLayout_textSizeInterpolator,
                        android.R.anim.accelerate_interpolator);
        this.mTextSizeInterpolator = AnimationUtils.loadInterpolator(context, interpolatorId);

        a.recycle();

        this.mToolbarContentBounds = new Rect();

        this.setWillNotDraw(false);
    }

    public void setTextAppearance(int resId) {
        TypedArray atp = this.getContext().obtainStyledAttributes(resId,
                R.styleable.CollapsingTextAppearance);
        this.mTextPaint.setColor(atp.getColor(
                R.styleable.CollapsingTextAppearance_android_textColor, Color.WHITE));
        this.mCollapsedTitleTextSize = atp.getDimensionPixelSize(
                R.styleable.CollapsingTextAppearance_android_textSize, 0);

        // added shadow so the text shows better on top of bright images
        this.mTextPaint.setShadowLayer(5, 0, 0, Color.rgb(0, 0, 0));

        atp.recycle();

        this.recalculate();
    }

    @Override
    public void addView(@NonNull View child, int index, ViewGroup.LayoutParams params) {
        super.addView(child, index, params);

        if (child instanceof Toolbar) {
            this.mToolbar = (Toolbar) child;
            this.mDummyView = new View(this.getContext());
            this.mToolbar.addView(this.mDummyView, LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        }
    }

    /**
     * Set the value indicating the current scroll value. This decides how much of the
     * background will be displayed, as well as the title metrics/positioning.
     *
     * A value of {@code 0.0} indicates that the layout is fully expanded.
     * A value of {@code 1.0} indicates that the layout is fully collapsed.
     */
    public void setScrollOffset(float offset) {
        if (offset != this.mScrollOffset) {
            this.mScrollOffset = offset;
            this.calculateOffsets();
        }
    }

    private void calculateOffsets() {
        final float offset = this.mScrollOffset;
        final float textSizeOffset = this.mTextSizeInterpolator != null
                ? this.mTextSizeInterpolator.getInterpolation(this.mScrollOffset)
                : offset;

        this.mTextLeft = interpolate(this.mExpandedMarginLeft, this.mToolbarContentBounds.left, offset);
        this.mTextTop = interpolate(this.mExpandedTop, this.mCollapsedTop, offset);
        this.mTextRight = interpolate(this.getWidth() - this.mExpandedMarginRight, this.mToolbarContentBounds.right, offset);

        this.setInterpolatedTextSize(
                interpolate(this.mExpandedTitleTextSize, this.mCollapsedTitleTextSize, textSizeOffset));

        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void calculateTextBounds() {
        final DisplayMetrics metrics = this.getResources().getDisplayMetrics();

        // We then calculate the collapsed text size, using the same logic
        this.mTextPaint.setTextSize(this.mCollapsedTitleTextSize);
        float textHeight = this.mTextPaint.descent() - this.mTextPaint.ascent();
        float textOffset = (textHeight / 2) - this.mTextPaint.descent();
        this.mCollapsedTop = this.mToolbarContentBounds.centerY() + textOffset;

        // First, let's calculate the expanded text size so that it fit within the bounds
        // We make sure this value is at least our minimum text size
        this.mExpandedTitleTextSize = (int) Math.max(this.mCollapsedTitleTextSize,
                getSingleLineTextSize(this.mTitle, this.mTextPaint,
                        this.getWidth() - this.mExpandedMarginLeft - this.mExpandedMarginRight, 0f,
                        this.mRequestedExpandedTitleTextSize, 0.5f, metrics));
        this.mExpandedTop = this.getHeight() - this.mExpandedMarginBottom;

        // The bounds have changed so we need to clear the texture
        this.clearTexture();
    }

    @Override
    public void draw(@NonNull Canvas canvas) {
        final int saveCount = canvas.save();

        final int toolbarHeight = this.mToolbar.getHeight();
        canvas.clipRect(0, 0, canvas.getWidth(),
                interpolate(canvas.getHeight(), toolbarHeight, this.mScrollOffset));

        // Now call super and let it draw the background, etc
        super.draw(canvas);

        if (this.mTitleToDraw != null) {
            float x = this.mTextLeft;
            float y = this.mTextTop;

            final float ascent = this.mTextPaint.ascent() * this.mScale;
            final float descent = this.mTextPaint.descent() * this.mScale;
            final float h = descent - ascent;

            if (DEBUG_DRAW) {
                // Just a debug tool, which drawn a Magneta rect in the text bounds
                canvas.drawRect(this.mTextLeft,
                        y - h + descent,
                        this.mTextRight,
                        y + descent,
                        DEBUG_DRAW_PAINT);
            }

            if (this.mUseTexture) {
                y = y - h + descent;
            }

            if (this.mScale != 1f) {
                canvas.scale(this.mScale, this.mScale, x, y);
            }

            if (this.mUseTexture && this.mExpandedTitleTexture != null) {
                // If we should use a texture, draw it instead of text
                canvas.drawBitmap(this.mExpandedTitleTexture, x, y, this.mTexturePaint);
            } else {
                canvas.drawText(this.mTitleToDraw, x, y, this.mTextPaint);
            }
        }

        canvas.restoreToCount(saveCount);
    }

    private void setInterpolatedTextSize(final float textSize) {
        if (this.mTitle == null) {
            return;
        }

        if (isClose(textSize, this.mCollapsedTitleTextSize) || isClose(textSize, this.mExpandedTitleTextSize)
                || this.mTitleToDraw == null) {
            // If the text size is 'close' to being a decimal, then we use this as a sync-point.
            // We disable our manual scaling and set the paint's text size.
            this.mTextPaint.setTextSize(textSize);
            this.mScale = 1f;

            // We also use this as an opportunity to ellipsize the string
            final CharSequence title = TextUtils.ellipsize(this.mTitle, this.mTextPaint,
                    this.mTextRight - this.mTextLeft,
                    TextUtils.TruncateAt.END);
            if (title != this.mTitleToDraw) {
                // If the title has changed, turn it into a string
                this.mTitleToDraw = title.toString();
            }

            if (USE_SCALING_TEXTURE && isClose(textSize, this.mExpandedTitleTextSize)) {
                this.ensureExpandedTexture();
            }
            this.mUseTexture = false;
        } else {
            // We're not close to a decimal so use our canvas scaling method
            if (this.mExpandedTitleTexture != null) {
                this.mScale = textSize / this.mExpandedTitleTextSize;
            } else {
                this.mScale = textSize / this.mTextPaint.getTextSize();
            }

            this.mUseTexture = USE_SCALING_TEXTURE;
        }

        ViewCompat.postInvalidateOnAnimation(this);
    }

    private void ensureExpandedTexture() {
        if (this.mExpandedTitleTexture != null) {
            return;
        }

        int w = (int) (this.getWidth() - this.mExpandedMarginLeft - this.mExpandedMarginRight);
        int h = (int) (this.mTextPaint.descent() - this.mTextPaint.ascent());

        this.mExpandedTitleTexture = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        Canvas c = new Canvas(this.mExpandedTitleTexture);
        c.drawText(this.mTitleToDraw, 0, h - this.mTextPaint.descent(), this.mTextPaint);

        if (this.mTexturePaint == null) {
            // Make sure we have a paint
            this.mTexturePaint = new Paint();
            this.mTexturePaint.setAntiAlias(true);
            this.mTexturePaint.setFilterBitmap(true);
        }
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        this.mToolbarContentBounds.left = this.mDummyView.getLeft();
        this.mToolbarContentBounds.top = this.mDummyView.getTop();
        this.mToolbarContentBounds.right = this.mDummyView.getRight();
        this.mToolbarContentBounds.bottom = this.mDummyView.getBottom();

        if (changed && this.mTitle != null) {
            // If we've changed and we have a title, re-calculate everything!
            this.recalculate();
        }
    }

    private void recalculate() {
        if (this.getHeight() > 0) {
            this.calculateTextBounds();
            this.calculateOffsets();
        }
    }

    /**
     * Set the title to display
     */
    public void setTitle(String title) {
        if (title == null || !title.equals(this.mTitle)) {
            this.mTitle = title;

            this.clearTexture();

            if (this.getHeight() > 0) {
                // If we've already been laid out, calculate everything now otherwise we'll wait
                // until a layout
                this.recalculate();
            }
        }
    }

    private void clearTexture() {
        if (this.mExpandedTitleTexture != null) {
            this.mExpandedTitleTexture.recycle();
            this.mExpandedTitleTexture = null;
        }
    }

    /**
     * Recursive binary search to find the best size for the text
     *
     * Adapted from https://github.com/grantland/android-autofittextview
     */
    private static float getSingleLineTextSize(String text, TextPaint paint, float targetWidth, float low, float high, float precision, DisplayMetrics metrics) {
        final float mid = (low + high) / 2.0f;

        paint.setTextSize(TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_PX, mid, metrics));
        final float maxLineWidth = paint.measureText(text);

        if ((high - low) < precision) {
            return low;
        } else if (maxLineWidth > targetWidth) {
            return getSingleLineTextSize(text, paint, targetWidth, low, mid, precision, metrics);
        } else if (maxLineWidth < targetWidth) {
            return getSingleLineTextSize(text, paint, targetWidth, mid, high, precision, metrics);
        } else {
            return mid;
        }
    }

    /**
     * Returns true if {@code value} is 'close' to it's closest decimal value. Close is currently
     * defined as it's difference being < 0.01.
     */
    private static boolean isClose(float value, float targetValue) {
        return Math.abs(value - targetValue) < 0.01f;
    }

    /**
     * Interpolate between {@code startValue} and {@code endValue}, using {@code progress}.
     */
    private static float interpolate(float startValue, float endValue, float progress) {
        return startValue + ((endValue - startValue) * progress);
    }
}