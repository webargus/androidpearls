package br.com.pearls.Settings;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;

import androidx.preference.MultiSelectListPreference;

public class LanguageMultiSelect extends MultiSelectListPreference {

    private static final String TAG = LanguageMultiSelect.class.getName();

    private CharSequence[] entries;
    private CharSequence[] entry_values;

    public LanguageMultiSelect(Context context, AttributeSet attributes) {
        super(context, attributes);


        try {
            this.setEntries(entries);
            this.setEntryValues(entry_values);
        } catch (Exception ex) {
            Log.e(TAG, ex.toString());
        }
    }



}
