package br.com.pearls.utils;

import android.os.Parcel;
import android.os.Parcelable;

import androidx.room.Ignore;

public class GraphSearchResult implements Parcelable {

    public long graph_ref;
    public long domain_ref;
    public long area_ref;
    public String domainName;
    public String areaName;
    public String term_ascii;

    public GraphSearchResult() {}

    @Ignore
    protected GraphSearchResult(Parcel in) {
        graph_ref = in.readLong();
        domain_ref = in.readLong();
        area_ref = in.readLong();
        domainName = in.readString();
        areaName = in.readString();
        term_ascii = in.readString();
    }

    public static final Creator<GraphSearchResult> CREATOR = new Creator<GraphSearchResult>() {
        @Override
        public GraphSearchResult createFromParcel(Parcel in) {
            return new GraphSearchResult(in);
        }

        @Override
        public GraphSearchResult[] newArray(int size) {
            return new GraphSearchResult[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeLong(graph_ref);
        dest.writeLong(domain_ref);
        dest.writeLong(area_ref);
        dest.writeString(domainName);
        dest.writeString(areaName);
        dest.writeString(term_ascii);
    }
}
