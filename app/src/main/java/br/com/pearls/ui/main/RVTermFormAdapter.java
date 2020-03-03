package br.com.pearls.ui.main;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.pearls.DB.Language;
import br.com.pearls.R;

public class RVTermFormAdapter extends RecyclerView.Adapter<RVTermFormAdapter.TermFormHolder> {

    private List<Language> languages;

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
        holder.tvLanguage.setText(language.getLanguage());
    }

    @Override
    public int getItemCount() {
        return languages.size();
    }

    public void setLanguages(List<Language> languages) {
        this.languages = languages;
    }

    class TermFormHolder extends RecyclerView.ViewHolder {
        private TextView tvLanguage;
        private TextView tvRank;
        private EditText etTerm;
        private EditText etContext;

        public TermFormHolder(@NonNull View itemView) {
            super(itemView);
            tvLanguage = itemView.findViewById(R.id.tv_term_form_language);
            tvRank = itemView.findViewById(R.id.tv_term_form_rank);
            etTerm = itemView.findViewById(R.id.et_term_form_term);
            etContext = itemView.findViewById(R.id.et_term_form_context);

        }
    }
}
