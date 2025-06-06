package com.example.createinn.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.createinn.R;
import com.example.createinn.model.FavoritProduct;

import java.util.List;

// En package com.example.createinn.adapter
public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {
    private List<FavoritProduct> favorites;
    private OnDeleteClickListener deleteListener;

    public interface OnDeleteClickListener {
        void onDeleteClick(FavoritProduct product);
    }

    public FavoriteAdapter(List<FavoritProduct> favorites, OnDeleteClickListener listener) {
        this.favorites = favorites;
        this.deleteListener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product_favorite, parent, false);
        return new ViewHolder(view);
    }


/*Corregir con los datos corresspondoientes*/
    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
     FavoritProduct product = favorites.get(position);
        holder.marca.setText(product.getMarca());
        holder.nombre.setText(product.getNombre());
        holder.hechoEn.setText(product.getHechoEn());
        holder.pais.setText(product.getPais());

        // Cargar imagen con Glide/Picasso
        Glide.with(holder.itemView.getContext())
                .load(product.getImagenUrl())
                .placeholder(R.drawable.no_image)
                .into(holder.imagenUrl);

        holder.btnDelete.setOnClickListener(v -> deleteListener.onDeleteClick(product));
    }

    @Override
    public int getItemCount() {
        return favorites.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imagenUrl;
        TextView marca, nombre, hechoEn, pais;
        ImageButton btnDelete;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            imagenUrl = itemView.findViewById(R.id.imagenUrl);
            marca = itemView.findViewById(R.id.marca);
            nombre = itemView.findViewById(R.id.nombre);
            hechoEn = itemView.findViewById(R.id.hecho_En);
            pais = itemView.findViewById(R.id.pais);
            btnDelete = itemView.findViewById(R.id.btn_delete);
        }
    }
}