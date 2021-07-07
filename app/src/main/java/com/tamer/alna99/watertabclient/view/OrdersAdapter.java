package com.tamer.alna99.watertabclient.view;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.tamer.alna99.watertabclient.R;
import com.tamer.alna99.watertabclient.model.Order;

import java.util.ArrayList;

public class OrdersAdapter extends RecyclerView.Adapter<OrdersAdapter.OrdersAdapterViewHolder> {
    private final ArrayList<Order> orders;

    public OrdersAdapter(ArrayList<Order> orders) {
        this.orders = orders;
    }

    @NonNull
    @Override
    public OrdersAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_layout, parent, false);
        return new OrdersAdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersAdapterViewHolder holder, int position) {
        holder.bind(orders.get(position));
    }

    @Override
    public int getItemCount() {
        return orders.size();
    }

    static class OrdersAdapterViewHolder extends RecyclerView.ViewHolder {
        TextView driverName, date;

        public OrdersAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            driverName = itemView.findViewById(R.id.driver_name);
            date = itemView.findViewById(R.id.date);
        }

        private void bind(Order order) {
            driverName.setText(order.getDriverName());
            date.setText(order.getCreatedAt());
        }
    }
}
