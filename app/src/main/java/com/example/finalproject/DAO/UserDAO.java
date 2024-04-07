package com.example.finalproject;

import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Dao;

import com.example.finalproject.Domain.User;

import java.util.List;

@Dao
public interface UserDAO {
    @Query("SELECT * FROM User")
    List<User> getAll();

    @Query("SELECT * FROM User WHERE name = :name COLLATE NOCASE")
    User getUser(String name);

    @Insert
    void insertAll(User users);
}
