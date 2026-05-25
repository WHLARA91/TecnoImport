package com.ti.tecnoimport.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.models.CartItem;
import com.ti.tecnoimport.models.Product;

import java.util.List;

public class CompradorProductAdapter
        extends RecyclerView.Adapter<CompradorProductAdapter.ViewHolder> {

    private List<Product> listaProductos;

    public CompradorProductAdapter(List<Product> listaProductos) {
        this.listaProductos = listaProductos;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_comprador, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        if (listaProductos == null || position >= listaProductos.size()) return;

        Product product = listaProductos.get(position);

        // 🛑 Blindaje total contra errores de nulos o formatos de texto al pintar la celda
        try {
            if (product != null) {
                if (holder.txtNombre != null) {
                    holder.txtNombre.setText(product.getNombre() != null ? product.getNombre() : "Sin nombre");
                }
                if (holder.txtDescripcion != null) {
                    holder.txtDescripcion.setText(product.getDescripcion() != null ? product.getDescripcion() : "Sin descripción");
                }
                if (holder.txtPrecio != null) {
                    // Conversión ultra segura de double/int a String explícito
                    String precioFormateado = "$ " + String.valueOf(product.getPrecio());
                    holder.txtPrecio.setText(precioFormateado);
                }

                // 🖼️ Cargar imagen de forma segura
                if (holder.imgProducto != null) {
                    Glide.with(holder.itemView.getContext())
                            .load(product.getImagenUrl())
                            .placeholder(R.drawable.ic_launcher_background)
                            .error(R.drawable.ic_launcher_background)
                            .into(holder.imgProducto);
                }
            }
        } catch (Exception e) {
            Toast.makeText(holder.itemView.getContext(), "Error visualizando ítem: " + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

        if (holder.btnAgregarCarrito != null) {
            holder.btnAgregarCarrito.setOnClickListener(v -> {
                FirebaseFirestore db = FirebaseFirestore.getInstance();

                if (FirebaseAuth.getInstance().getCurrentUser() == null) {
                    Toast.makeText(v.getContext(), "Error: Sesión no válida. Intente reingresar.", Toast.LENGTH_LONG).show();
                    return;
                }

                String usuarioId = FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

                CartItem item = new CartItem(
                        product.getNombre(),
                        product.getDescripcion(),
                        product.getPrecio(),
                        usuarioId
                );

                db.collection("carrito")
                        .document(usuarioId)
                        .collection("productos")
                        .add(item)
                        .addOnSuccessListener(documentReference -> {
                            Toast.makeText(
                                    v.getContext(),
                                    "Producto agregado al carrito",
                                    Toast.LENGTH_SHORT
                            ).show();
                        })
                        .addOnFailureListener(e -> {
                            Toast.makeText(
                                    v.getContext(),
                                    "Error al agregar: " + e.getMessage(),
                                    Toast.LENGTH_LONG
                            ).show();
                        });
            });
        }
    }

    @Override
    public int getItemCount() {
        return listaProductos != null ? listaProductos.size() : 0;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView txtNombre;
        TextView txtDescripcion;
        TextView txtPrecio;
        ImageView imgProducto;
        MaterialButton btnAgregarCarrito;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            // El try-catch aquí evita que la app muera si un ID del XML item_product_comprador no coincide
            try {
                txtNombre = itemView.findViewById(R.id.txtNombreProducto);
                txtDescripcion = itemView.findViewById(R.id.txtDescripcionProducto);
                txtPrecio = itemView.findViewById(R.id.txtPrecioProducto);
                imgProducto = itemView.findViewById(R.id.imgProducto);
                btnAgregarCarrito = itemView.findViewById(R.id.btnAgregarCarrito);
            } catch (Exception e) {
                // Si falta un ID en el XML, se captura aquí en lugar de tumbar la app entera
            }
        }
    }
}