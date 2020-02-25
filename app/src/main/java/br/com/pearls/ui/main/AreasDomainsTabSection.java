package br.com.pearls.ui.main;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.pearls.DB.AreasWithDomains;
import br.com.pearls.DB.Domain;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class AreasDomainsTabSection extends Section {

    private final KnowledgeArea area;
    private final List<Domain> domains;
    private final OnHeaderClick clickListener;
    private boolean expanded = true;

    interface OnHeaderClick {
        void onHeaderClicked(@NonNull final AreasDomainsTabSection section);
    }

    public AreasDomainsTabSection(@NonNull final AreasWithDomains areasWithDomains,
                                  @NonNull final  OnHeaderClick clickListener) {
        // call constructor with layout resources for this Section header and items
        super(SectionParameters.builder()
                .itemResourceId(R.layout.recyclerview_domain)
                .headerResourceId(R.layout.recyclerview_areas)
                .build());
        this.area = areasWithDomains.area;
        this.domains = areasWithDomains.domains;
        this.clickListener = clickListener;
    }

    boolean isExpanded() {
        return expanded;
    }

    void setExpanded(final boolean expanded) {
        this.expanded = expanded;
    }

    @Override
    public int getContentItemsTotal() {
        if((domains == null) || !expanded) {
            return 0;
        }
        return domains.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new DomainsViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        DomainsViewHolder itemViewHolder = (DomainsViewHolder) holder;
        itemViewHolder.tvItem.setText(domains.get(position).getDomain());
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new AreasViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        final AreasViewHolder viewHolder = (AreasViewHolder) holder;
        viewHolder.tvHeader.setText(area.getArea());
        viewHolder.imgCaret.setImageResource(
                expanded ? R.drawable.ic_caret_up: R.drawable.ic_caret_down
        );
        viewHolder.headerView.setOnClickListener(view -> clickListener.onHeaderClicked(this));
    }
}