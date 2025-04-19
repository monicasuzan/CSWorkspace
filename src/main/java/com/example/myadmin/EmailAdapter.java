package com.example.myadmin;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class EmailAdapter extends RecyclerView.Adapter<EmailAdapter.EmailViewHolder> {

    private final Context context;
    private final List<Email> emailList;
    private final String loggedInUserEmail;
    private final OnEmailActionListener emailActionListener;

    public interface OnEmailActionListener {
        void onMoveToTrash(Email email);
    }

    public EmailAdapter(Context context, List<Email> emailList, String loggedInUserEmail, OnEmailActionListener listener) {
        this.context = context;
        this.emailList = emailList;
        this.loggedInUserEmail = loggedInUserEmail;
        this.emailActionListener = listener;
    }

    @NonNull
    @Override
    public EmailViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.email_item, parent, false);
        return new EmailViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull EmailViewHolder holder, int position) {
        Email email = emailList.get(position);

        holder.subjectTextView.setText(email.getSubject());
        holder.senderTextView.setText("From: " + email.getSender());
        holder.timestampTextView.setText(formatTimestamp(email.getTimestamp()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, EmailDetailActivity.class);
            intent.putExtra("email", email);
            context.startActivity(intent);
        });

        holder.deleteButton.setOnClickListener(v -> emailActionListener.onMoveToTrash(email));
    }

    @Override
    public int getItemCount() {
        return emailList.size();
    }

    public static class EmailViewHolder extends RecyclerView.ViewHolder {
        TextView subjectTextView, senderTextView, timestampTextView;
        ImageButton deleteButton;

        public EmailViewHolder(@NonNull View itemView) {
            super(itemView);
            subjectTextView = itemView.findViewById(R.id.subjectTextView);
            senderTextView = itemView.findViewById(R.id.senderTextView);
            timestampTextView = itemView.findViewById(R.id.timestampTextView);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }

    private String formatTimestamp(long timestamp) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault());
        return sdf.format(new Date(timestamp));
    }
}
