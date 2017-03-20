package edu.cmu.chimps.love_study;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.provider.Settings;
import android.widget.Toast;

import static edu.cmu.chimps.love_study.Utils.isAccessibilityEnabled;
import static edu.cmu.chimps.love_study.Utils.isTrackingEnabled;


public class GeneralSettingActivity extends PreferenceActivity {

    private static Context context;
    private static boolean tracking_clicked;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;
        // Display the fragment as the main content.
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                new PrefsFragment()).commit();
    }

    @Override
    public void onResume(){
        super.onResume();
        if(!isTrackingEnabled(context) && tracking_clicked){
           Toast.makeText(context,"Tracking Started!", Toast.LENGTH_LONG).show();
           startTracking();
        }
    }

    public static void startTracking(){
        Intent serviceIntent = new Intent(context,TrackingService.class);
        serviceIntent.setAction(Constants.ACTION.STARTFOREGROUND_ACTION);
        context.startService(serviceIntent);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public static class PrefsFragment extends PreferenceFragment {

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);

            // Load the preferences from an XML resource
            addPreferencesFromResource(R.xml.preferences);

            Preference trackingServicePreference =findPreference("collectDataButton");
            trackingServicePreference
                    .setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    if(!isAccessibilityEnabled(context)){
                        tracking_clicked = true;
                        startActivity(new Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS));
                    }
                    else{
                        startTracking();
                        Toast.makeText(context,"Tracking Started!", Toast.LENGTH_LONG).show();
                    }
                    return false;
                }
            });

            Preference participantIdPreference = findPreference("participantId");
            participantIdPreference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.preference_participant_id_key),
                                    newValue.toString());
                            editor.apply();

                            return true;
                        }
            });

            Preference partnerInitialPreference = findPreference("partnerInitial");
            partnerInitialPreference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.preference_partner_key),
                                    newValue.toString());
                            editor.apply();

                            return true;
                        }
                    });

            Preference f1Preference = findPreference("username1");
            f1Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.f1_key),
                                    newValue.toString());
                            editor.apply();

                            return true;
                        }
                    });

            Preference f2Preference = findPreference("username2");
            f2Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.f2_key),
                                    newValue.toString());
                            editor.apply();

                            return true;
                        }
                    });

            Preference f3Preference = findPreference("username3");
            f3Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.f3_key),
                                    newValue.toString());
                            editor.apply();

                            return true;
                        }
                    });

            Preference f4Preference = findPreference("username4");
            f4Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.f4_key),
                                    newValue.toString());
                            editor.apply();

                            return true;
                        }
            });

            Preference f5Preference = findPreference("username5");
            f5Preference
                    .setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                        @Override
                        public boolean onPreferenceChange(Preference preference,
                                                          Object newValue) {
                            SharedPreferences sharedPref = getActivity().getPreferences(Context.MODE_PRIVATE);

                            SharedPreferences.Editor editor = sharedPref.edit();
                            editor.putString(getString(R.string.f5_key),
                                    newValue.toString());
                            editor.apply();

                            return true;
                        }
                    });
        };
    }
}