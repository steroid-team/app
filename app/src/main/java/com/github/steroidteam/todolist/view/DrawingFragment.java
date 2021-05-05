package com.github.steroidteam.todolist.view;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
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

    private Button firstButton;
    private Button secondButton;
    private Button thirdButton;
    private Button fourthButton;
    private Button colorChooseButton;

    private final char FIRST_BUTTON = 1;
    private final char SECOND_BUTTON = 2;
    private final char THIRD_BUTTON = 3;
    private final char FOURTH_BUTTON = 4;
    private final char COLOR_CHOOSE_BUTTON = 0;
    private final char NONE = 9;

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

        setUpButtonAndColor(root);

        return root;
    }

    private void setUpButtonAndColor(View root) {
        // SET BUTTON
        firstButton = root.findViewById(R.id.drawing_first_button);
        secondButton = root.findViewById(R.id.drawing_second_button);
        thirdButton = root.findViewById(R.id.drawing_third_button);
        fourthButton = root.findViewById(R.id.drawing_fourth_button);
        colorChooseButton = root.findViewById(R.id.colorChoose);

        // SET COLOR OF BUTTONS:
        firstButtonColor = getActivity().getColor(R.color.first_drawing_button);
        secondButtonColor = getActivity().getColor(R.color.second_drawing_button);
        thirdButtonColor = getActivity().getColor(R.color.third_drawing_button);
        fourthButtonColor = getActivity().getColor(R.color.fourth_drawing_button);
        setButtonFocus(NONE);
    }

    private void setPaintColor(int color) {
        drawingCanvas.setPaintColor(color);
    }

    public void fourthButton(View view) {
        setPaintColor(fourthButtonColor);
        setButtonFocus(FOURTH_BUTTON);
    }

    public void thirdButton(View view) {
        setPaintColor(thirdButtonColor);
        setButtonFocus(THIRD_BUTTON);
    }

    public void secondButton(View view) {
        setPaintColor(secondButtonColor);
        setButtonFocus(SECOND_BUTTON);
    }

    public void firstButton(View view) {
        setPaintColor(firstButtonColor);
        setButtonFocus(FIRST_BUTTON);
    }

    public void ColorPickerButton(View view) {
        setButtonFocus(COLOR_CHOOSE_BUTTON);
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

    private void setButtonFocus(int indexButtonFocus) {

        firstButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.first_button_drawing));
        secondButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.second_button_drawing));
        thirdButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.third_button_drawing));
        fourthButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.fourth_button_drawing));
        colorChooseButton.setBackground(ContextCompat.getDrawable(getActivity(), R.drawable.colorpicker_button));

        switch(indexButtonFocus) {
            case FIRST_BUTTON:
                firstButton.setBackground(
                        ContextCompat.getDrawable(getActivity(), R.drawable.first_button_drawing_onfocus));
                break;
            case SECOND_BUTTON:
                secondButton.setBackground(
                        ContextCompat.getDrawable(getActivity(), R.drawable.second_button_drawing_onfocus));
                break;
            case THIRD_BUTTON:
                thirdButton.setBackground(
                        ContextCompat.getDrawable(getActivity(), R.drawable.third_button_drawing_onfocus));
                break;
            case FOURTH_BUTTON:
                fourthButton.setBackground(
                        ContextCompat.getDrawable(getActivity(), R.drawable.fourth_button_drawing_onfocus));
                break;
            case COLOR_CHOOSE_BUTTON:
                colorChooseButton.setBackground(
                        ContextCompat.getDrawable(getActivity(), R.drawable.colorpicker_button_onfocus));
                break;
            default:
        }
    }
}
