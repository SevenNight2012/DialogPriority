package android.app;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import androidx.annotation.NonNull;
import androidx.lifecycle.DefaultLifecycleObserver;
import androidx.lifecycle.LifecycleOwner;

import com.xxc.my.dialog.hook.dialog.DialogManager;
import com.xxc.my.dialog.hook.utils.ActivityUtils;

import java.lang.ref.WeakReference;

class ListenersHandler extends Handler {

    static final int DISMISS = 0x43;
    static final int CANCEL = 0x44;
    static final int SHOW = 0x45;
    private final WeakReference<Dialog> mDialog;

    ListenersHandler(Dialog dialog) {
        super(Looper.getMainLooper());
        mDialog = new WeakReference<>(dialog);
        if (dialog != null) {
            Context ctx = dialog.getContext();
            Activity master = ActivityUtils.getActivityByContext(ctx);
            if (master instanceof LifecycleOwner) {
                LifecycleOwner owner = (LifecycleOwner) master;
                owner.getLifecycle().addObserver(new DefaultLifecycleObserver() {
                    @Override
                    public void onDestroy(@NonNull LifecycleOwner owner) {
                        removeCallbacksAndMessages(null);
                    }
                });
            }
        }
    }

    @Override
    public void handleMessage(Message msg) {
        Dialog dialog = mDialog.get();
        if (msg.obj == null) {
            msg.obj = new EmptyListener();
        }
        switch (msg.what) {
            case DISMISS:
                DialogManager.getInstance().tryResetShowingDialog(dialog);
                ((DialogInterface.OnDismissListener) msg.obj).onDismiss(dialog);
                break;
            case CANCEL:
                ((DialogInterface.OnCancelListener) msg.obj).onCancel(dialog);
                break;
            case SHOW:
                ((DialogInterface.OnShowListener) msg.obj).onShow(dialog);
                break;
        }
    }

    /**
     * 空实现而已
     */
    private static class EmptyListener implements DialogInterface.OnDismissListener, DialogInterface.OnCancelListener, DialogInterface.OnShowListener {

        @Override
        public void onCancel(DialogInterface dialog) {

        }

        @Override
        public void onDismiss(DialogInterface dialog) {

        }

        @Override
        public void onShow(DialogInterface dialog) {

        }
    }
}