package fiap.com.br.amapp.adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import fiap.com.br.amapp.EstablishmentActivity;
import fiap.com.br.amapp.EventsActivity;
import fiap.com.br.amapp.MyClickListener;
import fiap.com.br.amapp.R;
import fiap.com.br.amapp.model.Establishment;
import fiap.com.br.amapp.model.Event;
import fiap.com.br.amapp.model.Order;

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventViewHolder> {

    private Context context;
    private ArrayList<Event> itens;
    int selectedPos;

    public EventAdapter(Context context, ArrayList<Event> itens){
        this.context = context;
        this.itens = itens;
    }

    @NonNull
    @Override
    public EventViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.event, parent, false);
        EventViewHolder vh = new EventViewHolder(v);

        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull final EventViewHolder holder, int position) {
        final Event event = itens.get(position);
        holder.name.setText(event.getName());
        holder.description.setText(event.getDescription());
        holder.name.setTextColor(Color.parseColor("#ff3036"));
        holder.description.setTextColor(Color.parseColor("#ffffff"));

        holder.setClickListener(new MyClickListener() {
            @Override
            public void onMyClick(int pos) {
                selectedPos=pos;
                holder.order.setEventId(event.getId());
            }
        });

    }

    @Override
    public int getItemCount() {
        return itens != null? itens.size() : 0;
    }

    public static class EventViewHolder  extends RecyclerView.ViewHolder implements View.OnClickListener{
        TextView name;
        TextView description;
        MyClickListener clickListener;
        Order order = Order.getInstance();

        public EventViewHolder(View itemView) {
            super(itemView);

            name = (TextView) itemView.findViewById(R.id.name);
            description = (TextView) itemView.findViewById(R.id.description);
            itemView.setOnClickListener(this);
        }

        public void setClickListener(MyClickListener clickListener)
        {
            this.clickListener=clickListener;
        }

        @Override
        public void onClick(View v) {
            this.clickListener.onMyClick(getLayoutPosition());
            Intent intent = new Intent(v.getContext(), EstablishmentActivity.class);
            v.getContext().startActivity(intent);
        }
    }
}
