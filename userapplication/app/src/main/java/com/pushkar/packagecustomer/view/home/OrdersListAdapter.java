package com.pushkar.packagecustomer.view.home;

import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.pushkar.packagecustomer.R;
import com.pushkar.packagecustomer.model.Order;
import com.pushkar.packagecustomer.utils.Constants;

import java.util.ArrayList;
import java.util.List;

public class OrdersListAdapter extends RecyclerView.Adapter<OrdersListAdapter.OrdersViewHolder> {
    private List<Order> listOfOrders = new ArrayList<>();;
    private LayoutInflater mInflater;
    private Context context;

    public OrdersListAdapter(Context context) {
        this.context = context;
        this.mInflater = LayoutInflater.from(context);
    }

    public void setData(List<Order> orderList){
        this.listOfOrders = orderList;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public OrdersViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = mInflater.inflate(R.layout.order_list_item,parent,false);
        return new OrdersViewHolder(itemView,this);
    }

    @Override
    public void onBindViewHolder(@NonNull OrdersViewHolder holder, int position) {
        Order currentOrder = listOfOrders.get(position);
        holder.trackingNumberTV.setText("Tracking#: "+currentOrder.getTrackingNumber());
        holder.statusTV.setText("Status: "+currentOrder.getPackageStatus().toString());
        holder.storeNumTV.setText("Store#: "+currentOrder.getStoreNum());
        holder.descriptionTV.setText("Description: "+currentOrder.getDescription());
        switch (currentOrder.getPackageStatus().toString()){
            case Constants.IN_TRANSIT_STATUS:
                holder.statusIV.setBackground(context.getDrawable(R.drawable.ic_local_shipping_black_24dp));
                break;
            case Constants.IN_STORE_STATUS:
                holder.statusIV.setBackground(context.getDrawable(R.drawable.ic_store_black_24dp));
                break;
            case Constants.DELIVERED_STATUS:
                holder.statusIV.setBackground(context.getDrawable(R.drawable.ic_sentiment_satisfied_black_24dp));
                break;
            default:
                holder.statusIV.setBackground(context.getDrawable(R.drawable.ic_not_interested_black_24dp));
        }
    }

    @Override
    public int getItemCount() {
        return listOfOrders.size();
    }

    public static class OrdersViewHolder extends RecyclerView.ViewHolder{
        public OrdersListAdapter adapter;
        public TextView trackingNumberTV;
        public TextView statusTV;
        public TextView storeNumTV;
        public ImageView statusIV;
        public TextView descriptionTV;
        public OrdersViewHolder(@NonNull View itemView, OrdersListAdapter adapter) {
            super(itemView);
            this.adapter = adapter;
            trackingNumberTV = itemView.findViewById(R.id.orderTrackingNumTV);
            statusTV = itemView.findViewById(R.id.orderStatusTV);
            storeNumTV = itemView.findViewById(R.id.orderStoreNumTV);
            statusIV = itemView.findViewById(R.id.ordersStatusIV);
            descriptionTV = itemView.findViewById(R.id.descriptionTV);
        }
    }
}
