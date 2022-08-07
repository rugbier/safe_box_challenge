package org.example.safebox.exceptions;

public class InternalServerError  extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public InternalServerError() {
        super("Unexpected API error");
    }
}
