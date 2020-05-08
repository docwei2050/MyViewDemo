package com.docwei.myviewdemo.scroll;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Scroller;

public class MyOriginalView extends ViewGroup {

    private float mLastX;
    private int mChildWidth;
    private int mChildCount;

    public MyOriginalView(Context context) {
        this(context, null);
    }

    public MyOriginalView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyOriginalView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                //最初down时要保存到
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                //)*0.85是为了增加阻力f
                scrollBy(-(int) ((x - mLastX)*0.85f), 0);
                break;
            case MotionEvent.ACTION_UP:
                //处理最后定位的位置
                //getScrollX距离View的left滑动了多远
                //>0是左滑 <0是右滑*/
                int scrollX = getScrollX();
                //默认过了中点就算滑动一个子View
                int index = (scrollX + mChildWidth / 2) / mChildWidth;
                if (index > mChildCount - 1) {
                    index = mChildCount - 1;
                }
                if (index < 0) {
                    index = 0;
                }
                //右滑当速度达到200，此时如果index的float
                scrollBy(index * mChildWidth - scrollX,0);
                break;
            default:
                break;
        }
        mLastX = x;
        return true;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);
        //如果父控件width是At_most测量会显示不出来
        measureChildren(widthMeasureSpec, heightMeasureSpec);
        setMeasuredDimension(width, height);


    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        int childWidth = 0;
        mChildCount = getChildCount();
        for (int i = 0; i < mChildCount; i++) {
            View child = getChildAt(i);
            if (child.getVisibility() != View.GONE) {
                mChildWidth = child.getMeasuredWidth();
                child.layout(childWidth + getPaddingLeft(),  getPaddingTop(), childWidth + mChildWidth- getPaddingRight(), bottom - getPaddingBottom()-top);
                childWidth += child.getMeasuredWidth();
            }
        }
    }

}
