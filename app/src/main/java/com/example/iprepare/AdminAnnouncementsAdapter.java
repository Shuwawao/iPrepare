package com.example.iprepare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.List;
import java.util.Map;

public class AdminAnnouncementsAdapter extends RecyclerView.Adapter<AdminAnnouncementsAdapter.ViewHolder> {

    private Context context;
    private List<DocumentSnapshot> announcements;
    private FirebaseFirestore db;

    public AdminAnnouncementsAdapter(Context context, List<DocumentSnapshot> announcements) {
        this.context = context;
        this.announcements = announcements;
        db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.announcement_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        DocumentSnapshot doc = announcements.get(position);
        Map<String, Object> data = doc.getData();

        holder.titleText.setText((String) data.get("title"));
        holder.contentText.setText((String) data.get("content"));
        holder.authorText.setText("Author: " + data.get("author"));
        holder.typeText.setText("Type: " + data.get("type"));

        holder.deleteButton.setOnClickListener(v -> {
            db.collection("announcements").document(doc.getId())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show();
                        announcements.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, announcements.size());
                    })
                    .addOnFailureListener(e ->
                            Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
        });
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, contentText, authorText, typeText;
        Button deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.titleText);
            contentText = itemView.findViewById(R.id.contentText);
            authorText = itemView.findViewById(R.id.authorText);
            typeText = itemView.findViewById(R.id.typeText);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
