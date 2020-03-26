package br.com.pearls.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.pearls.DB.Language;
import br.com.pearls.R;

public class CsvLanguagesAdapter
        extends RecyclerView.Adapter<CsvLanguagesAdapter.CsvLanguagesViewHolder> {

    private List<Language> languages;

    private OnLanguageActiveClick onLanguageActiveClick;

    public interface OnLanguageActiveClick {
        void onLanguageActiveClicked(Language language);
        void onLastActiveLanguageClicked();
    }

    public void setOnLanguageActiveClick(OnLanguageActiveClick onLanguageActiveClick) {
        this.onLanguageActiveClick = onLanguageActiveClick;
    }

    @NonNull
    @Override
    public CsvLanguagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.csv_languages_item, parent, false);
        return new CsvLanguagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CsvLanguagesViewHolder holder, int position) {
        Language language = languages.get(position);
        holder.csvLanguageView.setText(language.getLanguage());
        holder.csvLanguageActive.setChecked(language.getActive() == 1);
        holder.csvLanguageActive.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Language language = languages.get(position);
                // vvv true => we're clicking on the very last active language in list
                if((getActiveCount() == 1) && (language.getActive() == 1)) {
                    // if we've just unchecked the last active language then re-check it and abort
                    if(!isChecked) {
                        buttonView.setChecked(true);
                        if(onLanguageActiveClick != null) {
                            onLanguageActiveClick.onLastActiveLanguageClicked();
                        }
                        return;
                    }
                }
                // toggle language active state
                language.setActive((language.getActive() == 1 ? 0 : 1));
                // update db on change, if i-face set
                if(onLanguageActiveClick != null) {
                    onLanguageActiveClick.onLanguageActiveClicked(language);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        if(languages == null) {
            return 0;
        }
        return languages.size();
    }

    private int getActiveCount() {
        int count = 0;
        for(Language language: languages) {
            count += language.getActive();
        }
        return count;
    }

    class CsvLanguagesViewHolder extends RecyclerView.ViewHolder {

        TextView csvLanguageView;
        CheckBox csvLanguageActive;

        public CsvLanguagesViewHolder(@NonNull View itemView) {
            super(itemView);
            csvLanguageView = itemView.findViewById(R.id.csv_language_name);
            csvLanguageActive = itemView.findViewById(R.id.csv_language_active);
        }
    }

    public void setLanguages(@NonNull final List<Language> languages) {
        this.languages = languages;
        notifyDataSetChanged();
    }

    public void setLanguage(@NonNull int position, @NonNull Language language) {
        languages.set(position, language);
    }

    public Language getLanguage(@NonNull final  int position) {
        return languages.get(position);
    }


}
