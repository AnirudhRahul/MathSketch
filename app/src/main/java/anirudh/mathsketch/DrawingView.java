package anirudh.mathsketch;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.googlecode.tesseract.android.TessBaseAPI;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;


public class DrawingView extends View{
    private int initialWritingColor;
    String datapath="";
    AssetManager assetManager;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initialWritingColor=ContextCompat.getColor(context, R.color.initialWritingColor);
        invalidate();
        setupDrawing();
    }
    public void setdatpath(File in){
        datapath = in+ "/tesseract/";
    }
    public void setAssetManager(AssetManager a){
        assetManager=a;
    }
    private ImageView prevstroke;
    //Tracks current color
    private int currentcolor;
    //Create a LIST of Paths so we can keep track of how many paths(basically strokes)
    // we have drawn and erase them if needed
    private ArrayList<Path> moveList=new ArrayList<>();
    private ArrayList<Path> currentMoveList=new ArrayList<>();
    //Create a list of objects so we can group strokes which are one after the other togethor
    private ArrayList<StrokeGroup> strokeRecord=new ArrayList<>();
    //records individual drawing paths(strokes)
    private Path drawPath;
    //drawpaint is basically holds the characteristics of
    // your "brush" like the width, stroke type, etc.
    private Paint drawPaint;
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
    }
    //In case screen orientation changes
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }
    public void setImageView(ImageView v){
        prevstroke=v;
    }
    public RectF maxRect(ArrayList<RectF> list){
        if(list.size()==1)
            return list.get(0);
        float left=list.get(0).left;
        float right=list.get(0).right;
        float top=list.get(0).top;
        float bottom=list.get(0).bottom;
        for(int i=1;i<list.size();i++){
            float curleft=list.get(i).left;
            float curright=list.get(i).right;
            float curtop=list.get(i).top;
            float curbottom=list.get(i).bottom;
            if(curleft<left)
                left=curleft;
            if(curbottom>bottom)
                bottom=curbottom;
            if(curright>right)
                right=curright;
            if(curtop<top)
                top=curtop;
        }
        return new RectF(left,top,right,bottom);
    }
    public void lastBitmap(){
        if(strokeRecord.size()==0)
            return;
//        newBitmap.eraseColor(android.graphics.Color.GREEN);
        StrokeGroup lastStroke=strokeRecord.get(strokeRecord.size()-1);
        ArrayList<RectF> rectFArrayList=new ArrayList<>();
        for(Path path:lastStroke.pathset) {
            RectF temp=new RectF();
            path.computeBounds(temp, false);
            rectFArrayList.add(temp);
        }
        RectF bmpRect=maxRect(rectFArrayList);

        Bitmap newBitmap=Bitmap.createBitmap((int)(bmpRect.right-bmpRect.left+75), (int)(bmpRect.bottom-bmpRect.top+40),Bitmap.Config.ARGB_8888);
        Canvas newCanvas=new Canvas(newBitmap);
        for(Path path:lastStroke.pathset) {
            Path temp=new Path(path);
            temp.offset(-bmpRect.left+50,-bmpRect.top+20);
            newCanvas.drawPath(temp, drawPaint);

        }

        checkFile(new File(datapath+"tessdata/"));
        TessBaseAPI baseAPI = new TessBaseAPI();
        baseAPI.init(datapath, "eng");
        String result="";
        baseAPI.setImage(newBitmap);
        result=baseAPI.getUTF8Text();
        Toast temptoast=Toast.makeText(getContext(),result,Toast.LENGTH_LONG);
        temptoast.show();
        prevstroke.setImageBitmap(newBitmap);


    }
    public void drawRect(RectF rectangle){
        drawCanvas.drawRect(rectangle, drawPaint);
    }
    //THIS IS WHERE ALL THE DRAWING HAPPENS
    @Override
    protected void onDraw(Canvas canvas) {
        //Draw out all your strokes
        boolean redraw=false;
        combineStrokes();
        for (Path path : currentMoveList) {
            canvas.drawPath(path, drawPaint);
        }
        int i=0;
        for (StrokeGroup stroke: strokeRecord) {
            setColor(stroke.getColor(), false);
            if(strokeRecord.size()-1==i&&System.currentTimeMillis()-stroke.time>1000) {
                lastBitmap();
                setColor(initialWritingColor, false);
                redraw=false;
            }
            else
                redraw=true;
            for(Path path:stroke.getPathset()) {
                canvas.drawPath(path, drawPaint);
            }
            i++;

        }
        setColor(currentcolor, false);
        if(redraw)
            invalidate();

    }
    public void combineStrokes(){
        if(strokeRecord.size()>1)
        for(int i=strokeRecord.size()-2;i>=0;i--){
            if(strokeRecord.get(i).combine(strokeRecord.get(i+1),getContext())){
                strokeRecord.remove(i+1);
            }
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
                ArrayList<Path> temp=new ArrayList();
                temp.add(drawPath);
                strokeRecord.add(new StrokeGroup(temp,currentcolor, System.currentTimeMillis()));
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
    public void setColor(int newColor, boolean isbutton){
        if(isbutton)
        currentcolor=newColor;
        drawPaint.setColor(newColor);
    }
    public void erase(){
        canvasBitmap.eraseColor(Color.TRANSPARENT);
        drawCanvas.drawBitmap(canvasBitmap, 0, 0, drawPaint);
        invalidate();
    }
    public void undo() {
        if (strokeRecord.size() > 0) {
            strokeRecord.remove(strokeRecord.size() - 1);
            invalidate();
        }
    }


     void copyFiles() {
        try {
            //location we want the file to be at
            String filepath = datapath + "/tessdata/eng.traineddata";

            //get access to AssetManager


            //open byte streams for reading/writing
            InputStream instream = assetManager.open("tessdata/eng.traineddata");
            OutputStream outstream = new FileOutputStream(filepath);

            //copy the file to the location specified by filepath
            byte[] buffer = new byte[1024];
            int read;
            while ((read = instream.read(buffer)) != -1) {
                outstream.write(buffer, 0, read);
            }
            outstream.flush();
            outstream.close();
            instream.close();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void checkFile(File dir) {
        //directory does not exist, but we can successfully create it
        if (!dir.exists()&& dir.mkdirs()){
            copyFiles();
        }
        //The directory exists, but there is no data file in it
        if(dir.exists()) {
            String datafilepath = datapath+ "/tessdata/eng.traineddata";
            File datafile = new File(datafilepath);
            if (!datafile.exists()) {
                copyFiles();
            }
        }

    }



}
