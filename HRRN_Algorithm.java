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
import java.awt.geom.RoundRectangle2D;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.JTableHeader;



class Process {
    /**
     * A class to store information and attributes 
     * of each processes that will be needed for
     * calculations and generating Gantt Chart.
     */
    int id;
    int arrivalTime;
    int burstTime;
    int startTime;
    int endTime;
    int waitingTime;
    int turnAroundTime;
    double responseRatio;
    
    Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.startTime = 0;
        this.endTime = 0;
        this.waitingTime = 0;
        this.turnAroundTime = 0;
        this.responseRatio = 0.0;
    }
}




public class HRRN_Algorithm implements ActionListener {    
    // declare frame and panel components
    JFrame frame;
    JPanel panelStart;
    JPanel panelInput;
    JPanel panelOutput;
    JPanel panelGanttChart;
    
    // declare start panel components
    private JLabel labelTitle1;
    private JLabel labelHeader;
    private JLabel labelDescription;
    private JLabel labelEnterNumProcesses;
    private JTextField fieldNumProcesses;
    private JButton buttonMinus, buttonPlus;
    private JButton buttonNumProceed;
    
    // declare input table panel components
    private JLabel labelTitle2;
    private JLabel labelInputTable;
    private JLabel labelNumProcesses;
    private JLabel labelNumProcessesInput;
    private JLabel labelInputError;
    private JButton buttonLoad, buttonClear, buttonRun;   
    private DefaultTableModel inputModel;
    private JTable inputTable;
    
        
    // declare output gantt chart components
    private JLabel labelTitle3;
    private JLabel labelGanttChart;
    private JLabel labelCalculations;
    private JLabel labelAverageWaitingTime;
    private JLabel labelAverageTurnAroundTime;
    
    // declare output table componentns
    private JPanel panelOutputTable;
    private JLabel labelOutputTable;
    private DefaultTableModel outputModel;
    private JTable outputTable;
    
    // declare calculations components
    private JPanel panelCalculations;
    private JLabel labelCalculationsHeader;
    private JLabel labelWaitingTimeHeader;
    private JLabel labelTurnAroundTimeHeader;
    private JLabel labelAverageWtEquation;
    private JLabel labelAverageTatEquation;
    
    // declare procedures components
    private JPanel panelProcedures;
    private JLabel labelResponseRatioHeader;
    private JLabel labelResponseRatioFormula;
               
    // declare screen, cursor and other properties
    private int WIDTH = 900;
    private int HEIGHT = 600;
    private int hoveredRow = -1;

    // declare color objects (light mode)
    private final Color primaryColor = new Color(220, 220, 220);
    private final Color textColor = new Color(50, 50, 50);    
    private final Color backgroundColor = new Color(244, 245, 235);     
    private Color ganttChartColors[] = new Color[6];   
    private Color processColors[] = new Color[6];
    
    // declare color objects (dark mode)
    // private final Color primaryColor = new Color(34, 40, 44);
    // private final Color textColor = new Color(235, 235, 235);
    // private final Color backgroundColor = new Color(44, 51, 60);    
    
    // important values and arrays for scheduling calculations 
    private int numberOfProcesses = 4;
    private String currentNumInput = "4";
    private ArrayList<Process> processesInput = new ArrayList<>();
    private ArrayList<Process> scheduledProcesses = new ArrayList<>();
    private HashMap<String, Integer> chartData = new HashMap<>();
    private HashMap<Integer, Process[][]> proceduralData = new HashMap<>();
    private StringBuilder aveWtEquation, aveTatEquation;
    private String formattedAveWt, formattedAveTat;
    
    
    
    // CONSTRUCTOR: program starts here
    public HRRN_Algorithm() {
        // initialize frame and panel components
        initializeStartPanel();
        initializeInputPanel();
        initializeOutputPanel();
        initializeFrame();      
    }
    
    
        
    // INITIALIZE FRAME PROPERTIES
    public void initializeFrame() {
        // create main frame component
        frame = new JFrame("Group 4: High Response Ratio Next Algorithm") {
            @Override
            public void paint(Graphics g) {
                // enable anti-aliasing for smoother edges
                Graphics2D g2 = (Graphics2D) g;
                g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                super.paint(g);
            }
        };
        
        // set frame properties
        frame.setSize(WIDTH, HEIGHT);
        frame.setLocation(500, 175);
        frame.setLocationRelativeTo(null);
        frame.setUndecorated(true);
        frame.setShape(new RoundRectangle2D.Double(0, 0, WIDTH, HEIGHT, 25, 25));
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setBackground(backgroundColor);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // add panels to frame
        frame.add(panelStart);
        frame.add(panelInput);
        frame.add(panelOutput);
        
        // show first panel
        panelStart.setVisible(true);
        panelInput.setVisible(false);
        panelOutput.setVisible(false);
    }
        
    
    
    
    // PANEL 1: ALGORITHM DESCRIPTION AND INPUT FOR NUMBER OF PROCESSES
    public void initializeStartPanel() {
        panelStart = new JPanel () {
            @Override
            protected void paintComponent(Graphics g) {
                // draw rectangle at the top
                super.paintComponent(g);
                g.setColor(primaryColor);
                g.fillRect(0, 0, getWidth(), 40);
                
                // minimize button
                g.setColor(new Color(199, 202, 117));
                g.fillRoundRect(15, 5, 30, 30, 20, 20);
                
                // close button
                g.setColor(new Color(202, 117, 117));
                g.fillRoundRect(getWidth()-45, 5, 30, 30, 20, 20);
                        
                // bottom border
                g.setColor(new Color(200, 200, 200));
                g.fillRect(0, 40, getWidth(), 2);
            }
        };
        
        // set panel properties
        panelStart.setLayout(null);
        panelStart.setBounds(0, 0, WIDTH, HEIGHT);
        panelStart.setBackground(backgroundColor);
        
        createStartComponents();        
    }
    
    
    public void createStartComponents() {
        // LABEL: title 
        labelTitle1 = new JLabel("Group 4 | HRRN Algorithm");
        labelTitle1.setForeground(textColor);
        labelTitle1.setFont(new Font("Segoe UI", Font.BOLD, 18));       
        labelTitle1.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitle1.setBounds(0, 0, 900, 40);
        panelStart.add(labelTitle1);
        
        // LABEL: header
        labelHeader = new JLabel("<html>Welcome to Highest Response Ratio Next (HRRN) Algorithm<br>An Interactive GUI!</html>");
        labelHeader.setForeground(textColor);
        labelHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));       
        labelHeader.setHorizontalAlignment(SwingConstants.LEFT);
        labelHeader.setBounds(55, 80, 900, 40);
        panelStart.add(labelHeader);
        
        // LABEL: description 
        labelDescription = new JLabel("<html>This tool allows you to schedule processes using the HRRN algorithm.<br>Enter the number of processes and their arrival and burst times.</html>");
        labelDescription.setForeground(textColor);
        labelDescription.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        labelDescription.setBounds(55, 140, 800, 60);
        panelStart.add(labelDescription);

        // LABEL: enter number of processes
        labelEnterNumProcesses = new JLabel("Number of Processes");
        labelEnterNumProcesses.setForeground(new Color(110, 160, 130));
        labelEnterNumProcesses.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelEnterNumProcesses.setVerticalAlignment(SwingConstants.CENTER);
        labelEnterNumProcesses.setHorizontalAlignment(SwingConstants.CENTER);
        labelEnterNumProcesses.setBounds(0, 250, 900, 40);
        panelStart.add(labelEnterNumProcesses);        
        
        // FIELD: gets the number of processes input (1-6)
        fieldNumProcesses = new JTextField("4");
        fieldNumProcesses.setForeground(new Color(80, 80, 80)); 
        fieldNumProcesses.setBackground(new Color(240, 240, 240)); 
        fieldNumProcesses.setFont(new Font("Segoe UI", Font.BOLD, 24));
        fieldNumProcesses.setHorizontalAlignment(SwingConstants.CENTER);
        fieldNumProcesses.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 0, new Color(200, 200, 200)));
        fieldNumProcesses.setBounds(425, 300, 50, 50);       
        fieldNumProcesses.addKeyListener(new KeyAdapter() {
            @Override
            public void keyTyped(KeyEvent e) {
                char c = e.getKeyChar();
                String text = fieldNumProcesses.getText();

                // Allow only digits 1-6 and ensure only one character is allowed
                if ((c < '1' || c > '6') || text.length() >= 1) {
                    e.consume(); // Ignore the event
                } else {
                    SwingUtilities.invokeLater(() -> {
                        // simulate focus loss by focusing another component
                        buttonPlus.requestFocusInWindow(); // Change this to another component if needed
                    });
                }
            }
        });        
        
        panelStart.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                buttonPlus.requestFocusInWindow();
               
            }
        });
        
        fieldNumProcesses.addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                // Clear the text field when focused
                fieldNumProcesses.setText("");
            }

            @Override
            public void focusLost(FocusEvent e) {
                // Restore the previous value if the field is empty
                if (fieldNumProcesses.getText().isEmpty()) {
                    fieldNumProcesses.setText(currentNumInput);
                } else {
                    // Update the previous value if a new number is entered
                    currentNumInput = fieldNumProcesses.getText();
                }
            }
        });
        panelStart.add(fieldNumProcesses);
        
        
        // BUTTON: -
        buttonMinus = new JButton("") {
            private boolean isPressed = false;
            
            @Override
            protected void paintComponent(Graphics g) {
                // draw the minus icon
                super.paintComponent(g);
                g.setFont(new Font("Segoe UI", Font.BOLD, 14));
                FontMetrics fm = g.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth("—")) / 2;
                int textY = (getHeight() + fm.getAscent()) / 2 - 4; 
                
                // set the background color based on pressed state and boundary
                int num = Integer.parseInt(currentNumInput);
                if (isPressed && num > 1) {
                    g.setColor(new Color(245, 210, 210));
                } else {
                    g.setColor(getBackground());
                }
                g.fillRect(0, 0, getWidth(), getHeight()); 
                g.setColor(getForeground());
                g.drawString("—", textX, textY);
            }

            @Override
            public void addNotify() {
                super.addNotify();
                // add mouse listeners to manage the pressed state
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint(); 
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint();
                    }
                });
            }
        };
        buttonMinus.setForeground(new Color(150, 150, 150));
        buttonMinus.setBackground(new Color(240, 240, 240));
        buttonMinus.setFocusPainted(false);
        buttonMinus.setBorder(BorderFactory.createMatteBorder(1, 1, 1, 0, new Color(200, 200, 200)));
        buttonMinus.setBounds(375, 300, 50, 50);
        buttonMinus.addActionListener(this);
        panelStart.add(buttonMinus);

                
        // BUTTON: +
        buttonPlus = new JButton("") {
            private boolean isPressed = false;

            @Override
            protected void paintComponent(Graphics g) {
                // draw the plus icon
                super.paintComponent(g);
                g.setFont(new Font("Segoe UI", Font.PLAIN, 36));
                FontMetrics fm = g.getFontMetrics();
                int textX = (getWidth() - fm.stringWidth("+")) / 2 + 1;
                int textY = (getHeight() + fm.getAscent()) / 2 - 8;

                // set the background color based on pressed state and boundary
                int num = Integer.parseInt(currentNumInput);
                if (isPressed && num < 6) {
                    g.setColor(new Color(200, 245, 200)); 
                } else {
                    g.setColor(getBackground());
                }
                g.fillRect(0, 0, getWidth(), getHeight()); 
                g.setColor(getForeground());
                g.drawString("+", textX, textY);
            }

            @Override
            public void addNotify() {
                super.addNotify();
                // add mouse listeners to manage the pressed state
                addMouseListener(new MouseAdapter() {
                    @Override
                    public void mousePressed(MouseEvent e) {
                        isPressed = true;
                        repaint(); 
                    }

                    @Override
                    public void mouseReleased(MouseEvent e) {
                        isPressed = false;
                        repaint(); 
                    }
                });
            }
        };
        buttonPlus.setForeground(new Color(150, 150, 150));
        buttonPlus.setBackground(new Color(240, 240, 240));
        buttonPlus.setFont(new Font("Segoe UI", Font.PLAIN, 36));
        buttonPlus.setFocusPainted(false);
        buttonPlus.setBorder(BorderFactory.createMatteBorder(1, 0, 1, 1, new Color(200, 200, 200)));
        buttonPlus.setBounds(475, 300, 50, 50);
        buttonPlus.addActionListener(this);
        panelStart.add(buttonPlus);        
        
        
        // BUTTON: proceed
        buttonNumProceed = createModernButton("Proceed");
        buttonNumProceed.setForeground(textColor);
        buttonNumProceed.setBackground(new Color(210, 210, 210));
        buttonNumProceed.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        buttonNumProceed.setBounds(400, 360, 100, 40);
        buttonNumProceed.addActionListener(this);
        panelStart.add(buttonNumProceed);
    }
    
    
    
    
    // PANEL 2: INPUT FOR NUMBER OF PROCESSES AND THEIR ARRIVAL AND BURST TIME 
    public void initializeInputPanel() {
        // create input panel component
        panelInput = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // draw rectangle at the top
                super.paintComponent(g);
                g.setColor(primaryColor);
                g.fillRect(0, 0, getWidth(), 40);
                
                // minimize button
                g.setColor(new Color(199, 202, 117));
                g.fillRoundRect(15, 5, 30, 30, 20, 20);
                
                // close button
                g.setColor(new Color(202, 117, 117));
                g.fillRoundRect(getWidth()-45, 5, 30, 30, 20, 20);
                        
                // bottom border
                g.setColor(new Color(200, 200, 200));
                g.fillRect(0, 40, getWidth(), 2);
            }
        };
        
        // set panel properties
        panelInput.setLayout(null);
        panelInput.setBounds(0, 0, WIDTH, HEIGHT);
        panelInput.setBackground(backgroundColor); // Set background color
        
        createInputComponents();
    }
    
    
    public void createInputComponents() {
        // LABEL: title 
        labelTitle2 = new JLabel("Group 4 | HRRN Algorithm");
        labelTitle2.setForeground(textColor);
        labelTitle2.setFont(new Font("Segoe UI", Font.BOLD, 18));       
        labelTitle2.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitle2.setBounds(0, 0, 900, 40);
        panelInput.add(labelTitle2);

        // LABEL: input table
        labelInputTable = new JLabel("INPUT TABLE");
        labelInputTable.setForeground(new Color(90, 90, 90));
        labelInputTable.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelInputTable.setBounds(60, 60, 200, 40);
        panelInput.add(labelInputTable);
        
        // LABEL: number of processes
        labelNumProcesses = new JLabel("Number of Processes: ");
        labelNumProcesses.setForeground(new Color(85, 85, 85));
        labelNumProcesses.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        labelNumProcesses.setBounds(640, 70, 180, 30);
        panelInput.add(labelNumProcesses);
        
        // LABEL: display of input value after proceed
        labelNumProcessesInput = new JLabel("");
        labelNumProcessesInput.setForeground(new Color(85, 85, 85));
        labelNumProcessesInput.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        labelNumProcessesInput.setBounds(820, 70, 50, 30);
        panelInput.add(labelNumProcessesInput);

        // BUTTON: load example data
        buttonLoad = createModernButton("Load Example Data");
        buttonLoad.setBounds(55, 500, 280, 50);
        panelInput.add(buttonLoad);
        
        // BUTTON: clear
        buttonClear = createModernButton("Clear");
        buttonClear.setBounds(335, 500, 200, 50);
        panelInput.add(buttonClear);
        
        // BUTTON: run algorithm
        buttonRun = createModernButton("Run Algorithm");
        buttonRun.setBounds(535, 500, 300, 50);       
        panelInput.add(buttonRun);
        
        // LABEL: table input error message
        labelInputError = new JLabel("Please complete all table fields to proceed.");
        labelInputError.setForeground(new Color(150, 50, 50));
        labelInputError.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelInputError.setBounds(530, 545, 300, 30); // Adjust position for visibility
        labelInputError.setVisible(false);
        panelInput.add(labelInputError);

        // add action listeners
        buttonLoad.addActionListener(this);
        buttonClear.addActionListener(this);
        buttonRun.addActionListener(this);
    }
    
    
    public JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setForeground(textColor);
        button.setBackground(primaryColor);
        button.setFocusPainted(false);
        button.setBorderPainted(true);
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        return button;
    }
    
    
    public JLabel createModernLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        
        return label;
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
        inputModel = new DefaultTableModel(data, columnNames);

        // create the table component and render design
        inputTable = new JTable(inputModel) {
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
                    // highlight the text field using a lighter color
                    c.setBackground(getBackground()); 
                    c.setForeground(Color.BLACK); 
                    ((JComponent) c).setBorder(BorderFactory.createLineBorder(new Color(183, 224, 182), 2));
                }

                return c;
            }
        };

        // add mouse motion listener to handle hover and deselect behavior
        inputTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = inputTable.rowAtPoint(e.getPoint());

                // if we hover over a different row
                if (row != hoveredRow) {
                    // if a cell is being edited, stop editing to commit changes
                    if (inputTable.isEditing()) {
                        // deselect cell and save changes
                        inputTable.getCellEditor().stopCellEditing();
                    }
                    
                    // update the currently hovered row
                    hoveredRow = row; 
                    inputTable.clearSelection();
                    inputTable.repaint();
                }
            }
        });
        
        inputTable.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                // if from an error, hide the error message 
                labelInputError.setVisible(false);
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
        inputTable.setDefaultEditor(Object.class, cellEditor);

        // customize table appearance
        inputTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        inputTable.setRowHeight((int)(335 / data.length));
        inputTable.setShowGrid(false); // Remove grid lines for a clean look
        inputTable.setIntercellSpacing(new Dimension(0, 0));

        // set table background colors
        inputTable.setBackground(new Color(229, 245, 224));
        inputTable.setForeground(Color.DARK_GRAY);
        inputTable.setSelectionBackground(new Color(183, 224, 182)); // Selection background
        inputTable.setSelectionForeground(Color.BLACK);

        // center the cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setVerticalAlignment(JLabel.CENTER);
        for (int i = 0; i < inputTable.getColumnCount(); i++) {
            inputTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // customize the table header
        JTableHeader tableHeader = inputTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD, 16));
        tableHeader.setBackground(new Color(169, 209, 173)); // Header background
        tableHeader.setForeground(new Color(65, 65, 65)); // Header text color
        tableHeader.setPreferredSize(new Dimension(100, 50));
        tableHeader.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(183, 224, 182))); // Underline effect
        inputTable.getTableHeader().setResizingAllowed(false);
        inputTable.getTableHeader().setReorderingAllowed(false);

        // wrap the table in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(inputTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(2, 2, 2,2, primaryColor));//Color.LIGHT_GRAY));
        scrollPane.getViewport().setBackground(new Color(229, 245, 224)); // Match background with the table

        // set the preferred size and position for the scroll pane
        scrollPane.setPreferredSize(new Dimension(780, 390)); // Adjust size as needed
        scrollPane.setBounds(55, 105, scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height); // Position scroll pane

        // add the scroll pane directly to the panel
        panelInput.add(scrollPane); // Add scroll pane to the main panel
    }
    
        
    // INPUT TABLE METHODS
    public boolean validateTableInput() {
        // traverse through each rows and determine if have blank
        for (int row = 0; row < inputTable.getRowCount(); row++) {
            Object artInput = inputTable.getValueAt(row, 1);
            Object brtInput = inputTable.getValueAt(row, 2);
            if (artInput.equals("___") || brtInput.equals("___")) {
                return false;
            } 
        }                
        // if no ___ is found, then it is valid and not empty
        return true;
    }
    
    
    public void parseInputData() {
        // parse the raw input from the table to Process objects
        for (int row = 0; row < numberOfProcesses; row++) {
            // get data from each row input
            int artInput = Integer.parseInt(inputTable.getValueAt(row, 1).toString());
            int brtInput = Integer.parseInt(inputTable.getValueAt(row, 2).toString());
            
            // create the process object for this row
            Process process = new Process(row + 1, artInput, brtInput);
            
            // then add to array collections of processes
            processesInput.add(process);
            System.out.println("LOG: parseInputData() -> Process(" + (row+1) + ", " + artInput + ", " + brtInput + ")");
        }
    }
    
   
    
    
    
    
    // PANEL 2: OUTPUT OF GANTT CHART, CALCULATIONS AND PROCEDURES
    public void initializeOutputPanel() {
        // create output panel component
        panelOutput = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // draw rectangle at the top
                super.paintComponent(g);
                g.setColor(primaryColor);
                g.fillRect(0, 0, getWidth(), 40);
                
                // minimize button
                g.setColor(new Color(199, 202, 117));
                g.fillRoundRect(15, 5, 30, 30, 15, 15);
                
                // close button
                g.setColor(new Color(202, 117, 117));
                g.fillRoundRect(getWidth()-45, 5, 30, 30, 15, 15);
                        
                // bottom border
                g.setColor(new Color(200, 200, 200));
                g.fillRect(0, 40, getWidth(), 2);
            }
        };
        panelOutput.setLayout(null);
        panelOutput.setBounds(0, 0, WIDTH, HEIGHT);
        panelOutput.setBackground(backgroundColor);
        
        // LABEL: title bar
        labelTitle3 = new JLabel("Group 4 | HRRN Algorithm");
        labelTitle3.setForeground(textColor);
        labelTitle3.setFont(new Font("Segoe UI", Font.BOLD, 18));       
        labelTitle3.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitle3.setBounds(0, 0, 900, 40);
        panelOutput.add(labelTitle3);
        
        // LABEL: gantt chart
        labelGanttChart = new JLabel("HRRN - Gantt Chart");
        labelGanttChart.setForeground(new Color(90, 90, 90));
        labelGanttChart.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelGanttChart.setHorizontalAlignment(SwingConstants.LEFT);
        labelGanttChart.setBounds(70, 60, 900, 30); // Adjust position for visibility
        panelOutput.add(labelGanttChart);   
        
        // LABEL: calculations header
        labelCalculationsHeader = new JLabel("CALCULATIONS");
        labelCalculationsHeader.setForeground(new Color(90, 90, 90));
        labelCalculationsHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelCalculationsHeader.setHorizontalAlignment(SwingConstants.LEFT);
        labelCalculationsHeader.setBounds(70, 192, 300, 30); 
        panelOutput.add(labelCalculationsHeader);  
        
        // BUTTON: prev
        JButton buttonPrev = new JButton("prev");
        buttonPrev.setForeground(new Color(65, 65, 65));
        buttonPrev.setBackground(new Color(221, 223, 240));
        buttonPrev.setFocusPainted(false);
        buttonPrev.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 0, new Color(200, 200, 230)));
        buttonPrev.setBounds(70, 527, 70, 30);
        buttonPrev.addActionListener(this);
        panelOutput.add(buttonPrev);
        
        // BUTTON: next
        JButton buttonNext = new JButton("next");
        buttonNext.setForeground(new Color(65, 65, 65));
        buttonNext.setBackground(new Color(221, 223, 240));
        buttonNext.setFocusPainted(false);
        buttonNext.setBorder(BorderFactory.createMatteBorder(2, 1, 2, 2, new Color(200, 200, 230)));
        buttonNext.setBounds(140, 527, 70, 30);
        buttonNext.addActionListener(this);
        panelOutput.add(buttonNext);
        
    }   
    
    
    public void generateOutputs() {        
        // OUTPUT (1/2): calculations
        generateCalculations();
        
        // OUTPUT (2/2): response ratio
        generateResponseRatio();
        
        // OUTPUT: gantt chart
        generateGanttChart();           
    }
    
    
    public void generateGanttChart() {
        // draw the gantt chart based on the calculations from the input        
        System.out.println("LOG: generate gantt chart");
        int chartWidth = 780;
        int chartHeight = 100;
        int len = scheduledProcesses.size();
        int totalTime = scheduledProcesses.get(len - 1).endTime;
        int unit = (chartWidth - 15) / totalTime; // x multiplier for each rect
        
        // colors for each processes in gantt chart
        ganttChartColors = new Color[]{
            new Color(255, 182, 193), // Pastel Red
            new Color(173, 216, 230), // Pastel Blue
            new Color(240, 250, 153), // Pastel Yellow
            new Color(144, 238, 144), // Pastel Green
            new Color(255, 204, 153), // Pastel Orange
            new Color(221, 160, 221)  // Pastel Purple
        };

        // generate gantt chart graphics
        panelGanttChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int x = 10; // starting x position
                int y = 20; // y position for the gantt chart
                int colorIndex = 0;
                
                for (Process process : scheduledProcesses) {
                    // retrieve process information
                    int ID = process.id;
                    int sT = process.startTime;
                    int BT = process.burstTime;
                    
                    // determine the length of the process rect
                    int length = BT * unit;
                    
                    // draw the border - 3 pixels thick, dark gray
                    Graphics2D g2d = (Graphics2D) g;
                    g2d.setStroke(new BasicStroke(3)); 
                    g.setColor(new Color(100, 100, 100));     
                    g.drawRect(x, y, length, 50);

                    // fill each process rect with designated colors
                    if (process.id != -1) {
                        g.setColor(ganttChartColors[colorIndex % ganttChartColors.length]); 
                        processColors[process.id-1] = ganttChartColors[colorIndex];
                        colorIndex = (colorIndex + 1) % ganttChartColors.length;
                        System.out.println("LOG: processColors Updated");
                    } else {
                        g.setColor(new Color(200, 200, 200));
                    }
                    g.fillRect(x + 1, y + 1, length - 2, 49);

                    // write the process name inside each process rect
                    g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g.setColor(new Color(80, 80, 80)); 
                    if (process.id != -1) {
                        g.drawString("P" + ID, x + length / 2 - 5, y + 30);
                    } else {
                        g.setColor(new Color(120, 120, 120));
                        g.setFont(new Font("Segoe UI", Font.BOLD, 12));                        
                        g.drawString("IDLE", x + length / 2 - 10, y + 30);
                    }
                    // write the start time below
                    g.setColor(new Color(100, 100, 100));
                    g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g.drawString(String.valueOf(sT), x, y + 65);
                    
                    // move to the next position
                    x += length; 
                }
                
                // write the end time below at the end of the chart
                g.drawString(String.valueOf(totalTime), x - 10, y + 65); 
            }
        };
        
        // gantt chart panel properties
        panelGanttChart.setBackground(backgroundColor);
        panelGanttChart.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelGanttChart.setPreferredSize(new Dimension(780, 100));
        panelGanttChart.setBounds(60, 75, 780, 100);
        panelOutput.add(panelGanttChart);
    }
    
    
    public void generateCalculations() {
        System.out.println("LOG: generateOutputTable()");
        // PANEL: output table
        panelCalculations = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;

                g2d.setColor(new Color(150, 150, 150));
                g2d.setStroke(new BasicStroke(2)); 

                // waiting time
                g2d.drawLine(335, 25, 335, 15);
                g2d.drawLine(335, 15, 378, 15);
                g2d.drawLine(485, 15, 528, 15);
                g2d.drawLine(528, 15, 528, 25);
                
                // turn around time
                g2d.drawLine(549, 25, 549, 15);
                g2d.drawLine(549, 15, 577, 15);
                g2d.drawLine(718, 15, 746, 15);
                g2d.drawLine(746, 15, 746, 25);                
                
                // reset thickness next rendering
                g2d.setStroke(new BasicStroke(0)); 
            }
        };
        panelCalculations.setLayout(null);
        panelCalculations.setBackground(backgroundColor);
        panelCalculations.setBounds(70, 195, 755, 400);
        panelOutput.add(panelCalculations);
        
        // LABEL: calculations header
//        labelCalculationsHeader = new JLabel("CALCULATIONS");
//        labelCalculationsHeader.setForeground(new Color(90, 90, 90));
//        labelCalculationsHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
//        labelCalculationsHeader.setHorizontalAlignment(SwingConstants.LEFT);
//        labelCalculationsHeader.setBounds(0, 0, 300, 30); 
//        panelCalculations.add(labelCalculationsHeader);          

        // LABEL: waiting time header
        labelWaitingTimeHeader = new JLabel("Waiting Time");
        labelWaitingTimeHeader.setForeground(new Color(100, 100, 100));
        labelWaitingTimeHeader.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        labelWaitingTimeHeader.setHorizontalAlignment(SwingConstants.CENTER);
        labelWaitingTimeHeader.setBounds(323, 0, 218, 30);
        panelCalculations.add(labelWaitingTimeHeader); 

        // LABEL: turn around time header
        labelTurnAroundTimeHeader = new JLabel("Turn Around Time");
        labelTurnAroundTimeHeader.setForeground(new Color(100, 100, 100));
        labelTurnAroundTimeHeader.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        labelTurnAroundTimeHeader.setHorizontalAlignment(SwingConstants.CENTER);
        labelTurnAroundTimeHeader.setBounds(539, 0, 218, 30);
        panelCalculations.add(labelTurnAroundTimeHeader);  
        
        // COMPUTE: average waiting time and turn around time equation and results
        calculateAverageWtAndTat();
        
        // LABEL: average waiting time equation
        labelAverageWtEquation = new JLabel("<html>(" + aveWtEquation + ")/" + numberOfProcesses + "</html>");
        labelAverageWtEquation.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelAverageWtEquation.setHorizontalAlignment(SwingConstants.CENTER);
        labelAverageWtEquation.setVerticalAlignment(SwingConstants.BOTTOM);
        labelAverageWtEquation.setForeground(new Color(80, 80, 80));
        labelAverageWtEquation.setBackground(new Color(230, 230, 255));
        labelAverageWtEquation.setOpaque(true);
        labelAverageWtEquation.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 2, new Color(200, 200, 200)));        
        labelAverageWtEquation.setBounds(345, 320, 180, 25); 
        panelCalculations.add(labelAverageWtEquation);
        
        // LABEL: average waiting time result
        labelAverageWaitingTime = new JLabel("<html><b>Ave WT</b> = "  + formattedAveWt + "</div></html>");
        labelAverageWaitingTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelAverageWaitingTime.setHorizontalAlignment(SwingConstants.CENTER);
        labelAverageWaitingTime.setVerticalAlignment(SwingConstants.TOP);
        labelAverageWaitingTime.setForeground(new Color(80, 80, 80));
        labelAverageWaitingTime.setBackground(new Color(230, 230, 255));
        labelAverageWaitingTime.setOpaque(true);
        labelAverageWaitingTime.setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, new Color(200, 200, 200)));        
        labelAverageWaitingTime.setBounds(345, 345, 180, 30); 
        panelCalculations.add(labelAverageWaitingTime);
        
        // LABEL: average turn around time equation
        labelAverageTatEquation = new JLabel("<html>(" + aveTatEquation + ")/" + numberOfProcesses + "</html>");
        labelAverageTatEquation.setFont(new Font("Segoe UI", Font.PLAIN, 12));
        labelAverageTatEquation.setHorizontalAlignment(SwingConstants.CENTER);
        labelAverageTatEquation.setVerticalAlignment(SwingConstants.BOTTOM);
        labelAverageTatEquation.setForeground(new Color(80, 80, 80));
        labelAverageTatEquation.setBackground(new Color(230, 230, 255));
        labelAverageTatEquation.setOpaque(true);
        labelAverageTatEquation.setBorder(BorderFactory.createMatteBorder(2, 2, 0, 2, new Color(200, 200, 200)));        
        labelAverageTatEquation.setBounds(555, 320, 180, 25);
        panelCalculations.add(labelAverageTatEquation);
        
        // LABEL: average turn around time result
        labelAverageTurnAroundTime = new JLabel("<html><b>Ave TAT</b> = "  + formattedAveTat + "</div></html>");
        labelAverageTurnAroundTime.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelAverageTurnAroundTime.setHorizontalAlignment(SwingConstants.CENTER);
        labelAverageTurnAroundTime.setVerticalAlignment(SwingConstants.TOP);
        labelAverageTurnAroundTime.setForeground(new Color(80, 80, 80));
        labelAverageTurnAroundTime.setBackground(new Color(230, 230, 255));
        labelAverageTurnAroundTime.setOpaque(true);
        labelAverageTurnAroundTime.setBorder(BorderFactory.createMatteBorder(0, 2, 2, 2, new Color(200, 200, 200)));
        labelAverageTurnAroundTime.setBounds(555, 345, 180, 30);
        panelCalculations.add(labelAverageTurnAroundTime);
        
        // TABLE: output data and equations
        createOutputTable();
    }
    
    
    public void calculateAverageWtAndTat() {
        // compute equations and results for average waiting and turn around time        
        aveWtEquation = new StringBuilder();
        aveTatEquation = new StringBuilder();
        int totalWaitingTime = 0;
        int totalTurnAroundTime = 0;

        // build equations and calculate totals for waiting time and turn around time
        for (Process process : processesInput) {
            if (aveWtEquation.length() > 0) aveWtEquation.append("+");
            if (aveTatEquation.length() > 0) aveTatEquation.append("+");

            aveWtEquation.append(process.waitingTime);
            aveTatEquation.append(process.turnAroundTime);

            totalWaitingTime += process.waitingTime;
            totalTurnAroundTime += process.turnAroundTime;
        }
        
        // calculate and format averages
        formattedAveWt = String.format("%.2f", (double) totalWaitingTime / numberOfProcesses);
        formattedAveTat = String.format("%.2f", (double) totalTurnAroundTime / numberOfProcesses);
    }
    
    
    public void createOutputTable() {
        // columns and data for the process input table
        System.out.println("LOG: createOutputTable()");
        String[] columnNames = {"Process ID", "Arrival Time", "Burst Time", 
            "sT - AT", "WT", "eT - AT", "TAT"};        
        Object[][] data = new Object[numberOfProcesses][7];
        
        for (int i = 1; i <= numberOfProcesses; i++) {
            // retrieve processes data
            int AT = processesInput.get(i-1).arrivalTime;
            int BT = processesInput.get(i-1).burstTime;
            int sT = processesInput.get(i-1).startTime;
            int eT = processesInput.get(i-1).endTime;
            int WT = processesInput.get(i-1).waitingTime;
            int TAT = processesInput.get(i-1).turnAroundTime;
            
            // input data section
            data[i-1][0] = "P" + i;
            data[i-1][1] = AT;
            data[i-1][2] = BT;
            
            // waiting time section
            data[i-1][3] = sT + " - " + AT;
            data[i-1][4] = WT;
            
            // turn around time section
            data[i-1][5] = eT + " - " + AT;
            data[i-1][6] = TAT;
        }
        
        for (Color color : processColors) {
            System.out.print(color);
        }
        // create the table model
        outputModel = new DefaultTableModel(data, columnNames);

        // create the table component and render design
        outputTable = new JTable(outputModel) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }

            @Override
            public Component prepareRenderer(javax.swing.table.TableCellRenderer renderer, int row, int column) {
                Component c = super.prepareRenderer(renderer, row, column);
                
                // apply the hover effect
                if (row == hoveredRow && (column == 4 || column == 6)) {
                    c.setBackground(processColors[row].brighter());
                    c.setForeground(Color.DARK_GRAY);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    ((JLabel) c).setHorizontalAlignment(JLabel.LEFT);
                    int d = ((JLabel) c).getText().length() - 1; 
                    ((JLabel) c).setText("=" + " ".repeat(9-d) + ((JLabel) c).getText());
                    ((JComponent) c).setBorder(BorderFactory.createMatteBorder(5, 0, 5, 5, processColors[row]));
                } else if (row == hoveredRow && (column == 3 || column == 5)) {
                    c.setBackground(processColors[row].brighter());
                    c.setForeground(Color.DARK_GRAY);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER); 
                    ((JComponent) c).setBorder(BorderFactory.createMatteBorder(5, 5, 5, 0, processColors[row]));
                } else if (row == hoveredRow && (column == 0)) {
                    c.setBackground(processColors[row]);
                    c.setForeground(Color.DARK_GRAY);
                    c.setFont(new Font("Segoe UI", Font.BOLD, 14));
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                    ((JComponent) c).setBorder(null);
                } else if (row == hoveredRow) {
                    c.setBackground(processColors[row]);
                    c.setForeground(Color.DARK_GRAY);
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                    ((JComponent) c).setBorder(null);
               } else {                    
                    c.setBackground(getBackground());
                    c.setForeground(Color.DARK_GRAY);
                    c.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                    ((JLabel) c).setHorizontalAlignment(JLabel.CENTER);
                    ((JComponent) c).setBorder(null);
                }
                return c;
            }
        };

        outputTable.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = outputTable.rowAtPoint(e.getPoint());

                // update the currently hovered row
                if (row != hoveredRow) {                    
                    
                    hoveredRow = row;         
                    outputTable.clearSelection(); 
                    outputTable.repaint();
                }
            }
            public void mouseClicked(MouseEvent e) {
                outputTable.clearSelection();
            }
        });

        // customize table appearance
        outputTable.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        outputTable.setRowHeight((int)(275 / (data.length + 1)));
        outputTable.setShowGrid(false);
        outputTable.setIntercellSpacing(new Dimension(0, 0));

        // set table background colors
        outputTable.setBackground(new Color(229, 245, 224));
        outputTable.setForeground(Color.DARK_GRAY);
        outputTable.setSelectionBackground(new Color(183, 224, 182));
        outputTable.setSelectionForeground(Color.BLACK);
        
        // center the cell content
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        centerRenderer.setVerticalAlignment(JLabel.CENTER);
        for (int i = 0; i < outputTable.getColumnCount(); i++) {
            outputTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
        }

        // customize the table header
        JTableHeader tableHeader = outputTable.getTableHeader();
        tableHeader.setFont(new Font("Segoe UI", Font.BOLD  , 16));
        tableHeader.setBackground(new Color(169, 183, 209)); // Header background
        tableHeader.setForeground(new Color(65, 65, 65)); // Header text color
        tableHeader.setPreferredSize(new Dimension(100, (int)(275 / (data.length + 1))));
        tableHeader.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(183, 192, 224)));
        outputTable.getTableHeader().setResizingAllowed(false);
        outputTable.getTableHeader().setReorderingAllowed(false);

        // wrap the table in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(outputTable);
        scrollPane.setBorder(BorderFactory.createMatteBorder(2, 2, 2,2, new Color(200, 200, 250)));
        scrollPane.getViewport().setBackground(new Color(229, 224, 245)); // Match background with the table
        outputTable.setFillsViewportHeight(true);

        // set the preferred size and position for the scroll pane
        scrollPane.setPreferredSize(new Dimension(755, 280)); // Adjust size as needed
        scrollPane.setBounds(0, 30, scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height); // Position scroll pane

        // add the scroll pane directly to the panel
        panelCalculations.add(scrollPane); // Add scroll pane to the main panel
        System.out.println("LOG: createOutputTable() - end");
    }
    
    
    
    public void generateResponseRatio() {
        
    }
    
    
    
    public void initializeCalculationsPanel() {
        
    }
    
    
    
    
        
    public void initializeProceduresPanel() {
        // PANEL: procedures (for response ratio computations)
        panelProcedures = new JPanel();
        panelProcedures.setLayout(null);
        panelProcedures.setBackground(new Color(240, 230, 240));
        panelProcedures.setBounds(70, 230, 755, 320);
        panelOutput.add(panelProcedures);
        
        // LABEL: response ratio header
        labelResponseRatioHeader = new JLabel("RESPONSE RATIO");
        labelResponseRatioHeader.setForeground(new Color(90, 90, 90));
        labelResponseRatioHeader.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelResponseRatioHeader.setHorizontalAlignment(SwingConstants.LEFT);
        labelResponseRatioHeader.setBounds(70, 200, 900, 30); // Adjust position for visibility
        panelOutput.add(labelResponseRatioHeader);   
        
        // LABEL: response ratio formula
        labelResponseRatioFormula = new JLabel("RR = [xWT+BT] / BT ");
        labelResponseRatioFormula.setForeground(new Color(85, 85, 85));
        labelResponseRatioFormula.setFont(new Font("Segoe UI", Font.PLAIN, 14));
        labelResponseRatioFormula.setBounds(700, 200, 200, 30);
        panelOutput.add(labelResponseRatioFormula);
        
        
        JLabel labelRR1 = new JLabel("<html>RR1 = [(9-4) + 2] / 4<br>RR1 = <b>2.25<b></html>");
        labelRR1.setForeground(new Color(20, 20, 20));
        labelRR1.setFont(new Font("Segeo UI", Font.PLAIN, 12));
        labelRR1.setBackground(new Color(180, 180, 220));
        labelRR1.setBounds(10, 20, 120, 30);
        panelProcedures.add(labelRR1);
        
        JLabel labelRR2 = new JLabel("<html>RR2 = [(13-3) + 4] / 3<br>RR1 = <b>2.25<b></html>");
        labelRR2.setForeground(new Color(20, 20, 20));
        labelRR2.setFont(new Font("Segeo UI", Font.PLAIN, 12));
        labelRR2.setBackground(new Color(180, 180, 220));
        labelRR2.setBounds(10, 80, 120, 30);
        panelProcedures.add(labelRR2);
        
        JLabel labelRR3 = new JLabel("<html>RR3 = [(16-4) + 5] / 4<br>RR1 = <b>2.25<b></html>");
        labelRR3.setForeground(new Color(20, 20, 20));
        labelRR3.setFont(new Font("Segeo UI", Font.PLAIN, 12));
        labelRR3.setBackground(new Color(180, 180, 220));
        labelRR3.setBounds(10, 140, 120, 30);
        panelProcedures.add(labelRR3);
        
        JLabel labelRR4 = new JLabel("<html>RR1 = [(9-4) + 2] / 4<br>RR1 = <b>2.25<b></html>");
        labelRR4.setForeground(new Color(20, 20, 20));
        labelRR4.setFont(new Font("Segeo UI", Font.PLAIN, 12));
        labelRR4.setBackground(new Color(180, 180, 220));
        labelRR4.setBounds(10, 200, 120, 30);
        panelProcedures.add(labelRR4);
        
        JLabel labelRR5 = new JLabel("<html>RR1 = [(9-4) + 2] / 4<br>RR1 = <b>2.25<b></html>");
        labelRR5.setForeground(new Color(20, 20, 20));
        labelRR5.setFont(new Font("Segeo UI", Font.PLAIN, 12));
        labelRR5.setBackground(new Color(180, 180, 220));
        labelRR5.setBounds(10, 260, 120, 30);
        panelProcedures.add(labelRR5);
        

        labelResponseRatioHeader.setVisible(false);
        labelResponseRatioFormula.setVisible(false);
        panelProcedures.setVisible(false);
    }
      
    

    
    // MAIN COMPUTATION METHODS FOR HTTN SCHEDULING ALGORITHM
    public ArrayList<Process> sortByArrivalTime(ArrayList<Process> processes) {
        // initialize local variables
        int minIndex;
        int arrivalTimeI, arrivalTimeJ, burstTimeI, burstTimeJ;
        
        // sort first by arrival time using selection sort
        for (int i = 0; i < processes.size() - 1; i++) {
            minIndex = i;
            for (int j = i + 1; j < processes.size(); j++) {
                // retrieve arrival and burst time for both processes
                arrivalTimeI = processes.get(i).arrivalTime;
                arrivalTimeJ = processes.get(j).arrivalTime;
                burstTimeI = processes.get(i).arrivalTime;
                burstTimeJ = processes.get(j).arrivalTime;
                    
                // change min index if lower is found
                if (arrivalTimeI > arrivalTimeJ) {
                    minIndex = j;
                } 
                
                // if same arrival times, base it on burst time
                if (arrivalTimeI == arrivalTimeJ) {
                    if (burstTimeI > burstTimeJ) {
                        minIndex = j;
                    }
                }
            }
            // swap if necessary
            Process temp = processes.get(i  );
            processes.set(i, processes.get(minIndex));
            processes.set(minIndex, temp);
        }
        
        return processes;
    }
    
    
    public void runSchedulingAlgorithm(ArrayList<Process> processes) {
        // STEP 1: sort each processes by arrival time
        processes = sortByArrivalTime(processes);
        
        int currentTime = 0;
        while (!processes.isEmpty()) {
            // STEP 2: store processes that have arrived during the current time
            ArrayList<Process> arrivedProcesses = new ArrayList<>();
            for (Process process : processes) {
                if (process.arrivalTime <= currentTime) {
                    arrivedProcesses.add(process);
                }
            }            
            
            // if no pending processes, move on to next second, initialize idle object
            if (arrivedProcesses.isEmpty()) {
                
                // check first if real process already exist in array
                if (scheduledProcesses.isEmpty()) { 
                    // if there is no any real processes
                    if (currentTime > 0) {          
                        // if an existing idle already exist
                        Process lastIdle = scheduledProcesses.get(scheduledProcesses.size()-1);
                        lastIdle.startTime = currentTime;
                        lastIdle.burstTime += 1;
                    } else {                        
                        // if no exising idle and real process
                        Process idle = new Process(-1, 0, 1);
                        scheduledProcesses.add(idle);
                    }
                } else { 
                    // if there are existing real processes
                    Process lastProcess = scheduledProcesses.get(scheduledProcesses.size()-1);
                    if (lastProcess.id != -1)  {
                        // if last process element is a real process
                        Process idle = new Process(-1, currentTime, 1);
                        idle.startTime = currentTime;
                        scheduledProcesses.add(idle);
                    } else {
                        // if last process element is a real process
                        lastProcess.burstTime += 1;
                    }
                }
                currentTime++;
                continue;
            }
            
            // STEP 3: compute and look for the highest response ratio among each pending processes
            Process selectedProcess = arrivedProcesses.get(0);
            for (Process process : arrivedProcesses) {
                process.waitingTime = currentTime - process.arrivalTime;
                process.responseRatio = (double) (process.waitingTime + process.burstTime) / process.burstTime;
                
                if (process.responseRatio > selectedProcess.responseRatio) {
                    selectedProcess = process;
                }
            }       
            
            // STEP 4: update the current time and process attributes
            selectedProcess.startTime = currentTime;
            currentTime += selectedProcess.burstTime; // execute
            selectedProcess.endTime = currentTime;
            selectedProcess.turnAroundTime = selectedProcess.endTime - selectedProcess.arrivalTime;
            selectedProcess.responseRatio = (double) Math.round(selectedProcess.responseRatio * 100.0) / 100.0;
            
            // STEP 5: add the process object to processes schedule
            scheduledProcesses.add(selectedProcess);
            processes.remove(selectedProcess);
            
            // add essential information to procedural data for visualization
            Process[][] executionInfo = new Process[][]{arrivedProcesses.toArray(new Process[0]), {selectedProcess}};
            proceduralData.put(currentTime, executionInfo);
            
            // LOGS
            System.out.print("LOG: [" + currentTime + "] {");
            for (Process process : arrivedProcesses) {
                
                System.out.print("("+process.id + ": " + process.responseRatio + "), ");
                System.out.print(" ");
            }
            System.out.println("}");
        }
        
        // LOGS
        displayProcessesContents();
        System.out.println(processesInput.size());
        System.out.println("\n\t==========\n");
        displayScheduledProcessesContents();
    }
    
  
               
  
    
    // ACTION LISTENER METHOD FOR BUTTONS
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        Object source = e.getSource();
        
        if (source == buttonMinus) {
            int num = Integer.parseInt(fieldNumProcesses.getText());
            if (num > 1) {
                fieldNumProcesses.setText(String.valueOf(num - 1));
                currentNumInput = String.valueOf(num - 1);
            }            
        }
        
        if (source == buttonPlus) {
            int num = Integer.parseInt(fieldNumProcesses.getText());
            if (num < 6) {
                fieldNumProcesses.setText(String.valueOf(num + 1));
                currentNumInput = String.valueOf(num + 1);
           }
        }
        
        switch (action) {
            case "Proceed" -> {
                System.out.println("LOG: Proceed Button");
                proceedButtonPressed();
            }
            
            case "Load Example Data" -> {
                System.out.println("LOG: Load Example Data Button");
                loadButtonPressed();
            }
            
            case "Clear" -> {
                System.out.println("LOG: Clear Button");
                clearButtonPressed();
            }
            
            case "Run Algorithm" -> {
                System.out.println("LOG: Run Algorithm Button");
                runAlgorithmPressed();
            }
        }
    }
    
    
    
    public void proceedButtonPressed() {
        // retrieve data from the text field
        numberOfProcesses = Integer.parseInt(fieldNumProcesses.getText());
        labelNumProcessesInput.setText(String.valueOf(numberOfProcesses));
        
        // create the table according to number of processes
        createInputTable();

        // switch to input panel
        panelStart.setVisible(false);
        panelInput.setVisible(true);
        
    }
    
    
    
    public void loadButtonPressed() {
        // initialize random object
        Random random = new Random();

        // 2nd column - generate random unique arrival times 
        ArrayList<Integer> arrivalTimes = new ArrayList<>();
        for (int i = 0; i < numberOfProcesses; i++) {
            arrivalTimes.add(i);
        } 
        Collections.shuffle(arrivalTimes);

        // 3rd column - generate random burst times and update table
        for (int row = 0; row < numberOfProcesses; row++) {
            int randomArt = arrivalTimes.get(row);
            int randomBrt = random.nextInt(1, 10);
            inputTable.setValueAt(randomArt, row, 1);
            inputTable.setValueAt(randomBrt, row, 2);
        }

        // hide message if from an error
        labelInputError.setVisible(false);
        inputTable.repaint(); 
    }
    
    
    
    public void clearButtonPressed() {
        // set all cells to blank
        for (int row = 0; row < inputTable.getRowCount(); row++) {
            inputTable.setValueAt("___", row, 1);
            inputTable.setValueAt("___", row, 2);
        }
        
        // hide message if from an error
        labelInputError.setVisible(false);
        inputTable.repaint(); 
    }
    
    
    
    public void runAlgorithmPressed() {
        // clear selection first to save user input
        if (inputTable.getCellEditor() != null) {
            inputTable.getCellEditor().stopCellEditing();
        }
        inputTable.clearSelection();
        inputTable.repaint();

        // validate if input are valid and not empty, then get the input values
        if (validateTableInput()) {
            parseInputData();
        } else {
            labelInputError.setVisible(true);
            return;
        }

        // execute computation for scheduling processes
        runSchedulingAlgorithm(new ArrayList<>(processesInput));

        // show output panel
        panelInput.setVisible(false);
        panelOutput.setVisible(true);

        // then show output: calculations, procedures and gantt chart
        generateOutputs();        
    }
    
    
    
    // LOGGING METHODS
    public void displayProcessesContents() {
        // display on console for logging and debugging purposes
        for (Process process : scheduledProcesses) {
            System.out.println("Process: " + process.id);
            System.out.println("\tArrival Time: " + process.arrivalTime);
            System.out.println("\tBurst Time: " + process.burstTime + "\n");
        }
    }
    
    public void displayScheduledProcessesContents() {
        // display on console for logging and debugging purposes
        for (Process process : scheduledProcesses) {
            if (process.id != -1) {
                System.out.println("Process: " + process.id);
                System.out.println("\tArrival Time: " + process.arrivalTime);
                System.out.println("\tBurst Time: " + process.burstTime);
                System.out.println("\tStart T   ime: " + process.startTime);
                System.out.println("\tEnd Time: " + process.endTime);
                System.out.println("\tWaiting Time: " + process.waitingTime);
                System.out.println("\tTurn Around Time: " + process.turnAroundTime);
                System.out.println("\tResponse Ratio: " + process.responseRatio + "\n");
            }
        }
    }
        
    
    
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                // run program
                HRRN_Algorithm systemGUI = new HRRN_Algorithm();                
            }
        });
    }
}
