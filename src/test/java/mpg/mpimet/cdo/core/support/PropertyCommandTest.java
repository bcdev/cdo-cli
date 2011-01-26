package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import junit.framework.TestCase;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.util.TemplateResolver;
import mpg.mpimet.cdo.util.Resolver;

import java.util.Properties;

/**
 * Tests for class {@link PropertyCommand}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.7 $ $Date: 2007-12-13 10:49:30 $
 */
public class PropertyCommandTest extends TestCase  {

    private PropertyCommand propertyCommand;

    @Override
    protected void setUp() throws Exception {
        final XStream xstream = new XStream();
        final String string = "<property name=\"my-property\" value=\"my-value\"/>";

        new PropertyCommand.Spi().configureXStream(xstream);
        propertyCommand = (PropertyCommand) xstream.fromXML(string);
    }

    @Override
    protected void tearDown() throws Exception {
        propertyCommand = null;
    }

    public void testConfigureXStream() {
        assertEquals("my-property", propertyCommand.getName());
        assertEquals("my-value", propertyCommand.getValue());
    }

    public void testExecute() throws Exception {
        final CommandContext context = new MyContext();

        assertNull(context.getProperty("my-property"));
        propertyCommand.execute(context);
        assertNotNull(context.getProperty("my-property"));
        assertEquals("my-value", context.getProperty("my-property"));
    }


    private static class MyContext implements CommandContext {
        private final Properties properties = new Properties();
        private final Resolver resolver = new TemplateResolver(properties);

        public String getProperty(String key) {
            return getProperty(key, null);
        }

        public String getProperty(String key, String defaultValue) {
            return resolver.resolve(properties.getProperty(key, defaultValue));
        }

        public void setProperty(String key, String value) {
            properties.setProperty(key, value);
        }

        public String resolve(String string) {
            return resolver.resolve(string);
        }
    }

}
