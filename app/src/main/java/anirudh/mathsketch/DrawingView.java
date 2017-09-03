package anirudh.mathsketch;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;


public class DrawingView extends View{

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        invalidate();
        setupDrawing();
    }
    //Create a LIST of Paths so we can keep track of how many paths(basically strokes)
    // we have drawn and erase them if needed
    private ArrayList<Path> moveList=new ArrayList<>();
    private ArrayList<Path> currentMoveList=new ArrayList<>();
    //records individual drawing paths(strokes)
    private Path drawPath;
    //drawpaint is basically holds the characteristics of
    // your "brush" like the width, stroke type, etc.
    private Paint drawPaint, canvasPaint;
    //initial color
    private int paintColor = 0xffffff;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;
    //Initialize canvas and stuff
    protected void setupDrawing(){
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }
    //In case screen orientation changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }
    //THIS IS WHERE ALL THE DRAWING HAPPENS
    @Override
    protected void onDraw(Canvas canvas) {
        //Draw out all your strokes

        for (Path path : currentMoveList) {
            canvas.drawPath(path, drawPaint);
        }
        for (Path path : moveList) {
            canvas.drawPath(path, drawPaint);
        }
    }
    //Detects touches and tells us where to draw with paths
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                currentMoveList.add(drawPath);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                moveList.add(drawPath);
                //reset drawpath since it only tracks
                //individual paths(strokes)
                drawPath=new Path();
                currentMoveList.clear();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }
    public void setColor(int newColor){

        drawPaint.setColor(newColor);
    }
    public void erase(){
        canvasBitmap.eraseColor(Color.TRANSPARENT);
        drawCanvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        invalidate();
    }
    public void undo() {
        if (moveList.size() > 0) {
            moveList.remove(moveList.size() - 1);
            invalidate();
        }
    }
}
