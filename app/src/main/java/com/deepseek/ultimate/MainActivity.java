package com.deepseek.ultimate;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.ScrollView;
import android.widget.LinearLayout;
import android.graphics.Color;
import java.io.File;
import android.widget.EditText;
import android.widget.Button;
import android.widget.Toast;
import android.view.Gravity;
import android.graphics.drawable.GradientDrawable;

public class MainActivity extends Activity {
    private final String RULES_FILE = "/sdcard/DeepSeekAgent/rules.txt";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ScrollView scroll = new ScrollView(this);
        LinearLayout root = new LinearLayout(this);
        root.setOrientation(LinearLayout.VERTICAL);
        root.setPadding(60, 100, 60, 100);
        root.setBackgroundColor(Color.parseColor("#F0F2F5"));
        scroll.addView(root);

        TextView title = new TextView(this);
        title.setText("⚡ 终极控制台");
        title.setTextSize(28f);
        title.setTextColor(Color.parseColor("#1E1E1E"));
        title.setGravity(Gravity.CENTER);
        root.addView(title);

        LinearLayout card = new LinearLayout(this);
        card.setOrientation(LinearLayout.VERTICAL);
        card.setPadding(40, 40, 40, 40);
        GradientDrawable gd = new GradientDrawable();
        gd.setColor(Color.WHITE); gd.setCornerRadius(40f); gd.setStroke(2, Color.parseColor("#D0D0D0"));
        card.setBackground(gd);
        LinearLayout.LayoutParams lpCard = new LinearLayout.LayoutParams(-1, -2);
        lpCard.setMargins(0, 50, 0, 0);
        root.addView(card, lpCard);

        final EditText etRules = new EditText(this);
        etRules.setHint("在此输入 AI 核心逻辑与防审查规则...");
        etRules.setMinLines(12); etRules.setGravity(Gravity.TOP);
        etRules.setText(readRules()); etRules.setPadding(30, 30, 30, 30);
        etRules.setBackgroundColor(Color.parseColor("#F8F8F8")); etRules.setTextSize(14f);
        card.addView(etRules);

        Button btnSave = new Button(this);
        btnSave.setText("💾 保存规则并生效");
        btnSave.setBackgroundColor(Color.parseColor("#0B57D0"));
        btnSave.setTextColor(Color.WHITE);
        btnSave.setOnClickListener(v -> {
            saveRules(etRules.getText().toString());
            Toast.makeText(MainActivity.this, "规则已保存！", Toast.LENGTH_SHORT).show();
        });
        LinearLayout.LayoutParams lpBtn = new LinearLayout.LayoutParams(-1, -2);
        lpBtn.setMargins(0, 30, 0, 0);
        card.addView(btnSave, lpBtn);

        Button btnStart = new Button(this);
        btnStart.setText("🚀 启动 DeepSeek");
        btnStart.setTextSize(18f); btnStart.setTextColor(Color.WHITE);
        btnStart.setBackgroundColor(Color.parseColor("#1E1E1E"));
        btnStart.setPadding(0, 40, 0, 40);
        btnStart.setOnClickListener(v -> {
            try { startActivity(getPackageManager().getLaunchIntentForPackage("com.deepseek.chat")); }
            catch (Exception e) { Toast.makeText(MainActivity.this, "未安装 DeepSeek", Toast.LENGTH_SHORT).show(); }
        });
        LinearLayout.LayoutParams lpStart = new LinearLayout.LayoutParams(-1, -2);
        lpStart.setMargins(0, 60, 0, 0);
        root.addView(btnStart, lpStart);
        setContentView(scroll);
    }

    private String readRules() {
        File f = new File(RULES_FILE);
        if (f.exists()) {
            try { return new String(java.nio.file.Files.readAllBytes(f.toPath())); } catch (Exception e) {}
        }
        String def = "1. 读代码：[READ]路径[/READ]\n2. 写代码：[WRITE]路径[/WRITE]```代码```\n3. 执行命令：[CMD]命令[/CMD]\n4. 完成：[DONE]";
        saveRules(def); return def;
    }

    private void saveRules(String rules) {
        try {
            File dir = new File("/sdcard/DeepSeekAgent"); dir.mkdirs();
            java.io.FileWriter writer = new java.io.FileWriter(RULES_FILE);
            writer.write(rules); writer.close();
        } catch (Exception e) {}
    }
}
