package com.example.promise.journalapp;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.example.promise.journalapp.Activity.AddJournalActivity;
import com.example.promise.journalapp.Activity.GoogleSignInActivity;
import com.example.promise.journalapp.Adapter.JournalAdapter;
import com.example.promise.journalapp.Database.JournalDatabase;
import com.example.promise.journalapp.Model.JournalEntry;
import com.example.promise.journalapp.Util.AppExecutor;
import com.example.promise.journalapp.Viewmodel.MainViewModel;
import com.google.firebase.auth.FirebaseAuth;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @BindView(R.id.journal_rv)
    RecyclerView mRecyclerView;

    private JournalDatabase journalDatabase;

    // Firebase instance variables
    private FirebaseAuth mFirebaseAuth;
    FirebaseAuth.AuthStateListener mAuthListener;

    private JournalAdapter journalAdapter;

    @Override
    protected void onStart() {
        super.onStart();
        mFirebaseAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // sets views
        ButterKnife.bind(this);

        // Initialize Firebase Auth
        mFirebaseAuth = FirebaseAuth.getInstance();
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() == null) {
                    startActivity(new Intent(MainActivity.this, GoogleSignInActivity.class));
                }
            }
        };

        // init journal database
        journalDatabase = JournalDatabase.getsInstance(getApplicationContext());

        // set recycler views needs
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        journalAdapter = new JournalAdapter(this);
        mRecyclerView.setAdapter(journalAdapter);

        deleteItem();

        setUpViewModel();

    }

    private void deleteItem() {
        new ItemTouchHelper(new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
                return false;
            }

            @Override
            public void onSwiped(final RecyclerView.ViewHolder viewHolder, int direction) {
                AppExecutor.getsInstance().getDiskIO().execute(new Runnable() {
                    @Override
                    public void run() {
                        int position = viewHolder.getAdapterPosition();
                        List<JournalEntry> journalEntryList = journalAdapter.getJournalEntryList();
                        journalDatabase.journalDao().deleteJournal(journalEntryList.get(position));

                    }
                });
            }
        }).attachToRecyclerView(mRecyclerView);
    }

    @OnClick(R.id.fab)
    public void callAddActivity() {
        startActivity(new Intent(this, AddJournalActivity.class));
    }


    private void setUpViewModel() {
        MainViewModel mainViewModel = ViewModelProviders.of(this).get(MainViewModel.class);
        mainViewModel.getEntries().observe(this, new Observer<List<JournalEntry>>() {
            @Override
            public void onChanged(@Nullable List<JournalEntry> journalEntries) {
                Log.d(TAG, "onChanged: receiving data from view model");
                journalAdapter.setJournalEntryList(journalEntries);
                mRecyclerView.setAdapter(journalAdapter);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_sign_out) {
            mFirebaseAuth.signOut();
        }

        return super.onOptionsItemSelected(item);
    }
}
