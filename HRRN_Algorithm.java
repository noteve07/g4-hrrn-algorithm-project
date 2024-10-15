/**
 * ICSC-0113 | OPERATING SYSTEMS | BSCS-SD2A | GROUP 4
 *
 * Highest Response Ratio Next (HRRN) Algorithm
 * This algorithm schedules processes based on their response ratio,
 * optimizing the waiting time and improving overall system performance.
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
    private JTable table;
    private DefaultTableModel model;
        
    
    // output gantt chart components
    private JButton buttonFinish;
    private JLabel labelGanttChart;
    private JLabel labelCalculations;
    private JLabel labelAverageWaitingTime;
    
    // color objects
    private final Color backgroundColor = new Color(209, 222, 222);    
    private final Color underlineColor = new Color(102, 150, 134);
            
    // other properties
    private int WIDTH = 900;
    private int HEIGHT = 600;
    
    private int hoveredRow = -1; // Variable to store the hovered row

    
    // important values for scheduling calculations
    private int numberOfProcesses = 1;
    private ArrayList<Process> processes = new ArrayList<>();
    
    
    
    public HRRN_Algorithm() {
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
        frame.add(panelOutput);
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
        // panel properties
        panelOutput = new JPanel();
        panelOutput.setLayout(null);
        panelOutput.setBounds(0, 0, WIDTH, HEIGHT);

        // gantt chart panel
        panelGanttChart = new JPanel();
        panelGanttChart.setBackground(new Color(150, 150, 150));
        panelGanttChart.setBounds(100, 150, 700, 200);
        

        // components
        buttonFinish = new JButton("Finish");
        
       
        // add components to output panel
        
        panelOutput.add(panelGanttChart);
        panelOutput.add(buttonFinish);
        
    }
    
    
    
    // GANTT CHART AND CALCULATIONS
    public void generateGanttChart() {
        // panel properties
        panelGanttChart = new JPanel();
    }
    
    
    
    
    
    // INPUT TABLE METHODS
    
    public boolean validateTableInput() {
        // traverse through each rows and determine if have blank
        for (int row = 0; row < table.getRowCount(); row++) {
            Object artInput = table.getValueAt(row, 1);
            Object brtInput = table.getValueAt(row, 2);
            if (artInput.equals("___") || brtInput.equals("___")) {
                return false;
            } 
        }                
        // if no ___ is found, then it is not empty
        return true;
    }
    
    
    public int[][] retrieveInputData() {
        // array to store input from the table for each proccesses 
        int[][] dataArray = new int[numberOfProcesses][3];
        for (int row = 0; row < numberOfProcesses; row++) {
            // arrival time and burst time
            int artInput = Integer.parseInt(table.getValueAt(row, 1).toString());
            int brtInput = Integer.parseInt(table.getValueAt(row, 2).toString());
            
            // add each record to an array
            dataArray[row][0] = row + 1;
            dataArray[row][1] = artInput;
            dataArray[row][2] = brtInput;
            
            // LOG
            System.out.println("LOG: retrieveInputData() -> P" + (row+1) + ": " + artInput  + ", " + brtInput);
        } 
        // return the 2d array for instantiation
        return dataArray;
    }
    
    
    public void createProcesses(int[][] inputData) {
        for (int p = 0; p < inputData.length; p++) {
            // get data from each row input
            int id = inputData[p][0];
            int arrivalTime = inputData[p][1];
            int burstTime = inputData[p][2];
            
            // create the process object for this row input
            Process process = new Process(id, arrivalTime, burstTime);
            
            // then add to process collections
            processes.add(process);
        }
    }
    
    public void displayInputError() {
        
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
            case "Run Algorithm" -> {
                // clear selection first to save user input
                if (table.getCellEditor() != null) {
                    table.getCellEditor().stopCellEditing();
                }
                table.clearSelection();
                table.repaint();

                // validate first if input are valid and not empty
                if (validateTableInput()) {
                    // if valid get the table data and convert to processes 
                    System.out.println("LOG: buttonRun -> Input Valid");
                    int[][] inputData = retrieveInputData();
                    createProcesses(inputData);
                } else {
                    // otherwise show an input error
                    System.out.println("LOG: buttonRun -> Input Invalid");
                    displayInputError();
                    break;
                }
                
                
                
                
                
                // show output panel
                System.out.println("LOG: Run Algorithm Button");
                panelInput.setVisible(false);
                panelOutput.setVisible(true);
                panelGanttChart.setVisible(true);
                
            }
            
        }
    }
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                HRRN_Algorithm systemGUI = new HRRN_Algorithm();
            }
        });
    }
}






class Process {
    int id;
    int arrivalTime;
    int burstTime;
    int waitingTime;
    int turnAroundTime;
    double responseRatio;
    
    Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.waitingTime = 0;
        this.turnAroundTime = 0;
        this.responseRatio = 0.0;
    }
}
