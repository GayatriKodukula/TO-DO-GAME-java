package todogame;

import java.util.*;

/**
 * Represents a player. Tracks coins, gems, level, streak, and owned items.
 */
public class User {

    private static final int[] LEVEL_THRESHOLDS = {
        0, 50, 120, 220, 350, 520, 740, 1020, 1370, 1800
    };
    public static final int MAX_LEVEL = LEVEL_THRESHOLDS.length;

    private String name;
    private int    coins, gems, totalXP, level, streak;
    private String lastActiveDate;
    private final List<String> ownedItemIds;

    public User(String name) {
        this.name = name; this.coins = 0; this.gems = 0;
        this.totalXP = 0; this.level = 1; this.streak = 0;
        this.lastActiveDate = ""; this.ownedItemIds = new ArrayList<>();
    }

    public User(String name, int coins, int gems, int totalXP, int level,
                int streak, String lastActiveDate, List<String> ownedItemIds) {
        this.name = name; this.coins = coins; this.gems = gems;
        this.totalXP = totalXP; this.level = level; this.streak = streak;
        this.lastActiveDate = lastActiveDate;
        this.ownedItemIds = new ArrayList<>(ownedItemIds);
    }

    /** Awards coins+gems, advances level. Returns true if levelled up. */
    public boolean awardCurrency(int coins, int gems) {
        this.coins  += coins;
        this.gems   += gems;
        this.totalXP += coins + gems * 10;
        return levelUp();
    }

    private boolean levelUp() {
        int old = level;
        while (level < MAX_LEVEL && totalXP >= LEVEL_THRESHOLDS[level]) level++;
        return level > old;
    }

    public boolean spendCoins(int amount) {
        if (coins < amount) return false; coins -= amount; return true;
    }
    public boolean spendGems(int amount) {
        if (gems < amount) return false; gems -= amount; return true;
    }

    /** Returns {bonusCoins, bonusGems} for the streak update. */
    public int[] updateStreak(String todayStr) {
        if (todayStr.equals(lastActiveDate)) return new int[]{0,0};
        boolean consec = isConsecutiveDay(lastActiveDate, todayStr);
        streak = consec ? streak + 1 : 1;
        lastActiveDate = todayStr;
        int bonusCoins = Math.min(streak * 5, 30);
        int bonusGems  = streak > 0 && streak % 7 == 0 ? 1 : 0;
        return new int[]{bonusCoins, bonusGems};
    }

    private boolean isConsecutiveDay(String prev, String today) {
        if (prev == null || prev.isBlank()) return false;
        try {
            return java.time.LocalDate.parse(prev).plusDays(1)
                    .equals(java.time.LocalDate.parse(today));
        } catch (Exception e) { return false; }
    }

    public boolean owns(String itemId)  { return ownedItemIds.contains(itemId); }
    public void    addItem(String id)   { if (!owns(id)) ownedItemIds.add(id); }
    public List<String> getOwnedItemIds() { return Collections.unmodifiableList(ownedItemIds); }

    public double levelProgress() {
        if (level >= MAX_LEVEL) return 1.0;
        int s = LEVEL_THRESHOLDS[level-1], e = LEVEL_THRESHOLDS[level];
        return (double)(totalXP - s) / (e - s);
    }
    public int xpToNextLevel() {
        if (level >= MAX_LEVEL) return 0; return LEVEL_THRESHOLDS[level] - totalXP;
    }
    public int getTotalXP() { return totalXP; }

    public String getName()       { return name; }
    public int    getCoins()      { return coins; }
    public int    getGems()       { return gems; }
    public int    getLevel()      { return level; }
    public int    getStreak()     { return streak; }
    public String getLastActive() { return lastActiveDate; }
    public void   setName(String n){ this.name = n; }

    public String toFileString() {
        return name+"|"+coins+"|"+gems+"|"+totalXP+"|"+level+"|"+streak+"|"
                +lastActiveDate+"|"+String.join(",", ownedItemIds);
    }

    public static User fromFileString(String line) {
        String[] p = line.split("\\|", 8);
        if (p.length < 8) throw new IllegalArgumentException("Bad user: "+line);
        List<String> items = new ArrayList<>();
        if (!p[7].isBlank())
            for (String id : p[7].split(",")) if (!id.isBlank()) items.add(id);
        return new User(p[0], Integer.parseInt(p[1]), Integer.parseInt(p[2]),
                Integer.parseInt(p[3]), Integer.parseInt(p[4]),
                Integer.parseInt(p[5]), p[6], items);
    }

    @Override public String toString() {
        return String.format("%s | Lv.%d | 🪙%d | 💎%d | 🔥%d-day streak",
                name, level, coins, gems, streak);
    }
}
