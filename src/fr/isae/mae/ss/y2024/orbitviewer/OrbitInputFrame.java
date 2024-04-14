package fr.isae.mae.ss.y2024.orbitviewer;

import javax.swing.*;
import javax.swing.border.EmptyBorder;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * Represents a frame for entering orbit parameters.
 */
@SuppressWarnings("serial")
public class OrbitInputFrame extends JFrame implements ActionListener {
    private GUI parentFrame;
    private JTextField nameField, semiMajorAxisField, eccentricityField, inclinationField, raanField, meanAnomalyField, argPerigeeField;
    private JButton submitButton;

    /**
     * Constructor for the OrbitInputFrame class.
     * @param parentFrame The parent GUI frame.
     */
    public OrbitInputFrame(GUI parentFrame) {
        super("Enter Orbit Parameters");
        this.parentFrame = parentFrame;
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        // Create input panel with grid layout
        JPanel inputPanel = new JPanel(new GridLayout(8, 2));
        inputPanel.setBorder(new EmptyBorder(20, 20, 20, 20)); // Add margins

        // Initialize text fields
        nameField = new JTextField();
        semiMajorAxisField = new JTextField();
        eccentricityField = new JTextField();
        inclinationField = new JTextField();
        raanField = new JTextField();
        meanAnomalyField = new JTextField();
        argPerigeeField = new JTextField();

        // Add labels and text fields to the input panel
        inputPanel.add(new JLabel("Orbit name:"));
        inputPanel.add(nameField);
        inputPanel.add(new JLabel("Semi-major Axis (km):"));
        inputPanel.add(semiMajorAxisField);
        inputPanel.add(new JLabel("Eccentricity:"));
        inputPanel.add(eccentricityField);
        inputPanel.add(new JLabel("Inclination (°):"));
        inputPanel.add(inclinationField);
        inputPanel.add(new JLabel("RAAN (°):"));
        inputPanel.add(raanField);
        inputPanel.add(new JLabel("Mean Anomaly (°):"));
        inputPanel.add(meanAnomalyField);
        inputPanel.add(new JLabel("Argument of Perigee (°):"));
        inputPanel.add(argPerigeeField);

        // Add submit button to the input panel
        submitButton = new JButton("Submit");
        submitButton.addActionListener(this);

        // Add input panel to the frame
        inputPanel.add(submitButton);

        add(inputPanel);
        setVisible(true);
    }

    /**
     * Invoked when the submit button is clicked.
     *
     * @param e The action event
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == submitButton) {
            // Check if any field is blank
            if (nameField.getText().isEmpty() || semiMajorAxisField.getText().isEmpty() || eccentricityField.getText().isEmpty() ||
                    inclinationField.getText().isEmpty() || raanField.getText().isEmpty() || meanAnomalyField.getText().isEmpty() ||
                    argPerigeeField.getText().isEmpty()) {
            	// Show error message if any field is empty
                JOptionPane.showMessageDialog(this, "Please fill in all fields.", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            // Retrieve values from text fields
            String name = nameField.getText();
            String semiMajorAxisKm = semiMajorAxisField.getText();
            String eccentricity = eccentricityField.getText();
            String inclination = inclinationField.getText();
            String raan = raanField.getText();
            String meanAnomaly = meanAnomalyField.getText();
            String argPerigee = argPerigeeField.getText();

            // Close the input frame
            double semiMajorAxisMeters = Double.parseDouble(semiMajorAxisKm) * 1000; // Convert kilometers to meters
            dispose();

            // Add orbit data to the parent frame
            parentFrame.addOrbit(new String[]{name, String.valueOf(semiMajorAxisMeters), eccentricity, inclination, raan, meanAnomaly, argPerigee});
        }
    }
}