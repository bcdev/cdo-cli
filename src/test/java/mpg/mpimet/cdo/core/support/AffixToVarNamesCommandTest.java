package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

/**
 * Tests for class {@link AffixToVarNamesCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.3 $ $Date: 2007-08-03 11:41:59 $
 */
public class AffixToVarNamesCommandTest extends TestCase {

    public void testConfigureXStream() {
        final XStream xstream = new XStream();
        final String string = "<affixToVarNames affix=\"min\" ifile=\"old.nc\" ofile=\"new.nc\"/>";

        new AffixToVarNamesCommand.Spi().configureXStream(xstream);
        final AffixToVarNamesCommand command = (AffixToVarNamesCommand) xstream.fromXML(string);

        assertEquals("min", command.getAffix());
        assertEquals("old.nc", command.getIFile());
        assertEquals("new.nc", command.getOFile());

        assertEquals(string, command.toString());
    }

}
