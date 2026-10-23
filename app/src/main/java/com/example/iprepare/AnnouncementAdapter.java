package com.example.iprepare;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.google.firebase.firestore.FirebaseFirestore;
import java.util.List;

public class AnnouncementAdapter extends RecyclerView.Adapter<AnnouncementAdapter.ViewHolder> {

    private final List<Announcement> announcements;
    private final boolean isAdmin;
    private final Context context;
    private final FirebaseFirestore db;

    public AnnouncementAdapter(Context context, List<Announcement> announcements, boolean isAdmin) {
        this.context = context;
        this.announcements = announcements;
        this.isAdmin = isAdmin;
        this.db = FirebaseFirestore.getInstance();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_announcement, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Announcement announcement = announcements.get(position);
        holder.title.setText(announcement.getTitle());
        holder.content.setText(announcement.getContent());
        holder.author.setText("By: " + announcement.getAuthor());
        holder.date.setText(announcement.getDatePosted());

        // Show delete button only if admin
        if (isAdmin) {
            holder.deleteButton.setVisibility(View.VISIBLE);
            holder.deleteButton.setOnClickListener(v -> {
                new android.app.AlertDialog.Builder(context)
                        .setTitle("Delete Announcement")
                        .setMessage("Are you sure you want to delete this announcement?")
                        .setPositiveButton("Delete", (dialog, which) -> {
                            db.collection("announcements")
                                    .document(announcement.getId())
                                    .delete()
                                    .addOnSuccessListener(aVoid -> {
                                        Toast.makeText(context, "Announcement deleted", Toast.LENGTH_SHORT).show();
                                        announcements.remove(position);
                                        notifyItemRemoved(position);
                                        notifyItemRangeChanged(position, announcements.size());
                                    })
                                    .addOnFailureListener(e ->
                                            Toast.makeText(context, "Failed to delete: " + e.getMessage(), Toast.LENGTH_SHORT).show());
                        })
                        .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                        .show();
            });
        } else {
            holder.deleteButton.setVisibility(View.GONE);
        }
    }

    @Override
    public int getItemCount() {
        return announcements.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView title, content, author, date;
        ImageButton deleteButton;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.announcementTitle);
            content = itemView.findViewById(R.id.announcementContent);
            author = itemView.findViewById(R.id.announcementAuthor);
            date = itemView.findViewById(R.id.announcementDate);
            deleteButton = itemView.findViewById(R.id.deleteButton);
        }
    }
}
