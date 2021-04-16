package com.github.steroidteam.todolist.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.github.steroidteam.todolist.R;

public class DrawingActivity extends AppCompatActivity {

    public DrawingView drawingCanvas;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        drawingCanvas = new DrawingView(DrawingActivity.this);
        FrameLayout canvasLayout = findViewById(R.id.drawSpace);
        canvasLayout.addView(drawingCanvas);
    }

    public void redButton(View view) {
        drawingCanvas.setColor(Color.RED);
    }

    public void greenButton(View view) {
        drawingCanvas.setColor(Color.GREEN);
    }

    public void blueButton(View view) {
        drawingCanvas.setColor(Color.BLUE);
    }

    public void blackButton(View view) {
        drawingCanvas.setColor(Color.BLACK);
    }

    public void eraseButton(View view) {
        drawingCanvas.erase();
    }
}
