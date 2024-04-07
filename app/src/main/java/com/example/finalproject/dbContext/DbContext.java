package com.example.finalproject.dbContext;

import androidx.room.Database;
import androidx.room.RoomDatabase;

import com.example.finalproject.Domain.User;
import com.example.finalproject.UserDAO;

@Database(entities = {User.class}, version = 1)
public abstract class DbContext extends RoomDatabase {
    public abstract UserDAO userDao();
}
