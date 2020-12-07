package ai.chadda.myruns_4.model;

public final class DBSchema {

    public static abstract class ExerciseEntry {

        // db variables
        public static final String TABLE_NAME = "manual_activity_entry";
        public static final String ID = "id";
        public static final String INPUT_TYPE = "input_type";
        public static final String ACTIVITY_TYPE = "activity_type";
        public static final String DATE_TIME = "date";
        public static final String DURATION = "duration";
        public static final String DISTANCE = "distance";
        public static final String AVERAGE_PACE = "average_pace";
        public static final String AVERAGE_SPEED = "average_speed";
        public static final String CALORIES = "cals";
        public static final String CLIMB = "climb";
        public static final String HEARTRATE = "heartrate";
        public static final String COMMENT = "comment";
        public static final String PRIVACY = "privacy";
        public static final String GPS_DATA = "gps_data";

    }

}
