package todogame;

import java.util.*;
import java.util.stream.Collectors;

public class TaskManager {
    private final ArrayList<Task> tasks = new ArrayList<>();

    public void addTask(Task t)          { tasks.add(t); }
    public void loadAll(List<Task> list) { tasks.clear(); tasks.addAll(list); }

    public Optional<Task> findById(int id) {
        return tasks.stream().filter(t -> t.getId() == id).findFirst();
    }

    public Task completeTask(int id) {
        Optional<Task> opt = findById(id);
        if (opt.isEmpty()) return null;
        Task t = opt.get();
        return t.markComplete() ? t : null;
    }

    public boolean deleteTask(int id)    { return tasks.removeIf(t -> t.getId() == id); }

    public boolean editTask(int id, String title, String desc,
                            Task.Priority prio, java.time.LocalDate due) {
        Optional<Task> opt = findById(id);
        if (opt.isEmpty()) return false;
        Task t = opt.get();
        if (title != null) t.setTitle(title);
        if (desc  != null) t.setDescription(desc);
        if (prio  != null) t.setPriority(prio);
        t.setDueDate(due);
        return true;
    }

    public List<Task> getAllTasks()       { return new ArrayList<>(tasks); }
    public List<Task> getPendingTasks()  { return tasks.stream().filter(t -> !t.isCompleted()).collect(Collectors.toList()); }
    public List<Task> getCompletedTasks(){ return tasks.stream().filter(Task::isCompleted).collect(Collectors.toList()); }

    public List<Task> getSortedByPriority() {
        return tasks.stream()
                .sorted(Comparator.comparingInt((Task t) -> t.getPriority().getCoins()).reversed())
                .collect(Collectors.toList());
    }
    public List<Task> getSortedByDueDate() {
        return tasks.stream()
                .sorted(Comparator.comparing(Task::getDueDate, Comparator.nullsLast(Comparator.naturalOrder())))
                .collect(Collectors.toList());
    }

    public int totalCount()     { return tasks.size(); }
    public int completedCount() { return (int) tasks.stream().filter(Task::isCompleted).count(); }
    public int pendingCount()   { return totalCount() - completedCount(); }
}
