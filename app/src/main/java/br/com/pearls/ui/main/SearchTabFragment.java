package br.com.pearls.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.pearls.DB.GraphSearchResultDao;
import br.com.pearls.R;
import br.com.pearls.utils.GraphSearchUtil;

public class SearchTabFragment extends Fragment implements GraphSearchUtil.OnGraphSearchFinished {

    private static final String TAG = SearchTabFragment.class.getName();

    SearchView searchView;
    RecyclerView recyclerView;
    OnNewTermFABClick newTermFABClick;
    GraphSearchUtil graphSearchUtil;

    @Override
    public void fetchGraphSearchResults(List<GraphSearchResultDao.GraphSearchResult> results) {
        for(int ix = 0; ix < results.size(); ix++) {
            GraphSearchResultDao.GraphSearchResult result = results.get(ix);
            Log.v(TAG, "graph_ref: " + result.graph_ref +
                            "; area: " + result.areaName +
                            "; domain: " + result.domainName);
        }
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
        return root;
    }

}
