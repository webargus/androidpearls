package br.com.pearls.ui.main;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import java.util.ArrayList;
import java.util.List;

import br.com.pearls.DB.Language;
import br.com.pearls.DB.LanguagesDao;
import br.com.pearls.DB.LanguagesViewModel;
import br.com.pearls.DB.PearlsRoomDatabase;
import br.com.pearls.R;
import br.com.pearls.utils.LiveDataUtil;

public class LanguagesActivity extends AppCompatActivity {

    private static final String TAG = LanguagesActivity.class.getName();

    private int checkedCount = 0;
    private List<Language> activeLanguages = new ArrayList<>();
    private Context context;
    private LanguagesViewModel languagesViewModel;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.languages_activity);
        context = this;
        setTitle(R.string.title_activity_languages);

        Observer<List<Language>> languageObserver = new Observer<List<Language>>() {
            @Override
            public void onChanged(List<Language> languages) {
                activeLanguages = languages;
                LinearLayout linearLayout = findViewById(R.id.language_checkboxes);
                Language language;
                CheckBox checkBox;
                for (int ix = 0; ix < activeLanguages.size(); ix++) {
                    language = activeLanguages.get(ix);
                    checkBox = new CheckBox(context);
                    checkBox.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
                    checkBox.setPadding(16, 16, 16, 16);
                    checkBox.setTextSize(24);
                    checkBox.setText(language.getLanguage());
                    checkedCount += language.getActive();
                    checkBox.setChecked(language.getActive() == 1);
                    checkBox.setId((int)language.getId());
                    checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                            if (!isChecked) {
                                if(checkedCount <= 1) {
                                    buttonView.setChecked(true);
                                    Toast.makeText(context,
                                            "Leave at least one language set",
                                            Toast.LENGTH_SHORT).show();
                                } else {
                                    checkedCount -= 1;
                                }
                            } else {
                                checkedCount += 1;
                            }
                        }
                    });
                    linearLayout.addView(checkBox);
                }
            }
        };
        languagesViewModel = new ViewModelProvider(this).get(LanguagesViewModel.class);
        LiveDataUtil.observeOnce(languagesViewModel.getmAllLanguages(), languageObserver);

        ((Button) findViewById(R.id.btn_set_languages)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int ix = 0; ix < activeLanguages.size(); ix++) {
                    CheckBox cb = (CheckBox) findViewById((int) activeLanguages.get(ix).getId());
                    activeLanguages.get(ix).setActive((cb.isChecked() ? 1 : 0));
                    Log.v(TAG, "Language " + activeLanguages.get(ix).getLanguage() + " " +
                            (activeLanguages.get(ix).getActive() == 1 ? "active" : "inactive"));
                }
                PearlsRoomDatabase db = PearlsRoomDatabase.getDatabase(context);
                LanguagesDao dao = db.languagesDao();
                db.databaseWriteExecutor.execute(() -> {
                    dao.update(activeLanguages);
                });
                finish();
            }
        });
    }

}









