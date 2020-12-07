package ai.chadda.myruns_4.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

import ai.chadda.myruns_4.R;
import ai.chadda.myruns_4.view.fragments.MyRunsDialogFragment;
import ai.chadda.myruns_4.model.HistoryFragmentModel;

public class HistoryFragmentAdapter extends ArrayAdapter {

    // Constants
    private static final double KM_TO_MI_CONVERSION = 1.60934;

    // Globals
    private TextView mInputActivityTypeText;
    private TextView mDistanceDurationText;
    private TextView mDateTimeText;
    private ArrayList mlist = new ArrayList();

    public HistoryFragmentAdapter(@NonNull Context context, int resource) {
        super(context, resource);
    }

    @Override
    // Adds activity to history list
    public void add(@Nullable Object object) {
        super.add(object);
        mlist.add(object);
    }

    @Override
    public int getCount() {
        return mlist.size();
    }

    @Nullable
    @Override
    public Object getItem(int position) {
        return mlist.get(position);
    }

    @NonNull
    @Override
    // Get specific item in listview
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get item in listview
        View currentRow = convertView;

        // If there is no listview item for position, then create a list view element
        if (currentRow == null) {
            LayoutInflater layoutInflater = (LayoutInflater) this.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            currentRow = layoutInflater.inflate(R.layout.history_fragment_list,parent,false);
            mInputActivityTypeText = currentRow.findViewById(R.id.input_activity_type_text);
            mDistanceDurationText = currentRow.findViewById(R.id.distance_duration_text);
            mDateTimeText = currentRow.findViewById(R.id.date_time_text);
        }
        HistoryFragmentModel historyFragmentModel = (HistoryFragmentModel) getItem(position);

        // Update activity type text view
        if (historyFragmentModel.getmInputType() == 1) {
            mInputActivityTypeText.setText("Manual: " + historyFragmentModel.getmActivityType());
        } else if (historyFragmentModel.getmInputType() == 2) {
            mInputActivityTypeText.setText("GPS: " + historyFragmentModel.getmActivityType());
        } else {
            mInputActivityTypeText.setText("Automatic: " + historyFragmentModel.getmActivityType());
        }

        // update distance and duration text view based on units
        if (MyRunsDialogFragment.METRIC_UNITS_FLAG) {
            mDistanceDurationText.setText(((int)historyFragmentModel.getmDistance()) + " kms, " + (historyFragmentModel.getmDuration()) + " mins");
        } else {
            int unRoundedDistance = (int)(Math.floor(historyFragmentModel.getmDistance()/ KM_TO_MI_CONVERSION));
            String Distance = String.valueOf(unRoundedDistance);
            mDistanceDurationText.setText(( Distance + " mi, " + (historyFragmentModel.getmDuration()) + " mins"));

        }

        // update date and time text view
        if (historyFragmentModel.getmDateTime() != null) {
            mDateTimeText.setText(historyFragmentModel.getmDateTime());
        }

        return currentRow;
    }

}
