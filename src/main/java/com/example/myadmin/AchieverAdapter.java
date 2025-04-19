package com.example.myadmin; // Replace with your package name

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class AchieverAdapter extends RecyclerView.Adapter<AchieverAdapter.AchieverViewHolder> {
    private List<Achievement> achieversList;
    private Context context;

    public AchieverAdapter(List<Achievement> achieversList, Context context) {
        this.achieversList = achieversList;
        this.context = context;
    }

    @NonNull
    @Override
    public AchieverViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.achiever_item, parent, false);
        return new AchieverViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AchieverViewHolder holder, int position) {
        Achievement achievement = achieversList.get(position);
        holder.name.setText(achievement.getName());
        holder.details.setText(achievement.getDetails());

        String base64Image = achievement.getImageBase64();

        if (base64Image != null && !base64Image.isEmpty()) {
            try {
                // Decode Base64 image
                byte[] decodedString = Base64.decode(base64Image, Base64.DEFAULT);
                Bitmap decodedBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

                if (decodedBitmap != null) {
                    holder.image.setImageBitmap(decodedBitmap);
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log the error but do nothing (No placeholder)
            }
        }
    }


    @Override
    public int getItemCount() {
        return achieversList.size();
    }

    public static class AchieverViewHolder extends RecyclerView.ViewHolder {
        TextView name, details;
        ImageView image;

        public AchieverViewHolder(@NonNull View itemView) {
            super(itemView);
            image = itemView.findViewById(R.id.achiever_image);
            name = itemView.findViewById(R.id.achiever_name);
            details = itemView.findViewById(R.id.achiever_details);
        }
    }
}