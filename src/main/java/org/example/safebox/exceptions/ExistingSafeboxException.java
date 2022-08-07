package org.example.safebox.exceptions;

public class ExistingSafeboxException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public ExistingSafeboxException() {
        super("Safebox already exists");
    }
}