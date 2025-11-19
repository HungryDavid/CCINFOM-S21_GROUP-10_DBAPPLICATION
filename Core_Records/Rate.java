package Core_Records;

import java.math.BigDecimal;
import java.sql.Date;

public class Rate {
    private int rateID;
    private Integer itemID;
    private Integer gameID;
    private BigDecimal price;
    private Date effectiveDate;
    
    // Default constructor
    public Rate() {}
    
    // Constructor with all fields
    public Rate(int rateID, Integer itemID, Integer gameID, BigDecimal price, Date effectiveDate) {
        this.rateID = rateID;
        this.itemID = itemID;
        this.gameID = gameID;
        this.price = price;
        this.effectiveDate = effectiveDate;
    }
    
    // Getters and Setters
    public int getRateID() {
        return rateID;
    }
    
    public void setRateID(int rateID) {
        this.rateID = rateID;
    }
    
    public Integer getItemID() {
        return itemID;
    }
    
    public void setItemID(Integer itemID) {
        this.itemID = itemID;
    }
    
    public Integer getGameID() {
        return gameID;
    }
    
    public void setGameID(Integer gameID) {
        this.gameID = gameID;
    }
    
    public BigDecimal getPrice() {
        return price;
    }
    
    public void setPrice(BigDecimal price) {
        this.price = price;
    }
    
    public Date getEffectiveDate() {
        return effectiveDate;
    }
    
    public void setEffectiveDate(Date effectiveDate) {
        this.effectiveDate = effectiveDate;
    }
    
    public String getReferenceType() {
        if (itemID != null) return "ITEM";
        if (gameID != null) return "GAME";
        return "NONE";
    }
    
    public Integer getReferenceID() {
        if (itemID != null) return itemID;
        if (gameID != null) return gameID;
        return null;
    }
    
    @Override
    public String toString() {
        return "Rate{" +
                "rateID=" + rateID +
                ", referenceType=" + getReferenceType() +
                ", referenceID=" + getReferenceID() +
                ", price=" + price +
                ", effectiveDate=" + effectiveDate +
                '}';
    }
}