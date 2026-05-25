package todogame;

/** A purchasable item in the store. Owned forever once bought. */
public class StoreItem {

    public enum Category {
        FURNITURE    ("🪑", "Furniture"),
        FOOD         ("🍕", "Food"),
        CLOTHES      ("👕", "Clothes"),
        DEVICES      ("📱", "Devices"),
        APPLIANCES   ("🏠", "Appliances"),
        ENTERTAINMENT("🎮", "Entertainment"),
        NATURE       ("🌿", "Nature"),
        TRANSPORT    ("🚗", "Transport");

        private final String icon, label;
        Category(String icon, String label){ this.icon=icon; this.label=label; }
        public String getIcon()  { return icon; }
        public String getLabel() { return label; }
    }

    public enum Currency { COINS, GEMS }

    private final String   id, icon, name, description;
    private final Category category;
    private final int      price;
    private final Currency currency;

    public StoreItem(String id, String icon, String name, String description,
                     Category category, int price, Currency currency) {
        this.id=id; this.icon=icon; this.name=name; this.description=description;
        this.category=category; this.price=price; this.currency=currency;
    }

    public String   getId()          { return id; }
    public String   getIcon()        { return icon; }
    public String   getName()        { return name; }
    public String   getDescription() { return description; }
    public Category getCategory()    { return category; }
    public int      getPrice()       { return price; }
    public Currency getCurrency()    { return currency; }

    public String getPriceDisplay() {
        return currency == Currency.GEMS
                ? "💎 " + price + " Gems"
                : "🪙 " + price + " Coins";
    }
}
