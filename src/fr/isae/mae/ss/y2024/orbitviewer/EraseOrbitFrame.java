package fr.isae.mae.ss.y2024.orbitviewer;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 * EraseOrbitFrame allows users to select and erase orbits from the main GUI.
 */
@SuppressWarnings("serial")
public class EraseOrbitFrame extends JFrame implements ActionListener {
    private GUI parentFrame; // Reference to the parent GUI frame
    private JComboBox<String> orbitComboBox; // Combo box to select orbit for erasing
    private JButton eraseButton; // Button to trigger orbit erasing

    /**
     * Constructs a new EraseOrbitFrame.
     *
     * @param parentFrame The parent GUI frame
     */
    public EraseOrbitFrame(GUI parentFrame) {
        super("Select Orbit to Erase");
        this.parentFrame = parentFrame;
        setSize(300, 150);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(2, 1));

        // Create combo box to display available orbits
        orbitComboBox = new JComboBox<>();
        JTable orbitTable = parentFrame.getOrbitTable(); // Access orbitTable
        for (int i = 0; i < orbitTable.getRowCount(); i++) {
            orbitComboBox.addItem((String) orbitTable.getValueAt(i, 0)); // Add orbit names to combo box
        }
        panel.add(orbitComboBox);
        
        // Create button to erase selected orbit
        eraseButton = new JButton("Erase Orbit");
        eraseButton.addActionListener(this);
        panel.add(eraseButton);

        // Add panel to frame
        add(panel);
        setVisible(true);
    }

    /**
     * Handles actions performed by the user.
     *
     * @param e The ActionEvent triggering the action
     */
    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == eraseButton) {
            // Get the selected orbit to erase
            String selectedOrbit = (String) orbitComboBox.getSelectedItem();
            DefaultTableModel model = (DefaultTableModel) parentFrame.getOrbitTable().getModel();
            // Find and remove the selected orbit from the table
            for (int i = 0; i < model.getRowCount(); i++) {
                if (model.getValueAt(i, 0).equals(selectedOrbit)) {
                    model.removeRow(i);
                    break;
                }
            }
            // Close the erase orbit frame
            dispose();
        }
    }
}
