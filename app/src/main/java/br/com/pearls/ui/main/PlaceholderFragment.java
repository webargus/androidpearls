package br.com.pearls.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProviders;

import org.json.JSONArray;
import org.json.JSONException;

import br.com.pearls.R;
import br.com.pearls.SearchActivity;

/**
 * A placeholder fragment containing a simple view.
 */
public class PlaceholderFragment extends Fragment {

    private static final String TAG = PlaceholderFragment.class.getName();

    private static final String ARG_SECTION_STRING = "section_string";

    private PageViewModel pageViewModel;

    private SearchTabFragment searchTabFragment;
    private LanguagesTabFragment languagesTabFragment;

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
        searchTabFragment = new SearchTabFragment();
        languagesTabFragment = new LanguagesTabFragment();
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
        if(pageViewModel.getIndex() == 1) {
            root = languagesTabFragment.getView();
        } else {
            root = searchTabFragment.getView();
        }
        pageViewModel.getJson().observe(this, new Observer<JSONArray>() {
            @Override
            public void onChanged(@Nullable JSONArray s) {
                try {
                    Log.v(TAG, s.getJSONObject(0).getString("section"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });
        return root;
    }
}