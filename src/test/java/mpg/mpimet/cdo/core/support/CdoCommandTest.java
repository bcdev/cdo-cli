package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

/**
 * Tests for class {@link mpg.mpimet.cdo.core.support.CdoCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.12 $ $Date: 2007-07-25 14:42:34 $
 */
public class CdoCommandTest extends TestCase {

    public void testConfigureXStream() {
        final XStream xstream = new XStream();
        final String string = "<cdo>timmean in.nc out.nc</cdo>";

        new CdoCommand.Spi().configureXStream(xstream);
        final CdoCommand command = (CdoCommand) xstream.fromXML(string);

        assertEquals("timmean in.nc out.nc", command.getArg());
        assertEquals(string, command.toString());
    }

}
