package com.fs.fs.api.network.core.callback;

/**
 * Created by wyx on 2017/1/10.
 */
@Deprecated
public abstract class FileCallback extends BaseCallback {
    public abstract void onProgress(int progress, boolean done, long networkSpeed);
}
