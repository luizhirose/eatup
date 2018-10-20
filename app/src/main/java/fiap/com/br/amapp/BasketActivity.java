package fiap.com.br.amapp;

import android.content.Intent;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;

import java.util.ArrayList;

import fiap.com.br.amapp.adapters.BasketAdapter;
import fiap.com.br.amapp.adapters.DishAdapter;
import fiap.com.br.amapp.icons.FontManager;
import fiap.com.br.amapp.model.Dish;
import fiap.com.br.amapp.model.Order;
import fiap.com.br.amapp.model.Payment;
import fiap.com.br.amapp.model.SalesforceAuth;

public class BasketActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private RecyclerView recycler;
    //private ArrayList<Dish> dishList = new ArrayList<Dish>();;
    private BasketAdapter adapter;
    private RequestQueue requestQueue;
    private StringRequest request;
    private Order order;
    private SalesforceAuth salesforceAuth;
    private BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_basket);

        order = Order.getInstance();

        recycler = (RecyclerView) findViewById(R.id.basketRecyclerView) ;
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        recycler.setLayoutManager(linearLayoutManager);

        adapter = new BasketAdapter(BasketActivity.this, order.getItemList());

        recycler.setAdapter(adapter);

        bottomNavigationView = (BottomNavigationView) findViewById(R.id.bottomNavigationFinishCart);

        order.refreshTotal();

        //bottomNavigationView.getMenu().getItem(1).setTitle("");

        bottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        if (item != null) {
            if (item.getItemId() == R.id.finishCart) {
                if(order.getItemList().size()==0){
                    Toast.makeText(getApplicationContext(), "Não há itens no Carrinho", Toast.LENGTH_LONG).show();
                }else {
                    order.refreshTotal();
                    Intent intent = new Intent(getApplicationContext(), PaymentActivity.class);
                    startActivity(intent);
                }
            }
        }
        return true;
    }



}
