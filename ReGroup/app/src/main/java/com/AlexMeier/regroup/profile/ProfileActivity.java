package com.AlexMeier.regroup.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.FileProvider;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.AlexMeier.regroup.R;
import com.bumptech.glide.Glide;
import com.bumptech.glide.signature.ObjectKey;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.io.File;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "PROFILE_ACTIVITY";
    private static final int RESULT_PICK_IMAGE = 10;
    private static final int RESULT_OPEN_CAMERA = 11;
    private ProfileData currentProfile;
    private FirebaseAuth mAuth; //user information
    private String userName;
    private String userProfileBody;
    private Uri profilePictureUri;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get user information
        mAuth = FirebaseAuth.getInstance();
        userName = mAuth.getCurrentUser().getDisplayName();

        //attempt to retrieve profile data
        ProfileUtil.getProfile(mAuth.getCurrentUser().getUid(), profileData -> {
            //if this is a new profile, set it accordingly
            if(profileData.getUserName() != null){
                userName = profileData.getUserName();
            }
            userProfileBody = profileData.getProfileBody();
            final TextView profileNameTextView = findViewById(R.id.user_name);
            profileNameTextView.setText(userName);
            final TextView profileBodyTextView = findViewById(R.id.profile_body);
            profileBodyTextView.setText(userProfileBody);

            //if it exists show profile picture
            StorageReference imageReference = profileData.getImageReference();

            if(imageReference != null){
                Glide.with(this)
                        .load(imageReference)
                        .signature(new ObjectKey(profileData))
                        .into((ImageView)findViewById(R.id.profile_picture));
            }

            currentProfile = profileData;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.user_profile_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch(item.getItemId()){
            case R.id.edit_profile:
                editProfileText();
                return true;
            case R.id.change_picture:
                editProfilePicture();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * prompts the user to update profile picture
     */
    private void editProfilePicture() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final CharSequence[] dialogOptions = {"Take Photo", "Choose From Gallery"};
        dialogBuilder.setTitle("Upload A Picture");
        Context context = this;

        //let user choose between camera and picking from the gallery
        dialogBuilder.setItems(dialogOptions, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(dialogOptions[which].equals("Choose From Gallery")){
                    Intent openGallery = new Intent(Intent.ACTION_PICK,
                            MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                    startActivityForResult(openGallery, RESULT_PICK_IMAGE);
                }else if(dialogOptions[which].equals("Take Photo")){
                    try {
                        File photo = new File(getExternalMediaDirs()[0], "photo.jpg");
                        if (photo.exists() == false) {
                            photo.getParentFile().mkdirs();
                            photo.createNewFile();

                        } else {
                            photo.delete();
                            photo.createNewFile();
                        }
                        Intent openCamera = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        openCamera.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context, getApplicationContext().getPackageName() + ".provider", photo));
                        startActivityForResult(openCamera, RESULT_OPEN_CAMERA);
                    }catch(Exception e){
                        Log.e(TAG, "Failed to open image file" + e);
                        Toast.makeText(context, "Open Camera Failed", Toast.LENGTH_LONG).show();
                    }

                }
            }
        });
        dialogBuilder.show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == RESULT_PICK_IMAGE){
            updateProfilePicture(data.getData());
        } else if(resultCode == RESULT_OK && requestCode == RESULT_OPEN_CAMERA){
            updateProfilePicture(Uri.fromFile( new File(getExternalMediaDirs()[0], "photo.jpg")));
        }
    }

    /**
     * Updates profile picture from image uri
     * @param imageUri
     */
    private void updateProfilePicture(Uri imageUri) {
        //update local reference
        profilePictureUri = imageUri;
        ImageView profilePicture = findViewById(R.id.profile_picture);
        profilePicture.setImageURI(imageUri);

        //update server version
        ProfileUtil.updateCurrentUserProfilePicture(imageUri, new ProfileData(userName, userProfileBody));
    }

    /**
     * This function is called when the user presses the edit profile menu option
     * */
    private void editProfileText() {
        //inflate textbox
        View editorView = LayoutInflater.from(this).inflate(R.layout.profile_editor_dialog, null);

        //create popup
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        final EditText profileEditor = editorView.findViewById(R.id.editor_text_box);
        profileEditor.setText(userProfileBody);
        profileEditor.setInputType(InputType.TYPE_TEXT_FLAG_MULTI_LINE); //multiline text
        dialogBuilder.setView(editorView);

        dialogBuilder.setPositiveButton("Done", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                updateProfileText(profileEditor.getText().toString());
            }
        });

        dialogBuilder.show();
    }

    /**
     * Updates both the onscreen body text as well as updates user data in firestore
     * @param text - profile body text
     */
    private void updateProfileText(String text) {
        userProfileBody = text;
        //update local body text
        final TextView bodyText = findViewById(R.id.profile_body);
        bodyText.setText(userProfileBody);

        //update firebase
        currentProfile.setProfileBody(text);
        ProfileUtil.updateCurrentUserProfile(currentProfile);
    }
}