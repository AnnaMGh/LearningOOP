package com.learning.java.app.fragments;

import static android.app.Activity.RESULT_OK;

import android.Manifest;
import android.app.Activity;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.ColorDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.legacy.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;

import com.learning.java.app.Constants;
import com.learning.java.app.R;
import com.learning.java.app.activities.ContainerActivity;
import com.learning.java.app.database.FirestoreDatabase;
import com.learning.java.app.model.IBitmapListener;
import com.learning.java.app.model.IRefreshListener;
import com.learning.java.app.model.ITestListener;
import com.learning.java.app.model.IUserListener;
import com.learning.java.app.model.Test;
import com.learning.java.app.model.User;

import java.util.ArrayList;
import java.util.Calendar;

public class MainMenuFragment extends BaseFragment {

    private static final String TAG = "MAIN_MENU_F";
    //views
    TextView usernameTxtV;
    ImageView circularImgV;
    ViewPager viewPager;
    PagerTabStrip tabStrip;
    CustomFragmentAdapter customAdapter;
    ImageView cancel, camera, gallery;

    //variables from previous fragment
    public User user;
    public ArrayList<User> userList;
    public ArrayList<Test> testList;


    //variables
    Dialog mDialog;
    Uri imageUri;
    boolean isAllInfoDownload = false;
    boolean isFromPhotoIntent = false;
    boolean refreshInfoFromMenuProfile = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        baseView = inflater.inflate(R.layout.fragment_main_menu, container, false);

        initialize();
        onClickViews();

        return baseView;
    }

    @Override
    public void onResume() {
        super.onResume();

        if (!isFromPhotoIntent) {
            getInfoFromFirestore();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == Constants.INTENT_REQUEST_CAMERA && resultCode == RESULT_OK) {
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageUri);

                String picturePath = getRealPathFromURI(imageUri);
                ExifInterface exif = new ExifInterface(picturePath);
                int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                Log.d(TAG, "onActivityResult: Exif: " + orientation);
                Matrix matrix = new Matrix();
                if (orientation == 6) {
                    matrix.postRotate(90);
                } else if (orientation == 3) {
                    matrix.postRotate(180);
                } else if (orientation == 8) {
                    matrix.postRotate(270);
                }

                bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

                int width = bitmap.getWidth();
                int height = bitmap.getHeight();
                if (width > 800 || height > 800) {
                    width = gs.convertFromOneRangeToAnother(width, 0, Math.max(width, height), 0, 800);
                    height = gs.convertFromOneRangeToAnother(height, 0, Math.max(width, height), 0, 800);
                }
                bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

                circularImgV.setImageBitmap(bitmap);
                FirestoreDatabase.addPhotoWithCallback(user, bitmap, doRefresh -> getInfoFromFirestore());

            } catch (Exception e) {
                isFromPhotoIntent = false;
                Log.e(TAG, "onActivityResult: ERROR | INTENT_REQUEST_CAMERA | " + e);
            }
        } else if (requestCode == Constants.INTENT_REQUEST_GALLERY && resultCode == RESULT_OK) {
            try {
                Uri selectedImage = data.getData();
                String[] filePathColumn = {MediaStore.Images.Media.DATA};
                Cursor cursor = getActivity().getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                cursor.moveToFirst();
                int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                String picturePath = cursor.getString(columnIndex);
                cursor.close();

                BitmapFactory.Options opt = new BitmapFactory.Options();
                opt.inSampleSize = 2;
                Bitmap bitmap = BitmapFactory.decodeFile(picturePath, opt);

                if (bitmap == null) {
                    Toast.makeText(getActivity(), "Gallery Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ExifInterface exif = new ExifInterface(picturePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.d(TAG, "onActivityResult: Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                    }

                    bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap

                    int width = bitmap.getWidth();
                    int height = bitmap.getHeight();
                    if (width > 800 || height > 800) {
                        int oldWidth = width; //i put this here because after above width change it's size, the height will be modified
                        width = gs.convertFromOneRangeToAnother(width, 0, Math.max(width, height), 0, 800);
                        height = gs.convertFromOneRangeToAnother(height, 0, Math.max(oldWidth, height), 0, 800);
                    }
                    bitmap = Bitmap.createScaledBitmap(bitmap, width, height, false);

                    circularImgV.setImageBitmap(bitmap);
                    FirestoreDatabase.addPhotoWithCallback(user, bitmap, doRefresh -> {
                        if (!doRefresh) {
                            showSimpleAlert("It's been an error while update ");
                        }
                        getInfoFromFirestore();
                    });
                } catch (Exception e) {
                    isFromPhotoIntent = false;
                    Log.e(TAG, "onActivityResult ERROR | INTENT_REQUEST_GALLERY | " + e);
                }
            } catch (Exception e) {
                isFromPhotoIntent = false;
                Log.e(TAG, "onActivityResult ERROR  | INTENT_REQUEST_GALLERY | " + e);
            }

        }
    }

    private void initialize() {
        //views
        usernameTxtV = baseView.findViewById(R.id.mainmenu_txtv);
        circularImgV = baseView.findViewById(R.id.mainmenu_imgv);
        viewPager = baseView.findViewById(R.id.mainmenu_viewpager);
        tabStrip = baseView.findViewById(R.id.mainmenu_tabstrip);

        //user
        setUserInfo();

        //pager
        customAdapter = new CustomFragmentAdapter(getChildFragmentManager());
        customAdapter.setUser(user);
        customAdapter.setUserList(userList);
        customAdapter.setTestList(testList);
        customAdapter.setRefreshState(refreshInfoFromMenuProfile);
        customAdapter.setListenerRefresh(new IRefreshListener() {
            @Override
            public void doRefresh(boolean doRefresh) {
                customAdapter.notifyDataSetChanged();
            }
        });
        customAdapter.setListenerTestRefresh(new ITestListener() {
            @Override
            public void getAllTests(ArrayList<Test> receivedTestList) {
                sortUsers();
                customAdapter.notifyDataSetChanged();
            }

            @Override
            public void getTest(Test test) {
            }
        });
        viewPager.setAdapter(customAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        tabStrip.setTabIndicatorColor(getResources().getColor(android.R.color.white));
        tabStrip.setTextColor(getResources().getColor(android.R.color.white));
    }

    private void getInfoFromFirestore() {
        Log.d(TAG, "getInfoFromFirestore");
        showLoadingBar();

        //get all users
        FirestoreDatabase.getAllUsers(new IUserListener() {
            @Override
            public void getUser(User user) {
            }

            @Override
            public void getAllUsers(ArrayList<User> receivedUserList) {

                userList.clear();
                userList.addAll(receivedUserList);

                //sort userList
                for (int i = 0; i < userList.size() - 1; i++) {
                    for (int j = 0; j < userList.size() - 1; j++) {
                        if ((userList.get(j).getTotalPoints() < userList.get(j + 1).getTotalPoints()
                                && !userList.get(j).getFunction().equals(Constants.ADMIN_FUNCTION))
                                || userList.get(j + 1).getFunction().equals(Constants.ADMIN_FUNCTION)) {
                            User replacedUser = userList.get(j);
                            userList.set(j, userList.get(j + 1));
                            userList.set(j + 1, replacedUser);
                        }

                        if (userList.get(j).getToken().equals(user.getToken())) {
                            user = userList.get(j);
                        }
                    }
                }

                //update to firestore
                for (int i = 0; i < userList.size(); i++) {
                    if (userList.get(i).getId() != i) {
                        userList.get(i).setId(i);
                        final User sortedUser = userList.get(i);
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                FirestoreDatabase.updateUser(sortedUser);
                            }
                        }, 0);
                    }
                }

                if (isAllInfoDownload) {
                    setUserInfo();
                    isFromPhotoIntent = false;
                    customAdapter.notifyDataSetChanged();
                    hideLoadingBar();
                }

                isAllInfoDownload = true;
            }

            @Override
            public void getUserToken(String token) {
            }
        });

        //get all test
        FirestoreDatabase.getAllTests(new ITestListener() {
            @Override
            public void getAllTests(ArrayList<Test> receivedTestList) {
                testList.clear();
                testList.addAll(receivedTestList);

                if (isAllInfoDownload) {
                    setUserInfo();
                    isFromPhotoIntent = false;
                    customAdapter.notifyDataSetChanged();
                    hideLoadingBar();
                }
                isAllInfoDownload = true;
            }

            @Override
            public void getTest(Test test) {

            }
        });
    }

    private void setUserInfo() {
        if (user != null) {
            //photo
            FirestoreDatabase.getPhoto(user, new IBitmapListener() {
                @Override
                public void getBitmap(Bitmap bitmap) {
                    if (bitmap == null) {
                        circularImgV.setImageResource(R.drawable.user);
                    } else {
                        circularImgV.setImageBitmap(bitmap);

                    }

                }
            });

            //name
            usernameTxtV.setText(String.valueOf("Hello " + user.getName()));
            checkDailyProgress();
            sortUsers();
        }
    }

    private void checkDailyProgress() {

        int oneDayInMilliseconds = 86400000;

        //get yesterday time
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DAY_OF_MONTH, -1);
        int yesterdayDay = calendar.get(Calendar.DAY_OF_MONTH);
        int yesterdayMonth = calendar.get(Calendar.MONTH);
        int yesterdayYear = calendar.get(Calendar.YEAR);

        //get last entrance time
        calendar.setTimeInMillis(user.getRegisteredTimestamp());

        if (yesterdayDay == calendar.get(Calendar.DAY_OF_MONTH) && yesterdayMonth == calendar.get(Calendar.MONTH) && yesterdayYear == calendar.get(Calendar.YEAR)) {

            //add one more day
            user.setDaysInARaw(user.getDaysInARaw() + 1);
            user.setRegisteredTimestamp(Calendar.getInstance().getTimeInMillis());

            //set bonus points
            int bonusPoints = 1;
            switch (user.getDaysInARaw()) {
                case 5: {
                    bonusPoints = 5;
                    break;
                }
                case 10: {
                    bonusPoints = 10;
                    break;
                }
                case 25: {
                    bonusPoints = 25;
                    break;
                }
                case 50: {
                    bonusPoints = 50;
                    break;
                }
                case 100: {
                    bonusPoints = 100;
                    break;
                }
            }

            if (bonusPoints > 1) {
                showSimpleAlert("Congratulations, you achieved to learn " + user.getDaysInARaw() + " days in raw!\n"
                        + "You received " + user.getDaysInARaw() + " bonus points!");
            }

            //add bonus
            user.setTotalPoints(user.getTotalPoints() + bonusPoints);

            //update user to Firestore
            FirestoreDatabase.updateUser(user);
        } else if (user.getRegisteredTimestamp() == 0 || Calendar.getInstance().getTimeInMillis() - user.getRegisteredTimestamp() > oneDayInMilliseconds) {

            if (user.getRegisteredTimestamp() > 0) {
                showSimpleAlert("You just lost your series of " + user.getDaysInARaw() + " days in raw!");
            }

            //start days in raw over
            user.setDaysInARaw(1);
            user.setRegisteredTimestamp(Calendar.getInstance().getTimeInMillis());
            FirestoreDatabase.updateUser(user);
        }
    }

    private void sortUsers() {
        //update current user in list
        userList.set(user.getId(), user);

        //check if no admin
        if (user == null || !user.getFunction().equals(Constants.ADMIN_FUNCTION) || userList != null) {
            return;
        }

        //sort by points
        //sort userList
        for (int i = 0; i < userList.size() - 1; i++) {
            for (int j = 0; j < userList.size() - 1; j++) {
                if ((userList.get(j).getTotalPoints() < userList.get(j + 1).getTotalPoints()
                        && !userList.get(j).getFunction().equals(Constants.ADMIN_FUNCTION))
                        || userList.get(j + 1).getFunction().equals(Constants.ADMIN_FUNCTION)) {
                    User replacedUser = userList.get(j);
                    userList.set(j, userList.get(j + 1));
                    userList.set(j + 1, replacedUser);
                }
            }
        }

        //update ids
        for (int i = 0; i < userList.size(); i++) {
            if (userList.get(i).getId() != i) {
                userList.get(i).setId(i);
                final User sortedUser = userList.get(i);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirestoreDatabase.updateUser(sortedUser);
                    }
                }, 0);
            }
        }
    }

    private void startCameraIntent() {
        Log.d(TAG, "startCameraIntent");
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From your Camera");
        imageUri = getActivity().getContentResolver().insert(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, Constants.INTENT_REQUEST_CAMERA);
    }

    private String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    private void showCamera() {
        if (getActivity().getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA_ANY)) {
            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.CAMERA}, Constants.MY_PERMISSIONS_REQUEST_CAMERA);

                Activity currentActivity = getActivity();
                if (currentActivity instanceof ContainerActivity) {
                    Log.d(TAG, "showCamera: before doRefresh");
                    ((ContainerActivity) currentActivity).listenerRefresh = doRefresh -> {
                        Log.d(TAG, "showCamera: after doRefresh");
                        isFromPhotoIntent = true;
                        startCameraIntent();
                    };
                }
            } else {
                try {
                    startCameraIntent();
                } catch (Exception e) {
                    showSimpleAlert("Error accessing camera");
                }
            }
        } else {
            showSimpleAlert("No camera available");
        }
    }

    private void showGallery() {

        Activity currentActivity = getActivity();

        // Here, thisActivity is the current activity
        String permission = Manifest.permission.READ_EXTERNAL_STORAGE;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            permission = Manifest.permission.READ_MEDIA_IMAGES;
        }
        if (ContextCompat.checkSelfPermission(currentActivity, permission) != PackageManager.PERMISSION_GRANTED) {
            Log.e(TAG, "showGallery: Requesting " + permission + " permission.");
            ActivityCompat.requestPermissions(currentActivity, new String[]{permission}, Constants.MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);


            if (currentActivity instanceof ContainerActivity) {
                Log.d(TAG, "showGallery: before doRefresh");
                ((ContainerActivity) currentActivity).listenerRefresh = doRefresh -> {
                    Log.d(TAG, "showGallery: after doRefresh");
                    if (doRefresh) {
                        isFromPhotoIntent = true;
                        Intent i = new Intent(Intent.ACTION_PICK,
                                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(i, Constants.INTENT_REQUEST_GALLERY);
                    }
                };
            } else {
                isFromPhotoIntent = true;
                Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                startActivityForResult(i, Constants.INTENT_REQUEST_GALLERY);
            }
        } else {
            Log.e(TAG, "showGallery: Permission READ_EXTERNAL_STORAGE already accepted.");
            isFromPhotoIntent = true;
            Intent i = new Intent(Intent.ACTION_PICK,
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, Constants.INTENT_REQUEST_GALLERY);
        }
    }

    private void onClickViews() {
        circularImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new Dialog(getActivity());
                mDialog.setContentView(R.layout.alert_dialog_custom);
                mDialog.setCancelable(true);
                if (mDialog.getWindow() != null) {
                    mDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
                }
                cancel = mDialog.findViewById(R.id.alert_cancel);
                camera = mDialog.findViewById(R.id.alert_camera);
                gallery = mDialog.findViewById(R.id.alert_gallery);

                cancel.setEnabled(true);
                camera.setEnabled(true);
                gallery.setEnabled(true);

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        mDialog.cancel();
                    }
                });

                camera.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showCamera();
                        mDialog.cancel();
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        showGallery();
                        mDialog.cancel();
                    }
                });

                mDialog.show();
            }
        });
    }

    class CustomFragmentAdapter extends FragmentPagerAdapter {

        private User user;
        private ArrayList<User> userList;
        private ArrayList<Test> testList;
        private IRefreshListener listenerRefresh;
        private ITestListener listenerTestRefresh;
        private boolean refreshInfoFromMenuProfile;

        public CustomFragmentAdapter(FragmentManager manager) {
            super(manager);
        }

        public void setUser(User user) {
            this.user = user;
        }

        public void setUserList(ArrayList<User> userList) {
            this.userList = userList;
        }

        public void setTestList(ArrayList<Test> testList) {
            this.testList = testList;
        }

        public void setListenerRefresh(IRefreshListener listenerRefresh) {
            this.listenerRefresh = listenerRefresh;
        }

        public void setRefreshState(boolean state) {
            this.refreshInfoFromMenuProfile = state;
        }

        public void setListenerTestRefresh(ITestListener listenerTest) {
            this.listenerTestRefresh = listenerTest;
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0) {
                MenuProfileFragment fragmentProfile = new MenuProfileFragment();
                fragmentProfile.user = user;
                fragmentProfile.userList = userList;
                fragmentProfile.testList = testList;
                fragmentProfile.canRefresh = refreshInfoFromMenuProfile;
                return fragmentProfile;
            } else if (position == 1) {
                MenuLearnFragment fragmentLearn = new MenuLearnFragment();
                fragmentLearn.user = user;
                return fragmentLearn;
            } else {
                MenuTestFragment fragmentTest = new MenuTestFragment();
                fragmentTest.user = user;
                fragmentTest.testList = testList;
                fragmentTest.listenerRefresh = listenerRefresh;
                fragmentTest.listenerTestRefresh = listenerTestRefresh;
                return fragmentTest;
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            if (position == 0) return "Profile";
            else if (position == 1) return "Learn";
            else return "Test";
        }

        @Override
        public int getItemPosition(@NonNull Object object) {
            return POSITION_NONE;
        }
    }

}
