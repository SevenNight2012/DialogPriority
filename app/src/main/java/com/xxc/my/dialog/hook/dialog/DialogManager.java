package com.xxc.my.dialog.hook.dialog;

import android.app.Dialog;
import android.os.MessageQueue;

import com.xxc.my.dialog.hook.utils.DialogUtils;
import com.xxc.my.dialog.hook.utils.ListUtils;
import com.xxc.my.dialog.hook.utils.MapUtils;

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

    private Dialog mShowingDialog;
    // 因为要做权限动态调整，所以用 LinkedList 更合适
    private final Map<Integer, LinkedList<Dialog>> priorityDialogs = new HashMap<>();

    public void prepareShow(Dialog dialog, int priority) {
        LinkedList<Dialog> dialogs = priorityDialogs.get(priority);
        if (null == dialogs) {
            dialogs = new LinkedList<>();
        }
        synchronized (this) {
            dialogs.add(dialog);
            priorityDialogs.put(priority, dialogs);
        }
    }

    public void tryResetShowingDialog(Dialog dialog) {
        if (dialog == mShowingDialog) {
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
                    LinkedList<Dialog> dialogs = priorityDialogs.get(key);
                    if (ListUtils.isNotEmpty(dialogs)) {
                        mShowingDialog = dialogs.pollFirst();
                        break;
                    }
                }
            }
            DialogUtils.show(mShowingDialog);
        }
        // 保持在队列中
        return true;
    }
}
