package com.learning.java.app.services;

import android.app.Activity;
import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.learning.java.app.model.ObjectListener;

public class AuthHandler {
    private static FirebaseAuth mAuth;
    private static FirebaseUser currentUser;
    private static Activity mContext;

    public final static String ERROR_EMAIL_ALREADY_IN_USE = "ERROR_EMAIL_ALREADY_IN_USE";


    private static String TAG = "AuthHandler";

    public static void createAuth(Activity context) {
        mContext = context;
        mAuth = FirebaseAuth.getInstance();
    }

    public static void startAuth(Activity context) {
        mContext = context;
        currentUser = mAuth.getCurrentUser();
    }

    public static FirebaseUser getUserDetails() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();

            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();

            // The user's ID, unique to the Firebase project.
            String uid = user.getUid();
        }
        return user;
    }


    public static void signUpUser(Activity context, String email, String password, ObjectListener listener) {
        mContext = context;
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign up: success
                            // update UI for current User
                            currentUser = mAuth.getCurrentUser();
                            listener.getObject(currentUser);
                        } else {
                            // Sign up: fail
                            Log.e(TAG, "create Account: Fail!", task.getException());
                            String exception = "";
                            if (task.getException() != null) {
                                exception = task.getException().toString();
                            }
                            listener.getObject(exception);
                        }

                    }
                });
    }

    public static void signInUser(Activity context, String email, String password, ObjectListener listener) {
        mContext = context;
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(mContext, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in: success
                            // update UI for current User
                            currentUser = mAuth.getCurrentUser();
                            listener.getObject(currentUser);
                        } else {
                            // Sign in: fail
                            Log.e(TAG, "create Account: Fail!", task.getException());
                            String exception = "";
                            if (task.getException() != null) {
                                exception = task.getException().toString();
                            }
                            listener.getObject(exception);
                        }

                    }
                });
    }

    public static void singOut() {
        mAuth.signOut();
    }

    public static void deleteAccount(ObjectListener listener) {
        Log.d(TAG, "deleteAccount: currentUser " + currentUser);
        if (currentUser == null) {
            currentUser = mAuth.getCurrentUser();
            Log.d(TAG, "deleteAccount: currentUser " + currentUser);
            if (currentUser == null) {
                Log.d(TAG, "deleteAccount: currentUser couldn't be found");
                if (listener != null) {
                    listener.getObject(false);
                }
                return;
            }
        }

        currentUser.delete()
                .addOnFailureListener(e -> {
                    Log.d(TAG, "deleteAccount: User account cound not be deleted." + e);
                    if (listener != null) {
                        listener.getObject(e.getMessage());
                    }
                })
                .addOnSuccessListener(task -> {
                    Log.d(TAG, "deleteAccount: User account deleted successfully.");
                    if (listener != null) {
                        listener.getObject(true);
                    }
                });
    }


    public static void sendVerificationEmail(Activity context, ObjectListener listener) {
        mContext = context;
        currentUser.sendEmailVerification()
                .addOnCompleteListener(mContext, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.e(TAG, "Verification email sent to " + currentUser.getEmail());
                        } else {
                            Log.e(TAG, "sendEmailVerification failed!", task.getException());
                        }
                    }
                });
    }

    public static void sendPasswordResetEmail(Activity context, String email, ObjectListener listener) {
        mContext = context;
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // do something when mail was sent successfully.
                        } else {
                            // ...
                        }

                        if (listener != null) {
                            listener.getObject(task.isSuccessful());
                        }
                    }
                });
    }
}
