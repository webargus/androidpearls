package br.com.pearls.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import androidx.room.Ignore;

public class GraphVertex implements Parcelable {
    public static final String TAG = GraphVertex.class.getName();

    public long vertex_id;
    public long graph_ref;
    public long term_ref;
    public int user_rank;
    public String vertex_context;
    public String term;
    public long lang_ref;
    public String language;

    public GraphVertex() {}

    @Ignore
    public GraphVertex(GraphVertex other) {
        this.vertex_id = other.vertex_id;
        this.graph_ref = other.graph_ref;
        this.term_ref = other.term_ref;
        this.user_rank = other.user_rank;
        this.vertex_context = other.vertex_context;
        this.term = other.term;
        this.lang_ref = other.lang_ref;
        this.language = other.language;
    }

    @Ignore
    protected GraphVertex(Parcel in) {
        vertex_id = in.readLong();
        graph_ref = in.readLong();
        term_ref = in.readLong();
        user_rank = in.readInt();
        vertex_context = in.readString();
        term = in.readString();
        lang_ref = in.readLong();
        language = in.readString();
    }

    public static final Creator<GraphVertex> CREATOR = new Creator<GraphVertex>() {
        @Override
        public GraphVertex createFromParcel(Parcel in) {
            return new GraphVertex(in);
        }

        @Override
        public GraphVertex[] newArray(int size) {
            return new GraphVertex[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(vertex_id);
        dest.writeLong(graph_ref);
        dest.writeLong(term_ref);
        dest.writeInt(user_rank);
        dest.writeString(vertex_context);
        dest.writeString(term);
        dest.writeLong(lang_ref);
        dest.writeString(language);
    }

    public void debugDump() {
        Log.v(TAG, "vertex id = " + vertex_id);
        Log.v(TAG, "graph_ref = " + graph_ref);
        Log.v(TAG, "term_ref = " + term_ref);
        Log.v(TAG, "term = " + term);
        Log.v(TAG, "lang_ref = " + lang_ref);
        Log.v(TAG, "language = " + language);
        Log.v(TAG, "context = " + vertex_context);
        Log.v(TAG, "user_rank = " + user_rank);
    }
}
