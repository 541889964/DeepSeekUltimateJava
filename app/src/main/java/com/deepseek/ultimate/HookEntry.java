package com.deepseek.ultimate;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.*;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class HookEntry implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(LoadPackageParam lpparam) throws Throwable {
        if (!lpparam.packageName.equals("com.deepseek.chat")) return;
        try {
            // 官方 API 的参数顺序：类名, 类加载器, 方法名, 参数类型..., 回调
            XposedHelpers.findAndHookMethod("android.app.Activity", lpparam.classLoader, "onResume", new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    final Activity a = (Activity) param.thisObject;
                    if (a != null) {
                        a.runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                a.getWindow().getDecorView().postDelayed(new Runnable() {
                                    @Override
                                    public void run() { injectUI(a); }
                                }, 500);
                            }
                        });
                    }
                }
            });
        } catch (Throwable ignored) {}
    }

    private void injectUI(final Activity activity) {
        try {
            final FrameLayout rootView = (FrameLayout) activity.getWindow().getDecorView();
            if (rootView.findViewWithTag("ultimate_btn") != null) return;

            final Button fab = new Button(activity);
            fab.setTag("ultimate_btn");
            fab.setText("⚡");
            fab.setTextSize(26f);
            fab.setTextColor(Color.WHITE);
            GradientDrawable gd = new GradientDrawable();
            gd.setShape(GradientDrawable.OVAL);
            gd.setColor(Color.parseColor("#1E1E1E"));
            gd.setStroke(4, Color.parseColor("#4CAF50"));
            fab.setBackground(gd);
            fab.setElevation(30f);

            final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(150, 150);
            lp.gravity = Gravity.BOTTOM | Gravity.END;
            lp.setMargins(0, 0, 50, 350);
            rootView.addView(fab, lp);

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) { showDialog(activity); }
            });

            fab.setOnTouchListener(new View.OnTouchListener() {
                float initialX, initialY, initialTouchX, initialTouchY;
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    switch (event.getAction()) {
                        case MotionEvent.ACTION_DOWN:
                            initialX = lp.leftMargin; initialY = lp.topMargin;
                            initialTouchX = event.getRawX(); initialTouchY = event.getRawY();
                            return false;
                        case MotionEvent.ACTION_MOVE:
                            lp.leftMargin = (int) (initialX + (event.getRawX() - initialTouchX));
                            lp.topMargin = (int) (initialY + (event.getRawY() - initialTouchY));
                            if (lp.leftMargin < 0) lp.leftMargin = 0;
                            if (lp.topMargin < 0) lp.topMargin = 0;
                            fab.setLayoutParams(lp);
                            return true;
                    }
                    return false;
                }
            });
        } catch (Throwable ignored) {}
    }

    private void showDialog(final Activity activity) {
        LinearLayout layout = new LinearLayout(activity);
        layout.setOrientation(LinearLayout.VERTICAL);
        layout.setPadding(50, 50, 50, 50);

        final EditText input = new EditText(activity);
        input.setHint("输入任务指令 (支持 @云端 URL)");
        layout.addView(input);

        final TextView output = new TextView(activity);
        output.setText("中枢就绪...\n可长按 ⚡ 拖动位置");
        output.setTextSize(12f); output.setTextColor(Color.GREEN);
        output.setBackgroundColor(Color.BLACK);
        output.setPadding(25, 25, 25, 25);

        final ScrollView scroll = new ScrollView(activity);
        scroll.addView(output);
        scroll.setLayoutParams(new LinearLayout.LayoutParams(-1, 600));
        layout.addView(scroll);

        final AlertDialog dialog = new AlertDialog.Builder(activity)
                .setTitle("⚡ 终极中枢")
                .setView(layout)
                .setPositiveButton("执行", null)
                .setNegativeButton("关闭", null)
                .create();

        dialog.setOnShowListener(d -> {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener(v -> {
                String t = input.getText().toString().trim();
                if (!t.isEmpty()) {
                    output.setText(">>> 任务启动...\n");
                    new Thread(() -> {
                        UltimateEngine.execute(t, log -> activity.runOnUiThread(() -> {
                            output.append(log + "\n");
                            scroll.post(() -> scroll.fullScroll(ScrollView.FOCUS_DOWN));
                        }));
                    }).start();
                }
            });
        });
        dialog.show();
    }
}
