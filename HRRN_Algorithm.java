
package hrrn_github;

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
import java.awt.image.BufferedImage;
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
    
    // input panel components
    private JLabel labelTitle;
    private JLabel labelDescription;
    private JLabel labelNumProcesses;
    private JTextField fieldNumProcesses;
    private JLabel labelNumProcessesInput;
    private JButton buttonNumProceed;
    
    private JButton buttonLoad;
    private JButton buttonClear;
    private JButton buttonRun;
    private JLabel labelInputError;

    private JTable table;
    private DefaultTableModel model;
        
    // output gantt chart components
    private JLabel labelTitle2;
    private JLabel labelGanttChart;
    private JLabel labelCalculations;
    private JLabel labelAverageWaitingTime;
            
    
    
    
    // screen and other properties
    private int WIDTH = 900;
    private int HEIGHT = 600;
    private int hoveredRow = -1; // Variable to store the hovered row

    // LIGHT MODE
    private final Color primaryColor = new Color(235, 235, 235);    
    private final Color backgroundColor = new Color(209, 222, 222);    
    private final Color textColor = new Color(10, 10, 10);    
    
    // DARK MODE
//    private final Color primaryColor = new Color(34, 40, 44);
//    private final Color textColor = new Color(235, 235, 235);
//    private final Color backgroundColor = new Color(44, 51, 60);    
    
  
    // important values for scheduling calculations
    private int numberOfProcesses = 1;
    private ArrayList<Process> processesInput = new ArrayList<>();
    private ArrayList<Process> scheduledProcesses = new ArrayList<>();
    private HashMap<String, Integer> chartData = new HashMap<>();
    private HashMap<Integer, Process[][]> proceduralData = new HashMap<>();
    
    
    
    
    
    // CONSTRUCTOR: program starts here
    public HRRN_Algorithm() {
        // initialize frame and panel to create each components
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
        frame.setShape(new RoundRectangle2D.Double(0, 0, WIDTH, HEIGHT, 50, 50));
        frame.setVisible(true);
        frame.setLayout(null);
        frame.setResizable(false);
        frame.setBackground(backgroundColor);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        // add panels to frame
        frame.add(panelInput);
        frame.add(panelOutput);
        
        // show first panel
        panelInput.setVisible(true);
    }
        
    
    
    // PANEL 1: INPUT FOR NUMBER OF PROCESSES AND THEIR ARRIVAL AND BURST TIME 
    public void initializeInputPanel() {
        // create input panel component
        panelInput = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                // draw rectangle at the top
                super.paintComponent(g);
                g.setColor(primaryColor);
                g.fillRect(0, 0, getWidth(), 50);
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
        labelTitle = new JLabel("Highest Response Ratio Next (HRRN) Algorithm");
        labelTitle.setForeground(textColor);
        labelTitle.setFont(new Font("Segoe UI", Font.BOLD, 24));       
        labelTitle.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitle.setBounds(50, 10, 800, 30);
        panelInput.add(labelTitle);
        
        // LABEL: description 
        labelDescription = new JLabel("<html>This tool allows you to schedule processes using the HRRN algorithm.<br>Enter the number of processes and their arrival and burst times.</html>");
        labelDescription.setForeground(textColor);
        labelDescription.setFont(new Font("Segoe UI", Font.PLAIN, 16));
        labelDescription.setBounds(50, 50, 800, 60);
        panelInput.add(labelDescription);

        // LABEL: enter number of processes
        labelNumProcesses = new JLabel("Enter Number of Processes (1-6): ");
        labelNumProcesses.setForeground(textColor);
        labelNumProcesses.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        labelNumProcesses.setBounds(50, 120, 300, 30);
        panelInput.add(labelNumProcesses);        
        
        // FIELD: gets the number of processes input (1-6)
        fieldNumProcesses = new JTextField();
        fieldNumProcesses.setForeground(textColor);
        fieldNumProcesses.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        fieldNumProcesses.setBounds(400, 120, 50, 30);
        panelInput.add(fieldNumProcesses);        
        
        // BUTTON: proceed
        buttonNumProceed = new JButton("Proceed");
        buttonNumProceed.setForeground(textColor);
        buttonNumProceed.setFont(new Font("Segoe UI", Font.PLAIN, 18));
        buttonNumProceed.setBounds(500, 120, 100, 30);
        panelInput.add(buttonNumProceed);
        
        // LABEL: display of input value after proceed
        labelNumProcessesInput = new JLabel("");
        labelNumProcessesInput.setForeground(textColor);
        labelNumProcessesInput.setFont(new Font("Segoe UI", Font.BOLD, 18));
        labelNumProcessesInput.setBounds(230, 50, 50, 30);
        panelInput.add(labelNumProcessesInput);

        // BUTTON: load example data
        buttonLoad = new JButton("Load Example Data");
        buttonLoad.setBounds(50, 485, 280, 50);
        panelInput.add(buttonLoad);
        
        // BUTTON: clear
        buttonClear = new JButton("Clear");
        buttonClear.setBounds(330, 485, 200, 50);
        panelInput.add(buttonClear);
        
        // BUTTON: run algorithm
        buttonRun = new JButton("Run Algorithm");
        buttonRun.setBounds(530, 485, 300, 50);
        panelInput.add(buttonRun);
        
        // LABEL: table input error message
        labelInputError = new JLabel("Please complete all table fields to proceed.");
        labelInputError.setForeground(new Color(150, 50, 50));
        labelInputError.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelInputError.setBounds(530, 530, 300, 30); // Adjust position for visibility
        panelInput.add(labelInputError);

        
        // hide table buttons first
        buttonLoad.setVisible(false);
        buttonClear.setVisible(false);
        buttonRun.setVisible(false);
        labelInputError.setVisible(false);     

        // add action listeners
        buttonNumProceed.addActionListener(this);
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
        scrollPane.setBounds(50, 90, scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height); // Position scroll pane

        // add the scroll pane directly to the panel
        panelInput.add(scrollPane); // Add scroll pane to the main panel
    }
  
    public JButton createModernButton(String text) {
        JButton button = new JButton(text);
        button.setFocusPainted(false);
        button.setBorderPainted(false);
        button.setContentAreaFilled(false);
        button.setFont(new Font("Poppins", Font.BOLD, 17));
        button.setAlignmentX(Component.CENTER_ALIGNMENT);
        button.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        button.setForeground(new Color(0, 0, 0));
        button.addActionListener(this);
        return button;
    }
    
    public JLabel createModernLabel(String text, int fontSize) {
        JLabel label = new JLabel(text);
        label.setForeground(textColor);
        
        return label;
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
                g.fillRect(0, 0, getWidth(), 50);
            }
        };
        panelOutput.setLayout(null);
        panelOutput.setBounds(0, 0, WIDTH, HEIGHT);
        panelOutput.setBackground(backgroundColor);
        
        // LABEL: title bar
        labelTitle2 = new JLabel("Highest Response Ratio Next (HRRN) Algorithm");
        labelTitle2.setForeground(textColor);
        labelTitle2.setFont(new Font("Segoe UI", Font.BOLD, 24));       
        labelTitle2.setHorizontalAlignment(SwingConstants.CENTER);
        labelTitle2.setBounds(50, 10, 800, 30);
        panelOutput.add(labelTitle2);
        
        // LABEL: gantt chart
        labelGanttChart = new JLabel("GANTT CHART");
        labelGanttChart.setForeground(new Color(150, 150, 150));
        labelGanttChart.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelGanttChart.setBounds(340, 50, 200, 30); // Adjust position for visibility
        panelOutput.add(labelGanttChart);        
        
        // LABEL: calculations
        labelCalculations = new JLabel("CALCULATIONS");
        labelCalculations.setForeground(new Color(150, 150, 150));
        labelCalculations.setFont(new Font("Segoe UI", Font.BOLD, 24));
        labelCalculations.setHorizontalAlignment(SwingConstants.CENTER);
        labelCalculations.setBounds(340, 320, 200, 30); // Adjust position for visibility
        panelOutput.add(labelCalculations);        
        
        // PANEL: calculations (temporary)
        JPanel panelCalculations = new JPanel();
        panelCalculations.setBackground(new Color(200, 200, 200));
        panelCalculations.setBounds(90, 350, 700, 200);
        panelOutput.add(panelCalculations);
        
    }
    
    public void generateGanttChart() {
        System.out.println("LOG: Generate Gantt Chart (start)");
        
        // temp variables
        int chartWidth = 700;
        int totalTime = scheduledProcesses.get(numberOfProcesses - 1).endTime;
        int unit = chartWidth / totalTime;
        
        // generate gantt chart graphics
        panelGanttChart = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                int x = 0; // Starting x position for the Gantt chart
                int y = 20; // Y position for the Gantt chart
                
                // test
                g.setColor(new Color(30, 41,44));                
                
                System.out.println("LOG: panelGanttChart Graphics");
                
                for (int i = 0; i < numberOfProcesses; i++) {
                    // retrieve process information
                    Process process = scheduledProcesses.get(i);
                    int ID = process.id;
                    int sT = process.startTime;
                    int BT = process.burstTime;
                    
                    // drawing the rectangle for the process
                    int length = BT * unit;
                    g.drawRect(x, y, length, 150);
                    g.drawString("P" + ID, x + length / 2, y + 85); // Process name
                    g.drawString(String.valueOf(sT), x, y - 10); // Start time

                    x += length; // Move to the next position
                }
                
                
                g.drawString(String.valueOf(totalTime), x, y - 10); 
            }
        };
        panelGanttChart.setPreferredSize(new Dimension(700, 200));
        panelGanttChart.setAlignmentX(Component.CENTER_ALIGNMENT);
        panelGanttChart.setBackground(new Color(220, 220, 220));
        panelGanttChart.setBounds(90, 100, 700, 200);
        panelGanttChart.setVisible(true); 
        panelOutput.add(panelGanttChart);
        
              
        
        
        System.out.println("LOG: Generate Gantt Chart (end)");   
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
    
    
    public void displayInputError() {
        // show error to user if input is invalid
        labelInputError.setVisible(true);
        
        // then hide again if user decided to edit the table
    }
    
    
    
    
    // SCHEDULING ALGORITHM COMPUTATIONS HRRN
    
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
        
        // display processes on console for logging and debugging
        displayProcessesContents();
        System.out.println(processesInput.size());
        System.out.println("\n\t==========\n");
        
        // display scheduled processes and its data
        displayScheduledProcessesContents();
    }
    
    
    public void collectChartData() {
        // get essential data from the scheduled algorithm for gantt chart
        
    }
    
    
    public void displayProcessesContents() {
        for (Process process : processesInput) {
            System.out.println("Process: " + process.id);
            System.out.println("\tArrival Time: " + process.arrivalTime);
            System.out.println("\tBurst Time: " + process.burstTime + "\n");
        }
    }
    
    public void displayScheduledProcessesContents() {
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
            
            
    
    // ACTION LISTENER METHOD
    @Override
    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();
        
        switch (action) {
            case "Proceed" -> {
                // hide description, text field and proceed button
                labelDescription.setVisible(false);
                fieldNumProcesses.setVisible(false);
                buttonNumProceed.setVisible(false);
                
                // retrieve data from the text field
                numberOfProcesses = Integer.parseInt(fieldNumProcesses.getText());
                
                // show and move the label to display input value
                labelNumProcesses.setText("Number of Processes: ");
                labelNumProcesses.setBounds(50, 50, 180, 30);
                labelNumProcessesInput.setText(String.valueOf(numberOfProcesses));
                labelNumProcessesInput.setVisible(true);
                
                
                // create the table according to number of processes
                createInputTable();
                
                // and start to show table function buttons
                buttonLoad.setVisible(true);
                buttonClear.setVisible(true);
                buttonRun.setVisible(true);
                
                
                System.out.println("LOG: Proceed Button Pressed");
                
            }
            
            case "Load Example Data" -> {
                // initialize random object
                Random random = new Random();
                
                // generate random unique arrival times
                ArrayList<Integer> arrivalTimes = new ArrayList<>();
                for (int i = 0; i < numberOfProcesses; i++) {
                    arrivalTimes.add(i);
                } 
                Collections.shuffle(arrivalTimes);
                
                // generate random burst times and update table
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
            
            case "Clear" -> {
                // set all cells to blank
                for (int row = 0; row < table.getRowCount(); row++) {
                    table.setValueAt("___", row, 1);
                    table.setValueAt("___", row, 2);
                }
                // hide message if from an error
                labelInputError.setVisible(false);
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
                    parseInputData();
                } else {
                    // otherwise show an input error
                    System.out.println("LOG: buttonRun -> Input Invalid");
                    displayInputError();
                    break;
                }
                
                // execute computation for scheduling processes
                runSchedulingAlgorithm(new ArrayList<>(processesInput));
                
                
                // show output panel
                System.out.println("LOG: Run Algorithm Button");
                panelInput.setVisible(false);
                panelOutput.setVisible(true);
                
                // then show gantt chart
                generateGanttChart();
                
                
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
