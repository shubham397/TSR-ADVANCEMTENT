package com.dialursearch.prosperous;

import android.app.Activity;
import android.content.Intent;
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
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

public class Service_CentreActivity extends Activity {

    ListView listView;

    CardView cardView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service__centre);

        new JSONAsyncTask().execute("http://prosperousapp.emeglobal.com/readRMN.php");
        listView = (ListView) findViewById(R.id.listView);

        cardView=(CardView)findViewById(R.id.card_view);
    }
    //Back Button
    private Boolean goBack = false;
    @Override
    public void onBackPressed() {
        if (goBack) {
            Intent intent=new Intent(Service_CentreActivity.this,MainActivity.class);
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

        String[] invoiceno, productname, productmodel;

        JSONArray jarray;


        String str;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("http://prosperousapp.emeglobal.com/readServiceCentre.php");

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                str =  EntityUtils.toString(httpResponse.getEntity());

                JSONObject jsono = new JSONObject(str);
                jarray = jsono.getJSONArray("RMN");
                invoiceno = new String[jarray.length()];
                productname = new String[jarray.length()];
                productmodel = new String[jarray.length()];
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject o = jarray.getJSONObject(i);
                    invoiceno[i] = o.get("InvoiceNo").toString();
                    productname[i] = o.getString("ProductName");
                    productmodel[i] = o.getString("ProductModel");

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
            CustomAdapter adapter = new CustomAdapter(invoiceno,productname, productmodel);
            listView.setAdapter(adapter);
        }
    }

    class CustomAdapter extends BaseAdapter {
        String[] invoice;
        String[] product;
        String[] productmodel;

        public CustomAdapter(String[] invoice, String[] product, String[] productmodel) {
            this.invoice = invoice;
            this.product = product;
            this.productmodel = productmodel;
        }

        @Override
        public int getCount() {
            return invoice.length;
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
            final TextView txtinvoice, txtproduct, txtproductmodel,txtView;
            txtinvoice = (TextView) row.findViewById(R.id.textView1);
            txtproduct = (TextView) row.findViewById(R.id.textView2);
            txtproductmodel = (TextView) row.findViewById(R.id.textView3);
            txtView = (TextView) row.findViewById(R.id.textView4);
            txtinvoice.setText("Invoice No. : "+invoice[i]);
            txtproduct.setText("Product Name : "+product[i]);
            txtproductmodel.setText("Product Model : "+productmodel[i]);
            txtView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //strinvoice=txtinvoice.getText().toString();
                    String[] str;
                    //str=strinvoice.split(":");
                    //strinvoice=str[1].trim();
                    //editorI.putString("Invoice",strinvoice);
                    //editorI.commit();
                    Intent intent=new Intent(Service_CentreActivity.this,ComplainBookingActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            return (row);
        }

    }
}
