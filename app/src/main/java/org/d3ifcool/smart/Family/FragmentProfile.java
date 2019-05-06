package org.d3ifcool.smart.Family;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.gdacciaro.iOSDialog.iOSDialog;
import com.gdacciaro.iOSDialog.iOSDialogBuilder;
import com.gdacciaro.iOSDialog.iOSDialogClickListener;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.d3ifcool.smart.AccountActivity.LoginActivity;
import org.d3ifcool.smart.AccountActivity.RegistActivity;
import org.d3ifcool.smart.Model.User;
import org.d3ifcool.smart.R;

import java.util.HashMap;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.MODE_PRIVATE;

public class FragmentProfile extends Fragment {

    private static final String TAG = "History";
    private TextView name, email, password, account;
    private Button logOut, removeAccount;
    private FirebaseAuth auth;
    private FirebaseAuth.AuthStateListener authListener;
    private ProgressDialog pd;
    private ProgressBar load_pict;
    private ImageView image_profile;
    static int PReqCode = 1 ;
    static int REQUESCODE = 1 ;

    private FirebaseUser firebaseUser;
    private String profileid;
    private Uri mImageUri;
    private StorageTask<UploadTask.TaskSnapshot> uploadTask;
    private StorageReference storageRef;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.profile_fragment,container,false);

        image_profile = view.findViewById(R.id.image_profile);

        name = view.findViewById(R.id.setname);

        email = view.findViewById(R.id.setemail);

        account = view.findViewById(R.id.setaccount);

        password = view.findViewById(R.id.value_password);

        load_pict = view.findViewById(R.id.load_pic);

        load_pict.setVisibility(View.VISIBLE);

        removeAccount = view.findViewById(R.id.delete_acoun);

        logOut = view.findViewById(R.id.btn_logout);

        auth = FirebaseAuth.getInstance();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        storageRef = FirebaseStorage.getInstance().getReference("uploads");

        SharedPreferences prefs = getContext().getSharedPreferences("PREFS", MODE_PRIVATE);

        profileid = prefs.getString("profileid", "none");

        pd = new ProgressDialog(getActivity(), R.style.MyAlertDialogStyle);

        userInfo();

        if (profileid.equals(firebaseUser.getUid())){

        }


        image_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (Build.VERSION.SDK_INT >= 22){

                    checkAndRequestForPermission();

                }

                else {

                    cropImage();

                }

            }

        });


        authListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                FirebaseUser user = firebaseAuth.getCurrentUser();

                if (user == null) {

                    startActivity(new Intent(getActivity(), LoginActivity.class));

                    getActivity().finish();

                }

            }

        };


        logOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                logOutAccount();

                getActivity().overridePendingTransition(R.anim.fade_in, R.anim.fade_out);

            }

            FirebaseAuth.AuthStateListener authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {

                    FirebaseUser user = firebaseAuth.getCurrentUser();

                    if (user == null) {

                        pd.hide();

                        startActivity(new Intent(getActivity(), LoginActivity.class));

                        getActivity().finish();

                    }

                }

            };

            private void logOutAccount() {

                auth.signOut();
            }

        });


        removeAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                        child(firebaseUser.getEmail().replace(".",","));

                new iOSDialogBuilder(getActivity())

                .setTitle("DELETE ACCOUNT")

                .setSubtitle("If your unsubscribe, you will lose all information. Doyou want to continue?")

                .setNegativeListener("NO", new iOSDialogClickListener() {

                            public void onClick(iOSDialog dialog) {

                                dialog.dismiss();

                            }

                        })

                .setPositiveListener("YES", new iOSDialogClickListener() {

                            public void onClick(iOSDialog dialog) {

                                pd.setMessage("Please wait...");

                                pd.setCancelable(false);

                                pd.setCanceledOnTouchOutside(false);

                                pd.show();

                                if (firebaseUser != null) {

                                    firebaseUser.delete()

                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {

                                                    pd.hide();

                                                    if (task.isSuccessful()) {

                                                        Toast.makeText(getActivity(), "Your profile is deleted:( Create a account now!", Toast.LENGTH_SHORT).show();

                                                        startActivity(new Intent(getActivity(), RegistActivity.class));

                                                        getActivity().finish();

                                                    } else {

                                                        pd.hide();

                                                        Toast.makeText(getActivity(), "Failed to delete your account!", Toast.LENGTH_SHORT).show();

                                                    }

                                                }

                                            });

                                }

                            }

                        }).build().show();

            }

        });

        return view;

    }

    private void cropImage() {

        CropImage.activity()

                .setAspectRatio(1, 1)

                .setCropShape(CropImageView.CropShape.OVAL)

                .start(getContext(), FragmentProfile.this);

    }

    private void userInfo(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getEmail().replace(".", ","));
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                String showEmail = firebaseUser.getEmail();

                if (getContext() == null){

                    return;

                }

                User user = dataSnapshot.getValue(User.class);

                Glide.with(getContext()).load(user.getImageurl()).into(image_profile);

                name.setText(user.getFullname());

                email.setText(showEmail);

                account.setText(user.getTypeAccount());

                load_pict.setVisibility(View.INVISIBLE);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }

        });

    }

    public void deleteDataUser(){

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").child(firebaseUser.getUid());

        reference.removeValue();

        startActivity(new Intent(getActivity(), RegistActivity.class));

    }


    private String getFileExtension(Uri uri){

        ContentResolver cR = getActivity().getContentResolver();

        MimeTypeMap mime = MimeTypeMap.getSingleton();

        return mime.getExtensionFromMimeType(cR.getType(uri));

    }


    private void uploadImage(){

        pd.setMessage("Uploading...");

        pd.show();

        if (mImageUri != null){

            final StorageReference fileReference = storageRef.child(System.currentTimeMillis()

                    + "." + getFileExtension(mImageUri));

            uploadTask = fileReference.putFile(mImageUri);

            uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {

                    if (!task.isSuccessful()) {

                        throw task.getException();

                    }

                    return fileReference.getDownloadUrl();

                }

            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {

                    if (task.isSuccessful()) {

                        Uri downloadUri = task.getResult();

                        String miUrlOk = downloadUri.toString();

                        DatabaseReference reference = FirebaseDatabase.getInstance().getReference("Users").
                                child(firebaseUser.getEmail().replace(".", ","));

                        DatabaseReference reference1 = FirebaseDatabase.getInstance().getReference();

                        String code = reference1.push().getKey();

                        reference1.child("Devices").child(code).child("Member").child(firebaseUser.getEmail().replace(".", ","));

                        HashMap<String, Object> map1 = new HashMap<>();

                        map1.put("imageurl", ""+miUrlOk);

                        reference.updateChildren(map1);

                        reference1.child("imageurl").setValue(miUrlOk);

                        pd.dismiss();

                    } else {

                        Toast.makeText(getActivity(), "Failed", Toast.LENGTH_SHORT).show();

                    }

                }

            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(getActivity(), e.getMessage(), Toast.LENGTH_SHORT).show();

                }

            });

        } else {

            Toast.makeText(getActivity(), "No image selected", Toast.LENGTH_SHORT).show();

        }

    }

    private void openGallery() {

        Intent galleryIntent = new Intent(Intent.ACTION_GET_CONTENT);

        galleryIntent.setType("image/*");

        startActivityForResult(galleryIntent,REQUESCODE);

    }


    private void checkAndRequestForPermission() {

        if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED) {

        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE)) {

            Toast.makeText(getActivity(),"Please accept for required permission",Toast.LENGTH_SHORT).show();

        }

        else
        {
            ActivityCompat.requestPermissions(getActivity(),

                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    PReqCode);

        }

    }
        else{

            cropImage();

        }

    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {

            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            mImageUri = result.getUri();

            uploadImage();

        } else {

            Toast.makeText(getActivity(), "Something gone wrong!", Toast.LENGTH_SHORT).show();

        }

    }


    @Override
    public void onResume() {

        super.onResume();

    }


    @Override
    public void onStart() {

        super.onStart();

        auth.addAuthStateListener(authListener);

    }


    @Override
    public void onStop() {

        super.onStop();

        if (authListener != null) {

            auth.removeAuthStateListener(authListener);

        }

    }

}


