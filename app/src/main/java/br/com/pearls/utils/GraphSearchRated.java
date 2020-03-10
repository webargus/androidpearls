package br.com.pearls.utils;

import android.util.Log;

import br.com.pearls.DB.Domain;

public class GraphSearchRated {

    private int score;
    public GraphSearchResult graph;

    public GraphSearchRated(GraphSearchResult graph) {
        this.graph = graph;
    }

    public void setScore(final Domain domain, final String searchTerm) {
        score = 1;
        if (domain != null) {
            if(graph.area_ref == domain.getArea_ref()) {
                score += 1;
            }
            if(graph.domain_ref == domain.getId()) {
                score += 1;
            }
        }
        if(graph.term_ascii.equals(searchTerm)) {
            score += 1;
        }
    }

    public int getScore() { return score; }

    public String getScorePercent() {
        return (int)(100*score/4) + "%";
    }
}
