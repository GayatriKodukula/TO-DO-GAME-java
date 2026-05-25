package todogame.ui;

import todogame.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Store panel — items grouped by category, with wallet display and purchase flow.
 */
public class StorePanel extends JPanel {

    public interface PurchaseCallback { void onPurchase(StoreItem item, int coins, int gems); }

    private final User             user;
    private final GameSystem       game;
    private final PurchaseCallback callback;

    private String selectedCategory = "ALL";
    private JPanel itemGrid;
    private JLabel walletLabel;

    public StorePanel(User user, GameSystem game, PurchaseCallback callback) {
        this.user=user; this.game=game; this.callback=callback;
        setBackground(Theme.BG_DEEP); setLayout(new BorderLayout());
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
        JLabel title = new JLabel("🛒  ITEM STORE");
        title.setFont(new Font("Monospaced",Font.BOLD,18)); title.setForeground(Theme.COIN_GOLD);
        JLabel sub = new JLabel("Spend your hard-earned coins and gems on fun items!");
        sub.setFont(Theme.FONT_SMALL); sub.setForeground(Theme.TEXT_SECONDARY);
        titleCol.add(title); titleCol.add(sub);

        walletLabel = new JLabel(walletText());
        walletLabel.setFont(new Font("Monospaced",Font.BOLD,14));
        walletLabel.setForeground(Theme.TEXT_PRIMARY);

        header.add(titleCol,    BorderLayout.WEST);
        header.add(walletLabel, BorderLayout.EAST);
        add(header, BorderLayout.NORTH);

        // Category bar + grid
        JPanel centre = new JPanel(new BorderLayout());
        centre.setBackground(Theme.BG_DEEP);
        centre.add(buildCatBar(), BorderLayout.NORTH);

        itemGrid = new JPanel(new GridLayout(0,3,12,12));
        itemGrid.setBackground(Theme.BG_DEEP);
        itemGrid.setBorder(new EmptyBorder(Theme.PAD_MD,Theme.PAD_LG,Theme.PAD_MD,Theme.PAD_LG));

        JScrollPane scroll = new JScrollPane(itemGrid);
        scroll.setBorder(null); scroll.getViewport().setBackground(Theme.BG_DEEP);
        centre.add(scroll, BorderLayout.CENTER);
        add(centre, BorderLayout.CENTER);

        refreshGrid();
    }

    private JPanel buildCatBar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        bar.setBackground(Theme.BG_DEEP);
        bar.setBorder(new EmptyBorder(4,Theme.PAD_LG,0,Theme.PAD_LG));

        addCat(bar,"ALL","🌟 All");
        for (StoreItem.Category cat : StoreItem.Category.values())
            addCat(bar,cat.name(),cat.getIcon()+" "+cat.getLabel());
        return bar;
    }

    private void addCat(JPanel bar, String key, String label) {
        GameButton btn = new GameButton(label,
                "ALL".equals(key)?Theme.COIN_GOLD:Theme.GEM_PURPLE);
        int w = label.length()>10 ? 170 : 140;
        btn.setPreferredSize(new Dimension(w,28));
        btn.addActionListener(e->{ selectedCategory=key; refreshGrid(); });
        bar.add(btn);
    }

    public void refreshGrid() {
        itemGrid.removeAll();
        walletLabel.setText(walletText());

        List<StoreItem> items = StoreRegistry.getAll().stream()
                .filter(it -> "ALL".equals(selectedCategory)
                        || it.getCategory().name().equals(selectedCategory))
                .collect(Collectors.toList());

        for (StoreItem item : items) itemGrid.add(buildItemCard(item));

        // Pad last row
        int cols=3, rem=items.size()%cols;
        if(rem>0) for(int i=0;i<cols-rem;i++){
            JPanel f=new JPanel(); f.setOpaque(false); itemGrid.add(f);
        }
        itemGrid.revalidate(); itemGrid.repaint();
    }

    private JPanel buildItemCard(StoreItem item) {
        boolean owned = user.owns(item.getId());
        boolean isGem = item.getCurrency()==StoreItem.Currency.GEMS;
        Color borderCol = owned ? Theme.ACCENT_GREEN : isGem ? Theme.GEM_PURPLE : Theme.COIN_AMBER;

        final boolean[] hov = {false};
        JPanel card = new JPanel(new BorderLayout(0,8)) {
            @Override protected void paintComponent(Graphics g) {
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                Color bg = owned ? Theme.BG_OWNED
                         : hov[0] ? Theme.BG_HOVER : Theme.BG_CARD;
                g2.setColor(bg);
                g2.fillRoundRect(0,0,getWidth(),getHeight(),Theme.RADIUS,Theme.RADIUS);
                g2.setColor(borderCol);
                g2.setStroke(new BasicStroke(owned||hov[0]?2f:1f));
                g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,Theme.RADIUS,Theme.RADIUS);
                // Gem shimmer top highlight
                if (isGem && !owned) {
                    g2.setPaint(new GradientPaint(0,0,new Color(0xBB,0x86,0xFC,30),0,40,new Color(0,0,0,0)));
                    g2.fillRoundRect(0,0,getWidth(),40,Theme.RADIUS,Theme.RADIUS);
                }
                g2.dispose();
            }
        };
        card.setOpaque(false);
        card.setBorder(new EmptyBorder(Theme.PAD_MD,Theme.PAD_MD,Theme.PAD_MD,Theme.PAD_MD));

        if (!owned) {
            card.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            card.addMouseListener(new MouseAdapter(){
                public void mouseEntered(MouseEvent e){hov[0]=true; card.repaint();}
                public void mouseExited (MouseEvent e){hov[0]=false;card.repaint();}
            });
        }

        // Big emoji icon
        JLabel iconLbl = new JLabel(item.getIcon(),SwingConstants.CENTER);
        iconLbl.setFont(new Font("Dialog",Font.PLAIN,40));

        // Name
        JLabel nameLbl = new JLabel(item.getName(),SwingConstants.CENTER);
        nameLbl.setFont(Theme.FONT_BADGE);
        nameLbl.setForeground(owned?Theme.ACCENT_GREEN:Theme.TEXT_PRIMARY);

        // Category
        JLabel catLbl = new JLabel(item.getCategory().getIcon()+" "+item.getCategory().getLabel(), SwingConstants.CENTER);
        catLbl.setFont(Theme.FONT_SMALL); catLbl.setForeground(Theme.TEXT_MUTED);

        // Description
        JLabel descLbl = new JLabel("<html><center>"+item.getDescription()+"</center></html>",SwingConstants.CENTER);
        descLbl.setFont(Theme.FONT_SMALL); descLbl.setForeground(Theme.TEXT_SECONDARY);

        // Price / owned
        JPanel bottomRow = new JPanel(new FlowLayout(FlowLayout.CENTER,4,0));
        bottomRow.setOpaque(false);

        if (owned) {
            JLabel ownedLbl = new JLabel("✅ Owned",SwingConstants.CENTER);
            ownedLbl.setFont(Theme.FONT_BADGE); ownedLbl.setForeground(Theme.ACCENT_GREEN);
            bottomRow.add(ownedLbl);
        } else {
            GameButton buyBtn = new GameButton(item.getPriceDisplay(),
                    isGem?Theme.GEM_PURPLE:Theme.COIN_AMBER);
            buyBtn.setPreferredSize(new Dimension(160,30));
            buyBtn.addActionListener(e->onBuy(item));
            bottomRow.add(buyBtn);
        }

        JPanel info = new JPanel(new GridLayout(4,1,0,4)); info.setOpaque(false);
        info.add(nameLbl); info.add(catLbl); info.add(descLbl); info.add(bottomRow);

        card.add(iconLbl,BorderLayout.NORTH);
        card.add(info,BorderLayout.CENTER);
        return card;
    }

    private void onBuy(StoreItem item) {
        int result = game.purchase(user,item);
        switch (result) {
            case 0 -> {
                callback.onPurchase(item,user.getCoins(),user.getGems());
                refreshGrid();
                showPurchasePopup(item);
            }
            case 1 -> JOptionPane.showMessageDialog(this,"You already own this item!","Owned",JOptionPane.INFORMATION_MESSAGE);
            case 2 -> JOptionPane.showMessageDialog(this,
                    "Not enough coins! Need "+item.getPrice()+" 🪙, you have "+user.getCoins()+".",
                    "Insufficient Coins",JOptionPane.WARNING_MESSAGE);
            case 3 -> JOptionPane.showMessageDialog(this,
                    "Not enough gems! Need "+item.getPrice()+" 💎, you have "+user.getGems()+".",
                    "Insufficient Gems",JOptionPane.WARNING_MESSAGE);
        }
    }

    private void showPurchasePopup(StoreItem item) {
        Window win = SwingUtilities.getWindowAncestor(this);
        JWindow popup = new JWindow(win);
        popup.setBackground(new Color(0,0,0,0));

        final float[] a={0};
        JPanel p = new JPanel(new GridLayout(2,1,0,6)){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,a[0]));
                g2.setColor(new Color(0x05,0x18,0x0C,238));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),18,18);
                g2.setColor(Theme.ACCENT_GREEN); g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,18,18);
                g2.dispose(); super.paintComponent(g);
            }
        };
        p.setOpaque(false);
        p.setBorder(new EmptyBorder(16,28,16,28));

        JLabel l1=new JLabel(item.getIcon()+"  Purchased!",SwingConstants.CENTER);
        l1.setFont(new Font("Dialog",Font.BOLD,20)); l1.setForeground(Theme.ACCENT_GREEN);
        JLabel l2=new JLabel(item.getName()+" added to inventory",SwingConstants.CENTER);
        l2.setFont(Theme.FONT_BODY); l2.setForeground(Theme.TEXT_PRIMARY);

        p.add(l1); p.add(l2);
        popup.setContentPane(p); popup.pack();
        if(win!=null){
            Rectangle b=win.getBounds();
            popup.setLocation(b.x+(b.width-popup.getWidth())/2,b.y+(b.height-popup.getHeight())/2-70);
        }
        popup.setVisible(true);

        Timer fadeIn=new Timer(16,null);
        fadeIn.addActionListener(e->{
            a[0]=Math.min(1f,a[0]+0.1f); popup.repaint();
            if(a[0]>=1f){
                ((Timer)e.getSource()).stop();
                Timer hold=new Timer(1600,e2->{
                    ((Timer)e2.getSource()).stop();
                    Timer fadeOut=new Timer(20,e3->{
                        a[0]=Math.max(0f,a[0]-0.07f); popup.repaint();
                        if(a[0]<=0f){((Timer)e3.getSource()).stop();popup.dispose();}
                    }); fadeOut.start();
                }); hold.setRepeats(false); hold.start();
            }
        }); fadeIn.start();
    }

    private String walletText() {
        return "🪙 " + user.getCoins() + " Coins    💎 " + user.getGems() + " Gems";
    }
}
