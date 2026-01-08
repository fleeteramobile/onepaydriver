package com.onepaytaxi.driver;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.cardview.widget.CardView;

import com.onepaytaxi.driver.utils.NC;

public class DeleteAccountActivity extends  MainActivity {
    TextView deleteacc;
    private TextView HeadTitle;
    private CardView btn_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public int setLayout() {
        return R.layout.activity_delete_account;
    }

    @SuppressLint("WrongViewCast")
    @Override
    public void Initialize() {

        deleteacc= findViewById(R.id.deleteacc);
        HeadTitle = findViewById(R.id.headerTxt);
        btn_back = findViewById(R.id.back_page);
        btn_back.setVisibility(View.VISIBLE);
        HeadTitle.setText(NC.getString(R.string.privacy_settings));
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
        deleteacc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent in = new Intent(DeleteAccountActivity.this, WebviewAct.class);
                in.putExtra("type", "delete");
                startActivity(in);
            }
        });


    }

}
