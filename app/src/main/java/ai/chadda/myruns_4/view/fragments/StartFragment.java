package ai.chadda.myruns_4.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.farbod.labelledspinner.LabelledSpinner;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import ai.chadda.myruns_4.view.ManualEntryActivity;
import ai.chadda.myruns_4.view.MapActivity;
import ai.chadda.myruns_4.R;

public class StartFragment extends Fragment implements LabelledSpinner.OnItemChosenListener {

    // Constants
    public static final String ACTIVITY_TYPE_KEY = "activity";
    public static final String SOURCE_FLAG_MANUAL = "manual";
    public static final String SOURCE_FLAG_GPS = "gps";
    public static final String SOURCE_FLAG_AUTOMATIC = "automatic";
    public static final String SOURCE_FLAG_START = "start";
    public static final String INPUT_SPINNER = "input";
    public static final String ACTIVITY_SPINNER = "activity";
    private static final String EMPTY = "Empty";

    // Globals
    private LabelledSpinner mInputSpinner;
    private LabelledSpinner mActivitySpinner;
    private FloatingActionButton mFloatingButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.start_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Initialize UI
        mInputSpinner = view.findViewById(R.id.start_input_spinner);
        mActivitySpinner = view.findViewById(R.id.start_activity_spinner);
        mFloatingButton = view.findViewById(R.id.start_floating_button);

        // Initialize UI data
        mInputSpinner.setItemsArray(R.array.input_array);
        mActivitySpinner.setItemsArray(R.array.activities_array);

        // Set up listeners
        mInputSpinner.setOnItemChosenListener(this);
        mFloatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Behavior for input spinner choice
                Intent intent;
                switch (mInputSpinner.getSpinner().getSelectedItemPosition()) {

                    // Manual option
                    case 0:
                        intent = new Intent(getContext(), ManualEntryActivity.class);
                        intent.putExtra(SOURCE_FLAG_MANUAL, SOURCE_FLAG_START);
                        intent.putExtra(ACTIVITY_TYPE_KEY, mActivitySpinner.getSpinner().getSelectedItem().toString());
                        startActivity(intent);
                        break;

                    // GPS
                    case 1:
                        intent = new Intent(getContext(), MapActivity.class);
                        intent.putExtra(SOURCE_FLAG_GPS, SOURCE_FLAG_START);
                        intent.putExtra(SOURCE_FLAG_AUTOMATIC, EMPTY);
                        intent.putExtra(ACTIVITY_TYPE_KEY, mActivitySpinner.getSpinner().getSelectedItem().toString());
                        startActivity(intent);
                        break;

                    // Automatic
                    case 2:
                        intent = new Intent(getContext(), MapActivity.class);
                        intent.putExtra(SOURCE_FLAG_GPS, SOURCE_FLAG_START);
                        intent.putExtra(SOURCE_FLAG_AUTOMATIC, SOURCE_FLAG_START);
                        intent.putExtra(ACTIVITY_TYPE_KEY, mActivitySpinner.getSpinner().getSelectedItem().toString());
                        startActivity(intent);
                        break;
                }
            }
        });

        // Load spinner selection state
        if (savedInstanceState != null) {
            mInputSpinner.setSelection(savedInstanceState.getInt(INPUT_SPINNER));
            mActivitySpinner.setSelection(savedInstanceState.getInt(ACTIVITY_SPINNER));
        }

    }

    @Override
    public void onItemChosen(View labelledSpinner, AdapterView<?> adapterView, View itemView, int position, long id) {
        // Behavior for input spinner selecting 'automatic'
        switch (labelledSpinner.getId()) {
            case R.id.start_input_spinner:
                if (mInputSpinner.getSpinner().getSelectedItemPosition() == 2) {
                    mActivitySpinner.getSpinner().setEnabled(false);
                } else {
                    mActivitySpinner.getSpinner().setEnabled(true);
                }
                break;
        }
    }

    // Required method for LabelledSpinner.OnItemChosenListener implementation
    @Override
    public void onNothingChosen(View labelledSpinner, AdapterView<?> adapterView) {
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        // Save spinner selections
        outState.putInt(INPUT_SPINNER, mInputSpinner.getSpinner().getSelectedItemPosition());
        outState.putInt(ACTIVITY_SPINNER, mActivitySpinner.getSpinner().getSelectedItemPosition());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }
}
