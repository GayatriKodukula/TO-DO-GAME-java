package todogame.ui;

import todogame.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * Full-screen player selection screen.
 * Shows player cards, leaderboard, and New Player button.
 */
public class PlayerSelectScreen extends JPanel {

    public interface PlayerChosenCallback { void onPlayerChosen(User user); }

    private final UserManager          userManager;
    private final StorageManager       storage;
    private final PlayerChosenCallback callback;
    private JPanel playerGrid;

    public PlayerSelectScreen(UserManager um, StorageManager st, PlayerChosenCallback cb) {
        this.userManager=um; this.storage=st; this.callback=cb;
        setBackground(Theme.BG_DEEP); setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // ── Header ─────────────────────────────────────────────────────────
        JPanel header = new JPanel(new GridLayout(3,1,0,5));
        header.setBackground(Theme.BG_DEEP);
        header.setBorder(new EmptyBorder(44,0,24,0));

        JLabel logo = new JLabel("🎮  TODO  GAME", SwingConstants.CENTER);
        logo.setFont(new Font("Monospaced",Font.BOLD,34)); logo.setForeground(Theme.COIN_GOLD);

        JLabel sub = new JLabel("Complete Tasks  ·  Earn Coins & Gems  ·  Shop  ·  Level Up", SwingConstants.CENTER);
        sub.setFont(Theme.FONT_BODY); sub.setForeground(Theme.TEXT_SECONDARY);

        JLabel choose = new JLabel("— Who's playing? —", SwingConstants.CENTER);
        choose.setFont(Theme.FONT_HEADING); choose.setForeground(Theme.GEM_CYAN);

        header.add(logo); header.add(sub); header.add(choose);
        add(header, BorderLayout.NORTH);

        // ── Body ───────────────────────────────────────────────────────────
        JPanel body = new JPanel(new BorderLayout(Theme.PAD_LG,0));
        body.setBackground(Theme.BG_DEEP);
        body.setBorder(new EmptyBorder(0,60,0,60));

        playerGrid = new JPanel();
        playerGrid.setLayout(new BoxLayout(playerGrid, BoxLayout.Y_AXIS));
        playerGrid.setBackground(Theme.BG_DEEP);
        rebuildPlayerGrid();

        JScrollPane scroll = new JScrollPane(playerGrid);
        scroll.setBorder(null); scroll.getViewport().setBackground(Theme.BG_DEEP);
        scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);

        body.add(scroll,           BorderLayout.CENTER);
        body.add(buildLeaderboard(), BorderLayout.EAST);
        add(body, BorderLayout.CENTER);

        // ── Footer ─────────────────────────────────────────────────────────
        JPanel footer = new JPanel(new FlowLayout(FlowLayout.CENTER,0,18));
        footer.setBackground(Theme.BG_DEEP);
        footer.setBorder(new MatteBorder(1,0,0,0,Theme.BORDER));

        GameButton newBtn = new GameButton("✨  New Player", Theme.ACCENT_GREEN);
        newBtn.setPreferredSize(new Dimension(200,44));
        newBtn.setFont(new Font("Monospaced",Font.BOLD,14));
        newBtn.addActionListener(e -> onNewPlayer());
        footer.add(newBtn);
        add(footer, BorderLayout.SOUTH);
    }

    private void rebuildPlayerGrid() {
        playerGrid.removeAll();
        List<User> users = userManager.getUsers();
        if (users.isEmpty()) {
            JLabel empty = new JLabel("No players yet — create one below!", SwingConstants.CENTER);
            empty.setFont(Theme.FONT_BODY); empty.setForeground(Theme.TEXT_MUTED);
            empty.setAlignmentX(Component.CENTER_ALIGNMENT);
            playerGrid.add(Box.createVerticalStrut(50));
            playerGrid.add(empty);
        } else {
            for (User u : users) {
                playerGrid.add(buildPlayerCard(u));
                playerGrid.add(Box.createVerticalStrut(10));
            }
        }
        playerGrid.revalidate(); playerGrid.repaint();
    }

    private JPanel buildPlayerCard(User user) {
        final boolean[] hov = {false};

        JPanel card = new JPanel(new BorderLayout(Theme.PAD_MD,0)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                // Gradient background on hover
                if (hov[0]) {
                    g2.setPaint(new GradientPaint(0,0,Theme.BG_HOVER,getWidth(),0,
                            new Color(0x1A,0x28,0x44)));
                } else {
                    g2.setColor(Theme.BG_CARD);
                }
                g2.fillRoundRect(0,0,getWidth(),getHeight(),Theme.RADIUS,Theme.RADIUS);
                g2.setColor(hov[0]?Theme.COIN_GOLD:Theme.BORDER);
                g2.setStroke(new BasicStroke(hov[0]?1.8f:0.8f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,Theme.RADIUS,Theme.RADIUS);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(14,18,14,18));
        card.setMaximumSize(new Dimension(Integer.MAX_VALUE,96));
        card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        card.addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){hov[0]=true; card.repaint();}
            public void mouseExited (MouseEvent e){hov[0]=false;card.repaint();}
            public void mouseClicked(MouseEvent e){callback.onPlayerChosen(user);}
        });

        // Avatar
        JLabel avatar = new JLabel(String.valueOf(user.getName().charAt(0)).toUpperCase(), SwingConstants.CENTER){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                // Radial-ish gradient for the avatar
                Color base = avatarColor(user.getName());
                g2.setPaint(new RadialGradientPaint(new Point(getWidth()/2,getHeight()/2),
                        getWidth()/2f, new float[]{0f,1f},
                        new Color[]{base.brighter(), base.darker()}));
                g2.fillOval(0,0,getWidth(),getHeight());
                g2.dispose(); super.paintComponent(g);
            }
        };
        avatar.setFont(new Font("Monospaced",Font.BOLD,24));
        avatar.setForeground(Color.WHITE);
        avatar.setPreferredSize(new Dimension(56,56));

        // Info panel
        JPanel info = new JPanel(new GridLayout(3,1,0,2)); info.setOpaque(false);

        JLabel nm = new JLabel(user.getName());
        nm.setFont(new Font("Monospaced",Font.BOLD,16)); nm.setForeground(Theme.TEXT_PRIMARY);

        JLabel stats = new JLabel(String.format(
                "Lv.%d  |  🪙 %d Coins  |  💎 %d Gems  |  🔥 %d-day streak",
                user.getLevel(), user.getCoins(), user.getGems(), user.getStreak()));
        stats.setFont(Theme.FONT_SMALL); stats.setForeground(Theme.TEXT_SECONDARY);

        JLabel items = new JLabel("🛒 " + user.getOwnedItemIds().size() + " items in inventory");
        items.setFont(Theme.FONT_SMALL); items.setForeground(Theme.GEM_PURPLE);

        info.add(nm); info.add(stats); info.add(items);

        // Arrow hint
        JLabel arrow = new JLabel("▶", SwingConstants.RIGHT);
        arrow.setFont(new Font("Monospaced",Font.BOLD,20));
        arrow.setForeground(hov[0]?Theme.COIN_GOLD:Theme.TEXT_MUTED);

        card.add(avatar, BorderLayout.WEST);
        card.add(info,   BorderLayout.CENTER);
        card.add(arrow,  BorderLayout.EAST);

        JPanel wrapper = new JPanel(new BorderLayout()); wrapper.setOpaque(false);
        wrapper.add(card);
        return wrapper;
    }

    private JPanel buildLeaderboard() {
        JPanel panel = new JPanel(new BorderLayout(0,Theme.PAD_MD));
        panel.setOpaque(false);
        panel.setPreferredSize(new Dimension(280,0));

        JLabel title = new JLabel("🏆  Leaderboard", SwingConstants.CENTER);
        title.setFont(Theme.FONT_HEADING); title.setForeground(Theme.COIN_GOLD);
        title.setBorder(new EmptyBorder(0,0,10,0));
        panel.add(title, BorderLayout.NORTH);

        JPanel rows = new JPanel();
        rows.setLayout(new BoxLayout(rows,BoxLayout.Y_AXIS));
        rows.setBackground(Theme.BG_PANEL);
        rows.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER_BRIGHT,1),
                new EmptyBorder(Theme.PAD_MD,Theme.PAD_MD,Theme.PAD_MD,Theme.PAD_MD)));

        List<User> lb = userManager.getLeaderboard();
        String[] medals = {"🥇","🥈","🥉"};

        if (lb.isEmpty()) {
            JLabel e=new JLabel("No players yet",SwingConstants.CENTER);
            e.setFont(Theme.FONT_SMALL); e.setForeground(Theme.TEXT_MUTED);
            e.setAlignmentX(Component.CENTER_ALIGNMENT);
            rows.add(e);
        }

        for (int i=0; i<lb.size(); i++) {
            User u=lb.get(i);
            String medal = i<3 ? medals[i] : (i+1)+".";

            JPanel row = new JPanel(new BorderLayout(8,0));
            row.setOpaque(false);
            row.setBorder(new EmptyBorder(6,0,6,0));
            row.setMaximumSize(new Dimension(Integer.MAX_VALUE,50));

            JLabel rank=new JLabel(medal,SwingConstants.CENTER);
            rank.setFont(new Font("Dialog",Font.PLAIN,18));
            rank.setPreferredSize(new Dimension(32,24));

            JPanel mid=new JPanel(new GridLayout(2,1,0,1)); mid.setOpaque(false);
            JLabel nm=new JLabel(u.getName());
            nm.setFont(Theme.FONT_BADGE);
            nm.setForeground(i==0?Theme.COIN_GOLD:i==1?Theme.TEXT_PRIMARY:Theme.TEXT_SECONDARY);
            JLabel st=new JLabel("Lv."+u.getLevel()+"  🪙"+u.getCoins()+"  💎"+u.getGems()+"  🛒"+u.getOwnedItemIds().size());
            st.setFont(Theme.FONT_SMALL); st.setForeground(Theme.TEXT_MUTED);
            mid.add(nm); mid.add(st);

            row.add(rank,BorderLayout.WEST); row.add(mid,BorderLayout.CENTER);
            rows.add(row);

            if (i<lb.size()-1) {
                JSeparator sep=new JSeparator(); sep.setForeground(Theme.BORDER);
                sep.setMaximumSize(new Dimension(Integer.MAX_VALUE,1)); rows.add(sep);
            }
        }

        JScrollPane sc = new JScrollPane(rows);
        sc.setBorder(null); sc.getViewport().setBackground(Theme.BG_PANEL);
        panel.add(sc, BorderLayout.CENTER);
        return panel;
    }

    private void onNewPlayer() {
        JTextField nameField = new JTextField(16);
        nameField.setFont(Theme.FONT_BODY); nameField.setBackground(Theme.BG_INPUT);
        nameField.setForeground(Theme.TEXT_PRIMARY); nameField.setCaretColor(Theme.GEM_CYAN);

        int res = JOptionPane.showConfirmDialog(
                SwingUtilities.getWindowAncestor(this),
                new Object[]{"Enter player name:", nameField},
                "New Player", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (res!=JOptionPane.OK_OPTION) return;
        String name = nameField.getText().trim();
        try {
            User u = userManager.createUser(name);
            rebuildPlayerGrid();
            callback.onPlayerChosen(u);
        } catch (IllegalArgumentException ex) {
            JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this),
                    ex.getMessage(),"Error",JOptionPane.WARNING_MESSAGE);
        }
    }

    private Color avatarColor(String name) {
        float hue = (Math.abs(name.hashCode()) % 360) / 360f;
        return Color.getHSBColor(hue, 0.65f, 0.75f);
    }
}
