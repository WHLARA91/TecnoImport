package com.ti.tecnoimport.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.models.ItemCarrito;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para visualizar y eliminar los artículos agregados al carrito de compras.
 */
public class CartAdapter extends RecyclerView.Adapter<CartAdapter.CartViewHolder> {

    public interface OnCartItemDeleteListener {
        void onEliminarItem(ItemCarrito item);
    }

    private final Context context;
    private final List<ItemCarrito> items;
    private final OnCartItemDeleteListener deleteListener;

    public CartAdapter(Context context, List<ItemCarrito> items, OnCartItemDeleteListener deleteListener) {
        this.context = context;
        this.items = items;
        this.deleteListener = deleteListener;
    }

    @NonNull
    @Override
    public CartViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_carrito, parent, false);
        return new CartViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CartViewHolder holder, int position) {
        ItemCarrito item = items.get(position);
        NumberFormat formato = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        holder.tvNombre.setText(item.getNombre());
        holder.tvCantidad.setText("Cantidad: " + item.getCantidad());
        holder.tvSubtotal.setText(formato.format(item.calcularSubtotal()));

        Glide.with(context)
                .load(item.getImagenUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivImagen);

        holder.btnEliminar.setOnClickListener(v -> deleteListener.onEliminarItem(item));
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    static class CartViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre, tvCantidad, tvSubtotal;
        ImageButton btnEliminar;

        public CartViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivItemCarrito);
            tvNombre = itemView.findViewById(R.id.tvNombreItemCarrito);
            tvCantidad = itemView.findViewById(R.id.tvCantidadItemCarrito);
            tvSubtotal = itemView.findViewById(R.id.tvSubtotalItemCarrito);
            btnEliminar = itemView.findViewById(R.id.btnEliminarItemCarrito);
        }
    }
}