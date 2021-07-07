package com.tamer.alna99.watertabclient.view.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.tamer.alna99.watertabclient.R;
import com.tamer.alna99.watertabclient.model.Order;
import com.tamer.alna99.watertabclient.model.SharedPrefs;
import com.tamer.alna99.watertabclient.view.OrdersAdapter;

import java.util.ArrayList;

public class OldOrdersFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_old_orders, container, false);
        ArrayList<Order> orders = new ArrayList<>();
        RecyclerView rv = view.findViewById(R.id.rv);

        JsonArray jsonArray = SharedPrefs.getOrders(getContext());
        Gson gson = new Gson();
        for (int i = 0; i < jsonArray.size(); i++) {
            Order order = gson.fromJson(jsonArray.get(i), Order.class);
            orders.add(order);
        }

        OrdersAdapter adapter = new OrdersAdapter(orders);
        rv.setAdapter(adapter);
        return view;
    }
}