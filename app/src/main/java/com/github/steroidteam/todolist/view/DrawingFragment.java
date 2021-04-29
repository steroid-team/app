package com.github.steroidteam.todolist.view;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.github.steroidteam.todolist.R;
import com.larswerkman.holocolorpicker.ColorPicker;
import com.larswerkman.holocolorpicker.SaturationBar;
import com.larswerkman.holocolorpicker.ValueBar;

public class DrawingFragment extends Fragment {

    public DrawingView drawingCanvas;
    private ColorPicker colorPicker;
    private LinearLayout colorPickerWindow;
    private FrameLayout canvasLayout;
    private SaturationBar saturationBar;
    private ValueBar valueBar;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_drawing, container, false);

        canvasLayout = root.findViewById(R.id.drawSpace);
        colorPicker = root.findViewById(R.id.colorPicker);
        colorPickerWindow = root.findViewById(R.id.colorPickerWindow);
        saturationBar = root.findViewById(R.id.saturationbar);
        valueBar = root.findViewById(R.id.valuebar);

        root.findViewById(R.id.colorRed).setOnClickListener(this::redButton);
        root.findViewById(R.id.colorGreen).setOnClickListener(this::greenButton);
        root.findViewById(R.id.colorBlue).setOnClickListener(this::blueButton);
        root.findViewById(R.id.colorBlack).setOnClickListener(this::blackButton);
        root.findViewById(R.id.erase_button).setOnClickListener(this::eraseButton);
        root.findViewById(R.id.colorChoose).setOnClickListener(this::ColorPickerButton);
        root.findViewById(R.id.cancelColor).setOnClickListener(this::cancelColorButton);
        root.findViewById(R.id.applyColor).setOnClickListener(this::applyColorButton);
        root.findViewById(R.id.backButton).setOnClickListener((view) -> getParentFragmentManager().popBackStack());

        drawingCanvas = new DrawingView(getContext());
        canvasLayout.addView(drawingCanvas);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addValueBar(valueBar);

        return root;
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
