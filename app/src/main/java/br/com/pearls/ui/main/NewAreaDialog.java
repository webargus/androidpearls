package br.com.pearls.ui.main;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import br.com.pearls.R;

public class NewAreaDialog extends DialogFragment {

    private static final String TAG = "NewAreaDialog";

    private EditText mAreaNameEdit;
    private OnNewAreaInput onNewAreaInput;

    public interface OnNewAreaInput {
        void sendAreaInput(String area);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_area_dlg, null);

        builder.setView(view).setTitle("New Knowledge Area")
               .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       dismiss();
                   }
               })
               .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                   @Override
                   public void onClick(DialogInterface dialog, int which) {
                       String area = mAreaNameEdit.getText().toString().trim();
                       onNewAreaInput.sendAreaInput(area);
                   }
               });
        mAreaNameEdit = view.findViewById(R.id.new_area_name);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onNewAreaInput = (OnNewAreaInput) getTargetFragment();
        } catch (ClassCastException ex) {
            Log.e(TAG, "onAttach: ClassCastException: " + ex.getMessage());
        }
    }
}


















