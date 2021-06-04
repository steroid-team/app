package com.github.steroidteam.todolist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> userLiveData;
    private MutableLiveData<Boolean> errorOccurred;

    public LiveData<Boolean> getErrorOccurred() {
        if (errorOccurred == null) {
            errorOccurred = new MutableLiveData<>(false);
        }
        return errorOccurred;
    }

    public void errorOccurredDone() {
        errorOccurred.postValue(false);
    }

    public LiveData<FirebaseUser> getUser() {
        if (userLiveData == null) {
            userLiveData = new MutableLiveData<>();
            loadUser();
        }
        return userLiveData;
    }

    private void loadUser() {
        System.err.println("ON LOAD !!!!!!!!!!!!!!!!! <<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        userLiveData.postValue(UserFactory.get());
    }

    public void updateUser(UserProfileChangeRequest request) {
        if (userLiveData.getValue() != null) {
            userLiveData
                    .getValue()
                    .updateProfile(request)
                    .addOnSuccessListener(
                            v -> {
                                System.out.println("=================================>");
                                loadUser();
                            })
                    .addOnFailureListener(
                            v -> {
                                errorOccurred.postValue(true);
                            });
        }
    }

    public void updateUserMail(String newMail) {
        if (userLiveData.getValue() != null) {
            userLiveData
                    .getValue()
                    .updateEmail(newMail)
                    .addOnSuccessListener(v -> loadUser())
                    .addOnFailureListener(v -> errorOccurred.postValue(true));
        }
    }

    public void updatePassword(String newPassword) {
        if (userLiveData.getValue() != null) {
            userLiveData
                    .getValue()
                    .updatePassword(newPassword)
                    .addOnSuccessListener(v -> loadUser())
                    .addOnFailureListener(v -> errorOccurred.postValue(true));
        }
    }
}
