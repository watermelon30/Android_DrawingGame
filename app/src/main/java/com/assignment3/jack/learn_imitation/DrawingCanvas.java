package com.assignment3.jack.learn_imitation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.media.ThumbnailUtils;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

public class DrawingCanvas extends View {

    private static final int TOLERANCE = 4;

    //Number of grid line.
    private static  final int LINENUM = 3;

    public int width;
    public int height;

    private float currentX;
    private float currentY;

    private Bitmap bitmap;
    private Canvas canvas;
    private Paint paint;
    private Paint gridPaint;
    private Path path;
    private ArrayList<Path> paths = new ArrayList<Path>();
    private ArrayList<Integer> strokeWidths = new ArrayList<Integer>();
    private int currentStroke = 50;

    private float gap;
    private boolean gridMode = true;
    private ArrayList<Path> gridPaths = new ArrayList<Path>();

    Context context;

    //Constructor
    public DrawingCanvas(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        this.context = context;

        path = new Path();

        //Preset some parameters for the paint.
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setColor(Color.BLACK);
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeJoin(Paint.Join.ROUND);
        paint.setStrokeWidth(currentStroke);


        //preset the grid paint.
        gridPaint = new Paint();
        gridPaint.setAntiAlias(true);
        gridPaint.setColor(Color.RED);
        gridPaint.setStyle(Paint.Style.STROKE);
        gridPaint.setStrokeJoin(Paint.Join.ROUND);
        gridPaint.setStrokeWidth(5f);
        setDrawingCacheEnabled(true);
    }

    //This will be called whenever invalidate() is called or user tries to draw things.
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //We are not drawing on bmp.
//        canvas.drawBitmap(bitmap, 0,0, paint);

        //Adding grid lines to the canvas.
        addGrid(canvas);

        //Draw each path on corresponding stroke width.
        for(int i=0;i<paths.size(); i++){
            paint.setStrokeWidth(strokeWidths.get(i));
            canvas.drawPath(paths.get(i), paint);
        }

        //Draw current line.
        paint.setStrokeWidth(currentStroke);
        canvas.drawPath(path, paint);
    }

    @Override
    //The w and h are supposed to be the same.
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        width = w;
        height = h;

        //Reinitialise array lists.
        paths = new ArrayList<Path>();
        gridPaths = new ArrayList<Path>();
        strokeWidths = new ArrayList<Integer>();

        gap = (float) (width / (LINENUM + 1));

        //Store bitmap with 4 bytes each pixel
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);

        canvas = new Canvas(bitmap);
        //Add white background.
        canvas.drawColor(Color.WHITE);
    }

    public void clearCanvas(){
        //Reset and redraw the canvas.
        onSizeChanged(width, height, width, height);
        invalidate();
    }

    public Bitmap getDrawing(){
        //To return the whole drawing canvas, we draw each path on to the new bmp and return it.
        Bitmap bmpToReturn = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas tempCanvas = new Canvas(bmpToReturn);
        tempCanvas.drawColor(Color.WHITE);
        tempCanvas.drawBitmap(bmpToReturn, 0,0, paint);

        //Draw each path with corresponding stroke width.
        for(int i=0;i<paths.size(); i++){
            paint.setStrokeWidth(strokeWidths.get(i));
            tempCanvas.drawPath(paths.get(i), paint);
        }
        return bmpToReturn;
    }

    //User start to draw a new line.
    private void touchStart(float x, float y){
        path.reset();
        path.moveTo(x, y);
        currentX = x;
        currentY = y;
    }

    //User draws by moving finger.
    private void touchMove(float x, float y){
        float dx = Math.abs(currentX - x);
        float dy = Math.abs(currentY - y);
        //Only move the path if the moving amount is big enough.
        if(dx >= TOLERANCE || dy >= TOLERANCE){

            path.quadTo(x, y, (x + currentX)/2, (y+currentY)/2);
            currentX = x;
            currentY = y;
        }
    }

    //User finishes drawing.
    private void touchUp() {
        path.lineTo(currentX, currentY);
//        paint.setStrokeWidth(currentStroke);
//        canvas.drawPath(path, paint);

        //Add current stroke width and the finished path to the array.
        strokeWidths.add(currentStroke);
        paths.add(path);
        //Reset path.
        path = new Path();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();
        switch(event.getAction()){
            //Detect start, moving, end event. Call invalidate to redraw the canvas.
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                touchUp();
                invalidate();
                break;
        }
        return true;
    }

    //Change stroke width when user set value through slider.
    public void changeStrokeWidth(int width){
        currentStroke = width;
    }

    //Undo event from user.
    public void undo(){
        //Remove the most current path and stroke from array and redraw the canvas.
        if(paths.size() > 0){
            paths.remove(paths.size()-1);
            strokeWidths.remove(strokeWidths.size()-1);
        }
        invalidate();
    }

    //Add grid to the drawing canvas to make user easier to draw.
    private void addGrid(Canvas canvas){
        Path gridPath = new Path();
        for (int i = 1; i < LINENUM + 1; i++) {
            //Draw parallel grid
            gridPath.moveTo(0, gap * i);
            gridPath.lineTo(width, gap * i);
            canvas.drawPath(gridPath, gridPaint);
//            gridPath = new Path();
            //Draw vertical grid
            gridPath.moveTo(gap * i, 0);
            gridPath.lineTo(gap * i, width);
            canvas.drawPath(gridPath, gridPaint);
        }
    }

    public int getStrokeNum(){
        return paths.size();
    }
}



