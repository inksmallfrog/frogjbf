package com.inksmallfrog.frogjbf.util;

import java.io.InputStream;

/**
 * Created by inksmallfrog on 17-7-27.
 */
public class StreamResponse {
    private String filename;
    private byte[] buffer;
    private InputStream stream;

    public StreamResponse(String filename, byte[] buffer) {
        this.filename = filename;
        this.buffer = buffer;
    }

    public StreamResponse(String filename, InputStream stream) {
        this.filename = filename;
        this.stream = stream;
    }

    public String getFilename() {
        return filename;
    }

    public void setFilename(String filename) {
        this.filename = filename;
    }

    public byte[] getBuffer() {
        return buffer;
    }

    public void setBuffer(byte[] buffer) {
        this.buffer = buffer;
    }

    public InputStream getStream() {
        return stream;
    }

    public void setStream(InputStream stream) {
        this.stream = stream;
    }
}
