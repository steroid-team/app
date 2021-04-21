package com.github.steroidteam.todolist;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import androidx.test.espresso.intent.rule.IntentsTestRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.rule.GrantPermissionRule;
import com.github.steroidteam.todolist.view.NoteDisplayActivity;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class NoteDisplayActivityTest {
    /*@Rule
    public ActivityScenarioRule<NoteDisplayActivity> activityRule =
            new ActivityScenarioRule<>(NoteDisplayActivity.class);*/

    @Rule
    public IntentsTestRule<NoteDisplayActivity> mIntentsRule =
            new IntentsTestRule<>(NoteDisplayActivity.class);

    @Rule
    public GrantPermissionRule permissionRule =
            GrantPermissionRule.grant(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);

    @Before
    public void stubImagePickerIntent() {
        savePickedImage();
        Instrumentation.ActivityResult result = createImageGallerySetResultStub();
        intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(result);
    }

    @Test
    public void saveTextWorks() {
        onView(withId(R.id.activity_notedisplay_save)).perform(click());
    }

    @Test
    public void filePickingWorks() {
        /*activityRule.getScenario().onActivity( activity -> {
            savePickedImage(activity);
            Instrumentation.ActivityResult res = createImageGallerySetResultStub(activity);
            intending(hasAction(Intent.ACTION_CHOOSER)).respondWith(res);
        });*/
        onView(withId(R.id.camera_button)).perform(click());
    }

    private void savePickedImage() {
        Bitmap bm =
                BitmapFactory.decodeResource(
                        mIntentsRule.getActivity().getResources(),
                        R.mipmap.asteroid_launcher_foreground);
        File dir = mIntentsRule.getActivity().getExternalCacheDir();
        File file = new File(dir.getPath(), "pickImageResult.jpeg");
        FileOutputStream outStream;
        try {
            outStream = new FileOutputStream(file);
            bm.compress(Bitmap.CompressFormat.JPEG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Instrumentation.ActivityResult createImageGallerySetResultStub() {
        Bundle bundle = new Bundle();
        ArrayList<Parcelable> parcels = new ArrayList<>();
        Intent resultData = new Intent();
        File dir = mIntentsRule.getActivity().getExternalCacheDir();
        File file = new File(dir.getPath(), "pickImageResult.jpeg");
        Uri uri = Uri.fromFile(file);
        Parcelable parcelable1 = (Parcelable) uri;
        parcels.add(parcelable1);
        bundle.putParcelableArrayList(Intent.EXTRA_STREAM, parcels);
        resultData.putExtras(bundle);
        return new Instrumentation.ActivityResult(Activity.RESULT_OK, resultData);
    }
}
