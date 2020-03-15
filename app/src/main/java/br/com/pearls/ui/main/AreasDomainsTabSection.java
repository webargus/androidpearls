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
    private final OnAreasDomainsTabSectionClick clickListener;
    private boolean expanded = true;

    interface OnAreasDomainsTabSectionClick {
        void onHeaderClicked(@NonNull final AreasDomainsTabSection section);
        boolean onHeaderLongClicked(@NonNull final KnowledgeArea area,
                                    @NonNull final AreasViewHolder viewHolder);
        void onDomainClicked(@NonNull final KnowledgeArea area, @NonNull Domain domain);
        boolean onDomainLongClicked(@NonNull final KnowledgeArea area, @NonNull final Domain domain);
    }

    public AreasDomainsTabSection(@NonNull final AreasWithDomains areasWithDomains,
                                  @NonNull final OnAreasDomainsTabSectionClick clickListener) {
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
        itemViewHolder.itemView
                .setOnClickListener(view -> clickListener.onDomainClicked(area, domains.get(position)));
        itemViewHolder.itemView
                .setOnLongClickListener(view ->
                        clickListener.onDomainLongClicked(area, domains.get(position)));
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
        viewHolder.headerView.setOnLongClickListener(view -> clickListener.onHeaderLongClicked(area, viewHolder));
    }
}
