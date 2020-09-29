
package com.learning.java.app.helper;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SqliteHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "LearningJava.db";

    private static final String TABLE_USER = "User";
    private static final String TABLE_TEST = "Test";
    private static final String TABLE_LEARN = "Learn";

    //user
    private static final String KEY_ID = "id";
    private static final String KEY_NAME = "name";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PASSWORD = "password";
    private static final String KEY_FUNCTION = "function";
    private static final String KEY_NOTIFICATION = "notification";
    private static final String KEY_LEARN_PROGRESS = "learnProgress";
    private static final String KEY_TEST_PROGRESS = "testProgress";
    private static final String KEY_DAYS_RAW = "days";
    private static final String KEY_POINTS = "points";
    private static final String KEY_PHOTO = "photo";

    //test
    private static final String KEY_TEST_ID = "idTest";
    private static final String KEY_TEST_TITLE = "title";
    private static final String KEY_TEST_CHECK = "checkIfDone";
    private static final String KEY_TEST_TYPE1 = "type1";
    private static final String KEY_TEST_TYPE2 = "type2";
    private static final String KEY_TEST_TYPE3 = "type3";
    private static final String KEY_TEST_TYPE4 = "type4";
    private static final String KEY_TEST_TYPE5 = "type5";
    private static final String KEY_TEST_QUESTION1 = "question1";
    private static final String KEY_TEST_QUESTION2 = "question2";
    private static final String KEY_TEST_QUESTION3 = "question3";
    private static final String KEY_TEST_QUESTION4 = "question4";
    private static final String KEY_TEST_QUESTION5 = "question5";
    private static final String KEY_TEST_ANSWER1 = "answer1";
    private static final String KEY_TEST_ANSWER2 = "answer2";
    private static final String KEY_TEST_ANSWER3 = "answer3";
    private static final String KEY_TEST_ANSWER4 = "answer4";
    private static final String KEY_TEST_ANSWER5 = "answer5";
    private static final String KEY_TEST_ANSWER11 = "answer11";
    private static final String KEY_TEST_ANSWER12 = "answer12";
    private static final String KEY_TEST_ANSWER13 = "answer13";
    private static final String KEY_TEST_ANSWER21 = "answer21";
    private static final String KEY_TEST_ANSWER22 = "answer22";
    private static final String KEY_TEST_ANSWER23 = "answer23";
    private static final String KEY_TEST_ANSWER31 = "answer31";
    private static final String KEY_TEST_ANSWER32 = "answer32";
    private static final String KEY_TEST_ANSWER33 = "answer33";
    private static final String KEY_TEST_ANSWER41 = "answer41";
    private static final String KEY_TEST_ANSWER42 = "answer42";
    private static final String KEY_TEST_ANSWER43 = "answer43";
    private static final String KEY_TEST_ANSWER51 = "answer51";
    private static final String KEY_TEST_ANSWER52 = "answer52";
    private static final String KEY_TEST_ANSWER53 = "answer53";


    private static final String TAG = "DBHelper";
    private static SqliteHelper sInstance;

    public static synchronized SqliteHelper getInstance(Context context) {
        if (sInstance == null) {
            sInstance = new SqliteHelper(context.getApplicationContext());
        }
        return sInstance;
    }

    public SqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_USER_TABLE = "CREATE TABLE " + TABLE_USER + "( "
                + KEY_ID + " INTEGER PRIMARY KEY, " + KEY_NAME + " TEXT, "
                + KEY_EMAIL + " TEXT, " + KEY_PASSWORD + " TEXT, "
                + KEY_FUNCTION + " TEXT, "
                + KEY_NOTIFICATION + " TEXT, " + KEY_LEARN_PROGRESS + " TEXT, "
                + KEY_TEST_PROGRESS + " TEXT, " + KEY_DAYS_RAW + " TEXT, "
                + KEY_POINTS + " TEXT, " + KEY_PHOTO + " TEXT)";

        db.execSQL(CREATE_USER_TABLE);

        String CREATE_TEST_TABLE = "CREATE TABLE " + TABLE_TEST + "( "
                + KEY_TEST_ID + " INTEGER PRIMARY KEY, " + KEY_TEST_TITLE + " TEXT, "
                + KEY_TEST_CHECK + " TEXT, " + KEY_TEST_TYPE1 + " TEXT, "
                + KEY_TEST_TYPE2 + " TEXT, " + KEY_TEST_TYPE3 + " TEXT, "
                + KEY_TEST_TYPE4 + " TEXT, " + KEY_TEST_TYPE5 + " TEXT, "
                + KEY_TEST_QUESTION1 + " TEXT, " + KEY_TEST_QUESTION2 + " TEXT, "
                + KEY_TEST_QUESTION3 + " TEXT, " + KEY_TEST_QUESTION4 + " TEXT, "
                + KEY_TEST_QUESTION5 + " TEXT, " + KEY_TEST_ANSWER1 + " TEXT, "
                + KEY_TEST_ANSWER2 + " TEXT, " + KEY_TEST_ANSWER3 + " TEXT, "
                + KEY_TEST_ANSWER4 + " TEXT, " + KEY_TEST_ANSWER5 + " TEXT, "
                + KEY_TEST_ANSWER11 + " TEXT, "
                + KEY_TEST_ANSWER12 + " TEXT, " + KEY_TEST_ANSWER13 + " TEXT, "
                + KEY_TEST_ANSWER21 + " TEXT, " + KEY_TEST_ANSWER22 + " TEXT, "
                + KEY_TEST_ANSWER23 + " TEXT, " + KEY_TEST_ANSWER31 + " TEXT, "
                + KEY_TEST_ANSWER32 + " TEXT, " + KEY_TEST_ANSWER33 + " TEXT, "
                + KEY_TEST_ANSWER41 + " TEXT, " + KEY_TEST_ANSWER42 + " TEXT, "
                + KEY_TEST_ANSWER43 + " TEXT, " + KEY_TEST_ANSWER51 + " TEXT, "
                + KEY_TEST_ANSWER52 + " TEXT, " + KEY_TEST_ANSWER53 + " TEXT)";

        db.execSQL(CREATE_TEST_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(String.valueOf("DROP TABLE IF EXISTS" + TABLE_USER));
        onCreate(db);
    }

    ////////////////////
    //      User      //
    ////////////////////

    public void addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, user.getName());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_PASSWORD, user.getPassword());
            values.put(KEY_FUNCTION, user.getFunction());
            values.put(KEY_NOTIFICATION, String.valueOf(user.getNotifications()));
            values.put(KEY_LEARN_PROGRESS, String.valueOf(user.getLearnProgress()));
            values.put(KEY_TEST_PROGRESS, String.valueOf(user.getTestProgress()));
            values.put(KEY_DAYS_RAW, String.valueOf(user.getDaysInARaw()));
            values.put(KEY_POINTS, String.valueOf(user.getTotalPoints()));
            values.put(KEY_PHOTO, user.getPhoto());

            db.insert(TABLE_USER, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add users to database!");
        } finally {
            db.endTransaction();
        }
    }

    public User getUser(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID, KEY_NAME, KEY_EMAIL,
                        KEY_PASSWORD, KEY_FUNCTION, KEY_NOTIFICATION, KEY_LEARN_PROGRESS,
                        KEY_TEST_PROGRESS, KEY_DAYS_RAW,
                        KEY_POINTS, KEY_PHOTO}, KEY_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();

                user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)),
                        Integer.parseInt(cursor.getString(7)), Integer.parseInt(cursor.getString(8)),
                        Integer.parseInt(cursor.getString(9)), cursor.getString(10));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get users from database!");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return user;
    }

    public User getUserByName(String name) {
        SQLiteDatabase db = this.getReadableDatabase();
        User user = null;

        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID, KEY_NAME, KEY_EMAIL,
                        KEY_PASSWORD, KEY_FUNCTION, KEY_NOTIFICATION, KEY_LEARN_PROGRESS,
                        KEY_TEST_PROGRESS, KEY_DAYS_RAW,
                        KEY_POINTS, KEY_PHOTO}, KEY_NAME + "=?",
                new String[]{name}, null, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                user = new User(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        cursor.getString(2), cursor.getString(3), cursor.getString(4),
                        Integer.parseInt(cursor.getString(5)), Integer.parseInt(cursor.getString(6)),
                        Integer.parseInt(cursor.getString(7)), Integer.parseInt(cursor.getString(8)),
                        Integer.parseInt(cursor.getString(9)), cursor.getString(10));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get users from database!");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return user;
    }

    public User getUserByDetails(User user) {
        List<User> users = getAllUsers();
        for (User u : users) {
            if (u.getName().equals(user.getName()) && u.getEmail().equals(user.getEmail()) && u.getPassword().equals(user.getPassword())
                    && u.getFunction().equals(user.getFunction()) && u.getNotifications() == user.getNotifications()
                    && u.getLearnProgress() == user.getLearnProgress() && u.getTestProgress() == user.getTestProgress()
                    && u.getDaysInARaw() == user.getDaysInARaw() && u.getTotalPoints() == user.getTotalPoints()) {
                return u;
            }
        }
        return null;
    }

    public int getUserIdByUser(User u) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID}, KEY_NAME + " = ?" + " AND " + KEY_PASSWORD + " = ?",
                new String[]{String.valueOf(u.getName()), u.getPassword()}, null, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                return Integer.parseInt(cursor.getString(0));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get users from database!");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return 0;
    }

    public int getUserIdByName(String name, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USER, new String[]{KEY_ID}, KEY_NAME + " = ?" + " AND " + KEY_PASSWORD + " = ?",
                new String[]{name, password}, null, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();
                return Integer.parseInt(cursor.getString(0));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get id from database!");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return 0;
    }

    public List<User> getAllUsers() {
        List<User> userList = new ArrayList<User>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        User user = null;
        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    user = new User();
                    user.setId(Integer.parseInt(cursor.getString(0)));
                    user.setName(cursor.getString(1));
                    user.setEmail(cursor.getString(2));
                    user.setPassword(cursor.getString(3));
                    user.setFunction(cursor.getString(4));
                    user.setNotifications(Integer.parseInt(cursor.getString(5)));
                    user.setLearnProgress(Integer.parseInt(cursor.getString(6)));
                    user.setTestProgress(Integer.parseInt(cursor.getString(7)));
                    user.setDaysInARaw(Integer.parseInt(cursor.getString(8)));
                    user.setTotalPoints(Integer.parseInt(cursor.getString(9)));
                    user.setPhoto(cursor.getString(10));

                    userList.add(user);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get users from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return userList;
    }

    public void updateUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_NAME, user.getName());
            values.put(KEY_EMAIL, user.getEmail());
            values.put(KEY_PASSWORD, user.getPassword());
            values.put(KEY_FUNCTION, user.getFunction());
            values.put(KEY_NOTIFICATION, String.valueOf(user.getNotifications()));
            values.put(KEY_LEARN_PROGRESS, String.valueOf(user.getLearnProgress()));
            values.put(KEY_TEST_PROGRESS, String.valueOf(user.getTestProgress()));
            values.put(KEY_DAYS_RAW, String.valueOf(user.getDaysInARaw()));
            values.put(KEY_POINTS, String.valueOf(user.getTotalPoints()));
            values.put(KEY_PHOTO, user.getPhoto());

            // updating row
            db.update(TABLE_USER, values, KEY_ID + " = ?",
                    new String[]{String.valueOf(user.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update user!");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_USER, KEY_ID + " = ?",
                    new String[]{String.valueOf(user.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete user!");
        } finally {
            db.endTransaction();
        }
    }

    public int getUserCount() {
        int count = 0;
        String countQuery = "SELECT * FROM " + TABLE_USER;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        try {
            count = cursor.getCount();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get user from database!");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return count;
    }

    public boolean checkUserById(User user) {
        List<User> userList = getAllUsers();

        for (User u : userList) {
            if (u.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkUserByName(User user) {
        List<User> userList = getAllUsers();

        for (User u : userList) {
            if (u.getName() == user.getName()) {
                return true;
            }
        }
        return false;
    }

    public boolean checkUserId(User user) {
        List<User> userList = getAllUsers();

        for (User u : userList) {
            if (u.getId() == user.getId()) {
                return true;
            }
        }
        return false;
    }

    public String checkUserFunction(String name, String password) {
        List<User> userList = getAllUsers();

        for (User u : userList) {
            if (u.getName().equals(name) && u.getPassword().equals(password)) {
                if (u.getFunction().equals("admin")) {
                    return "admin";
                }
                return "user";
            }
        }
        return "none";
    }


    ////////////////////
    //      Test      //
    ////////////////////

    public void addTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TEST_TITLE, test.getTitle());
            values.put(KEY_TEST_CHECK, String.valueOf(test.getChecked()));
            values.put(KEY_TEST_TYPE1, test.getType1());
            values.put(KEY_TEST_TYPE2, test.getType2());
            values.put(KEY_TEST_TYPE3, test.getType3());
            values.put(KEY_TEST_TYPE4, test.getType4());
            values.put(KEY_TEST_TYPE5, test.getType5());
            values.put(KEY_TEST_QUESTION1, test.getQuestion1());
            values.put(KEY_TEST_QUESTION2, test.getQuestion2());
            values.put(KEY_TEST_QUESTION3, test.getQuestion3());
            values.put(KEY_TEST_QUESTION4, test.getQuestion4());
            values.put(KEY_TEST_QUESTION5, test.getQuestion5());
            values.put(KEY_TEST_ANSWER1, test.getAnswer1());
            values.put(KEY_TEST_ANSWER2, test.getAnswer2());
            values.put(KEY_TEST_ANSWER3, test.getAnswer3());
            values.put(KEY_TEST_ANSWER4, test.getAnswer4());
            values.put(KEY_TEST_ANSWER5, test.getAnswer5());
            values.put(KEY_TEST_ANSWER11, test.getAnswer11());
            values.put(KEY_TEST_ANSWER12, test.getAnswer12());
            values.put(KEY_TEST_ANSWER13, test.getAnswer13());
            values.put(KEY_TEST_ANSWER21, test.getAnswer21());
            values.put(KEY_TEST_ANSWER22, test.getAnswer22());
            values.put(KEY_TEST_ANSWER23, test.getAnswer23());
            values.put(KEY_TEST_ANSWER31, test.getAnswer31());
            values.put(KEY_TEST_ANSWER32, test.getAnswer32());
            values.put(KEY_TEST_ANSWER33, test.getAnswer33());
            values.put(KEY_TEST_ANSWER41, test.getAnswer41());
            values.put(KEY_TEST_ANSWER42, test.getAnswer42());
            values.put(KEY_TEST_ANSWER43, test.getAnswer43());
            values.put(KEY_TEST_ANSWER51, test.getAnswer51());
            values.put(KEY_TEST_ANSWER52, test.getAnswer52());
            values.put(KEY_TEST_ANSWER53, test.getAnswer53());

            db.insert(TABLE_TEST, null, values);
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to add users to database!");
        } finally {
            db.endTransaction();
        }
    }

    public Test getTest(int id) {
        SQLiteDatabase db = this.getReadableDatabase();
        Test test = null;

        Cursor cursor = db.query(TABLE_TEST, new String[]{KEY_TEST_ID, KEY_TEST_TITLE, KEY_TEST_CHECK,
                        KEY_TEST_TYPE1, KEY_TEST_TYPE2, KEY_TEST_TYPE3, KEY_TEST_TYPE4, KEY_TEST_TYPE5,
                        KEY_TEST_QUESTION1, KEY_TEST_QUESTION2, KEY_TEST_QUESTION3,
                        KEY_TEST_QUESTION4, KEY_TEST_QUESTION5, KEY_TEST_ANSWER1, KEY_TEST_ANSWER2,
                        KEY_TEST_ANSWER3, KEY_TEST_ANSWER4, KEY_TEST_ANSWER5,
                        KEY_TEST_ANSWER11, KEY_TEST_ANSWER12, KEY_TEST_ANSWER13, KEY_TEST_ANSWER21,
                        KEY_TEST_ANSWER22, KEY_TEST_ANSWER23, KEY_TEST_ANSWER31, KEY_TEST_ANSWER32,
                        KEY_TEST_ANSWER33, KEY_TEST_ANSWER41, KEY_TEST_ANSWER42, KEY_TEST_ANSWER43,
                        KEY_TEST_ANSWER51, KEY_TEST_ANSWER52, KEY_TEST_ANSWER53}, KEY_TEST_ID + "=?",
                new String[]{String.valueOf(id)}, null, null, null, null);

        try {
            if (cursor != null) {
                cursor.moveToFirst();

                test = new Test(Integer.parseInt(cursor.getString(0)), cursor.getString(1),
                        Boolean.parseBoolean(cursor.getString(2)), cursor.getString(3), cursor.getString(4),
                        cursor.getString(5), cursor.getString(6),
                        cursor.getString(7), cursor.getString(8),
                        cursor.getString(9), cursor.getString(10),
                        cursor.getString(11), cursor.getString(12),
                        cursor.getString(13), cursor.getString(14),
                        cursor.getString(15), cursor.getString(16),
                        cursor.getString(17), cursor.getString(18),
                        cursor.getString(19), cursor.getString(20),
                        cursor.getString(21), cursor.getString(22),
                        cursor.getString(23), cursor.getString(24),
                        cursor.getString(25), cursor.getString(26),
                        cursor.getString(27), cursor.getString(28),
                        cursor.getString(29), cursor.getString(30),
                        cursor.getString(31), cursor.getString(32));
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get tests from database!");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }

        return test;
    }

    public List<Test> getAllTests() {
        List<Test> testList = new ArrayList<Test>();
        // Select All Query
        String selectQuery = "SELECT * FROM " + TABLE_TEST;

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        Test test = null;
        try {
            // looping through all rows and adding to list
            if (cursor.moveToFirst()) {
                do {
                    test = new Test();
                    test.setId(Integer.parseInt(cursor.getString(0)));
                    test.setTitle(cursor.getString(1));
                    test.setChecked(Boolean.parseBoolean(cursor.getString(2)));
                    test.setType1(cursor.getString(3));
                    test.setType2(cursor.getString(4));
                    test.setType3(cursor.getString(5));
                    test.setType4(cursor.getString(6));
                    test.setType5(cursor.getString(7));
                    test.setQuestion1(cursor.getString(8));
                    test.setQuestion2(cursor.getString(9));
                    test.setQuestion3(cursor.getString(10));
                    test.setQuestion4(cursor.getString(11));
                    test.setQuestion5(cursor.getString(12));
                    test.setAnswer1(cursor.getString(13));
                    test.setAnswer2(cursor.getString(14));
                    test.setAnswer3(cursor.getString(15));
                    test.setAnswer4(cursor.getString(16));
                    test.setAnswer5(cursor.getString(17));
                    test.setAnswer11(cursor.getString(18));
                    test.setAnswer12(cursor.getString(19));
                    test.setAnswer13(cursor.getString(20));
                    test.setAnswer21(cursor.getString(21));
                    test.setAnswer22(cursor.getString(22));
                    test.setAnswer23(cursor.getString(23));
                    test.setAnswer31(cursor.getString(24));
                    test.setAnswer32(cursor.getString(25));
                    test.setAnswer33(cursor.getString(26));
                    test.setAnswer41(cursor.getString(27));
                    test.setAnswer42(cursor.getString(28));
                    test.setAnswer43(cursor.getString(29));
                    test.setAnswer51(cursor.getString(30));
                    test.setAnswer52(cursor.getString(31));
                    test.setAnswer53(cursor.getString(32));


                    testList.add(test);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get test from database");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return testList;
    }

    public void updateTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            values.put(KEY_TEST_TITLE, test.getTitle());
            values.put(KEY_TEST_CHECK, String.valueOf(test.getChecked()));
            values.put(KEY_TEST_TYPE1, test.getType1());
            values.put(KEY_TEST_TYPE2, test.getType2());
            values.put(KEY_TEST_TYPE3, test.getType3());
            values.put(KEY_TEST_TYPE4, test.getType4());
            values.put(KEY_TEST_TYPE5, test.getType5());
            values.put(KEY_TEST_QUESTION1, test.getQuestion1());
            values.put(KEY_TEST_QUESTION2, test.getQuestion2());
            values.put(KEY_TEST_QUESTION3, test.getQuestion3());
            values.put(KEY_TEST_QUESTION4, test.getQuestion4());
            values.put(KEY_TEST_QUESTION5, test.getQuestion5());
            values.put(KEY_TEST_ANSWER1, test.getAnswer1());
            values.put(KEY_TEST_ANSWER2, test.getAnswer2());
            values.put(KEY_TEST_ANSWER3, test.getAnswer3());
            values.put(KEY_TEST_ANSWER4, test.getAnswer4());
            values.put(KEY_TEST_ANSWER5, test.getAnswer5());
            values.put(KEY_TEST_ANSWER11, test.getAnswer11());
            values.put(KEY_TEST_ANSWER12, test.getAnswer12());
            values.put(KEY_TEST_ANSWER13, test.getAnswer13());
            values.put(KEY_TEST_ANSWER21, test.getAnswer21());
            values.put(KEY_TEST_ANSWER22, test.getAnswer22());
            values.put(KEY_TEST_ANSWER23, test.getAnswer23());
            values.put(KEY_TEST_ANSWER31, test.getAnswer31());
            values.put(KEY_TEST_ANSWER32, test.getAnswer32());
            values.put(KEY_TEST_ANSWER33, test.getAnswer33());
            values.put(KEY_TEST_ANSWER41, test.getAnswer41());
            values.put(KEY_TEST_ANSWER42, test.getAnswer42());
            values.put(KEY_TEST_ANSWER43, test.getAnswer43());
            values.put(KEY_TEST_ANSWER51, test.getAnswer51());
            values.put(KEY_TEST_ANSWER52, test.getAnswer52());
            values.put(KEY_TEST_ANSWER53, test.getAnswer53());

            // updating row
            db.update(TABLE_TEST, values, KEY_TEST_ID + " = ?",
                    new String[]{String.valueOf(test.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to update test!");
        } finally {
            db.endTransaction();
        }
    }

    public void deleteTest(Test test) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.beginTransaction();
        try {
            db.delete(TABLE_TEST, KEY_TEST_ID + " = ?",
                    new String[]{String.valueOf(test.getId())});
            db.setTransactionSuccessful();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to delete test!");
        } finally {
            db.endTransaction();
        }
    }

    public int getTestCount() {
        int count = 0;
        String countQuery = "SELECT * FROM " + TABLE_TEST;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        try {
            count = cursor.getCount();
        } catch (Exception e) {
            Log.d(TAG, "Error while trying to get user from database!");
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return count;
    }
}
