package br.com.pearls.Settings;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.MultiSelectListPreference;

import java.util.HashSet;
import java.util.Set;

public class LanguageMultiSelect extends MultiSelectListPreference {

    private static final String TAG = LanguageMultiSelect.class.getName();

    public LanguageMultiSelect(Context context, AttributeSet attributes) {
        super(context, attributes);

        Bundle bundle = SettingsActivity.getLanguageEntries();
        this.setEntries(bundle.getCharSequenceArray("entries"));
        CharSequence[] entryValues = bundle.getCharSequenceArray("entryValues");
        this.setEntryValues(entryValues);
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
    }

}
