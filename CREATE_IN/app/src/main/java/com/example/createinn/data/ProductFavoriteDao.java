// ProductFavoriteDao.java
package com.example.createinn.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.createinn.model.FavoritProduct;
import java.util.List;

@Dao  //  Anotación clave para Room
public interface ProductFavoriteDao {  //  Interfaz directa (sin clase contenedora)
    @Insert
    void insert(FavoritProduct product);

    @Delete
    void delete(FavoritProduct product);

    @Query("SELECT * FROM favoritproduct")
    LiveData<List<FavoritProduct>> getAllFavorites();  //  LiveData para observar cambios

    @Query("SELECT COUNT(*) FROM favoritproduct WHERE marca = :marca AND nombre = :nombre")
    int checkIfExists(String marca, String nombre);
}