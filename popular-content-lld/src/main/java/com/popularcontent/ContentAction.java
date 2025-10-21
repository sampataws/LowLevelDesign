package com.popularcontent;

/**
 * Enum representing actions that can be performed on content.
 */
public enum ContentAction {
    /**
     * Increases the popularity of content by 1.
     * Triggered when someone likes or comments on the content.
     */
    INCREASE_POPULARITY,
    
    /**
     * Decreases the popularity of content by 1.
     * Triggered when spam comments/likes are removed.
     */
    DECREASE_POPULARITY
}

