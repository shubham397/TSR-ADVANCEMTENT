package com.dialursearch.prosperous;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
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
import android.widget.TextView;
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
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ComplainBookingActivity extends Activity {

    Button btn1, btn2;

    EditText txtmanufacture, txtproductname, txtproductmodel,
            txtname, txtcity, txtaddress, txtpin, txtnumber;

    TextView txtinvoiceno,txtinvoicedate,txtservicerate;

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    String invoiceno, invoicedate, manufacture, productname, productmodel, problem, name, city, address, pin, number;

    EditText editProblem;
    String strcomplain;

    Spinner spinner;

    Spinner spinner_product,spinner_service;

    String str_product,str_market,str_service,str_type,tax,total;

    String[] type,service;

    String str_SMS;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_complain_booking);

        btn1 = (Button) findViewById(R.id.btn_Reset);
        btn2 = (Button) findViewById(R.id.btn_Submit);

        txtinvoiceno = (TextView) findViewById(R.id.edit_invoiveNo);
        txtinvoicedate = (TextView) findViewById(R.id.edit_invoiceDate);
        txtservicerate = (TextView) findViewById(R.id.edit_serviceRate);
        //txtmanufacture = (EditText) findViewById(R.id.edit_manufacture);
        //txtproductname = (EditText) findViewById(R.id.edit_productName);
        txtproductmodel = (EditText) findViewById(R.id.edit_productModel);
        txtname = (EditText) findViewById(R.id.edit_customerName);
        txtcity = (EditText) findViewById(R.id.edit_city);
        txtaddress = (EditText) findViewById(R.id.edit_address);
        txtpin = (EditText) findViewById(R.id.edit_pin);
        txtnumber = (EditText) findViewById(R.id.edit_customerNumber);

        //editProblem = (EditText) findViewById(R.id.edit_problem);

        pref = getApplicationContext().getSharedPreferences("Invoice", 0); // 0 - for private mode
        editor = pref.edit();

        strcomplain=pref.getString("Invoice","");

        txtinvoiceno.setText(strcomplain);

        new JSONAsyncTask().execute("http://prosperousapp.emeglobal.com/read.php");

        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ComplainBookingActivity.this,"Processing...",Toast.LENGTH_LONG).show();
                finish();
                startActivity(getIntent());
            }
        });

        btn2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(ComplainBookingActivity.this,"Processing...",Toast.LENGTH_LONG).show();
                set();
            }
        });

        spinner = (Spinner) findViewById(R.id.spinner_manufature);

        spinner_product=(Spinner)findViewById(R.id.spinner_product);
        spinner_service=(Spinner)findViewById(R.id.spinner_problem);

        new ProductAsyncTask().execute("http://prosperousapp.emeglobal.com/Rate_Product.php");
        spinner_product.setOnItemSelectedListener(new ClassSpinnerProduct());

        new ManufactureAsyncTask().execute("http://prosperousapp.emeglobal.com/manufacture.php");
        spinner.setOnItemSelectedListener(new ClassSpinnerManufature());

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yyyy");
        String str= sdf.format(cal.getTime());

        txtinvoicedate.setText(str);

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
            txtservicerate.setText(" ₹ "+str_service);

            //Toast.makeText(ComplainBookingActivity.this,""+str_market+""+str_service+""+str_type,Toast.LENGTH_SHORT).show();
        }

        public void onNothingSelected(AdapterView<?> parent){
        }
    }

    class ClassSpinnerManufature implements AdapterView.OnItemSelectedListener
    {
        public void onItemSelected(AdapterView<?> parent, View v, int position, long id)
        {
            manufacture=spinner.getSelectedItem().toString();
            //Toast.makeText(ComplainRegisterInstitutionalActivity.this,area,Toast.LENGTH_SHORT).show();
            if(manufacture.equals("Others"))
            {
                // get prompts.xml view
                LayoutInflater li = LayoutInflater.from(ComplainBookingActivity.this);
                View promptsView = li.inflate(R.layout.prompts, null);

                AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ComplainBookingActivity.this);

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
                                        manufacture=userInput.getText().toString();
                                    }
                                });


                // create alert dialog
                AlertDialog alertDialog = alertDialogBuilder.create();

                // show it
                alertDialog.show();

            }
        }

        public void onNothingSelected(AdapterView<?> parent){
            Toast.makeText(ComplainBookingActivity.this, "Choose :", Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean goBack = false;

    @Override
    public void onBackPressed() {
        if (goBack) {
            Intent intent = new Intent(ComplainBookingActivity.this, MainActivity.class);
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

    public void set() {

        Calendar cal = Calendar.getInstance();
        SimpleDateFormat sdf = new SimpleDateFormat("d-MMM-yyyy");
        String str= sdf.format(cal.getTime());
        invoiceno = strcomplain;
        invoicedate = str;
        productname = str_product;
        productmodel = txtproductmodel.getText().toString();
        problem = str_type;
        name = txtname.getText().toString();
        city = txtcity.getText().toString();
        address = txtaddress.getText().toString();
        pin = txtpin.getText().toString();
        number = txtnumber.getText().toString();

        if (productmodel.isEmpty()||pin.isEmpty()) {
            Toast.makeText(ComplainBookingActivity.this, "Please Fill the Form", Toast.LENGTH_SHORT).show();
        } else {
            new SubmitAsyncTask().execute("http://prosperousapp.emeglobal.com/ComplainBooking.php");
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
            list.add(new BasicNameValuePair("complainNo", "" + invoiceno));
            list.add(new BasicNameValuePair("complainDate", "" + invoicedate));
            list.add(new BasicNameValuePair("manufacture", "" + manufacture));
            list.add(new BasicNameValuePair("product", "" + productname));
            list.add(new BasicNameValuePair("Model", "" + productmodel));
            list.add(new BasicNameValuePair("problem", "" + problem));
            list.add(new BasicNameValuePair("service", "" + str_service));
            list.add(new BasicNameValuePair("name", "" + name));
            list.add(new BasicNameValuePair("city", "" + city));
            list.add(new BasicNameValuePair("address", "" + address));
            list.add(new BasicNameValuePair("pin", "" + pin));
            list.add(new BasicNameValuePair("number", "" + number));
            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("http://prosperousapp.emeglobal.com/ComplainBooking.php");

                httpPost.setEntity(new UrlEncodedFormEntity(list));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                str = EntityUtils.toString(httpResponse.getEntity());


            } catch (ClientProtocolException e) {

            } catch (IOException e) {

            }
            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);
//            Toast.makeText(ComplainBookingActivity.this, ""+result, Toast.LENGTH_LONG).show();
            AlertDialog.Builder builder;
            builder = new AlertDialog.Builder(ComplainBookingActivity.this);
            builder.setTitle("Complain Registed")
                    .setMessage("Complain Successfully Booked \n Complain No. = "+str)
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            Intent intent = new Intent(ComplainBookingActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    })
                    .setIcon(android.R.drawable.ic_dialog_info)
                    .show();
                    str_SMS="Customer Name : "+name+" |Mobile : "+number+" |Complain No : "+invoiceno+" |Problem : "+problem;
                    new SendSMSAsyncTask().execute("http://prosperousapp.emeglobal.com/sendSMS.php");
                    sendEmail();

        }
    }

    class JSONAsyncTask extends AsyncTask<String, Void, String> {

        String[] name,mobile,address,area,institute;

        JSONArray jarray;


        String str;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("complain", ""+strcomplain));
            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("http://prosperousapp.emeglobal.com/read.php");

                httpPost.setEntity(new UrlEncodedFormEntity(list));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                str =  EntityUtils.toString(httpResponse.getEntity());

                JSONObject jsono = new JSONObject(str);
                jarray = jsono.getJSONArray("Complain");
                name = new String[jarray.length()];
                mobile = new String[jarray.length()];
                address = new String[jarray.length()];
                area = new String[jarray.length()];
                institute = new String[jarray.length()];

                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject o = jarray.getJSONObject(i);
                    name[i] = o.getString("Name");
                    mobile[i] = o.getString("Mobile");
                    address[i] = o.getString("Address");
                    area[i] = o.getString("Area");
                    institute[i] = o.getString("Institute/Industry");
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
            for (int i = 0; i < jarray.length(); i++) {
                txtname.setText(name[i]);
                txtcity.setText(area[i]);
                txtaddress.setText(address[i]);
                txtnumber.setText(mobile[i]);
                //txtname.setText(name[i]);
            }

        }
    }

    class ManufactureAsyncTask extends AsyncTask<String, Void, Boolean> {

        JSONArray jarray;
        String[] manufacture;


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
                jarray = jsono.getJSONArray("Manufature");
                manufacture = new String[jarray.length()];
                for (int i = 0; i < jarray.length(); i++) {
                    JSONObject o = jarray.getJSONObject(i);
                    manufacture[i] = o.get("Name").toString();
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

            ArrayAdapter manu = new ArrayAdapter(ComplainBookingActivity.this,android.R.layout.simple_spinner_item,manufacture);
            manu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner.setAdapter(manu);
            if (result == false)
                Toast.makeText(ComplainBookingActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

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

            ArrayAdapter manu = new ArrayAdapter(ComplainBookingActivity.this,android.R.layout.simple_spinner_item,product);
            manu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner_product.setAdapter(manu);
            if (result == false)
                Toast.makeText(ComplainBookingActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();

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
            ArrayAdapter manu = new ArrayAdapter(ComplainBookingActivity.this,android.R.layout.simple_spinner_item,type);
            manu.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            //Setting the ArrayAdapter data on the Spinner
            spinner_service.setAdapter(manu);
            if (result.equals("Not"))
                Toast.makeText(ComplainBookingActivity.this, "Unable to fetch data from server", Toast.LENGTH_LONG).show();
        }
    }

    protected void sendEmail() {
        String[] TO = {"info@tsrservices.co.in"};
        String[] CC = {""};
        Intent emailIntent = new Intent(Intent.ACTION_SEND);

        emailIntent.setData(Uri.parse("mailto:"));
        emailIntent.setType("text/plain");
        emailIntent.putExtra(Intent.EXTRA_EMAIL, TO);
        emailIntent.putExtra(Intent.EXTRA_CC, CC);
        emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Complain");
        emailIntent.putExtra(Intent.EXTRA_TEXT, "Invoice No.: "+invoiceno+"\nInvoice Date: "+invoicedate+
                "\nManufacture: "+manufacture+"\nProduct: "+productname+"\nModel: "+productmodel+"\nProblem: "+problem+
                "\nOur Service Centre Rate: "+str_service+"\nCustomer Name: "+name+"\nCity: "+city+"\nAddress: "+address+
                "\nPin: "+pin+"\nCustomer Number: "+number);

        try {
            startActivity(Intent.createChooser(emailIntent, "Send mail..."));
        } catch (android.content.ActivityNotFoundException ex) {
            Toast.makeText(ComplainBookingActivity.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
        }
    }

    class SendSMSAsyncTask extends AsyncTask<String, Void, String> {

        JSONArray jarray;


        String str;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... urls) {
            List<NameValuePair> list = new ArrayList<NameValuePair>();
            list.add(new BasicNameValuePair("m", ""+str_SMS));
            try {
                HttpClient httpClient = new DefaultHttpClient();

                HttpPost httpPost = new HttpPost("http://prosperousapp.emeglobal.com/sendSMS.php");

                httpPost.setEntity(new UrlEncodedFormEntity(list));

                HttpResponse httpResponse = httpClient.execute(httpPost);

                HttpEntity httpEntity = httpResponse.getEntity();

                str =  EntityUtils.toString(httpResponse.getEntity());

                } catch (UnsupportedEncodingException e1) {
                e1.printStackTrace();
            } catch (ClientProtocolException e1) {
                e1.printStackTrace();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            return str;
        }

        @Override
        protected void onPostExecute(String result) {
            //Toast.makeText(ComplainBookingActivity.this,""+str_SMS,Toast.LENGTH_SHORT).show();
            //Toast.makeText(ComplainBookingActivity.this,""+result,Toast.LENGTH_SHORT).show();
        }
    }
}