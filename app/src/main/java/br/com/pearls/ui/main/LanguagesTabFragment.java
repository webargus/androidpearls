package br.com.pearls.ui.main;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ListView;

import com.google.android.material.snackbar.Snackbar;

import br.com.pearls.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LanguagesTabFragment extends Fragment {

    private static final String TAG = LanguagesTabFragment.class.getName();

    private Button languageSaveBtn;
    private LanguagesListAdapter langAdapter;

    public LanguagesTabFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
//        Log.v(TAG, "....-> onCreateView");
        View root = inflater.inflate(R.layout.fragment_tab_languages, container, false);
        languageSaveBtn = root.findViewById(R.id.btn_save_language);
        languageSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "clicked on save btn", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        String[] langCodes = getResources().getStringArray(R.array.language_codes);
        ListView langListView = root.findViewById(R.id.languages_list_view);
        langAdapter = new LanguagesListAdapter(getContext(), langCodes);
        langListView.setAdapter(langAdapter);
        refreshList();

        return root;
    }

    private void refreshList() {
        langAdapter.notifyDataSetChanged();
    }

}
