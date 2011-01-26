package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.converters.SingleValueConverter;
import mpg.mpimet.cdo.core.*;

import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Logger;
import java.util.logging.Level;

/**
 * Command for executing climate data operators (CDO).
 * todo - API doc
 *
 * @author Ralf Quast
 * @version $Revision: 1.25 $ $Date: 2007-12-19 22:42:20 $
 */
@XStreamAlias("cdo")
public class CdoCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");
    private static XStream xstream = new Spi().configureXStream(new XStream());

    private String arg;

    private Object readResolve() {
        if (arg == null) {
            arg = "";
        }

        return this;
    }

    private CdoCommand(final String arg) {
        if (arg == null) {
            throw new NullPointerException("arg == null");
        }

        this.arg = arg;
    }

    public String getArg() {
        return arg;
    }

    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        final String path = cdoFilePath(context);
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(MessageFormat.format("found CDO executable ''{0}''", path));
        }
        final String command = new StringBuilder(path).append(" ").append(arg).toString();

        try {
            ProcessRunner.execute(context.resolve(command));
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

    private String cdoFilePath(CommandContext context) throws CommandException {
        final String name = context.getProperty("cdo-name");
        final String path = context.getProperty("cdo-path");

        if (name != null) {
            if (path != null) {
                for (final String parent : path.split(":")) {
                    final File file = new File(parent, name);

                    if (file.isFile()) {
                        return file.getPath();
                    }
                }
            }
        }
        throw new CommandException(
                MessageFormat.format("command {0} failed: CDO executable not found", this));
    }


    public static class Spi implements CommandSpi {
        public String getCommandId() {
            return "cdo";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Execution of climate data operators (CDO)";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, CdoCommand.class);
            xstream.registerConverter(new CdoCommandConverter());

            return xstream;
        }
    }


    private static class CdoCommandConverter implements SingleValueConverter {
        public boolean canConvert(final Class type) {
            return CdoCommand.class.equals(type);
        }

        public String toString(Object obj) {
            return ((CdoCommand) obj).getArg();
        }

        public Object fromString(final String str) {
            return new CdoCommand(str);
        }
    }
}
