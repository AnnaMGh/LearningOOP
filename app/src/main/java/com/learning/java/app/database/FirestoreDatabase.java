package com.learning.java.app.database;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import androidx.annotation.NonNull;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.model.IBitmapListener;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.ITestListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.Question;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;
import com.learning.java.app.utilities.GlobalSingleton;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class FirestoreDatabase {

    public static Activity context;
    private static FirebaseFirestore database = FirebaseFirestore.getInstance();
    private static CollectionReference usersReferences = database.collection("Users");
    private static CollectionReference testsReferences = database.collection("Tests");
    public static StorageReference mStorageRef;

    private static final String TAG = "Firestore";

    //users
    public static void addUser(final User user) {
        usersReferences.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //update local
                        user.setToken(documentReference.getId());
                        //update to firestore
                        updateUser(user);
                        //add custom photo for the new user
                        if (context != null) {
                            addPhoto(user, BitmapFactory.decodeResource(context.getResources(), R.drawable.user));
                        }
                        //save token
                        GlobalSingleton.getInstance().setString(Constants.TOKEN_KEY, user.getToken());
                    }
                });
    }

    //users
    public static void addUserWithCallback(final User user, final boolean addPhoto, final IRefreshListener listenerRefresh) {
        usersReferences.add(user)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        //update local
                        user.setToken(documentReference.getId());
                        //update to firestore
                        updateUserWithCallback(user, listenerRefresh);
                        //add custom photo for the new user
                        if (context != null && addPhoto) {
                            addPhoto(user, BitmapFactory.decodeResource(context.getResources(), R.drawable.user));
                        }
                        //save token
                        GlobalSingleton.getInstance().setString(Constants.TOKEN_KEY, user.getToken());

                    }
                });
    }

    public static void updateUserWithCallback(User user, final IRefreshListener listenerRefresh) {

        if (user.getToken() == null || user.getToken().isEmpty()) {
            return;
        }

        WriteBatch batch = database.batch();
        DocumentReference update = usersReferences.document(user.getToken());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("id", user.getId());
        updateMap.put("token", user.getToken());
        updateMap.put("daysInARaw", user.getDaysInARaw());
        updateMap.put("registeredTimestamp", user.getRegisteredTimestamp());
        updateMap.put("learnProgress", user.getLearnProgress());
        updateMap.put("testProgress", user.getTestProgress());
        updateMap.put("testsFinished", user.getTestsFinished());
        updateMap.put("notifications", user.getNotifications());
        updateMap.put("totalPoints", user.getTotalPoints());
        updateMap.put("photo", user.getPhoto());
        batch.update(update, updateMap);

        batch.commit()
                .addOnSuccessListener(aVoid -> {
                    Log.i(TAG, "updateUserWithCallback | addOnSuccessListener ");
                    if (listenerRefresh != null) {
                        listenerRefresh.doRefresh(true);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "updateUserWithCallback | addOnFailureListener | " + e);
                });
    }


    public static void updateUser(User user) {

        if (user.getToken() == null || user.getToken().isEmpty()) {
            return;
        }

        WriteBatch batch = database.batch();
        DocumentReference update = usersReferences.document(user.getToken());
        Map<String, Object> updateMap = new HashMap<>();
        updateMap.put("id", user.getId());
        updateMap.put("token", user.getToken());
        updateMap.put("daysInARaw", user.getDaysInARaw());
        updateMap.put("registeredTimestamp", user.getRegisteredTimestamp());
        updateMap.put("learnProgress", user.getLearnProgress());
        updateMap.put("testProgress", user.getTestProgress());
        updateMap.put("testsFinished", user.getTestsFinished());
        updateMap.put("notifications", user.getNotifications());
        updateMap.put("totalPoints", user.getTotalPoints());
        updateMap.put("photo", user.getPhoto());
        batch.update(update, updateMap);

        batch.commit()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FIRESTORE RESPONSE: ", "ERROR | updateUser | " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    public static void deleteUser(User user) {
        WriteBatch batch = database.batch();

        DocumentReference delete = usersReferences.document(user.getToken());
        batch.delete(delete);

        batch.commit().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.e("FIRESTORE RESPONSE: ", "ERROR | updateUser | " + e.getMessage());
                e.printStackTrace();
            }
        });
    }

    public static void getAllUsers(final IUserListener listener) {
        usersReferences.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (listener != null) {
                            ArrayList<User> userList = new ArrayList<>();
                            for (QueryDocumentSnapshot documentSnapshot : queryDocumentSnapshots) {
                                userList.add(documentSnapshot.toObject(User.class));
                            }

                            listener.getAllUsers(userList);
                        }

                        Log.i(TAG, "getAllUsers - SUCCESS : " + queryDocumentSnapshots.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "getAllUsers - ERROR");
                    }
                });
    }

    public static void isUserExisting(final User user, final IUserListener listener) {
        usersReferences.whereEqualTo("email", user.getEmail())
//                .whereEqualTo("password", user.getPassword())
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        if (listener != null) {
                            if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                listener.getUser(queryDocumentSnapshots.getDocuments().get(0).toObject(User.class));
                                listener.getUserToken(queryDocumentSnapshots.getDocuments().get(0).getId());
                            } else {
                                listener.getUser(null);
                            }
                        }
                        Log.i(TAG, "userExist - SUCCESS : " + queryDocumentSnapshots.toString());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e(TAG, "userExist - ERROR: " + e.getMessage());
                    }
                });
    }

    public static void getUserBy(String key, String value, final IUserListener listener) {
        usersReferences.whereEqualTo(key, value)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        User foundUser = null;

                        if (queryDocumentSnapshots != null) {
                            if (queryDocumentSnapshots.getDocuments().size() > 0) {
                                foundUser = queryDocumentSnapshots.getDocuments().get(0).toObject(User.class);
                                if (foundUser != null) {
                                    foundUser.setToken(queryDocumentSnapshots.getDocuments().get(0).getId());
                                }
                            }
                        }
                        listener.getUser(foundUser);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "Error at getUserBy: " + e.getMessage());
                    }
                });
    }

    //tests
    public static void addTest(final Test test, final ITestListener listenerTest) {
        Test newTest = new Test(test.getId(), "", test.getTitle(), test.getChecked(), test.isLive(), new ArrayList<>());
        testsReferences.add(newTest)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        test.setToken(documentReference.getId());
                        updateTest(test);

                        if (listenerTest != null) {
                            listenerTest.getTest(test);
                        }
                    }
                });
    }

    public static void updateTest(Test test) {
        WriteBatch batch = database.batch();

        DocumentReference update = testsReferences.document(test.getToken());
        Map<String, Object> updateMap = new HashMap<>();
        if (test.getToken() != null) {
            if (!test.getToken().isEmpty()) {
                updateMap.put("token", test.getToken());
            }
        }

        HashMap<String, Object> questionListMap = new HashMap<>();

        for (Question question : test.getQuestionsList()) {
            HashMap<String, Object> questionMap = new HashMap<>();
            HashMap<String, Object> answersMap = new HashMap<>();
            HashMap<String, Object> questionsMap = new HashMap<>();

            //add questions in question
            for (int i = 0; i < question.questions.size(); i++) {
                questionsMap.put(String.valueOf(i), question.questions.get(i));
            }

            //add answers in question
            for (int i = 0; i < question.answers.size(); i++) {
                answersMap.put(String.valueOf(i), question.answers.get(i));
            }

            //add info in question
            questionMap.put("type", question.type);
            questionMap.put("questions", questionsMap);
            questionMap.put("answers", answersMap);
            questionMap.put("multipleCorrectAnswerId", question.multipleCorrectAnswerId);

            //add question in questionListMap
            questionListMap.put(String.valueOf(test.getQuestionsList().indexOf(question)), questionMap);
        }

        updateMap.put("id", test.getId());
        updateMap.put("questionsList", questionListMap);

        batch.update(update, updateMap);
        batch.commit();
    }

    public static void getAllTests(final ITestListener listener) {
        testsReferences.get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        ArrayList<Test> testList = new ArrayList<>();

                        for (QueryDocumentSnapshot queryDocumentSnapshot : queryDocumentSnapshots) {
                            //get the
//                            testList.add(queryDocumentSnapshot.toObject(Test.class));
                            try {
                                ObjectMapper oMapper = new ObjectMapper();
                                Map<String, Object> testMap = queryDocumentSnapshot.getData();
                                Map<String, Object> questionListMap = oMapper.convertValue(testMap.get("questionsList"), Map.class);

                                //handle question list
                                ArrayList<Question> questionList = new ArrayList<>();
                                for (int i = 0; i < questionListMap.size(); i++) {

                                    Map<String, Object> questionMap = oMapper.convertValue(questionListMap.get(String.valueOf(i)), Map.class);
                                    Map<String, Object> questionsMap = oMapper.convertValue(questionMap.get("questions"), Map.class);
                                    Map<String, Object> answersMap = oMapper.convertValue(questionMap.get("answers"), Map.class);

                                    //get questions
                                    ArrayList<String> questions = new ArrayList<>();
                                    for (int j = 0; j < questionsMap.size(); j++) {
                                        questions.add(questionsMap.get(String.valueOf(j)).toString());
                                    }

                                    //get answers
                                    ArrayList<String> answers = new ArrayList<>();
                                    for (int j = 0; j < answersMap.size(); j++) {
                                        answers.add(answersMap.get(String.valueOf(j)).toString());
                                    }

                                    //create question
                                    Question question = new Question();
                                    question.type = questionMap.get("type").toString();
                                    question.questions = questions;
                                    question.answers = answers;
                                    question.multipleCorrectAnswerId = questionMap.get("multipleCorrectAnswerId").toString();

                                    //add question to question list
                                    questionList.add(question);
                                }


                                //checked
                                boolean checked = false;
                                if (testMap.get("checked").toString().equals("true")) {
                                    checked = true;
                                }

                                boolean live = false;
                                if (testMap.get("live").toString().equals("true")) {
                                    live = true;
                                }

                                //add data to test
                                Test test = new Test(Integer.parseInt(testMap.get("id").toString()), testMap.get("token").toString(),
                                        testMap.get("title").toString(),
                                        checked,
                                        live,
                                        questionList);

                                testList.add(test);

                            } catch (Exception e) {
                                Log.e("Exceptions Firestore", e.getMessage());
                            }
                        }


                        //sort test in the order of id's so the standard ones will ALWAYS be the first ones
                        ArrayList<Test> sortedTest = new ArrayList<>(testList);
                        for (Test test : testList) {
                            sortedTest.set((test.getId() - 1), test);
                        }

                        //send list
                        listener.getAllTests(sortedTest);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("Firestore", "FirestoreDatabase | getAllTests | " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    public static void deleteTest(Test test) {
        WriteBatch batch = database.batch();

        DocumentReference delete = testsReferences.document(test.getToken());
        batch.delete(delete);

        batch.commit();
    }

    public static void addPhoto(final User user, Bitmap bitmap) {
        if (mStorageRef == null) {
            return;
        }

        if (bitmap == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference photoReference = mStorageRef.child("Images/" + user.getToken() + ".png");
        photoReference.putBytes(data)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        user.setPhoto(String.valueOf(taskSnapshot.getUploadSessionUri()));
                        updateUser(user);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FIRESTORE RESPONSE: ", "FirestoreDatabase | addPhoto | " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    public static void addPhotoWithCallback(final User user, Bitmap bitmap, final IRefreshListener listenerRefresh) {
        if (mStorageRef == null) {
            return;
        }

        if (bitmap == null) {
            return;
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] data = baos.toByteArray();

        StorageReference photoReference = mStorageRef.child("Images/" + user.getToken() + ".png");
        photoReference.putBytes(data)
                .addOnSuccessListener(taskSnapshot -> {
                    user.setPhoto(String.valueOf(taskSnapshot.getUploadSessionUri()));
                    updateUserWithCallback(user, listenerRefresh);
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "addPhotoWithCallback | addOnFailureListener | " + e);
                });
    }

    public static void deletePhoto(final User user) {
        if (mStorageRef == null) {
            return;
        }

        StorageReference photoReference = mStorageRef.child("Images/" + user.getToken() + ".png");
        photoReference.delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.i("FIRESTORE RESPONSE: ", "FirestoreDatabase | deletePhoto | " + user.getToken());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("FIRESTORE RESPONSE: ", "FirestoreDatabase | deletePhoto | " + e.getMessage());
                        e.printStackTrace();
                    }
                });
    }

    public static void getPhoto(User user, final IBitmapListener listener) {
        if (user == null) {
            return;
        }
        StorageReference photoReference = mStorageRef.child("Images/" + user.getToken() + ".png");

        final long ONE_MEGABYTE = 1024 * 1024;
        photoReference.getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        if (listener != null) {
                            if (bytes.length > 0) {
                                listener.getBitmap(BitmapFactory.decodeByteArray(bytes, 0, bytes.length));
                            } else {
                                listener.getBitmap(null);
                            }
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        if (listener != null) {
                            listener.getBitmap(null);
                        }
                        Log.e("Firestore", "FirestoreDatabase | getPhoto | " + exception.getMessage());
                        exception.printStackTrace();
                    }
                });
    }
}
