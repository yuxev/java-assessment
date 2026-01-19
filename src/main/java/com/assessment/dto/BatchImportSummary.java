package com.assessment.dto;

/**
 * DTO for batch import summary response.
 */
public class BatchImportSummary {
    
    private int total;
    private int imported;
    private int rejected;
    
    public BatchImportSummary() {
    }
    
    public BatchImportSummary(int total, int imported, int rejected) {
        this.total = total;
        this.imported = imported;
        this.rejected = rejected;
    }
    
    // Getters and Setters
    
    public int getTotal() {
        return total;
    }
    
    public void setTotal(int total) {
        this.total = total;
    }
    
    public int getImported() {
        return imported;
    }
    
    public void setImported(int imported) {
        this.imported = imported;
    }
    
    public int getRejected() {
        return rejected;
    }
    
    public void setRejected(int rejected) {
        this.rejected = rejected;
    }
}
