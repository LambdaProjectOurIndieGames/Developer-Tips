import java.io.File;
import java.util.Locale;

import org.hipparchus.util.FastMath;
import org.orekit.data.DataProvidersManager;
import org.orekit.data.DirectoryCrawler;
import org.orekit.errors.OrekitException;
import org.orekit.frames.Frame;
import org.orekit.frames.FramesFactory;
import org.orekit.orbits.KeplerianOrbit;
import org.orekit.orbits.Orbit;
import org.orekit.orbits.PositionAngle;
import org.orekit.propagation.SpacecraftState;
import org.orekit.propagation.analytical.KeplerianPropagator;
import org.orekit.time.AbsoluteDate;
import org.orekit.time.TimeScale;
import org.orekit.time.TimeScalesFactory;

public class SlaveMode {

    public static void main(String[] args) {
        try {

            File home       = new File(System.getProperty("user.home"));
            File orekitData = new File(home, "orekit-data");
            if (!orekitData.exists()) {
                System.err.format(Locale.US, orekitData.getAbsolutePath());
                System.err.format(Locale.US, home.getAbsolutePath());
                System.exit(1);
            }
            DataProvidersManager manager = DataProvidersManager.getInstance();
            manager.addProvider(new DirectoryCrawler(orekitData));

            double a = 24396159;
            double e = 0.72831215; 
            double i = FastMath.toRadians(7);
            double omega = FastMath.toRadians(180);
            double raan = FastMath.toRadians(261); 
            double lM = 0;

           
            Frame inertialFrame = FramesFactory.getEME2000();

            TimeScale utc = TimeScalesFactory.getUTC();
            AbsoluteDate initialDate = new AbsoluteDate(2004, 01, 01, 23, 30, 00.000, utc); //Data iniziale

            double mu =  3.986004415e+14;

            Orbit initialOrbit = new KeplerianOrbit(a, e, i, omega, raan, lM, PositionAngle.MEAN,
                                                    inertialFrame, initialDate, mu);

            KeplerianPropagator kepler = new KeplerianPropagator(initialOrbit);

            kepler.setSlaveMode();

            double duration = 600.;

            final AbsoluteDate finalDate = initialDate.shiftedBy(duration);

            double stepT = 60.;

            int cpt = 1;
            for (AbsoluteDate extrapDate = initialDate;
                 extrapDate.compareTo(finalDate) <= 0;
                 extrapDate = extrapDate.shiftedBy(stepT))  {

                SpacecraftState currentState = kepler.propagate(extrapDate);
                System.out.println("Passo " + cpt++);
                System.out.println("Tempo : " + currentState.getDate());
                System.out.println(" " + currentState.getOrbit());

            }

        } catch (OrekitException oe) {
            System.err.println(oe.getMessage());
        }
    }

}
