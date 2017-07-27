package com.inksmallfrog.frogjbf.test.controller;

import com.inksmallfrog.frogjbf.annotation.Data;
import com.inksmallfrog.frogjbf.annotation.ResponseType;
import com.inksmallfrog.frogjbf.util.IOUtil;
import com.inksmallfrog.frogjbf.util.ResponseTypeEnum;

import java.io.IOException;

/**
 * Created by inksmallfrog on 17-7-27.
 */
public class File {
    @Data
    private String filename;

    @ResponseType(type= ResponseTypeEnum.STREAM)
    public byte[] getFile(){
        filename = "important中文.rar";
        byte[] buffer = null;
        try {
            buffer = IOUtil.getBytesFromFile("/home/inksmallfrog/下载/workspace.rar");
        } catch (IOException e) {
            e.printStackTrace();
        }
        return buffer;

    }
}
