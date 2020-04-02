package com.docwei.myviewdemo.draw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseArray;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

//多根手指各自干自己的事情，实现多指涂鸦，互不干扰
//事件序列中down--move---->up里PointerId是不会变的，拿来追踪手指很适合
//涂鸦的话，需要记录路径path
public class MyMultiDrawView extends View {
    //这个paths由手指按下到up时候产生的path
    private HashMap<Integer,Path> paths;
    private final int[] COLOR_ARRAY = {0xffEA4335, 0xff4285F4,
            0xffFBBC05, 0xff34A853, 0xff42BD17, 0xff90BD0E, 0xff18BD8D,
            0xff27BDAD, 0xff2098BD, 0xffA96FBD, 0xff86B9BD, 0xff3DBDA4};
    //总共的path，要去重
    private Set<Path> totals;
    private Paint paint;

    public MyMultiDrawView(Context context) {
        this(context, null);
    }

    public MyMultiDrawView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MyMultiDrawView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paths=new HashMap<>();
        totals=new HashSet<>();
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int actionIndex=event.getActionIndex();
        //按下的时候记录，就可以获取到正确的PointerId
        final int recordPointer=event.getPointerId(actionIndex);
         switch (event.getActionMasked()){
             case MotionEvent.ACTION_DOWN:
             case MotionEvent.ACTION_POINTER_DOWN:
                 Path path=new Path();
                 paths.put(recordPointer,path);
                 totals.add(path);
                 path.moveTo(event.getX(event.findPointerIndex(recordPointer))
                         ,event.getY(event.findPointerIndex(recordPointer)));
                 break;
             case MotionEvent.ACTION_MOVE:
                  //拿到记录的PointerId的path
                 for(Integer index:paths.keySet()) {
                     for (int i = 0; i < event.getPointerCount(); i++) {
                         int pointerId = event.getPointerId(i);
                         if(index==pointerId){
                             //History历史记录是最近一次move产生的，要记录完整的path，需要将每一次lineTo连接
                             for(int j=0;j<event.getHistorySize();j++){
                                 float x= event.getHistoricalX(event.findPointerIndex(pointerId),j);
                                 float y= event.getHistoricalY(event.findPointerIndex(pointerId),j);
                                 paths.get(index).lineTo(x,y);
                             }
                             //也要加入最新的点，跟下一次可能有重复
                             paths.get(index).lineTo(event.getX(event.findPointerIndex(index))
                                     ,event.getY(event.findPointerIndex(index)));
                         }
                     }
                 }

                 break;
             case MotionEvent.ACTION_UP:
             case MotionEvent.ACTION_POINTER_UP:
             case MotionEvent.ACTION_CANCEL:
                 //最后的path要保存
                 totals.add(paths.get(recordPointer));
                 paths.remove(recordPointer);
                 break;
             default:
                 break;
         }
        invalidate();
        return true;
    }
   private int count=0;
    @Override
    public void draw(Canvas canvas) {
        super.draw(canvas);
        //这里没有将Path和颜色进行绑定，每次绘制时之前的path颜色是会变化的。
        for(Path path:totals){
            paint.setColor(COLOR_ARRAY[(count++)%COLOR_ARRAY.length]);
            canvas.drawPath(path,paint);
        }
        count=0;
    }
}
