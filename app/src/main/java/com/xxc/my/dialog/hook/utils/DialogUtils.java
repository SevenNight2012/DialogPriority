package com.xxc.my.dialog.hook.utils;

import android.app.Dialog;
import android.content.DialogInterface;

public class DialogUtils {

    public static void show(Dialog dialog) {
        try {
            if (dialog != null) {
                dialog.show();
            }
        } catch (Exception ignore) {
        }
    }

    public static void dismiss(DialogInterface dialogInterface) {
        try {
            if (dialogInterface != null) {
                dialogInterface.dismiss();
            }
        } catch (Exception ignore) {
        }
    }
}
