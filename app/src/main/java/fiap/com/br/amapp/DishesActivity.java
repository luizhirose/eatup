package fiap.com.br.amapp;

import android.app.Dialog;
import android.content.Intent;
import android.os.StrictMode;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
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

import fiap.com.br.amapp.adapters.DishAdapter;
import fiap.com.br.amapp.model.Dish;
import fiap.com.br.amapp.model.Order;
import fiap.com.br.amapp.model.SalesforceAuth;

public class DishesActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {
    private RecyclerView recycler;
    private ArrayList<Dish> dishList = new ArrayList<Dish>();;
    private DishAdapter adapter;
    private RequestQueue requestQueue;
    private StringRequest request;
    private Order order;
    private SalesforceAuth salesforceAuth;
    private Button btnAdd;
    private Button btnRemove;
    private Button btnAccept;
    private TextView tvDescQuantity;
    private TextView tvQuantity;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dishes);

        order = Order.getInstance();
        salesforceAuth = SalesforceAuth.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        recycler = (RecyclerView) findViewById(R.id.dishesRecyclerView) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationDishes);

        bottomNavigationView.setOnNavigationItemSelectedListener(this);

        getList();
    }

    private void getList() {
        JSONObject jsonBody = new JSONObject();

        final String requestBody = jsonBody.toString();

        request = new StringRequest(Request.Method.GET, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/query?q=SELECT+Descricao__c,Id,Imagem__c,Nome__c,Preco__c,Restaurante__c+FROM+Produto__c+WHERE+Restaurante__c+=+'"+order.getEstablishmentId()+"'+ORDER+BY+Nome__c+ASC+NULLS+FIRST", new Response.Listener<String>() {
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

                            dishList.add(new Dish(
                                    object.getJSONArray("records").getJSONObject(i).get("Id").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Nome__c").toString(),
                                    object.getJSONArray("records").getJSONObject(i).get("Descricao__c").toString(),
                                    Double.parseDouble((object.getJSONArray("records").getJSONObject(i).get("Preco__c").toString()==null)?"0":object.getJSONArray("records").getJSONObject(i).get("Preco__c").toString()),
                                    object.getJSONArray("records").getJSONObject(i).get("Imagem__c").toString()
                            ));
                        }

                        adapter = new DishAdapter(DishesActivity.this, dishList);

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

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item != null) {
            if (item.getItemId() == R.id.goToCart) {
                if(order.getItemList().size()==0){
                    Toast.makeText(getApplicationContext(), "Não há itens no Carrinho", Toast.LENGTH_LONG).show();
                }else {
                    Intent intent = new Intent(this, BasketActivity.class);
                    startActivity(intent);
                }
            }
        }
        return true;
    }
}
