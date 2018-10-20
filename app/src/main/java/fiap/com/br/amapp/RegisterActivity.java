package fiap.com.br.amapp;

import android.content.Intent;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
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

public class RegisterActivity extends AppCompatActivity {
    private Authentication authentication = new Authentication();
    private SalesforceAuth salesforceAuth = SalesforceAuth.getInstance();
    private Order order = Order.getInstance();
    private User user = null;

    private RequestQueue requestQueue;
    private StringRequest request;
    private String senderType = "";

    private EditText mFullName;
    private EditText mCpf;
    private EditText mTelNumber;
    private EditText mEmail;
    private EditText mPassword;
    private EditText mPasswordConfirm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

//        authentication.getAuth(getApplicationContext());
        salesforceAuth = SalesforceAuth.getInstance();

        mFullName = (AutoCompleteTextView) findViewById(R.id.fullName);
        mCpf = (AutoCompleteTextView) findViewById(R.id.cpf);
        mTelNumber = (AutoCompleteTextView) findViewById(R.id.number);
        mEmail = (AutoCompleteTextView) findViewById(R.id.email);
        mPassword = findViewById(R.id.password);
        mPasswordConfirm = findViewById(R.id.passwordConfirm);

        Button mRegisterButton = (Button) findViewById(R.id.btnRegister);
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validatePassword()) {
                    user = new User(
                            mFullName.getText().toString(),
                            mPassword.getText().toString(),
                            mCpf.getText().toString(),
                            mEmail.getText().toString(),
                            mTelNumber.getText().toString()
                    );

                    requestQueue = Volley.newRequestQueue(getApplicationContext());

                    Log.i("SALESFORCE", salesforceAuth.toString());

                    attemptLogin();

                } else {
                    Toast.makeText(RegisterActivity.this, "As senhas não são iguais", Toast.LENGTH_SHORT).show();
                }
            }
        });

        Bundle bundle = getIntent().getExtras();

        try{
            if(bundle.size()>0) {
                senderType = bundle.getString("senderType");

                if (senderType.equals("update")) {
                    mFullName.setText(order.getUser().getName());
                    mCpf.setText(order.getUser().getCpf());
                    mTelNumber.setText(order.getUser().getPhone());
                    mEmail.setText(order.getUser().getEmail());

                }
            }
        }catch (Exception e){
            return;
        }
    }

    public void updateUser(final User u) {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nome__c", u.getName());
            jsonBody.put("CPF__c", u.getCpf());
            jsonBody.put("Email__c", u.getEmail());
            jsonBody.put("Telefone__c", u.getPhone());
            jsonBody.put("Senha__c", u.getPassword());
            final String requestBody = jsonBody.toString();

            request = new StringRequest(Request.Method.PATCH, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/sobjects/Usuario__c/" + order.getUser().getId(), new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    Toast.makeText(RegisterActivity.this, "Dados Atualizados com Sucesso!", Toast.LENGTH_LONG).show();


                    order.getUser().setName(u.getName());
                    order.getUser().setCpf(u.getCpf());
                    order.getUser().setEmail(u.getEmail());
                    order.getUser().setPhone(u.getPhone());
                    order.getUser().setPassword(u.getPassword());


                    finish();
                }
            }, new Response.ErrorListener() {

                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.i("Payment: ", u.toString());
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

    private void attemptLogin() {
        // Reset errors.
        mCpf.setError(null);
        mEmail.setError(null);
        mFullName.setError(null);
        mPassword.setError(null);
        mPasswordConfirm.setError(null);
        mTelNumber.setError(null);

        // Store values at the time of the login attempt.
        String cpf = mCpf.getText().toString();
        String email = mEmail.getText().toString();
        String name = mFullName.getText().toString();
        String password = mPassword.getText().toString();
        String passwordConfirm = mPasswordConfirm.getText().toString();
        String phone = mTelNumber.getText().toString();

        boolean cancel = false;
        View focusView = null;

        if(!isGenericValid(passwordConfirm)){
            mPasswordConfirm.setError(getString(R.string.error_field_required));
            focusView = mPasswordConfirm;
            cancel = true;
        }

        if(!isGenericValid(password)){
            mPassword.setError(getString(R.string.error_field_required));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid(password)) {
            mPassword.setError(getString(R.string.error_invalid_password));
            focusView = mPassword;
            cancel = true;
        }

        // Check for a valid email address.
        if (TextUtils.isEmpty(email)) {
            mEmail.setError(getString(R.string.error_field_required));
            focusView = mEmail;
            cancel = true;
        } else if (!isEmailValid(email)) {
            mEmail.setError(getString(R.string.error_invalid_email));
            focusView = mEmail;
            cancel = true;
        }

        if(!isGenericValid(phone)){
            mTelNumber.setError(getString(R.string.error_field_required));
            focusView = mTelNumber;
            cancel = true;
        }

        if(!isGenericValid(cpf)){
            mCpf.setError(getString(R.string.error_field_required));
            focusView = mCpf;
            cancel = true;
        }

        if(!isGenericValid(name)){
            mFullName.setError(getString(R.string.error_field_required));
            focusView = mFullName;
            cancel = true;
        }

        if (cancel) {
            focusView.requestFocus();
        } else {

            if(senderType.equals("update")){
                updateUser(user);
            }else {
                setUser();
            }
        }
    }

    private boolean isEmailValid(String email) {
        return email.contains("@");
    }

    private boolean isPasswordValid(String password) {
        return password.length() > 4;
    }

    private boolean isGenericValid(String anyString) {
        return anyString.length() > 4;
    }

    private boolean validatePassword() {
        return mPassword.getText().toString().equals(mPasswordConfirm.getText().toString());
    }

    public void setUser() {
        JSONObject jsonBody = new JSONObject();
        try {
            jsonBody.put("Nome__c", user.getName());
            jsonBody.put("CPF__c", user.getCpf());
            jsonBody.put("Email__c", user.getEmail());
            jsonBody.put("Telefone__c", user.getPhone());
            jsonBody.put("Senha__c", user.getPassword());
            final String requestBody = jsonBody.toString();

            request = new StringRequest(Request.Method.POST, salesforceAuth.getInstanceUrl() + "/services/data/v43.0/sobjects/Usuario__c", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);
                    JSONObject object = new JSONObject(response);
                    user.setId(object.get("id").toString());
                    order.setUser(user);
                    Toast.makeText(RegisterActivity.this, "Sucesso!", Toast.LENGTH_SHORT).show();
//                    Toast.makeText(RegisterActivity.this, "Id: " + order.getUserId(), Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), EventsActivity.class);
                    startActivity(intent);
                    Log.i("Response", object.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.i("Error: ", new String(error.networkResponse.data));

                if (error.networkResponse.statusCode == 400){
                    Toast.makeText(getApplicationContext(), "Usuário já existe.", Toast.LENGTH_LONG).show();
                }
                else
                    Toast.makeText(getApplicationContext(), "Você tem problemas com sua conexão. Teste novamente. "+ error.getCause(), Toast.LENGTH_LONG).show();
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
