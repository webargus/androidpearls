package br.com.pearls.ui.main;


import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.pearls.DB.Language;
import br.com.pearls.R;
import br.com.pearls.utils.GraphVertex;

public class AddEditTermAdapter extends RecyclerView.Adapter<AddEditTermAdapter.TermFormHolder> {

    private List<GraphVertex> vertices;

    @NonNull
    @Override
    public TermFormHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                                      .inflate(R.layout.rv_term_form_item, parent, false);
        return new TermFormHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull TermFormHolder holder, int position) {
        GraphVertex vertex = vertices.get(position);
        holder.tvLanguage.setText(vertex.language);
        holder.rbRank.setRating((float) vertex.user_rank);
        holder.etTerm.setText(vertex.term);
        holder.etContext.setText(vertex.vertex_context);
    }

    @Override
    public int getItemCount() {
        if(vertices != null) {
            return vertices.size();
        }
        return 0;
    }

    public void setData(List<GraphVertex> vertices) {
        this.vertices = vertices;
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
    }
}
