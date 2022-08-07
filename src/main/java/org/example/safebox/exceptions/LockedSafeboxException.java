package org.example.safebox.exceptions;

public class LockedSafeboxException  extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public LockedSafeboxException() {
        super("Request safebox is locked");
    }
}