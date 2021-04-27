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
        setButtonFocus(false, false, false, false, false);
    }

    private void setPaintColor(int color) {
        drawingCanvas.setPaintColor(color);
    }

    public void fourthButton(View view) {
        setPaintColor(fourthButtonColor);
        setButtonFocus(false, false, false, true, false);
    }

    public void thirdButton(View view) {
        setPaintColor(thirdButtonColor);
        setButtonFocus(false, false, true, false, false);
    }

    public void secondButton(View view) {
        setPaintColor(secondButtonColor);
        setButtonFocus(false, true, false, false, false);
    }

    public void firstButton(View view) {
        setPaintColor(firstButtonColor);
        setButtonFocus(true, false, false, false, false);
    }

    public void ColorPickerButton(View view) {
        setButtonFocus(false, false, false, false, true);
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

    private void setButtonFocus(boolean firstBtnHasFocus, boolean secondBtnHasFocus, boolean thirdBtnHasFocus, boolean fourthBtnHasFocus, boolean colorBtnHasFocus) {
        Button firstButton = findViewById(R.id.drawing_first_button);
        Button secondButton = findViewById(R.id.drawing_second_button);
        Button thirdButton = findViewById(R.id.drawing_third_button);
        Button fourthButton = findViewById(R.id.drawing_fourth_button);
        Button colorChooseButton = findViewById(R.id.colorChoose);

        if(firstBtnHasFocus) {
            firstButton.setBackground(getDrawable(R.drawable.first_button_drawing_onfocus));
        }
        else {
            firstButton.setBackground(getDrawable(R.drawable.first_button_drawing));
        }
        if(secondBtnHasFocus) {
            secondButton.setBackground(getDrawable(R.drawable.second_button_drawing_onfocus));
        }
        else {
            secondButton.setBackground(getDrawable(R.drawable.second_button_drawing));
        }
        if(thirdBtnHasFocus) {
            thirdButton.setBackground(getDrawable(R.drawable.third_button_drawing_onfocus));
        }
        else {
            thirdButton.setBackground(getDrawable(R.drawable.third_button_drawing));
        }
        if(fourthBtnHasFocus) {
            fourthButton.setBackground(getDrawable(R.drawable.fourth_button_drawing_onfocus));
        }
        else {
            fourthButton.setBackground(getDrawable(R.drawable.fourth_button_drawing));
        }
        if(colorBtnHasFocus) {
            colorChooseButton.setBackground(getDrawable(R.drawable.colorpicker_button));
        }
        else {
            colorChooseButton.setBackground(getDrawable(R.drawable.colorpicker_button_onfocus));
        }
    }
}
