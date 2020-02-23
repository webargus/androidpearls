package br.com.pearls.ui.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.pearls.DB.AreasViewModel;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.R;

import static android.app.Activity.RESULT_OK;

public class AreasTabFragment extends Fragment {

    private static final String TAG = LanguagesTabFragment.class.getName();
    private AreasViewModel mAreasViewModel;
    public static final int NEW_AREA_ACTIVITY_REQUEST_CODE = 1;

    public AreasTabFragment() {
        // Required empty public constructor
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_tab_areas, container, false);
        RecyclerView recyclerView = root.findViewById(R.id.areas_recyclerview);
        // TODO: try root.getContext() here
        final AreasViewAdapter adapter = new AreasViewAdapter(getContext());
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        FloatingActionButton fab = root.findViewById(R.id.areas_fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), NewAreaActivity.class);
                startActivityForResult(intent, NEW_AREA_ACTIVITY_REQUEST_CODE);
            }
        });

        mAreasViewModel = new ViewModelProvider(this).get(AreasViewModel.class);
        mAreasViewModel.getmAllAreas().observe(this, new Observer<List<KnowledgeArea>>() {
            @Override
            public void onChanged(List<KnowledgeArea> knowledgeAreas) {
                adapter.setAreas(knowledgeAreas);
            }
        });
        return root;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == NEW_AREA_ACTIVITY_REQUEST_CODE && resultCode == RESULT_OK) {
            KnowledgeArea area = new KnowledgeArea();
            area.setArea(data.getStringExtra(NewAreaActivity.EXTRA_REPLY));
            mAreasViewModel.insert(area);
        } else {
            Toast.makeText(getActivity().getApplicationContext(),
                           R.string.area_empty_not_saved,
                           Toast.LENGTH_LONG).show();
        }
    }
}












