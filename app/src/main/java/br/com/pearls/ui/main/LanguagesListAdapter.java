package br.com.pearls.ui.main;

import java.util.Locale;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import br.com.pearls.R;

public class LanguagesListAdapter extends ArrayAdapter<String> {

    private static final String TAG = LanguagesListAdapter.class.getName();

    private final Context ctxt;
    private final String[] valueArray;

    public LanguagesListAdapter(Context context, String[] values) {
        super(context, R.layout.languages_list_item, values);
        ctxt = context;
        valueArray = values;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) ctxt.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View rowView = inflater.inflate(R.layout.languages_list_item, null, true);
        TextView textView = rowView.findViewById(R.id.txtViewLanguageName);
        ImageView imageView = rowView.findViewById(R.id.imgViewFlag);

        String[] langs = valueArray[position].split(";");
        String code = langs[0];
        textView.setText(langs[1]);

        imageView.setImageResource(
            ctxt.getResources().getIdentifier("drawable/" + code, null, ctxt.getPackageName())
        );

        return rowView;
    }

}