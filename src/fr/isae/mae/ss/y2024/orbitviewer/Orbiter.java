package fr.isae.mae.ss.y2024.orbitviewer;

import java.util.ArrayList;
import java.util.Random;

import fr.cnes.sirius.patrius.bodies.GeodeticPoint;
import fr.cnes.sirius.patrius.bodies.OneAxisEllipsoid;
import fr.cnes.sirius.patrius.bodies.BodyShape;
import fr.cnes.sirius.patrius.frames.Frame;
import fr.cnes.sirius.patrius.frames.FramesFactory;
import fr.cnes.sirius.patrius.math.util.FastMath;
import fr.cnes.sirius.patrius.orbits.Orbit;
import fr.cnes.sirius.patrius.orbits.OrbitType;
import fr.cnes.sirius.patrius.orbits.PositionAngle;
import fr.cnes.sirius.patrius.orbits.ApsisOrbit;
import fr.cnes.sirius.patrius.orbits.orbitalparameters.ApsisRadiusParameters;
import fr.cnes.sirius.patrius.propagation.SpacecraftState;
import fr.cnes.sirius.patrius.math.ode.FirstOrderIntegrator;
import fr.cnes.sirius.patrius.math.ode.nonstiff.ClassicalRungeKuttaIntegrator;
import fr.cnes.sirius.patrius.propagation.numerical.NumericalPropagator;
import fr.cnes.sirius.patrius.propagation.sampling.PatriusFixedStepHandler;
import fr.cnes.sirius.patrius.time.AbsoluteDate;
import fr.cnes.sirius.patrius.time.TimeScale;
import fr.cnes.sirius.patrius.time.TimeScalesFactory;
import fr.cnes.sirius.patrius.utils.Constants;
import fr.cnes.sirius.patrius.utils.exception.PatriusException;
import fr.cnes.sirius.patrius.utils.exception.PropagationException;
import gov.nasa.worldwind.geom.Position;
import gov.nasa.worldwind.WorldWind;
import gov.nasa.worldwind.avlist.AVKey;
import gov.nasa.worldwind.render.*;
import java.awt.Color;
import gov.nasa.worldwind.layers.RenderableLayer;


/**
 * Orbiter class handles the propagation of orbits and rendering them.
 */
public class Orbiter {
	
	/**
     * Runs propagation for the given orbit parameters.
     *
     * @param sma Semi-major axis
     * @param ecc Eccentricity
     * @param inc Inclination
     * @param raan Right ascension of ascending node
     * @param anm Mean anomaly
     * @param pa Argument of perigee
     * @return ArrayList of positions
     */
	public ArrayList<Position> runPropagation(double sma, double ecc, double inc, double raan, double anm, double pa) {
	    ArrayList<Position> toreturn = new ArrayList<>();

	    // Propagation 
	    // Recovery of the UTC time scale using a "factory" (not to duplicate such unique object)
	    final TimeScale TUC = TimeScalesFactory.getTAI();

	    // Date of the orbit given in UTC time scale)
	    final AbsoluteDate date = new AbsoluteDate("2010-01-01T12:00:00.000", TUC);

	    // Getting the frame with which will defined the orbit parameters
	    // As for time scale, we will use also a "factory".
	    final Frame GCRF = FramesFactory.getGCRF();

	    // Initial orbit
	    final double per = sma * (1. - ecc);
	    final double apo = sma * (1. + ecc);
	    final double MU = Constants.WGS84_EARTH_MU;

	    final ApsisRadiusParameters par = new ApsisRadiusParameters(per, apo, inc, pa, raan, anm, PositionAngle.MEAN, MU);
	    final Orbit iniOrbit = new ApsisOrbit(par, GCRF, date);

	    // We create a spacecratftstate
	    final SpacecraftState iniState = new SpacecraftState(iniOrbit);

	    // Definition of the Earth ellipsoid
	    final double AE = Constants.WGS84_EARTH_EQUATORIAL_RADIUS;
	    final BodyShape EARTH = new OneAxisEllipsoid(AE, Constants.WGS84_EARTH_FLATTENING, GCRF);

	    // Initialization of the Runge Kutta integrator with a 2 s step
	    final double pasRk = 2.;
	    final FirstOrderIntegrator integrator = new ClassicalRungeKuttaIntegrator(pasRk);

	    // Initialization of the propagator
	    final NumericalPropagator propagator = new NumericalPropagator(integrator);
	    propagator.resetInitialState(iniState);

	    // Forcing integration using Cartesian equations
	    propagator.setOrbitType(OrbitType.CARTESIAN);

	    // SPECIFIC
	    // Creation of a fixed step handler
	    final ArrayList<SpacecraftState> listOfStates = new ArrayList<SpacecraftState>();
	    PatriusFixedStepHandler myStepHandler = new PatriusFixedStepHandler() {
	        private static final long serialVersionUID = 1L;
	        public void init(SpacecraftState s0, AbsoluteDate t) {
	            // Nothing to do ...
	        }
	        public void handleStep(SpacecraftState currentState, boolean isLast) throws PropagationException {
	            // Adding S/C to the list
	            listOfStates.add(currentState);
	            try {
	                // Adjust position for Earth rotation
	                final GeodeticPoint geodeticPoint = EARTH.transform(currentState.getPVCoordinates().getPosition(), GCRF, date);
	                // Get the inertial frame of the spacecraft state
	                final Frame inertialFrame = currentState.getFrame();
	                // Get the rotation angle of the inertial frame relative to GCRF
	                final double rotationAngle = inertialFrame.getTransformTo(GCRF, date).getRotation().getAngle();
	                // Incorporate Earth rotation
	                double longitude = geodeticPoint.getLongitude() + rotationAngle;
	                // Ensure longitude is within [-PI, PI] range
	                if (longitude > FastMath.PI) longitude -= 2 * FastMath.PI;
	                else if (longitude < -FastMath.PI) longitude += 2 * FastMath.PI;
	                toreturn.add(Position.fromRadians(geodeticPoint.getLatitude(), longitude, geodeticPoint.getAltitude()));
	            } catch (PatriusException e) {
	                e.printStackTrace();
	            }
	        }
	    };

	    // The handler frequency is set to 10S
	    propagator.setMasterMode(10., myStepHandler);
	    // SPECIFIC

	    // Calculate the orbital period
	    final double T = 2 * Math.PI * Math.sqrt(Math.pow(sma, 3) / MU);
	    final AbsoluteDate finalDate = date.shiftedBy(T);

	    // Propagate for the calculated orbital period
	    SpacecraftState finalState;
	    try {
	        finalState = propagator.propagate(finalDate);
	        System.out.println(finalState.getDate().toString(TUC) + " ; LV = " + FastMath.toDegrees(finalState.getLv()) + " deg");
	    } catch (PropagationException e) {
	        e.printStackTrace();
	    }

	    // Return the list of positions
	    return toreturn;
	}
    
    // Define some colors for orbits
    private static final Color[] ORBIT_COLORS = {Color.RED, Color.GREEN, Color.BLUE, Color.ORANGE, Color.MAGENTA, Color.CYAN};
	
    /**
     * Renders the orbit path on the globe.
     *
     * @param positions ArrayList of positions representing the orbit path
     * @param orbitName Name of the orbit
     * @return RenderableLayer containing the rendered orbit path
     */
    public RenderableLayer drawOrbit(ArrayList<Position> positions, String orbitName) {
        RenderableLayer layer = new RenderableLayer();

        // Get a random color from the predefined array
        Color orbitColor = ORBIT_COLORS[new Random().nextInt(ORBIT_COLORS.length)];

        // Create and set attributes for the orbit
        ShapeAttributes attrs = new BasicShapeAttributes();
        attrs.setOutlineMaterial(new Material(orbitColor));
        attrs.setOutlineWidth(2d);

        // Create a path for the orbit
        Path path = new Path(positions);
        path.setAttributes(attrs);
        path.setVisible(true);
        path.setAltitudeMode(WorldWind.RELATIVE_TO_GROUND);
        path.setPathType(AVKey.GREAT_CIRCLE);

        // Set the name of the orbit as the display name
        layer.setName(orbitName);

        // Add tooltip with the orbit name
        path.setValue(AVKey.DISPLAY_NAME, orbitName);

        // Add the orbit path to the layer
        layer.addRenderable(path);

        return layer;
    }
}