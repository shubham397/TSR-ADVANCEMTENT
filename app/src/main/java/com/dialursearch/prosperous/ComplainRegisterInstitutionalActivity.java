package com.dialursearch.prosperous;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
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
import org.apache.http.ParseException;
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
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Handler;

public class ComplainRegisterInstitutionalActivity extends Activity {

    EditText ed1,ed2,ed3,ed4,ed5,ed6;

    Button btn1,btn2;

    String mobile,email,area,name,address,institute;

    Spinner spinner;

    SharedPreferences pref_I;
    SharedPreferences.Editor editor_I;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_register_institutional);
        ed1=(EditText) findViewById(R.id.edit_name);
        ed2=(EditText) findViewById(R.id.edit_mobile);
        ed3=(EditText) findViewById(R.id.edit_email);
        ed4=(EditText) findViewById(R.id.edit_address);
        //ed5=(EditText) findViewById(R.id.edit_area);
        ed6=(EditText) findViewById(R.id.edit_institute);

        btn1=(Button)findViewById(R.id.btn_complain_submit);
        btn2=(Button)findViewById(R.id.btn_complain_reset);

        pref_I = getApplicationContext().getSharedPreferences("Invoice", 0); // 0 - for private mode
        editor_I = pref_I.edit();

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ComplainRegisterInstitutionalActivity.this,"Processing...",Toast.LENGTH_LONG).show();
                set();
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ComplainRegisterInstitutionalActivity.this,"Processing...",Toast.LENGTH_LONG).show();
                ed1.setText("");
                ed2.setText("");
                ed3.setText("");
                ed4.setText("");
                ed6.setText("");
            }
        });

        spinner = (Spinner)findViewById(R.id.spinner_area);

        new AreaAsyncTask().execute("http://prosperousapp.emeglobal.com/area.php");

        spinner.setOnItemSelectedListener(new ClassSpinnerArea());

    }

    class ClassSpinnerArea implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            area=spinner.getSelectedItem().toString();
            //Toast.makeText(ComplainRegisterInstitutionalActivity.this,area,Toast.LENGTH_SHORT).show();
            if(area.equals("Others"))
            {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ComplainRegisterInstitutionalActivity.this);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ComplainRegisterInstitutionalActivity.this);

                // set prompts.xml to alertdialog builder
                alertDialogBuilder.setView(promptsView);

                final EditText userInput = (EditText) promptsView
                        .findViewById(R.id.editTextDialogUserInput);

                // set dialog message
                alertDialogBuilder
                        .setCancelable(false)
                        .setPositiveButton("OK",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,int id) {
                                        // get user input and set it to result
                                        // edit text
                                        area=userInput.getText().toString();
                                    }
                                });

                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        }

        public void onNothingSelected(AdapterView<?> parent){
            Toast.makeText(ComplainRegisterInstitutionalActivity.this, "Choose :", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean goBack = false;

    @Override
    public void onBackPressed() {
        if (goBack) {
            Intent intent=new Intent(ComplainRegisterInstitutionalActivity.this,ComplainRegisterHomeActivity.class);
            startActivity(intent);
            finish();
        } else {
            Toast.makeText(this, "Press Back again to Go Back.",
                    Toast.LENGTH_SHORT).show();
            goBack = true;
            new android.os.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    goBack = false;
                }
            }, 3 * 1000);

        }
    }

    public void set()
    {
        name=ed1.getText().toString();
        mobile=ed2.getText().toString();
        email=ed3.getText().toString();
        address=ed4.getText().toString();
        institute=ed6.getText().toString();


        if(mobile.isEmpty()||email.isEmpty()||area.isEmpty()||name.isEmpty()||institute.isEmpty()||address.isEmpty())
        {
            Toast.makeText(ComplainRegisterInstitutionalActivity.this,"Please fill the form",Toast.LENGTH_SHORT).show();
        }
        else {
            new SubmitAsyncTask().execute("http://prosperousapp.emeglobal.com/ComplainRegister.php");
        }
    }

    class SubmitAsyncTask extends AsyncTask<String, Void, String> {

        String str;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("name", ""+name));
            list.add(new BasicNameValuePair("mobile", ""+mobile));
            list.add(new BasicNameValuePair("email", ""+email));
            list.add(new BasicNameValuePair("address", ""+address));
            list.add(new BasicNameValuePair("area", ""+area));
            list.add(new BasicNameValuePair("institute", ""+institute));

            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("http://prosperousapp.emeglobal.com/ComplainRegister.php");

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
            //Toast.makeText(ComplainBookingActivity.this, ""+result, Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(ComplainRegisterInstitutionalActivity.this);
            builder.setTitle("Complain Registed")
                    .setMessage("Complain Id : "+str)
                    .setCancelable(false)
                    .setPositiveButton("Book Complain", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            String str1=str;
                            editor_I.putString("Invoice",""+str1);
                            editor_I.commit();
                            Intent intent=new Intent(ComplainRegisterInstitutionalActivity.this,ComplainBookingActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
        }
    }

    class AreaAsyncTask extends AsyncTask<String, Void, Boolean> {

        JSONArray jarray;
        String[] area_s;


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
                jarray = jsono.getJSONArray("Area");
                area_s = new String[jarray.length()];
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject o = jarray.getJSONObject(i);
                    area_s[i] = o.get("area").toString();
                }

                return true;

                //------------------>>

            } catch (ParseException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return false;
        }

        protected void onPostExecute(Boolean result) {

            ArrayAdapter area = new ArrayAdapter(ComplainRegisterInstitutionalActivity.this,android.R.layout.simple_spinner_item,area_s);
            area.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner.setAdapter(area);
            if (result == false)
                Toast.makeText(ComplainRegisterInstitutionalActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

        }
    }
}
