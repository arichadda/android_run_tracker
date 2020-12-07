package ai.chadda.myruns_4.view.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import ai.chadda.myruns_4.view.LoginActivity;
import ai.chadda.myruns_4.R;

public class SettingsFragment extends PreferenceFragmentCompat {

    private Preference mUnitPreference;
    private Preference mWebpagePreference;
    private Preference mSignOutPreference;
    private static final String UNIT_PREFERENCE = "preference_units";
    private static final String WEBPAGE_PREFERENCE = "preference_webpage";
    private static final String SIGN_OUT_PREFERENCE = "preference_sign_out";
    public static int selection = 0;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        // Handle settings fragment
        setPreferencesFromResource(R.xml.preferences, rootKey);

        // Initialize preferences
        mUnitPreference = findPreference(UNIT_PREFERENCE);
        mWebpagePreference = findPreference(WEBPAGE_PREFERENCE);
        mSignOutPreference = findPreference(SIGN_OUT_PREFERENCE);

        // Set up listeners
        mUnitPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                DialogFragment unitDialog = MyRunsDialogFragment.newInstance(MyRunsDialogFragment.UNIT_DIALOG);
                getActivity().getSupportFragmentManager().beginTransaction().add(unitDialog, getString(R.string.dialog_fragment_tag_unit_preference)).commit();
                return false;
            }
        });
        mWebpagePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://cs.dartmouth.edu/~campbell/cs65/cs65.html"));
                startActivity(browserIntent);
                return true;
            }
        });
        mSignOutPreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getContext(), LoginActivity.class);
                startActivity(intent);
                return true;
            }
        });

    }
}
