package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.core.CommandException;
import mpg.mpimet.cdo.core.CommandSpi;

import java.text.MessageFormat;
import java.util.logging.Logger;


/**
 * Echo command.
 * todo - API doc
 *
 * @author Ralf Quast
 * @version $Revision: 1.20 $ $Date: 2007-08-03 10:39:37 $
 */
@XStreamAlias("echo")
public class EchoCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");
    private static XStream xstream = new Spi().configureXStream(new XStream());

    private String message;

    private Object readResolve() {
        if (message == null) {
            message = "";
        }

        return this;
    }

    private EchoCommand(final String message) {
        if (message == null) {
            throw new NullPointerException("message == null");
        }

        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(final String message) {
        if (message == null) {
            throw new NullPointerException("message = null");
        }

        this.message = message;
    }

    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        try {
            System.out.println(context.resolve(getMessage()));
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
            return "echo";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Sends a message to the standard output stream";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, EchoCommand.class);
            xstream.registerConverter(new EchoCommandConverter());

            return xstream;
        }
    }


    private static class EchoCommandConverter implements SingleValueConverter {
        public boolean canConvert(final Class type) {
            return EchoCommand.class.equals(type);
        }

        public String toString(Object obj) {
            return ((EchoCommand) obj).getMessage();
        }

        public Object fromString(final String str) {
            return new EchoCommand(str);
        }
    }

}
