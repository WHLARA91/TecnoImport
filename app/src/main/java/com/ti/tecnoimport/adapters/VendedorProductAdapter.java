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
import com.ti.tecnoimport.models.Producto;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

/**
 * Adaptador para visualizar el inventario de productos del rol Vendedor.
 * Permite disparar las acciones de edición y eliminación sobre cada artículo publicado.
 */
public class VendedorProductAdapter extends RecyclerView.Adapter<VendedorProductAdapter.VendedorViewHolder> {

    /**
     * Interfaz de eventos para gestionar las operaciones CRUD desde cada tarjeta de producto.
     */
    public interface OnVendedorProductoListener {
        void onEditarProducto(Producto producto);
        void onEliminarProducto(Producto producto);
    }

    private final Context context;
    private final List<Producto> productos;
    private final OnVendedorProductoListener listener;

    /**
     * Constructor del adaptador del vendedor.
     *
     * @param context   Contexto de la actividad anfitriona.
     * @param productos Lista de productos asociados al vendedor autenticado.
     * @param listener  Manejador de clics para editar o eliminar.
     */
    public VendedorProductAdapter(Context context, List<Producto> productos, OnVendedorProductoListener listener) {
        this.context = context;
        this.productos = productos;
        this.listener = listener;
    }

    @NonNull
    @Override
    public VendedorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // Ajusta el nombre del layout si tu ítem de vendedor tiene otro nombre en res/layout/
        View view = LayoutInflater.from(context).inflate(R.layout.item_producto_vendedor, parent, false);
        return new VendedorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VendedorViewHolder holder, int position) {
        Producto producto = productos.get(position);
        NumberFormat formatoMoneda = NumberFormat.getCurrencyInstance(new Locale("es", "CO"));

        holder.tvNombre.setText(producto.getNombre());
        holder.tvPrecio.setText(formatoMoneda.format(producto.getPrecio()));
        holder.tvStock.setText("Stock disponible: " + producto.getStock());

        Glide.with(context)
                .load(producto.getImagenUrl())
                .placeholder(R.mipmap.ic_launcher)
                .into(holder.ivImagen);

        holder.btnEditar.setOnClickListener(v -> listener.onEditarProducto(producto));
        holder.btnEliminar.setOnClickListener(v -> listener.onEliminarProducto(producto));
    }

    @Override
    public int getItemCount() {
        return productos.size();
    }

    /**
     * ViewHolder que mantiene las referencias visuales de cada producto del vendedor.
     */
    static class VendedorViewHolder extends RecyclerView.ViewHolder {
        ImageView ivImagen;
        TextView tvNombre, tvPrecio, tvStock;
        ImageButton btnEditar, btnEliminar;

        public VendedorViewHolder(@NonNull View itemView) {
            super(itemView);
            ivImagen = itemView.findViewById(R.id.ivProductoVendedor);
            tvNombre = itemView.findViewById(R.id.tvNombreProductoVendedor);
            tvPrecio = itemView.findViewById(R.id.tvPrecioProductoVendedor);
            tvStock = itemView.findViewById(R.id.tvStockProductoVendedor);
            btnEditar = itemView.findViewById(R.id.btnEditarProducto);
            btnEliminar = itemView.findViewById(R.id.btnEliminarProducto);
        }
    }
}
