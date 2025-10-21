package com.commodityprices;

/**
 * Represents a commodity price data point with timestamp and price.
 */
public class PriceDataPoint implements Comparable<PriceDataPoint> {
    
    private final long timestamp;
    private final double price;
    
    public PriceDataPoint(long timestamp, double price) {
        if (timestamp < 0) {
            throw new IllegalArgumentException("Timestamp cannot be negative");
        }
        if (price < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        
        this.timestamp = timestamp;
        this.price = price;
    }
    
    public long getTimestamp() {
        return timestamp;
    }
    
    public double getPrice() {
        return price;
    }
    
    @Override
    public int compareTo(PriceDataPoint other) {
        // First compare by price (descending), then by timestamp (ascending)
        int priceCompare = Double.compare(other.price, this.price);
        if (priceCompare != 0) {
            return priceCompare;
        }
        return Long.compare(this.timestamp, other.timestamp);
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PriceDataPoint that = (PriceDataPoint) o;
        return timestamp == that.timestamp && 
               Double.compare(that.price, price) == 0;
    }
    
    @Override
    public int hashCode() {
        int result = Long.hashCode(timestamp);
        result = 31 * result + Double.hashCode(price);
        return result;
    }
    
    @Override
    public String toString() {
        return "PriceDataPoint{" +
                "timestamp=" + timestamp +
                ", price=" + price +
                '}';
    }
}

