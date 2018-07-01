package com.example.promise.journalapp.Viewmodel;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.ViewModel;

import com.example.promise.journalapp.Database.JournalDatabase;
import com.example.promise.journalapp.Model.JournalEntry;

public class AddTaskViewModel extends ViewModel {

    private LiveData<JournalEntry> task;

    public AddTaskViewModel(JournalDatabase journalDatabase, int id) {
        task = journalDatabase.journalDao().loadJournalById(id);
    }

    public LiveData<JournalEntry> getTask() {
        return task;
    }
}
