package fr.isae.mae.ss.y2024.orbitviewer;

import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.view.orbit.BasicOrbitView;

/**
 * CustomOrbitView extends the BasicOrbitView to provide custom behavior for far distance computation.
 */
public class CustomOrbitView extends BasicOrbitView {

	/**
     * Constructs a new CustomOrbitView.
     */
    public CustomOrbitView() {
        super();
    }

    /**
     * Computes the far distance based on the eye position.
     *
     * @param eyePosition The eye position in the globe
     * @return The computed far distance
     */
    @Override
    protected double computeFarDistance(Position eyePosition) {
        // Get the current far distance from the superclass
        double farDistance = super.computeFarDistance(eyePosition);

        // Get the altitude of the eye position
        double eyeAltitude = eyePosition.getElevation(); 

        // Define a minimum distance to avoid clipping
        double minDistance = eyeAltitude * 3; // Adjust this factor as needed

        // Ensure the far distance is at least the minimum distance
        if (farDistance < minDistance) {
            farDistance = minDistance;
        }

        return farDistance;
    }
}
