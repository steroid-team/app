package com.github.steroidteam.todolist.view;

import android.graphics.Bitmap;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
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
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

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

    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View root = inflater.inflate(R.layout.fragment_drawing, container, false);

        canvasLayout = root.findViewById(R.id.drawing_space);
        colorPicker = root.findViewById(R.id.drawing_color_picker);
        colorPickerWindow = root.findViewById(R.id.drawing_color_picker_layout);
        saturationBar = root.findViewById(R.id.drawing_saturation_bar);
        valueBar = root.findViewById(R.id.drawing_value_bar);

        root.findViewById(R.id.drawing_first_btn).setOnClickListener(this::firstButton);
        root.findViewById(R.id.drawing_second_btn).setOnClickListener(this::secondButton);
        root.findViewById(R.id.drawing_third_btn).setOnClickListener(this::thirdButton);
        root.findViewById(R.id.drawing_fourth_btn).setOnClickListener(this::fourthButton);
        root.findViewById(R.id.drawing_erase_btn).setOnClickListener(this::eraseButton);
        root.findViewById(R.id.drawing_fifth_btn).setOnClickListener(this::ColorPickerButton);
        root.findViewById(R.id.drawing_cancel_color_btn)
                .setOnClickListener(this::cancelColorButton);
        root.findViewById(R.id.drawing_apply_color_btn).setOnClickListener(this::applyColorButton);
        root.findViewById(R.id.drawing_back_btn)
                .setOnClickListener((view) -> getParentFragmentManager().popBackStack());
        root.findViewById(R.id.drawing_save_btn).setOnClickListener(this::saveButton);

        drawingCanvas = new DrawingView(getContext());
        canvasLayout.addView(drawingCanvas);
        colorPicker.addSaturationBar(saturationBar);
        colorPicker.addValueBar(valueBar);

        setUpButtonAndColor(root);

        return root;
    }

    private void setUpButtonAndColor(View root) {
        // SET BUTTON
        firstButton = root.findViewById(R.id.drawing_first_btn);
        secondButton = root.findViewById(R.id.drawing_second_btn);
        thirdButton = root.findViewById(R.id.drawing_third_btn);
        fourthButton = root.findViewById(R.id.drawing_fourth_btn);
        colorChooseButton = root.findViewById(R.id.drawing_fifth_btn);

        // SET COLOR OF BUTTONS:
        firstButtonColor = getActivity().getColor(R.color.first_drawing_button);
        secondButtonColor = getActivity().getColor(R.color.second_drawing_button);
        thirdButtonColor = getActivity().getColor(R.color.third_drawing_button);
        fourthButtonColor = getActivity().getColor(R.color.fourth_drawing_button);
        setButtonBackground();
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

    public void saveButton(View view) {
        String fileName = "bitmap" + UUID.randomUUID().toString() + ".png";
        File bitmapFile =
                new File(
                        Environment.getExternalStoragePublicDirectory(
                                Environment.DIRECTORY_PICTURES),
                        fileName);
        try (FileOutputStream output = new FileOutputStream(bitmapFile)) {
            drawingCanvas.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, output);
            output.close();
            MediaScannerConnection.scanFile(this.getContext(),
                    new String[] { bitmapFile.toString() }, null,
                    (MediaScannerConnection.OnScanCompletedListener) (path, uri) -> {
                        NoteDisplayFragment.lastDrawingUri = ("file://" + path);
                        getParentFragmentManager().popBackStack();
                    });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void eraseButton(View view) {
        drawingCanvas.erase();
    }

    private void setButtonFocus(int indexButtonFocus) {

        setButtonBackground();

        switch (indexButtonFocus) {
            case FIRST_BUTTON:
                firstButton.setBackground(
                        ContextCompat.getDrawable(
                                getActivity(), R.drawable.first_button_drawing_onfocus));
                break;
            case SECOND_BUTTON:
                secondButton.setBackground(
                        ContextCompat.getDrawable(
                                getActivity(), R.drawable.second_button_drawing_onfocus));
                break;
            case THIRD_BUTTON:
                thirdButton.setBackground(
                        ContextCompat.getDrawable(
                                getActivity(), R.drawable.third_button_drawing_onfocus));
                break;
            case FOURTH_BUTTON:
                fourthButton.setBackground(
                        ContextCompat.getDrawable(
                                getActivity(), R.drawable.fourth_button_drawing_onfocus));
                break;
            case COLOR_CHOOSE_BUTTON:
                colorChooseButton.setBackground(
                        ContextCompat.getDrawable(
                                getActivity(), R.drawable.colorpicker_button_onfocus));
                break;
            default:
        }
    }

    private void setButtonBackground() {
        firstButton.setBackground(
                ContextCompat.getDrawable(getActivity(), R.drawable.first_button_drawing));
        secondButton.setBackground(
                ContextCompat.getDrawable(getActivity(), R.drawable.second_button_drawing));
        thirdButton.setBackground(
                ContextCompat.getDrawable(getActivity(), R.drawable.third_button_drawing));
        fourthButton.setBackground(
                ContextCompat.getDrawable(getActivity(), R.drawable.fourth_button_drawing));
        colorChooseButton.setBackground(
                ContextCompat.getDrawable(getActivity(), R.drawable.colorpicker_button));
    }
}
