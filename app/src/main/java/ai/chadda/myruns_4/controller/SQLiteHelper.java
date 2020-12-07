package ai.chadda.myruns_4.controller;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import ai.chadda.myruns_4.model.DBSchema;


public class SQLiteHelper extends SQLiteOpenHelper {

    // db variables
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "activity_history.db";
    private static final String CREATION_QUERY = "create table "
            + DBSchema.ExerciseEntry.TABLE_NAME + "("
            + DBSchema.ExerciseEntry.ID + " integer primary key autoincrement,"
            + DBSchema.ExerciseEntry.INPUT_TYPE + " integer not null,"
            + DBSchema.ExerciseEntry.ACTIVITY_TYPE + " integer not null,"
            + DBSchema.ExerciseEntry.DATE_TIME + " text not null,"
            + DBSchema.ExerciseEntry.DURATION + " integer not null,"
            + DBSchema.ExerciseEntry.DISTANCE + " float,"
            + DBSchema.ExerciseEntry.AVERAGE_PACE + " float,"
            + DBSchema.ExerciseEntry.AVERAGE_SPEED + " float,"
            + DBSchema.ExerciseEntry.CALORIES + " integer,"
            + DBSchema.ExerciseEntry.CLIMB + " float,"
            + DBSchema.ExerciseEntry.HEARTRATE + " integer,"
            + DBSchema.ExerciseEntry.COMMENT + " text,"
            + DBSchema.ExerciseEntry.PRIVACY + " integer,"
            + DBSchema.ExerciseEntry.GPS_DATA + " text" + ")";


    public SQLiteHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATION_QUERY);
    }

    // Add row to df with given parameters
    public void addInfo(SQLiteDatabase db, int mInputType, String mActivityType, String mDateTime,
                        int mDuration, double mDistance, double mAvgPace, double mAvgSpeed, int mCalorie,
                        double mClimb, int mHeartRate, String mComment, int mPrivacy, String mGPS) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(DBSchema.ExerciseEntry.INPUT_TYPE, mInputType);
        contentValues.put(DBSchema.ExerciseEntry.ACTIVITY_TYPE, mActivityType);
        contentValues.put(DBSchema.ExerciseEntry.DATE_TIME, mDateTime);
        contentValues.put(DBSchema.ExerciseEntry.DURATION, mDuration);
        contentValues.put(DBSchema.ExerciseEntry.DISTANCE, mDistance);
        contentValues.put(DBSchema.ExerciseEntry.AVERAGE_PACE, mAvgPace);
        contentValues.put(DBSchema.ExerciseEntry.AVERAGE_SPEED, mAvgSpeed);
        contentValues.put(DBSchema.ExerciseEntry.CALORIES, mCalorie);
        contentValues.put(DBSchema.ExerciseEntry.CLIMB, mClimb);
        contentValues.put(DBSchema.ExerciseEntry.HEARTRATE, mHeartRate);
        contentValues.put(DBSchema.ExerciseEntry.COMMENT, mComment);
        contentValues.put(DBSchema.ExerciseEntry.PRIVACY, mPrivacy);
        contentValues.put(DBSchema.ExerciseEntry.GPS_DATA, mGPS);
        db.insert(DBSchema.ExerciseEntry.TABLE_NAME, null,contentValues);
    }

    public Cursor getInfo(SQLiteDatabase db) {

//        String[] columns = {DBSchema.ExerciseEntry.ID, DBSchema.ExerciseEntry.INPUT_TYPE, DBSchema.ExerciseEntry.ACTIVITY_TYPE,
//                DBSchema.ExerciseEntry.DATE_TIME, DBSchema.ExerciseEntry.DURATION, DBSchema.ExerciseEntry.DISTANCE,
//                DBSchema.ExerciseEntry.AVERAGE_PACE, DBSchema.ExerciseEntry.AVERAGE_SPEED, DBSchema.ExerciseEntry.CALORIES, DBSchema.ExerciseEntry.CLIMB,
//                DBSchema.ExerciseEntry.HEARTRATE, DBSchema.ExerciseEntry.COMMENT, DBSchema.ExerciseEntry.PRIVACY, DBSchema.ExerciseEntry.GPS_DATA};

        Cursor cursor = db.query(DBSchema.ExerciseEntry.TABLE_NAME, null, null, null, null, null, null);
        return cursor;
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) { }
}
