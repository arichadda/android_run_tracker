package ai.chadda.myruns_4.view.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;

import ai.chadda.myruns_4.controller.BackgroundTask;
import ai.chadda.myruns_4.model.HistoryFragmentModel;
import ai.chadda.myruns_4.view.HistoryFragmentAdapter;
import ai.chadda.myruns_4.view.ManualEntryActivity;
import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.view.MapActivity;

public class HistoryFragment extends Fragment implements LoaderManager.LoaderCallbacks<String> {

    // Tags
    public static final String SOURCE_FLAG_MANUAL = "manual";
    public static final String SOURCE_FLAG_HISTORY = "history";
    public static final String DATE_OF_CLICK = "date";
    public static final String POSITION_OF_CLICK = "position";
    public static final String GET_INFO_FLAG = "get_info";
    public static final String GET_DATA_FLAG = "get_data";
    public static final String GET_GPS_DATA = "get_gps";
    private static final String SOURCE_FLAG_GPS = "gps";

    // Globals
    ListView mlistView;
    TextView mActivityInputText;
    TextView mDateTimeText;
    HistoryFragmentAdapter adapter;

    @Nullable
    @Override
    // Set up layout with listview and adapter
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.history_fragment, container, false);
        mlistView = view.findViewById(R.id.history_list_view);
        adapter = new HistoryFragmentAdapter(getContext(), R.layout.history_fragment);
        mlistView.setAdapter(adapter);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Start Asynctask to get activity history from db
        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute(GET_INFO_FLAG);

        // Set listener for each item in listview
        mlistView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                mActivityInputText = view.findViewById(R.id.input_activity_type_text);
                String activityInput = mActivityInputText.getText().toString();
                String[] activity = activityInput.split(" ");

                // If activity was manual
                if (activity[0].equals("Manual:")) {
                    mDateTimeText = view.findViewById(R.id.date_time_text);
                    String dateTime = mDateTimeText.getText().toString();
                    BackgroundTask manualBackgroundTask = new BackgroundTask(getContext());
                    manualBackgroundTask.execute(GET_DATA_FLAG, dateTime);
                    Intent intent = new Intent(getActivity(), ManualEntryActivity.class);
                    intent.putExtra(DATE_OF_CLICK, dateTime);
                    intent.putExtra(POSITION_OF_CLICK, position);
                    intent.putExtra(SOURCE_FLAG_MANUAL, SOURCE_FLAG_HISTORY);
                    startActivity(intent);

                // If activity was GPS or automatic
                } else if (activity[0].equals("GPS:") || activity[0].equals("Automatic:")) {
                    mDateTimeText = view.findViewById(R.id.date_time_text);
                    String dateTime = mDateTimeText.getText().toString();
                    BackgroundTask manualBackgroundTask = new BackgroundTask(getContext());
                    manualBackgroundTask.execute(GET_GPS_DATA, dateTime);
                    Intent intent = new Intent(getActivity(), MapActivity.class);
                    intent.putExtra(SOURCE_FLAG_GPS, SOURCE_FLAG_HISTORY);
                    startActivity(intent);
                }
            }
        });
    }

    @Override
    // Update history fragment listview on return
    public void onResume() {
        BackgroundTask backgroundTask = new BackgroundTask(getContext());
        backgroundTask.execute(GET_INFO_FLAG);
        adapter.notifyDataSetChanged();
        super.onResume();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
