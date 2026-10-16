
package com.erdal.helpdeskpro.service.impl;

import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;

final class LimitedInputStream extends FilterInputStream {

    private final long maxBytes;
    private long bytesRead;

    LimitedInputStream(InputStream input, long maxBytes) {
        super(input);
        this.maxBytes = maxBytes;
    }

    @Override
    public int read() throws IOException {
        int value = super.read();

        if (value != -1) {
            bytesRead++;

            if (bytesRead > maxBytes) {
                throw new IOException("File exceeds the size limit");
            }
        }

        return value;
    }

    @Override
    public int read(byte[] buffer, int offset, int length)
            throws IOException {

        if (length == 0) {
            return 0;
        }

        long remaining = maxBytes - bytesRead;

        int allowed = (int) Math.min(
                length,
                remaining + 1
        );

        int count = super.read(buffer, offset, allowed);

        if (count > 0) {
            bytesRead += count;

            if (bytesRead > maxBytes) {
                throw new IOException("File exceeds the size limit");
            }
        }

        return count;
    }
}
