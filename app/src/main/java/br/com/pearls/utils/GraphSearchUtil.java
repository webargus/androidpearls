package br.com.pearls.utils;

import android.app.Application;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import java.util.Comparator;
import java.util.List;
import java.util.TreeMap;

import br.com.pearls.DB.Domain;
import br.com.pearls.DB.GraphSearchRepository;
import br.com.pearls.DB.GraphSearchVerticesRepository;
import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.SearchActivity;

public class GraphSearchUtil {

    public static final String TAG = GraphSearchUtil.class.getName();

    private static TreeMap<GraphSearchResult, List<SearchVertex>> treeMap;
    private static GraphSearchRepository graphSearchRepository;
    private static GraphSearchVerticesRepository graphSearchVerticesRepository;
    private static SearchUtilIFace searchUtilIFace;

    public interface SearchUtilIFace {
        void fetchGraphSearchResults(TreeMap<GraphSearchResult, List<SearchVertex>> results);
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

    /*  Comparator class for TreeMap -> rank results according to score
    *   calculated in getScore method
    * */
    private static class CompareGraphs implements Comparator<GraphSearchResult> {

        private KnowledgeArea area;
        private Domain domain;

        CompareGraphs(KnowledgeArea area, Domain domain) {
            this.area = area;
            this.domain = domain;
        }

        @Override
        public int compare(GraphSearchResult o1, GraphSearchResult o2) {
            int score = getScore(o2) - getScore(o1);
            if(score == 0) {
                return 1;
            }
            return score;
        }

        private int getScore(GraphSearchResult obj) {
            int score = 0;
                if (area != null) {
                    if(obj.area_ref == domain.getArea_ref()) {
                        score ++;
                    }
                    if(obj.domain_ref == domain.getId()) {
                        score ++;
                    }
                }
            return score;
        }
    }

    private static void processGraphResults(List<GraphSearchResult> results) {
        // get current area and domain
        Bundle bundle = searchUtilIFace.getAreaAndDomain();
        KnowledgeArea area = bundle.getParcelable(SearchActivity.CURRENT_AREA);
        Domain domain = bundle.getParcelable(SearchActivity.CURRENT_DOMAIN);
        treeMap = new TreeMap<>(new CompareGraphs(area, domain));
        VertexAsyncSearch.Result result;
        long lastGraphRef = 0;
        for(int ix = 0; ix < results.size(); ix ++) {
            long currentGraphRef = results.get(ix).graph_ref;
            if(currentGraphRef == lastGraphRef) {
                continue;
            }
            lastGraphRef = currentGraphRef;
            result = new VertexAsyncSearch.Result();
            result.graph = results.get(ix);
            if(ix == results.size() - 1) {
                result.lastResult = true;
            }
            new VertexAsyncSearch().execute(result);
        }
    }

    private static void addVerticesToMap(VertexAsyncSearch.Result result) {
        treeMap.put(result.graph, result.vertices);
        if(result.lastResult) {
            searchUtilIFace.fetchGraphSearchResults(treeMap);
        }
    }

    private static class VertexAsyncSearch
            extends AsyncTask<VertexAsyncSearch.Result, Void, VertexAsyncSearch.Result> {

        private static class Result {
            GraphSearchResult graph;
            List<SearchVertex> vertices;
            boolean lastResult;

            Result() {
                lastResult = false;
            }
        }

        @Override
        protected Result doInBackground(Result... results) {
            Result result = results[0];
            result.vertices = graphSearchVerticesRepository.fetchVerticesForGraph(result.graph.graph_ref);
            return result;
        }

        @Override
        protected void onPostExecute(Result result) {
            addVerticesToMap(result);
        }
    }

    private static class GraphSearchAsyncTask
            extends AsyncTask<String, Void, List<GraphSearchResult>> {

        @Override
        protected List<GraphSearchResult> doInBackground(String... strings) {
            return  graphSearchRepository.loadGraphsForTermSearch(strings[0]);
        }

        @Override
        protected void onPostExecute(List<GraphSearchResult> graphSearchResults) {
            processGraphResults(graphSearchResults);
        }
    }
}










