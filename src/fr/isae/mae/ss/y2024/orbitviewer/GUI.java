package fr.isae.mae.ss.y2024.orbitviewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

//The GUI class represents the main window of the Orbitas application
@SuppressWarnings("serial")
public class GUI extends JFrame implements ActionListener {
	// Declaration of GUI components
    private JButton newOrbitButton, eraseOrbitButton, startSimulationButton, readmeButton;
    private JTable orbitTable;
    private DefaultTableModel tableModel;
    private String[] orbitNames; // Added orbitNames array
    private JLabel versionLabel;
    
    // Constructor for the GUI class
    public GUI() {
    	// Set up the main frame
        super("Orbitas - An orbit visualization tool");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        // Create panel for buttons
        JPanel buttonPanel = new JPanel(new GridLayout(3, 1));

        // Initialize buttons and add action listeners
        newOrbitButton = new JButton("New Orbit");
        eraseOrbitButton = new JButton("Erase Orbit");
        startSimulationButton = new JButton("Start Simulation");
        readmeButton = new JButton("Readme (GitHub)");

        newOrbitButton.addActionListener(this);
        eraseOrbitButton.addActionListener(this);
        startSimulationButton.addActionListener(this);
        readmeButton.addActionListener(this);

        // Add buttons to button panel
        buttonPanel.add(newOrbitButton);
        buttonPanel.add(eraseOrbitButton);
        buttonPanel.add(startSimulationButton);

        // Creatie table model and table
        tableModel = new DefaultTableModel();
        tableModel.addColumn("Name");
        tableModel.addColumn("Semi-major Axis");
        tableModel.addColumn("Eccentricity");
        tableModel.addColumn("Inclination");
        tableModel.addColumn("RAAN");
        tableModel.addColumn("Mean Anomaly");
        tableModel.addColumn("Argument of Perigee");

        orbitTable = new JTable(tableModel);

        // Create scroll pane for table
        JScrollPane scrollPane = new JScrollPane(orbitTable);

        // Create bottom panel
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.add(readmeButton, BorderLayout.LINE_END);

        // Add version label with styling
        versionLabel = new JLabel("Version Alpha 0.1");
        versionLabel.setBorder(new EmptyBorder(0, 10, 0, 0)); // Add left margin
        Font labelFont = versionLabel.getFont();
        versionLabel.setFont(new Font(labelFont.getName(), Font.PLAIN, 12)); // Setting smaller font size
        versionLabel.setForeground(Color.LIGHT_GRAY); // Setting lighter color

        bottomPanel.add(versionLabel, BorderLayout.LINE_START);

        // Add components to the main frame
        add(buttonPanel, BorderLayout.NORTH);
        add(scrollPane, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);

        // Make the frame visible
        setVisible(true);
    }
    
    // ActionListener implementation
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == newOrbitButton) {
            // Show the frame for entering orbit parameters
            new OrbitInputFrame(this);
        } else if (e.getSource() == eraseOrbitButton) {
            // Handle erase orbit action
            new EraseOrbitFrame(this);
        } else if (e.getSource() == startSimulationButton) {
            // Retrieve orbit parameters from the table and populate the arrays
            int rowCount = tableModel.getRowCount();
            if (rowCount == 0) {
                JOptionPane.showMessageDialog(this, "No orbits available to start simulation.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Prepare arrays to hold orbit parameters
            double[] smas = new double[rowCount];
            double[] eccs = new double[rowCount];
            double[] incs = new double[rowCount];
            double[] raans = new double[rowCount];
            double[] anms = new double[rowCount];
            double[] pas = new double[rowCount];
            orbitNames = new String[rowCount]; // Initialize orbitNames array

            // Populate arrays with orbit parameters
            for (int i = 0; i < rowCount; i++) {
                orbitNames[i] = (String) tableModel.getValueAt(i, 0); // Store orbit names
                smas[i] = Double.parseDouble((String) tableModel.getValueAt(i, 1));
                eccs[i] = Double.parseDouble((String) tableModel.getValueAt(i, 2));
                incs[i] = Math.toRadians(Double.parseDouble((String) tableModel.getValueAt(i, 3)));
                raans[i] = Math.toRadians(Double.parseDouble((String) tableModel.getValueAt(i, 4)));
                anms[i] = Math.toRadians(Double.parseDouble((String) tableModel.getValueAt(i, 5)));
                pas[i] = Math.toRadians(Double.parseDouble((String) tableModel.getValueAt(i, 6)));
                System.out.println("sma:" + smas[i] + ", eccs:" + eccs[i] + ", incs:" + incs[i]);
            }

            // Create and start the simulation
            @SuppressWarnings("unused")
			Orbitviewer.AppFrame appFrame = new Orbitviewer.AppFrame(smas, eccs, incs, raans, anms, pas, orbitNames);
        } else if (e.getSource() == readmeButton) {
            // Open the README file in the default web browser
            try {
                Desktop.getDesktop().browse(new URI("https://github.com/CoronadoAlvaro/Orbit-Simulator/blob/main/README.md"));
            } catch (IOException | URISyntaxException ex) {
                ex.printStackTrace();
            }
        }
    }

    // Method to add a new orbit to the table
    public void addOrbit(String[] data) {
        tableModel.addRow(data);
    }

    // Method to get the orbit table
    public JTable getOrbitTable() {
        return orbitTable;
    }

    // Main method, entry point of the application
    public static void main(String[] args) {
        SwingUtilities.invokeLater(GUI::new);
    }
}
