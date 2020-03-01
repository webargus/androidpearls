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

public class NewDomainDialog extends DialogFragment {

    private static final String TAG = NewDomainDialog.class.getName();

    private EditText mDomainNameEdit;
    private OnNewDomainInput onNewDomainInput;

    public interface OnNewDomainInput {
        void onNewDomainInput(String area);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.new_domain_dlg, null);

        builder.setView(view).setTitle("New Area Domain")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String area = mDomainNameEdit.getText().toString().trim();
                        onNewDomainInput.onNewDomainInput(area);
                    }
                });
        mDomainNameEdit = view.findViewById(R.id.new_domain_name);

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onNewDomainInput = (OnNewDomainInput) getTargetFragment();
        } catch (ClassCastException ex) {
            Log.e(TAG, "onAttach: ClassCastException: " + ex.getMessage());
        }
    }
}
















