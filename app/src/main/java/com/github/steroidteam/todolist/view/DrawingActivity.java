package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
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

    private int firstButtonColor;
    private int secondButtonColor;
    private int thirdButtonColor;
    private int fourthButtonColor;

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

        // SET COLOR OF BUTTONS:
        firstButtonColor = getColor(R.color.first_drawing_button);
        secondButtonColor = getColor(R.color.second_drawing_button);
        thirdButtonColor = getColor(R.color.third_drawing_button);
        fourthButtonColor = getColor(R.color.fourth_drawing_button);
        setFocusBackground(5);
    }

    private void setPaintColor(int color) {
        drawingCanvas.setPaintColor(color);
    }

    public void fourthButton(View view) {
        setPaintColor(fourthButtonColor);
        setFocusBackground(3);
    }

    public void thirdButton(View view) {
        setPaintColor(thirdButtonColor);
        setFocusBackground(2);
    }

    public void secondButton(View view) {
        setPaintColor(secondButtonColor);
        setFocusBackground(1);
    }

    public void firstButton(View view) {
        setPaintColor(firstButtonColor);
        setFocusBackground(0);
    }

    public void ColorPickerButton(View view) {
        setFocusBackground(4);
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

    public void backButton(View view) {
        Intent listSelectionActivity =
                new Intent(DrawingActivity.this, ListSelectionActivity.class);
        startActivity(listSelectionActivity);
    }

    private void setFocusBackground(int buttonIndex) {
        Button firstButton = findViewById(R.id.drawing_first_button);
        Button secondButton = findViewById(R.id.drawing_second_button);
        Button thirdButton = findViewById(R.id.drawing_third_button);
        Button fourthButton = findViewById(R.id.drawing_fourth_button);
        Button colorChooseButton = findViewById(R.id.colorChoose);
        switch (buttonIndex) {
            case 0:
                firstButton.setBackground(getDrawable(R.drawable.first_button_drawing_onfocus));
                secondButton.setBackground(getDrawable(R.drawable.second_button_drawing));
                thirdButton.setBackground(getDrawable(R.drawable.third_button_drawing));
                fourthButton.setBackground(getDrawable(R.drawable.fourth_button_drawing));
                colorChooseButton.setBackground(getDrawable(R.drawable.colorpicker_button));
                break;
            case 1:
                firstButton.setBackground(getDrawable(R.drawable.first_button_drawing));
                secondButton.setBackground(getDrawable(R.drawable.second_button_drawing_onfocus));
                thirdButton.setBackground(getDrawable(R.drawable.third_button_drawing));
                fourthButton.setBackground(getDrawable(R.drawable.fourth_button_drawing));
                colorChooseButton.setBackground(getDrawable(R.drawable.colorpicker_button));
                break;
            case 2:
                firstButton.setBackground(getDrawable(R.drawable.first_button_drawing));
                secondButton.setBackground(getDrawable(R.drawable.second_button_drawing));
                thirdButton.setBackground(getDrawable(R.drawable.third_button_drawing_onfocus));
                fourthButton.setBackground(getDrawable(R.drawable.fourth_button_drawing));
                colorChooseButton.setBackground(getDrawable(R.drawable.colorpicker_button));
                break;
            case 3:
                firstButton.setBackground(getDrawable(R.drawable.first_button_drawing));
                secondButton.setBackground(getDrawable(R.drawable.second_button_drawing));
                thirdButton.setBackground(getDrawable(R.drawable.third_button_drawing));
                fourthButton.setBackground(getDrawable(R.drawable.fourth_button_drawing_onfocus));
                colorChooseButton.setBackground(getDrawable(R.drawable.colorpicker_button));
                break;
            case 4:
                firstButton.setBackground(getDrawable(R.drawable.first_button_drawing));
                secondButton.setBackground(getDrawable(R.drawable.second_button_drawing));
                thirdButton.setBackground(getDrawable(R.drawable.third_button_drawing));
                fourthButton.setBackground(getDrawable(R.drawable.fourth_button_drawing));
                colorChooseButton.setBackground(getDrawable(R.drawable.colorpicker_button_onfocus));
                break;
            default:
                firstButton.setBackground(getDrawable(R.drawable.first_button_drawing));
                secondButton.setBackground(getDrawable(R.drawable.second_button_drawing));
                thirdButton.setBackground(getDrawable(R.drawable.third_button_drawing));
                fourthButton.setBackground(getDrawable(R.drawable.fourth_button_drawing));
                colorChooseButton.setBackground(getDrawable(R.drawable.colorpicker_button));
                break;
        }
    }
}
