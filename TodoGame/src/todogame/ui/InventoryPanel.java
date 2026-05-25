package todogame.ui;

import todogame.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Inventory panel — displays all items owned by the player.
 * Grouped by category with a summary header.
 */
public class InventoryPanel extends JPanel {

    private final User  user;
    private       JPanel grid;
    private       JLabel countLabel;

    public InventoryPanel(User user) {
        this.user=user; setBackground(Theme.BG_DEEP); setLayout(new BorderLayout());
        buildUI();
    }

    private void buildUI() {
        // Header
        JPanel header = new JPanel(new BorderLayout(Theme.PAD_MD,0));
        header.setBackground(Theme.BG_PANEL);
        header.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,Theme.BORDER),
                new EmptyBorder(Theme.PAD_MD,Theme.PAD_LG,Theme.PAD_MD,Theme.PAD_LG)));

        JPanel titleCol = new JPanel(new GridLayout(2,1,0,2)); titleCol.setOpaque(false);
        JLabel title = new JLabel("🎒  MY INVENTORY");
        title.setFont(new Font("Monospaced",Font.BOLD,18)); title.setForeground(Theme.GEM_PURPLE);
        JLabel sub = new JLabel("Everything you've earned and purchased so far");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_SECONDARY);
        titleCol.add(title); titleCol.add(sub);

        countLabel = new JLabel("0 items", SwingConstants.RIGHT);
        countLabel.setFont(Theme.FONT_HEADING); countLabel.setForeground(Theme.GEM_PURPLE);

        header.add(titleCol,   BorderLayout.WEST);
        header.add(countLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Scroll grid
        grid = new JPanel(new GridLayout(0,4,12,12));
        grid.setBackground(Theme.BG_DEEP);
        grid.setBorder(new EmptyBorder(Theme.PAD_LG,Theme.PAD_LG,Theme.PAD_LG,Theme.PAD_LG));

        JScrollPane scroll = new JScrollPane(grid);
        scroll.setBorder(null); scroll.getViewport().setBackground(Theme.BG_DEEP);
        add(scroll, BorderLayout.CENTER);

        refresh();
    }

    public void refresh() {
        grid.removeAll();
        grid.setLayout(new GridLayout(0,4,12,12)); // reset layout

        List<String> ids = new ArrayList<>(user.getOwnedItemIds());
        countLabel.setText(ids.size() + (ids.size()==1?" item":" items"));

        if (ids.isEmpty()) {
            grid.setLayout(new BorderLayout());
            JPanel empty = new JPanel(new GridBagLayout()); empty.setOpaque(false);
            JLabel msg = new JLabel(
                "<html><center><br>Your inventory is empty.<br><br>"
                +"Complete tasks to earn 🪙 Coins and 💎 Gems,<br>"
                +"then head to the Store to buy cool stuff!</center></html>",
                SwingConstants.CENTER);
            msg.setFont(Theme.FONT_BODY); msg.setForeground(Theme.TEXT_MUTED);
            empty.add(msg);
            grid.add(empty, BorderLayout.CENTER);
        } else {
            // Group by category for a nicer display
            Map<StoreItem.Category, List<StoreItem>> grouped = new LinkedHashMap<>();
            for (StoreItem.Category cat : StoreItem.Category.values())
                grouped.put(cat, new ArrayList<>());

            for (String id : ids) {
                StoreItem item = StoreRegistry.findById(id);
                if (item!=null) grouped.get(item.getCategory()).add(item);
            }

            for (Map.Entry<StoreItem.Category, List<StoreItem>> entry : grouped.entrySet()) {
                if (entry.getValue().isEmpty()) continue;
                // Section label spans 4 columns by adding it to a flat list approach —
                // we'll flatten everything into a stream and just add cards
                for (StoreItem item : entry.getValue()) {
                    grid.add(buildItemCard(item));
                }
            }
        }

        grid.revalidate(); grid.repaint();
    }

    private JPanel buildItemCard(StoreItem item) {
        boolean isGem = item.getCurrency()==StoreItem.Currency.GEMS;
        Color glow = isGem ? Theme.GEM_PURPLE : Theme.COIN_AMBER;

        JPanel card = new JPanel(new BorderLayout(0,6)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setColor(Theme.BG_OWNED);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),Theme.RADIUS,Theme.RADIUS);
                // Subtle top sheen using the currency colour
                g2.setPaint(new GradientPaint(0,0,new Color(glow.getRed(),glow.getGreen(),glow.getBlue(),22),
                        0,getHeight()/2,new Color(0,0,0,0)));
                g2.fillRoundRect(0,0,getWidth(),getHeight()/2,Theme.RADIUS,Theme.RADIUS);
                g2.setColor(Theme.ACCENT_GREEN); g2.setStroke(new BasicStroke(1.5f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,Theme.RADIUS,Theme.RADIUS);
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(Theme.PAD_MD,Theme.PAD_SM,Theme.PAD_MD,Theme.PAD_SM));

        // Big icon
        JLabel iconLbl = new JLabel(item.getIcon(),SwingConstants.CENTER);
        iconLbl.setFont(new Font("Dialog",Font.PLAIN,34));

        // Name
        JLabel nameLbl = new JLabel(item.getName(),SwingConstants.CENTER);
        nameLbl.setFont(Theme.FONT_BADGE); nameLbl.setForeground(Theme.ACCENT_GREEN);

        // Category
        JLabel catLbl = new JLabel(item.getCategory().getIcon()+" "+item.getCategory().getLabel(), SwingConstants.CENTER);
        catLbl.setFont(Theme.FONT_SMALL); catLbl.setForeground(Theme.TEXT_MUTED);

        // What was paid
        JLabel paidLbl = new JLabel("Paid: "+item.getPriceDisplay(),SwingConstants.CENTER);
        paidLbl.setFont(Theme.FONT_SMALL);
        paidLbl.setForeground(isGem?Theme.GEM_PURPLE:Theme.COIN_AMBER);

        JPanel info = new JPanel(new GridLayout(3,1,0,3)); info.setOpaque(false);
        info.add(nameLbl); info.add(catLbl); info.add(paidLbl);

        card.add(iconLbl,BorderLayout.NORTH);
        card.add(info,   BorderLayout.CENTER);
        return card;
    }
}
