package br.com.pearls.ui.main;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.pearls.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class AreasDomainsTabSection extends Section {

    private final String title;
    private final List<String> itemList;
    private final OnHeaderClick clickListener;
    private boolean expanded = true;

    interface OnHeaderClick {
        void onHeaderClicked(@NonNull final AreasDomainsTabSection section);
    }

    public AreasDomainsTabSection(@NonNull final String title,
                                  final List<String> itemList,
                                  @NonNull final  OnHeaderClick clickListener) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.fragment_areas_domains_item)
                .headerResourceId(R.layout.fragment_areas_domains_header)
                .build());
        this.title = title;
        this.itemList = itemList;
        this.clickListener = clickListener;
    }

    class DomainsViewHolder extends RecyclerView.ViewHolder {

        private final TextView tvItem;

        public DomainsViewHolder(View itemView) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.itemTextView);
        }
    }

    class HeaderViewHolder extends RecyclerView.ViewHolder {

        private final View headerView;
        private final TextView tvHeader;
        private final ImageView imgCaret;

        public HeaderViewHolder(View headerView) {
            super(headerView);
            this.headerView = headerView;
            tvHeader = headerView.findViewById(R.id.headerTextView);
            imgCaret = headerView.findViewById(R.id.headerCaretImg);
        }
    }

    boolean isExpanded() {
        return expanded;
    }

    void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public int getContentItemsTotal() {
        if((itemList == null) || !expanded) {
            return 0;
        }
        return itemList.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new DomainsViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        DomainsViewHolder itemViewHolder = (DomainsViewHolder) holder;
        itemViewHolder.tvItem.setText(itemList.get(position));
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new HeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final HeaderViewHolder viewHolder = (HeaderViewHolder) holder;
        viewHolder.tvHeader.setText(title);
        viewHolder.imgCaret.setImageResource(
                expanded ? R.drawable.ic_caret_up: R.drawable.ic_caret_down
        );
        viewHolder.headerView.setOnClickListener(view -> clickListener.onHeaderClicked(this));
    }
}
