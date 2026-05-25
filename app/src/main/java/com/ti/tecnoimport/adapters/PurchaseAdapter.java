package com.ti.tecnoimport.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ti.tecnoimport.R;
import com.ti.tecnoimport.models.Purchase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class PurchaseAdapter
        extends RecyclerView.Adapter<PurchaseAdapter.ViewHolder> {

    private List<Purchase> listaCompras;

    public PurchaseAdapter(List<Purchase> listaCompras) {

        this.listaCompras = listaCompras;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_purchase,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Purchase purchase = listaCompras.get(position);

        holder.txtNombre.setText(
                purchase.getNombre()
        );

        holder.txtDescripcion.setText(
                purchase.getDescripcion()
        );

        holder.txtPrecio.setText(
                "$ " + purchase.getPrecio()
        );

        SimpleDateFormat sdf =
                new SimpleDateFormat(
                        "dd/MM/yyyy HH:mm",
                        Locale.getDefault()
                );

        String fecha =
                sdf.format(
                        new Date(purchase.getFecha())
                );

        holder.txtFecha.setText(fecha);
    }

    @Override
    public int getItemCount() {
        return listaCompras.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtDescripcion;
        TextView txtPrecio;
        TextView txtFecha;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre =
                    itemView.findViewById(
                            R.id.txtNombreCompra
                    );

            txtDescripcion =
                    itemView.findViewById(
                            R.id.txtDescripcionCompra
                    );

            txtPrecio =
                    itemView.findViewById(
                            R.id.txtPrecioCompra
                    );

            txtFecha =
                    itemView.findViewById(
                            R.id.txtFechaCompra
                    );
        }
    }
}
