package com.nexus.enrollment.common.util;

/**
 * Interface for objects that can be serialized to JSON format.
 * This provides a clean contract for converting domain objects to JSON strings.
 */
public interface JsonSerializable {
    
    /**
     * Converts the object to its JSON string representation.
     * 
     * @return JSON string representation of the object
     */
    String toJson();
}
