package com.github.steroidteam.todolist.user;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class FBaseUser implements User {
    /* FIXME : There are some cases where getCurrentUser will return a non-null FirebaseUser
     * but the underlying token is not valid. In this case, you may get a valid user
     * getCurrentUser but subsequent calls to authenticated resources will fail.
     */
    private FirebaseUser firebaseUser;
    private final FirebaseAuth auth;
    private final ArrayList<UserStateListener> stateListeners;

    public FBaseUser(FirebaseAuth auth) {
        stateListeners = new ArrayList<>();
        this.auth = auth;
        this.firebaseUser = auth.getCurrentUser();
        auth.addAuthStateListener(firebaseAuth -> {
            this.firebaseUser = auth.getCurrentUser();
            notifiyListeners();
        });
    }

    @Override
    public boolean isLoggedIn() {
        return firebaseUser != null;
    }

    @Override
    public String getUserId() throws UserLoginException {
        checkUserIsLoggedIn();
        return firebaseUser.getUid();
    }

    @Override
    public String getDisplayName() throws UserLoginException {
        checkUserIsLoggedIn();
        return firebaseUser.getDisplayName();
    }

    @Override
    public String getEmail() throws UserLoginException {
        checkUserIsLoggedIn();
        return firebaseUser.getEmail();
    }

    @Override
    public boolean isEmailVerified() throws UserLoginException {
        checkUserIsLoggedIn();
        return firebaseUser.isEmailVerified();
    }

    @Override
    public void updateDisplayName(String newDisplayName, OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        checkUserIsLoggedIn();
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newDisplayName)
                .build();

        firebaseUser.updateProfile(profileUpdates)
                .addOnCompleteListener(onComplete);
    }

    @Override
    public void updateEmail(String newEmail, OnCompleteListener<Void> onComplete)
            throws UserLoginException{
        checkUserIsLoggedIn();
        firebaseUser.updateEmail(newEmail).addOnCompleteListener(onComplete);
    }

    @Override
    public void sendEmailVerification(OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        checkUserIsLoggedIn();
        firebaseUser.sendEmailVerification().addOnCompleteListener(onComplete);
    }

    @Override
    public void setUserPassword(String newPassword, OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        checkUserIsLoggedIn();
        firebaseUser.updatePassword(newPassword).addOnCompleteListener(onComplete);
    }

    @Override
    public void sendPasswordResetEmail(OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        checkUserIsLoggedIn();
        auth.sendPasswordResetEmail(firebaseUser.getEmail()).addOnCompleteListener(onComplete);
    }

    @Override
    public void deleteUser(OnCompleteListener<Void> onComplete) throws UserLoginException {
        checkUserIsLoggedIn();
        firebaseUser.delete().addOnCompleteListener(onComplete);
    }

    @Override
    public void reauthenticate(OnCompleteListener<Void> onComplete) {
        /* TODO : this is tricky. There are multiple providers :
        *  Google, Facebook for example, email... */
    }

    @Override
    public void logOut() {
        auth.signOut();
    }

    @Override
    public void addUserStateListener(UserStateListener listener) {
        stateListeners.add(listener);
    }

    @Override
    public void removeUserStateListener(UserStateListener listener) {
        stateListeners.remove(listener);
    }

    private void checkUserIsLoggedIn() throws UserLoginException {
        if ((firebaseUser = auth.getCurrentUser()) == null)
            throw new UserLoginException("User is not logged in");
    }

    private void notifiyListeners() {
        for (UserStateListener listener: stateListeners) {
            listener.onStateChanged(this);
        }
    }
}
