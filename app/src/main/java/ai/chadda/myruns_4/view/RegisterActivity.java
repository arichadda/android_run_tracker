package ai.chadda.myruns_4.view;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.fragment.app.DialogFragment;

import com.soundcloud.android.crop.Crop;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import ai.chadda.myruns_4.BuildConfig;
import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.view.fragments.MyRunsDialogFragment;

public class RegisterActivity extends AppCompatActivity {

    // Constants
    public static final String SHARED_PREF = "share";
    public static final String EXTRA_KEY_EMAIL = "email";
    public static final String EXTRA_KEY_PASSWORD = "password";
    public static final String EXTRA_KEY_NAME = "name";
    public static final String EXTRA_KEY_PHONE = "phone";
    public static final String EXTRA_KEY_MAJOR = "major";
    public static final String EXTRA_KEY_CLASS = "class";
    public static final String EXTRA_KEY_GENDER = "gender";
    public static final String GENDER_MALE = "male";
    public static final String GENDER_FEMALE = "female";
    public static final String GENDER_NONE = "";
    public static final String EXTRA_KEY_URI = "uri";
    private static final String URI_INSTANCE_STATE_KEY = "saved_uri";
    public static final int REQUEST_CODE_TAKE_FROM_CAMERA = 0;
    public static final int REQUEST_CODE_TAKE_FROM_GALLERY = 1;
    public static final int REQUEST_CODE_PERMISSIONS = 2;
    public final static String PROFILE_ORIGIN_KEY = "origin";
    public static final String TITLE_PROFILE = "Profile";

    // Global variables
    private EditText mNameEText, mEmailEText, mPasswordEText, mPhoneEText, mMajorEText, mDClassEText;
    private String mNameValue, mEmailValue, mPasswordValue, mMajorValue, mGenderValue, mPhoneValue, mDClassValue;
    private RadioButton mMaleRadioButton, mFemaleRadioButton;
    private RadioGroup mGenderButtons;
    private Button mPhotoButton;
    private Uri mImageCaptureUri;
    private String mUriValue;
    private ImageView mImageView;
    private Bitmap mBitmap;
    private File photoFile = null;
    private File storageDir;
    private String mPasswordTemp = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Create layout
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_register);

        // Initialize EditTexts
        mImageView = findViewById(R.id.imageProfile);
        mNameEText = findViewById(R.id.rname_etext);
        mEmailEText = findViewById(R.id.remail_etext);
        mPasswordEText = findViewById(R.id.rpassword_etext);
        mPhoneEText = findViewById(R.id.rphone_etext);
        mMajorEText = findViewById(R.id.rmajor_etext);
        mDClassEText = findViewById(R.id.rdclass_etext);

        // Initialize RadioButtons
        mMaleRadioButton = findViewById(R.id.rmale_rbutton);
        mFemaleRadioButton = findViewById(R.id.rfemale_rbutton);
        mGenderButtons = findViewById(R.id.rgender_buttons);
        mGenderButtons.clearCheck();
        mPhotoButton = findViewById(R.id.btnChangePhoto);

        // Request camera and storage permissions
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
        }

        // Set up photo button listener
        mPhotoButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment photoFrag = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.PHOTO_DIALOG);
                getSupportFragmentManager().beginTransaction().add(photoFrag, getString(R.string.dialog_fragment_tag_photo_picker)).commit();
            }
        });

        // Fill in profile fields if intent is from "edit profile"
        if (getIntent().getExtras().getString(PROFILE_ORIGIN_KEY).equals(MainActivity.PROFILE_ORIGIN_VALUE)) {
            setFields();
        }

        // If there is no saved photo, load default photo
        if (mImageCaptureUri == null) {
            mImageView.setImageResource(R.drawable.default_profile);
        }
    }

    // Fills in profile fields if intent is from "edit profile"
    private void setFields() {

        // Set toolbar title
        setTitle(TITLE_PROFILE);

        // Makes email uneditable
        mEmailEText.setFocusable(false);

        // Load and initialize SharedPreferences (Profile information)
        SharedPreferences userPref = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        mNameValue = userPref.getString(EXTRA_KEY_NAME, null);
        mEmailValue = userPref.getString(EXTRA_KEY_EMAIL, null);
        mPasswordValue = userPref.getString(EXTRA_KEY_PASSWORD, null);
        mGenderValue = userPref.getString(EXTRA_KEY_GENDER, null);
        mPhoneValue = userPref.getString(EXTRA_KEY_PHONE, null);
        mMajorValue = userPref.getString(EXTRA_KEY_MAJOR, null);
        mDClassValue = userPref.getString(EXTRA_KEY_CLASS, null);
        mUriValue = userPref.getString(EXTRA_KEY_URI, null);
        mPasswordTemp = userPref.getString(EXTRA_KEY_PASSWORD, null);
        mNameEText.setText(mNameValue, TextView.BufferType.EDITABLE);
        mEmailEText.setText(mEmailValue, TextView.BufferType.EDITABLE);
        mPasswordEText.setText(mPasswordValue, TextView.BufferType.EDITABLE);
        mPhoneEText.setText(mPhoneValue, TextView.BufferType.EDITABLE);
        mMajorEText.setText(mMajorValue, TextView.BufferType.EDITABLE);
        mDClassEText.setText(mDClassValue, TextView.BufferType.EDITABLE);

        // Set gender RadioButtons accordingly
        if (mGenderValue.equalsIgnoreCase("male")) {
            mMaleRadioButton.setChecked(true);
        } else if (mGenderValue.equalsIgnoreCase("Female")) {
            mFemaleRadioButton.setChecked(true);
        }

        // Load profile image
        if (mUriValue != null) {
            mImageCaptureUri = Uri.parse(mUriValue);
        } else {
            mImageView.setImageResource(R.drawable.default_profile);
        }
        try {
            if (mImageCaptureUri != null) {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                mImageView.setImageBitmap(mBitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    // Behavior for when either permission request (for camera and storage) is denied
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        // Check if any permission is denied
        if ((grantResults[0] == PackageManager.PERMISSION_DENIED || grantResults[1] == PackageManager.PERMISSION_DENIED) && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE) || shouldShowRequestPermissionRationale(Manifest.permission.CAMERA))
            {
                // Create dialog to insist permissions
                AlertDialog.Builder mAlert = new AlertDialog.Builder(this);
                mAlert.setMessage("This functionality is important for app use").setTitle("Permissions required:");

                // Set up dialog buttons
                mAlert.setPositiveButton("Grant permission", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which)
                    {
                        requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE_PERMISSIONS);
                    }
                });
                mAlert.setNegativeButton("Continue use without photos", new DialogInterface.OnClickListener()
                {
                    @Override
                    public void onClick(DialogInterface dialog, int which) { }
                });

                mAlert.show();
            }
        }
    }

    // Returns from MyRunsDialogFragment
    // Goes to camera or gallery depending on user input
    public void onPhotoSelection(int type) {
        Intent intent;
        switch (type) {
            case MyRunsDialogFragment.PICK_CAMERA:
                takePicture();
                break;
            case MyRunsDialogFragment.PICK_GALLERY:
                intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_CODE_TAKE_FROM_GALLERY);
                break;
        }
    }

    // Creates photo filepath and sets up intent to native camera app
    private void takePicture() {
        photoFile = createPhotoFile();

        // Set up intent
        Intent imageIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

        // Create URI and start native camera app
        if (photoFile != null) {
            Uri photoUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
            imageIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
            startActivityForResult(imageIntent, REQUEST_CODE_TAKE_FROM_CAMERA);
        }
    }

    // Creates unique photo filepath
    private File createPhotoFile() {
        String name = new SimpleDateFormat("yyyy_MMdd_HHmmss").format(new Date());
        storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        return new File(storageDir, "JPEG_" + name + ".jpg");
    }

    @Override
    // Handles behavior for returning from image activities
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case (REQUEST_CODE_TAKE_FROM_CAMERA):
                    // Create bitmap for photo
                    mBitmap = imageOrientationValidator(photoFile);

                    // Prepare crop activity with origin URI
                    mImageCaptureUri = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", photoFile);
                    if (photoFile != null) {
                        beginCrop(mImageCaptureUri);
                    }
                    break;

                // After preparing crop, start crop activity
                case (Crop.REQUEST_CROP):
                    try {
                        handleCrop(resultCode, data);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;

                case REQUEST_CODE_TAKE_FROM_GALLERY:
                    // Prepare crop activity with origin URI
                    mImageCaptureUri = data.getData();
                    beginCrop(mImageCaptureUri);
                    break;
            }

        }
    }

    private Bitmap imageOrientationValidator(File file) {
        // Set up image interface
        ExifInterface ei;

        // Initialize return value
        Bitmap res = null;

        try {
            // Get bitmap
            Bitmap tempBitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());

            // Get orientation of photo
            ei = new ExifInterface(file.getAbsolutePath());
            int orientation = ei.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED);

            // Rotate image accordingly
            res = rotateImage(tempBitmap, orientation);

        } catch (IOException e) {
            e.printStackTrace();
        }

        return res;
    }

    private Bitmap rotateImage(Bitmap source, int orientation) {
        // Initialize matrix for rotation
        Matrix matrix = new Matrix();

        // Rotate accordingly
        switch (orientation) {
            case ExifInterface.ORIENTATION_ROTATE_90:
                matrix.postRotate(90);
                break;
            case ExifInterface.ORIENTATION_ROTATE_180:
                matrix.postRotate(180);
                break;
            case ExifInterface.ORIENTATION_ROTATE_270:
                matrix.postRotate(270);
                break;
            case ExifInterface.ORIENTATION_NORMAL:
            default:
                break;
        }

        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    // Get source and create destination for cropped image
    // Then start crop process
    private void beginCrop(Uri source) {
        Uri fileLocation = FileProvider.getUriForFile(this, BuildConfig.APPLICATION_ID + ".provider", createPhotoFile());
        mImageCaptureUri = fileLocation;
        Crop.of(source, fileLocation).asSquare().start(this);
    }

    // Set profile image to cropped result
    private void handleCrop(int resultCode, Intent result) throws IOException {
        if (resultCode == RESULT_OK) {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), Crop.getOutput(result));
            mImageView.setImageBitmap(bitmap);
        }
    }

    // Called after hitting register
    // Checks that data is complete
    // Save data to shared preferences
    private void register() {
        // Get data values
        mNameValue = RegisterActivity.this.mNameEText.getText().toString();
        mEmailValue = RegisterActivity.this.mEmailEText.getText().toString();
        mPasswordValue = RegisterActivity.this.mPasswordEText.getText().toString();
        mPhoneValue = RegisterActivity.this.mPhoneEText.getText().toString();
        mMajorValue = RegisterActivity.this.mMajorEText.getText().toString();
        mDClassValue = RegisterActivity.this.mDClassEText.getText().toString();
        if (mMaleRadioButton.isChecked()) {
            mGenderValue = GENDER_MALE;
        } else if (mFemaleRadioButton.isChecked()) {
            mGenderValue = GENDER_FEMALE;
        } else {
            mGenderValue = GENDER_NONE;
        }

        // Check if data is complete
        if (!isEmailValid(mEmailValue)) mEmailEText.setError("Please use a valid email");
        else mEmailEText.setError(null);
        if (!isPasswordValid(mPasswordValue))
            mPasswordEText.setError("Password must be greater than 5 characters");
        else mPasswordEText.setError(null);
        if (!isPhoneValid(mPhoneValue))
            mPhoneEText.setError("Phone number must be at least 10 characters");
        else mPhoneEText.setError(null);
        if (!isClassValid(mDClassValue)) mDClassEText.setError("Please use 4 characters");
        else mDClassEText.setError(null);
        if (mNameValue.equals("")) mNameEText.setError("Please enter a name");
        else mNameEText.setError(null);
        if (mMajorValue.equals("")) mMajorEText.setError("Please enter a major");
        else mMajorEText.setError(null);
        if (mGenderValue.equals(""))
            Toast.makeText(RegisterActivity.this, "Please enter a gender", Toast.LENGTH_SHORT).show();
        if (isEmailValid(mEmailValue) && isPasswordValid(mPasswordValue) && isPhoneValid(mPhoneValue) && isClassValid(mDClassValue) && !mEmailValue.equals("") && !mMajorValue.equals("") && !mGenderValue.equals("")) {
            // Call method to save data to preferences
            saveData();
        }
    }

    // Method to check email validity
    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    // Method to check password validity
    private boolean isPasswordValid(String password) {
        return password.length() > 5;
    }

    // Method to check phone number validity
    private boolean isPhoneValid(String number) {
        return number.length() >= 10;
    }

    // Method to check class year validity
    private boolean isClassValid(String grad) {
        return grad.length() == 4;
    }

    // Save data to SharedPreferences
    public void saveData() {
        // Convert URI to string
        if (mImageCaptureUri != null) {
            mUriValue = mImageCaptureUri.toString();
        }

        // Initialize SharedPreferences editor
        SharedPreferences userPref = getSharedPreferences(SHARED_PREF, MODE_PRIVATE);
        SharedPreferences.Editor editor = userPref.edit();

        // Enter values
        editor.putString(EXTRA_KEY_NAME, mNameValue);
        editor.putString(EXTRA_KEY_EMAIL, mEmailValue);
        editor.putString(EXTRA_KEY_PASSWORD, mPasswordValue);
        editor.putString(EXTRA_KEY_GENDER, mGenderValue);
        editor.putString(EXTRA_KEY_PHONE, mPhoneValue);
        editor.putString(EXTRA_KEY_MAJOR, mMajorValue);
        editor.putString(EXTRA_KEY_CLASS, mDClassValue);
        editor.putString(EXTRA_KEY_URI, mUriValue);

        editor.apply();
    }


    @Override
    // Saves URI value
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(URI_INSTANCE_STATE_KEY, mImageCaptureUri);
    }

    @Override
    // Loads URI value
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        mImageCaptureUri = savedInstanceState.getParcelable(URI_INSTANCE_STATE_KEY);
        try {
            if (mImageCaptureUri != null) {
                mBitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), mImageCaptureUri);
                mImageView.setImageBitmap(mBitmap);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    // Load options menu depending on intent value
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if ((getIntent().getExtras().getString(PROFILE_ORIGIN_KEY).equals(LoginActivity.PROFILE_ORIGIN_VALUE))) {
            inflater.inflate(R.menu.register_bar, menu);
        } else if ((getIntent().getExtras().getString(PROFILE_ORIGIN_KEY).equals(MainActivity.PROFILE_ORIGIN_VALUE))) {
                inflater.inflate(R.menu.profile_bar, menu);
        }
        return true;
    }

    @Override
    // Behavior for "register", "save", and "back" button
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
            // "Register" button
            case R.id.register_button:
                register();

                // Check if data entries are full
                if (IsSubmissionError()) {
                    // Go back to LoginActivity
                    intent = new Intent(RegisterActivity.this, LoginActivity.class);
                    setResult(RESULT_OK, intent);
                    startActivity(intent);
                } else {
                    Toast.makeText(RegisterActivity.this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                }

                return true;

            // "Back" button
            // If on LoginActivity, returning from RegisterActivity, navigation bar "back" button is disabled
            case android.R.id.home:
                onBackPressed();
                return true;

            // "Save" button
            case R.id.profile_button:
                // If password is changed, go to LoginActivity
                if (!mPasswordTemp.equals(RegisterActivity.this.mPasswordEText.getText().toString())) {
                    register();

                    // Check if data entries are full
                    if (IsSubmissionError()) {
                        intent = new Intent(RegisterActivity.this, LoginActivity.class);
                        setResult(RESULT_OK, intent);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                    }
                    return true;

                // Otherwise, go to MainActivity
                } else {
                    register();

                    // Check if data entries are full
                    if (IsSubmissionError()) {
                        intent = new Intent(RegisterActivity.this, MainActivity.class);
                        setResult(RESULT_OK, intent);
                        startActivity(intent);
                    } else {
                        Toast.makeText(RegisterActivity.this, "Please fill all fields correctly", Toast.LENGTH_SHORT).show();
                    }
                    return true;
                }

        }
        return false;
    }

    // Check if all fields are filled
    private boolean IsSubmissionError() {
        if (mNameEText.getError() == null && mEmailEText.getError() == null && mPasswordEText.getError() == null && mPhoneEText.getError() == null && mMajorEText.getError() == null && mDClassEText.getError() == null && mGenderButtons.getCheckedRadioButtonId() != -1) {
            return true;
        } else {
            return false;
        }
    }

}