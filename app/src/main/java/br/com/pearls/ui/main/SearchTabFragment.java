package br.com.pearls.ui.main;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import br.com.pearls.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class SearchTabFragment extends Fragment {


    public SearchTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_tab_search, container, false);
        return root;
    }

}
