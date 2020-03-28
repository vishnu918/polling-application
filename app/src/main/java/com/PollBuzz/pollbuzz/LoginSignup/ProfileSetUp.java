package com.PollBuzz.pollbuzz.LoginSignup;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.bitmap.CircleCrop;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.ImagePickerActivity;
import Utils.firebase;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSetUp extends AppCompatActivity {
    TextInputLayout name, Uname, date;
    ImageView pPic;
    ImageButton edit;
    MaterialButton save, male, female;
    DatePickerDialog mDatePickerDialog;
    Uri uri;
    int flag = 1;
    int age = 0;
    String gender = null;
    firebase fb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set_up);
        setGlobals();
        setListeners();
    }

    private void setListeners() {
        date.getEditText().setOnClickListener(view -> {
            final Calendar cldr = Calendar.getInstance();
            final int dayT = cldr.get(Calendar.DAY_OF_MONTH);
            final int monthT = cldr.get(Calendar.MONTH);
            final int yearT = cldr.get(Calendar.YEAR);
            mDatePickerDialog = new DatePickerDialog(ProfileSetUp.this,
                    new DatePickerDialog.OnDateSetListener() {
                        @Override
                        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                            date.getEditText().setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                            age = yearT - year;
                            isDateValid(year, monthOfYear, dayOfMonth, yearT, monthT, dayT);
                        }
                    }, yearT, monthT, dayT);
            mDatePickerDialog.show();
        });
        male.setOnClickListener(view -> {
            isMale();
        });
        female.setOnClickListener(view -> isFemale());
        edit.setOnClickListener(view -> {
            try {
                Dexter.withActivity(ProfileSetUp.this)
                        .withPermissions(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        .withListener(new MultiplePermissionsListener() {
                            @Override
                            public void onPermissionsChecked(MultiplePermissionsReport report) {
                                if (report.areAllPermissionsGranted()) {
                                    showImagePickerOptions();
                                }

                                if (report.isAnyPermissionPermanentlyDenied()) {
                                    showSettingsDialog();
                                }
                            }

                            @Override
                            public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                                token.continuePermissionRequest();
                            }
                        }).check();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
        save.setOnClickListener(view -> {
            saveProfile();
        });
    }

    private void isFemale() {
        female.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        female.setTextColor(getResources().getColor(R.color.white));
        female.setAlpha(1.0f);
        male.setBackgroundColor(getResources().getColor(R.color.white));
        male.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        male.setAlpha(0.5f);
        male.setElevation(-0.5f);
        gender = "female";
    }

    private void isMale() {
        male.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
        male.setTextColor(getResources().getColor(R.color.white));
        male.setAlpha(1.0f);
        female.setBackgroundColor(getResources().getColor(R.color.white));
        female.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        female.setAlpha(0.5f);
        female.setElevation(-0.5f);
        gender = "male";
    }

    private void isDateValid(int year, int monthOfYear, int dayOfMonth, int yearT, int monthT, int dayT) {
        if (yearT < year) {
            flag = 0;
        } else if (yearT == year) {
            if (monthT < monthOfYear)
                flag = 0;
            else if (monthT == monthOfYear + 1) {
                if (dayT < dayOfMonth)
                    flag = 0;
                else flag = 1;
            } else flag = 1;
        } else flag = 1;
    }

    private void setGlobals() {
        getSupportActionBar().setTitle("Create Profile");
        name = findViewById(R.id.name);
        Uname = findViewById(R.id.Uname);
        date = findViewById(R.id.birth);
        edit = findViewById(R.id.edit);
        pPic = findViewById(R.id.profilePic);
        male = findViewById(R.id.male);
        male.setBackgroundColor(getResources().getColor(R.color.white));
        male.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        male.setAlpha(0.5f);
        male.setElevation(-0.5f);
        female = findViewById(R.id.female);
        female.setBackgroundColor(getResources().getColor(R.color.white));
        female.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        female.setAlpha(0.5f);
        female.setElevation(-0.5f);
        save = findViewById(R.id.save);
        fb = new firebase();
        if (fb.getUser().getDisplayName() != null)
            name.getEditText().setText(fb.getUser().getDisplayName());
        loadProfilePic(fb.getUser().getPhotoUrl());
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(this, new ImagePickerActivity.PickerOptionListener() {
            @Override
            public void onTakeCameraSelected() {
                launchCameraIntent();
            }

            @Override
            public void onChooseGallerySelected() {
                launchGalleryIntent();
            }

            @Override
            public void defaultPic() {
                loadProfilePic(fb.getUser().getPhotoUrl());
            }
        });
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(ProfileSetUp.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_IMAGE_CAPTURE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);

        // setting maximum bitmap width and height
        intent.putExtra(ImagePickerActivity.INTENT_SET_BITMAP_MAX_WIDTH_HEIGHT, true);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_WIDTH, 1000);
        intent.putExtra(ImagePickerActivity.INTENT_BITMAP_MAX_HEIGHT, 1000);

        startActivityForResult(intent, 100);
    }

    private void launchGalleryIntent() {
        Intent intent = new Intent(ProfileSetUp.this, ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                    // loading profile image from local cache

                    loadProfilePic(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProfilePic(Uri uri) {
        if (uri != null) {
            Glide.with(this)
                    .load(uri)
                    .transform(new CircleCrop())
                    .into(pPic);
        } else {
            pPic.setImageResource(R.drawable.ic_person_black_24dp);
        }
    }

    private void saveProfile() {
        String nameS = name.getEditText().getText().toString();
        String UnameS = Uname.getEditText().getText().toString();
        String Bday = date.getEditText().getText().toString();
        if (nameS.isEmpty()) {
            Toast.makeText(this, "Please enter your full name!", Toast.LENGTH_SHORT).show();
            name.requestFocus();
        } else if (UnameS.isEmpty()) {
            Toast.makeText(this, "Please enter a username!", Toast.LENGTH_SHORT).show();
            Uname.requestFocus();
        } else if (Bday.isEmpty()) {
            Toast.makeText(this, "Please enter your birth date!", Toast.LENGTH_SHORT).show();
            date.requestFocus();
        } else if (flag == 0) {
            Toast.makeText(this, "Select appropriate birth date!", Toast.LENGTH_SHORT).show();
            date.requestFocus();
        } else if (gender == null) {
            Toast.makeText(this, "Select your gender!", Toast.LENGTH_SHORT).show();
        } else {
            createProfile(nameS, UnameS, Bday);
        }
    }

    private void createProfile(String nameS, String unameS, String bday) {
        ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Creating profile...");
        progressDialog.setMessage("Please Wait...");
        progressDialog.show();
        Map<String, String> data = new HashMap<>();
        data.put("name", nameS);
        data.put("username", unameS);
        data.put("birthdate", bday);
        data.put("age", String.valueOf(age));
        data.put("gender", gender);
        if (uri == null) {
            if (fb.getUser().getPhotoUrl() == null)
                data.put("pic", null);
            else
                data.put("pic", fb.getUser().getPhotoUrl().toString());
            addToDatabase(unameS, data);
        } else {
            addToStorage(unameS, data);
        }
        progressDialog.dismiss();
    }

    private void addToDatabase(String unameS, Map<String, String> data) {
        fb.getUserDocument()
                .set(data)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        setSharedPreference(unameS, fb.getUser().getPhotoUrl().toString());
                        Intent i = new Intent(ProfileSetUp.this, MainActivity.class);
                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(i);
                    } else {
                        Toast.makeText(ProfileSetUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Exception", task.getException().toString());
                    }
                });
    }

    private void addToStorage(String unameS, Map<String, String> data) {
        StorageReference mRef = fb.getStorageReference().child("images/" + fb.getUserId());
        byte[] compressedImage = compressImage();
        if (compressedImage != null) {
            mRef.putBytes(compressedImage)
                    .addOnSuccessListener(taskSnapshot -> mRef.getDownloadUrl().addOnSuccessListener(uri -> {
                        String imagePath = uri.toString();
                        data.put("pic", imagePath);
                        fb.getUserDocument()
                                .set(data)
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        setSharedPreference(unameS, imagePath);
                                        deleteCache();
                                        Intent i = new Intent(ProfileSetUp.this, MainActivity.class);
                                        i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                        startActivity(i);
                                    } else {
                                        Toast.makeText(ProfileSetUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        Log.d("Exception", task.getException().toString());
                                    }
                                });
                    }))
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            exception.printStackTrace();
                            Log.d("Exception", exception.toString());
                        }
                    })
                    .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                            double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                        }
                    });
        }
    }

    private void deleteCache() {
        deleteDir(getApplicationContext().getCacheDir());
        deleteDir(getApplicationContext().getExternalCacheDir());
    }

    private byte[] compressImage() {
        try {
            Bitmap bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
            return baos.toByteArray();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("Exception", e.toString());
            return null;
        }
    }

    private void setSharedPreference(String unameS, String imagePath) {
        Utils.helper.setProfileSetUpPref(getApplicationContext(), true);
        if (imagePath == null)
            Utils.helper.setpPicPref(getApplicationContext(), null);
        else
            Utils.helper.setpPicPref(getApplicationContext(), imagePath);
        Utils.helper.setusernamePref(getApplicationContext(), unameS);
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(ProfileSetUp.this);
        builder.setTitle("Grant Permissions");
        builder.setMessage("This app needs permission to use this feature. You can grant them in app settings.");
        builder.setPositiveButton("Go to settings", (dialog, which) -> {
            dialog.cancel();
            openSettings();
        });
        builder.setNegativeButton(getString(android.R.string.cancel), (dialog, which) -> dialog.cancel());
        builder.show();

    }

    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }

    public static boolean deleteDir(File dir) {
        if (dir != null && dir.isDirectory()) {
            String[] children = dir.list();
            if (children != null) {
                for (String child : children) {
                    boolean success = deleteDir(new File(dir, child));
                    if (!success) {
                        return false;
                    }
                }
            }
            return dir.delete();
        } else if (dir != null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
