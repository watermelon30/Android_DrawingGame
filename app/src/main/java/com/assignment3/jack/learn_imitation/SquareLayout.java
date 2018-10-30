package com.assignment3.jack.learn_imitation;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;


//This is the class to make a square linear layout to put image/drawing canvas for user.
public class SquareLayout extends LinearLayout{
    private int squareWidth;
    public SquareLayout(Context context) {
        super(context);
    }

    public SquareLayout(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public SquareLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int width = MeasureSpec.getSize(widthMeasureSpec);
        int height = MeasureSpec.getSize(heightMeasureSpec);

        //Set the layout with either width or height of the screen size depends on which one is shorter.
        int size = width > height ? height : width;
        squareWidth = size;
        setMeasuredDimension(size, size);
    }
    //To return the width.
    public int getSquareWidth(){
        return squareWidth;
    }
}
