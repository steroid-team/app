package com.github.steroidteam.todolist.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.appcompat.app.AppCompatActivity;
import com.github.steroidteam.todolist.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class DrawingActivity extends AppCompatActivity {

    public DrawingView drawingCanvas;
    private ColorPicker colorPicker;
    private LinearLayout colorPickerWindow;
    private FrameLayout canvasLayout;
    private SaturationBar saturationBar;
    private ValueBar valueBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_drawing);
        drawingCanvas = new DrawingView(DrawingActivity.this);
        canvasLayout = findViewById(R.id.drawSpace);
        canvasLayout.addView(drawingCanvas);
        colorPicker = findViewById(R.id.colorPicker);
        colorPickerWindow = findViewById(R.id.colorPickerWindow);
        saturationBar = (SaturationBar) findViewById(R.id.saturationbar);
        valueBar = (ValueBar) findViewById(R.id.valuebar);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addValueBar(valueBar);
    }

    private void setPaintColor(int color) {
        System.out.println("SetPaint");
        drawingCanvas.setPaintColor(color);
    }

    public void blackButton(View view) {
        setPaintColor(Color.BLACK);
    }

    public void blueButton(View view) {
        setPaintColor(Color.BLUE);
    }

    public void greenButton(View view) {
        setPaintColor(Color.GREEN);
    }

    public void redButton(View view) {
        System.out.println("Clicked");
        setPaintColor(Color.RED);
    }

    public void ColorPickerButton(View view) {
        drawingCanvas.setVisibility(View.GONE);
        colorPickerWindow.setVisibility(View.VISIBLE);
        colorPicker.setOldCenterColor(drawingCanvas.getPaint().getColor());
    }

    public void cancelColorButton(View view) {
        colorPickerWindow.setVisibility(View.GONE);
        drawingCanvas.setVisibility(View.VISIBLE);
    }

    public void applyColorButton(View view) {
        setPaintColor(colorPicker.getColor());
        colorPickerWindow.setVisibility(View.GONE);
        drawingCanvas.setVisibility(View.VISIBLE);
    }

    public void eraseButton(View view) {
        drawingCanvas.erase();
    }
}
