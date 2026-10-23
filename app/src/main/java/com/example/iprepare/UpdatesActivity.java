package com.example.iprepare;

import android.os.Bundle;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.MaterialToolbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.List;

public class UpdatesActivity extends AppCompatActivity {

    private RecyclerView updatesRecyclerView;
    private ProgressBar progressBar;
    private AnnouncementAdapter adapter;
    private final List<Announcement> announcements = new ArrayList<>();
    private FirebaseFirestore db;
    private boolean isAdmin = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_updates);

        // 🧭 Top App Bar Back Button
        MaterialToolbar toolbar = findViewById(R.id.topAppBar);
        if (toolbar != null) {
            toolbar.setNavigationOnClickListener(v -> onBackPressed());
        }

        // ✅ Check if user is admin
        isAdmin = getIntent().getBooleanExtra("isAdmin", false);

        updatesRecyclerView = findViewById(R.id.updatesRecyclerView);
        progressBar = findViewById(R.id.progressBar);

        updatesRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new AnnouncementAdapter(this, announcements, isAdmin);
        updatesRecyclerView.setAdapter(adapter);

        db = FirebaseFirestore.getInstance();

        loadAnnouncements();
    }

    private void loadAnnouncements() {
        progressBar.setVisibility(ProgressBar.VISIBLE);

        CollectionReference announcementsRef = db.collection("announcements");

        announcementsRef.orderBy("datePosted", Query.Direction.DESCENDING)
                .get()
                .addOnCompleteListener(task -> {
                    progressBar.setVisibility(ProgressBar.GONE);

                    if (task.isSuccessful()) {
                        announcements.clear();
                        for (QueryDocumentSnapshot doc : task.getResult()) {
                            String id = doc.getId();
                            String title = doc.getString("title");
                            String content = doc.getString("content");
                            String author = doc.getString("author");
                            String type = doc.getString("type");
                            String datePosted = (doc.getTimestamp("datePosted") != null)
                                    ? doc.getTimestamp("datePosted").toDate().toString()
                                    : "";

                            announcements.add(new Announcement(
                                    id, title, content, author, datePosted, null, type
                            ));
                        }
                        adapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(this, "Failed to load announcements.", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
