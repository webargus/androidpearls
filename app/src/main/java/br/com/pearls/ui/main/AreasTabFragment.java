package br.com.pearls.ui.main;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.List;

import br.com.pearls.DB.AreasViewModel;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.R;

public class AreasTabFragment extends AppCompatDialogFragment implements NewAreaDialog.OnNewAreaInput  {

    private static final String TAG = AreasTabFragment.class.getName();
    private AreasViewModel mAreasViewModel;

    @Override
    public void sendAreaInput(String area) {
        Log.v(TAG, "Got new area input: " + area);

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
                openAreaCreateDialog();
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

    private void openAreaCreateDialog() {
        NewAreaDialog dlg = new NewAreaDialog();
        dlg.setTargetFragment(AreasTabFragment.this, 1);
        dlg.show(getActivity().getSupportFragmentManager(), "AreasTabFragment");
    }

}












