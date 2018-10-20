package fiap.com.br.amapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

import fiap.com.br.amapp.DishesActivity;
import fiap.com.br.amapp.MyClickListener;
import fiap.com.br.amapp.R;
import fiap.com.br.amapp.model.Establishment;
import fiap.com.br.amapp.model.Order;

public class EstablishmentAdapter extends RecyclerView.Adapter<EstablishmentAdapter.EstablishmentViewHolder> {
    private Context context;
    private ArrayList<Establishment> itens;
    int selectedPos;

    public EstablishmentAdapter(Context context, ArrayList<Establishment> itens) {
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public EstablishmentViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.establishment, parent, false);
        EstablishmentViewHolder vh = new EstablishmentViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final EstablishmentViewHolder holder, int position) {
        final Establishment establishment = itens.get(position);
        holder.name.setText(establishment.getName());
        holder.description.setText(establishment.getDescription());
        holder.name.setTextColor(Color.parseColor("#ff3036"));
        holder.description.setTextColor(Color.parseColor("#ffffff"));

        holder.setClickListener(new MyClickListener() {
            @Override
            public void onMyClick(int pos) {
                selectedPos=pos;
                holder.order.setEstablishmentId(establishment.getId());
            }
        });
    }

    @Override
    public int getItemCount() {
        return itens != null? itens.size() : 0;
    }

    public static class EstablishmentViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        TextView description;
        MyClickListener clickListener;
        Order order = Order.getInstance();

        public EstablishmentViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.dishName);
            description = (TextView) itemView.findViewById(R.id.establishmentDescription);
            itemView.setOnClickListener(this);
        }

        public void setClickListener(MyClickListener clickListener)
        {
            this.clickListener=clickListener;
        }

        @Override
        public void onClick(View view) {
            this.clickListener.onMyClick(getLayoutPosition());
            Intent intent = new Intent(view.getContext(), DishesActivity.class);
            view.getContext().startActivity(intent);
        }
    }

}
