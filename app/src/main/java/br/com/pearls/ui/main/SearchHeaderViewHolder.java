package br.com.pearls.ui.main;

import android.view.View;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import br.com.pearls.R;

public class SearchHeaderViewHolder extends RecyclerView.ViewHolder {

    final TextView tvDomain;
    final RatingBar pearlsRating;

    public SearchHeaderViewHolder(@NonNull View itemView) {
        super(itemView);

        tvDomain = itemView.findViewById(R.id.tv_result_domain);
        pearlsRating = itemView.findViewById(R.id.pearls_rank);
    }
}
