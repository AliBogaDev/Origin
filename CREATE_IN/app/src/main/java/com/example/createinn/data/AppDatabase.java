package com.example.createinn.data;

import android.content.Context;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

import com.example.createinn.model.FavoritProduct;

@Database(entities = {FavoritProduct.class}, version = 1)
public abstract class AppDatabase extends RoomDatabase {
    public abstract ProductFavoriteDao productFavoritDao();
//la base de datos
    private static volatile AppDatabase INSTANCE;

    public static AppDatabase getInstance(Context context) {
        if (INSTANCE == null) {
            synchronized (AppDatabase.class) {
                if (INSTANCE == null) {
                    INSTANCE = Room.databaseBuilder(
                            context.getApplicationContext(),
                            AppDatabase.class,
                            "favorites_db"
                    ).build();
                }
            }
        }
        return INSTANCE;
    }
}