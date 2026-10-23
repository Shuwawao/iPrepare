package com.example.iprepare;

import android.Manifest;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.material.appbar.MaterialToolbar;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

public class PDFViewerActivity extends AppCompatActivity {

    private static final int REQUEST_WRITE_PERMISSION = 100;
    private String pdfFile;
    private String topicName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdfviewer);

        PDFView pdfView = findViewById(R.id.pdfView);
        MaterialToolbar toolbar = findViewById(R.id.pdfToolbar);

        // ✅ Get PDF file name and topic
        pdfFile = getIntent().getStringExtra("PDF_FILE");
        topicName = getIntent().getStringExtra("TOPIC_NAME");

        // ✅ Set toolbar title
        if (topicName != null && !topicName.isEmpty()) {
            toolbar.setTitle(topicName + " Module");
        } else {
            toolbar.setTitle("PDF Viewer");
        }

        // ✅ Enable back button
        toolbar.setNavigationOnClickListener(v -> onBackPressed());

        // ✅ Menu click (download button)
        toolbar.setOnMenuItemClickListener(item -> {
            if (item.getItemId() == R.id.action_download_pdf) {
                checkPermissionAndDownload();
                return true;
            }
            return false;
        });

        // ✅ Load PDF from assets
        if (pdfFile != null) {
            try {
                pdfView.fromAsset(pdfFile)
                        .enableSwipe(true)
                        .swipeHorizontal(false)
                        .enableDoubletap(true)
                        .load();
            } catch (Exception e) {
                Toast.makeText(this, "Error loading PDF", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** 🔹 Request legacy storage permission if needed (Android 9 and below) */
    private void checkPermissionAndDownload() {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_WRITE_PERMISSION);
            } else {
                downloadCurrentPdf();
            }
        } else {
            // Android 10+ doesn’t require manual permission for MediaStore
            downloadCurrentPdf();
        }
    }

    /** 🔹 Handle permission result */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_WRITE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                downloadCurrentPdf();
            } else {
                Toast.makeText(this, "Permission denied. Cannot save PDF.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    /** 🔹 Save PDF to Downloads folder (compatible with all Android versions) */
    private void downloadCurrentPdf() {
        if (pdfFile == null) {
            Toast.makeText(this, "No PDF to download", Toast.LENGTH_SHORT).show();
            return;
        }

        try (InputStream in = getAssets().open(pdfFile)) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // ✅ Use MediaStore for Android 10+
                ContentResolver resolver = getContentResolver();
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, pdfFile);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, values);
                if (uri != null) {
                    try (OutputStream out = resolver.openOutputStream(uri)) {
                        copyStream(in, out);
                    }
                }
            } else {
                // ✅ Legacy file system for Android 9 and below
                File downloadsDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                File outputFile = new File(downloadsDir, pdfFile);
                try (OutputStream out = new FileOutputStream(outputFile)) {
                    copyStream(in, out);
                }
            }

            Toast.makeText(this, "PDF saved to Downloads/" + pdfFile, Toast.LENGTH_LONG).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF: " + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    /** Utility method for copying file data */
    private void copyStream(InputStream in, OutputStream out) throws Exception {
        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = in.read(buffer)) != -1) {
            out.write(buffer, 0, bytesRead);
        }
        out.flush();
    }
}
