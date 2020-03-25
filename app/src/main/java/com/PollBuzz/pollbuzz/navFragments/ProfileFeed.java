package com.PollBuzz.pollbuzz.navFragments;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import com.PollBuzz.pollbuzz.LogIn_SignUp.Login_Signup_Activity;
import com.PollBuzz.pollbuzz.MainActivity;
import com.PollBuzz.pollbuzz.Polldetails;
import com.PollBuzz.pollbuzz.R;
import com.PollBuzz.pollbuzz.adapters.ProfileFeedAdapter;
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
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import Utils.ImagePickerActivity;
import Utils.helper;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class ProfileFeed extends Fragment {
    MaterialTextView Uname;
    ImageView pPic;
    ImageButton edit;
    FirebaseAuth mAuth;
    FirebaseUser mUser;
    StorageReference mStorage;
    Uri uri;
    RecyclerView profileRV;
    ProfileFeedAdapter mAdapter;
    ArrayList<Polldetails> mArrayList;
    private FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
    private CollectionReference userColRef, pollsColRef;
    private DocumentReference userDocRef;
    public ProfileFeed() {
        // Required empty public constructor
    }
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.activity_profile_feed, container, false);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        setHasOptionsMenu(true);
//        ((MainActivity) getActivity()).getSupportActionBar().setTitle("Your Polls");
        Toolbar toolbar = view.findViewById(R.id.htab_toolbar);
//        setSupportActionBar(toolbar);
        ((MainActivity) getActivity()).setSupportActionBar(toolbar);

        if (((MainActivity) getActivity()).getSupportActionBar() != null) {
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
            ((MainActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        userDocRef = firebaseFirestore.collection("Users").document(mUser.getUid());
        userColRef = userDocRef.collection("Created");
        pollsColRef = firebaseFirestore.collection("Polls");
        mStorage = FirebaseStorage.getInstance().getReference();
        Uname = view.findViewById(R.id.username);
        edit = view.findViewById(R.id.edit);
        pPic = view.findViewById(R.id.profilePic);
        profileRV = view.findViewById(R.id.profileRV);
        mArrayList = new ArrayList<>();
        mAdapter = new ProfileFeedAdapter(getContext(), mArrayList);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
        profileRV.setAdapter(mAdapter);
        profileRV.setLayoutManager(linearLayoutManager);
        userColRef.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    for (QueryDocumentSnapshot dS : task.getResult()) {
                        if (dS.get("pollId") != null)
                            pollsColRef.document(dS.get("pollId").toString()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                    if (task.isSuccessful() && task.getResult() != null) {
                                        DocumentSnapshot dS1 = task.getResult();
                                        if (dS1.exists()) {
                                            Polldetails polldetails = dS1.toObject(Polldetails.class);
                                            polldetails.setUID(dS1.getId());
                                            mArrayList.add(polldetails);
                                            mAdapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        Toast.makeText(getContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                    }
                }
            }
        });
        String imagePath = Utils.helper.getpPicPref(getContext());
        Log.d("imagePAth", imagePath);
        if (imagePath != null) {
            Glide.with(this)
                    .load(imagePath)
                    .transform(new CircleCrop())
                    .into(pPic);
        }
        Uname.setText(Utils.helper.getusernamePref(getContext()));
        edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    Dexter.withActivity(getActivity())
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
    }

    private void showImagePickerOptions() {
        ImagePickerActivity.showImagePickerOptions(getContext(), new ImagePickerActivity.PickerOptionListener() {
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
            Utils.helper.setpPicPref(getContext(), mUser.getPhotoUrl().toString());
            userDocRef.update("pic", mUser.getPhotoUrl().toString());
        } else {
            pPic.setImageResource(R.drawable.ic_person_black_24dp);
            Utils.helper.setpPicPref(getContext(), null);
            userDocRef.update("pic", null);
        }
        StorageReference mRef = mStorage.child("images/" + mUser.getUid());
        mRef.delete();
    }

    private void launchCameraIntent() {
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
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
        Intent intent = new Intent(getActivity(), ImagePickerActivity.class);
        intent.putExtra(ImagePickerActivity.INTENT_IMAGE_PICKER_OPTION, ImagePickerActivity.REQUEST_GALLERY_IMAGE);

        // setting aspect ratio
        intent.putExtra(ImagePickerActivity.INTENT_LOCK_ASPECT_RATIO, true);
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_X, 1); // 16x9, 1x1, 3:4, 3:2
        intent.putExtra(ImagePickerActivity.INTENT_ASPECT_RATIO_Y, 1);
        startActivityForResult(intent, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            if (resultCode == Activity.RESULT_OK) {
                uri = data.getParcelableExtra("path");
                try {
                    // You can update this bitmap to your server
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
                    // loading profile image from local cache

                    loadProfile(uri);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void loadProfile(Uri uri) {
        StorageReference mRef = mStorage.child("images/" + mUser.getUid());
        Bitmap bmp = null;
        byte[] dataCompressed = new byte[0];
        try {
            bmp = MediaStore.Images.Media.getBitmap(getContext().getContentResolver(), uri);
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
                                String imagePath = uri.toString();
                                Glide.with(ProfileFeed.this)
                                        .load(imagePath)
                                        .transform(new CircleCrop())
                                        .into(pPic);
                                helper.setpPicPref(getContext(), imagePath);
                                userDocRef.update("pic", imagePath);
                            }
                        });
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        exception.printStackTrace();
                        Toast.makeText(getContext(), exception.getMessage(), Toast.LENGTH_SHORT).show();
                        Log.d("Exception", exception.toString());
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    }
                });
        deleteDir(getContext().getCacheDir());
        deleteDir(getContext().getExternalCacheDir());
    }

    private void showSettingsDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
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
        Uri uri = Uri.fromParts("package", getContext().getPackageName(), null);
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        menu.clear();
        inflater.inflate(R.menu.main_menu,menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.logOut) {
            logOut();
        }
        return super.onOptionsItemSelected(item);
    }

    private void logOut() {
        if (mAuth.getCurrentUser() != null) {
            Utils.helper.removeProfileSetUpPref(getContext());
            mAuth.signOut();
            Intent i = new Intent(getActivity(), Login_Signup_Activity.class);
            i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(i);
        }
    }
}