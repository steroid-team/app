package com.github.steroidteam.todolist.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.github.steroidteam.todolist.model.user.UserFactory;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

public class UserViewModel extends ViewModel {

    private MutableLiveData<FirebaseUser> userLiveData;

    public LiveData<FirebaseUser> getUser() {
        if(userLiveData==null) {
            userLiveData = new MutableLiveData<>();
            loadUser();
        }
        return userLiveData;
    }

    private void loadUser() {
        userLiveData.postValue(UserFactory.get());
    }

    public void updateUser(UserProfileChangeRequest request) {
        if(userLiveData.getValue()!=null) {
            userLiveData.getValue().updateProfile(request)
                .addOnSuccessListener(v -> loadUser())
                .addOnFailureListener(v -> loadUser());
        }
    }

    public void updateUserMail(String newMail) {
        if(userLiveData.getValue()!=null) {
            userLiveData.getValue().updateEmail(newMail)
                    .addOnSuccessListener(v -> loadUser())
                    .addOnFailureListener(v -> loadUser());
        }
    }
}
