package android.app;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.xxc.my.dialog.hook.dialog.DialogManager;
import com.xxc.my.dialog.hook.dialog.DialogPriority;

import java.lang.reflect.Field;

public class DialogShadow {

    public static final String TAG = "DialogShadow";

    public static void tryShowPriority(Dialog dialog) {
        Log.d(TAG, "tryShowPriority: " + dialog);
        if (dialog instanceof DialogPriority && tryResetDismissListener(dialog)) {
            DialogPriority dialogPriority = (DialogPriority) dialog;
            int priority = dialogPriority.getPriority();
            DialogManager.getInstance().prepareShow(dialog, priority);
        } else {
            dialog.show();
        }
    }

    @SuppressLint("DiscouragedPrivateApi")
    private static boolean tryResetDismissListener(Dialog dialog) {
        if (null == dialog) {
            return false;
        }
        try {
            Class<?> dialogClazz = Class.forName("android.app.Dialog");
            Field listenerHandlerField = dialogClazz.getDeclaredField("mListenersHandler");
            listenerHandlerField.setAccessible(true);
            Handler proxyHandler = new ListenersHandler(dialog);
            listenerHandlerField.set(dialog, proxyHandler);

            Field dismissMessageField = dialogClazz.getDeclaredField("mDismissMessage");
            dismissMessageField.setAccessible(true);

            Message dismissMessage = (Message) dismissMessageField.get(dialog);

            if (null != dismissMessage) {
                // 从消息中取出回调监听，然后替换成自己的message
                if (dismissMessage.obj instanceof DialogInterface.OnDismissListener) {
                    DialogInterface.OnDismissListener original = (DialogInterface.OnDismissListener) dismissMessage.obj;
                    dismissMessage = proxyHandler.obtainMessage(ListenersHandler.DISMISS, original);
                }
            } else {
                dismissMessage = proxyHandler.obtainMessage(ListenersHandler.DISMISS, null);
            }
            dismissMessageField.set(dialog, dismissMessage);
            return true;
        } catch (Exception ignore) {
        }
        return false;
    }

}
