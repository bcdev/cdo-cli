package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.core.CommandException;
import mpg.mpimet.cdo.core.CommandSpi;

import java.text.MessageFormat;
import java.util.logging.Logger;

/**
 * Command for setting a property.
 *
 * @author Ralf Quast
 * @version $Revision: 1.8 $ $Date: 2007-08-03 10:39:37 $
 */
@XStreamAlias("property")
public class PropertyCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");
    private static XStream xstream = new Spi().configureXStream(new XStream());

    private String name;
    private String value;

    private PropertyCommand() {
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        try {
            context.setProperty(name, value);
        } catch (Exception e) {
            throw new CommandException(
                    MessageFormat.format("command {0} failed: {1}", this, e.getMessage()), e);
        }
        logger.exiting(getClass().getName(), "execute");
    }

    @Override
    public String toString() {
        return xstream.toXML(this);
    }


    public static class Spi implements CommandSpi {
        public String getCommandId() {
            return "property";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Command for setting a property";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, PropertyCommand.class);
            xstream.useAttributeFor("name", String.class);
            xstream.useAttributeFor("value", String.class);

            return xstream;
        }
    }

}
