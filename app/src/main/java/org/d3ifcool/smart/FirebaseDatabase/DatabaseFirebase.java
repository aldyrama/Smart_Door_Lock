package org.d3ifcool.smart.FirebaseDatabase;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.d3ifcool.smart.Account.Account;
import org.d3ifcool.smart.Home.MainActivity;
import org.d3ifcool.smart.Model.Connect;
import org.d3ifcool.smart.Model.User;

import java.util.ArrayList;

public class DatabaseFirebase implements DatabaseAction.user , DatabaseAction.appInfo {

    public String UUIDImage;
    private FirebaseDatabase databaseFirebase;
    private DatabaseReference mDatabase;
    FirebaseStorage storage;
    StorageReference storageReference;
    private Context mContext;
    ProgressDialog progressDialog;
    Uri resultUri;
    String username = "";
    public int nApp = 0;
//    public ArrayList<App> appInfo = new ArrayList<>();


    public DatabaseFirebase(Context context) {
        this.mContext = context;
        databaseFirebase = FirebaseDatabase.getInstance();
        progressDialog = new ProgressDialog(mContext);
        storage = FirebaseStorage.getInstance();
        storageReference = storage.getReference();
    }

    public DatabaseFirebase() {
    }


    public DatabaseReference getmDatabase() {
        return mDatabase;
    }


    @Override
    public void insertUser(User user) {
        progressDialog.setMessage("Please wait...");
        progressDialog.setCancelable(false);
        progressDialog.show();

        username = user.getUsername();
        dataUser(user);
//        uploadImage(imageUri, user.UUIDImage, user);

    }

    public void inserInviteUser(String username, Connect connect) {

        mDatabase = FirebaseDatabase.getInstance().getReference().child("connected");
        mDatabase.child("connect_" + username).setValue(connect);
    }

    @Override
    public void deleteUser(String username) {
        mDatabase.child("usr_" + username).removeValue();

    }

    @Override
    public void updateUser(String username, User user) {
        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        if (user.getUsername() != null) {
            mDatabase.child("usr_" + username).setValue("usr_" + user.getUsername());
            mDatabase.child("usr_" + user.getUsername()).child("username").setValue(user.getUsername());
        }
        if (user.getFullname() != null)
            mDatabase.child("usr_" + username).child("fullname").setValue(user.getFullname());
//        if(user.UUIDImage!=null)mDatabase.child("usr_"+username).child("UUIDImage").setValue(user.UUIDImage);
        if (user.getTypeAccount() != null)
            mDatabase.child("usr_" + username).child("typeAccount").setValue(user.getTypeAccount());
        if (user.getHouseName() != null)
            mDatabase.child("usr_" + username).child("housename").setValue(user.getHouseName());

    }

    @Override
    public void viewAllUser() {

    }

    @Override
    public void signOut() {

    }

    @Override
    public void insertAppInfo() {

    }

    @Override
    public void deleteAppInfo() {

    }

    @Override
    public void updateAppInfo() {

    }

    @Override
    public void viewAllAppInfo() {

    }


    private void uploadImage(final Uri imageUri, String UUIDid, final User mUser) {
        final Uri uri = imageUri;

        StorageReference ref = storageReference.child("images/" + UUIDid);
        ref.putFile(imageUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
                        String pushId = mDatabase.push().getKey();
//                        mUser.UUIDImage = taskSnapshot.getDownloadUrl().toString();
                        mDatabase.child("usr_" + mUser.getUsername()).setValue(mUser);

                        inserInviteUser(username, new Connect(""));

                        //insertConnectedUser(username, new Connect(""));

                        DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);

                        databaseAdapter.addAccount(
                                new Account(
                                        mUser.getUsername(),
                                        imageUri.toString(),
                                        mUser.getTypeAccount(),
                                        mUser.getPassword())
                        );
                        progressDialog.dismiss();
                        Intent createProfile = new Intent(mContext, MainActivity.class);
                        mContext.startActivity(createProfile);
                        ((Activity) mContext).finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                //uploadImage(uri, UUIDImage);
                Toast.makeText(mContext, "Gagal Upload Gambar", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
            }
        });
    }

    public void dataUser(final User mUser){

        mDatabase = FirebaseDatabase.getInstance().getReference().child("users");
        String pushId = mDatabase.push().getKey();
//                        mUser.UUIDImage = taskSnapshot.getDownloadUrl().toString();
        mDatabase.child("usr_" + mUser.getUsername()).setValue(mUser);

        inserInviteUser(username, new Connect(""));

        //insertConnectedUser(username, new Connect(""));

        DatabaseAdapter databaseAdapter = new DatabaseAdapter(mContext);

        databaseAdapter.addAccount(
                new Account(
                        mUser.getUsername(),
                        mUser.getTypeAccount(),
                        mUser.getPassword())
        );
        progressDialog.dismiss();
        Intent intent = new Intent(mContext, MainActivity.class);
        mContext.startActivity(intent);
        ((Activity) mContext).finish();

    }
}

interface DatabaseAction {
    interface user {
        public void insertUser(User user);

        public void deleteUser(String username);

        public void updateUser(String username, User user);

        public void viewAllUser();

        public void signOut();
    }


    interface appInfo {
        public void insertAppInfo();

        public void deleteAppInfo();

        public void updateAppInfo();

        public void viewAllAppInfo();
    }
}