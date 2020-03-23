package br.com.pearls.ui.main;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import br.com.pearls.DB.Language;
import br.com.pearls.DB.LanguagesRepository;
import br.com.pearls.DB.LanguagesViewModel;
import br.com.pearls.R;

public class CsvReaderLanguagesFragment extends Fragment {

    private RecyclerView languagesRecyclerView;
    private CsvLanguagesAdapter languagesAdapter;
    private LanguagesViewModel languagesViewModel;
    private Observer<List<Language>> languageObserver;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.csv_languages_set_fragment, null);
        languagesRecyclerView = view.findViewById(R.id.csv_languages_recycler_view);
        languagesRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        languagesAdapter = new CsvLanguagesAdapter();
        languagesRecyclerView.setAdapter(languagesAdapter);

        languageObserver = new Observer<List<Language>>() {
            @Override
            public void onChanged(List<Language> languages) {
                languagesAdapter.setLanguages(languages);
            }
        };
        languagesViewModel = new ViewModelProvider(this).get(LanguagesViewModel.class);
        languagesViewModel.getmAllLanguages().observe(getViewLifecycleOwner(), languageObserver);

        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(simpleCallback);
        itemTouchHelper.attachToRecyclerView(languagesRecyclerView);

        return view;
    }

    ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(
            ItemTouchHelper.UP|ItemTouchHelper.DOWN|ItemTouchHelper.START|ItemTouchHelper.END, 0) {
        @Override
        public boolean onMove(@NonNull RecyclerView recyclerView,
                              @NonNull RecyclerView.ViewHolder viewHolder,
                              @NonNull RecyclerView.ViewHolder target) {

            int fromPosition = viewHolder.getAdapterPosition();
            int toPosition = target.getAdapterPosition();

            Language lang1 = languagesAdapter.getLanguage(fromPosition);
            Language lang2 = languagesAdapter.getLanguage(toPosition);
            int status1 = lang1.getStatus();
            lang1.setStatus(lang2.getStatus());
            lang2.setStatus(status1);
            new updateLanguageAsync().execute(lang1, lang2);

            return false;
        }

        @Override
        public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {

        }
    };

    class updateLanguageAsync extends AsyncTask<Language, Void, Void> {

        @Override
        protected Void doInBackground(Language... languages) {
            LanguagesRepository repository = new LanguagesRepository(getActivity().getApplication());
            List<Language> lang = Arrays.asList(languages);
            repository.update(lang);
            return null;
        }
    }
}
