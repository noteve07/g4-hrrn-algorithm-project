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
    private JLabel labelNumProcesses;
    private JTextField fieldNumProcesses;
    private JButton buttonNumProceed;
    
    private JButton buttonLoad;
    private JButton buttonClear;
    private JButton buttonRun;
    private JLabel labelInputError;

    private JTable table;
    private DefaultTableModel model;
        
    // output gantt chart components
    private JButton buttonFinish;
    private JLabel labelGanttChart;
    private JLabel labelCalculations;
    private JLabel labelAverageWaitingTime;
            
    // screen and other properties
    private int WIDTH = 900;
    private int HEIGHT = 600;
    private int hoveredRow = -1; // Variable to store the hovered row

    // color objects
    private final Color backgroundColor = new Color(209, 222, 222);    
    private final Color underlineColor = new Color(102, 150, 134);
    
    // important values for scheduling calculations
    private int numberOfProcesses = 1;
    private ArrayList<Process> processesInput = new ArrayList<>();
    private ArrayList<Process> scheduledProcesses = new ArrayList<>();
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
        
        // show first panel
        panelInput.setVisible(true);
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
               
        // create components
        buttonLoad = new JButton("Load Example Data");
        buttonClear = new JButton("Clear");
        buttonRun = new JButton("Run Algorithm");
        labelInputError = new JLabel("Please complete all table fields to proceed.");
        buttonLoad.setVisible(false);
        buttonClear.setVisible(false);
        buttonRun.setVisible(false);
        labelInputError.setVisible(false);
        
        // set fonts
        labelInputError.setFont(new Font("Segoe UI", Font.BOLD, 14));
        labelInputError.setForeground(new Color(150, 50, 50));
        
        // set size and position
        buttonLoad.setBounds(50, 470, 280, 50);
        buttonClear.setBounds(330, 470, 200, 50);
        buttonRun.setBounds(530, 470, 300, 50);
        labelInputError.setBounds(530, 520, 300, 30);
        
        // add components to panel
        panelInput.add(buttonLoad);
        panelInput.add(buttonClear);
        panelInput.add(buttonRun);
        panelInput.add(labelInputError);
        
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
        scrollPane.setBounds(50, 70, scrollPane.getPreferredSize().width, scrollPane.getPreferredSize().height); // Position scroll pane

        // add the scroll pane directly to the panel
        panelInput.add(scrollPane); // Add scroll pane to the main panel
    }
    
    
    
    // PANEL 2: OUTPUT OF GANTT CHART, CALCULATIONS AND PROCEDURES
    public void initializeOutputPanel() {
        // panel properties
        panelOutput = new JPanel();
        panelOutput.setLayout(null);
        panelOutput.setBounds(0, 0, WIDTH, HEIGHT);

        // gantt chart panel
        generateGanttChart();
        
        // components
        buttonFinish = new JButton("Finish");
        
        
        // add components to output panel
        panelOutput.add(panelGanttChart);
        panelOutput.add(buttonFinish);
        
        // add action listeners
        buttonFinish.addActionListener(this);
    }
    
    public void generateGanttChart() {
        // gantt chart panel properties
        panelGanttChart = new JPanel();
        panelGanttChart.setBackground(new Color(150, 150, 150));
        panelGanttChart.setBounds(100, 150, 700, 200);
        
        
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
        
        // STEP 1: run the process with the earliest arrival time
        processes = sortByArrivalTime(processes);       
        Process first = processes.get(0);
        first.startTime = first.arrivalTime;
        first.endTime = first.arrivalTime + first.burstTime;
        first.waitingTime = 0;
        first.turnAroundTime = first.burstTime;
        first.responseRatio = (first.waitingTime + first.burstTime) / first.burstTime;
        scheduledProcesses.add(first);
        processes.remove(first);
        
        
        int LPeT = first.endTime;
        while (!processes.isEmpty()) {
            Process selectedProcess;
            
            // STEP 2: Assess the end time & the AT of the remaining process
            ArrayList<Process> arrivedProcesses = new ArrayList<>();
            for (Process process : processes) {
                if (process.arrivalTime <= LPeT) {
                    arrivedProcesses.add(process);
                }
            }     
            
            // if processes have arrived, move on to next second 
            if (arrivedProcesses.isEmpty()) {
                LPeT += 1;
                continue;
            }
            
            
            
            if (arrivedProcesses.size() == 1) {
                // STEP 2-A: if process == 1, then run the said process
                selectedProcess = arrivedProcesses.get(0);
            } else {
                // STEP 2-B: else, solve for the response ratio
                for (Process process : arrivedProcesses) {
                    process.waitingTime = LPeT - process.arrivalTime;
                    process.responseRatio = (double) (process.waitingTime + process.burstTime) / process.burstTime;
                }
            
            }
            
            // STEP 2-C: run the process with the highest response ratio
            selectedProcess = arrivedProcesses.get(0);
            for (Process process : arrivedProcesses) {
                if (process.responseRatio > selectedProcess.responseRatio) {
                    selectedProcess = process;
                }
            }       
            selectedProcess.startTime = LPeT;  
            selectedProcess.endTime = LPeT + selectedProcess.burstTime;
            selectedProcess.waitingTime = selectedProcess.startTime - selectedProcess.arrivalTime;
            selectedProcess.responseRatio = (double) (selectedProcess.waitingTime + selectedProcess.burstTime) / selectedProcess.burstTime;
            selectedProcess.turnAroundTime = selectedProcess.endTime - selectedProcess.arrivalTime;                                              
            scheduledProcesses.add(selectedProcess);
            processes.remove(selectedProcess);
            LPeT = selectedProcess.endTime;
                        
            
            
            // add essential information to procedural data for visualization
            Process[][] executionInfo = new Process[][]{arrivedProcesses.toArray(new Process[0]), {selectedProcess}};
            proceduralData.put(LPeT, executionInfo);
            
            
            
            // LOGS
            System.out.print("LOG: [" + LPeT + "] {");
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
    
    
    public void sortByArrivalTime() {
        // REFACTOR HERE
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
                numberOfProcesses = Integer.parseInt(fieldNumProcesses.getText());
                // create the table according to number of processes
                createInputTable();
                // show other buttons
                buttonLoad.setVisible(true);
                buttonClear.setVisible(true);
                buttonRun.setVisible(true);
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
