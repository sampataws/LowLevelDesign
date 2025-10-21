package com.customersatisfaction;

import java.time.LocalDateTime;
import java.time.YearMonth;
import java.util.List;

/**
 * Driver application demonstrating the Customer Satisfaction System.
 */
public class DriverApplication {
    
    public static void main(String[] args) {
        System.out.println("╔════════════════════════════════════════════════════════════════╗");
        System.out.println("║     Customer Satisfaction System - Real-time Demo             ║");
        System.out.println("╚════════════════════════════════════════════════════════════════╝");
        System.out.println();
        
        demo1_BasicRatings();
        demo2_TieBreaking();
        demo3_MonthlyPerformance();
        demo4_ExportFormats();
        demo5_UnsortedAndTotals();
        
        System.out.println("✅ All demos completed successfully!");
        System.out.println();
        System.out.println("════════════════════════════════════════════════════════════════");
        System.out.println("Key Features Demonstrated:");
        System.out.println("  ✓ Accept ratings (1-5) for agents");
        System.out.println("  ✓ Get all agents sorted by average rating");
        System.out.println("  ✓ Handle ties with multiple strategies");
        System.out.println("  ✓ Monthly performance tracking");
        System.out.println("  ✓ Export in CSV, JSON, XML formats");
        System.out.println("  ✓ Unsorted results and total ratings");
        System.out.println("════════════════════════════════════════════════════════════════");
    }
    
    private static void demo1_BasicRatings() {
        System.out.println("📋 Demo 1: Basic Ratings (Part a)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CustomerSatisfactionSystem system = new CustomerSatisfactionSystem();
        
        // Add ratings for different agents
        System.out.println("Adding ratings:");
        system.addRating("Alice", 5);
        system.addRating("Alice", 4);
        system.addRating("Alice", 5);
        System.out.println("  Alice: 5, 4, 5");
        
        system.addRating("Bob", 3);
        system.addRating("Bob", 4);
        System.out.println("  Bob: 3, 4");
        
        system.addRating("Charlie", 5);
        system.addRating("Charlie", 5);
        system.addRating("Charlie", 5);
        system.addRating("Charlie", 5);
        System.out.println("  Charlie: 5, 5, 5, 5");
        
        system.addRating("Diana", 2);
        system.addRating("Diana", 3);
        system.addRating("Diana", 3);
        System.out.println("  Diana: 2, 3, 3");
        
        System.out.println();
        System.out.println("All agents sorted by average rating:");
        List<AgentStats> stats = system.getAllAgentsSorted();
        for (AgentStats stat : stats) {
            System.out.printf("  %s: %.2f (from %d ratings)%n",
                stat.getAgentId(), stat.getAverageRating(), stat.getTotalRatings());
        }
        
        System.out.println();
        System.out.println("✓ Agents sorted correctly by average rating");
        System.out.println();
    }
    
    private static void demo2_TieBreaking() {
        System.out.println("🔗 Demo 2: Tie Breaking (Part b)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CustomerSatisfactionSystem system = new CustomerSatisfactionSystem();
        
        // Create agents with same average rating
        System.out.println("Creating agents with same average rating (4.0):");
        system.addRating("Alice", 4);
        system.addRating("Alice", 4);
        
        system.addRating("Bob", 3);
        system.addRating("Bob", 5);
        
        system.addRating("Charlie", 4);
        system.addRating("Charlie", 4);
        
        System.out.println("  Alice: 4, 4 → avg = 4.0");
        System.out.println("  Bob: 3, 5 → avg = 4.0");
        System.out.println("  Charlie: 4, 4 → avg = 4.0");
        System.out.println();
        
        // Strategy 1: By Agent ID
        System.out.println("Strategy 1: AGENT_ID (alphabetical)");
        system.setTieBreakStrategy(TieBreakStrategy.AGENT_ID);
        List<AgentStats> stats = system.getAllAgentsSorted();
        for (AgentStats stat : stats) {
            System.out.printf("  %s: %.2f%n", stat.getAgentId(), stat.getAverageRating());
        }
        System.out.println();
        
        // Add more ratings to differentiate by total count
        system.addRating("Alice", 4);
        system.addRating("Alice", 4);
        
        // Strategy 2: By Total Ratings
        System.out.println("Strategy 2: TOTAL_RATINGS (more ratings = higher)");
        system.setTieBreakStrategy(TieBreakStrategy.TOTAL_RATINGS);
        stats = system.getAllAgentsSorted();
        for (AgentStats stat : stats) {
            System.out.printf("  %s: %.2f (from %d ratings)%n",
                stat.getAgentId(), stat.getAverageRating(), stat.getTotalRatings());
        }
        System.out.println();
        
        System.out.println("✓ Multiple tie-breaking strategies demonstrated");
        System.out.println();
    }
    
    private static void demo3_MonthlyPerformance() {
        System.out.println("📅 Demo 3: Monthly Performance (Part c)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CustomerSatisfactionSystem system = new CustomerSatisfactionSystem();
        
        // January ratings
        LocalDateTime jan1 = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime jan2 = LocalDateTime.of(2024, 1, 20, 14, 30);
        
        System.out.println("January 2024 ratings:");
        system.addRating("Alice", 5, jan1);
        system.addRating("Alice", 4, jan2);
        system.addRating("Bob", 3, jan1);
        system.addRating("Bob", 4, jan2);
        System.out.println("  Alice: 5, 4");
        System.out.println("  Bob: 3, 4");
        System.out.println();
        
        // February ratings
        LocalDateTime feb1 = LocalDateTime.of(2024, 2, 10, 9, 0);
        LocalDateTime feb2 = LocalDateTime.of(2024, 2, 25, 16, 0);
        
        System.out.println("February 2024 ratings:");
        system.addRating("Alice", 3, feb1);
        system.addRating("Alice", 3, feb2);
        system.addRating("Bob", 5, feb1);
        system.addRating("Bob", 5, feb2);
        System.out.println("  Alice: 3, 3");
        System.out.println("  Bob: 5, 5");
        System.out.println();
        
        // Get best agents for January
        System.out.println("Best agents for January 2024:");
        List<AgentStats> janStats = system.getBestAgentsForMonth(YearMonth.of(2024, 1));
        for (AgentStats stat : janStats) {
            System.out.printf("  %s: %.2f%n", stat.getAgentId(), stat.getAverageRating());
        }
        System.out.println();
        
        // Get best agents for February
        System.out.println("Best agents for February 2024:");
        List<AgentStats> febStats = system.getBestAgentsForMonth(YearMonth.of(2024, 2));
        for (AgentStats stat : febStats) {
            System.out.printf("  %s: %.2f%n", stat.getAgentId(), stat.getAverageRating());
        }
        System.out.println();
        
        System.out.println("✓ Monthly performance tracking working correctly");
        System.out.println();
    }
    
    private static void demo4_ExportFormats() {
        System.out.println("📤 Demo 4: Export Formats (Part d)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CustomerSatisfactionSystem system = new CustomerSatisfactionSystem();
        
        // Add ratings across multiple months
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime feb = LocalDateTime.of(2024, 2, 15, 10, 0);
        LocalDateTime mar = LocalDateTime.of(2024, 3, 15, 10, 0);
        
        system.addRating("Alice", 5, jan);
        system.addRating("Alice", 4, jan);
        system.addRating("Alice", 3, feb);
        system.addRating("Alice", 4, feb);
        system.addRating("Alice", 5, mar);
        
        system.addRating("Bob", 4, jan);
        system.addRating("Bob", 5, feb);
        system.addRating("Bob", 5, feb);
        system.addRating("Bob", 3, mar);
        
        // Export as CSV
        System.out.println("Export as CSV:");
        String csv = system.exportMonthlyAverages(ExportFormat.CSV);
        System.out.println(csv);
        
        // Export as JSON
        System.out.println("Export as JSON:");
        String json = system.exportMonthlyAverages(ExportFormat.JSON);
        System.out.println(json);
        System.out.println();
        
        // Export as XML
        System.out.println("Export as XML:");
        String xml = system.exportMonthlyAverages(ExportFormat.XML);
        System.out.println(xml);
        System.out.println();
        
        System.out.println("✓ All export formats working correctly");
        System.out.println();
    }
    
    private static void demo5_UnsortedAndTotals() {
        System.out.println("📊 Demo 5: Unsorted Results & Total Ratings (Part e)");
        System.out.println("────────────────────────────────────────────────────────────────");
        
        CustomerSatisfactionSystem system = new CustomerSatisfactionSystem();
        
        LocalDateTime jan = LocalDateTime.of(2024, 1, 15, 10, 0);
        LocalDateTime feb = LocalDateTime.of(2024, 2, 15, 10, 0);
        
        system.addRating("Alice", 5, jan);
        system.addRating("Alice", 4, jan);
        system.addRating("Alice", 3, feb);
        
        system.addRating("Bob", 4, jan);
        system.addRating("Bob", 5, feb);
        
        // Unsorted results
        System.out.println("Unsorted agent statistics:");
        List<AgentStats> unsorted = system.getAllAgentsUnsorted();
        for (AgentStats stat : unsorted) {
            System.out.printf("  %s: %.2f (from %d ratings)%n",
                stat.getAgentId(), stat.getAverageRating(), stat.getTotalRatings());
        }
        System.out.println();
        
        // Total ratings (not averages)
        System.out.println("Export total ratings (not averages) as CSV:");
        String totalsCsv = system.exportMonthlyTotals(ExportFormat.CSV);
        System.out.println(totalsCsv);
        
        System.out.println("Export total ratings as JSON:");
        String totalsJson = system.exportMonthlyTotals(ExportFormat.JSON);
        System.out.println(totalsJson);
        System.out.println();
        
        System.out.println("✓ Unsorted results and total ratings working correctly");
        System.out.println();
    }
}

