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
import ai.chadda.myruns_4.model.ManualEntryModel;

class ManualEntryAdapter extends ArrayAdapter<ManualEntryModel> {

    // Initialize globals
    private Context mContext;
    private int mResource;
    private TextView mTitleView;
    private TextView mAnswerView;

    // Initialize adapter
    public ManualEntryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<ManualEntryModel> objects) {
        super(context, resource, objects);
        mContext = context;
        mResource = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Get title and data from select item in ListView layout
        String title = getItem(position).getTitle();
        String data = getItem(position).getData();

        // Get ManualEntryActivity layout
        LayoutInflater inflater = LayoutInflater.from(mContext);
        convertView = inflater.inflate(mResource, parent, false);

        // Load title/data pairs
        mTitleView = convertView.findViewById(R.id.adapter_text_view);
        mAnswerView = convertView.findViewById(R.id.adapter_answer_text);

        // Set title/data pairs
        mTitleView.setText(title);
        mAnswerView.setText(data);

        return convertView;
    }
}
