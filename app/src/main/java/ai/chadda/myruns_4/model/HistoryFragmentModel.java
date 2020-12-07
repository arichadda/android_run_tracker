package ai.chadda.myruns_4.model;

public class HistoryFragmentModel {

    private int mId;
    private int mInputType;
    private String mActivityType;
    private String mDateTime;
    private int mDuration;
    private double mDistance;
    private double mAveragePace;
    private double mAverageSpeed;
    private int mCalories;
    private double mClimb;
    private int mHeartRate;
    private String mComment;
    private int mPrivacy;
    private String mGPS;

    public HistoryFragmentModel(int mId, int mInputType, String mActivityType, String mDateTime, int mDuration, double mDistance, double mAveragePace,
                                double mAverageSpeed, int mCalories, double mClimb, int mHeartRate, String mComment, int mPrivacy, String mGPS) {
        this.mInputType = mInputType;
        this.mActivityType = mActivityType;
        this.mDateTime = mDateTime;
        this.mDuration = mDuration;
        this.mDistance = mDistance;
        this.mAveragePace = mAveragePace;
        this.mAverageSpeed = mAverageSpeed;
        this.mCalories = mCalories;
        this.mClimb = mClimb;
        this.mHeartRate = mHeartRate;
        this.mComment = mComment;
        this.mPrivacy = mPrivacy;
        this.mGPS = mGPS;
    }

    public int getmInputType() {
        return mInputType;
    }

    public void setmInputType(int mInputType) {
        this.mInputType = mInputType;
    }

    public String getmActivityType() {
        return mActivityType;
    }

    public void setmActivityType(String mActivityType) {
        this.mActivityType = mActivityType;
    }

    public String getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }

    public int getmDuration() {
        return mDuration;
    }

    public void setmDuration(int mDuration) {
        this.mDuration = mDuration;
    }

    public double getmDistance() {
        return mDistance;
    }

    public void setmDistance(double mDistance) {
        this.mDistance = mDistance;
    }

    public double getmAveragePace() {
        return mAveragePace;
    }

    public void setmAveragePace(double mAveragePace) {
        this.mAveragePace = mAveragePace;
    }

    public double getmAverageSpeed() {
        return mAverageSpeed;
    }

    public void setmAverageSpeed(double mAverageSpeed) {
        this.mAverageSpeed = mAverageSpeed;
    }

    public int getmCalories() {
        return mCalories;
    }

    public void setmCalories(int mCalories) {
        this.mCalories = mCalories;
    }

    public double getmClimb() {
        return mClimb;
    }

    public void setmClimb(double mClimb) {
        this.mClimb = mClimb;
    }

    public int getmHeartRate() {
        return mHeartRate;
    }

    public void setmHeartRate(int mHeartRate) {
        this.mHeartRate = mHeartRate;
    }

    public String getmComment() {
        return mComment;
    }

    public void setmComment(String mComment) {
        this.mComment = mComment;
    }

    public int getmPrivacy() {
        return mPrivacy;
    }

    public void setmPrivacy(int mPrivacy) {
        this.mPrivacy = mPrivacy;
    }

    public String getmGPS() {
        return mGPS;
    }

    public void setmGPS(String mGPS) {
        this.mGPS = mGPS;
    }

    public int getmId() {
        return mId;
    }

    public void setmId(int mId) {
        this.mId = mId;
    }
}
