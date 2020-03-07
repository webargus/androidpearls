package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.pearls.DB.GraphSearchDao;
import br.com.pearls.DB.GraphSearchRepository;
import br.com.pearls.DB.GraphSearchVerticesDao;
import br.com.pearls.DB.GraphSearchVerticesRepository;

public class GraphSearchUtil {

    public static final String TAG = GraphSearchUtil.class.getName();

    private static GraphSearchRepository graphSearchRepository;
    private static GraphSearchVerticesRepository graphSearchVerticesRepository;
    private static OnGraphSearchFinished onGraphSearchFinished;

    public interface OnGraphSearchFinished {
        void fetchGraphSearchResults(Map<GraphSearchResult, List<SearchVertex>> results);
    }

    public GraphSearchUtil(Application application, Fragment fragment) {
        graphSearchRepository = new GraphSearchRepository(application);
        graphSearchVerticesRepository = new GraphSearchVerticesRepository(application);
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
            extends AsyncTask<String, Void, Map<GraphSearchResult, List<SearchVertex>> > {

        @Override
        protected Map<GraphSearchResult, List<SearchVertex>> doInBackground(String... strings) {
            Map<GraphSearchResult, List<SearchVertex>> map = new HashMap<>();
            List<GraphSearchResult> graphs =
                    graphSearchRepository.loadGraphsForTermSearch(strings[0]);
            for(int ix = 0; ix < graphs.size(); ix++) {
                map.put(graphs.get(ix),
                        graphSearchVerticesRepository.fetchVerticesForGraph(graphs.get(ix).graph_ref));
            }
            return map;
        }

        @Override
        protected void onPostExecute(Map<GraphSearchResult, List<SearchVertex>> graphSearchResults) {
            onGraphSearchFinished.fetchGraphSearchResults(graphSearchResults);
        }
    }
}
