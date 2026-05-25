package todogame;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages the roster of all player profiles on this device.
 * Each player gets their own save subdirectory via StorageManager.
 */
public class UserManager {

    private final List<User>   users   = new ArrayList<>();
    private final StorageManager storage;

    public UserManager(StorageManager storage) {
        this.storage = storage;
    }

    /** Load all saved user profiles from disk. */
    public void loadAll() {
        users.clear();
        users.addAll(storage.loadAllUsers());
    }

    public List<User> getUsers() { return new ArrayList<>(users); }

    public boolean nameExists(String name) {
        return users.stream().anyMatch(u -> u.getName().equalsIgnoreCase(name));
    }

    /**
     * Creates and registers a brand-new user.
     * @throws IllegalArgumentException if name is taken or blank.
     */
    public User createUser(String name) {
        name = name.trim();
        if (name.isBlank())          throw new IllegalArgumentException("Name cannot be blank.");
        if (nameExists(name))        throw new IllegalArgumentException("Name already taken.");
        User u = new User(name);
        users.add(u);
        storage.saveUser(u);
        return u;
    }

    /** Find an existing user by name (case-insensitive). Returns null if not found. */
    public User findByName(String name) {
        return users.stream()
                .filter(u -> u.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    /** Persist one user's current state. */
    public void save(User u) { storage.saveUser(u); }

    /** Returns a leaderboard: users sorted by level desc, then coins+gems desc. */
    public List<User> getLeaderboard() {
        List<User> lb = new ArrayList<>(users);
        lb.sort((a, b) -> {
            if (b.getLevel() != a.getLevel()) return b.getLevel() - a.getLevel();
            int aWealth = a.getCoins() + a.getGems() * 10;
            int bWealth = b.getCoins() + b.getGems() * 10;
            return bWealth - aWealth;
        });
        return lb;
    }
}
