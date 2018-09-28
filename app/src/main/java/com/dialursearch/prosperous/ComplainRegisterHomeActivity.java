package com.dialursearch.prosperous;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

public class ComplainRegisterHomeActivity extends Activity {

    Button btn1,btn2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_register_home);

        btn1=(Button)findViewById(R.id.btn_complain_individual_registration);
        btn2=(Button)findViewById(R.id.btn_complain_institutional_registration);

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ComplainRegisterHomeActivity.this,ComplainRegisterIndividualActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ComplainRegisterHomeActivity.this,ComplainRegisterInstitutionalActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
    private Boolean goBack = false;

    @Override
    public void onBackPressed() {
        if (goBack) {
            Intent intent=new Intent(ComplainRegisterHomeActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Go Back.",
                    Toast.LENGTH_SHORT).show();
            goBack = true;
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goBack = false;
                }
            }, 3 * 1000);

        }
    }
}
