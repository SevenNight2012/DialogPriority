package com.xxc.my.dialog.hook.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.xxc.my.dialog.hook.R;
import com.xxc.my.dialog.hook.utils.DialogUtils;

import java.util.Random;


public class SimplePriorityDialog extends Dialog implements DialogPriority {

    private int priority;
    private String title;
    private String content;

    private TextView tvTitle;
    private TextView tvContent;
    private Button btnPositive;
    private Button btnNegative;
    private final OnClickListener negativeClick = (dialogInterface, i) -> {
        // 安全的关闭弹窗
        DialogUtils.dismiss(dialogInterface);
    };

    public SimplePriorityDialog(@NonNull Context context) {
        super(context);
        init(context);
    }

    public SimplePriorityDialog(@NonNull Context context, int themeResId) {
        super(context, themeResId);
        init(context);
    }

    protected SimplePriorityDialog(@NonNull Context context, boolean cancelable,
            @Nullable OnCancelListener cancelListener) {
        super(context, cancelable, cancelListener);
        init(context);
    }

    private void init(Context context) {
        setContentView(R.layout.dialog_content);
        tvTitle = findViewById(R.id.dialog_title);
        tvContent = findViewById(R.id.dialog_content);
        btnPositive = findViewById(R.id.dialog_positive);
        btnNegative = findViewById(R.id.dialog_negative);
        setButton(DialogInterface.BUTTON_POSITIVE, "确定", new OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                // int p = Math.max(2, --priority);
                Random random = new Random();
                int priorityValue = random.nextInt(10) + 1; // [1,10]
                Dialog dialog;
                if (priorityValue == 9) {
                    dialog = new AlertDialog.Builder(getContext())
                            .setNegativeButton("取消", (dialog1, which) -> {
                                // 将弹窗关闭
                                DialogUtils.dismiss(dialog1);
                            })
                            .setPositiveButton("确定",this)
                            .setTitle("这是第9级标题")
                            .setMessage("这个第9级弹窗的内容")
                            .create();
                } else {
                    SimplePriorityDialog simple = new SimplePriorityDialog(getContext());
                    simple.setPriority(priorityValue);
                    dialog = simple;
                }
                dialog.show();
            }
        });

        setButton(DialogInterface.BUTTON_NEGATIVE, "取消", negativeClick);
    }

    public SimplePriorityDialog setButton(int which, String msg, OnClickListener clickListener) {
        switch (which) {
            case DialogInterface.BUTTON_NEGATIVE:
                btnNegative.setText(msg);
                btnNegative.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onClick(SimplePriorityDialog.this, DialogInterface.BUTTON_NEGATIVE);
                        }
                    }
                });
                break;
            case DialogInterface.BUTTON_POSITIVE:
                btnPositive.setText(msg);
                btnPositive.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (clickListener != null) {
                            clickListener.onClick(SimplePriorityDialog.this, DialogInterface.BUTTON_POSITIVE);
                        }
                    }
                });
                break;
        }
        return this;
    }


    public SimplePriorityDialog setDialogTitle(String title) {
        this.title = title;
        if (!TextUtils.isEmpty(title)) {
            tvTitle.setText(title);
        }
        return this;
    }

    public SimplePriorityDialog setDialogContent(String content) {
        this.content = content;
        if (!TextUtils.isEmpty(content)) {
            tvContent.setText(content);
        }
        return this;
    }

    @Override
    public void setPriority(int priority) {
        this.priority = priority;
        setDialogTitle("这是第" + priority + "标题");
        setDialogContent("这个第" + priority + "个弹窗的内容");
    }

    @Override
    public int getPriority() {
        return priority;
    }
}
