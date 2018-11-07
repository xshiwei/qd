package com.qvd.smartswitch.activity.base;

import java.lang.ref.WeakReference;

public class BaseRunnable implements Runnable {

    public interface MyInterface {
        void doSomething();
    }

    private final WeakReference<MyInterface> mInterface;

    public BaseRunnable(MyInterface mInterface) {
        this.mInterface = new WeakReference<>(mInterface);
    }

    @Override
    public void run() {
        if (mInterface.get() == null) {
            return;
        }
        mInterface.get().doSomething();
    }
}
