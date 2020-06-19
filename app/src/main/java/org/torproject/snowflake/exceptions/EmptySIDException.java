package org.torproject.snowflake.exceptions;

import androidx.annotation.NonNull;

public class EmptySIDException extends Exception {
    private final String ERROR = "SID is not initialized or generated";

    @NonNull
    @Override
    public String toString() {
        return ERROR;
    }
}
