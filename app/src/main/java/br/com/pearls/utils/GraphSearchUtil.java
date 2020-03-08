package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.GraphSearchRepository;
import br.com.pearls.DB.GraphSearchVerticesRepository;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.SearchActivity;

public class GraphSearchUtil {

    public static final String TAG = GraphSearchUtil.class.getName();

    private static GraphSearchRepository graphSearchRepository;
    private static GraphSearchVerticesRepository graphSearchVerticesRepository;
    private static SearchUtilIFace searchUtilIFace;

    public interface SearchUtilIFace {
        void fetchGraphSearchResults(Map<GraphSearchResult, List<SearchVertex>> results);

        Bundle getAreaAndDomain();
    }

    public GraphSearchUtil(Application application, Fragment fragment) {
        graphSearchRepository = new GraphSearchRepository(application);
        graphSearchVerticesRepository = new GraphSearchVerticesRepository(application);
        try {
            searchUtilIFace = (SearchUtilIFace) fragment;
        } catch (ClassCastException e) {
            throw new NullPointerException("You must implement the SearchUtilIFace interface...");
        }
    }

    public void asyncSearchForTerm(@NonNull final String term) {
        new GraphSearchAsyncTask().execute(RemoveDiacritics.removeDiacritics(term));
    }

    private static class GraphSearchAsyncTask
            extends AsyncTask<String, Void, Map<GraphSearchResult, List<SearchVertex>>> {

        @Override
        protected Map<GraphSearchResult, List<SearchVertex>> doInBackground(String... strings) {
            // get current area and domain
            Bundle bundle = searchUtilIFace.getAreaAndDomain();
            KnowledgeArea area = bundle.getParcelable(SearchActivity.CURRENT_AREA);
            Domain domain = bundle.getParcelable(SearchActivity.CURRENT_DOMAIN);
            if (area != null) {
                Log.v(TAG, "area = " + area.getArea() + "; domain = " + domain.getDomain());
            }
            Map<GraphSearchResult, List<SearchVertex>> map1 = new HashMap<>();
            Map<GraphSearchResult, List<SearchVertex>> map2 = new HashMap<>();
            List<GraphSearchResult> graphs =
                    graphSearchRepository.loadGraphsForTermSearch(strings[0]);
            GraphSearchResult graphResult;
            for (int ix = 0; ix < graphs.size(); ix++) {
                map1.put(graphs.get(ix),
                        graphSearchVerticesRepository.fetchVerticesForGraph(graphs.get(ix).graph_ref));
            }
            map1.putAll(map2);
            return map1;
        }

        @Override
        protected void onPostExecute(Map<GraphSearchResult, List<SearchVertex>> graphSearchResults) {
            searchUtilIFace.fetchGraphSearchResults(graphSearchResults);
        }
    }
}
