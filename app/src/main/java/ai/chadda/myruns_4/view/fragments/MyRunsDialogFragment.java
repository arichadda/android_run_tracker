package ai.chadda.myruns_4.view.fragments;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.widget.EditText;

import androidx.fragment.app.DialogFragment;

import java.util.HashMap;
import java.util.Map;

import ai.chadda.myruns_4.view.ManualEntryActivity;
import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.view.RegisterActivity;

public class MyRunsDialogFragment extends DialogFragment {

    // Constants
    public static final int TEXT_DIALOG = 0;
    public static final int PHOTO_DIALOG = 1;
    public static final int UNIT_DIALOG = 2;
    public static final int DURATION_DIALOG = 3;
    public static final int DISTANCE_DIALOG = 4;
    public static final int CALORIES_DIALOG = 5;
    public static final int HEARTBEAT_DIALOG = 6;
    public static Boolean METRIC_UNITS_FLAG = true;
    public static final int PICK_CAMERA = 0;
    public static final int PICK_GALLERY = 1;
    private static final String DIALOG_KEY = "dialog";
    private static final String UNIT_PREF = "Unit Preference";
    private static final String DIALOG_COMMENT = "Comment";
    private static final String DIALOG_OK = "OK";
    private static final String DIALOG_CANCEL = "Cancel";


    // Set up dialog with info
    public static MyRunsDialogFragment newInstance(int dialog_type) {
        MyRunsDialogFragment newFragment = new MyRunsDialogFragment();
        Bundle dialogType = new Bundle();
        dialogType.putInt(DIALOG_KEY, dialog_type);
        newFragment.setArguments(dialogType);
        return newFragment;
    }

    @Override
    // Create dialog based on dialog arguments
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        // Map for number dialog titles
        Map<Integer, String> dialogMap = new HashMap<>();
        dialogMap.put(DURATION_DIALOG, "Duration");
        dialogMap.put(DISTANCE_DIALOG, "Distance");
        dialogMap.put(CALORIES_DIALOG, "Calories");
        dialogMap.put(HEARTBEAT_DIALOG, "Heartbeat");


        final int dialog_type = getArguments().getInt(DIALOG_KEY);
        final Activity parent = getActivity();

        switch (dialog_type) {

            // Photo dialog
            case PHOTO_DIALOG:

                // Create camera/gallery dialog
                AlertDialog.Builder dialog = new AlertDialog.Builder(parent);
                dialog.setTitle(R.string.ui_profile_photo_picker_title);
                DialogInterface.OnClickListener dButtons = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        switch (which) {
                            case PICK_CAMERA:
                                ((RegisterActivity) parent).onPhotoSelection(which);
                                break;
                            case PICK_GALLERY:
                                ((RegisterActivity) parent).onPhotoSelection(which);
                                break;
                        }
                    }
                };
                dialog.setItems(R.array.ui_profile_photo_picker_items, dButtons);
                return dialog.create();

            // Settings unit dialog
            case UNIT_DIALOG:

                // Get array of units
                final String[] units = getActivity().getResources().getStringArray(R.array.unit_preference);

                // Set up dialog and RadioButtons
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                builder.setTitle(UNIT_PREF);
                builder.setSingleChoiceItems(R.array.unit_preference, SettingsFragment.selection, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        SettingsFragment.selection = which;
                        if (which == 1) {
                            METRIC_UNITS_FLAG = false;
                        } else if (which == 0) {
                            METRIC_UNITS_FLAG = true;
                        }
                        dialog.dismiss();
                    }
                });

                // Set up cancel button
                builder.setNegativeButton(DIALOG_CANCEL, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });

                return builder.create();

            // Text dialog
            case TEXT_DIALOG:

                // Set up text dialog
                AlertDialog.Builder commentDialog = new AlertDialog.Builder(parent);
                commentDialog.setTitle(DIALOG_COMMENT);
                final EditText comment = new EditText(getContext());
                comment.setInputType(InputType.TYPE_CLASS_TEXT);
                commentDialog.setView(comment);
                commentDialog.setPositiveButton(DIALOG_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update ListView comment to dialog user input
                        ((ManualEntryActivity) parent).setComment(comment.getText().toString());
                    }
                });

                return commentDialog.create();

            // Number dialog
            case DURATION_DIALOG:
            case DISTANCE_DIALOG:
            case CALORIES_DIALOG:
            case HEARTBEAT_DIALOG:

                // Set up dialog
                AlertDialog.Builder textDialog = new AlertDialog.Builder(parent);
                textDialog.setTitle(dialogMap.get(dialog_type));
                final EditText text = new EditText(getContext());
                text.setInputType(InputType.TYPE_CLASS_NUMBER);
                textDialog.setView(text);
                textDialog.setPositiveButton(DIALOG_OK, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Update appropriate ListView item
                        if (!text.getText().toString().equals("")) {
                            ((ManualEntryActivity) parent).setNumber(text.getText().toString(), dialog_type);
                        }
                    }
                });

                return textDialog.create();

            default:
                return null;
        }
    }
}
