package com.assistne.dribbble.logoloader;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.View;

import com.assistne.dribbble.R;

/**
 * https://dribbble.com/shots/2657317-Logo-loader-animation
 * Created by assistne on 16/4/21.
 */
@SuppressWarnings("deprecation")
public class LogoLoaderAnimation extends View{

    private Paint mCirclePaint;
    private Paint mMainPaint;
    private AnimatorSet mAnimatorSet;
    private ValueAnimator mInvalidAnimator;

    //  直线距竖直方向的夹角值
    public static final float SIN_ALPHA = (float) Math.cos(Math.toRadians(PointContainer.LINE_DEGREE));
    public static final float COS_ALPHA = (float) Math.sin(Math.toRadians(PointContainer.LINE_DEGREE));

    private PointContainer mGreenContainer;
    private PointContainer mPinkContainer1;
    private PointContainer mPinkContainer2;
    private PointContainer mPurpleContainer;
    private PointContainer mBlueContainer1;
    private PointContainer mBlueContainer2;

    private final float mSymmetryAxis = 378f;
    //  直线起始位置
    private final float mGreenLineStartX = mSymmetryAxis - 20;
    private final float mGreenLineStartY = 330.25f;
    //  直线起始位置
    private final float mPurpleLineStartX = mSymmetryAxis + 20;
    private final float mPurpleLineStartY = mGreenLineStartY + 108;//(float) (mGreenLineStartY + (2 * PointContainer.PAINT_WIDTH + PointContainer.LINE_MARGIN) / Math.sin(Math.toRadians(PointContainer.LINE_DEGREE)));

    private final float mDownCircleCenterX = mSymmetryAxis;
    private final float mDownCircleCenterY = mGreenLineStartY + 140;//mLineLen / COS_ALPHA + mGreenLineStartY;
    private final float mUpCircleCenterX = mSymmetryAxis;
    private final float mUpCircleCenterY = mPurpleLineStartY - 140;//(float) (mPurpleLineStartY - mLineLen / COS_ALPHA);

    private AnimatorListenerAdapter mRestartListener;

    public LogoLoaderAnimation(Context context) {
        this(context, null);
    }

    public LogoLoaderAnimation(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public LogoLoaderAnimation(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
        final float TO_RIGHT = 1;
        final float TO_LEFT = -1;

        mGreenContainer = new PointContainer(mGreenLineStartX, mGreenLineStartY, TO_RIGHT,
                mDownCircleCenterX, mDownCircleCenterY);
        PointF pinkStart1 = movePoint(mGreenLineStartX, mGreenLineStartY, TO_RIGHT);
        mPinkContainer1 = new PointContainer(pinkStart1.x, pinkStart1.y, TO_RIGHT,
                mDownCircleCenterX, mDownCircleCenterY);
        mPinkContainer1.setTailDirect(1);
        PointF pinkStart2 = movePoint(pinkStart1.x, pinkStart1.y, TO_RIGHT);
        mPinkContainer2 = new PointContainer(pinkStart2.x, pinkStart2.y, TO_RIGHT,
                mDownCircleCenterX, mDownCircleCenterY);
        mPinkContainer2.setTailDirect(2);
        mPurpleContainer = new PointContainer(mPurpleLineStartX, mPurpleLineStartY, TO_LEFT,
                mUpCircleCenterX, mUpCircleCenterY);
        PointF blueStart1 = movePoint(mPurpleLineStartX, mPurpleLineStartY, TO_LEFT);
        mBlueContainer1 = new PointContainer(blueStart1.x, blueStart1.y, TO_LEFT,
                mUpCircleCenterX, mUpCircleCenterY);
        mBlueContainer1.setTailDirect(3);
        PointF blueStart2 = movePoint(blueStart1.x, blueStart1.y, TO_LEFT);
        mBlueContainer2 = new PointContainer(blueStart2.x, blueStart2.y, TO_LEFT,
                mUpCircleCenterX, mUpCircleCenterY);
        mBlueContainer2.setTailDirect(4);

        mAnimatorSet = new AnimatorSet();
        mInvalidAnimator = ValueAnimator.ofFloat(0, 1);
        mInvalidAnimator.setDuration(1000);
        mInvalidAnimator.setRepeatCount(ValueAnimator.INFINITE);
        mInvalidAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                invalidate();
            }
        });

        final AnimatorSet showSet = new AnimatorSet();
        final AnimatorSet hideSet = new AnimatorSet();
        final AnimatorSet SpotSet = new AnimatorSet();
        PointAnimator greenAni = new PointAnimator(mGreenContainer, 200);
        PointAnimator pinkAni1 = new PointAnimator(mPinkContainer1, 100);
        PointAnimator pinkAni2 = new PointAnimator(mPinkContainer2, 0);
        PointAnimator purpleAni = new PointAnimator(mPurpleContainer, 200);
        PointAnimator blueAni1 = new PointAnimator(mBlueContainer1, 100);
        PointAnimator blueAni2 = new PointAnimator(mBlueContainer2, 0);

        showSet.playTogether(greenAni.getShowAnimatorSet(), pinkAni1.getShowAnimatorSet(),
                pinkAni2.getShowAnimatorSet(), blueAni2.getShowAnimatorSet(),
                purpleAni.getShowAnimatorSet(), blueAni1.getShowAnimatorSet());

        hideSet.playTogether(pinkAni2.getHideAnimatorSet(), blueAni2.getHideAnimatorSet(),
                greenAni.getHideAnimatorSet(), pinkAni1.getHideAnimatorSet(),
                purpleAni.getHideAnimatorSet(), blueAni1.getHideAnimatorSet());

        SpotSet.playTogether(greenAni.getSpotAnimatorSet(), pinkAni1.getSpotAnimatorSet(),
                pinkAni2.getSpotAnimatorSet(), purpleAni.getSpotAnimatorSet(),
                blueAni1.getSpotAnimatorSet(), blueAni2.getSpotAnimatorSet());
        mAnimatorSet.playSequentially(showSet, hideSet, SpotSet);
        mRestartListener = new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (getVisibility() == VISIBLE) {
                    //  循环动画
                    mAnimatorSet.start();
                }
            }

        };
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        //  固定图像大小
        setMeasuredDimension(765, 765);
    }

    /**
     * 根据前一直线的起始点计算相邻平行直线的起始位置*/
    private PointF movePoint(float x, float y, float direct) {
        float x1 = x + direct * (PointContainer.PAINT_WIDTH + PointContainer.LINE_MARGIN) * COS_ALPHA;
        float y1 = y - direct * (PointContainer.PAINT_WIDTH + PointContainer.LINE_MARGIN) * SIN_ALPHA;
        return new PointF(x1, y1);
    }

    private void initPaint() {
        mCirclePaint = new Paint();
        mMainPaint = new Paint();
        mMainPaint.setAntiAlias(true);
        mMainPaint.setStyle(Paint.Style.STROKE);
        mMainPaint.setStrokeWidth(PointContainer.PAINT_WIDTH);
        mMainPaint.setColor(getContext().getResources().getColor(R.color.green));
    }

    private void onDrawContainer(Canvas canvas, PointContainer container, int color) {
        mCirclePaint.setColor(color);
        mMainPaint.setColor(color);
        if (!container.isSpotDrawing) {
            //  起点圆点
            canvas.drawCircle(container.head.x, container.head.y, PointContainer.PAINT_WIDTH/2, mCirclePaint);
            if (container.isShowing) {// 显示图形
                //  画线
                canvas.drawLine(container.lineStart.x, container.lineStart.y,
                        container.currentLine.x, container.currentLine.y, mMainPaint);
                if (!container.isDrawingLine) {//  进入画弧阶段
                    //  直线与弧的交接点补色
                    canvas.drawCircle(container.lineEnd.x, container.lineEnd.y, PointContainer.PAINT_WIDTH/2, mCirclePaint);
                    //  画弧
                    canvas.drawArc(container.circleRectF, container.startDegree, container.sweepDegree, false, mMainPaint);
                }
            } else {//  隐藏图形
                if (container.isDrawingLine) {
                    canvas.drawLine(container.currentLine.x, container.currentLine.y,
                            container.lineEnd.x, container.lineEnd.y, mMainPaint);
                    canvas.drawArc(container.circleRectF, container.startDegree, container.sweepDegree, false, mMainPaint);
                } else {
                    canvas.drawArc(container.circleRectF, container.startDegree + container.sweepDegree,
                            container.degreeRange - container.sweepDegree, false, mMainPaint);
                }
            }
            if (container.isDrawingTail && container.hasTailArc) {
                //  弧与尾端弧度的交接点补色
                canvas.drawCircle(container.arcEnd.x, container.arcEnd.y, PointContainer.PAINT_WIDTH/2, mCirclePaint);
                //  尾端弧度
                canvas.drawArc(container.tailCircleRectF, container.tailStartDegree, container.tailSweepDegree, false, mMainPaint);
            }
            //  画末端的圆点
            canvas.drawCircle(container.tail.x, container.tail.y, PointContainer.PAINT_WIDTH/2, mCirclePaint);
        } else {
            //  动点
            canvas.drawCircle(container.head.x, container.head.y, PointContainer.PAINT_WIDTH/2, mCirclePaint);
        }
    }


    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        onDrawContainer(canvas, mGreenContainer, getContext().getResources().getColor(R.color.green));
        onDrawContainer(canvas, mPinkContainer1, getContext().getResources().getColor(R.color.pink));
        onDrawContainer(canvas, mPinkContainer2, getContext().getResources().getColor(R.color.pink));
        onDrawContainer(canvas, mPurpleContainer, getContext().getResources().getColor(R.color.purple));
        onDrawContainer(canvas, mBlueContainer1, getContext().getResources().getColor(R.color.blue));
        onDrawContainer(canvas, mBlueContainer2, getContext().getResources().getColor(R.color.blue));

    }

    @Override
    protected void onWindowVisibilityChanged(int visibility) {
        super.onWindowVisibilityChanged(visibility);
        if (visibility == VISIBLE) {
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAnimatorSet.addListener(mRestartListener);
                    mInvalidAnimator.start();
                    mAnimatorSet.start();
                }
            }, 700);

        } else if (visibility == GONE) {
            mAnimatorSet.removeAllListeners();
            mInvalidAnimator.end();
            mAnimatorSet.end();
            mGreenContainer.reset();
            mPinkContainer1.reset();
            mPinkContainer2.reset();
            mPurpleContainer.reset();
            mBlueContainer1.reset();
            mBlueContainer2.reset();
        }
    }

}
