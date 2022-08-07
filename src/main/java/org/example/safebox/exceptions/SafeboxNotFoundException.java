package org.example.safebox.exceptions;

public class SafeboxNotFoundException extends RuntimeException  {
    private static final long serialVersionUID = 1L;

    public SafeboxNotFoundException() {
        super("Safebox not found");
    }
}
