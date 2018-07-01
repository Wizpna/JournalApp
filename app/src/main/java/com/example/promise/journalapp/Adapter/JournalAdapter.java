package com.example.promise.journalapp.Adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.promise.journalapp.Activity.AddJournalActivity;
import com.example.promise.journalapp.Model.JournalEntry;
import com.example.promise.journalapp.R;

import java.util.List;

public class JournalAdapter extends RecyclerView.Adapter<JournalAdapter.JournalViewHolder> {

    private static final String TAG = "JournalAdapter";

    private List<JournalEntry> journalEntryList;
    private int id;
    private Context context;

    public JournalAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public JournalViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.journal_layout, parent, false);
        return new JournalViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull JournalViewHolder holder, int position) {
        holder.mTitle.setText(journalEntryList.get(position).getTitle());
        holder.mDate.setText(journalEntryList.get(position).getDate().toString());
    }

    @Override
    public int getItemCount() {
        if (journalEntryList == null)
            return 0;
        return journalEntryList.size();
    }

    public List<JournalEntry> getJournalEntryList() {
        return journalEntryList;
    }

    public void setJournalEntryList(List<JournalEntry> entryList) {
        journalEntryList = entryList;
        notifyDataSetChanged();
    }

    public class JournalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView mTitle, mDate;

        public JournalViewHolder(View itemView) {
            super(itemView);
            mTitle = (TextView) itemView.findViewById(R.id.journal_title);
            mDate = (TextView) itemView.findViewById(R.id.journal_date);

            // set click listener
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            Context context = view.getContext();
            JournalEntry journalEntry = journalEntryList.get(getLayoutPosition());
            Intent intent = new Intent(context.getApplicationContext(), AddJournalActivity.class);
            intent.putExtra("extraTaskId", journalEntry);
            context.startActivity(intent);
        }
    }
}
