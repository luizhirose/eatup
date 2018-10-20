package fiap.com.br.amapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;

import fiap.com.br.amapp.model.Order;
import fiap.com.br.amapp.model.SalesforceAuth;
import fiap.com.br.amapp.model.User;
import fiap.com.br.amapp.salesforce.Authentication;

public class StatusPaymentActivity extends AppCompatActivity {
    private Authentication authentication = new Authentication();
    private SalesforceAuth salesforceAuth = SalesforceAuth.getInstance();
    private Order order = Order.getInstance();

    private RequestQueue requestQueue;
    private StringRequest request;

    private TextView mOderNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_status_payment);

        salesforceAuth = SalesforceAuth.getInstance();

        requestQueue = Volley.newRequestQueue(getApplicationContext());

        mOderNumber = findViewById(R.id.done_code);
        mOderNumber.setText("");

        getOrderNumber();

        Button mRatingButton = findViewById(R.id.rating);
        mRatingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), RatingActivity.class);
                startActivity(intent);
            }
        });

        Button mBoThanks = findViewById(R.id.no_thanks);
        mBoThanks.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                order.resetOrder();
                Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void getOrderNumber() {
        /*TODO: Quando LUIZ terminar as telas de Evento, Restaurante e Produto, e recolher os IDs de cada um selecionado descomentar linha abaixo*/
            String url = salesforceAuth.getInstanceUrl() + "/services/data/v43.0/query?q=SELECT+Name,Id+FROM+Fatura__c+WHERE+Id+=+'"+order.getOrderId()+"'" +
                    "+AND+Restaurante__c+=+'" + order.getEstablishmentId() + "'+AND+Usuario__c=+'" + order.getUser().getId() + "'+AND+Evento__c+=+'" + order.getEventId() + "'";
//        String url = salesforceAuth.getInstanceUrl() + "/services/data/v43.0/query?q=SELECT+Name,Id+FROM+Fatura__c+WHERE+Restaurante__c+=+'a04f4000005iQcrAAE'+AND+Usuario__c+=+'a0Af400000C7j0VEAR'+AND+Pagamento__c+=+'a06f4000008JJuhAAG'+AND+Evento__c+=+'a03f400000aPLXMAA4'";

        request = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    JSONObject object = new JSONObject(response);
                    mOderNumber.setText(object.getJSONArray("records").getJSONObject(0).get("Name").toString());
                    order.setOrderId(object.getJSONArray("records").getJSONObject(0).get("Id").toString());
                    Log.i("Response", object.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error: ", new String(error.networkResponse.data));
                Toast.makeText(getApplicationContext(), "Você tem problemas com sua conexão. Teste novamente. " + error.getCause(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", salesforceAuth.getTokenType() + " " + salesforceAuth.getAccessToken());
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
