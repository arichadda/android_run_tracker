package ai.chadda.myruns_4.view;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import java.util.ArrayList;


public class ManualActivityLoader extends AsyncTaskLoader<ArrayList> {
    public ManualActivityLoader(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onStartLoading() {
       forceLoad();
    }

    @Nullable
    @Override
    public ArrayList loadInBackground() {
        ArrayList mManualList = new ArrayList();
        return null;
    }

    @Override
    public void deliverResult(@Nullable ArrayList data) {
        super.deliverResult(data);
    }
}
