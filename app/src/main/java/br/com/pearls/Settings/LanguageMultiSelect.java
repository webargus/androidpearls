package br.com.pearls.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.text.Spanned;
import android.util.AttributeSet;
import android.util.Log;

import android.widget.TextView;

import androidx.preference.MultiSelectListPreference;
import androidx.preference.Preference;

import java.util.HashSet;
import java.util.Set;

import br.com.pearls.R;

public class LanguageMultiSelect extends MultiSelectListPreference
                                    implements SettingsActivity.multiSelectEntries{

    private static final String TAG = LanguageMultiSelect.class.getName();

    public LanguageMultiSelect(Context context, AttributeSet attributes) {
        super(context, attributes);

        // get db language entries from SettingsActivity
        Bundle bundle = SettingsActivity.multiSelectEntries.getEntries();

        // set entries, entry values and default values
        setEntries(bundle.getCharSequenceArray("entries"));
        CharSequence[] entryValues = bundle.getCharSequenceArray("entryValues");
        setEntryValues(entryValues);

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

    }

    private void showAlertDialog(Context context) {
        TextView textView = new TextView(context);
        StringBuilder msg = new StringBuilder();
        msg.append("<div style='text-align: center;'><b>");
        msg.append("Please, select at least one language!");
        msg.append("</b></div>");
        msg.append("<div>");
        msg.append("No changes were made to the previous language selection");
        msg.append("</div>");
        Spanned text = Html.fromHtml(msg.toString());
        textView.setText(text);
        textView.setPadding(15, 15, 15, 15);
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
