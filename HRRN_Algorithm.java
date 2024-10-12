/**
 * ICSC-0113 | OPERATING SYSTEMS | BSCS-SD2A | GROUP 4
 *
 * NOTE: This is the all-in-one file version of the HRRN algorithm, 
 * compiled for ease of execution. For a more simplified, modular
 * and refactored version, refer to the 'src' directory.
 * 
 */



import java.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.text.NumberFormat;
import javax.swing.text.NumberFormatter;
import javax.swing.border.EmptyBorder;
import javax.swing.border.MatteBorder;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;



public class HRRN_Algorithm implements ActionListener {    
    // frame and panel components
    JFrame frame;
    JPanel panelInput;
    JPanel panelOutput;
    JPanel panelGanttChart;
    
    // number of processes input components
    private JLabel labelNumProcesses;
    private JTextField fieldNumProcesses;
    private JButton buttonNumProceed;
    
    // input panel components
    private JButton buttonLoad;
    private JButton buttonClear;
    private JButton buttonRun;
    
    // input table components
    JTable table;
    DefaultTableModel model;
        
    
    // gantt chart and calculation output components
    
    
    // color objects
    private final Color backgroundColor = new Color(209, 222, 222);    
    private final Color underlineColor = new Color(102, 150, 134);
    private final Color[] processColors = {
        new Color(173, 216, 230), // Pastel Blue
        new Color(119, 221, 119), // Pastel Green
        new Color(255, 255, 153), // Pastel Yellow
        new Color(255, 192, 203), // Pastel Red
        new Color(179, 158, 227), // Pastel Purple
        new Color(255, 204, 153)  // Pastel Orange
    };
            
    // other properties
    private int WIDTH = 900;
    private int HEIGHT = 600;
    
    private int hoveredRow = -1; // Variable to store the hovered row

    
    // important values for scheduling calculations
    private int numberOfProcesses = 1;
    
    
    
    
    public HRRN_GUI_Layout() {
        initializeInputPanel();
        initializeOutputPanel();
        initializeFrame();
        
        panelInput.setVisible(true);
        // panelOutput.setVisible(true);
        // panelGanttChart.setVisible(false);
        
    }
    
    
    // INITIALIZE FRAME
    public void initializeFrame() {
        // frame properties
        frame = new JFrame("Group 4: High Response Ratio Next Algorithm");
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocation(500, 175);
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setBackground(backgroundColor);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // add panels to frame
        frame.add(panelInput);
//        frame.add(panelGanttChart);
    }
    
    
 
    
    
    
    // PANEL 1: INPUT FOR NUMBER OF PROCESSES AND THEIR ARRIVAL AND BURST TIME 
    public void initializeInputPanel() {
        // panel properties
        panelInput = new JPanel();
        panelInput.setLayout(null);
        panelInput.setBounds(0, 0, WIDTH, HEIGHT); // Set size and position
        
        

        // get number of processes input
        labelNumProcesses = new JLabel("Enter Number of Processes (1-6): ");
        fieldNumProcesses = new JTextField();
        buttonNumProceed = new JButton("Proceed");
        
        // set font
        labelNumProcesses.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        fieldNumProcesses.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        buttonNumProceed.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        
        // set bounds
        labelNumProcesses.setBounds(200, 10, 300, 30);
        fieldNumProcesses.setBounds(500, 10, 50, 30);
        buttonNumProceed.setBounds(600, 10, 100, 30);
        buttonNumProceed.addActionListener(this);
        
        // add to panel
        panelInput.add(labelNumProcesses);
        panelInput.add(fieldNumProcesses);
        panelInput.add(buttonNumProceed);
               
        // create button components
        buttonLoad = new JButton("Load Example Data");
        buttonClear = new JButton("Clear");
        buttonRun = new JButton("Run Algorithm");
        buttonLoad.setVisible(false);
        buttonClear.setVisible(false);
        buttonRun.setVisible(false);
        
        // set button fonts
        
        // set button size and position
        buttonLoad.setBounds(50, 470, 280, 50);
        buttonClear.setBounds(330, 470, 200, 50);
        buttonRun.setBounds(530, 470, 300, 50);
        
        // add components to panel
        panelInput.add(buttonLoad);
        panelInput.add(buttonClear);
        panelInput.add(buttonRun);
        
        // add action listeners
        buttonLoad.addActionListener(this);        
        buttonClear.addActionListener(this);
        buttonRun.addActionListener(this);
        
        
        
    }
    
    public void createInputTable() {
        // columns and data for the process input table
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time"};        
        Object[][] data = new Object[numberOfProcesses][3];
        for (int i = 1; i <= numberOfProcesses; i++) {
            data[i-1][0] = "P" + i;
            data[i-1][1] = "___";
            data[i-1][2] = "___";
        }
        
        // create the table model
        model = new DefaultTableModel(data, columnNames);

        // create the table component and render design
        table = new JTable(model) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return column != 0; // Only make cells editable for Arrival Time and Burst Time
            }

            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                
                // apply the hover effect to columns 1 and 2
                if (row == hoveredRow && (column == 1 || column == 2)) {
                    c.setBackground(new Color(203, 244, 202)); // Light green background
                    c.setForeground(Color.DARK_GRAY);
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 16));
                    ((JComponent) c).setBorder(BorderFactory.createLineBorder(new Color(183, 224, 182), 2)); // Green outline
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER); // Center the text
                } else if (row == hoveredRow && column == 0) {
                    c.setBackground(new Color(183, 224, 182)); // Light green background
                    c.setForeground(Color.DARK_GRAY);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 16));
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER); // Center the text
                } else {                    
                    c.setBackground(getBackground());
                    c.setForeground(Color.DARK_GRAY);
                    ((JComponent) c).setBorder(null); // No border for non-hovered cells
                }

                // if editing a cell, keep the hover effect intact
                if (isEditing()) {
                    c.setBackground(getBackground()); // Reset background color
                    c.setForeground(Color.BLACK); // Reset color
                    ((JComponent) c).setBorder(BorderFactory.createLineBorder(new Color(183, 224, 182), 2)); // Keep the border during editing
                }

                return c;
            }
        };

        // add mouse motion listener to handle hover and deselect behavior
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());

                // If we hover over a different row
                if (row != hoveredRow) {
                    // If a cell is being edited, stop editing to commit changes
                    if (table.isEditing()) {
                        table.getCellEditor().stopCellEditing(); // Commit changes
                    }
                    
                    hoveredRow = row; // Update the currently hovered row
                    table.clearSelection(); // Optionally clear selection
                    table.repaint(); // Repaint the table to reflect changes
                }
            }
        });


        // customize the cell editor input field
        DefaultCellEditor cellEditor = new DefaultCellEditor(new JTextField()) {
            @Override
            public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row, int column) {
                JTextField editor = (JTextField) super.getTableCellEditorComponent(table, value, isSelected, row, column);
                editor.setBackground(new Color(203, 244, 202)); // Match the cell's background color
                editor.setForeground(Color.DARK_GRAY);
                editor.setFont(new Font("Segoe UI", Font.BOLD, 20));
                editor.setHorizontalAlignment(JTextField.CENTER); // Center text
                editor.setBorder(BorderFactory.createLineBorder(new Color(183, 224, 182), 2)); // Keep the border

                // remove placeholder when cell is selected
                if (editor.getText().equals("___")) {
                    editor.setText("");
                }

                // Add a key listener to restrict input to integers
                editor.addKeyListener(new KeyAdapter() {
                    @Override
                    public void keyTyped(KeyEvent e) {
                        char c = e.getKeyChar();
                        if (!Character.isDigit(c) && c != KeyEvent.VK_BACK_SPACE) {
                            e.consume(); // Ignore non-integer input
                        }
                    }
                });

                return editor;
            }

            @Override
            public boolean stopCellEditing() {
                JTextField editor = (JTextField) getComponent();

                // If the cell is empty after editing, reset the value to "___"
                if (editor.getText().trim().isEmpty()) {
                    editor.setText("___");
                }

                return super.stopCellEditing();
            }
        };

        
        // apply the cell editor to all columns
        table.setDefaultEditor(Object.class, cellEditor);

        // customize table appearance
        table.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        table.setRowHeight((int)(335 / data.length));
        table.setShowGrid(false); // Remove grid lines for a clean look
        table.setIntercellSpacing(new Dimension(0, 0));

        // set table background colors
        table.setBackground(new Color(229, 245, 224));
        table.setForeground(Color.DARK_GRAY);
        table.setSelectionBackground(new Color(183, 224, 182)); // Selection background
        table.setSelectionForeground(Color.BLACK);

        // center the cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setVerticalAlignment(JLabel.CENTER);
        for (int i = 0; i < table.getColumnCount(); i++) {
            table.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // customize the table header
        JTableHeader tableHeader = table.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableHeader.setBackground(new Color(164, 204, 168)); // Header background
        tableHeader.setForeground(new Color(40, 40, 40)); // Header text color
        tableHeader.setPreferredSize(new Dimension(100, 50));
        tableHeader.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(183, 224, 182))); // Underline effect

        // wrap the table in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setBorder(BorderFactory.createMatteBorder(2, 2, 2,2, Color.LIGHT_GRAY));
        scrollPane.getViewport().setBackground(new Color(229, 245, 224)); // Match background with the table

        // set the preferred size and position for the scroll pane
        scrollPane.setPreferredSize(new Dimension(780, 390)); // Adjust size as needed
        scrollPane.setBounds(50, 70, scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height); // Position scroll pane

        // add the scroll pane directly to the panel
        panelInput.add(scrollPane); // Add scroll pane to the main panel
    }



    
    
    
    public void initializeOutputPanel() {
        
    }
    
    
    
    // GANTT CHART AND CALCULATIONS
    public void initializeGanttChartPanel() {
        // panel properties
        panelGanttChart = new JPanel();
    }
    
    
    
    // ACTION LISTENER METHOD
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        
        switch (action) {
            case "Proceed" -> {
                numberOfProcesses = Integer.parseInt(fieldNumProcesses.getText());
                // create the table according to number of processes
                createInputTable();
                // show other buttons
                buttonLoad.setVisible(true);
                buttonClear.setVisible(true);
                buttonRun.setVisible(true);
            }
            case "Clear" -> {
                for (int row = 0; row < table.getRowCount(); row++) {
                    table.setValueAt("___", row, 1);
                    table.setValueAt("___", row, 2);
                }
                table.repaint(); 
            }
            
        }
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                HRRN_GUI_Layout systemGUI = new HRRN_Algorithm();
            }
        });
    }
}





