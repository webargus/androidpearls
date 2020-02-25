package br.com.pearls.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import br.com.pearls.DB.AreasViewModel;
import br.com.pearls.DB.AreasWithDomains;
import br.com.pearls.R;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionAdapter;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class AreasDomainsTabFragment extends AppCompatDialogFragment
                        implements AreasDomainsTabSection.OnHeaderClick{

    SectionedRecyclerViewAdapter sectionAdapter;

    private AreasViewModel areasViewModel;

    @Override
    public void onHeaderClicked(@NonNull AreasDomainsTabSection section) {
        final SectionAdapter adapterForSection = sectionAdapter.getAdapterForSection(section);

        // store info of current section state before changing its state
        final boolean wasExpanded = section.isExpanded();
        final int previousItemsTotal = section.getContentItemsTotal();

        section.setExpanded(!wasExpanded);
        adapterForSection.notifyHeaderChanged();

        if (wasExpanded) {
            adapterForSection.notifyItemRangeRemoved(0, previousItemsTotal);
        } else {
            adapterForSection.notifyAllItemsInserted();
        }

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_areas_domains, container, false);

        sectionAdapter = new SectionedRecyclerViewAdapter();

        RecyclerView recyclerView = root.findViewById(R.id.areas_domains_recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionAdapter);

        areasViewModel = new ViewModelProvider(this).get(AreasViewModel.class);
        areasViewModel.getmAreasWithDomains().observe(this, new Observer<List<AreasWithDomains>>() {
            @Override
            public void onChanged(List<AreasWithDomains> areasWithDomains) {
                sectionAdapter.removeAllSections();
                for (AreasWithDomains awd : areasWithDomains) {
                    sectionAdapter.addSection(new AreasDomainsTabSection(awd, AreasDomainsTabFragment.this));
                }
            }
        });

        return root;
    }

}















