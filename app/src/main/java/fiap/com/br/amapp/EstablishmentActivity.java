package fiap.com.br.amapp;

import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fiap.com.br.amapp.adapters.EstablishmentAdapter;
import fiap.com.br.amapp.model.Establishment;
import fiap.com.br.amapp.model.Order;
import fiap.com.br.amapp.model.Ralationship;
import fiap.com.br.amapp.model.SalesforceAuth;
import fiap.com.br.amapp.salesforce.Authentication;

public class EstablishmentActivity extends AppCompatActivity {

    private RecyclerView recycler;
    private ArrayList<Establishment> establishmentsList = new ArrayList<Establishment>();;
    private EstablishmentAdapter adapter;
    private RequestQueue requestQueue;
    private StringRequest request;
    private SalesforceAuth salesforceAuth;

    private ArrayList<Ralationship> ralstionship = new ArrayList<Ralationship>();
    private Order order = Order.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_establishment);

        salesforceAuth = SalesforceAuth.getInstance();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        recycler = (RecyclerView) findViewById(R.id.establishmentRecyclerView) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);

        getRelation();
    }

    public void getList(){
        JSONObject jsonBody = new JSONObject();

        final String requestBody = jsonBody.toString();

        request = new StringRequest(Request.Method.GET, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/query?q=SELECT+Id,Avaliacao__c,Cidade__c,CNPJ__c,Descricao__c,Email__c,Estado__c,Logradouro__c,Name,Telefone__c+FROM+Restaurante__c+ORDER+BY+Name+ASC+NULLS+FIRST", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    JSONObject object = new JSONObject(response);

                    try {

                        for (Ralationship r: ralstionship) {
                            int i;
                            for (i=0; i < object.getJSONArray("records").length();i++){

                                Log.i("i:", i+"");
                                Log.i("Order:", order.getEventId());
                                Log.i("getEventId:", r.getEventId());
                                Log.i("getEstablishmentId:", r.getEstablishmentId());
                                Log.i("JSON:", object.getJSONArray("records").getJSONObject(i).get("Id").toString());

                                if(r.getEventId().equals(order.getEventId()) && r.getEstablishmentId().equals(object.getJSONArray("records").getJSONObject(i).get("Id").toString())){
                                    establishmentsList.add(new Establishment(
                                            object.getJSONArray("records").getJSONObject(i).get("Id").toString(),
                                            Integer.parseInt(object.getJSONArray("records").getJSONObject(i).get("Avaliacao__c").toString()==null? object.getJSONArray("records").getJSONObject(i).get("Avalicao__c").toString():"0"),
                                            object.getJSONArray("records").getJSONObject(i).get("Cidade__c").toString(),
                                            object.getJSONArray("records").getJSONObject(i).get("CNPJ__c").toString(),
                                            object.getJSONArray("records").getJSONObject(i).get("Descricao__c").toString(),
                                            object.getJSONArray("records").getJSONObject(i).get("Email__c").toString(),
                                            object.getJSONArray("records").getJSONObject(i).get("Estado__c").toString(),
                                            object.getJSONArray("records").getJSONObject(i).get("Logradouro__c").toString(),
                                            object.getJSONArray("records").getJSONObject(i).get("Name").toString(),
                                            object.getJSONArray("records").getJSONObject(i).get("Telefone__c").toString()
                                    ));
                                }
                            }

                        }
                        


                        adapter = new EstablishmentAdapter(EstablishmentActivity.this, establishmentsList);
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
        }){
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

    public void getRelation(){
        JSONObject jsonBody = new JSONObject();

        final String requestBody = jsonBody.toString();

        request = new StringRequest(Request.Method.GET, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/query?q=SELECT+Evento__c,Restaurante__c+FROM+Restaurante_e_Estabelecimento__c", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DateFormat format = new SimpleDateFormat("dd/MM/yyyy", Locale.ITALY);

                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    JSONObject object = new JSONObject(response);

                    try {

                        int i;
                        for (i=0; i < object.getJSONArray("records").length();i++){
                            ralstionship.add(new Ralationship(
                                    object.getJSONArray("records").getJSONObject(i).get("Evento__c").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Restaurante__c").toString()
                            ));
                        }
                        getList();
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
        }){
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
