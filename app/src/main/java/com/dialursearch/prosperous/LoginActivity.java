package com.dialursearch.prosperous;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity {

    Button btnInvoiceReset, btnInvoiceSubmit,btnRMNReset,btnRMNSubmit;

    EditText edi_invoice,edi_RMN;

    SharedPreferences pref_I,pref_R;
    SharedPreferences.Editor editor_I,editor_R;

    String serverURL="http://prosperousapp.emeglobal.com/Login.php";
    String serverURLRMN="http://prosperousapp.emeglobal.com/LoginRMN.php";

    String strinvoice,strRMN;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btnInvoiceReset = (Button) findViewById(R.id.btn_Reset);
        btnInvoiceSubmit = (Button) findViewById(R.id.btn_Submit);
        btnRMNReset = (Button) findViewById(R.id.btn_ResetRMN);
        btnRMNSubmit = (Button) findViewById(R.id.btn_SubmitRMN);


        edi_invoice=(EditText)findViewById(R.id.edit_invoice);
        edi_RMN=(EditText)findViewById(R.id.edit_RMN);

        pref_I = getApplicationContext().getSharedPreferences("Invoice", 0); // 0 - for private mode
        editor_I = pref_I.edit();

        pref_R = getApplicationContext().getSharedPreferences("RMN", 0); // 0 - for private mode
        editor_R = pref_R.edit();

        //Reset Button Invoice
        btnInvoiceReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"Processing...",Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        });

        //Submit Button Invoice
        btnInvoiceSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"Processing...",Toast.LENGTH_LONG).show();
                String str1=edi_invoice.getText().toString();
                editor_I.putString("Invoice",""+str1);
                editor_I.commit();
                strinvoice=edi_invoice.getText().toString();
                new UserLogin().execute(serverURL);
            }
        });

        //Reset Button RMN
        btnRMNReset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"Processing...",Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        });

        //Submit Button RMN
        btnRMNSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(LoginActivity.this,"Processing...",Toast.LENGTH_LONG).show();
                String str1=edi_RMN.getText().toString();
                editor_R.putString("RMN",""+str1);
                editor_R.commit();
                strRMN=edi_RMN.getText().toString();
                new UserLoginRMN().execute(serverURLRMN);
            }
        });
    }

    //Back Button
    private Boolean goBack = false;
    @Override
    public void onBackPressed() {
        if (goBack) {
            Intent intent=new Intent(LoginActivity.this,MainActivity.class);
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
    class UserLogin extends AsyncTask<String, Void, String> {

        String str;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("invoice", ""+strinvoice));
            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(serverURL);

                httpPost.setEntity(new UrlEncodedFormEntity(list));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                str =  EntityUtils.toString(httpResponse.getEntity());


            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(str.equals("Done"))
            {
                Intent intent = new Intent(LoginActivity.this, ComplainBookingActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(LoginActivity.this,"Invoice No. is not correct",Toast.LENGTH_SHORT).show();
            }


        }
    }

    class UserLoginRMN extends AsyncTask<String, Void, String> {

        String str;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> list1 = new ArrayList<NameValuePair>();
            list1.add(new BasicNameValuePair("rmn", ""+strRMN));
            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost(serverURLRMN);

                httpPost.setEntity(new UrlEncodedFormEntity(list1));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                str =  EntityUtils.toString(httpResponse.getEntity());


            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
            if(str.equals("Done"))
            {
                Intent intent = new Intent(LoginActivity.this, RMNActivity.class);
                startActivity(intent);
                finish();
            }
            else
            {
                Toast.makeText(LoginActivity.this,"RMN is not correct",Toast.LENGTH_SHORT).show();
            }


        }
    }
}
