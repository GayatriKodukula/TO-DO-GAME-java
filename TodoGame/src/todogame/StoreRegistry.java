package todogame;

import java.util.ArrayList;
import java.util.List;

/**
 * Master catalogue of every item available in the store.
 * Add new items here — no other class needs to change.
 */
public class StoreRegistry {

    private static final List<StoreItem> ALL = new ArrayList<>();

    static {
        // ── FURNITURE ─────────────────────────────────────────────────────
        add("sofa",        "🛋️",  "Comfy Sofa",       "Relax after a productive day",          StoreItem.Category.FURNITURE,    40,  StoreItem.Currency.COINS);
        add("desk",        "🪑",  "Study Desk",        "Where great tasks get done",            StoreItem.Category.FURNITURE,    55,  StoreItem.Currency.COINS);
        add("bookshelf",   "📚",  "Bookshelf",         "Store your knowledge and trophies",     StoreItem.Category.FURNITURE,    35,  StoreItem.Currency.COINS);
        add("lamp",        "🪔",  "Floor Lamp",        "Illuminate your workspace",             StoreItem.Category.FURNITURE,    20,  StoreItem.Currency.COINS);
        add("bed",         "🛏️",  "King Size Bed",     "Sleep like the champion you are",       StoreItem.Category.FURNITURE,    80,  StoreItem.Currency.COINS);
        add("plant_stand", "🌸",  "Plant Stand",       "Bring nature to your room",             StoreItem.Category.FURNITURE,    25,  StoreItem.Currency.COINS);

        // ── FOOD ──────────────────────────────────────────────────────────
        add("pizza",       "🍕",  "Pizza Party",       "Celebrate completing tasks",            StoreItem.Category.FOOD,         15,  StoreItem.Currency.COINS);
        add("sushi",       "🍣",  "Sushi Set",         "Elegant reward for focused work",       StoreItem.Category.FOOD,         25,  StoreItem.Currency.COINS);
        add("cake",        "🎂",  "Birthday Cake",     "Every level up deserves cake",          StoreItem.Category.FOOD,         30,  StoreItem.Currency.COINS);
        add("coffee",      "☕",  "Fancy Coffee",      "Premium beans for peak productivity",   StoreItem.Category.FOOD,         10,  StoreItem.Currency.COINS);
        add("burger",      "🍔",  "Gourmet Burger",    "The tastiest task reward",              StoreItem.Category.FOOD,         18,  StoreItem.Currency.COINS);
        add("ramen",       "🍜",  "Ramen Bowl",        "Cozy meal after a long day",            StoreItem.Category.FOOD,         20,  StoreItem.Currency.COINS);

        // ── CLOTHES ───────────────────────────────────────────────────────
        add("hoodie",      "🧥",  "Hoodie",            "Comfy coding uniform",                  StoreItem.Category.CLOTHES,      30,  StoreItem.Currency.COINS);
        add("sneakers",    "👟",  "Fresh Sneakers",    "Step up your style",                    StoreItem.Category.CLOTHES,      50,  StoreItem.Currency.COINS);
        add("cap",         "🧢",  "Cool Cap",          "Top off your achievements",             StoreItem.Category.CLOTHES,      20,  StoreItem.Currency.COINS);
        add("scarf",       "🧣",  "Winter Scarf",      "Warm reward for cold days",             StoreItem.Category.CLOTHES,      15,  StoreItem.Currency.COINS);
        add("suit",        "👔",  "Business Suit",     "Look professional, feel unstoppable",   StoreItem.Category.CLOTHES,      3,   StoreItem.Currency.GEMS);
        add("crown",       "👑",  "Golden Crown",      "For the highest achievers",             StoreItem.Category.CLOTHES,      10,  StoreItem.Currency.GEMS);

        // ── DEVICES ───────────────────────────────────────────────────────
        add("phone",       "📱",  "Smartphone",        "Stay connected on the go",              StoreItem.Category.DEVICES,      100, StoreItem.Currency.COINS);
        add("laptop",      "💻",  "Laptop",            "Ultimate productivity machine",         StoreItem.Category.DEVICES,      5,   StoreItem.Currency.GEMS);
        add("tablet",      "📟",  "Tablet",            "Work anywhere, anytime",                StoreItem.Category.DEVICES,      3,   StoreItem.Currency.GEMS);
        add("headphones",  "🎧",  "Headphones",        "Block out the world, focus up",         StoreItem.Category.DEVICES,      60,  StoreItem.Currency.COINS);
        add("camera",      "📷",  "DSLR Camera",       "Capture your achievements",             StoreItem.Category.DEVICES,      4,   StoreItem.Currency.GEMS);
        add("smartwatch",  "⌚",  "Smartwatch",        "Time your tasks in style",              StoreItem.Category.DEVICES,      75,  StoreItem.Currency.COINS);

        // ── HOME APPLIANCES ───────────────────────────────────────────────
        add("fridge",      "🧊",  "Smart Fridge",      "Keep your brain fuel cold",             StoreItem.Category.APPLIANCES,   90,  StoreItem.Currency.COINS);
        add("microwave",   "📦",  "Microwave",         "Quick meals between tasks",             StoreItem.Category.APPLIANCES,   40,  StoreItem.Currency.COINS);
        add("washer",      "🫧",  "Washing Machine",   "Clean clothes, clear mind",             StoreItem.Category.APPLIANCES,   65,  StoreItem.Currency.COINS);
        add("aircon",      "❄️",  "Air Conditioner",   "Peak performance temperature",          StoreItem.Category.APPLIANCES,   2,   StoreItem.Currency.GEMS);
        add("toaster",     "🍞",  "Toaster Oven",      "Morning fuel sorted",                   StoreItem.Category.APPLIANCES,   25,  StoreItem.Currency.COINS);
        add("vacuum",      "🌀",  "Robot Vacuum",      "Let robots do the cleaning",            StoreItem.Category.APPLIANCES,   55,  StoreItem.Currency.COINS);

        // ── ENTERTAINMENT ─────────────────────────────────────────────────
        add("console",     "🕹️",  "Game Console",      "You earned a break — play!",            StoreItem.Category.ENTERTAINMENT,4,   StoreItem.Currency.GEMS);
        add("guitar",      "🎸",  "Electric Guitar",   "Rock out after hitting goals",          StoreItem.Category.ENTERTAINMENT,70,  StoreItem.Currency.COINS);
        add("board_game",  "♟️",  "Chess Set",         "Strategy on and off the task list",     StoreItem.Category.ENTERTAINMENT,30,  StoreItem.Currency.COINS);
        add("tv",          "📺",  "4K TV",             "Ultimate relaxation reward",            StoreItem.Category.ENTERTAINMENT,6,   StoreItem.Currency.GEMS);
        add("speaker",     "🔊",  "Bluetooth Speaker", "Music makes tasks fly by",              StoreItem.Category.ENTERTAINMENT,45,  StoreItem.Currency.COINS);
        add("vr",          "🥽",  "VR Headset",        "Escape to another dimension",           StoreItem.Category.ENTERTAINMENT,8,   StoreItem.Currency.GEMS);

        // ── NATURE ────────────────────────────────────────────────────────
        add("cactus",      "🌵",  "Cactus",            "Low-maintenance, like this task",       StoreItem.Category.NATURE,       10,  StoreItem.Currency.COINS);
        add("bonsai",      "🌳",  "Bonsai Tree",       "Patience and growth",                   StoreItem.Category.NATURE,       35,  StoreItem.Currency.COINS);
        add("sunflower",   "🌻",  "Sunflower",         "Bright energy for your space",          StoreItem.Category.NATURE,       8,   StoreItem.Currency.COINS);
        add("aquarium",    "🐠",  "Fish Tank",         "Calming vibes after hard work",         StoreItem.Category.NATURE,       2,   StoreItem.Currency.GEMS);
        add("mushroom",    "🍄",  "Mushroom Garden",   "Quirky and cool",                       StoreItem.Category.NATURE,       20,  StoreItem.Currency.COINS);

        // ── TRANSPORT ─────────────────────────────────────────────────────
        add("bike",        "🚲",  "Bicycle",           "Eco-friendly commute reward",           StoreItem.Category.TRANSPORT,    50,  StoreItem.Currency.COINS);
        add("scooter",     "🛵",  "Scooter",           "Zip around in style",                   StoreItem.Category.TRANSPORT,    3,   StoreItem.Currency.GEMS);
        add("car",         "🚗",  "Sports Car",        "The ultimate big purchase",             StoreItem.Category.TRANSPORT,    15,  StoreItem.Currency.GEMS);
        add("boat",        "⛵",  "Sailboat",          "Navigate life's challenges",            StoreItem.Category.TRANSPORT,    12,  StoreItem.Currency.GEMS);
        add("skateboard",  "🛹",  "Skateboard",        "Cool ride for cool achievers",          StoreItem.Category.TRANSPORT,    30,  StoreItem.Currency.COINS);
    }

    private static void add(String id, String icon, String name, String desc,
                            StoreItem.Category cat, int price, StoreItem.Currency currency) {
        ALL.add(new StoreItem(id, icon, name, desc, cat, price, currency));
    }

    public static List<StoreItem> getAll() { return new ArrayList<>(ALL); }

    public static StoreItem findById(String id) {
        return ALL.stream().filter(i -> i.getId().equals(id)).findFirst().orElse(null);
    }
}
