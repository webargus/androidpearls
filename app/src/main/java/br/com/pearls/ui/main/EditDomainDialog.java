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

import br.com.pearls.DB.Domain;
import br.com.pearls.R;

public class EditDomainDialog extends DialogFragment {

    private static final String TAG = EditDomainDialog.class.getName();

    private EditText mDomainNameEdit;
    private OnEditDomain onEditDomain;

    public interface OnEditDomain {
        void onEditDomainInput(String domainName);
        String getDomainName();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.edit_domain_dlg, null);

        builder.setView(view).setTitle("Edit Domain")
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dismiss();
                    }
                })
                .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String domainName = mDomainNameEdit.getText().toString().trim();
                        onEditDomain.onEditDomainInput(domainName);
                    }
                });
        mDomainNameEdit = view.findViewById(R.id.edit_domain_name);
        mDomainNameEdit.setText(onEditDomain.getDomainName());
        mDomainNameEdit.requestFocus();

        return builder.create();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            onEditDomain = (OnEditDomain) getTargetFragment();
        } catch (ClassCastException ex) {
            Log.e(TAG, "onAttach: ClassCastException: " + ex.getMessage());
            throw new RuntimeException("You must attach the OnEditDomain interface...");
        }
    }
}
















