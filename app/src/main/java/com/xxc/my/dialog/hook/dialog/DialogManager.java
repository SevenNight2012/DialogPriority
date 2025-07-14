package com.xxc.my.dialog.hook.dialog;

import android.app.Dialog;
import android.os.MessageQueue;

import com.xxc.my.dialog.hook.utils.DialogUtils;
import com.xxc.my.dialog.hook.utils.ListUtils;
import com.xxc.my.dialog.hook.utils.MapUtils;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class DialogManager implements MessageQueue.IdleHandler {
    private static final DialogManager INSTANCE = new DialogManager();
    private final Comparator<Integer> mComparator = new Comparator<Integer>() {
        @Override
        public int compare(Integer o1, Integer o2) {
            if (null == o1 || null == o2) {
                return 0;
            } else {
                return o2 - o1;
            }
        }
    };

    public static DialogManager getInstance() {
        return INSTANCE;
    }

    // 单例中所有的Dialog对象都要用弱引用持有，否则会泄露
    private WeakReference<Dialog> mShowingDialog;
    // 因为要做权限动态调整，所以用 LinkedList 更合适
    private final Map<Integer, LinkedList<WeakReference<Dialog>>> priorityDialogs = new HashMap<>();

    public void prepareShow(Dialog dialog, int priority) {
        LinkedList<WeakReference<Dialog>> dialogs = priorityDialogs.get(priority);
        if (null == dialogs) {
            dialogs = new LinkedList<>();
        }
        synchronized (this) {
            dialogs.add(new WeakReference<>(dialog));
            priorityDialogs.put(priority, dialogs);
        }
    }

    public void tryResetShowingDialog(Dialog dialog) {
        if (null != mShowingDialog && dialog == mShowingDialog.get()) {
            synchronized (this) {
                mShowingDialog = null;
            }
        }
    }

    @Override
    public boolean queueIdle() {
        if (null == mShowingDialog && MapUtils.isNotEmpty(priorityDialogs)) {
            Set<Integer> integers = priorityDialogs.keySet();
            List<Integer> keys = new ArrayList<>(integers);
            synchronized (this) {
                keys.sort(mComparator);
                for (Integer key : keys) {
                    LinkedList<WeakReference<Dialog>> dialogs = priorityDialogs.get(key);
                    if (ListUtils.isNotEmpty(dialogs)) {
                        WeakReference<Dialog> dialogReference = dialogs.pollFirst();
                        if (null != dialogReference) {
                            // 这里不用管取出来的对象是不是为null，在DialogUtils中统一处理空的情况
                            mShowingDialog = dialogReference;
                        }
                        break;
                    }
                }
            }
            Dialog dialog = null != mShowingDialog ? mShowingDialog.get() : null;
            DialogUtils.show(dialog);
        }
        // 保持在队列中
        return true;
    }
}
