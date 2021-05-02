package com.github.steroidteam.todolist.user;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import java.util.ArrayList;

public class FirebaseUser implements User {
    /* FIXME : There are some cases where getCurrentUser will return a non-null FirebaseUser
     * but the underlying token is not valid. In this case, you may get a valid user
     * getCurrentUser but subsequent calls to authenticated resources will fail.
     */
    private final FirebaseAuth auth;
    private final ArrayList<UserStateListener> stateListeners;

    public FirebaseUser(FirebaseAuth auth) {
        stateListeners = new ArrayList<>();
        this.auth = auth;
        auth.addAuthStateListener(
                firebaseAuth -> {
                    notifiyListeners();
                });
    }

    @Override
    public boolean isLoggedIn() {
        return auth.getCurrentUser() != null;
    }

    @Override
    public String getUserId() throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        return user.getUid();
    }

    @Override
    public String getDisplayName() throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        return user.getDisplayName();
    }

    @Override
    public String getEmail() throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        return user.getEmail();
    }

    @Override
    public boolean isEmailVerified() throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        return user.isEmailVerified();
    }

    @Override
    public void updateDisplayName(String newDisplayName, OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        UserProfileChangeRequest profileUpdates =
                new UserProfileChangeRequest.Builder().setDisplayName(newDisplayName).build();

        user.updateProfile(profileUpdates).addOnCompleteListener(onComplete);
    }

    @Override
    public void updateEmail(String newEmail, OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        user.updateEmail(newEmail).addOnCompleteListener(onComplete);
    }

    @Override
    public void sendEmailVerification(OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        user.sendEmailVerification().addOnCompleteListener(onComplete);
    }

    @Override
    public void setUserPassword(String newPassword, OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        user.updatePassword(newPassword).addOnCompleteListener(onComplete);
    }

    @Override
    public void sendPasswordResetEmail(OnCompleteListener<Void> onComplete)
            throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        String email = user.getEmail();
        if (email != null) {
            auth.sendPasswordResetEmail(user.getEmail()).addOnCompleteListener(onComplete);
        }
    }

    @Override
    public void deleteUser(OnCompleteListener<Void> onComplete) throws UserLoginException {
        com.google.firebase.auth.FirebaseUser user = auth.getCurrentUser();
        checkUserIsLoggedIn(user);
        user.delete().addOnCompleteListener(onComplete);
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

    private void checkUserIsLoggedIn(com.google.firebase.auth.FirebaseUser user) throws UserLoginException {
        if (user == null) throw new UserLoginException("User is not logged in");
    }

    private void notifiyListeners() {
        for (UserStateListener listener : stateListeners) {
            listener.onStateChanged(this);
        }
    }
}
