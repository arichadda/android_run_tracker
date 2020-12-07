package ai.chadda.myruns_4.controller;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.ListView;

import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.model.DBSchema;
import ai.chadda.myruns_4.model.HistoryFragmentModel;
import ai.chadda.myruns_4.view.HistoryFragmentAdapter;
import ai.chadda.myruns_4.view.ManualEntryActivity;
import ai.chadda.myruns_4.view.MapActivity;

public class BackgroundTask extends AsyncTask<String, HistoryFragmentModel, String> {

    // Constants
    private static final String ADD_INFO_FLAG = "add_info";
    private static final String GET_INFO_FLAG = "get_info";
    private static final String GET_DATA_FLAG = "get_data";
    private static final String DELETE_ROW_FLAG = "delete_row";
    private static final String ACTIVITY_SAVED_FLAG = "saved activity";
    private static final String INFO_GOTTEN_FLAG = "got info";
    private static final String DATA_GOTTEN_FLAG = "got data";
    private static final String ROW_DELETED_FLAG = "row deleted";
    private static final String GET_GPS_DATA = "get_gps";
    private static final String GPS_GOTTEN_FLAG = "got gps";

    // Page variables
    private Context context;
    private HistoryFragmentAdapter historyFragmentAdapter;
    private Activity activity;
    private ListView listView;

    // db variables
    private int mId;
    private int mInputType = 0;
    private String mActivityType = "";
    private String mDateTime = "";
    private int mDuration = 0;
    private double mDistance = 0.0;
    private double mAveragePace = 0.0;
    private double mAverageSpeed = 0.0;
    private int mCalories = 0;
    private double mClimb = 0.0;
    private int mHeartRate = 0;
    private String mComment = "";
    private int mPrivacy = 0;
    private String mGPS = "";

    public BackgroundTask(Context context) {
        this.context = context;
        activity = (Activity) context;
    }

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
    }

    @Override
    protected String doInBackground(String... params) {

        // Get method
        String method = params[0];

        // Set up helper
        SQLiteHelper sqLiteHelper = new SQLiteHelper(context);

        switch (method) {
            case ADD_INFO_FLAG: {
                // Arrange variables
                mInputType = Integer.parseInt(params[1]);
                mActivityType = params[2];
                mDateTime = params[3];
                mDuration = Integer.parseInt(params[4]);
                mDistance = Double.parseDouble(params[5]);
                mAveragePace = Double.parseDouble(params[6]);
                mAverageSpeed = Double.parseDouble(params[7]);
                mCalories = Integer.parseInt(params[8]);
                mClimb = Double.parseDouble(params[9]);
                mHeartRate = Integer.parseInt(params[10]);
                mComment = params[11];
                mPrivacy = Integer.parseInt(params[12]);
                mGPS = params[13];

                // Enter variables into db
                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();
                sqLiteHelper.addInfo(db, mInputType, mActivityType, mDateTime, mDuration, mDistance, mAveragePace,
                        mAverageSpeed, mCalories, mClimb, mHeartRate, mComment, mPrivacy, mGPS);

                return ACTIVITY_SAVED_FLAG;

            }
            case GET_INFO_FLAG: {
                // Get db
                SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
                Cursor cursor = sqLiteHelper.getInfo(db);

                // Set up history fragment
                historyFragmentAdapter = new HistoryFragmentAdapter(context, R.layout.history_fragment);
                listView = activity.findViewById(R.id.history_list_view);

                while (cursor.moveToNext()) {

                    // Get info from db
                    mId = cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.ID));
                    mInputType = cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.INPUT_TYPE));
                    mActivityType = cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.ACTIVITY_TYPE));
                    mDateTime = cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.DATE_TIME));
                    mDuration = cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.DURATION));
                    mDistance = cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.DISTANCE));
                    mAveragePace = cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.AVERAGE_PACE));
                    mAverageSpeed = cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.AVERAGE_SPEED));
                    mCalories = cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.CALORIES));
                    mClimb = cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.CLIMB));
                    mHeartRate = cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.HEARTRATE));
                    mComment = cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.COMMENT));
                    mPrivacy = cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.PRIVACY));
                    mGPS = cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.GPS_DATA));

                    // Enter data into fragment
                    HistoryFragmentModel historyFragmentModel = new HistoryFragmentModel(mId, mInputType, mActivityType, mDateTime,
                            mDuration, mDistance, mAveragePace, mAverageSpeed, mCalories, mClimb, mHeartRate, mComment, mPrivacy, mGPS);

                    // Send data
                    publishProgress(historyFragmentModel);
                }
                return INFO_GOTTEN_FLAG;
            }
            case GET_DATA_FLAG: {
                // Get db
                SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
                Cursor cursor = db.query(DBSchema.ExerciseEntry.TABLE_NAME, null, DBSchema.ExerciseEntry.DATE_TIME + "=?", new String[]{params[1]}, null, null, null);

                // Put data into ManualEntryActivity
                while (cursor.moveToNext()) {
                    ManualEntryActivity.mManualList.add(cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.ACTIVITY_TYPE)));
                    ManualEntryActivity.mManualList.add(cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.DATE_TIME)));
                    ManualEntryActivity.mManualList.add(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.DURATION))));
                    ManualEntryActivity.mManualList.add(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.DISTANCE))));
                    ManualEntryActivity.mManualList.add(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.CALORIES))));
                    ManualEntryActivity.mManualList.add(Integer.toString(cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.HEARTRATE))));
                    ManualEntryActivity.mManualList.add(cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.COMMENT)));
                    ManualEntryActivity.mManualList.add(Integer.toString((cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.ID)))));
                }

                return DATA_GOTTEN_FLAG;
            }
            case DELETE_ROW_FLAG: {

                // Get db
                SQLiteDatabase db = sqLiteHelper.getWritableDatabase();

                // Delete row
                db.delete(DBSchema.ExerciseEntry.TABLE_NAME, DBSchema.ExerciseEntry.ID + "=?", new String[]{params[1]});

                return ROW_DELETED_FLAG;
            }
            // Gets map data
            case GET_GPS_DATA: {
                SQLiteDatabase db = sqLiteHelper.getReadableDatabase();
                Cursor cursor = db.query(DBSchema.ExerciseEntry.TABLE_NAME, null, DBSchema.ExerciseEntry.DATE_TIME + "=?", new String[]{params[1]}, null, null, null);
                while (cursor.moveToNext()) {
                    MapActivity.mMapList.add(cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.ACTIVITY_TYPE)));
                    MapActivity.mMapList.add(Double.toString(cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.AVERAGE_PACE))));
                    MapActivity.mMapList.add(Double.toString(cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.AVERAGE_SPEED))));
                    MapActivity.mMapList.add(Double.toString(cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.CLIMB))));
                    MapActivity.mMapList.add(Double.toString(cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.CALORIES))));
                    MapActivity.mMapList.add(Double.toString(cursor.getDouble(cursor.getColumnIndex(DBSchema.ExerciseEntry.DISTANCE))));
                    MapActivity.mMapList.add(cursor.getString(cursor.getColumnIndex(DBSchema.ExerciseEntry.GPS_DATA)));
                    MapActivity.mMapList.add(Integer.toString((cursor.getInt(cursor.getColumnIndex(DBSchema.ExerciseEntry.ID)))));

                    return GPS_GOTTEN_FLAG;
                }

            }
        }
        return null;
    }

    @Override
    // When progress gets published, add data to historyFragmentAdapter
    protected void onProgressUpdate(HistoryFragmentModel... values) {
            historyFragmentAdapter.add(values[0]);
    }

    @Override

    protected void onPostExecute(String result) {
        // If getting info, set up listview adapter
        if (result.equals(INFO_GOTTEN_FLAG)) listView.setAdapter(historyFragmentAdapter);
    }
}
