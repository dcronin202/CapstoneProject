package com.example.android.inagiffy.database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.android.inagiffy.data.Gif;

import java.util.List;

@Dao
public interface GifDao {

    @Query("SELECT * FROM favorites")
    LiveData<List<Gif>> loadAllGifImages();

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertGif(Gif gif);

    @Delete
    void removeGif(Gif gif);

}
