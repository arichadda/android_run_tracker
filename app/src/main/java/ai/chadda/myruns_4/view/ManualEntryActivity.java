package ai.chadda.myruns_4.view;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.TimePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.AsyncTaskLoader;
import androidx.loader.content.Loader;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import ai.chadda.myruns_4.controller.BackgroundTask;
import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.view.fragments.HistoryFragment;
import ai.chadda.myruns_4.view.fragments.MyRunsDialogFragment;
import ai.chadda.myruns_4.view.fragments.StartFragment;
import ai.chadda.myruns_4.model.ManualEntryModel;

public class ManualEntryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<String> {

    // Constants
    private static final String ADD_INFO_FLAG = "add_info";
    private static final String DELETE_ROW_FLAG = "delete_row";
    public static final String INPUT_TYPE_MANUAL = "1";
    public static final String SOURCE_FLAG_MANUAL = "manual";
    public static final double KM_TO_MI_CONVERSION = 1.60934;

    // Globals
    private ArrayList<ManualEntryModel> mManualItems = new ArrayList<>();
    private Calendar cal = Calendar.getInstance();
    private ManualEntryAdapter adapter;
    public static ArrayList mManualList = new ArrayList();

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up layout
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setContentView(R.layout.activity_manual_entry);
        final ListView mListView = findViewById(R.id.manual_list_view);

        if (getIntent().getExtras().getString(SOURCE_FLAG_MANUAL).equals(StartFragment.SOURCE_FLAG_START)) {
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yy HH:mm");
            Date date = new Date();
            String toEditDate = dateFormat.format(date);
            String[] splitDateTime = toEditDate.split(" ");
            // Set up ListView items
            mManualItems.add(new ManualEntryModel("Activity", getIntent().getExtras().getString(StartFragment.ACTIVITY_TYPE_KEY)));
            mManualItems.add(new ManualEntryModel("Date", splitDateTime[0]));
            mManualItems.add(new ManualEntryModel("Time", splitDateTime[1]));
            mManualItems.add(new ManualEntryModel("Duration", "0 mins"));
            if (MyRunsDialogFragment.METRIC_UNITS_FLAG) {
                mManualItems.add(new ManualEntryModel("Distance", "0 kms"));
            } else {
                mManualItems.add(new ManualEntryModel("Distance", "0 mi"));
            }

            mManualItems.add(new ManualEntryModel("Calories", "0 cals"));
            mManualItems.add(new ManualEntryModel("Heartbeat", "0 bpm"));
            mManualItems.add(new ManualEntryModel("Comment", ""));

            // Set up adapter
            adapter = new ManualEntryAdapter(this, R.layout.adapter_view, mManualItems);
            mListView.setAdapter(adapter);

            // Set up listeners for each ListView item
            mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                    switch (position) {
                        case 0:
                            break;
                        case 1:
                            // Load calendar dialog
                            new DatePickerDialog(ManualEntryActivity.this, datePicker, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show();
                            break;
                        case 2:
                            // Load time dialog
                            TimePickerDialog timePicker = new TimePickerDialog(ManualEntryActivity.this, new TimePickerDialog.OnTimeSetListener() {
                                @Override
                                public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                                    // Set and update ListView
                                    if (minute > 9) {
                                        if (hourOfDay < 10){
                                            mManualItems.get(position).setData("0" + hourOfDay + ":" + minute);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            mManualItems.get(position).setData(hourOfDay + ":" + minute);
                                            adapter.notifyDataSetChanged();
                                        }
                                    } else {
                                        if (hourOfDay < 10) {
                                            mManualItems.get(position).setData("0" + hourOfDay + ":0" + minute);
                                            adapter.notifyDataSetChanged();
                                        } else {
                                            mManualItems.get(position).setData(hourOfDay + ":0" + minute);
                                            adapter.notifyDataSetChanged();
                                        }
                                    }

                                }
                            }, cal.get(Calendar.HOUR_OF_DAY), cal.get(Calendar.MINUTE), true);

                            timePicker.setTitle("Select Time");
                            timePicker.show();
                            break;

                        // Start number based dialog
                        case 3:
                        case 4:
                        case 5:
                        case 6:
                            startNumberDialog(position);
                            break;

                        // Start comment dialog
                        case 7:
                            DialogFragment commentDialog = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.TEXT_DIALOG);
                            getSupportFragmentManager().beginTransaction().add(commentDialog, getString(R.string.dialog_fragment_tag_comments)).commit();
                            break;

                        default:
                            break;
                    }
                }
            });


        } else if (getIntent().getExtras().getString(SOURCE_FLAG_MANUAL).equals(HistoryFragment.SOURCE_FLAG_HISTORY)){

            // Set up adapter
            adapter = new ManualEntryAdapter(this, R.layout.adapter_view, mManualItems);
            mListView.setAdapter(adapter);

            // Reformat date
            String[] dateToken = mManualList.get(1).toString().split(" ");
            String[] dateTemp = dateToken[0].split("-");
            dateTemp[0] = dateTemp[0].substring(2);
            String date = dateTemp[1] + "/" + dateTemp[2] + "/" + dateTemp[0];

            // Enter current data to each entry
            mManualItems.add(new ManualEntryModel("Activity", mManualList.get(0).toString()));
            mManualItems.add(new ManualEntryModel("Date", date));
            mManualItems.add(new ManualEntryModel("Time", dateToken[1]));
            mManualItems.add(new ManualEntryModel("Duration", mManualList.get(2).toString() + " mins"));

            // Show correct distance depending on metrics settings
            if (MyRunsDialogFragment.METRIC_UNITS_FLAG) {
                mManualItems.add(new ManualEntryModel("Distance", mManualList.get(3).toString() + " kms"));
            } else {
                int unRoundedDistance = (int)(Math.ceil(Double.parseDouble(mManualList.get(3).toString())/ KM_TO_MI_CONVERSION));
                String Distance = String.valueOf(unRoundedDistance);
                mManualItems.add(new ManualEntryModel("Distance",  Distance + " mi"));
            }

            // Cont'd enter current data to each entry
            mManualItems.add(new ManualEntryModel("Calories", mManualList.get(4).toString() + " cals"));
            mManualItems.add(new ManualEntryModel("Heartbeat", mManualList.get(5).toString() + " bpm"));
            mManualItems.add(new ManualEntryModel("Comment", mManualList.get(6).toString()));

            // Set up adapter with entries
            adapter = new ManualEntryAdapter(this, R.layout.adapter_view, mManualItems);
            mListView.setAdapter(adapter);

            // Make data uneditable
            mListView.setOnItemSelectedListener(null);

        }
    }

    // Set comment data
    public void setComment(String comment) {
        mManualItems.get(7).setData(comment);
        adapter.notifyDataSetChanged();
    }

    // Starts number based dialog
    private void startNumberDialog(int which) {
        DialogFragment numberDialog = MyRunsDialogFragment.newInstance(which);
        getSupportFragmentManager().beginTransaction().add(numberDialog, getString(R.string.dialog_fragment_tag_numbers)).commit();
    }

    // Update ListView after dialog input
    public void setNumber(String number, int type) {
            switch (type) {
                case 1:
                    mManualItems.get(type).setData(number);
                    break;
                case 3:
                    mManualItems.get(type).setData(number + " mins");
                    break;
                case 4:
                    mManualItems.get(type).setData(number + " kms");
                    break;
                case 5:
                    mManualItems.get(type).setData(number + " cals");
                    break;
                case 6:
                    mManualItems.get(type).setData(number + " bpm");
                    break;
                default:
                    break;
            }
            adapter.notifyDataSetChanged();
    }

    // Listen for user input on date and update after submission
    DatePickerDialog.OnDateSetListener datePicker = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
            cal.set(Calendar.YEAR, year);
            cal.set(Calendar.MONTH, month);
            cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yy", Locale.US);
            String date = sdf.format(cal.getTime());
            setNumber(date, 1);
            // adapter.notifyDataSetChanged();
        }
    };

    @Override
    // Load options menu depending on intent value
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        if (getIntent().getExtras().getString(SOURCE_FLAG_MANUAL).equals(StartFragment.SOURCE_FLAG_START)) {
            inflater.inflate(R.menu.save_bar, menu);
        } else if (getIntent().getExtras().getString(SOURCE_FLAG_MANUAL).equals(HistoryFragment.SOURCE_FLAG_HISTORY)) {
            inflater.inflate(R.menu.delete_bar, menu);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save_button:

                // Set up data for BackgroundTask
                String mDateTime = "";
                String mDuration = mManualItems.get(3).getData();
                String mDistance = mManualItems.get(4).getData();
                String mCalories = mManualItems.get(5).getData();
                String mHeartRate = mManualItems.get(6).getData();
                String dateTemp = mManualItems.get(1).getData();
                String[] date = dateTemp.split("/");
                mDateTime = ("20" + date[2] + "-" + date[0] + "-" + date[1] + " " + mManualItems.get(2).getData());
                mDuration = mDuration.replace(" mins", "");
                if (MyRunsDialogFragment.METRIC_UNITS_FLAG) {
                    mDistance = mDistance.replace(" kms", "");
                } else {
                    mDistance = mDistance.replace(" mi", "");
                    Double unRoundedDistance = (Double.parseDouble(mDistance) * KM_TO_MI_CONVERSION);
                    mDistance = String.valueOf(unRoundedDistance);
                }

                // Chop off units from data
                mCalories = mCalories.replace(" cals", "");
                mHeartRate = mHeartRate.replace(" bpm", "");

                // Set up and start BackgroundTask
                BackgroundTask backgroundTask = new BackgroundTask(this);
                backgroundTask.execute(ADD_INFO_FLAG, INPUT_TYPE_MANUAL, mManualItems.get(0).getData(), mDateTime,
                        mDuration, mDistance, "0", "0", mCalories, "0", mHeartRate, mManualItems.get(7).getData(), "0", "");
                finish();
                break;

            case android.R.id.home:
                onBackPressed();
                return true;

            case R.id.delete_button:
                // Delete activity from db
                BackgroundTask backgroundTaskDelete = new BackgroundTask(this);
                backgroundTaskDelete.execute(DELETE_ROW_FLAG, mManualList.get(7).toString());

                // Delete activity from local db
                mManualItems.clear();
                mManualList.clear();
                finish();

        }
        return false;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mManualItems.clear();
        mManualList.clear();
    }

    @NonNull
    @Override
    public Loader<String> onCreateLoader(int id, @Nullable Bundle args) {
        return new AsyncTaskLoader<String>(this) {
            @Nullable
            @Override
            public String loadInBackground() {
                return null;
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<String> loader, String data) {

    }

    @Override
    public void onLoaderReset(@NonNull Loader<String> loader) {

    }
}
