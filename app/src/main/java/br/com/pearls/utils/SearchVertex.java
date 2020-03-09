package br.com.pearls.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Ignore;

public class SearchVertex implements Parcelable {
    public long vertex_id;
    public long term_ref;
    public int user_rank;
    public String vertex_context;
    public String term;
    public long lang_ref;
    public String language;

    public SearchVertex() {}

    @Ignore
    protected SearchVertex(Parcel in) {
        vertex_id = in.readLong();
        term_ref = in.readLong();
        user_rank = in.readInt();
        vertex_context = in.readString();
        term = in.readString();
        lang_ref = in.readLong();
        language = in.readString();
    }

    public static final Creator<SearchVertex> CREATOR = new Creator<SearchVertex>() {
        @Override
        public SearchVertex createFromParcel(Parcel in) {
            return new SearchVertex(in);
        }

        @Override
        public SearchVertex[] newArray(int size) {
            return new SearchVertex[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(vertex_id);
        dest.writeLong(term_ref);
        dest.writeInt(user_rank);
        dest.writeString(vertex_context);
        dest.writeString(term);
        dest.writeLong(lang_ref);
        dest.writeString(language);
    }
}
