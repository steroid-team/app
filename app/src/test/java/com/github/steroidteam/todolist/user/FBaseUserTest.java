package com.github.steroidteam.todolist.user;

import static org.junit.Assert.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
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
public class FBaseUserTest {

    @Rule public MockitoRule rule = MockitoJUnit.rule().strictness(Strictness.STRICT_STUBS);

    @Mock FirebaseAuth authMock;

    FBaseUser fBaseUser;

    @Before
    public void before() {
        fBaseUser = new FBaseUser((authMock));
    }

    @Test
    public void isLoggedOutFirst() {
        when(authMock.getCurrentUser()).thenReturn(null);
        assertFalse(fBaseUser.isLoggedIn());
    }

    @Test
    public void throwExceptionWhenNotLoggedIn() {
        when(authMock.getCurrentUser()).thenReturn(null);

        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.getUserId();
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.getDisplayName();
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.getEmail();
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.isEmailVerified();
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.updateDisplayName("", null);
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.updateEmail("", null);
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.sendEmailVerification(null);
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.sendPasswordResetEmail(null);
                });
        assertThrows(
                User.UserLoginException.class,
                () -> {
                    fBaseUser.deleteUser(null);
                });
    }

    @Test
    public void noExceptionWhenLoggIn() {
        FirebaseUser firebaseUserMock = Mockito.mock(FirebaseUser.class);
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
            fBaseUser.getUserId();
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            fBaseUser.getDisplayName();
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            fBaseUser.getEmail();
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            fBaseUser.isEmailVerified();
        } catch (User.UserLoginException ule) {
            fail();
        }
        // FIXME : test fails because some android.text.TextUtils is not mocked
        // fBaseUser.updateDisplayName("", null);
        try {
            fBaseUser.updateEmail("", onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            fBaseUser.sendEmailVerification(onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            fBaseUser.setUserPassword("", onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            fBaseUser.sendPasswordResetEmail(onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
        try {
            fBaseUser.deleteUser(onComplete);
        } catch (User.UserLoginException ule) {
            fail();
        }
    }
}
