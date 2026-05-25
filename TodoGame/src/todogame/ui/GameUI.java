package todogame.ui;

import todogame.*;
import javax.swing.*;
import java.awt.*;

public class GameUI {

    static final StorageManager storage = new StorageManager();
    static final UserManager    userMgr = new UserManager(storage);
    static final GameSystem     game    = new GameSystem();
    static JFrame shellFrame;

    public static void main(String[] args) {
        applyDarkDefaults();
        storage.init();
        userMgr.loadAll();

        SwingUtilities.invokeLater(() -> {
            shellFrame = new JFrame("TODO Game");
            shellFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            shellFrame.setMinimumSize(new Dimension(900, 620));
            shellFrame.setPreferredSize(new Dimension(1060, 700));
            shellFrame.getContentPane().setBackground(Theme.BG_DEEP);
            shellFrame.setLayout(new BorderLayout());
            showPlayerSelect();
            shellFrame.pack();
            shellFrame.setLocationRelativeTo(null);
            shellFrame.setVisible(true);
        });
    }

    static void showPlayerSelect() {
        userMgr.loadAll();
        swapContent(new PlayerSelectScreen(userMgr, storage, GameUI::launchGame));
        shellFrame.setTitle("TODO Game  —  Select Player");
    }

    static void launchGame(User user) {
        TaskManager manager = new TaskManager();
        manager.loadAll(storage.loadTasks(user.getName()));
        swapContent(new GamePanel(manager, user, game, storage, GameUI::showPlayerSelect));
        shellFrame.setTitle("TODO Game  ·  " + user.getName());
    }

    static void swapContent(JComponent panel) {
        shellFrame.getContentPane().removeAll();
        shellFrame.getContentPane().add(panel, BorderLayout.CENTER);
        shellFrame.getContentPane().revalidate();
        shellFrame.getContentPane().repaint();
    }

    private static void applyDarkDefaults() {
        try { UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()); } catch(Exception ignored){}
        UIManager.put("OptionPane.background",        Theme.BG_PANEL);
        UIManager.put("Panel.background",             Theme.BG_PANEL);
        UIManager.put("OptionPane.messageForeground", Theme.TEXT_PRIMARY);
        UIManager.put("Button.background",            Theme.BG_CARD);
        UIManager.put("Button.foreground",            Theme.TEXT_PRIMARY);
        UIManager.put("Button.font",                  Theme.FONT_BODY);
        UIManager.put("TextField.background",         Theme.BG_INPUT);
        UIManager.put("TextField.foreground",         Theme.TEXT_PRIMARY);
        UIManager.put("TextField.caretForeground",    Theme.GEM_CYAN);
        UIManager.put("TextArea.background",          Theme.BG_INPUT);
        UIManager.put("TextArea.foreground",          Theme.TEXT_PRIMARY);
        UIManager.put("ComboBox.background",          Theme.BG_INPUT);
        UIManager.put("ComboBox.foreground",          Theme.TEXT_PRIMARY);
        UIManager.put("ScrollPane.background",        Theme.BG_DEEP);
        UIManager.put("ScrollBar.background",         Theme.BG_PANEL);
        UIManager.put("ScrollBar.thumb",              Theme.BG_HOVER);
        UIManager.put("List.background",              Theme.BG_DEEP);
        UIManager.put("List.foreground",              Theme.TEXT_PRIMARY);
        UIManager.put("List.selectionBackground",     Theme.BG_HOVER);
        UIManager.put("List.selectionForeground",     Theme.TEXT_PRIMARY);
        UIManager.put("TabbedPane.background",        Theme.BG_DEEP);
        UIManager.put("TabbedPane.foreground",        Theme.TEXT_PRIMARY);
        UIManager.put("TabbedPane.selected",          Theme.BG_CARD);
        UIManager.put("Separator.foreground",         Theme.BORDER);
    }
}
