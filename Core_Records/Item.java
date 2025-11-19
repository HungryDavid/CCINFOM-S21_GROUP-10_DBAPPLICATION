package Core_Records;

import Data_Types.dataTypes.ItemType;

public class Item {
  private int itemID;
  private String itemName;
  private ItemType itemType;
  private double price;
  private int availability;     // if 1 then available else if 0 then unavailable
  private Integer ownerGameID;  // can be null

  public Item(String itemName, ItemType itemType, double price, int availability, Integer ownerGameID) {
    this.itemName = itemName;
    this.itemType = itemType;
    this.price = price;
    this.availability = availability;
    this.ownerGameID = ownerGameID;
  }
    // getters 
    public int getItemID() {
      return itemID; 
    }

    public String getItemName() { 
      return itemName; 
    }

    public ItemType getItemType() { 
      return itemType; 
    }

    public double getPrice() { 
      return price; 
    }

    public int getAvailability() { 
      return availability; 
    }

    public Integer getOwnerGameID() { 
      return ownerGameID; 
    }

    // setters
    public void setItemID(int itemID) { 
      this.itemID = itemID; 
    }

    public void setItemName(String itemName) { 
      this.itemName = itemName; 
    }

    public void setItemType(ItemType itemType) { 
      this.itemType = itemType; 
    }

    public void setPrice(double price) { 
      this.price = price; 
    }

    public void setAvailability(int availability) { 
      this.availability = availability; 
    }

    public void setOwnerGameID(Integer ownerGameID) { 
      this.ownerGameID = ownerGameID; 
    }

    // validate the item
    public boolean isValid() {
        if (itemType == ItemType.GAME && ownerGameID == null) return false;
        if (itemType == ItemType.STORE && ownerGameID != null) return false;
        return true;
    }

}

