package mpg.mpimet.cdo.core.support;

import junit.framework.TestCase;
import com.thoughtworks.xstream.XStream;

/**
 * Tests for class {@link AssertNonrotatedGridCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.1 $ $Date: 2007-07-25 14:35:25 $
 */
public class AssertNonrotatedGridCommandTest extends TestCase {

    public void testConfigureXStream() {
        final XStream xstream = new XStream();
        final String string = "<assertNonrotatedGrid ifile=\"in.nc\"/>";

        new AssertNonrotatedGridCommand.Spi().configureXStream(xstream);
        final AssertNonrotatedGridCommand command = (AssertNonrotatedGridCommand) xstream.fromXML(string);

        assertEquals("in.nc", command.getIFile());
        assertEquals(string, command.toString());
    }

}
