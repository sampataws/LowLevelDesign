package com.customersatisfaction;

/**
 * Strategy for breaking ties when agents have the same average rating.
 */
public enum TieBreakStrategy {
    /**
     * Order by agent ID alphabetically.
     */
    AGENT_ID,
    
    /**
     * Order by total number of ratings (more ratings = higher rank).
     */
    TOTAL_RATINGS,
    
    /**
     * Order by most recent rating timestamp.
     */
    MOST_RECENT,
    
    /**
     * No specific tie-breaking (arbitrary order).
     */
    NONE
}

