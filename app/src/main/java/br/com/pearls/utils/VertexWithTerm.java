package br.com.pearls.utils;

import androidx.annotation.NonNull;

import br.com.pearls.DB.Term;
import br.com.pearls.DB.Vertex;

public class VertexWithTerm {

    protected Vertex vertex;
    protected Term term;

    protected VertexWithTerm() {}

    public VertexWithTerm(@NonNull final Term term) {
        vertex = new Vertex();
        vertex.setTerm_ref(term.getId());
        this.term = term;
    }

    public void setVertexParams(final String vertexContext, final int userRank) {
        this.vertex.setVertex_context(vertexContext);
        this.vertex.setUser_rank(userRank);
    }
}



