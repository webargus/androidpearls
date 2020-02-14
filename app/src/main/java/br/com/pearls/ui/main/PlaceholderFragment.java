package br.com.pearls.ui.main;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONException;

import br.com.pearls.R;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String ARG_SECTION_STRING = "section_string";

    private PageViewModel pageViewModel;

    public static PlaceholderFragment newInstance(int index) {
        PlaceholderFragment fragment = new PlaceholderFragment();
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_SECTION_STRING, index - 1);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pageViewModel = ViewModelProviders.of(this).get(PageViewModel.class);
        int index = 1;
        if (getArguments() != null) {
            index = getArguments().getInt(ARG_SECTION_STRING);
        }
        pageViewModel.setIndex(index);
    }

    @Override
    public View onCreateView(
            @NonNull LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View root;
        final TextView textView;
        if(pageViewModel.getIndex() == 1) {
            root = inflater.inflate(R.layout.fragment_tab_languages, container, false);
            textView = root.findViewById(R.id.languages_text_view);
        } else {
            root = inflater.inflate(R.layout.fragment_tab_search, container, false);
            textView = root.findViewById(R.id.search_text_view);
        }
        pageViewModel.getJson().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(@Nullable JSONArray s) {
                try {
                    textView.setText(s.getJSONObject(0).getString("section"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return root;
    }
}