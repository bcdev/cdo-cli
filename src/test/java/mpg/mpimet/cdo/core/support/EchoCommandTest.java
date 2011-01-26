package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;

/**
 * Tests for clas {@link EchoCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.5 $ $Date: 2007-05-03 14:28:29 $
 */
public class EchoCommandTest extends TestCase {

    public void testConfigureXStream() {
        final XStream xstream = new XStream();
        final String string = "<echo>Hello world!</echo>";

        new EchoCommand.Spi().configureXStream(xstream);
        final EchoCommand command = (EchoCommand) xstream.fromXML(string);

        assertEquals("Hello world!", command.getMessage());
        assertEquals(string, command.toString());
    }

}
