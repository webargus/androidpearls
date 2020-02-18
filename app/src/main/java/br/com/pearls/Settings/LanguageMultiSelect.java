package br.com.pearls.Settings;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceManager;
import androidx.preference.PreferenceViewHolder;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.util.HashSet;
import java.util.Set;

import br.com.pearls.R;

public class LanguageMultiSelect extends MultiSelectListPreference {

    private static final String TAG = LanguageMultiSelect.class.getName();

    public LanguageMultiSelect(Context context, AttributeSet attributes) {
        super(context, attributes);

        // get db language entries from SettingsActivity
        Bundle bundle = SettingsActivity.getLanguageEntries();

        // set entries, entry values and default values
        setEntries(bundle.getCharSequenceArray("entries"));
        CharSequence[] entryValues = bundle.getCharSequenceArray("entryValues");
        setEntryValues(entryValues);
//        for(int ix = 0; ix < entryValues.length; ix++) {
//            Log.v(TAG, "xxxxxxxxxxxxx ix = " + ix + "   ->    " + entryValues[ix]);
//        }
        Set<String> defaults = new HashSet<>();
        if(entryValues.length > 0) {
            defaults.add(entryValues[0].toString());
            if(entryValues.length > 1) {
                defaults.add(entryValues[1].toString());
            }
            setDefaultValue(defaults);
        } else {
            setDefaultValue("");
        }

        // watch if user left at least one language option selected
        setOnPreferenceChangeListener(languageChangeListener);

//        setOnPreferenceClickListener(new OnPreferenceClickListener() {
//            @Override
//            public boolean onPreferenceClick(Preference preference) {
//
//
//                TextView textView = new TextView(context);
//                textView.setText("At least one language checked");
//                final AlertDialog.Builder d = new AlertDialog.Builder(context);
//                d.setTitle(R.string.app_name);
//                d.setView(textView);
//                d.show();
//                return false;
//            }
//        });

    }

    private void showAlertDialog(Context context) {
        TextView textView = new TextView(context);
        textView.setText("Please, check at least one language");
        final AlertDialog.Builder d = new AlertDialog.Builder(context);
        d.setTitle(R.string.app_name);
        d.setView(textView);
        d.show();
    }


    Preference.OnPreferenceChangeListener languageChangeListener  =
        new Preference.OnPreferenceChangeListener() {

        @Override
        public boolean onPreferenceChange(Preference preference, Object newValue) {
            if(newValue == null) {
                Log.v(TAG, "no languages selected");
            } else {
                try {
                    Set<String> selectedLanguages = (HashSet<String>) newValue;
                    if (selectedLanguages != null) {
                        if(selectedLanguages.size() > 0) {
                            CharSequence[] seq = selectedLanguages.toArray(new CharSequence[selectedLanguages.size()]);
                            for (int ix = 0; ix < seq.length; ix++) {
                                Log.v(TAG, "->->-> seq[" + ix + "] = " + seq[ix]);
                            }
                            return true;
                        } else {
                            Log.v(TAG, "no languages selected");
                            showAlertDialog(getContext());
                        }
                    }
                } catch (ClassCastException ignored) {
                    Log.v(TAG, "xxxxxxxxxxxxxxxx" + ignored.getMessage());
                }
            }

            return false;
        }
    };


}
