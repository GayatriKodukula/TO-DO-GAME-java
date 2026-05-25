package todogame.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.RoundRectangle2D;

// ── RoundedPanel ──────────────────────────────────────────────────────────────
class RoundedPanel extends JPanel {
    private final Color bgColor, borderColor;
    private final int radius;

    RoundedPanel(Color bg, int radius)              { this(bg, radius, null); }
    RoundedPanel(Color bg, int radius, Color border) {
        this.bgColor=bg; this.radius=radius; this.borderColor=border; setOpaque(false);
    }
    @Override protected void paintComponent(Graphics g) {
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(bgColor); g2.fillRoundRect(0,0,getWidth(),getHeight(),radius,radius);
        if (borderColor!=null) {
            g2.setColor(borderColor); g2.setStroke(new BasicStroke(1f));
            g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,radius,radius);
        }
        g2.dispose(); super.paintComponent(g);
    }
}

// ── GameButton ────────────────────────────────────────────────────────────────
class GameButton extends JButton {
    private final Color accent;
    private boolean hovered, pressed;

    GameButton(String text, Color accent) {
        super(text); this.accent=accent;
        setOpaque(false); setContentAreaFilled(false); setBorderPainted(false);
        setFocusPainted(false); setFont(Theme.FONT_BADGE);
        setForeground(Theme.TEXT_PRIMARY);
        setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        setPreferredSize(new Dimension(130,34));
        addMouseListener(new MouseAdapter(){
            public void mouseEntered(MouseEvent e){hovered=true; repaint();}
            public void mouseExited (MouseEvent e){hovered=false;repaint();}
            public void mousePressed(MouseEvent e){pressed=true; repaint();}
            public void mouseReleased(MouseEvent e){pressed=false;repaint();}
        });
    }
    @Override protected void paintComponent(Graphics g){
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        Color bg=pressed?accent.darker():hovered?accent.brighter()
                :new Color(accent.getRed(),accent.getGreen(),accent.getBlue(),200);
        g2.setColor(bg); g2.fillRoundRect(0,0,getWidth(),getHeight(),8,8);
        g2.setColor(new Color(255,255,255,30));
        g2.fillRoundRect(2,2,getWidth()-4,getHeight()/2,8,8);
        g2.dispose(); super.paintComponent(g);
    }
    @Override protected void paintBorder(Graphics g){
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setColor(accent.brighter()); g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0,0,getWidth()-1,getHeight()-1,8,8); g2.dispose();
    }
}

// ── XPBar ─────────────────────────────────────────────────────────────────────
class XPBar extends JPanel {
    private double target,current;
    private Timer animator;
    private String leftLabel="",rightLabel="";

    XPBar(){ setOpaque(false); setPreferredSize(new Dimension(200,20)); }

    void setProgress(double pct){
        target=Math.max(0,Math.min(1,pct));
        if(animator!=null&&animator.isRunning()) animator.stop();
        animator=new Timer(16,e->{
            double d=target-current;
            if(Math.abs(d)<0.004){current=target;((Timer)e.getSource()).stop();}
            else current+=d*0.12;
            repaint();
        }); animator.start();
    }
    void setLabels(String l,String r){leftLabel=l;rightLabel=r;repaint();}

    @Override protected void paintComponent(Graphics g){
        super.paintComponent(g);
        Graphics2D g2=(Graphics2D)g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
        int w=getWidth(),h=getHeight(),r=h/2;
        g2.setColor(Theme.BG_INPUT); g2.fillRoundRect(0,0,w,h,r,r);
        int fw=(int)(w*current);
        if(fw>0){
            g2.setPaint(new GradientPaint(0,0,Theme.COIN_AMBER,fw,0,Theme.COIN_GOLD));
            g2.fill(new RoundRectangle2D.Float(0,0,fw,h,r,r));
            g2.setColor(new Color(255,255,255,40));
            g2.fillRoundRect(4,2,Math.max(0,fw-8),h/2-2,4,4);
        }
        g2.setColor(Theme.BORDER); g2.setStroke(new BasicStroke(1f));
        g2.drawRoundRect(0,0,w-1,h-1,r,r);
        FontMetrics fm=g2.getFontMetrics(Theme.FONT_SMALL);
        int ty=(h+fm.getAscent()-fm.getDescent())/2;
        g2.setFont(Theme.FONT_SMALL);
        if(!leftLabel.isEmpty()){g2.setColor(Theme.TEXT_PRIMARY);g2.drawString(leftLabel,5,ty);}
        if(!rightLabel.isEmpty()){g2.setColor(Theme.TEXT_SECONDARY);g2.drawString(rightLabel,w-fm.stringWidth(rightLabel)-5,ty);}
        g2.dispose();
    }
}

// ── RewardPopup ───────────────────────────────────────────────────────────────
class RewardPopup extends JWindow {
    private float alpha;
    private Timer fadeIn,hold,fadeOut;

    RewardPopup(Window owner,int coins,int gems,int bonusCoins,int bonusGems,boolean levelUp){
        super(owner); setBackground(new Color(0,0,0,0));

        StringBuilder sb=new StringBuilder();
        if(coins>0)  sb.append("🪙 +").append(coins).append(" Coins  ");
        if(gems>0)   sb.append("💎 +").append(gems).append(" Gem!  ");
        String line1=sb.toString().trim();

        StringBuilder sb2=new StringBuilder();
        if(bonusCoins>0) sb2.append("🔥 Streak +").append(bonusCoins).append(" Coins  ");
        if(bonusGems>0)  sb2.append("💎 Bonus Gem!");
        String line2=sb2.toString().trim();

        int rows=1+(!line2.isEmpty()?1:0)+(levelUp?1:0);
        JPanel panel=new JPanel(new GridLayout(rows,1,0,6)){
            @Override protected void paintComponent(Graphics g){
                Graphics2D g2=(Graphics2D)g.create();
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
                g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER,alpha));
                g2.setColor(new Color(0x08,0x14,0x28,235));
                g2.fillRoundRect(0,0,getWidth(),getHeight(),20,20);
                g2.setColor(Theme.COIN_GOLD); g2.setStroke(new BasicStroke(2f));
                g2.drawRoundRect(1,1,getWidth()-2,getHeight()-2,20,20);
                g2.dispose(); super.paintComponent(g);
            }
        };
        panel.setOpaque(false);
        panel.setBorder(BorderFactory.createEmptyBorder(18,32,18,32));

        panel.add(lbl(line1,Theme.COIN_GOLD,19,Font.BOLD));
        if(!line2.isEmpty()) panel.add(lbl(line2,Theme.ACCENT_GREEN,12,Font.PLAIN));
        if(levelUp) panel.add(lbl("🚀  LEVEL UP!",Theme.GEM_CYAN,17,Font.BOLD));

        setContentPane(panel); pack();
        if(owner!=null){
            Rectangle b=owner.getBounds();
            setLocation(b.x+(b.width-getWidth())/2,b.y+(b.height-getHeight())/2-80);
        }
    }

    private JLabel lbl(String t,Color c,int sz,int style){
        JLabel l=new JLabel(t,SwingConstants.CENTER);
        l.setFont(new Font("Dialog",style,sz)); l.setForeground(c); l.setOpaque(false);
        return l;
    }

    void showAnimated(){
        setVisible(true);
        fadeIn=new Timer(18,e->{alpha=Math.min(1f,alpha+0.09f);repaint();if(alpha>=1f)((Timer)e.getSource()).stop();});
        fadeIn.start();
        hold=new Timer(2200,e->{
            ((Timer)e.getSource()).stop();
            fadeOut=new Timer(20,ev->{alpha=Math.max(0f,alpha-0.05f);repaint();if(alpha<=0f){((Timer)ev.getSource()).stop();dispose();}});
            fadeOut.start();
        });
        hold.setRepeats(false); hold.start();
    }
}
