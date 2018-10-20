package fiap.com.br.amapp.adapters;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;

import fiap.com.br.amapp.BasketActivity;
import fiap.com.br.amapp.MyClickListener;
import fiap.com.br.amapp.R;
import fiap.com.br.amapp.icons.FontManager;
import fiap.com.br.amapp.model.Dish;
import fiap.com.br.amapp.model.Order;

public class DishAdapter extends RecyclerView.Adapter<DishAdapter.DishesViewHolder>{
    private Context context;
    public static ArrayList<Dish> itens;
    int selectPos;
    private Button btnAdd;
    private Button btnRemove;
    private Button btnAccept;
    private TextView tvDescQuantity;
    private TextView tvQuantity;
    private Integer quantity;
    private FontManager fontManager;
    private Order order;

    public DishAdapter(Context context, ArrayList<Dish> dishList) {
        this.context = context;
        this.itens = dishList;
        order = Order.getInstance();
    }

    @NonNull
    @Override
    public DishesViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.dish,parent,false);
        DishesViewHolder vh = new DishesViewHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull DishesViewHolder holder, int position) {
        Dish dish = itens.get(position);
        holder.dishName.setText(dish.getName());
        holder.dishDescription.setText(dish.getDescription());
        holder.dishName.setTextColor(Color.parseColor("#ff3036"));
        holder.dishDescription.setTextColor(Color.parseColor("#ffffff"));

        holder.setClickListener(new MyClickListener() {
            @Override
            public void onMyClick(int pos) {
                selectPos= pos;
                displayDialog();
            }
        });
    }


    private void displayDialog(){
        final Dialog dialog=new Dialog(context);
        dialog.setTitle("Produto");
        dialog.setContentView(R.layout.basket);
        dialog.setCancelable(true);

        quantity=0;

        fontManager = new FontManager();

        Typeface iconFont = FontManager.getIcons(FontManager.FONTAWESOME, context);
        FontManager.markAsIconContainer(dialog.findViewById(R.id.btnAddToCart), iconFont);
        FontManager.markAsIconContainer(dialog.findViewById(R.id.btnRemoveFromCart), iconFont);

        tvDescQuantity= (TextView) dialog.findViewById(R.id.tvDescQuantity);
        tvQuantity= (TextView) dialog.findViewById(R.id.tvQuantity);
        btnAdd= (Button) dialog.findViewById(R.id.btnAddToCart);
        btnRemove= (Button) dialog.findViewById(R.id.btnRemoveFromCart);
        btnAccept= (Button) dialog.findViewById(R.id.btnAcceptCartQuantity);

        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quantity  = Integer.parseInt(tvQuantity.getText().toString());

                    if(quantity<15){
                        quantity++;
                    }else {
                        Toast.makeText(context, "Você atingiu o limite deste item!", Toast.LENGTH_LONG).show();
                    }

                    tvQuantity.setText(Integer.toString(quantity));

                }catch (Exception e){
                    return;
                }
            }
        });
        btnRemove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    quantity  = Integer.parseInt(tvQuantity.getText().toString());

                    if(quantity>0){
                        quantity--;
                    }

                    tvQuantity.setText(Integer.toString(quantity));
                }catch (Exception e){
                    return;
                }
            }
        });

        btnAccept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(quantity>0){
                    Dish d = itens.get(selectPos);
                    Boolean itemFound =false;

                    for (Dish myDish: order.getItemList()) {
                        if(myDish.getProdutoId().equals(d.getProdutoId())){
                            Integer newQuantity = quantity+myDish.getQuantity();

                            myDish.setQuantity((newQuantity>15? 15: newQuantity));
                            itemFound=true;
                            break;
                        }
                    }

                    if(itemFound){
                        Toast.makeText(context, "Seu carrinho foi atualizado", Toast.LENGTH_SHORT).show();
                    }else {
                        d.setQuantity(quantity);

                        order.getItemList().add(d);
                        Toast.makeText(context, "Item adicionado ao Carrinho", Toast.LENGTH_LONG).show();
                    }
                }

                dialog.dismiss();
            }
        });

        dialog.show();
    }

    @Override
    public int getItemCount() {
        return itens.size();
    }

    public static class DishesViewHolder extends RecyclerView.ViewHolder implements  View.OnClickListener{
        TextView dishName;
        TextView dishDescription;
        MyClickListener clickListener;

        public DishesViewHolder(View itemView) {
            super(itemView);
            dishName = (TextView) itemView.findViewById(R.id.dishName);
            dishDescription = (TextView) itemView.findViewById(R.id.dishDescription);

            itemView.setOnClickListener(this);
        }

        public void setClickListener(MyClickListener clickListener)
        {
            this.clickListener=clickListener;
        }
        @Override
        public void onClick(View view) {
            this.clickListener.onMyClick(getLayoutPosition());
        }
    }
}
