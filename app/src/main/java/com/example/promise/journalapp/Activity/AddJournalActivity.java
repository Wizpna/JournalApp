package com.example.promise.journalapp.Activity;

import android.arch.lifecycle.Observer;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.example.promise.journalapp.Database.JournalDatabase;
import com.example.promise.journalapp.Model.JournalEntry;
import com.example.promise.journalapp.R;
import com.example.promise.journalapp.Util.AppExecutor;
import com.example.promise.journalapp.Viewmodel.AddTaskViewModel;
import com.example.promise.journalapp.Viewmodel.AddTaskViewModelFactory;

import java.util.Date;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class AddJournalActivity extends AppCompatActivity {

    private static final String TAG = "AddJournalActivity";

    @BindView(R.id.et_add_title)
    EditText mEnterTitle;
    @BindView(R.id.et_add_details)
    EditText mEnterDetails;
    @BindView(R.id.txt_show_title)
    TextView mShowTitle;
    @BindView(R.id.txt_show_details)
    TextView mShowDetails;
    @BindView(R.id.btn_add)
    Button mSaveButton;

    private JournalDatabase journalDatabase;
    private static final int DEFAULT_ID = 1;
    private static final String EXTRA_TASK_ID = "extraTaskId";
    private static final String INSTANCE_TASK_ID = "instanceTaskId";
    private int mTaskID = DEFAULT_ID;
    private boolean toUpdate = false;
    private JournalEntry receiveIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_journal);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // init views
        ButterKnife.bind(this);

        if (savedInstanceState != null && savedInstanceState.containsKey(INSTANCE_TASK_ID)) {
            mTaskID = savedInstanceState.getInt(INSTANCE_TASK_ID, DEFAULT_ID);
        }

        // gets the database instance
        journalDatabase = JournalDatabase.getsInstance(getApplicationContext());

        // add textWatchers
        mEnterTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged: " + charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: " + charSequence.toString());
                mShowTitle.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged: " + editable.toString());
            }
        });

        mEnterDetails.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "beforeTextChanged: " + charSequence.toString());
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                Log.d(TAG, "onTextChanged: " + charSequence.toString());
                mShowDetails.setText(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                Log.d(TAG, "afterTextChanged: " + editable.toString());
            }
        });

        Intent intent = getIntent();
        if (intent != null && intent.hasExtra(EXTRA_TASK_ID)) {

            mSaveButton.setText("Update");

            receiveIntent = getIntent().getParcelableExtra(EXTRA_TASK_ID);

            AddTaskViewModelFactory factory = new AddTaskViewModelFactory(journalDatabase, receiveIntent.getId());

            final AddTaskViewModel viewModel
                    = ViewModelProviders.of(this, factory).get(AddTaskViewModel.class);

            if (mTaskID == DEFAULT_ID) {
                viewModel.getTask().observe(this, new Observer<JournalEntry>() {
                    @Override
                    public void onChanged(@Nullable JournalEntry journalEntry) {
                        viewModel.getTask().removeObserver(this);
                        // populates the UI
                        populateUI(journalEntry);
                        // set value of toUpdate to true
                        toUpdate = true;
                    }
                });
            }
        }
    }

    @OnClick(R.id.btn_add)
    public void saveJournal() {
        String title = mEnterTitle.getText().toString();
        String details = mEnterDetails.getText().toString();
        Date date = new Date();

        if (TextUtils.isEmpty(title) || TextUtils.isEmpty(details)) {
            finish();
        } else {
            final JournalEntry entry = new JournalEntry(title, details, date);
            AppExecutor.getsInstance().getDiskIO().execute(new Runnable() {
                @Override
                public void run() {
                    if (toUpdate) {
                        Log.d(TAG, "run: update this file");
                        entry.setId(receiveIntent.getId());
                        journalDatabase.journalDao().updateJournal(entry);
                    } else {
                        Log.d(TAG, "run: insert new file");
                        journalDatabase.journalDao().insertJournal(entry);
                    }
                    finish();
                }
            });
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.putInt(INSTANCE_TASK_ID, mTaskID);
        super.onSaveInstanceState(outState);
    }

    private void populateUI(JournalEntry journalEntry) {
        if (journalEntry == null) {
            return;
        }
        mEnterTitle.setText(journalEntry.getTitle());
        mEnterDetails.setText(journalEntry.getDescription());
    }
}
