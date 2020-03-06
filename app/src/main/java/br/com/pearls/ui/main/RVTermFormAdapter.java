package br.com.pearls.ui.main;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import br.com.pearls.DB.Language;
import br.com.pearls.R;

public class RVTermFormAdapter extends RecyclerView.Adapter<RVTermFormAdapter.TermFormHolder> {

    private List<Language> languages = new ArrayList<>();

    @NonNull
    @Override
    public TermFormHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.rv_term_form_item, parent, false);
        return new TermFormHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TermFormHolder holder, int position) {
        Language language = languages.get(position);
        holder.setTvLanguage(language);
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
        notifyDataSetChanged();
    }

    class TermFormHolder extends RecyclerView.ViewHolder {
        public TextView tvLanguage;
        public RatingBar rbRank;
        public EditText etTerm;
        public EditText etContext;
        public Language language;

        public TermFormHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguage = itemView.findViewById(R.id.tv_term_form_language);
            etTerm = itemView.findViewById(R.id.et_term_form_term);
            etContext = itemView.findViewById(R.id.et_term_form_context);
            rbRank = itemView.findViewById(R.id.term_form_rank);
        }

        public void setTvLanguage(Language language) {
            this.language = language;
            tvLanguage.setText(this.language.getLanguage());
        }
    }
}
