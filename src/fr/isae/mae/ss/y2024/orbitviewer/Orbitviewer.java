package fr.isae.mae.ss.y2024.orbitviewer;

import gov.nasa.worldwindx.examples.ApplicationTemplate;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.layers.RenderableLayer;

import javax.swing.SwingUtilities;
import gov.nasa.worldwind.View;

import java.util.ArrayList;


/**
 * Represents the main application class for displaying orbits.
 */
public class Orbitviewer extends ApplicationTemplate {
	
	/**
     * Represents the application frame for displaying orbits.
     */
    @SuppressWarnings("serial")
	public static class AppFrame extends ApplicationTemplate.AppFrame {

    	// Arrays to hold orbit parameters
        private double[] smas;
        private double[] eccs;
        private double[] incs;
        private double[] raans;
        private double[] anms;
        private double[] pas;
        private String[] orbitNames;

        /**
         * Constructs an instance of the AppFrame with orbit parameters.
         *
         * @param smas       Semi-major axis values
         * @param eccs       Eccentricity values
         * @param incs       Inclination values
         * @param raans      Right ascension of ascending node values
         * @param anms       Mean anomaly values
         * @param pas        Argument of perigee values
         * @param orbitNames Names of the orbits
         */
        public AppFrame(double[] smas, double[] eccs, double[] incs, double[] raans, double[] anms, double[] pas, String[] orbitNames) {
        	// Call superclass constructor with parameters
            super(true, true, false);

            // Replace BasicOrbitView with CustomOrbitView
            View view = new CustomOrbitView();
            this.getWwd().setView(view);
        	
            // Initialize orbit parameters
            this.smas = smas;
            this.eccs = eccs;
            this.incs = incs;
            this.raans = raans;
            this.anms = anms;
            this.pas = pas;
            this.orbitNames = orbitNames;
            
            // Start the simulation
            startSimulation();
        }

        /**
         * Starts the simulation with the provided orbit parameters.
         */
        public void startSimulation() {
            // Start the simulation with the provided orbit parameters
            if (smas == null || eccs == null || incs == null || raans == null || anms == null || pas == null || orbitNames == null) {
                System.out.println("Orbit parameters not set.");
                return;
            }

            // Initialize a list to hold the renderable layers
            ArrayList<RenderableLayer> renderableLayers = new ArrayList<>();

            // Create and add Orbiter objects to the list based on the provided parameters
            for (int i = 0; i < smas.length; i++) {
                Orbiter orbiter = new Orbiter();
                ArrayList<Position> positions = orbiter.runPropagation(smas[i], eccs[i], incs[i], raans[i], anms[i], pas[i]);
                String orbitName = orbitNames[i];
                RenderableLayer layer = orbiter.drawOrbit(positions, orbitName);
                renderableLayers.add(layer);
            }

            // Use SwingUtilities.invokeLater() to execute GUI-related operations on the event dispatch thread
            SwingUtilities.invokeLater(() -> {
                // Add the renderable layers to the WorldWindow
                for (RenderableLayer layer : renderableLayers) {
                    insertBeforeCompass(getWwd(), layer);
                }

                // Set the title of the frame
                setTitle("Orbit Viewer");

                // Make the frame visible
                setVisible(true);
            });
        }

    }
}