package com.learning.java.app.fragments;

import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.legacy.app.FragmentPagerAdapter;
import androidx.viewpager.widget.PagerTabStrip;
import androidx.viewpager.widget.ViewPager;
import android.util.Base64;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.learning.java.app.R;
import com.learning.java.app.activities.ContainerActivity;
import com.learning.java.app.helper.SqliteHelper;
import com.learning.java.app.model.User;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static android.app.Activity.RESULT_OK;


public class MainMenuFragment extends BaseFragment {

    TextView usernameTxtV;
    ImageView circularImgV;
    ViewPager viewPager;
    PagerTabStrip tabStrip;
    CustomFragmentAdapter customAdapter;
    ImageView cancel, camera, gallery;
    Dialog mDialog;
    Bitmap bmp;
    SharedPreferences settings;
    SqliteHelper database;
    User user;

    public static final int PICK_GALLERY_IMAGE = 1;
    public static final int PICK_CAMERA_IMAGE = 2;

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
        customAdapter.notifyDataSetChanged();
    }

    void initialize() {
        usernameTxtV = baseView.findViewById(R.id.mainmenu_txtv);
        circularImgV = baseView.findViewById(R.id.mainmenu_imgv);
        viewPager = baseView.findViewById(R.id.mainmenu_viewpager);
        tabStrip = baseView.findViewById(R.id.mainmenu_tabstrip);

        viewPager.setOffscreenPageLimit(2);

        settings = getActivity().getSharedPreferences("Learning_java", 0);
        int id = Integer.parseInt(settings.getString("ACCOUNT_KEY", null));
        String username = settings.getString("ACCOUNT_NAME", null);
       // String type = settings.getString("ACCOUNT_TYPE", null);
       // String function = settings.getString("ACCOUNT_FUNCTION", null);

        database = SqliteHelper.getInstance(getActivity());
        user = database.getUser(id);

        if (user != null) {
            if (user.getPhoto() == null) {

                bmp = ((BitmapDrawable) circularImgV.getDrawable()).getBitmap();
                user.setPhoto(encodeToBase64(bmp, Bitmap.CompressFormat.JPEG, 100));
                database.updateUser(user);
                circularImgV.setImageBitmap(decodeBase64(user.getPhoto()));

            } else {
                circularImgV.setImageBitmap(decodeBase64(user.getPhoto()));
            }
        }

        usernameTxtV.setText("Hello " + username);
        //pager
        customAdapter = new CustomFragmentAdapter(getChildFragmentManager());
        viewPager.setAdapter(customAdapter);
        viewPager.setOffscreenPageLimit(2);
        tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
        tabStrip.setTabIndicatorColor(getResources().getColor(android.R.color.white));
        tabStrip.setTextColor(getResources().getColor(android.R.color.white));
    }

    String encodeToBase64(Bitmap image, Bitmap.CompressFormat compressFormat, int quality) {
        ByteArrayOutputStream byteArrayOS = new ByteArrayOutputStream();
        image.compress(compressFormat, quality, byteArrayOS);
        return Base64.encodeToString(byteArrayOS.toByteArray(), Base64.DEFAULT);
    }

    Bitmap decodeBase64(String input) {
        byte[] decodedBytes = Base64.decode(input, 0);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    void onClickViews() {
        circularImgV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDialog = new Dialog(getActivity());
                mDialog.setContentView(R.layout.alert_dialog_custom);
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
                        Intent cameraIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
                        startActivityForResult(Intent.createChooser(cameraIntent, "Select Picture"), PICK_CAMERA_IMAGE);
                    }
                });

                gallery.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        galleryIntent.setType("image/*");
                        startActivityForResult(Intent.createChooser(galleryIntent, "Select Picture"), PICK_GALLERY_IMAGE);
                        mDialog.cancel();


                    }
                });

                mDialog.show();
            }
        });
    }

    void setPhoto() {
        String imageURL;

        if (settings.getString("ACCOUNT_TYPE", null).equals("facebook")) {
            ContainerActivity activity = (ContainerActivity) getActivity();

            imageURL = "http://graph.facebook.com/" + activity.facebookId + "/picture?type=large";
            InputStream in = null;
            try {
                in = (InputStream) new URL(imageURL).getContent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            bmp = BitmapFactory.decodeStream(in);
        }

        circularImgV.setImageBitmap(bmp);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_GALLERY_IMAGE && resultCode == RESULT_OK) {
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
                bmp = BitmapFactory.decodeFile(picturePath, opt);

                if (bmp == null) {
                    Toast.makeText(getActivity(), "Gallery Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    ExifInterface exif = new ExifInterface(picturePath);
                    int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
                    Log.d("EXIF", "Exif: " + orientation);
                    Matrix matrix = new Matrix();
                    if (orientation == 6) {
                        matrix.postRotate(90);
                    } else if (orientation == 3) {
                        matrix.postRotate(180);
                    } else if (orientation == 8) {
                        matrix.postRotate(270);
                    }
                    bmp = Bitmap.createBitmap(bmp, 0, 0, bmp.getWidth(), bmp.getHeight(), matrix, true); // rotating bitmap
                    //circularImgV.setImageBitmap(bmp);

                    // user.setPhoto(bitmapToByte());

                    /*byte[] b;
                    b = user.getPhoto();

                    //circularImgV.setImageBitmap(null);
                    circularImgV.setImageBitmap(byteToBitmap(user.getPhoto()));
                    database.updateUser(user);*/

                    ////////////////////////////////////////////////////////////////////////////////////////////
                    ///          si  aici apelez metodele care transforma imaginea cu Base 64                  ///
                    ////////////////////////////////////////////////////////////////////////////////////////////

                    user.setPhoto(encodeToBase64(bmp, Bitmap.CompressFormat.JPEG, 100));
                    database.updateUser(user);
                    Bitmap b = decodeBase64(user.getPhoto());
                    circularImgV.setImageBitmap(b);


                } catch (Exception e) {
                    String s = "";
                    Toast.makeText(getActivity(), "Error 3", Toast.LENGTH_SHORT).show();
                }
            } catch (Exception e) {
                String s = "";
                Toast.makeText(getActivity(), "Error 2", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_GALLERY_IMAGE && resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), "Error 1", Toast.LENGTH_SHORT).show();
        }


        if (requestCode == PICK_CAMERA_IMAGE && resultCode == RESULT_OK) {

            try {
                Bundle extras = data.getExtras();
                bmp = (Bitmap) extras.get("data");
                if (bmp == null) {
                    Toast.makeText(getActivity(), "Camera Error", Toast.LENGTH_SHORT).show();
                    return;
                }

                circularImgV.setImageBitmap(bmp);
            } catch (Exception e) {
                Toast.makeText(getActivity(), "Error 2", Toast.LENGTH_SHORT).show();
            }
        } else if (requestCode == PICK_CAMERA_IMAGE && resultCode != RESULT_OK) {
            Toast.makeText(getActivity(), "Error 1", Toast.LENGTH_SHORT).show();
        }
    }


    byte[] bitmapToByte() {
        Bitmap bitmap = ((BitmapDrawable) circularImgV.getDrawable()).getBitmap();
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        return baos.toByteArray();
    }

    Bitmap byteToBitmap(byte[] imageInByte) {
        return BitmapFactory.decodeByteArray(imageInByte, 0, imageInByte.length);
    }


    class CustomFragmentAdapter extends FragmentPagerAdapter {


        public CustomFragmentAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            if (position == 0)
                return new MenuProfileFragment();
            else if (position == 1)
                return new MenuLearnFragment();
            else
                return new MenuTestFragment();
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
