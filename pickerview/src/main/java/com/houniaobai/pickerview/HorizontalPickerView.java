package com.houniaobai.pickerview;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * created on 2018/1/16.
 *
 * @author houniao_bai
 * @version 0.0.2
 */

public class HorizontalPickerView extends HorizontalScrollView implements View.OnClickListener {
    private static final String TAG = HorizontalPickerView.class.getSimpleName();

    private Context context;
    /**
     * 父容器
     */
    private LinearLayout linearLayout;
    /**
     * 数据源
     */
    private List<String> mData = new ArrayList<>();

    /**
     * 属性
     */
    private float HorizontalPickerView_textSize;
    private int HorizontalPickerView_textColor, HorizontalPickerView_selectedTextColor;

    /**
     * 当前选中的position
     */
    private int position ;
    private int scrollX;

    /**事件监听*/
    private CurrentItemChangeListener currentItemChangeListener;
    private OnItemClickListener onItemClickListener;

    public HorizontalPickerView(Context context) {
        this(context, null);
    }

    public HorizontalPickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalPickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        setOverScrollMode(OVER_SCROLL_NEVER);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalPickerView, defStyleAttr, 0);
        try {
            HorizontalPickerView_textSize = a.getDimension(R.styleable.HorizontalPickerView_pv_textSize, 16);
            HorizontalPickerView_selectedTextColor = a.getColor(R.styleable.HorizontalPickerView_pv_selectedTextColor, Color.GREEN);
            HorizontalPickerView_textColor = a.getColor(R.styleable.HorizontalPickerView_pv_textColor, Color.GRAY);
        } finally {
            a.recycle();
        }
        init();
    }

    public HorizontalPickerView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        this.context = context;
        setOverScrollMode(OVER_SCROLL_NEVER);
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.HorizontalPickerView, defStyleAttr, 0);
        try {
            HorizontalPickerView_textSize = a.getDimension(R.styleable.HorizontalPickerView_pv_textSize, 16);
            HorizontalPickerView_selectedTextColor = a.getColor(R.styleable.HorizontalPickerView_pv_selectedTextColor, Color.GREEN);
            HorizontalPickerView_textColor = a.getColor(R.styleable.HorizontalPickerView_pv_textColor, Color.GRAY);
        } finally {
            a.recycle();
        }
        init();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onScrollChanged(int l, int t, int oldl, int oldt) {
        super.onScrollChanged(l, t, oldl, oldt);
        int scrollX = getScrollX();

        for (int i = 1; i < linearLayout.getChildCount()-1; i++) {
            TextView textview = (TextView) linearLayout.getChildAt(i);
            int screenWidth = getResources().getDisplayMetrics().widthPixels;
            if(textview.getLeft()-scrollX<=screenWidth/2&& screenWidth/2<textview.getLeft()+textview.getWidth()-scrollX){
                position = i-1;
                textview.setTextColor(HorizontalPickerView_selectedTextColor);
                if(currentItemChangeListener!=null)
                    currentItemChangeListener.onCurrentItemChanged(textview, position);
            }else {
                textview.setTextColor(HorizontalPickerView_textColor);
            }
        }
    }
    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_UP) {
            scrollX = getScrollX();
            postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (scrollX == getScrollX()) {
                        View view = linearLayout.getChildAt(position+1);
                        scrollto(view);
                        if(currentItemChangeListener!=null)
                            currentItemChangeListener.onScrollChangedFinish(view, position);
                    } else {
                        scrollX = getScrollX();
                        post(this);
                    }
                }
            }, 30);
        }
        return super.onTouchEvent(ev);
    }

    private void init(){
        /**
         * 添加LinearLayout
         */
        linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setGravity(Gravity.CENTER_VERTICAL);
        LayoutParams params = new LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        addView(linearLayout, params);
    }
    /**
     * 设置数据源
     */
    public void setData(List<String> data) {
        mData.clear();
        if(data!=null)
        mData.addAll(data);
        notifyDataSetChanged();
    }
    public final void notifyDataSetChanged() {
        int screenWidth = getResources().getDisplayMetrics().widthPixels;
        linearLayout.removeAllViews();
        for (int i = 0; i < mData.size(); i++) {
            TextView view = createTextView("" + mData.get(i));
            view.setTag(i);
            linearLayout.addView(view);
            if(i==0){
                int width = screenWidth/2 ;
                linearLayout.addView(createView(width), 0);
            }
            if(i==mData.size()-1){
                int width = screenWidth/2 ;
                linearLayout.addView(createView(width));
            }
        }
    }

    private TextView createTextView(@NonNull String text) {
        TextView textView = new TextView(context);
        textView.setText(text);
        textView.setTextColor(HorizontalPickerView_textColor);
        textView.setTextSize(HorizontalPickerView_textSize);
        textView.setPadding(getResources().getDimensionPixelOffset(R.dimen.pv_horizontalpickerview_pad_left),0, getResources().getDimensionPixelOffset(R.dimen.pv_horizontalpickerview_pad_left),0);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(params);
        textView.setOnClickListener(this);
        return textView;
    }

    private View createView(int width) {
        View view = new View(context);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, ViewGroup.LayoutParams.WRAP_CONTENT);
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public void onClick(View view) {
        scrollto(view);
        position = (int) view.getTag();
        if(onItemClickListener!=null)
            onItemClickListener.onClick(view, position);
        if(currentItemChangeListener!=null)
            currentItemChangeListener.onScrollChangedFinish(view, position);
    }
    private void scrollto(View view){
        if(view==null) return;
        int itemWidth = view.getWidth();
        int screenWidth = getResources().getDisplayMetrics().widthPixels;

        smoothScrollTo(view.getLeft() - (screenWidth / 2 - itemWidth / 2), 0);

    }
    public void scrollto(final int arg){
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                // TODO Auto-generated method stub
                if(arg<linearLayout.getChildCount()-2){
                    View view = linearLayout.getChildAt(arg+1);
                    scrollto(view);
                }
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setCurrentItemChangeListener(CurrentItemChangeListener currentItemChangeListener) {
        this.currentItemChangeListener = currentItemChangeListener;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    /**
     * 滚动时的回调接口
     */
    public interface CurrentItemChangeListener{
        void onCurrentItemChanged(View view, int position);
        void onScrollChangedFinish(View view, int position);
    }

    /**
     * item点击时的回调
     */
    public interface OnItemClickListener{
        void onClick(View view, int position);
    }
}
