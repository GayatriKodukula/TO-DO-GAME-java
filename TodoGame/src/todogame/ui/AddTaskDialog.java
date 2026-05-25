package todogame.ui;

import todogame.Task;
import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;

/** Modal dialog for creating a new task. */
public class AddTaskDialog extends JDialog {

    private Task createdTask;
    private final JTextField  titleField;
    private final JTextArea   descArea;
    private final JComboBox<String> prioBox;
    private final JTextField  dateField;

    public AddTaskDialog(Frame owner) {
        super(owner,"Add New Task",true);
        setUndecorated(true);
        getRootPane().setBorder(BorderFactory.createLineBorder(Theme.BORDER_BRIGHT,1));

        JPanel root = new JPanel(new BorderLayout(0,Theme.PAD_MD));
        root.setBackground(Theme.BG_PANEL);
        root.setBorder(BorderFactory.createEmptyBorder(Theme.PAD_LG,Theme.PAD_LG,Theme.PAD_LG,Theme.PAD_LG));

        JLabel header = new JLabel("  ⚔  ADD NEW TASK");
        header.setFont(Theme.FONT_HEADING); header.setForeground(Theme.GEM_CYAN);
        root.add(header,BorderLayout.NORTH);

        JPanel form = new JPanel(new GridBagLayout()); form.setOpaque(false);
        GridBagConstraints c = new GridBagConstraints();
        c.insets = new Insets(5,0,5,10); c.anchor = GridBagConstraints.WEST;

        titleField = styledField(24);
        addRow(form,c,0,"Title *",titleField);

        descArea = new JTextArea(3,24);
        descArea.setFont(Theme.FONT_BODY); descArea.setForeground(Theme.TEXT_PRIMARY);
        descArea.setBackground(Theme.BG_INPUT); descArea.setCaretColor(Theme.GEM_CYAN);
        descArea.setLineWrap(true);
        descArea.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(4,6,4,6)));
        addRow(form,c,1,"Description",new JScrollPane(descArea){{setBorder(null);}});

        prioBox = new JComboBox<>(new String[]{
            "HIGH — 30 🪙 + 1 💎  (most rewarding!)",
            "MEDIUM — 20 🪙",
            "LOW — 10 🪙"
        });
        prioBox.setFont(Theme.FONT_BODY); prioBox.setForeground(Theme.TEXT_PRIMARY);
        prioBox.setBackground(Theme.BG_INPUT);
        prioBox.setPreferredSize(new Dimension(260,30));
        addRow(form,c,2,"Priority",prioBox);

        dateField = styledField(14);
        dateField.setText("yyyy-MM-dd"); dateField.setForeground(Theme.TEXT_MUTED);
        dateField.addFocusListener(new java.awt.event.FocusAdapter(){
            public void focusGained(java.awt.event.FocusEvent e){
                if(dateField.getText().equals("yyyy-MM-dd")){
                    dateField.setText(""); dateField.setForeground(Theme.TEXT_PRIMARY);}
            }
            public void focusLost(java.awt.event.FocusEvent e){
                if(dateField.getText().isBlank()){
                    dateField.setText("yyyy-MM-dd"); dateField.setForeground(Theme.TEXT_MUTED);}
            }
        });
        addRow(form,c,3,"Due Date (optional)",dateField);
        root.add(form,BorderLayout.CENTER);

        JPanel btns = new JPanel(new FlowLayout(FlowLayout.RIGHT,8,0)); btns.setOpaque(false);
        GameButton cancel = new GameButton("Cancel",Theme.TEXT_MUTED);
        cancel.addActionListener(e->dispose());
        GameButton add = new GameButton("⚡ Add Task",Theme.ACCENT_GREEN);
        add.addActionListener(e->onAdd());
        getRootPane().setDefaultButton(add);
        btns.add(cancel); btns.add(add);
        root.add(btns,BorderLayout.SOUTH);

        setContentPane(root); pack(); setLocationRelativeTo(owner);
    }

    private void onAdd() {
        String title = titleField.getText().trim();
        if (title.isBlank()){
            JOptionPane.showMessageDialog(this,"Title is required.","Validation",JOptionPane.WARNING_MESSAGE);
            return;
        }
        Task.Priority prio = switch(prioBox.getSelectedIndex()){
            case 0 -> Task.Priority.HIGH; case 1 -> Task.Priority.MEDIUM; default -> Task.Priority.LOW;
        };
        LocalDate due = null;
        String ds = dateField.getText().trim();
        if (!ds.isBlank() && !ds.equals("yyyy-MM-dd")) {
            try { due = LocalDate.parse(ds); }
            catch (DateTimeParseException ex){
                JOptionPane.showMessageDialog(this,"Invalid date. Use yyyy-MM-dd.","Date Error",JOptionPane.WARNING_MESSAGE);
                return;
            }
        }
        createdTask = new Task(title,descArea.getText().trim(),prio,due);
        dispose();
    }

    public Task getCreatedTask(){ return createdTask; }

    private JTextField styledField(int cols){
        JTextField f=new JTextField(cols);
        f.setFont(Theme.FONT_BODY); f.setForeground(Theme.TEXT_PRIMARY);
        f.setBackground(Theme.BG_INPUT); f.setCaretColor(Theme.GEM_CYAN);
        f.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(Theme.BORDER),
                BorderFactory.createEmptyBorder(4,6,4,6)));
        return f;
    }

    private void addRow(JPanel p,GridBagConstraints c,int row,String label,Component field){
        c.gridx=0; c.gridy=row; c.fill=GridBagConstraints.NONE;
        JLabel l=new JLabel(label); l.setFont(Theme.FONT_SMALL); l.setForeground(Theme.TEXT_SECONDARY);
        p.add(l,c);
        c.gridx=1; c.fill=GridBagConstraints.HORIZONTAL; c.weightx=1;
        p.add(field,c); c.weightx=0;
    }
}
