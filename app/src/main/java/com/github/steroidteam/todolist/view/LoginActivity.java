package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Continue to the MainActivity if the user has already logged in.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Store the current user in the UserFactory.
            UserFactory.set(user);

            startApp();
            return;
        }

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        Intent authIntent =
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.asteroid_banner)
                        .build();

        ActivityResultLauncher<Intent> authLauncher =
                registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(), this::loginCallback);
        authLauncher.launch(authIntent);
    }

    private void loginCallback(ActivityResult result) {
        if (result.getResultCode() == RESULT_OK) {
            startApp();
        } else {
            Toast.makeText(this, "Sign in failed", Toast.LENGTH_LONG).show();
        }
    }

    private void startApp() {
        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        // Remove the activity from the stack, so the user cannot go back to it with the device's
        // "Back" button.
        finish();
        startActivity(mainActivity);
    }
}
