package com.dev.scribble.drawingconcept;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.Stack;

/**
 * Created by matthewfarley on 28/06/16.
 */
public class DrawingView extends View{

    private static final int ERASE_STROKE_PADDING = 2;

    private Path drawPath;
    private Paint drawPaint;
    private Paint erasePaint;
    private Paint canvasPaint;
    private Canvas drawCanvas;
    private Bitmap canvasBitmap;

    private int eraseStrokeWidth;
    private int drawStrokeWidth;

    // Stack of drawing paths for undo.
    Stack<Path> pathUndoStack;

    //

    public DrawingView(Context context) {
        super(context);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public DrawingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init();
    }

    private void init(){
        //prepare for drawing and setup paint stroke properties
        drawStrokeWidth = getResources().getInteger(R.integer.stroke_width);
        eraseStrokeWidth = drawStrokeWidth + ERASE_STROKE_PADDING;

        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(getResources().getColor(R.color.drawing_color));
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(drawStrokeWidth);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);

        erasePaint = new Paint(drawPaint);
        erasePaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        erasePaint.setStrokeWidth(eraseStrokeWidth);

        canvasPaint = new Paint(Paint.DITHER_FLAG);

        // TODO: find out why this isn't respected.
        canvasPaint.setColor(getResources().getColor(R.color.canvas_color));

        pathUndoStack = new Stack<>();
    }

    //size assigned to view
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    //draw the view - will be called after touch event
    @Override
    protected void onDraw(Canvas canvas) {
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    //register user touches as drawing action
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        //respond to down, move and up events
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                break;
            case MotionEvent.ACTION_UP:
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                pathUndoStack.add(new Path(drawPath));
                drawPath.reset();
                break;
            default:
                return false;
        }
        invalidate();
        return true;
    }

    //start new drawing
    // TODO: does this work?
    public void startNew(){
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    // TODO: Move Drawing, Erasing and Bitmap exporting to a seperate class.

    public void eraseLast(){
        if(!pathUndoStack.isEmpty()){
            Path path = pathUndoStack.pop();
            drawCanvas.drawPath(path, erasePaint);

            // Make sure that the undo doesn't cut the line of a previous layer
            for(Path pathFromStack : pathUndoStack){
                drawCanvas.drawPath(pathFromStack, drawPaint);
            }
        }
        invalidate();
    }

    //TODO: get bitmap
    public Bitmap getDrawingBitmap(){
        return null;
    }
}
