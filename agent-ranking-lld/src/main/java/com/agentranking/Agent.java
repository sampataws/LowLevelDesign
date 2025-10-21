package com.agentranking;

import java.util.Objects;

/**
 * Represents a customer support agent.
 */
public class Agent {
    private final String agentId;
    private final String name;
    private final String email;
    private final String department;
    
    public Agent(String agentId, String name, String email, String department) {
        if (agentId == null || agentId.trim().isEmpty()) {
            throw new IllegalArgumentException("Agent ID cannot be null or empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Name cannot be null or empty");
        }
        
        this.agentId = agentId;
        this.name = name;
        this.email = email;
        this.department = department;
    }
    
    public String getAgentId() {
        return agentId;
    }
    
    public String getName() {
        return name;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getDepartment() {
        return department;
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Agent agent = (Agent) o;
        return agentId.equals(agent.agentId);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(agentId);
    }
    
    @Override
    public String toString() {
        return String.format("Agent{id='%s', name='%s', dept='%s'}", 
                           agentId, name, department);
    }
}

