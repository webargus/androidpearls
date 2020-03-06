package br.com.pearls.ui.main;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.com.pearls.R;
import br.com.pearls.utils.RemoveDiacritics;

public class SearchTabFragment extends Fragment {

    private static final String TAG = SearchTabFragment.class.getName();

    OnNewTermFABClick newTermFABClick;

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
        return root;
    }

}
