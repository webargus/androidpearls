package br.com.pearls.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;
import java.util.Map;

import br.com.pearls.DB.GraphSearchDao;
import br.com.pearls.R;
import br.com.pearls.utils.GraphSearchResult;
import br.com.pearls.utils.GraphSearchUtil;
import br.com.pearls.utils.SearchVertex;
import io.github.luizgrp.sectionedrecyclerviewadapter.SectionedRecyclerViewAdapter;

public class SearchTabFragment extends Fragment implements GraphSearchUtil.OnGraphSearchFinished {

    private static final String TAG = SearchTabFragment.class.getName();

    SearchView searchView;
    RecyclerView recyclerView;
    OnNewTermFABClick newTermFABClick;
    GraphSearchUtil graphSearchUtil;

    private SectionedRecyclerViewAdapter sectionedAdapter;

    @Override
    public void fetchGraphSearchResults(Map<GraphSearchResult, List<SearchVertex>> results) {
        sectionedAdapter.removeAllSections();
        for(Map.Entry entry : results.entrySet()) {
            GraphSearchResult graph = (GraphSearchResult) entry.getKey();
            List<SearchVertex> vertices = (List<SearchVertex>) entry.getValue();
            sectionedAdapter.addSection(new SearchSection(graph, vertices));
//            Log.v(TAG,  "graph: { graph_ref = " + graph.graph_ref +
//                            "; domain_ref = " + graph.domain_ref +
//                            "; domainName = " + graph.domainName +
//                            "; area_ref = " + graph.area_ref +
//                            "; areaName = " + graph.areaName + " }");
//            Log.v(TAG, "\tvertices:");
//            SearchVertex vertex;
//            for(int ix = 0; ix < vertices.size(); ix++) {
//                vertex = vertices.get(ix);
//                Log.v(TAG, "\tvertex_id = " + vertex.vertex_id +
//                                "; term_ref = " + vertex.term_ref +
//                                "; user_rank = " + vertex.user_rank +
//                                "; vertex_context = " + vertex.vertex_context +
//                                "; term = " + vertex.term +
//                                "; lang_ref = " + vertex.lang_ref +
//                                "; language = " + vertex.language);
//            }
//            Log.v(TAG, "----------------------------------------");
        }
        sectionedAdapter.notifyDataSetChanged();
    }

    public interface OnNewTermFABClick {
        void onNewTermFABClick();
    }

    public SearchTabFragment(OnNewTermFABClick newTermFABClick) {
        this.newTermFABClick = newTermFABClick;
    }

    public SearchTabFragment() {/*default constructor: prevents app from crashing when shutting down*/}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tab_search, container, false);

        FloatingActionButton fab = root.findViewById(R.id.btn_add_term);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                newTermFABClick.onNewTermFABClick();
            }
        });

        graphSearchUtil = new GraphSearchUtil(getActivity().getApplication(), this);
        searchView = root.findViewById(R.id.searchView);
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                graphSearchUtil.asyncSearchForTerm("%" + query.trim() + "%");
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        sectionedAdapter = new SectionedRecyclerViewAdapter();

        final RecyclerView recyclerView = root.findViewById(R.id.rv_search);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(sectionedAdapter);

        return root;
    }

}
