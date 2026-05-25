package todogame.ui;

import todogame.*;
import javax.swing.*;
import javax.swing.border.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;

/**
 * Main game panel for a logged-in player.
 * Hosts three tabs: Tasks | Store | Inventory
 */
public class GamePanel extends JPanel {

    private final TaskManager    manager;
    private final User           user;
    private final GameSystem     game;
    private final StorageManager storage;
    private final Runnable       onSwitch;

    private final DefaultListModel<Task> listModel = new DefaultListModel<>();
    private String filterMode = "ALL";
    private JList<Task> taskList;

    private JLabel levelLabel, coinsLabel, gemsLabel, streakLabel, nameLabel, tipLabel;
    private XPBar  xpBar;
    private JLabel totalLbl, doneLbl, pendingLbl;

    private StorePanel     storePanel;
    private InventoryPanel inventoryPanel;

    public GamePanel(TaskManager manager, User user, GameSystem game,
                     StorageManager storage, Runnable onSwitch) {
        this.manager=manager; this.user=user; this.game=game;
        this.storage=storage; this.onSwitch=onSwitch;
        setBackground(Theme.BG_DEEP); setLayout(new BorderLayout());
        buildUI(); refreshAll();
    }

    private void buildUI() {
        add(buildHeader(), BorderLayout.NORTH);

        JTabbedPane tabs = new JTabbedPane();
        tabs.setBackground(Theme.BG_DEEP);
        tabs.setForeground(Theme.TEXT_PRIMARY);
        tabs.setFont(new Font("Monospaced",Font.BOLD,12));

        tabs.addTab("  ✅  Tasks  ",  buildTaskTab());

        storePanel = new StorePanel(user, game, (item,c,g) -> {
            storage.saveAll(user,manager);
            refreshHeader();
            inventoryPanel.refresh();
        });
        tabs.addTab("  🛒  Store  ", storePanel);

        inventoryPanel = new InventoryPanel(user);
        tabs.addTab("  🎒  Inventory  ", inventoryPanel);

        add(tabs, BorderLayout.CENTER);
        add(buildStatusBar(), BorderLayout.SOUTH);
    }

    // ── Header ──────────────────────────────────────────────────────────────
    private JPanel buildHeader() {
        JPanel h = new JPanel(new BorderLayout(Theme.PAD_LG,0));
        h.setBackground(Theme.BG_PANEL);
        h.setBorder(new CompoundBorder(
                new MatteBorder(0,0,1,0,Theme.BORDER),
                new EmptyBorder(Theme.PAD_MD,Theme.PAD_LG,Theme.PAD_MD,Theme.PAD_LG)));

        // Left: logo + player name
        JPanel left = new JPanel(new GridLayout(2,1,0,2)); left.setOpaque(false);
        JLabel logo = new JLabel("🎮  TODO GAME");
        logo.setFont(Theme.FONT_TITLE); logo.setForeground(Theme.COIN_GOLD);
        nameLabel = new JLabel("Player: "+user.getName());
        nameLabel.setFont(Theme.FONT_SMALL); nameLabel.setForeground(Theme.TEXT_SECONDARY);
        left.add(logo); left.add(nameLabel);

        // Centre: level + XP bar + wallet
        JPanel centre = new JPanel(new GridBagLayout()); centre.setOpaque(false);
        GridBagConstraints gc = new GridBagConstraints(); gc.insets=new Insets(2,8,2,8);

        levelLabel = new JLabel("LVL 1");
        levelLabel.setFont(new Font("Monospaced",Font.BOLD,20));
        levelLabel.setForeground(Theme.COIN_GOLD);

        xpBar = new XPBar(); xpBar.setPreferredSize(new Dimension(200,20));

        coinsLabel = new JLabel("🪙 0");
        coinsLabel.setFont(new Font("Monospaced",Font.BOLD,14));
        coinsLabel.setForeground(Theme.COIN_GOLD);

        gemsLabel = new JLabel("💎 0");
        gemsLabel.setFont(new Font("Monospaced",Font.BOLD,14));
        gemsLabel.setForeground(Theme.GEM_PURPLE);

        gc.gridx=0; centre.add(levelLabel,gc);
        gc.gridx=1; centre.add(xpBar,gc);
        gc.gridx=2; centre.add(coinsLabel,gc);
        gc.gridx=3; centre.add(gemsLabel,gc);

        // Right: streak + tip + switch button
        JPanel right = new JPanel(new GridLayout(3,1,0,2)); right.setOpaque(false);
        streakLabel = new JLabel("🔥 0-day streak",SwingConstants.RIGHT);
        streakLabel.setFont(Theme.FONT_HEADING); streakLabel.setForeground(Theme.COIN_AMBER);
        tipLabel = new JLabel("",SwingConstants.RIGHT);
        tipLabel.setFont(Theme.FONT_SMALL); tipLabel.setForeground(Theme.TEXT_SECONDARY);

        JPanel btnRow = new JPanel(new FlowLayout(FlowLayout.RIGHT,0,0)); btnRow.setOpaque(false);
        GameButton switchBtn = new GameButton("👥 Switch Player",Theme.GEM_CYAN);
        switchBtn.setPreferredSize(new Dimension(165,26));
        switchBtn.addActionListener(e->{ storage.saveAll(user,manager); onSwitch.run(); });
        btnRow.add(switchBtn);

        right.add(streakLabel); right.add(tipLabel); right.add(btnRow);

        h.add(left,   BorderLayout.WEST);
        h.add(centre, BorderLayout.CENTER);
        h.add(right,  BorderLayout.EAST);
        return h;
    }

    // ── Task Tab ────────────────────────────────────────────────────────────
    private JPanel buildTaskTab() {
        JPanel tab = new JPanel(new BorderLayout()); tab.setBackground(Theme.BG_DEEP);
        tab.add(buildToolbar(), BorderLayout.NORTH);
        tab.add(buildList(),    BorderLayout.CENTER);
        tab.add(buildActions(), BorderLayout.SOUTH);
        return tab;
    }

    private JPanel buildToolbar() {
        JPanel bar = new JPanel(new FlowLayout(FlowLayout.LEFT,8,8));
        bar.setBackground(Theme.BG_DEEP);
        bar.setBorder(new EmptyBorder(4,Theme.PAD_LG,0,0));
        JLabel fl=new JLabel("Filter:"); fl.setFont(Theme.FONT_SMALL); fl.setForeground(Theme.TEXT_SECONDARY);
        bar.add(fl);
        for (String mode : new String[]{"ALL","PENDING","DONE"}) {
            GameButton btn = new GameButton(mode,
                    "DONE".equals(mode)?Theme.ACCENT_GREEN:"PENDING".equals(mode)?Theme.COIN_AMBER:Theme.ACCENT_BLUE);
            btn.setPreferredSize(new Dimension(90,28));
            btn.addActionListener(e->{filterMode=mode;refreshTaskList();});
            bar.add(btn);
        }
        JPanel stats=new JPanel(new FlowLayout(FlowLayout.RIGHT,14,8)); stats.setBackground(Theme.BG_DEEP);
        totalLbl=sl("Total: 0"); doneLbl=sl("Done: 0"); pendingLbl=sl("Pending: 0");
        stats.add(totalLbl); stats.add(doneLbl); stats.add(pendingLbl);
        JPanel t=new JPanel(new BorderLayout()); t.setBackground(Theme.BG_DEEP);
        t.add(bar,BorderLayout.WEST); t.add(stats,BorderLayout.EAST);
        return t;
    }

    private JScrollPane buildList() {
        taskList=new JList<>(listModel);
        taskList.setCellRenderer(new TaskCellRenderer());
        taskList.setBackground(Theme.BG_DEEP);
        taskList.setFixedCellHeight(Theme.ROW_H);
        taskList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        taskList.setBorder(new EmptyBorder(4,Theme.PAD_MD,4,Theme.PAD_MD));
        taskList.addMouseListener(new MouseAdapter(){
            public void mouseClicked(MouseEvent e){if(e.getClickCount()==2)onComplete();}
        });
        JScrollPane sc=new JScrollPane(taskList); sc.setBorder(null);
        sc.getViewport().setBackground(Theme.BG_DEEP); return sc;
    }

    private JPanel buildActions() {
        JPanel bar=new JPanel(new FlowLayout(FlowLayout.CENTER,12,10));
        bar.setBackground(Theme.BG_PANEL); bar.setBorder(new MatteBorder(1,0,0,0,Theme.BORDER));
        GameButton addBtn=new GameButton("➕ Add Task",Theme.ACCENT_GREEN);
        GameButton doneBtn=new GameButton("✅ Complete",Theme.GEM_CYAN);
        GameButton editBtn=new GameButton("✏ Edit",Theme.COIN_AMBER);
        GameButton delBtn=new GameButton("🗑 Delete",Theme.ACCENT_RED);
        for(GameButton b:new GameButton[]{addBtn,doneBtn,editBtn,delBtn})
            b.setPreferredSize(new Dimension(140,36));
        addBtn.addActionListener(e->onAdd());
        doneBtn.addActionListener(e->onComplete());
        editBtn.addActionListener(e->onEdit());
        delBtn.addActionListener(e->onDelete());
        bar.add(addBtn);bar.add(doneBtn);bar.add(editBtn);bar.add(delBtn);
        return bar;
    }

    private JPanel buildStatusBar() {
        JPanel bar=new JPanel(new FlowLayout(FlowLayout.LEFT,8,4));
        bar.setBackground(Theme.BG_DEEP); bar.setBorder(new MatteBorder(1,0,0,0,Theme.BORDER));
        JLabel h=new JLabel("  Double-click to complete  |  HIGH tasks earn Gems  |  7-day streaks award a bonus Gem  |  Visit Store to spend rewards!");
        h.setFont(Theme.FONT_SMALL); h.setForeground(Theme.TEXT_MUTED); bar.add(h);
        return bar;
    }

    // ── Actions ─────────────────────────────────────────────────────────────
    private void onAdd() {
        Window win=SwingUtilities.getWindowAncestor(this);
        AddTaskDialog dlg=new AddTaskDialog(win instanceof Frame?(Frame)win:null);
        dlg.setVisible(true);
        Task t=dlg.getCreatedTask();
        if(t!=null){ manager.addTask(t); storage.saveAll(user,manager); refreshAll(); }
    }

    private void onComplete() {
        Task sel=taskList.getSelectedValue();
        if(sel==null)        {warn("Select a task first.");return;}
        if(sel.isCompleted()){warn("Task already completed!");return;}
        Task done=manager.completeTask(sel.getId());
        if(done==null) return;
        int[] r=game.processCompletion(user,done,LocalDate.now().toString());
        storage.saveAll(user,manager); refreshAll(); storePanel.refreshGrid();
        Window win=SwingUtilities.getWindowAncestor(this);
        new RewardPopup(win,r[0],r[1],r[2],r[3],r[4]==1).showAnimated();
    }

    private void onEdit() {
        Task sel=taskList.getSelectedValue();
        if(sel==null){warn("Select a task to edit.");return;}
        JTextField tf=new JTextField(sel.getTitle(),20);
        JTextField df=new JTextField(sel.getDescription(),20);
        String[] ps={"HIGH — 30 🪙 + 1 💎","MEDIUM — 20 🪙","LOW — 10 🪙"};
        JComboBox<String> pb=new JComboBox<>(ps);
        pb.setSelectedIndex(switch(sel.getPriority()){case HIGH->0;case MEDIUM->1;default->2;});
        JTextField dateF=new JTextField(sel.getDueDate()!=null?sel.getDueDate().toString():"",12);
        JPanel form=new JPanel(new GridLayout(4,2,8,8)); form.setBackground(Theme.BG_PANEL);
        form.add(fl("Title:")); form.add(tf);
        form.add(fl("Description:")); form.add(df);
        form.add(fl("Priority:")); form.add(pb);
        form.add(fl("Due (yyyy-MM-dd):")); form.add(dateF);
        int res=JOptionPane.showConfirmDialog(this,form,"Edit Task #"+sel.getId(),
                JOptionPane.OK_CANCEL_OPTION,JOptionPane.PLAIN_MESSAGE);
        if(res!=JOptionPane.OK_OPTION)return;
        String nt=tf.getText().trim();if(nt.isBlank()){warn("Title required.");return;}
        Task.Priority np=switch(pb.getSelectedIndex()){case 0->Task.Priority.HIGH;case 1->Task.Priority.MEDIUM;default->Task.Priority.LOW;};
        LocalDate nd=null; String ds=dateF.getText().trim();
        if(!ds.isBlank()){try{nd=LocalDate.parse(ds);}catch(Exception ex){warn("Invalid date.");}}
        manager.editTask(sel.getId(),nt,df.getText().trim(),np,nd);
        storage.saveAll(user,manager); refreshAll();
    }

    private void onDelete() {
        Task sel=taskList.getSelectedValue();
        if(sel==null){warn("Select a task to delete.");return;}
        int c=JOptionPane.showConfirmDialog(this,"Delete \""+sel.getTitle()+"\"?",
                "Confirm Delete",JOptionPane.YES_NO_OPTION,JOptionPane.WARNING_MESSAGE);
        if(c==JOptionPane.YES_OPTION){manager.deleteTask(sel.getId());storage.saveAll(user,manager);refreshAll();}
    }

    // ── Refresh ─────────────────────────────────────────────────────────────
    private void refreshAll() { refreshHeader(); refreshTaskList(); refreshStats(); }

    private void refreshHeader() {
        levelLabel.setText("LVL "+user.getLevel());
        coinsLabel.setText("🪙 "+user.getCoins());
        gemsLabel.setText("💎 "+user.getGems());
        streakLabel.setText("🔥 "+user.getStreak()+"-day streak");
        nameLabel.setText("Player: "+user.getName()+" | Level "+user.getLevel()+"/"+User.MAX_LEVEL);
        tipLabel.setText(game.getMotivationalTip(user));
        xpBar.setProgress(user.levelProgress());
        xpBar.setLabels("",user.getLevel()<User.MAX_LEVEL?user.xpToNextLevel()+" XP to next":"MAX!");
    }

    private void refreshTaskList() {
        Task sel=taskList.getSelectedValue(); listModel.clear();
        List<Task> tasks=switch(filterMode){
            case "PENDING"->manager.getPendingTasks();
            case "DONE"->manager.getCompletedTasks();
            default->manager.getAllTasks();
        };
        for(Task t:tasks) listModel.addElement(t);
        if(sel!=null) for(int i=0;i<listModel.size();i++)
            if(listModel.get(i).getId()==sel.getId()){taskList.setSelectedIndex(i);break;}
    }

    private void refreshStats() {
        totalLbl.setText("Total: "+manager.totalCount());
        doneLbl.setText("Done: "+manager.completedCount());
        pendingLbl.setText("Pending: "+manager.pendingCount());
    }

    private void warn(String m){JOptionPane.showMessageDialog(this,m,"Notice",JOptionPane.INFORMATION_MESSAGE);}
    private JLabel sl(String t){JLabel l=new JLabel(t);l.setFont(Theme.FONT_SMALL);l.setForeground(Theme.TEXT_SECONDARY);return l;}
    private JLabel fl(String t){JLabel l=new JLabel(t);l.setFont(Theme.FONT_SMALL);l.setForeground(Theme.TEXT_SECONDARY);l.setBackground(Theme.BG_PANEL);l.setOpaque(true);return l;}
}
