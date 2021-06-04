package com.github.steroidteam.todolist;

import static com.github.steroidteam.todolist.util.Utils.getRoundedBitmap;
import static com.github.steroidteam.todolist.util.Utils.scaleBitmap;
import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.platform.app.InstrumentationRegistry;
import com.github.steroidteam.todolist.util.Utils;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class UtilsTest {

    @Test
    public void simpleScaledBitmapTest() {

        int newWidth = 23;
        int newHeight = 27;

        Bitmap bitmap = Bitmap.createBitmap(10, 20, Bitmap.Config.ARGB_8888);

        Canvas canvas = new Canvas(bitmap);
        Paint myPaint = new Paint();
        myPaint.setColor(Color.BLUE);
        canvas.drawRect(0, 0, 10, 10, myPaint);

        Bitmap scaledBitmap = scaleBitmap(bitmap, newWidth, newHeight);

        assertEquals(newWidth, scaledBitmap.getWidth());
        assertEquals(newHeight, scaledBitmap.getHeight());
    }

    @Test
    public void simpleRoundedBitmapTest() {

        int newWidth = 40;
        int newHeight = 40;

        Bitmap bitmap = Bitmap.createBitmap(newWidth, newHeight, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.BLACK);
        canvas.drawRect(0, 0, newWidth, newHeight, paint);

        // make the corner rounded
        Bitmap scaledBitmap = getRoundedBitmap(bitmap, 10f, 10f);

        int center = scaledBitmap.getColor(20, 20).toArgb();
        int topLeft = scaledBitmap.getColor(1, 1).toArgb();
        int topRight = scaledBitmap.getColor(39, 1).toArgb();
        int bottomLeft = scaledBitmap.getColor(1, 39).toArgb();
        int bottomRight = scaledBitmap.getColor(39, 39).toArgb();

        assertEquals(Color.BLACK, center);
        assertEquals(Color.TRANSPARENT, topLeft);
        assertEquals(Color.TRANSPARENT, topRight);
        assertEquals(Color.TRANSPARENT, bottomLeft);
        assertEquals(Color.TRANSPARENT, bottomRight);
    }

    @Test
    public void dip2pxWorks() {
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        float scale = context.getResources().getDisplayMetrics().density;
        float dpInput = 10;

        int pxExpected = (int) (dpInput * scale + 0.5f);

        assertEquals(pxExpected, Utils.dip2px(context, dpInput));
    }
}
