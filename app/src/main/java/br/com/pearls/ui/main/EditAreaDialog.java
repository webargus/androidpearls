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

import br.com.pearls.DB.KnowledgeArea;
import br.com.pearls.R;

public class EditAreaDialog extends DialogFragment {

    private static final String TAG = EditAreaDialog.class.getName();

    private EditText mAreaNameEdit;
    private OnAreaEditIFace onAreaEditIFace;

    public interface OnAreaEditIFace {
        void onAreaEdit(String area);
        String getKnowledgeArea();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_area_dlg, null);

        builder.setView(view).setTitle("Edit Area")
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
                        onAreaEditIFace.onAreaEdit(area);
                    }
                });
        mAreaNameEdit = view.findViewById(R.id.edit_text_area);
        mAreaNameEdit.setText(onAreaEditIFace.getKnowledgeArea());
        mAreaNameEdit.requestFocus();

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onAreaEditIFace = (OnAreaEditIFace) getTargetFragment();
        } catch (ClassCastException ex) {
            Log.e(TAG, "onAttach: ClassCastException: " + ex.getMessage());
            throw new RuntimeException("You must attach the OnAreaEditIFace interface...");
        }
    }
}
















