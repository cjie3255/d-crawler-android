package com.jdcrawler.ui;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.jdcrawler.R;
import com.jdcrawler.db.DatabaseHelper;
import com.jdcrawler.model.JdAccount;
import com.jdcrawler.model.SearchResult;
import com.jdcrawler.util.CsvExporter;
import com.jdcrawler.util.JdCrawler;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * 主界面
 */
public class MainActivity extends AppCompatActivity {
    private static final int PERMISSION_REQUEST_CODE = 100;

    private EditText editKeywords;
    private TextView textProgress;
    private TextView textResultCount;
    private ProgressBar progressBar;
    private Button btnStart;
    private Button btnStop;
    private Button btnExport;
    private Button btnAccounts;

    private JdCrawler crawler;
    private DatabaseHelper dbHelper;
    private boolean isCrawling = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        dbHelper = new DatabaseHelper(this);
        crawler = new JdCrawler();

        // 检查并请求权限
        checkPermissions();

        // 加载活跃账号的 Cookie
        loadActiveAccount();
    }

    private void initViews() {
        editKeywords = findViewById(R.id.edit_keywords);
        textProgress = findViewById(R.id.text_progress);
        textResultCount = findViewById(R.id.text_result_count);
        progressBar = findViewById(R.id.progress_bar);
        btnStart = findViewById(R.id.btn_start);
        btnStop = findViewById(R.id.btn_stop);
        btnExport = findViewById(R.id.btn_export);
        btnAccounts = findViewById(R.id.btn_accounts);

        // 更新结果数量显示
        updateResultCount();

        btnStart.setOnClickListener(v -> startCrawling());
        btnStop.setOnClickListener(v -> stopCrawling());
        btnExport.setOnClickListener(v -> exportData());
        btnAccounts.setOnClickListener(v -> {
            // 打开账号管理界面
            Toast.makeText(this, "账号管理功能开发中", Toast.LENGTH_SHORT).show();
        });
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    PERMISSION_REQUEST_CODE);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "权限已授予", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "需要存储权限以导出文件", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void loadActiveAccount() {
        JdAccount account = dbHelper.getActiveAccount();
        if (account != null) {
            crawler.setCookie(account.getCookie());
            Toast.makeText(this, "已使用账号：" + account.getNickname(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "请先添加京东账号", Toast.LENGTH_LONG).show();
        }
    }

    private void startCrawling() {
        String keywordsText = editKeywords.getText().toString().trim();
        if (keywordsText.isEmpty()) {
            Toast.makeText(this, "请输入关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        // 解析关键词（支持换行或逗号分隔）
        List<String> keywords = parseKeywords(keywordsText);
        if (keywords.isEmpty()) {
            Toast.makeText(this, "没有有效的关键词", Toast.LENGTH_SHORT).show();
            return;
        }

        isCrawling = true;
        btnStart.setEnabled(false);
        btnStop.setEnabled(true);
        progressBar.setIndeterminate(false);
        progressBar.setMax(keywords.size());
        progressBar.setProgress(0);

        crawler.batchSearch(keywords, new JdCrawler.SearchCallback() {
            @Override
            public void onProgress(int current, int total, String keyword, int count) {
                runOnUiThread(() -> {
                    progressBar.setProgress(current);
                    textProgress.setText(String.format("进度：%d/%d", current, total));
                    
                    if (count >= 0) {
                        // 保存结果
                        SearchResult result = new SearchResult(keyword, count, "");
                        dbHelper.addSearchResult(result);
                        updateResultCount();
                    }
                });
            }

            @Override
            public void onComplete() {
                runOnUiThread(() -> {
                    isCrawling = false;
                    btnStart.setEnabled(true);
                    btnStop.setEnabled(false);
                    textProgress.setText("完成！");
                    Toast.makeText(MainActivity.this, "爬取完成！", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onError(String error) {
                runOnUiThread(() -> {
                    Toast.makeText(MainActivity.this, error, Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void stopCrawling() {
        crawler.stop();
        isCrawling = false;
        btnStart.setEnabled(true);
        btnStop.setEnabled(false);
        textProgress.setText("已停止");
        Toast.makeText(this, "已停止爬取", Toast.LENGTH_SHORT).show();
    }

    private void exportData() {
        List<SearchResult> results = dbHelper.getAllResults();
        if (results.isEmpty()) {
            Toast.makeText(this, "没有数据可导出", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // 创建导出目录
            File exportDir = new File(Environment.getExternalStoragePublicDirectory(
                    Environment.DIRECTORY_DOWNLOADS), "JDCrawler");
            if (!exportDir.exists()) {
                exportDir.mkdirs();
            }

            // 生成文件名
            String fileName = "jd_search_" + System.currentTimeMillis() + ".csv";
            File outputFile = new File(exportDir, fileName);

            // 导出 CSV
            CsvExporter.export(results, outputFile);

            Toast.makeText(this, "已导出到：" + outputFile.getAbsolutePath(), Toast.LENGTH_LONG).show();

        } catch (IOException e) {
            Toast.makeText(this, "导出失败：" + e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    private List<String> parseKeywords(String text) {
        List<String> keywords = new ArrayList<>();
        // 支持换行、逗号、顿号分隔
        String[] parts = text.split("[\\n,,]");
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                keywords.add(trimmed);
            }
        }
        return keywords;
    }

    private void updateResultCount() {
        int count = dbHelper.getResultCount();
        textResultCount.setText("已保存结果：" + count + " 条");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isCrawling) {
            crawler.stop();
        }
    }
}
