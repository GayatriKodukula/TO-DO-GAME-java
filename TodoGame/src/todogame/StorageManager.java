package todogame;

import java.io.*;
import java.nio.file.*;
import java.util.*;

/**
 * File layout:
 *   save/
 *     users.index        – one username per line (roster)
 *     <name>/tasks.dat   – that user's tasks
 *     <name>/user.dat    – that user's state
 */
public class StorageManager {

    private static final String SAVE_DIR   = "save";
    private static final String INDEX_FILE = SAVE_DIR + File.separator + "users.index";

    public void init() {
        new File(SAVE_DIR).mkdirs();
    }

    // ── Roster ──────────────────────────────────────────────────────────────
    private String userDir(String name) {
        return SAVE_DIR + File.separator + sanitize(name);
    }

    private String sanitize(String name) {
        return name.replaceAll("[^a-zA-Z0-9_\\-]", "_");
    }

    private void ensureUserDir(String name) {
        new File(userDir(name)).mkdirs();
    }

    private void addToIndex(String name) {
        try {
            List<String> names = readIndex();
            if (!names.contains(name)) {
                names.add(name);
                Files.write(Path.of(INDEX_FILE), String.join("\n", names).getBytes());
            }
        } catch (IOException e) { System.err.println("[Storage] Index write failed: "+e.getMessage()); }
    }

    private List<String> readIndex() {
        Path p = Path.of(INDEX_FILE);
        if (!Files.exists(p)) return new ArrayList<>();
        try {
            List<String> result = new ArrayList<>();
            for (String line : Files.readAllLines(p))
                if (!line.isBlank()) result.add(line.trim());
            return result;
        } catch (IOException e) { return new ArrayList<>(); }
    }

    // ── User ────────────────────────────────────────────────────────────────
    public void saveUser(User user) {
        ensureUserDir(user.getName());
        addToIndex(user.getName());
        try (BufferedWriter w = Files.newBufferedWriter(
                Path.of(userDir(user.getName()) + File.separator + "user.dat"))) {
            w.write(user.toFileString()); w.newLine();
        } catch (IOException e) { System.err.println("[Storage] Save user failed: "+e.getMessage()); }
    }

    public User loadUser(String name) {
        Path p = Path.of(userDir(name) + File.separator + "user.dat");
        if (!Files.exists(p)) return null;
        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line = r.readLine();
            if (line != null && !line.isBlank()) return User.fromFileString(line.trim());
        } catch (IOException e) { System.err.println("[Storage] Load user failed: "+e.getMessage()); }
        return null;
    }

    public List<User> loadAllUsers() {
        List<User> result = new ArrayList<>();
        for (String name : readIndex()) {
            User u = loadUser(name);
            if (u != null) result.add(u);
        }
        return result;
    }

    // ── Tasks ───────────────────────────────────────────────────────────────
    public void saveTasks(String username, TaskManager manager) {
        ensureUserDir(username);
        try (BufferedWriter w = Files.newBufferedWriter(
                Path.of(userDir(username) + File.separator + "tasks.dat"))) {
            for (Task t : manager.getAllTasks()) { w.write(t.toFileString()); w.newLine(); }
        } catch (IOException e) { System.err.println("[Storage] Save tasks failed: "+e.getMessage()); }
    }

    public List<Task> loadTasks(String username) {
        List<Task> tasks = new ArrayList<>();
        Path p = Path.of(userDir(username) + File.separator + "tasks.dat");
        if (!Files.exists(p)) return tasks;
        try (BufferedReader r = Files.newBufferedReader(p)) {
            String line;
            while ((line = r.readLine()) != null) {
                line = line.trim();
                if (!line.isEmpty()) {
                    try { tasks.add(Task.fromFileString(line)); }
                    catch (Exception e) { System.err.println("[Storage] Bad task: "+line); }
                }
            }
        } catch (IOException e) { System.err.println("[Storage] Load tasks failed: "+e.getMessage()); }
        return tasks;
    }

    public void saveAll(User user, TaskManager manager) {
        saveUser(user);
        saveTasks(user.getName(), manager);
    }
}
