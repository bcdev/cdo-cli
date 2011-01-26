package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.util.Resolver;
import mpg.mpimet.cdo.util.TemplateResolver;

import java.util.Properties;

/**
 * Tests for class {@link TempFileCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.15 $ $Date: 2007-12-13 10:49:30 $
 */
public class TempFileCommandTest extends TestCase {

    private static TempFileCommand tempFileCommand;

    @Override
    protected void setUp() throws Exception {
        final XStream xstream = new XStream();
        final String string = "<tempFile name=\"my-temp-file\"/>";

        new TempFileCommand.Spi().configureXStream(xstream);
        tempFileCommand = (TempFileCommand) xstream.fromXML(string);
    }

    @Override
    protected void tearDown() throws Exception {
        tempFileCommand = null;
    }

    public void testConfigureXStream() {
        assertEquals("my-temp-file", tempFileCommand.getName());
    }

    public void testExecute() throws Exception {
        final CommandContext context = new MyContext();

        assertNull(context.getProperty("my-temp-file"));
        tempFileCommand.execute(context);
        assertNotNull(context.getProperty("my-temp-file"));

        final String tempFilePath = context.getProperty("my-temp-file");
        assertTrue(tempFilePath.contains("my-temp-file"));
        assertTrue(tempFilePath.endsWith(".tmp"));
    }


    private static class MyContext implements CommandContext {
        private final Properties properties = new Properties();
        private final Resolver resolver = new TemplateResolver(properties);

        public String getProperty(final String key) {
            return getProperty(key, null);
        }

        public String getProperty(final String key, final String defaultValue) {
            return resolve(properties.getProperty(key, defaultValue));
        }

        public void setProperty(final String key, final String value) {
            properties.setProperty(key, value);
        }

        public String resolve(String string) {
            return resolver.resolve(string);
        }
    }

}
