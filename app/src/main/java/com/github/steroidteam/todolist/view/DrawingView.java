package com.github.steroidteam.todolist.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.view.MotionEvent;
import android.view.View;

public class DrawingView extends View {

    private Paint paint;
    private Path path;
    private Canvas canvas;
    private Bitmap bitmap;
    private float xPath, yPath;
    private static final float TOUCH_TOLERANCE = 4;

    public DrawingView(Context context) {
        super(context);
        path = new Path();
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setAntiAlias(true);
        paint.setDither(true);
        paint.setStyle(Paint.Style.STROKE); // default: FILL
        paint.setStrokeJoin(Paint.Join.ROUND); // default: MITER
        paint.setStrokeCap(Paint.Cap.ROUND); // default: BUTT
        paint.setStrokeWidth(12); // default: Hairline-width (really thin)
    }

    @Override
    protected void onSizeChanged(int width, int height, int oldWidth, int oldHeight) {
        super.onSizeChanged(width, height, oldWidth, oldHeight);
        bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float x = event.getX();
        float y = event.getY();

        // Invalidate() is inside the case statements because there are many
        // other types of motion events passed into this listener,
        // and we don't want to invalidate the view for those.
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                touchStart(x, y);
                // No need to invalidate because we are not drawing anything.
                break;
            case MotionEvent.ACTION_MOVE:
                touchMove(x, y);
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                path.reset();
                // No need to invalidate because we are not drawing anything.
                break;
            default:
                // Do nothing.
        }
        return true;
    }

    private void touchStart(float x, float y) {
        path.moveTo(x, y);
        xPath = x;
        yPath = y;
    }

    public void touchMove(float x, float y) {
        float dx = Math.abs(x - xPath);
        float dy = Math.abs(y - yPath);
        if (dx >= TOUCH_TOLERANCE || dy >= TOUCH_TOLERANCE) {
            // QuadTo() adds a quadratic bezier from the last point,
            // approaching control point (x1,y1), and ending at (x2,y2).
            path.quadTo(xPath, yPath, (x + xPath) / 2, (y + yPath) / 2);
            // Reset mX and mY to the last drawn point.
            xPath = x;
            yPath = y;
            // Save the path in the extra bitmap,
            // which we access through its canvas.
            canvas.drawPath(path, paint);
        }
    }

    public void setColor(int color) {
        paint.setColor(color);
    }

    public Paint getPaint() {
        return paint;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void erase() {
        canvas.drawColor(Color.rgb(235, 235, 235));
    }
}