package fiap.com.br.amapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.RatingBar;
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
import fiap.com.br.amapp.model.Payment;
import fiap.com.br.amapp.model.SalesforceAuth;
import fiap.com.br.amapp.salesforce.Authentication;

public class RatingActivity extends AppCompatActivity {
    private Authentication authentication = new Authentication();
    private SalesforceAuth salesforceAuth = SalesforceAuth.getInstance();
    private Order order = Order.getInstance();
    private Payment payment = Payment.getInstance();

    private RequestQueue requestQueue;
    private StringRequest request;

    private double generalRating;
    private double attendanceRanting;
    private double foodRating;
    private double speedRating;
    private String comment;

    private Button btnSubmit;

    private RatingBar mRatingBar1;
    private RatingBar mRatingBar2;
    private RatingBar mRatingBar3;
    private TextView mComment;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rating);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        authentication.getAuth(getApplicationContext());
        salesforceAuth = SalesforceAuth.getInstance();
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        mComment = (TextView) findViewById(R.id.comment);

        addListenerOnRatingBar();
        addListenerOnButton();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public void addListenerOnRatingBar() {
        mRatingBar1 = (RatingBar) findViewById(R.id.ratingBarQuestion1);
        mRatingBar2 = (RatingBar) findViewById(R.id.ratingBarQuestion2);
        mRatingBar3 = (RatingBar) findViewById(R.id.ratingBarQuestion3);

        mRatingBar1.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar mRatingBar1, float avaliacao, boolean fromUser) {
            }
        });
        mRatingBar2.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar mRatingBar2, float rating, boolean fromUser) {
            }
        });
        mRatingBar3.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            public void onRatingChanged(RatingBar mRatingBar3, float rating, boolean fromUser) {
            }
        });
    }

    public void addListenerOnButton() {
        btnSubmit = (Button) findViewById(R.id.send_validation);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                attendanceRanting = mRatingBar1.getRating();
                foodRating = mRatingBar2.getRating();
                speedRating = mRatingBar3.getRating();
                generalRating = (attendanceRanting+foodRating+speedRating)/3;
                comment = mComment.getText().toString();
                sendRating();
            }
        });
    }

    public void sendRating() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nota_Atendimento__c", attendanceRanting);
            jsonBody.put("Nota_Comida__c", foodRating);
            jsonBody.put("Nota_Velocidade__c", speedRating);
            jsonBody.put("Avaliacao__c", generalRating);
            jsonBody.put("Comentario__c", comment);
            final String requestBody = jsonBody.toString();

            /*TODO: Quando LUIZ terminar as telas de Evento, Restaurante e Produto, e recolher os IDs de cada um selecionado descomentar linha abaixo*/
            String url = salesforceAuth.getInstanceUrl() + "/services/data/v43.0/sobjects/Fatura__c/" + order.getOrderId();
//            String url = salesforceAuth.getInstanceUrl() + "/services/data/v43.0/sobjects/Fatura__c/a07f4000005OxpJAAS";

            request = new StringRequest(Request.Method.PATCH, url, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Toast.makeText(RatingActivity.this, "Valeu!", Toast.LENGTH_SHORT).show();

                    order.resetOrder();

                    Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
                    startActivity(intent);
                    finish();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Payment: ", payment.toString());
                    Log.i("Error: ", new String(error.networkResponse.data));
                    Toast.makeText(getApplicationContext(), "Você tem problemas com sua conexão. Teste novamente", Toast.LENGTH_LONG).show();
                }
            }) {

                @Override
                public byte[] getBody() throws AuthFailureError {
                    try {
                        return requestBody == null ? null : requestBody.getBytes("utf-8");
                    } catch (UnsupportedEncodingException uee) {
                        VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                        return null;
                    }
                }

                @Override
                public String getBodyContentType() {
                    return "application/json; charset=utf-8";
                }

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

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

}
