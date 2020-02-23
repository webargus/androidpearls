package br.com.pearls.ui.main;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import br.com.pearls.R;

public class NewAreaActivity extends AppCompatActivity {

    public static final String EXTRA_REPLY = "br.com.pearls.REPLY";
    private EditText mAreaEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_area);

        mAreaEditText = findViewById(R.id.area_edit);

        final Button areaSaveBtn = findViewById(R.id.area_save_btn);
        areaSaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent replyIntent = new Intent();
                if(TextUtils.isEmpty(mAreaEditText.getText())) {
                    setResult(RESULT_CANCELED, replyIntent);
                } else {
                    String area = mAreaEditText.getText().toString();
                    replyIntent.putExtra(EXTRA_REPLY, area);
                    setResult(RESULT_OK, replyIntent);
                }
                finish();
            }
        });
    }
}















