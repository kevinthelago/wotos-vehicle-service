package com.wotos.wotosvehicleservice.armor;

/**
 * Thrown when an {@link ArmorProfile} cannot be (de)serialized to/from its stored
 * JSON form. Surfaced as a 500 by the global exception handler (G7).
 */
public class ArmorSerializationException extends RuntimeException {

    public ArmorSerializationException(String message, Throwable cause) {
        super(message, cause);
    }
}
