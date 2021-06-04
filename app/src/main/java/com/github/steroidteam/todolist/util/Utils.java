package com.github.steroidteam.todolist.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import androidx.annotation.NonNull;
import java.util.Objects;

public class Utils {

    public static void hideKeyboard(@NonNull View view) {
        final InputMethodManager imm =
                (InputMethodManager)
                        view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    public static void checkNonNullArgs(Object... objects) {
        for (Object o : objects) Objects.requireNonNull(o);
    }

    public static Bitmap scaleBitmap(Bitmap bitmap, int width, int height) {
        Bitmap background = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        float bitmapWidth = bitmap.getWidth();
        float bitmapHeight = bitmap.getHeight();

        Canvas canvas = new Canvas(background);

        float scale = width / bitmapWidth;

        float xTranslation = 0.0f;
        float yTranslation = (height - bitmapHeight * scale) / 2.0f;

        Matrix transformation = new Matrix();
        transformation.postTranslate(xTranslation, yTranslation);
        transformation.preScale(scale, scale);

        Paint paint = new Paint();
        paint.setFilterBitmap(true);

        canvas.drawBitmap(bitmap, transformation, paint);

        return background;
    }

    public static Bitmap getRoundedBitmap(Bitmap bitmap, float radiusTop, float radiusBottom) {
        Bitmap output =
                Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        paint.setAntiAlias(true);
        Path path = new Path();
        float[] corners =
                new float[] {
                    radiusTop,
                    radiusTop, // Top left radius in px
                    radiusTop,
                    radiusTop, // Top right radius in px
                    radiusBottom,
                    radiusBottom, // Bottom right radius in px
                    radiusBottom,
                    radiusBottom // Bottom left radius in px
                };
        path.addRoundRect(rectF, corners, Path.Direction.CW);
        canvas.drawPath(path, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
