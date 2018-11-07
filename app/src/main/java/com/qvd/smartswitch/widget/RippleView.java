package com.qvd.smartswitch.widget;


import android.animation.ObjectAnimator;
import android.animation.TypeEvaluator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.animation.LinearInterpolator;
import android.widget.TextView;


/**
 * 设置textview的波纹效果
 */
@SuppressLint("AppCompatCustomView")
public class RippleView extends TextView
{

    private static final String tag = RippleView.class.getSimpleName();

    private static final int DEFAULT_RIPPLE_COLOR = Color.rgb(0x33, 0x99, 0xcc);
    /**
     * 波纹的颜色
     */
    private final int mRippleColor = DEFAULT_RIPPLE_COLOR;
    /**
     * 波纹动画效果是否正在进行
     */
    private boolean animationRunning = false;

    private int currentProgress = 0;
    /**
     * //无限长的数值，使动画不停止
     */
    private final int mTotalTime = 1000 * 1000;

    public static final int MODE_IN = 1;
    public static final int MODE_OUT = 2;

    private int mode = MODE_OUT;

    private int mCenterX;
    private int mCenterY;
    private int mRadius;
    private Paint mPaint;
    private ObjectAnimator mAnimator;

    public RippleView(Context context)
    {
        super(context);
        initPaint();
        initAnimation();
    }

    public RippleView(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        initPaint();
        initAnimation();
    }

    public RippleView(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initPaint();
        initAnimation();
    }

    private void initPaint()
    {
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(mRippleColor);
    }

    private void initAnimation()
    {
        mAnimator = ObjectAnimator.ofInt(this, "currentProgress", 0, 100);
        mAnimator.setRepeatCount(ObjectAnimator.INFINITE);
        mAnimator.setRepeatMode(ObjectAnimator.RESTART);
        mAnimator.setInterpolator(new LinearInterpolator());
        mAnimator.setEvaluator(mProgressEvaluator);
        mAnimator.setDuration(mTotalTime);
    }

    public void setMode(int mode)
    {
        this.mode = mode;
    }

    public void startRippleAnimation()
    {
        if (!animationRunning)
        {
            mAnimator.start();
            animationRunning = true;
        }
    }

    private void stopRippleAnimation()
    {
        if (animationRunning)
        {
            mAnimator.end();
            animationRunning = false;
        }
    }

    private boolean isRippleAnimationRunning()
    {
        return animationRunning;
    }

    public int getCurrentProgress()
    {
        return currentProgress;
    }

    public void setCurrentProgress(int currentProgress)
    {
        this.currentProgress = currentProgress;
        this.invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
    {
        //super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int resultWidth;
        int modeWidth = MeasureSpec.getMode(widthMeasureSpec);
        int sizeWidth = MeasureSpec.getSize(widthMeasureSpec);
        /*
      默认的波纹的最小值
     */
        int mMinSize = 200;
        if (modeWidth == MeasureSpec.EXACTLY)
        {
            resultWidth = sizeWidth;
        }
        else
        {
            resultWidth = mMinSize;
            if (modeWidth == MeasureSpec.AT_MOST)
            {
                resultWidth = Math.min(resultWidth, sizeWidth);
            }
        }

        int resultHeight;
        int modeHeight = MeasureSpec.getMode(heightMeasureSpec);
        int sizeHeight = MeasureSpec.getSize(heightMeasureSpec);
        if (modeHeight == MeasureSpec.EXACTLY)
        {
            resultHeight = sizeHeight;
        }
        else
        {
            resultHeight = mMinSize;
            if (modeHeight == MeasureSpec.AT_MOST)
            {
                resultHeight = Math.min(resultHeight, sizeHeight);
            }
        }

        mCenterX = resultWidth / 2;
        mCenterY = resultHeight / 2;
        mRadius = Math.max(resultWidth, resultHeight) / 2;

        Log.d(tag, "ripple out view radius = " + mRadius + "; width =" + resultWidth
            + "; height = " + resultHeight);

        setMeasuredDimension(resultWidth, resultHeight);
    }

    @Override
    public void onDraw(Canvas canvas)
    {
        /*
      动画中波纹的个数
     */
        int mRippleNum = 4;
        for (int i = 0; i < mRippleNum; i++)
        {
            int progress = (currentProgress + i * 100 / (mRippleNum)) % 100;
            if (mode == 1)
                progress = 100 - progress;

            mPaint.setAlpha(255 - 255 * progress / 100);
            canvas.drawCircle(mCenterX, mCenterY, mRadius * progress / 100, mPaint);
        }
        super.onDraw(canvas);
    }

    @Override
    protected void onDetachedFromWindow()
    {
        super.onDetachedFromWindow();
        if (isRippleAnimationRunning())
            stopRippleAnimation();
    }

    /**
     * 自定义估值器
     */
    private final TypeEvaluator mProgressEvaluator = (fraction, startValue, endValue) -> {
        int mPeriod = 30;
        fraction = (fraction * mTotalTime / mPeriod) % 100;
        return fraction;
    };

}
