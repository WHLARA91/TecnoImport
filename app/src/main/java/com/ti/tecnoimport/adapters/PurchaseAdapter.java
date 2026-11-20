package com.ti.tecnoimport.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.models.Orden;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para mostrar el historial de compras y transacciones confirmadas en Firestore.
 */
public class PurchaseAdapter extends RecyclerView.Adapter<PurchaseAdapter.PurchaseViewHolder> {

    private final Context context;
    private final List<Orden> ordenes;

    public PurchaseAdapter(Context context, List<Orden> ordenes) {
        this.context = context;
        this.ordenes = ordenes;
    }

    @NonNull
    @Override
    public PurchaseViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
        return new PurchaseViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PurchaseViewHolder holder, int position) {
        Orden orden = ordenes.get(position);
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());

        holder.tvReferencia.setText("Ref. Pago: " + orden.getReferenciaPago());
        holder.tvFecha.setText(sdf.format(new Date(orden.getFecha())));
        holder.tvDireccion.setText("Entrega: " + orden.getDireccionEntrega());
        holder.tvEstado.setText("Estado: " + orden.getEstado());
        holder.tvTotal.setText(formatoMoneda.format(orden.getTotal()));
    }

    @Override
    public int getItemCount() {
        return ordenes.size();
    }

    static class PurchaseViewHolder extends RecyclerView.ViewHolder {
        TextView tvReferencia, tvFecha, tvDireccion, tvEstado, tvTotal;

        public PurchaseViewHolder(@NonNull View itemView) {
            super(itemView);
            tvReferencia = itemView.findViewById(R.id.tvReferenciaOrden);
            tvFecha = itemView.findViewById(R.id.tvFechaOrden);
            tvDireccion = itemView.findViewById(R.id.tvDireccionOrden);
            tvEstado = itemView.findViewById(R.id.tvEstadoOrden);
            tvTotal = itemView.findViewById(R.id.tvTotalOrden);
        }
    }
}