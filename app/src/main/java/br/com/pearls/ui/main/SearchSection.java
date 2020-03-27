package br.com.pearls.ui.main;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.pearls.R;
import br.com.pearls.utils.GraphSearchRated;
import br.com.pearls.utils.GraphVertex;
import io.github.luizgrp.sectionedrecyclerviewadapter.Section;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionParameters;

public class SearchSection extends Section {

    private GraphSearchRated header;
    private List<GraphVertex> items;
    private ClickListener clickListener;
    private String stringId;

    public interface ClickListener {
        void onItemClick(String stringId, GraphSearchRated header, List<GraphVertex> vertices);
    }

    public SearchSection(@NonNull final GraphSearchRated header,
                         List<GraphVertex> items, ClickListener clickListener) {
        super(SectionParameters.builder()
                .itemResourceId(R.layout.rv_term_search_item)
                .headerResourceId(R.layout.rv_term_search_item_header)
                .build());

        this.header = header;
        this.items = items;
        this.clickListener = clickListener;
    }

    public void setStringId(String stringId) {
        this.stringId = stringId;
    }

    @Override
    public int getContentItemsTotal() {
        return items.size();
    }

    @Override
    public RecyclerView.ViewHolder getItemViewHolder(View view) {
        return new SearchItemViewHolder(view);
    }

    @Override
    public void onBindItemViewHolder(RecyclerView.ViewHolder holder, int position) {
        SearchItemViewHolder searchItemViewHolder = (SearchItemViewHolder) holder;
        GraphVertex vertex = items.get(position);
        searchItemViewHolder.tvLanguage.setText(vertex.language);
        searchItemViewHolder.userRating.setRating((float)vertex.user_rank);
        searchItemViewHolder.tvTerm.setText(vertex.term);
        searchItemViewHolder.tvContext.setText(vertex.vertex_context);

        searchItemViewHolder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(clickListener != null) {
                    clickListener.onItemClick(stringId, header, items);
                }
            }
        });
    }

    @Override
    public RecyclerView.ViewHolder getHeaderViewHolder(View view) {
        return new SearchHeaderViewHolder(view);
    }

    @Override
    public void onBindHeaderViewHolder(RecyclerView.ViewHolder holder) {
        SearchHeaderViewHolder headerViewHolder = (SearchHeaderViewHolder) holder;
        headerViewHolder.tvDomain.setText(header.graph.areaName + " > " + header.graph.domainName);
        headerViewHolder.tvPearlsRating.setText(header.getScorePercent());
    }

    public void setItems(List<GraphVertex> items) {
        this.items = items;
    }
}













