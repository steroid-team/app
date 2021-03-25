package com.github.steroidteam.todolist.user;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class FBaseUserTest {
    @Mock
    FirebaseAuth authMock;
    @Mock
    FirebaseUser firebaseUserMock;
    @InjectMocks
    FBaseUser fBaseUser;

    @Test
    public void isLoggedOutFirst() {
        when(authMock.getCurrentUser()).thenReturn(null);
        assertFalse(fBaseUser.isLoggedIn());
    }

    @Test
    public void throwExceptionWhenNotLoggedIn() {
        when(authMock.getCurrentUser()).thenReturn(null);

        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.getUserId();
        });
        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.getDisplayName();
        });
        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.getEmail();
        });
        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.isEmailVerified();
        });
        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.updateDisplayName("", null);
        });
        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.updateEmail("", null);
        });
        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.sendEmailVerification(null);
        });
        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.sendPasswordResetEmail(null);
        });
        assertThrows(User.UserLoginException.class, () -> {
            fBaseUser.deleteUser(null);
        });
    }

    @Test
    public void noExceptionWhenLoggIn() {
        when(authMock.getCurrentUser()).thenReturn(firebaseUserMock);

        Task<Void> taskMock = Mockito.mock(Task.class);
        //when(firebaseUserMock.updateProfile(any())).thenReturn(taskMock);
        when(firebaseUserMock.updateEmail(anyString())).thenReturn(taskMock);
        when(firebaseUserMock.sendEmailVerification()).thenReturn(taskMock);
        when(firebaseUserMock.updatePassword(anyString())).thenReturn(taskMock);
        when(firebaseUserMock.getEmail()).thenReturn("");
        when(authMock.sendPasswordResetEmail(anyString())).thenReturn(taskMock);
        when(firebaseUserMock.delete()).thenReturn(taskMock);

        OnCompleteListener<Void> onComplete = new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                // Do nothing
            }
        };

        try {
            fBaseUser.getUserId();
            fBaseUser.getDisplayName();
            fBaseUser.getEmail();
            fBaseUser.isEmailVerified();
            // FIXME : test fails because some android.text.TextUtils is not mocked
            //fBaseUser.updateDisplayName("", null);
            fBaseUser.updateEmail("", onComplete);
            fBaseUser.sendEmailVerification(onComplete);
            fBaseUser.sendPasswordResetEmail(onComplete);
            fBaseUser.deleteUser(onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
    }
}
