package com.github.steroidteam.todolist.user;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.MockitoRule;
import org.mockito.quality.Strictness;

@RunWith(MockitoJUnitRunner.class)
public class FirebaseUserTest {

    @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock FirebaseAuth authMock;

    FirebaseUser firebaseUser;

    @Before
    public void before() {
        firebaseUser = new FirebaseUser((authMock));
    }

    @Test
    public void isLoggedOutFirst() {
        when(authMock.getCurrentUser()).thenReturn(null);
        assertFalse(firebaseUser.isLoggedIn());
    }

    @Test
    public void throwExceptionWhenNotLoggedIn() {
        when(authMock.getCurrentUser()).thenReturn(null);

        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.getUserId();
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.getDisplayName();
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.getEmail();
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.isEmailVerified();
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.updateDisplayName("", null);
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.updateEmail("", null);
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.sendEmailVerification(null);
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.sendPasswordResetEmail(null);
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    firebaseUser.deleteUser(null);
                });
    }

    @Test
    public void noExceptionWhenLoggIn() {
        com.google.firebase.auth.FirebaseUser firebaseUserMock = Mockito.mock(com.google.firebase.auth.FirebaseUser.class);
        when(authMock.getCurrentUser()).thenReturn(firebaseUserMock);

        Task<Void> taskMock = Mockito.mock(Task.class);
        when(firebaseUserMock.updateEmail(anyString())).thenReturn(taskMock);
        when(firebaseUserMock.sendEmailVerification()).thenReturn(taskMock);
        when(firebaseUserMock.updatePassword(anyString())).thenReturn(taskMock);
        when(firebaseUserMock.getEmail()).thenReturn("");
        when(authMock.sendPasswordResetEmail(anyString())).thenReturn(taskMock);
        when(firebaseUserMock.delete()).thenReturn(taskMock);

        OnCompleteListener<Void> onComplete =
                task -> {
                    // Do nothing
                };

        try {
            firebaseUser.getUserId();
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            firebaseUser.getDisplayName();
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            firebaseUser.getEmail();
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            firebaseUser.isEmailVerified();
        } catch (User.UserLoginException ule) {
            fail();
        }
        // FIXME : test fails because some android.text.TextUtils is not mocked
        // fBaseUser.updateDisplayName("", null);
        try {
            firebaseUser.updateEmail("", onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            firebaseUser.sendEmailVerification(onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            firebaseUser.setUserPassword("", onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            firebaseUser.sendPasswordResetEmail(onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            firebaseUser.deleteUser(onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
    }
}
