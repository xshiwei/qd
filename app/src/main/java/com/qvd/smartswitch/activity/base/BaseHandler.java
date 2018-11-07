package com.qvd.smartswitch.activity.base;

import android.os.Handler;
import android.os.Message;

import java.lang.ref.WeakReference;

public abstract class BaseHandler<T> extends Handler {
    private final WeakReference<T> mReference;

    protected BaseHandler(T reference) {
        mReference = new WeakReference<>(reference);
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (mReference.get() == null)
            return;
        handleMessage(mReference.get(), msg);
    }

    protected abstract void handleMessage(T reference, Message msg);

}