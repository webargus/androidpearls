package br.com.pearls.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.R;

public class AreasViewAdapter extends RecyclerView.Adapter<AreasViewAdapter.AreaViewHolder> {

    class AreaViewHolder extends RecyclerView.ViewHolder {
        private final TextView areaItemView;

        private AreaViewHolder(View itemView) {
            super(itemView);
            areaItemView = itemView.findViewById(R.id.areaTextView);
        }
    }

    private final LayoutInflater mInflater;
    private List<KnowledgeArea> mAreas; // Cached copy of areas

    AreasViewAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public AreaViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.fragment_tab_areas_recyclerview_item, parent, false);
        return new AreaViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(AreaViewHolder holder, int position) {
        if (mAreas != null) {
            KnowledgeArea current = mAreas.get(position);
            holder.areaItemView.setText(current.getArea());
        } else {
            // Covers the case of data not being ready yet.
            holder.areaItemView.setText("No Area");
        }
    }

    void setAreas(List<KnowledgeArea> areas){
        mAreas = areas;
        notifyDataSetChanged();
    }

    // getItemCount() is called many times, and when it is first called,
    // mAreas has not been updated (means initially, it's null, and we can't return null).
    @Override
    public int getItemCount() {
        if (mAreas != null)
            return mAreas.size();
        else return 0;
    }
}

