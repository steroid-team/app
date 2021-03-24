package com.github.steroidteam.todolist.user;

import com.google.android.gms.tasks.OnCompleteListener;

public interface User {
    /* Trivial method to have here */
    boolean isLoggedIn();

    /* Unique user identifier */
    String getUserId() throws UserLoginException;

    /* User profile */
    String getDisplayName() throws UserLoginException;
    String getEmail() throws UserLoginException;
    boolean isEmailVerified() throws UserLoginException;

    /* Actions on current user */
    void updateDisplayName(String newDisplayName, OnCompleteListener<Void> onComplete)
            throws UserLoginException;
    void updateEmail(String newEmail, OnCompleteListener<Void> onComplete)
            throws UserLoginException;
    void sendEmailVerification(OnCompleteListener<Void> onComplete)
            throws UserLoginException;
    void setUserPassword(String newPassword, OnCompleteListener<Void> onComplete)
            throws UserLoginException;
    void sendPasswordResetEmail(OnCompleteListener<Void> onComplete)
            throws UserLoginException;
    void deleteUser(OnCompleteListener<Void> onComplete)
            throws UserLoginException;
    void reauthenticate(OnCompleteListener<Void> onComplete);
    void logOut();

    /* Permits other entities to listen to any change in the current user state */
    void addUserStateListener(UserStateListener listener);
    void removeUserStateListener(UserStateListener listener);

    /* Thrown whenever the user is not logged in */
    class UserLoginException extends Exception {
        public UserLoginException(String description) {
            super(description);
        }
    }

    interface UserStateListener {
        void onStateChanged(User user);
    }
}
