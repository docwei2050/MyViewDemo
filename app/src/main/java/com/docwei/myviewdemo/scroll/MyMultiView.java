package com.docwei.myviewdemo.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyMultiView extends ViewGroup {

    private float mLastX;
    private int mChildWidth;
    private int mChildCount;
    private Scroller mScroller;
    private VelocityTracker mVelocityTracker;

    public MyMultiView(Context context) {
        this(context, null);
    }

    public MyMultiView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyMultiView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mScroller = new Scroller(context);
        mVelocityTracker = VelocityTracker.obtain();

    }

    private int activePointer;//记录活动的手指(PointerId)
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int actionIndex = event.getActionIndex();
        mVelocityTracker.addMovement(event);
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                if (!mScroller.isFinished()) {
                    mScroller.abortAnimation();
                }
            case MotionEvent.ACTION_POINTER_DOWN:
                //第一次按下或者后续按下，都将其作为活动的手指
                activePointer = event.getPointerId(actionIndex);
                final int pointerIndex = event.findPointerIndex(activePointer);
                mLastX = event.getX(pointerIndex);
                break;
            case MotionEvent.ACTION_MOVE:
                for (int i = 0; i < event.getPointerCount(); i++) {
                    if (event.getPointerId(i) == activePointer) {
                        float x = event.getX(event.findPointerIndex(activePointer));
                        scrollBy(-(int) (x - mLastX) / 3, 0);
                        mLastX = x;
                    }
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                //如果抬起的手指是活动的手指，那么就要更新活动的手指
                if (event.getPointerId(actionIndex) == activePointer && event.getPointerCount() > 1) {
                    //如果当前的点是0，就选择1，因为至少有一个手指在View上 ,actionIndex存在补位机制

                    int newIndex;
                    if (actionIndex == event.getPointerCount() - 1) {
                        newIndex = event.getPointerCount() - 2;
                    } else {
                        newIndex = event.getPointerCount() - 1;
                    }
                    activePointer = event.getPointerId( newIndex);
                    final int newPointerIndex = event.findPointerIndex(activePointer);
                    //pointerIndex out of range
                    mLastX = event.getX(newPointerIndex);
                }
                break;
            case MotionEvent.ACTION_UP:
                //不处理cancel可能出现回不到正确的位置
            case MotionEvent.ACTION_CANCEL:
                //测速度
                mVelocityTracker.computeCurrentVelocity(1000);
                float xVelocity = mVelocityTracker.getXVelocity();
                //处理最后定位的位置
                //getScrollX距离View的left滑动了多远
                //>0是左滑 <0是右滑*/
                int scrollX = getScrollX();
                //默认过了中点就算滑动一个子View
                int index = (scrollX + mChildWidth / 2) / mChildWidth;

                int left = (scrollX) / mChildWidth;
                int right = (scrollX + mChildWidth) / mChildWidth;

                //左滑(xVelocity<0) 当速度达到200，此时如果index的float值在1.1——1.5那么给他2
                if (Math.abs(xVelocity) >= 200 && left == index && xVelocity < 0) {
                    index = (scrollX + mChildWidth) / mChildWidth;
                }
                //右滑(xVelocity>0) 当速度达到200，此时如果index的float值在3.1——3.4那么给他2
                if (Math.abs(xVelocity) >= 200 && right == index && xVelocity > 0) {
                    index = index - 1;
                }

                if (index > mChildCount - 1) {
                    index = mChildCount - 1;
                }
                if (index < 0) {
                    index = 0;
                }
                //右滑当速度达到200，此时如果index的float
                startSmoothScrool(scrollX, index * mChildWidth - scrollX);
                mVelocityTracker.clear();
                break;
            default:
                break;

        }
        return true;
    }

    public void startSmoothScrool(int start, int x) {
        mScroller.startScroll(start, 0, x, 0, 500);
        invalidate();
    }

    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(mScroller.getCurrX(), mScroller.getCurrY());
            postInvalidate();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        int mode = MeasureSpec.getMode(widthMeasureSpec);
        //如果父控件width是At_most测量会显示不出来
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childWidth = 0;
        mChildCount = getChildCount();
        for (int i = 0; i < getChildCount(); i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                mChildWidth = child.getMeasuredWidth();
                child.layout(childWidth + getPaddingLeft(), getPaddingTop(), childWidth + mChildWidth - getPaddingRight(), bottom - getPaddingBottom() - top);
                childWidth += child.getMeasuredWidth();
            }
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        mVelocityTracker.recycle();
        super.onDetachedFromWindow();
    }
}
