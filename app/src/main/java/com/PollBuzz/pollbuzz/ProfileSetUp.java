package com.PollBuzz.pollbuzz;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

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
import android.view.View;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

public class ProfileSetUp extends AppCompatActivity {
    TextInputLayout name, Uname, date;
    ImageView pPic;
    ImageButton edit;
    MaterialButton save, male, female;
    DatePickerDialog mDatePickerDialog;
    FirebaseFirestore db;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference mStorage;
    Uri uri;
    int flag = 1;
    int age = 0;
    String gender = null;
    String imagePath = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_set_up);
        getSupportActionBar().setTitle("Create Profile");
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        name = findViewById(R.id.name);
        if (mUser.getDisplayName() != null)
            name.getEditText().setText(mAuth.getCurrentUser().getDisplayName());
        Uname = findViewById(R.id.Uname);
        date = findViewById(R.id.birth);
        edit = findViewById(R.id.edit);
        pPic = findViewById(R.id.profilePic);
        if (mUser.getPhotoUrl() != null) {

            Glide.with(this)
                    .load(mUser.getPhotoUrl())
                    .transform(new CircleCrop())
                    .into(pPic);
            imagePath = mUser.getPhotoUrl().getPath();
        }
        male = findViewById(R.id.male);
        female = findViewById(R.id.female);
        male.setBackgroundColor(getResources().getColor(R.color.white));
        male.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        male.setAlpha(0.5f);
        male.setElevation(-0.5f);
        female.setBackgroundColor(getResources().getColor(R.color.white));
        female.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        female.setAlpha(0.5f);
        female.setElevation(-0.5f);
        save = findViewById(R.id.save);

        date.getEditText().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final Calendar cldr = Calendar.getInstance();
                final int dayT = cldr.get(Calendar.DAY_OF_MONTH);
                final int monthT = cldr.get(Calendar.MONTH);
                final int yearT = cldr.get(Calendar.YEAR);
                // date picker dialog
                mDatePickerDialog = new DatePickerDialog(ProfileSetUp.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                date.getEditText().setText(dayOfMonth + "/" + (monthOfYear + 1) + "/" + year);
                                age = yearT - year;
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
                        }, yearT, monthT, dayT);
                mDatePickerDialog.show();
            }
        });
        male.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                male.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                male.setTextColor(getResources().getColor(R.color.white));
                female.setBackgroundColor(getResources().getColor(R.color.white));
                female.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                male.setAlpha(1.0f);
                female.setAlpha(0.5f);
                female.setElevation(-0.5f);
                gender = "male";
            }
        });
        female.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                female.setBackgroundColor(getResources().getColor(R.color.colorPrimaryDark));
                female.setTextColor(getResources().getColor(R.color.white));
                male.setBackgroundColor(getResources().getColor(R.color.white));
                male.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                female.setAlpha(1.0f);
                male.setAlpha(0.5f);
                male.setElevation(-0.5f);
                gender = "female";
            }
        });
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
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
            }
        });
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String nameS = name.getEditText().getText().toString();
                String UnameS = Uname.getEditText().getText().toString();
                String birthD = date.getEditText().getText().toString();
                saveP(nameS, UnameS, birthD, gender);
            }
        });
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
                defaultIntent();
            }
        });
    }

    private void defaultIntent() {
        if (mUser.getPhotoUrl() != null) {
            Glide.with(this)
                    .load(mUser.getPhotoUrl())
                    .transform(new CircleCrop())
                    .into(pPic);
            imagePath = mUser.getPhotoUrl().getPath();
        } else {
            pPic.setImageResource(R.drawable.ic_person_black_24dp);
            imagePath = null;
        }
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

                    loadProfile(uri.toString());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProfile(String url) {
        Log.d("TAG", "Image cache path: " + url);

        Glide.with(this)
                .load(url)
                .transform(new CircleCrop())
                .into(pPic);
    }

    private void saveP(String nameS, String UnameS, String Bday, String gender) {
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
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Creating profile...");
            progressDialog.setMessage("Please Wait...");
            progressDialog.show();
            Map<String, String> data = new HashMap<>();
            data.put("name", nameS);
            data.put("username", UnameS);
            data.put("birthdate", Bday);
            data.put("age", String.valueOf(age));
            data.put("gender", gender);
            if (uri == null) {
                data.put("pic", null);
                db.collection("Users")
                        .document(mUser.getUid())
                        .set(data)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()) {
                                    Utils.helper.setProfileSetUpPref(getApplicationContext(), true);
                                    progressDialog.dismiss();
                                    Intent i = new Intent(ProfileSetUp.this, MainActivity.class);
                                    i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(i);
                                } else {
                                    Toast.makeText(ProfileSetUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    Log.d("Exception", task.getException().toString());
                                }
                            }
                        });
            } else {
                StorageReference mRef = mStorage.child("images/" + mUser.getUid());
                Bitmap bmp = null;
                byte[] dataCompressed = new byte[0];
                try {
                    bmp = MediaStore.Images.Media.getBitmap(getContentResolver(), uri);
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    bmp.compress(Bitmap.CompressFormat.JPEG, 40, baos);
                    dataCompressed = baos.toByteArray();
                } catch (IOException e) {
                    e.printStackTrace();
                    Log.d("Exception", e.toString());
                }
                mRef.putBytes(dataCompressed)
                        .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // Get a URL to the uploaded content
                                mRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                    @Override
                                    public void onSuccess(Uri uri) {
                                        imagePath = uri.toString();
                                        data.put("pic", imagePath);
                                        db.collection("Users")
                                                .document(mUser.getUid())
                                                .set(data)
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        if (task.isSuccessful()) {
                                                            Utils.helper.setProfileSetUpPref(getApplicationContext(), true);
                                                            deleteDir(getApplicationContext().getCacheDir());
                                                            deleteDir(getApplicationContext().getExternalCacheDir());
                                                            progressDialog.dismiss();
                                                            Intent i = new Intent(ProfileSetUp.this, MainActivity.class);
                                                            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                                                            startActivity(i);
                                                        } else {
                                                            Toast.makeText(ProfileSetUp.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                                            Log.d("Exception", task.getException().toString());
                                                        }
                                                    }
                                                });
                                    }
                                });
                            }
                        })
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

    // navigating user to app settings
    private void openSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivityForResult(intent, 101);
    }
    /**
     * Calling this will delete the images from cache directory
     * useful to clear some memory
     */
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
        } else if(dir!= null && dir.isFile()) {
            return dir.delete();
        } else {
            return false;
        }
    }
}
