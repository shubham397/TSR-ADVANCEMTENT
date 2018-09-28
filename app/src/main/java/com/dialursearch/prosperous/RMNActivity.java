package com.dialursearch.prosperous;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.widget.CardView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
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
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RMNActivity extends Activity {

    ListView listView;

    SharedPreferences pref,prefI;
    SharedPreferences.Editor editor,editorI;

    String strrmn,strcomplain;

    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rmn);

        pref = getApplicationContext().getSharedPreferences("RMN", 0); // 0 - for private mode
        editor = pref.edit();

        prefI = getApplicationContext().getSharedPreferences("Invoice", 0); // 0 - for private mode
        editorI = prefI.edit();

        strrmn=pref.getString("RMN","s");

        new JSONAsyncTask().execute("http://prosperousapp.emeglobal.com/readRMN.php");
        listView = (ListView) findViewById(R.id.listView);

        cardView=(CardView)findViewById(R.id.card_view);
    }

    //Back Button
    private Boolean goBack = false;
    @Override
    public void onBackPressed() {
        if (goBack) {
            Intent intent=new Intent(RMNActivity.this,MainActivity.class);
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

    class JSONAsyncTask extends AsyncTask<String, Void, String> {

        String[] complainno,name,mobile;

        JSONArray jarray;


        String str;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("rmn", ""+strrmn));
            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("http://prosperousapp.emeglobal.com/readRMN.php");

                httpPost.setEntity(new UrlEncodedFormEntity(list));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                str =  EntityUtils.toString(httpResponse.getEntity());

                JSONObject jsono = new JSONObject(str);
                jarray = jsono.getJSONArray("RMN");
                complainno = new String[jarray.length()];
                name = new String[jarray.length()];
                mobile = new String[jarray.length()];

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject o = jarray.getJSONObject(i);
                    complainno[i] = o.getString("ComplainNo");
                    name[i] = o.getString("Name");
                    mobile[i] = o.getString("Mobile");
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
            super.onPostExecute(result);
            CustomAdapter adapter = new CustomAdapter(complainno,name,mobile);
            listView.setAdapter(adapter);
        }
    }

    class CustomAdapter extends BaseAdapter {

        String[] complainno,name,mobile;

        public CustomAdapter(String[] complainno, String[] name, String[] mobile) {
            this.complainno=complainno;
            this.name=name;
            this.mobile=mobile;
        }

        @Override
        public int getCount() {
            return complainno.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int i, View row, ViewGroup parent) {
            LayoutInflater inflater = getLayoutInflater();
            row = inflater.inflate(R.layout.activity_layout, parent, false);

            final TextView txtcomplain, txtname, txtmobile, txtView;

            txtcomplain = (TextView) row.findViewById(R.id.textView1);
            txtname = (TextView) row.findViewById(R.id.textView2);
            txtmobile = (TextView) row.findViewById(R.id.textView3);
            txtView = (TextView) row.findViewById(R.id.textView4);

            txtcomplain.setText("Complain Id : "+complainno[i]);
            txtname.setText("Name : "+name[i]);
            txtmobile.setText("Mobile : "+mobile[i]);

            txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    strcomplain=txtcomplain.getText().toString();
                    String[] str;
                    str=strcomplain.split(":");
                    strcomplain=str[1].trim();
                    editorI.putString("Invoice",strcomplain);
                    editorI.commit();
                    Intent intent=new Intent(RMNActivity.this,ComplainBookingActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            return (row);
        }

    }
}
