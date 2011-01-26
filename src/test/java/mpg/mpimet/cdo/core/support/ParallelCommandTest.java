package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;
import mpg.mpimet.cdo.core.Command;

/**
 * Tests for class {@link ParallelCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.3 $ $Date: 2007-05-02 17:44:53 $
 */
public class ParallelCommandTest extends TestCase {

    public void testConfigureXStream() {
        final XStream xstream = new XStream();
        final String string = "<parallel><echo>A</echo><echo>B</echo></parallel>";

        new ParallelCommand.Spi().configureXStream(xstream);
        new EchoCommand.Spi().configureXStream(xstream);

        final ParallelCommand parallelCommand = (ParallelCommand) xstream.fromXML(string);
        final Command[] commands = parallelCommand.getCommands();

        assertEquals(2, commands.length);

        assertTrue(commands[0] instanceof EchoCommand);
        assertTrue(commands[1] instanceof EchoCommand);
        
        assertEquals("A", ((EchoCommand) commands[0]).getMessage());
        assertEquals("B", ((EchoCommand) commands[1]).getMessage());
    }

}
