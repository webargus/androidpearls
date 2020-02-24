package br.com.pearls.ui.main;

import android.view.View;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import br.com.pearls.R;

class DomainsViewHolder extends RecyclerView.ViewHolder {

    public final TextView tvItem;

    public DomainsViewHolder(View itemView) {
        super(itemView);

        tvItem = itemView.findViewById(R.id.itemTextView);
    }
}

