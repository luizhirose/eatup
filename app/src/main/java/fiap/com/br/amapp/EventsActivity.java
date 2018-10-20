package fiap.com.br.amapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import fiap.com.br.amapp.adapters.EventAdapter;
import fiap.com.br.amapp.model.Event;
import fiap.com.br.amapp.model.Order;
import fiap.com.br.amapp.model.SalesforceAuth;
import fiap.com.br.amapp.salesforce.Authentication;

public class EventsActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ArrayList<Event> eventList = new ArrayList<Event>();;
    private EventAdapter adapter;
    private RequestQueue requestQueue;
    private StringRequest request;
    private SalesforceAuth salesforceAuth;
    private Order order;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_events);

        order = Order.getInstance();

        order.resetOrder();

        salesforceAuth = SalesforceAuth.getInstance();

        requestQueue = Volley.newRequestQueue(getApplicationContext());


        recycler = (RecyclerView) findViewById(R.id.eventsRecyclerView) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);

        getEventList();

     }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item != null) {
            if (item.getItemId() == R.id.profile) {
                Intent intent = new Intent(this, RegisterActivity.class);
                intent.putExtra("senderType", "update");
                startActivity(intent);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    public void getEventList() {
        JSONObject jsonBody = new JSONObject();

        final String requestBody = jsonBody.toString();

        request = new StringRequest(Request.Method.GET, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/query?q=SELECT+Id,Name,Descricao__c,Logradouro__c,Estado__c,Cidade__c,Telefone__c,Email__c,DataInicio__c,DataFim__c+FROM+Evento__c+ORDER+BY+DataFim__c+ASC+NULLS+LAST", new Response.Listener<String>() {



            @Override
            public void onResponse(String response) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

//                    JSONArray array = new JSONArray(response);

                    JSONObject object = new JSONObject(response);

                    try {

                        int i;
                        for (i=0; i < object.getJSONArray("records").length();i++){

                            eventList.add(new Event(
                                    object.getJSONArray("records").getJSONObject(i).get("Id").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Name").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Descricao__c").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Logradouro__c").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Estado__c").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Cidade__c").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Telefone__c").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Email__c").toString(),
                                    format.parse("01/08/2018"),
                                    format.parse("01/08/2018")
                                    ));
                        }

                        adapter = new EventAdapter(EventsActivity.this, eventList);

                        recycler.setAdapter(adapter);

                    }catch (Exception e){
                        Log.i("Erro Login: ", e.toString());
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error: ", new String(error.networkResponse.data));
                Toast.makeText(getApplicationContext(), "Você tem problemas com sua conexão. Teste novamente", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer " + salesforceAuth.getAccessToken());
                return headers;
            }
        };


        request.setRetryPolicy(new DefaultRetryPolicy(
                10000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));

        requestQueue.add(request);
    }

}
