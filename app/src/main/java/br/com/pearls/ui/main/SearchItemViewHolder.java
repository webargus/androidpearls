package br.com.pearls.ui.main;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.pearls.R;

public class SearchItemViewHolder extends RecyclerView.ViewHolder {

    final View rootView;
    final TextView tvLanguage;
    final RatingBar userRating;
    final TextView tvTerm;
    final TextView tvContext;

    public SearchItemViewHolder(@NonNull View itemView) {
        super(itemView);

        rootView = itemView;
        tvLanguage = rootView.findViewById(R.id.tv_result_language);
        userRating = rootView.findViewById(R.id.result_rank);
        tvTerm = rootView.findViewById(R.id.tv_result_term);
        tvContext = rootView.findViewById(R.id.tv_result_context);
    }
}
