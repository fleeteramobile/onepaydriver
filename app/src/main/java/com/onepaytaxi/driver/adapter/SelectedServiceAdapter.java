package com.onepaytaxi.driver.adapter;



import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.onepaytaxi.driver.R;
import com.onepaytaxi.driver.utils.ServiceItem;

import java.util.List;

public class SelectedServiceAdapter extends RecyclerView.Adapter<SelectedServiceAdapter.ServiceViewHolder> {

    private List<ServiceItem> selectedServices;
    private OnItemRemoveListener onItemRemoveListener;

    public interface OnItemRemoveListener {
        void onRemoveItem(ServiceItem item);
    }

    public SelectedServiceAdapter(List<ServiceItem> selectedServices, OnItemRemoveListener onItemRemoveListener) {
        this.selectedServices = selectedServices;
        this.onItemRemoveListener = onItemRemoveListener;
    }

    @NonNull
    @Override
    public ServiceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_selected_service, parent, false);
        return new ServiceViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ServiceViewHolder holder, int position) {
        ServiceItem service = selectedServices.get(position);
        holder.serviceName.setText(service.getService_type());
        holder.serviceAmount.setText(String.format("%.2f", service.getService_amount()));

        holder.removeButton.setOnClickListener(v -> {
            if (onItemRemoveListener != null) {
                onItemRemoveListener.onRemoveItem(service);
            }
        });
    }

    @Override
    public int getItemCount() {
        return selectedServices.size();
    }

    public static class ServiceViewHolder extends RecyclerView.ViewHolder {
        TextView serviceName;
        TextView serviceAmount;
        ImageButton removeButton;

        public ServiceViewHolder(@NonNull View itemView) {
            super(itemView);
            serviceName = itemView.findViewById(R.id.tv_selected_service_name);
            serviceAmount = itemView.findViewById(R.id.tv_selected_service_amount);
            removeButton = itemView.findViewById(R.id.btn_remove_service);
        }
    }
}
