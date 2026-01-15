package com.assessment.dto;

/**
 * DTO for batch import summary response (API #2: POST /api/users/batch)
 * 
 * WHY THIS DTO?
 * - Returns import operation results to the client
 * - Shows transparency: what succeeded vs. what failed
 * - Matches assessment requirement: "return JSON with total/imported/not imported"
 * 
 * EXAMPLE RESPONSE:
 * {
 *   "total": 100,
 *   "imported": 95,
 *   "rejected": 5
 * }
 * 
 * INTERVIEW TALKING POINT:
 * "This DTO provides feedback on batch operations, helping users understand
 * what happened during the import. In production, you might also include
 * details about why records were rejected (e.g., duplicate email)."
 */
public class BatchImportSummary {
    
    private int total;      // Total records in uploaded file
    private int imported;   // Successfully imported to database
    private int rejected;   // Rejected due to duplicates or validation errors
    
    /**
     * Default constructor for Jackson deserialization.
     */
    public BatchImportSummary() {
    }
    
    /**
     * All-args constructor for easy creation.
     */
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
