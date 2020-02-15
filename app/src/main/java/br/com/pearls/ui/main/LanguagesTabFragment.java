package br.com.pearls.ui.main;


import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.google.android.material.snackbar.Snackbar;

import br.com.pearls.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class LanguagesTabFragment extends Fragment {

    private static final String TAG = LanguagesTabFragment.class.getName();

    Button languageSaveBtn;

    private static Integer cnt = 0;

    public LanguagesTabFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "....-> onCreateView");
        cnt++;
        View root = inflater.inflate(R.layout.fragment_tab_languages, container, false);
        languageSaveBtn = root.findViewById(R.id.btn_save_language);
        languageSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Language save btn clicked: " + cnt.toString(), Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        return root;
    }



}
