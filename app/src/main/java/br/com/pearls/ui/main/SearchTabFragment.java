package br.com.pearls.ui.main;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import br.com.pearls.R;
import br.com.pearls.utils.RemoveDiacritics;

public class SearchTabFragment extends Fragment {

    private static final String TAG = SearchTabFragment.class.getName();

    private TextView tvSearch;
    private EditText editText;
    private Button go;
    OnNewTermFABClick newTermFABClick;

    public interface OnNewTermFABClick {
        void onNewTermFABClick();
    }

    public SearchTabFragment(OnNewTermFABClick newTermFABClick) {
        this.newTermFABClick = newTermFABClick;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tab_search, container, false);

        tvSearch = root.findViewById(R.id.search_text_view);
        editText = root.findViewById(R.id.search_edit);
        go = root.findViewById(R.id.search_go);
        go.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = editText.getText().toString().trim();
                if(str.isEmpty()) {
                    return;
                }
                str = RemoveDiacritics.removeDiacritics(str);
                Log.v(TAG, "#####################################str=" + str);
                tvSearch.setText(str);
            }
        });

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
