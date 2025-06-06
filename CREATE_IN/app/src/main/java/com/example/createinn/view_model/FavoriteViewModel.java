package com.example.createinn.view_model;

import android.app.Application;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.createinn.data.AppDatabase;
import com.example.createinn.data.ProductFavoriteDao;
import com.example.createinn.model.FavoritProduct;

import java.util.List;

public class FavoriteViewModel extends AndroidViewModel {
    private final LiveData<List<FavoritProduct>> allFavorites;
     private final ProductFavoriteDao productFavoriteDao;

    public FavoriteViewModel(@NonNull Application application) {
        super(application);
        AppDatabase db = AppDatabase.getInstance(application);
        productFavoriteDao = db.productFavoritDao();  // ✅ Aquí lo inicializas
        allFavorites = productFavoriteDao.getAllFavorites();
    }

    public LiveData<List<FavoritProduct>> getAllFavorites() {
        return allFavorites;
    }

    public void deleteFavorite(FavoritProduct product) {
        new Thread(() -> productFavoriteDao.delete(product)).start();
    }

    public void insertFavorite(FavoritProduct product) {
        new Thread(() -> productFavoriteDao.insert(product)).start();
    }
}