package com.example.promise.journalapp.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import com.example.promise.journalapp.Model.JournalEntry;

import java.util.List;

@Dao
public interface JournalDao {

    // inserts into the database
    @Insert
    void insertJournal(JournalEntry journalEntry);

    // gets all items from the database
    @Query("SELECT * FROM JournalEntry ORDER BY id")
    LiveData<List<JournalEntry>> loadAllJournal();

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateJournal(JournalEntry journalEntry);

    // deletes an item from the database
    @Delete
    void deleteJournal(JournalEntry journalEntry);

    // selects a particular item that needs to be updated
    @Query("SELECT * FROM JournalEntry WHERE id = :id")
    LiveData<JournalEntry> loadJournalById(int id);
}
