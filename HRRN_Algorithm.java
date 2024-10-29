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
    private JTable table;
    private DefaultTableModel model;
        
    // declare output gantt chart components
    private JLabel labelTitle3;
    private JLabel labelGanttChart;
    private JLabel labelCalculations;
    private JLabel labelAverageWaitingTime;
               
    // declare screen, cursor and other properties
    private int WIDTH = 900;
    private int HEIGHT = 600;
    private int hoveredRow = -1;

    // declare color objects (light mode)
    private final Color primaryColor = new Color(220, 220, 220);
    private final Color textColor = new Color(50, 50, 50);    
    private final Color backgroundColor = new Color(244, 245, 235);     
    
    // declare color objects (dark mode)
    // private final Color primaryColor = new Color(34, 40, 44);
    // private final Color textColor = new Color(235, 235, 235);
    // private final Color backgroundColor = new Color(44, 51, 60);    
    
    // important values and arrays for scheduling calculations 
    private int numberOfProcesses = 1;
    private String currentNumInput = "1";
    private ArrayList<Process> processesInput = new ArrayList<>();
    private ArrayList<Process> scheduledProcesses = new ArrayList<>();
    private HashMap<String, Integer> chartData = new HashMap<>();
    private HashMap<Integer, Process[][]> proceduralData = new HashMap<>();
    
    
    
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
        fieldNumProcesses = new JTextField("3");
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
                g.drawString("—", textX, textY);
                
                // set the background color based on pressed state and boundary
                int num = Integer.parseInt(currentNumInput);
                if (isPressed && num > 1) {
                    g.setColor(new Color(240, 200, 200));
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
        buttonMinus.setForeground(textColor);
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
                    g.setColor(new Color(190, 255, 190)); 
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
        buttonPlus.setForeground(textColor);
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
                    // highlight the text field using a lighter color
                    c.setBackground(getBackground()); 
                    c.setForeground(Color.BLACK); 
                    ((JComponent) c).setBorder(BorderFactory.createLineBorder(new Color(183, 224, 182), 2));
                }

                return c;
            }
        };

        // add mouse motion listener to handle hover and deselect behavior
        table.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                int row = table.rowAtPoint(e.getPoint());

                // if we hover over a different row
                if (row != hoveredRow) {
                    // if a cell is being edited, stop editing to commit changes
                    if (table.isEditing()) {
                        // deselect cell and save changes
                        table.getCellEditor().stopCellEditing();
                    }
                    
                    // update the currently hovered row
                    hoveredRow = row; 
                    table.clearSelection();
                    table.repaint();
                }
            }
        });
        
        table.addMouseListener(new MouseAdapter() {
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
        tableHeader.setBackground(new Color(169, 209, 173)); // Header background
        tableHeader.setForeground(new Color(65, 65, 65)); // Header text color
        tableHeader.setPreferredSize(new Dimension(100, 50));
        tableHeader.setBorder(BorderFactory.createMatteBorder(2, 2, 2, 2, new Color(183, 224, 182))); // Underline effect

        // wrap the table in a JScrollPane
        JScrollPane scrollPane = new JScrollPane(table);
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
        for (int row = 0; row < table.getRowCount(); row++) {
            Object artInput = table.getValueAt(row, 1);
            Object brtInput = table.getValueAt(row, 2);
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
            int artInput = Integer.parseInt(table.getValueAt(row, 1).toString());
            int brtInput = Integer.parseInt(table.getValueAt(row, 2).toString());
            
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
        
        createOutputComponents();
    }   
    
     
    public void createOutputComponents() {
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
        labelGanttChart.setBounds(70, 70, 900, 30); // Adjust position for visibility
        panelOutput.add(labelGanttChart);        
        
        // LABEL: calculations
        labelCalculations = new JLabel("CALCULATIONS");
        labelCalculations.setForeground(new Color(90, 90, 90));
        labelCalculations.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelCalculations.setHorizontalAlignment(SwingConstants.LEFT);
        labelCalculations.setBounds(70, 200, 900, 30); // Adjust position for visibility
        panelOutput.add(labelCalculations);        
        
        // PANEL: calculations (temporary)
        JPanel panelCalculations = new JPanel();
        panelCalculations.setBackground(new Color(200, 200, 200));
        panelCalculations.setBounds(70, 230, 755, 320);
        panelOutput.add(panelCalculations);
    }
    
    
    public void generateGanttChart() {
        // draw the gantt chart based on the calculations from the input        
        int chartWidth = 780;
        int chartHeight = 100;
        int totalTime = scheduledProcesses.get(numberOfProcesses - 1).endTime;
        int unit = (chartWidth - 15) / totalTime; // x multiplier for each rect
        
        // colors for each processes in gantt chart
        Color[] ganttChartColors = {
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
                
                for (int i = 0; i < numberOfProcesses; i++) {
                    // retrieve process information
                    Process process = scheduledProcesses.get(i);
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
                    g.setColor(ganttChartColors[i]); 
                    g.fillRect(x + 1, y + 1, length - 2, 49);

                    // write the process name inside each process rect
                    g.setFont(new Font("Segoe UI", Font.BOLD, 12));
                    g.setColor(new Color(80, 80, 80)); 
                    g.drawString("P" + ID, x + length / 2 - 5, y + 30);
                    
                    // write the start time below
                    g.setFont(new Font("Segoe UI", Font.PLAIN, 12));
                    g.setColor(new Color(100, 100, 100));
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
        panelGanttChart.setBounds(60, 85, 780, 100);
        panelOutput.add(panelGanttChart);
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
            
            // if no pending processes, move on to next second 
            if (arrivedProcesses.isEmpty()) {
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
            table.setValueAt(randomArt, row, 1);
            table.setValueAt(randomBrt, row, 2);
        }

        // hide message if from an error
        labelInputError.setVisible(false);
        table.repaint(); 
    }
    
    
    
    public void clearButtonPressed() {
        // set all cells to blank
        for (int row = 0; row < table.getRowCount(); row++) {
            table.setValueAt("___", row, 1);
            table.setValueAt("___", row, 2);
        }
        
        // hide message if from an error
        labelInputError.setVisible(false);
        table.repaint(); 
    }
    
    
    
    public void runAlgorithmPressed() {
        // clear selection first to save user input
        if (table.getCellEditor() != null) {
            table.getCellEditor().stopCellEditing();
        }
        table.clearSelection();
        table.repaint();

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

        // then show gantt chart
        generateGanttChart();
    }
    
    
    
    // LOGGING METHODS
    public void displayProcessesContents() {
        // display on console for logging and debugging purposes
        for (Process process : processesInput) {
            System.out.println("Process: " + process.id);
            System.out.println("\tArrival Time: " + process.arrivalTime);
            System.out.println("\tBurst Time: " + process.burstTime + "\n");
        }
    }
    
    public void displayScheduledProcessesContents() {
        // display on console for logging and debugging purposes
        for (Process process : scheduledProcesses) {
            System.out.println("Process: " + process.id);
            System.out.println("\tArrival Time: " + process.arrivalTime);
            System.out.println("\tBurst Time: " + process.burstTime);
            System.out.println("\tStart Time: " + process.startTime);
            System.out.println("\tEnd Time: " + process.endTime);
            System.out.println("\tWaiting Time: " + process.waitingTime);
            System.out.println("\tTurn Around Time: " + process.turnAroundTime);
            System.out.println("\tResponse Ratio: " + process.responseRatio + "\n");
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
