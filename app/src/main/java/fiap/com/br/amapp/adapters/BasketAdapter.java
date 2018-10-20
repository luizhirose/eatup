package fiap.com.br.amapp.adapters;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fiap.com.br.amapp.R;
import fiap.com.br.amapp.model.Dish;
import fiap.com.br.amapp.model.Order;

public class BasketAdapter extends  RecyclerView.Adapter<BasketAdapter.BasketViewHolder>{
    private Context context;
    private ArrayList<Dish> itens;
    int selectPos;

    public BasketAdapter(Context context, ArrayList<Dish> dishList) {
        this.context = context;
        this.itens = dishList;
    }

    @NonNull
    @Override
    public BasketViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.cart_item,parent,false);
        BasketViewHolder vh = new BasketViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull BasketViewHolder holder,final int position) {
        Dish dish = itens.get(position);
        holder.dishName .setText(dish.getName());
        holder.dishDescription.setText(dish.getDescription());
        holder.dishQuantity.setText("Quantidade: "+Integer.toString(dish.getQuantity()));
        holder.dishPrice.setText("Valor : R$ "+Double.toString(dish.getPrice()* dish.getQuantity().doubleValue()));


        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteItem(position);
            }
        });

    }

    public void deleteItem(int position){
        Dish dish = itens.get(position);

        if(itens.remove(dish))
        {
            Order order = Order.getInstance();
            order.setTotal(order.getTotal()-(dish.getPrice())*dish.getQuantity());
            Toast.makeText(context,"Item removido",Toast.LENGTH_SHORT).show();
        }else {
            Toast.makeText(context,"Não foi possível remover do Carrinho",Toast.LENGTH_SHORT).show();
        }

        this.notifyItemRemoved(position);
        notifyItemRangeChanged(position,itens.size());
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public static class BasketViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView dishName;
        TextView dishDescription;
        TextView dishQuantity;
        TextView dishPrice;
        ImageView delete;


        public BasketViewHolder(View itemView) {
            super(itemView);
            dishName = (TextView) itemView.findViewById(R.id.tvcartItemTitle);
            dishDescription = (TextView) itemView.findViewById(R.id.tvcartItemContent);
            dishQuantity = (TextView) itemView.findViewById(R.id.tvcartItemQuantity);
            dishPrice = (TextView) itemView.findViewById(R.id.tvCartItemPrice);
            delete = (ImageView) itemView.findViewById(R.id.ivDelete);

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View view) {

        }
    }

}
