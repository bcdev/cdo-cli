package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

/**
 * Tests for class {@link ValidateTimeStepsCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.2 $ $Date: 2007-07-25 14:35:25 $
 */
public class ValidateTimeStepsCommandTest extends TestCase {

    public void testConfigureXStream() {
        final XStream xstream = new XStream();
        final String string = "<validateTimeSteps name=\"valid\" ifile=\"in.nc\" istep=\"d\" itime=\"y\" integralMultipleAcceptable=\"true\"/>";

        new ValidateTimeStepsCommand.Spi().configureXStream(xstream);
        final ValidateTimeStepsCommand command = (ValidateTimeStepsCommand) xstream.fromXML(string);

        assertEquals("valid", command.getName());
        assertEquals("in.nc", command.getIFile());
        assertEquals("d", command.getIStep());
        assertEquals("y", command.getITime());
        assertTrue(command.isIntegralMultipleAcceptable());
    }

}
