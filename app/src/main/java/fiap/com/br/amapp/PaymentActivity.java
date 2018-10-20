package fiap.com.br.amapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.WindowManager;
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
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fiap.com.br.amapp.adapters.DishAdapter;
import fiap.com.br.amapp.model.Dish;
import fiap.com.br.amapp.model.Order;
import fiap.com.br.amapp.model.Payment;
import fiap.com.br.amapp.model.SalesforceAuth;
import fiap.com.br.amapp.model.User;
import fiap.com.br.amapp.salesforce.Authentication;

public class PaymentActivity extends AppCompatActivity {
    private Authentication authentication = new Authentication();
    private SalesforceAuth salesforceAuth = SalesforceAuth.getInstance();
    private Order order = Order.getInstance();
    private Payment payment = Payment.getInstance();

    private RequestQueue requestQueue;
    private StringRequest request;

    private EditText mCardBrand;
    private EditText mDocument;
    private EditText mCardNumber;
    private EditText mCardExp;
    private EditText mCvv;

    private TextView tvTotalOrder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        authentication.getAuth(getApplicationContext());
        salesforceAuth = SalesforceAuth.getInstance();

//        if(order.getPaymentId() == null){
            mCardBrand = findViewById(R.id.card_brand);
            mDocument = findViewById(R.id.cpf);
            mCardNumber = findViewById(R.id.cardNumber);
            mCardExp = findViewById(R.id.cardExp);
            mCvv = findViewById(R.id.cvv);
            tvTotalOrder = findViewById(R.id.tvOrderTotal);
//        }

        if(order.getPaymentId() != null) {
            mCardBrand.setText(payment.getCardBrand());
            mDocument.setText(payment.getCpf());
            mCardNumber.setText(payment.getCardNumber());
            mCardExp.setText(payment.getCardExp());
            mCvv.setText(payment.getCvv());
        }

        tvTotalOrder.setText("Valor Total: R$ "+Double.toString(order.getTotal()));

        Button mPay = (Button) findViewById(R.id.btnPay);
        mPay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQueue = Volley.newRequestQueue(getApplicationContext());

                payment.setCardBrand(mCardBrand.getText().toString());
                payment.setCpf(mDocument.getText().toString());
                payment.setCardNumber(mCardNumber.getText().toString());
                payment.setCardExp(mCardExp.getText().toString());
                payment.setCvv(mCvv.getText().toString());

                attemptPay();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void attemptPay() {
        // Reset errors.
        mCardBrand.setError(null);
        mCardNumber.setError(null);
        mDocument.setError(null);
        mCardExp.setError(null);
        mCvv.setError(null);

        // Store values at the time of the login attempt.
        String cardBrand = mCardBrand.getText().toString();
        String cardNumber = mCardNumber.getText().toString();
        String cpf = mDocument.getText().toString();
        String cardExp = mCardExp.getText().toString();
        String cvv = mCvv.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!isGenericValid(cvv)){
            mCvv.setError(getString(R.string.error_field_required));
            focusView = mCvv;
            cancel = true;
        }

        if(!isGenericValid(cardExp)){
            mCardExp.setError(getString(R.string.error_field_required));
            focusView = mCardExp;
            cancel = true;
        }

        if(!isGenericValid(cardNumber)){
            mCardNumber.setError(getString(R.string.error_field_required));
            focusView = mCardNumber;
            cancel = true;
        }

        if(!isGenericValid(cpf)){
            mDocument.setError(getString(R.string.error_field_required));
            focusView = mDocument;
            cancel = true;
        }

        if(!isGenericValid(cardBrand)){
            mCardBrand.setError(getString(R.string.error_field_required));
            focusView = mCardBrand;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {
            if(order.getPaymentId() == null || order.getPaymentId().equalsIgnoreCase("null"))
                setPayment(payment);
            else
                updatePayment(payment);
        }
    }

    private boolean isGenericValid(String anyString) {
        return anyString.length() >= 3;
    }

    public void setItens(Dish dish) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Fatura__c", order.getOrderId());
            jsonBody.put("Produto__c", dish.getProdutoId());
            jsonBody.put("Quantidade__c", dish.getQuantity());
            jsonBody.put("Valor__c", dish.getPrice());
            final String requestBody = jsonBody.toString();

            request = new StringRequest(Request.Method.POST, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/sobjects/Item_Pedido__c", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        JSONObject object = new JSONObject(response);
//                        order.setOrderId(object.get("id").toString());
                        Toast.makeText(PaymentActivity.this, "Item pedido efetuado com sucesso!", Toast.LENGTH_SHORT).show();
//                        Intent intent = new Intent(getApplicationContext(), StatusPaymentActivity.class);
//                        startActivity(intent);
//                        finish();
                        Log.i("Response", object.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    public void setOrder() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Evento__c", order.getEventId());
            jsonBody.put("Pagamento__c", order.getPaymentId());
            jsonBody.put("Restaurante__c", order.getEstablishmentId());
            jsonBody.put("Usuario__c", order.getUser().getId());//order.getUserId());
            jsonBody.put("ValorTotal__c", order.getTotal());
            final String requestBody = jsonBody.toString();

            request = new StringRequest(Request.Method.POST, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/sobjects/Fatura__c", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        JSONObject object = new JSONObject(response);
                        order.setOrderId(object.get("id").toString());
                        Toast.makeText(PaymentActivity.this, "Pagamento efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                        Intent intent = new Intent(getApplicationContext(), StatusPaymentActivity.class);
                        startActivity(intent);

                        ArrayList<Dish> itens = DishAdapter.itens;
                        for (Dish dish: itens) {
                            setItens(dish);
                        }

                        finish();
                        Log.i("Response", object.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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

    public void updatePayment(final Payment payment) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nome__c", payment.getCardBrand());
            jsonBody.put("CPF__c", payment.getCpf());
            jsonBody.put("Cartao__c", payment.getCardNumber());
            jsonBody.put("Vencimento__c", payment.getCardExp());
            jsonBody.put("CVV__c", payment.getCvv());
            final String requestBody = jsonBody.toString();

            request = new StringRequest(Request.Method.PATCH, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/sobjects/Pagamento__c/" + order.getPaymentId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Toast.makeText(PaymentActivity.this, "Pagamento efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                    setOrder();
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

    public void setPayment(final Payment payment) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nome__c", payment.getCardBrand());
            jsonBody.put("CPF__c", payment.getCpf());
            jsonBody.put("Cartao__c", payment.getCardNumber());
            jsonBody.put("Vencimento__c", payment.getCardExp());
            jsonBody.put("CVV__c", payment.getCvv());
            final String requestBody = jsonBody.toString();

            request = new StringRequest(Request.Method.POST, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/sobjects/Pagamento__c", new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                        StrictMode.setThreadPolicy(policy);
                        JSONObject object = new JSONObject(response);
                        order.setPaymentId(object.get("id").toString());
//                        Toast.makeText(PaymentActivity.this, "Id: "+order.getPaymentId(), Toast.LENGTH_SHORT).show();
                        Toast.makeText(PaymentActivity.this, "Pagamento efetuado com sucesso!", Toast.LENGTH_SHORT).show();
                        setOrder();
                        finish();
                        Log.i("Response", object.toString());

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
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
//                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
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
