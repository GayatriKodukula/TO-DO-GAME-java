package todogame;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Represents a single task. Priority determines reward:
 *   LOW    → 10 Coins
 *   MEDIUM → 20 Coins
 *   HIGH   → 30 Coins + 1 Gem
 */
public class Task {

    public enum Priority {
        LOW   (10, 0, "Low",    "⬇"),
        MEDIUM(20, 0, "Medium", "➡"),
        HIGH  (30, 1, "High",   "⬆");

        private final int coins, gems;
        private final String label, arrow;

        Priority(int coins, int gems, String label, String arrow) {
            this.coins = coins; this.gems = gems;
            this.label = label; this.arrow = arrow;
        }
        public int    getCoins() { return coins; }
        public int    getGems()  { return gems;  }
        public String getLabel() { return label; }
        public String getArrow() { return arrow; }
    }

    private static int nextId = 1;

    private final int      id;
    private       String   title, description;
    private       Priority priority;
    private       boolean  isCompleted;
    private       LocalDate dueDate;

    public Task(String title, String description, Priority priority, LocalDate dueDate) {
        this.id = nextId++; this.title = title;
        this.description = description; this.priority = priority;
        this.isCompleted = false; this.dueDate = dueDate;
    }

    public Task(int id, String title, String description, Priority priority,
                boolean isCompleted, LocalDate dueDate) {
        this.id = id; this.title = title; this.description = description;
        this.priority = priority; this.isCompleted = isCompleted; this.dueDate = dueDate;
        if (id >= nextId) nextId = id + 1;
    }

    public boolean markComplete() {
        if (isCompleted) return false;
        isCompleted = true; return true;
    }
    public void markPending() { isCompleted = false; }

    public int       getId()          { return id; }
    public String    getTitle()       { return title; }
    public String    getDescription() { return description; }
    public Priority  getPriority()    { return priority; }
    public boolean   isCompleted()    { return isCompleted; }
    public LocalDate getDueDate()     { return dueDate; }

    public void setTitle(String t)       { this.title = t; }
    public void setDescription(String d) { this.description = d; }
    public void setPriority(Priority p)  { this.priority = p; }
    public void setDueDate(LocalDate d)  { this.dueDate = d; }

    public String toFileString() {
        String d = dueDate != null ? dueDate.toString() : "null";
        return id+"|"+title+"|"+description+"|"+priority.name()+"|"+isCompleted+"|"+d;
    }

    public static Task fromFileString(String line) {
        String[] p = line.split("\\|", 6);
        if (p.length < 6) throw new IllegalArgumentException("Bad task: " + line);
        return new Task(Integer.parseInt(p[0]), p[1], p[2], Priority.valueOf(p[3]),
                Boolean.parseBoolean(p[4]),
                p[5].equals("null") ? null : LocalDate.parse(p[5]));
    }

    public String toShortString() {
        String c = isCompleted ? "[✓]" : "[ ]";
        return String.format("%s #%-3d %-28s [%s | 🪙%d%s]",
                c, id, title, priority.getLabel(), priority.getCoins(),
                priority.getGems() > 0 ? " 💎" : "");
    }

    @Override public String toString() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern("dd MMM yyyy");
        String due = dueDate != null ? dueDate.format(f) : "No due date";
        return String.format("%s #%-3d %-28s | %-6s | Due: %s",
                isCompleted?"[✓]":"[ ]", id, title, priority.getLabel(), due);
    }
}
