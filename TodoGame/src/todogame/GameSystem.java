package todogame;

public class GameSystem {

    /**
     * Processes task completion. Awards coins + gems, updates streak.
     * Returns int[]{coins, gems, bonusCoins, bonusGems, leveledUp? 1:0}
     */
    public int[] processCompletion(User user, Task task, String todayStr) {
        int baseCoins = task.getPriority().getCoins();
        int baseGems  = task.getPriority().getGems();

        boolean leveledUp = user.awardCurrency(baseCoins, baseGems);

        int[] streakBonus  = user.updateStreak(todayStr);
        if (streakBonus[0] > 0 || streakBonus[1] > 0)
            user.awardCurrency(streakBonus[0], streakBonus[1]);

        return new int[]{ baseCoins, baseGems, streakBonus[0], streakBonus[1], leveledUp ? 1 : 0 };
    }

    /** ASCII progress bar for level XP. */
    public String buildProgressBar(User user, int width) {
        if (user.getLevel() >= User.MAX_LEVEL)
            return "[" + "█".repeat(width) + "] MAX";
        double pct   = user.levelProgress();
        int    fill  = (int)(pct * width);
        return "[" + "█".repeat(fill) + "░".repeat(width - fill)
             + String.format("] %3.0f%%", pct * 100);
    }

    public String getMotivationalTip(User user) {
        int lvl = user.getLevel();
        if (lvl == 1)  return "💡 Complete HIGH tasks for 30 coins + 1 gem!";
        if (lvl <= 3)  return "💡 Build a daily streak for bonus coins!";
        if (lvl <= 5)  return "💡 Check the store — you might afford something cool!";
        if (lvl <= 7)  return "💡 7-day streaks award a bonus gem!";
        if (lvl <= 9)  return "💡 Almost at max level — legendary status incoming!";
        return               "🏆 MAX LEVEL! Your coins and gems are endless fuel.";
    }

    /**
     * Attempt to purchase an item for the user.
     * Returns: 0=success, 1=already owned, 2=insufficient coins, 3=insufficient gems
     */
    public int purchase(User user, StoreItem item) {
        if (user.owns(item.getId())) return 1;
        if (item.getCurrency() == StoreItem.Currency.COINS) {
            if (!user.spendCoins(item.getPrice())) return 2;
        } else {
            if (!user.spendGems(item.getPrice())) return 3;
        }
        user.addItem(item.getId());
        return 0;
    }
}
