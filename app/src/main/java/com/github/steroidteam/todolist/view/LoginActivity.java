package com.github.steroidteam.todolist.view;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.firebase.ui.auth.AuthUI;
import com.github.steroidteam.todolist.R;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import java.util.Arrays;
import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Continue to the MainActivity if the user has already logged in.
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            startApp();
            return;
        }

        // Choose authentication providers
        List<AuthUI.IdpConfig> providers =
                Arrays.asList(
                        new AuthUI.IdpConfig.EmailBuilder().build(),
                        new AuthUI.IdpConfig.GoogleBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .setTheme(R.style.LoginTheme)
                        .setLogo(R.drawable.asteroid_banner)
                        .build(),
                RC_SIGN_IN);
    }

    private void startApp() {
        // Store the current user in the UserFactory.
        UserFactory.set(FirebaseAuth.getInstance().getCurrentUser());

        Intent mainActivity = new Intent(LoginActivity.this, MainActivity.class);
        // Remove the activity from the stack, so the user cannot go back to it with the device's
        // "Back" button.
        finish();
        startActivity(mainActivity);
    }
}
