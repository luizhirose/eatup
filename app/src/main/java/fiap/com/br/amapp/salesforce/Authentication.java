package fiap.com.br.amapp.salesforce;

import android.content.Context;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
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

import java.util.HashMap;
import java.util.Map;

import fiap.com.br.amapp.model.SalesforceAuth;

public class Authentication  {

    private static final String URL_AUTH = "https://login.salesforce.com/services/oauth2/token?grant_type=password&username=luizupload@gmail.com&password=senhasecreta1nMhX4g18vliFnWK3zXAgTakA&client_id=3MVG9zlTNB8o8BA2l.yTVFhQotILnjs.NoLPrbOt2gnfN68i_zeR6nLxNBRs3SgMSJBX5_5YBEGJas2VEAlvm&client_secret=1209714128997971937";
    private StringRequest request;
    private RequestQueue requestQueue;
    private JSONObject authUser;

    public Authentication() {
    }

    public void getAuth(final Context context) {
        requestQueue = Volley.newRequestQueue(context);
        request = new StringRequest(Request.Method.POST, URL_AUTH, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    // Caso exista alguma politica de acesso ou restrição de dados em HTTPS é necessário incluir as duas linhas abaixo para ter um acesso total ao conteúdo da resposta do RESTSErvice
                    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
                    StrictMode.setThreadPolicy(policy);

                    authUser = new JSONObject(response);
                    SalesforceAuth auth = SalesforceAuth.getInstance();
                    auth.setAccessToken(authUser.getString("access_token"));
                    auth.setInstanceUrl(authUser.getString("instance_url"));
                    auth.setId(authUser.getString("id"));
                    auth.setTokenType(authUser.getString("token_type"));
                    auth.setSignature(authUser.getString("issued_at"));
                    auth.setIssuedAt(authUser.getString("signature"));
                    Log.i("Authentication", authUser.toString());

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(context, "Você tem problemas com sua conexão. Teste novamente", Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> hashMap = new HashMap<String, String>();

                return hashMap;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();

                headers.put("Content-Type", "application/json");
                headers.put("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
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
