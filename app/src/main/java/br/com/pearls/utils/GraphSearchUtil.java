package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.List;

import br.com.pearls.DB.GraphSearchResultDao;
import br.com.pearls.DB.GraphSearchResultRepository;

public class GraphSearchUtil {

    public static final String TAG = GraphSearchUtil.class.getName();

    private static GraphSearchResultRepository graphSearchResultRepository;
    private static OnGraphSearchFinished onGraphSearchFinished;

    public interface OnGraphSearchFinished {
        void fetchGraphSearchResults(List<GraphSearchResultDao.GraphSearchResult> results);
    }

    public GraphSearchUtil(Application application, Fragment fragment) {
        graphSearchResultRepository = new GraphSearchResultRepository(application);
        try {
            onGraphSearchFinished = (OnGraphSearchFinished) fragment;
        } catch (ClassCastException e) {
            throw new NullPointerException("You must implement the OnGraphSearchFinished interface...");
        }
    }

    public void asyncSearchForTerm(@NonNull final String term) {
        new GraphSearchAsyncTask().execute(RemoveDiacritics.removeDiacritics(term));
    }

    private static class GraphSearchAsyncTask
            extends AsyncTask<String, Void, List<GraphSearchResultDao.GraphSearchResult>> {

        @Override
        protected List<GraphSearchResultDao.GraphSearchResult> doInBackground(String... strings) {
            return graphSearchResultRepository.loadGraphsForTermSearch(strings[0]);
        }

        @Override
        protected void onPostExecute(List<GraphSearchResultDao.GraphSearchResult> graphSearchResults) {
            onGraphSearchFinished.fetchGraphSearchResults(graphSearchResults);
        }
    }
}
