package br.com.pearls.Settings;

import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.preference.PreferenceCategory;
import androidx.preference.PreferenceFragmentCompat;

import java.util.ArrayList;
import java.util.List;

import br.com.pearls.DB.Language;
import br.com.pearls.DB.LanguagesViewModel;
import br.com.pearls.R;

import static java.lang.String.valueOf;

public class SettingsActivity extends AppCompatActivity {

    private static final String TAG = SettingsActivity.class.getName();

    private LanguagesViewModel mLanguagesViewModel;

    private SettingsFragment settingsFragment;

    private static Bundle LANGUAGE_ENTRIES;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);

        mLanguagesViewModel = new ViewModelProvider(this).get(LanguagesViewModel.class);
        mLanguagesViewModel.getmAllLanguages().observe(this, new Observer<List<Language>>() {
            @Override
            public void onChanged(List<Language> languages) {
                setLanguageEntries(languages, savedInstanceState);
            }
        });
    }

    private void setLanguageEntries(List<Language> languages, Bundle savedInstanceState) {
        List<CharSequence> entries = new ArrayList<>();
        List<CharSequence> entryValues = new ArrayList<>();

        for(int ix = 0; ix < languages.size(); ix++) {
            entries.add(languages.get(ix).getLanguage());
            entryValues.add(String.valueOf(languages.get(ix).getId()));
        }

        LANGUAGE_ENTRIES = new Bundle();
        LANGUAGE_ENTRIES.putCharSequenceArray("entries",
                entries.toArray(new CharSequence[entries.size()]));
        LANGUAGE_ENTRIES.putCharSequenceArray("entryValues",
                entryValues.toArray(new CharSequence[entryValues.size()]));

        // create settings fragment ONLY AFTER having gotten languages from db
        createSettingsFragment(savedInstanceState);
    }

    private void createSettingsFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            settingsFragment = new SettingsFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, settingsFragment)
                    .commit();
        }

        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    // LanguageMultiSelect obj implements this for setting entries and entry values
    public interface multiSelectEntries {
        static Bundle getEntries() { return LANGUAGE_ENTRIES; }
    }

    ///////////////////////////////////////////////////////////////////////////////////////////////
    //          Subclass SettingsFragment
    ///////////////////////////////////////////////////////////////////////////////////////////////

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            addPreferencesFromResource(R.xml.root_preferences);

            PreferenceCategory lang = (PreferenceCategory) findPreference("pref_category_languages");
        }

    }
}