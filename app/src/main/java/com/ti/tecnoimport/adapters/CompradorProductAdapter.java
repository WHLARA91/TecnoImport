package com.ti.tecnoimport.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.models.Producto;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para listar el catálogo de productos tecnológicos disponibles para el rol Comprador.
 */
public class CompradorProductAdapter extends RecyclerView.Adapter<CompradorProductAdapter.ProductViewHolder> {

    public interface OnProductoActionListener {
        void onAgregarAlCarrito(Producto producto);
    }

    private final Context context;
    private final List<Producto> productos;
    private final OnProductoActionListener listener;

    public CompradorProductAdapter(Context context, List<Producto> productos, OnProductoActionListener listener) {
        this.context = context;
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ProductViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ajusta el nombre del layout de ítem si tiene otro nombre en res/layout/
        View view = LayoutInflater.from(context).inflate(R.layout.item_product_comprador, parent, false);
        return new ProductViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ProductViewHolder holder, int position) {
        Producto producto = productos.get(position);
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        holder.tvNombre.setText(producto.getNombre());
        holder.tvDescripcion.setText(producto.getDescripcion());
        holder.tvPrecio.setText(formatoMoneda.format(producto.getPrecio()));

        Glide.with(context)
                .load(producto.getImagenUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivProducto);

        holder.btnAgregarCarrito.setOnClickListener(v -> listener.onAgregarAlCarrito(producto));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    static class ProductViewHolder extends RecyclerView.ViewHolder {
        ImageView ivProducto;
        TextView tvNombre, tvDescripcion, tvPrecio;
        Button btnAgregarCarrito;

        public ProductViewHolder(@NonNull View itemView) {
            super(itemView);
            ivProducto = itemView.findViewById(R.id.ivProductoItem);
            tvNombre = itemView.findViewById(R.id.tvNombreProductoItem);
            tvDescripcion = itemView.findViewById(R.id.tvDescripcionProductoItem);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProductoItem);
            btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarritoItem);
        }
    }
}