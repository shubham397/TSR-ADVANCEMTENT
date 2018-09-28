package com.dialursearch.prosperous;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;

public class RateActivity extends Activity {

    Spinner spinner_product,spinner_service;

    Button btn1;

    String str_product,str_service,str_type;

    String[] type,service;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);

        btn1=(Button)findViewById(R.id.btn_submit);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder=new AlertDialog.Builder(RateActivity.this);
                builder.setCancelable(true);
                builder.setMessage("Product - "+str_product+"\nService - "+str_type+"\nOur Service Centre Rate - ₹ "+str_service);
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alert=builder.create();
                alert.show();
            }
        });

        spinner_product=(Spinner)findViewById(R.id.spinner_product);
        spinner_service=(Spinner)findViewById(R.id.spinner_service);

        new ProductAsyncTask().execute("http://prosperousapp.emeglobal.com/Rate_Product.php");
        spinner_product.setOnItemSelectedListener(new ClassSpinnerProduct());

    }

    class ClassSpinnerProduct implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            str_product=spinner_product.getSelectedItem().toString();
            //Toast.makeText(RateActivity.this,""+str_product,Toast.LENGTH_SHORT).show();
            new ServiceAsyncTask().execute("http://prosperousapp.emeglobal.com/Rate_Service.php");
            spinner_service.setOnItemSelectedListener(new ClassSpinnerService());
            
        }

        public void onNothingSelected(AdapterView<?> parent){
        }
    }

    class ClassSpinnerService implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            str_service=service[position];
            str_type=type[position];
        }

        public void onNothingSelected(AdapterView<?> parent){
        }
    }

    //Back Button
    private Boolean goBack = false;
    @Override
    public void onBackPressed() {
        if (goBack) {
            Intent intent=new Intent(RateActivity.this,MainActivity.class);
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

    class ProductAsyncTask extends AsyncTask<String, Void, Boolean> {

        JSONArray jarray;
        String[] product;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Boolean doInBackground(String... urls) {
            try {
//establishing http connection
                //------------------>>
                HttpGet httppost = new HttpGet(urls[0]);
                HttpClient httpclient = new DefaultHttpClient();
                HttpResponse response = httpclient.execute(httppost);

                // StatusLine stat = response.getStatusLine();

                HttpEntity entity = response.getEntity();
                String data = EntityUtils.toString(entity);


                JSONObject jsono = new JSONObject(data);
                jarray = jsono.getJSONArray("Rate");
                product = new String[jarray.length()];
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject o = jarray.getJSONObject(i);
                    product[i] = o.get("Product").toString();
                }

                return true;

                //------------------>>

            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {

            ArrayAdapter manu = new ArrayAdapter(RateActivity.this,android.R.layout.simple_spinner_item,product);
            manu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner_product.setAdapter(manu);
            spinner_product.setBackgroundColor(Color.WHITE);
            if (result == false)
                Toast.makeText(RateActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }

    class ServiceAsyncTask extends AsyncTask<String, Void, String> {

        JSONArray jarray;


        String str;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("product", ""+str_product));
            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("http://prosperousapp.emeglobal.com/Rate_Service.php");

                httpPost.setEntity(new UrlEncodedFormEntity(list));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                str =  EntityUtils.toString(httpResponse.getEntity());

                JSONObject jsono = new JSONObject(str);
                jarray = jsono.getJSONArray("Rate");
                type = new String[jarray.length()];
                service = new String[jarray.length()];

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject o = jarray.getJSONObject(i);
                    type[i] = o.getString("TypeOfServicing");
                    service[i] = o.getString("ServiceCentreRate");
                }

            } catch (ClientProtocolException e) {

            } catch (IOException e) {
            } catch (JSONException e) {
                e.printStackTrace();
            }

            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            ArrayAdapter manu = new ArrayAdapter(RateActivity.this,android.R.layout.simple_spinner_item,type);
            manu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner_service.setAdapter(manu);
            spinner_service.setBackgroundColor(Color.WHITE);
            if (result.equals("Not"))
                Toast.makeText(RateActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();
        }
    }
}
