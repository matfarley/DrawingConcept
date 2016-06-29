package com.dev.scribble.drawingconcept;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;

import java.util.Stack;

/**
 * Created by matthewfarley on 29/06/16.
 */
public class DrawingUtils {


    public static void erasePath(Canvas canvas,
                           Path pathToErase,
                           Paint erasePaint){
        canvas.drawPath(pathToErase, erasePaint);
    }

    public static void drawPathStack(Canvas canvas,
                               Stack<Path> pathStack,
                               Paint paint){
        for(Path pathFromStack : pathStack){
            canvas.drawPath(pathFromStack, paint);
        }
    }
}
