package todogame.ui;

import todogame.Task;
import javax.swing.*;
import java.awt.*;
import java.time.format.DateTimeFormatter;

public class TaskCellRenderer implements ListCellRenderer<Task> {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yy");

    @Override
    public Component getListCellRendererComponent(JList<? extends Task> list, Task task,
            int index, boolean isSelected, boolean cellHasFocus) {

        final boolean sel = isSelected;
        JPanel card = new JPanel(new BorderLayout(Theme.PAD_MD, 0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(sel ? Theme.BG_HOVER : Theme.BG_CARD);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),Theme.RADIUS,Theme.RADIUS);
                // Priority sidebar strip
                g2.setColor(Theme.priorityColor(task.getPriority()));
                g2.fillRoundRect(0,0,5,getHeight(),3,3);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(BorderFactory.createEmptyBorder(8,14,8,12));

        // Left
        JPanel left = new JPanel(new BorderLayout(6,2)); left.setOpaque(false);
        JLabel check = new JLabel(task.isCompleted()?"✅":"⬜");
        check.setFont(new Font("Dialog",Font.PLAIN,15));

        JLabel title = new JLabel(task.isCompleted()
                ? "<html><s>"+task.getTitle()+"</s></html>" : task.getTitle());
        title.setFont(Theme.FONT_HEADING);
        title.setForeground(task.isCompleted()?Theme.TEXT_MUTED:Theme.TEXT_PRIMARY);

        JLabel desc = new JLabel((task.getDescription()!=null&&!task.getDescription().isBlank())
                ? task.getDescription() : " ");
        desc.setFont(Theme.FONT_SMALL); desc.setForeground(Theme.TEXT_SECONDARY);

        JPanel row = new JPanel(new FlowLayout(FlowLayout.LEFT,6,0)); row.setOpaque(false);
        row.add(check); row.add(title);
        left.add(row,  BorderLayout.NORTH);
        left.add(desc, BorderLayout.CENTER);

        // Right badges
        JPanel right = new JPanel(new GridLayout(2,1,0,3)); right.setOpaque(false);

        // Reward badge
        Task.Priority p = task.getPriority();
        String reward = "🪙+" + p.getCoins() + (p.getGems()>0?" 💎+"+p.getGems():"");
        JLabel rewLbl = badge(p.getLabel()+" "+reward, Theme.priorityColor(p));

        // Due date
        String dueStr = task.getDueDate()!=null ? "📅 "+task.getDueDate().format(FMT) : "No due date";
        JLabel dueLbl = new JLabel(dueStr,SwingConstants.RIGHT);
        dueLbl.setFont(Theme.FONT_SMALL); dueLbl.setForeground(Theme.TEXT_SECONDARY);

        right.add(rewLbl); right.add(dueLbl);

        card.add(left, BorderLayout.CENTER);
        card.add(right, BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setOpaque(false);
        wrapper.setBorder(BorderFactory.createEmptyBorder(3,6,3,6));
        wrapper.add(card);
        return wrapper;
    }

    private JLabel badge(String text, Color color) {
        JLabel l = new JLabel(text, SwingConstants.RIGHT) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(new Color(color.getRed(),color.getGreen(),color.getBlue(),35));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),6,6);
                g2.dispose(); super.paintComponent(g);
            }
        };
        l.setFont(Theme.FONT_BADGE); l.setForeground(color);
        l.setBorder(BorderFactory.createEmptyBorder(2,6,2,6)); l.setOpaque(false);
        return l;
    }
}
