package fiap.com.br.amapp;

import android.content.Intent;
import android.net.Uri;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.HashMap;
import java.util.Map;

import fiap.com.br.amapp.model.Payment;
import fiap.com.br.amapp.model.SalesforceAuth;
import fiap.com.br.amapp.salesforce.Authentication;

public class ForgetPasswordActivity extends AppCompatActivity {
    private Authentication authentication = new Authentication();
    private SalesforceAuth salesforceAuth = SalesforceAuth.getInstance();

    private RequestQueue requestQueue;
    private StringRequest request;

    private EditText mEmail;

    String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);

        authentication.getAuth(getApplicationContext());
        salesforceAuth = SalesforceAuth.getInstance();

        mEmail = findViewById(R.id.email);

        Button mSend = (Button) findViewById(R.id.btnSend);
        mSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                requestQueue = Volley.newRequestQueue(getApplicationContext());
                email = mEmail.getText().toString();
                recoveryPassword();
            }
        });
    }

    public void recoveryPassword() {
        JSONObject jsonBody = new JSONObject();
        final String requestBody = jsonBody.toString();

        request = new StringRequest(Request.Method.GET, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/query?q=SELECT+Senha__c+FROM+Usuario__c+WHERE+Email__c+=+'"+email+"'", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

//                    JSONArray array = new JSONArray(response);

                    JSONObject object = new JSONObject(response);

                    try {
                            String senha = "Sua senha: " + object.getJSONArray("records").getJSONObject(0).get("Senha__c").toString();
                            Toast.makeText(ForgetPasswordActivity.this, senha, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                            startActivity(intent);
                            finish();

                    }catch (Exception e){
                        Toast.makeText(ForgetPasswordActivity.this, "Usuário não existe", Toast.LENGTH_SHORT).show();
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
