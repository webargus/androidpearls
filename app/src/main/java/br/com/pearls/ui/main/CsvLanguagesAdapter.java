package br.com.pearls.ui.main;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Collections;
import java.util.List;

import br.com.pearls.DB.Language;
import br.com.pearls.R;

public class CsvLanguagesAdapter
        extends RecyclerView.Adapter<CsvLanguagesAdapter.CsvLanguagesViewHolder> {

    public static final String TAG = CsvLanguagesAdapter.class.getName();
    private List<Language> languages;

    @NonNull
    @Override
    public CsvLanguagesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.csv_languages_item, parent, false);
        return new CsvLanguagesViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull CsvLanguagesViewHolder holder, int position) {
        holder.csvLanguageView.setText(languages.get(position).getLanguage());
    }

    @Override
    public int getItemCount() {
        if(languages == null) {
            return 0;
        }
        return languages.size();
    }

    class CsvLanguagesViewHolder extends RecyclerView.ViewHolder {

        TextView csvLanguageView;

        public CsvLanguagesViewHolder(@NonNull View itemView) {
            super(itemView);
            csvLanguageView = itemView.findViewById(R.id.csv_language_name);
        }
    }

    public void setLanguages(@NonNull final List<Language> languages) {
        this.languages = languages;
    }

    public void swapPositions(@NonNull final int fromPosition, @NonNull final int toPosition) {
        Collections.swap(languages, fromPosition, toPosition);
        notifyDataSetChanged();
    }
}
