package com.fs.fs.api.network.core;

import java.io.File;

/**
 * Created by wyx on 2017/1/9.
 */

public class FileParam {
    public File file;
    public String fileName = "";
    public long fileSize = 0;

    public FileParam(File file) {
        this.file = file;
        this.fileName = file.getName();
        this.fileSize = file.length();
    }

    public FileParam() {
    }
}
