package com.ti.tecnoimport.adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.ti.tecnoimport.R;
import com.ti.tecnoimport.comprador.CartActivity;
import com.ti.tecnoimport.models.CartItem;

import java.util.List;

public class CartAdapter
        extends RecyclerView.Adapter<CartAdapter.ViewHolder> {

    private List<CartItem> listaCarrito;

    private Context context;

    public CartAdapter(
            List<CartItem> listaCarrito,
            Context context
    ) {

        this.listaCarrito = listaCarrito;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_cart,
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

        CartItem item = listaCarrito.get(position);

        holder.txtNombre.setText(item.getNombre());

        holder.txtDescripcion.setText(
                item.getDescripcion()
        );

        holder.txtPrecio.setText(
                "$ " + item.getPrecio()
        );

        holder.btnEliminar.setOnClickListener(v -> {

            AlertDialog.Builder builder =
                    new AlertDialog.Builder(context);

            builder.setTitle("Eliminar");

            builder.setMessage(
                    "¿Desea eliminar este producto?"
            );

            builder.setPositiveButton(
                    "Sí",
                    (dialog, which) -> eliminarProducto(item, position)
            );

            builder.setNegativeButton(
                    "Cancelar",
                    null
            );

            builder.show();

        });
    }

    private void eliminarProducto(
            CartItem item,
            int position
    ) {

        String usuarioId =
                FirebaseAuth.getInstance()
                        .getCurrentUser()
                        .getUid();

        FirebaseFirestore.getInstance()
                .collection("carrito")
                .document(usuarioId)
                .collection("productos")
                .document(item.getId())
                .delete()
                .addOnSuccessListener(unused -> {

                    listaCarrito.remove(position);

                    notifyItemRemoved(position);

                    if (context instanceof CartActivity) {

                        ((CartActivity) context)
                                .actualizarTotal();
                    }

                    Toast.makeText(
                            context,
                            "Producto eliminado",
                            Toast.LENGTH_SHORT
                    ).show();

                });
    }

    @Override
    public int getItemCount() {
        return listaCarrito.size();
    }

    public static class ViewHolder
            extends RecyclerView.ViewHolder {

        TextView txtNombre;
        TextView txtDescripcion;
        TextView txtPrecio;

        MaterialButton btnEliminar;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            txtNombre =
                    itemView.findViewById(
                            R.id.txtNombreCarrito
                    );

            txtDescripcion =
                    itemView.findViewById(
                            R.id.txtDescripcionCarrito
                    );

            txtPrecio =
                    itemView.findViewById(
                            R.id.txtPrecioCarrito
                    );

            btnEliminar =
                    itemView.findViewById(
                            R.id.btnEliminar
                    );
        }
    }
}