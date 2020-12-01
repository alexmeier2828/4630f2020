package com.AlexMeier.regroup.profile;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.AlexMeier.regroup.R;
import com.google.firebase.auth.FirebaseAuth;

import org.w3c.dom.Text;

public class ProfileActivity extends AppCompatActivity {
    private static final String TAG = "PROFILE_ACTIVITY";
    private static final int RESULT_PICK_IMAGE = 10;
    private FirebaseAuth mAuth; //user information
    private String userName;
    private String userProfileBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //get user information
        mAuth = FirebaseAuth.getInstance();
        userName = mAuth.getCurrentUser().getDisplayName();

        //attempt to retrieve profile data
        ProfileUtil.getProfile(mAuth.getCurrentUser(), profileData -> {
            //if this is a new profile, set it accordingly
            if(profileData.getUserName() != null){
                userName = profileData.getUserName();
            }
            userProfileBody = profileData.getProfileBody();
            final TextView profileNameTextView = findViewById(R.id.profile_name);
            profileNameTextView.setText(userName);
            final TextView profileBodyTextView = findViewById(R.id.profile_body_text);
            profileBodyTextView.setText(userProfileBody);
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
        Intent openGallery = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(openGallery, RESULT_PICK_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK && requestCode == RESULT_PICK_IMAGE){
            updateProfilePicture(data.getData());
        }
    }

    /**
     * Updates profile picture from image uri
     * @param imageURI
     */
    private void updateProfilePicture(Uri imageURI) {

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
        final TextView bodyText = findViewById(R.id.profile_body_text);
        bodyText.setText(userProfileBody);

        //update firebase
        ProfileUtil.updateCurrentUserProfile(new ProfileData(userName, userProfileBody));
    }
}