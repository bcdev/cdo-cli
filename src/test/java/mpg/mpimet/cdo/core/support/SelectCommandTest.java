package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.CommandContext;

import java.util.Properties;

/**
 * Tests for class {@link SelectCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.9 $ $Date: 2007-05-04 15:36:30 $
 */
public class SelectCommandTest extends TestCase {

    private static SelectCommand selectCommand;

    @Override
    protected void setUp() throws Exception {
        final XStream xstream = new XStream();

        new SelectCommand.Spi().configureXStream(xstream);
        new EchoCommand.Spi().configureXStream(xstream);

        selectCommand = (SelectCommand) xstream.fromXML(getClass().getResourceAsStream("select-command-test.xml"));
    }

    @Override
    protected void tearDown() throws Exception {
        selectCommand = null;
    }

    public void testConfigureXStream() {
        assertEquals("letter", selectCommand.getName());
        SelectCommand.Case[] cases = selectCommand.getCases();
        assertEquals(2, cases.length);

        // Case A
        assertEquals("A", cases[0].getValue());
        Command[] commands = cases[0].getCommands();
        assertEquals(1, commands.length);
        assertTrue(commands[0] instanceof EchoCommand);
        EchoCommand echoCommand = (EchoCommand) commands[0];
        assertEquals("I am an A.", echoCommand.getMessage());

        // Case B
        assertEquals("B", cases[1].getValue());
        commands = cases[1].getCommands();
        assertEquals(2, commands.length);
        assertTrue(commands[0] instanceof EchoCommand);
        echoCommand = (EchoCommand) commands[0];
        assertEquals("I am a B,", echoCommand.getMessage());
        assertTrue(commands[1] instanceof EchoCommand);
        echoCommand = (EchoCommand) commands[1];
        assertEquals("not an A.", echoCommand.getMessage());
    }

    public void testExecute() throws Exception {
        System.out.println("This test has failed if you do not read the following sentence twice:");
        System.out.println("I am a B,");
        System.out.println("not an A.");

        selectCommand.execute(new CommandContext() {
            final Properties properties = new Properties();

            {
                properties.setProperty("letter", "B");
            }

            public String getProperty(final String key) {
                return properties.getProperty(key);
            }

            public String getProperty(final String key, final String defaultValue) {
                return properties.getProperty(key, "");
            }

            public void setProperty(final String key, final String value) {
                properties.setProperty(key, value);
            }

            public String resolve(String string) {
                return string;
            }
        });
    }

}
