package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
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

    private int firstButtonColor;
    private int secondButtonColor;
    private int thirdButtonColor;
    private int fourthButtonColor;

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_drawing, container, false);

        canvasLayout = root.findViewById(R.id.drawSpace);
        colorPicker = root.findViewById(R.id.colorPicker);
        colorPickerWindow = root.findViewById(R.id.colorPickerWindow);
        saturationBar = root.findViewById(R.id.saturationbar);
        valueBar = root.findViewById(R.id.valuebar);

        root.findViewById(R.id.drawing_first_button).setOnClickListener(this::firstButton);
        root.findViewById(R.id.drawing_second_button).setOnClickListener(this::secondButton);
        root.findViewById(R.id.drawing_third_button).setOnClickListener(this::thirdButton);
        root.findViewById(R.id.drawing_fourth_button).setOnClickListener(this::fourthButton);
        root.findViewById(R.id.erase_button).setOnClickListener(this::eraseButton);
        root.findViewById(R.id.colorChoose).setOnClickListener(this::ColorPickerButton);
        root.findViewById(R.id.cancelColor).setOnClickListener(this::cancelColorButton);
        root.findViewById(R.id.applyColor).setOnClickListener(this::applyColorButton);
        root.findViewById(R.id.backButton)
                .setOnClickListener((view) -> getParentFragmentManager().popBackStack());

        drawingCanvas = new DrawingView(getContext());
        canvasLayout.addView(drawingCanvas);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addValueBar(valueBar);

        // SET COLOR OF BUTTONS:
        firstButtonColor = getActivity().getColor(R.color.first_drawing_button);
        secondButtonColor = getActivity().getColor(R.color.second_drawing_button);
        thirdButtonColor = getActivity().getColor(R.color.third_drawing_button);
        fourthButtonColor = getActivity().getColor(R.color.fourth_drawing_button);
        setButtonFocus(false, false, false, false, false);

        return root;
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

    private void setButtonFocus(
            boolean firstBtnHasFocus,
            boolean secondBtnHasFocus,
            boolean thirdBtnHasFocus,
            boolean fourthBtnHasFocus,
            boolean colorBtnHasFocus) {
        Button firstButton = getActivity().findViewById(R.id.drawing_first_button);
        Button secondButton = getActivity().findViewById(R.id.drawing_second_button);
        Button thirdButton = getActivity().findViewById(R.id.drawing_third_button);
        Button fourthButton = getActivity().findViewById(R.id.drawing_fourth_button);
        Button colorChooseButton = getActivity().findViewById(R.id.colorChoose);

        if (firstBtnHasFocus) {
            firstButton.setBackground(
                    getActivity().getDrawable(R.drawable.first_button_drawing_onfocus));
        } else {
            firstButton.setBackground(getActivity().getDrawable(R.drawable.first_button_drawing));
        }
        if (secondBtnHasFocus) {
            secondButton.setBackground(
                    getActivity().getDrawable(R.drawable.second_button_drawing_onfocus));
        } else {
            secondButton.setBackground(getActivity().getDrawable(R.drawable.second_button_drawing));
        }
        if (thirdBtnHasFocus) {
            thirdButton.setBackground(
                    getActivity().getDrawable(R.drawable.third_button_drawing_onfocus));
        } else {
            thirdButton.setBackground(getActivity().getDrawable(R.drawable.third_button_drawing));
        }
        if (fourthBtnHasFocus) {
            fourthButton.setBackground(
                    getActivity().getDrawable(R.drawable.fourth_button_drawing_onfocus));
        } else {
            fourthButton.setBackground(getActivity().getDrawable(R.drawable.fourth_button_drawing));
        }
        if (colorBtnHasFocus) {
            colorChooseButton.setBackground(
                    getActivity().getDrawable(R.drawable.colorpicker_button_onfocus));
        } else {
            colorChooseButton.setBackground(
                    getActivity().getDrawable(R.drawable.colorpicker_button));
        }
    }
}
