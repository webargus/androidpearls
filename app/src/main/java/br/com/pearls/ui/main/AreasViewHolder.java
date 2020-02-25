package br.com.pearls.ui.main;

import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.TextView;
// import android.view.ActionMode;

import androidx.recyclerview.widget.RecyclerView;

import br.com.pearls.R;

class AreasViewHolder extends RecyclerView.ViewHolder {

    private static final String TAG = AreasViewHolder.class.getName();

//    private ActionMode actionMode;

    public View headerView;
    public TextView tvHeader;
    public ImageView imgCaret;

    public AreasViewHolder(View headerView) {
        super(headerView);
        this.headerView = headerView;
        tvHeader = headerView.findViewById(R.id.headerTextView);
        imgCaret = headerView.findViewById(R.id.headerCaretImg);

        this.headerView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                PopupMenu menu = new PopupMenu(v.getContext(), v);
                MenuInflater inflater = menu.getMenuInflater();
                inflater.inflate(R.menu.area_menu, menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        switch (item.getItemId()) {
                            case R.id.domain_add:
                                Log.v(TAG, "******************* menu add domain selected");
                                return true;
                            case R.id.area_edit:
                                Log.v(TAG, "******************* menu edit area selected");
                                return true;
                            case R.id.area_delete:
                                Log.v(TAG, "******************* menu area delete selected");
                                return true;
                        }
                        return false;
                    }
                });
                menu.show();
                return true;
            }
        });


//        headerView.setOnLongClickListener(new View.OnLongClickListener() {
//            // Called when the user long-clicks on someView
//            public boolean onLongClick(View view) {
//                if (actionMode != null) {
//                    return false;
//                }
//
//                // Start the CAB using the ActionMode.Callback defined above
//                actionMode = headerView.startActionMode(actionModeCallback);
//                view.setSelected(true);
//                return true;
//            }
//        });
    }

//    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
//
//
//        // Called when the action mode is created; startActionMode() was called
//        @Override
//        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
//            // Inflate a menu resource providing context menu items
//            MenuInflater inflater = mode.getMenuInflater();
//            inflater.inflate(R.menu.area_menu, menu);
//            return true;
//        }
//
//        // Called each time the action mode is shown. Always called after onCreateActionMode, but
//        // may be called multiple times if the mode is invalidated.
//        @Override
//        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
//            return false; // Return false if nothing is done
//        }
//
//        // Called when the user selects a contextual menu item
//        @Override
//        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
//            switch (item.getItemId()) {
//                case R.id.domain_add:
//                    Log.v(TAG, "********************* Domain add clicked");
//                    mode.finish(); // Action picked, so close the CAB
//                    return true;
//                default:
//                    return false;
//            }
//        }
//
//        // Called when the user exits the action mode
//        @Override
//        public void onDestroyActionMode(ActionMode mode) {
//            actionMode = null;
//        }
//    };

}

