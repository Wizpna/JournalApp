package com.example.promise.journalapp.Viewmodel;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.support.annotation.NonNull;

import com.example.promise.journalapp.Database.JournalDatabase;
import com.example.promise.journalapp.Model.JournalEntry;

import java.util.List;

public class MainViewModel extends AndroidViewModel {

    private static final String TAG = "MainViewModel";

    private LiveData<List<JournalEntry>> entries;

    public MainViewModel(@NonNull Application application) {
        super(application);
        JournalDatabase journalDatabase = JournalDatabase.getsInstance(this.getApplication());
        entries = journalDatabase.journalDao().loadAllJournal();
    }

    public LiveData<List<JournalEntry>> getEntries(){
        return entries;
    }

}
