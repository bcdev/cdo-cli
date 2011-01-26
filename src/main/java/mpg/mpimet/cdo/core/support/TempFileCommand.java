package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.core.CommandException;
import mpg.mpimet.cdo.core.CommandSpi;

import java.io.File;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command for creating a temporary file.
 * todo - API doc
 *
 * @author Ralf Quast
 * @version $Revision: 1.20 $ $Date: 2007-12-19 22:42:20 $
 */
@XStreamAlias("tempFile")
public class TempFileCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");
    private static XStream xstream = new Spi().configureXStream(new XStream());

    private String name;

    private TempFileCommand() {
    }

    public String getName() {
        return name;
    }

    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        final String prefix = context.getProperty("temp-file-prefix", "tmp");
        final String suffix = context.getProperty("temp-file-suffix", "tmp");
        final String dirPath = context.getProperty("temp-dir-path");
        final String deleteOnExit = context.getProperty("delete-on-exit");

        final File file;
        try {
            if (dirPath == null) {
                file = File.createTempFile(prefix + "-" + name + "-", suffix);
            } else {
                file = File.createTempFile(prefix + "-" + name + "-", suffix, new File(dirPath));
            }
            if (!"false".equals(deleteOnExit)) {
                file.deleteOnExit();
            }
            context.setProperty(name, file.getPath());
        } catch (Exception e) {
            throw new CommandException(
                    MessageFormat.format("command {0} failed: {1}", this, e.getMessage()), e);
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(MessageFormat.format("created temporary file ''{0}''", file));
        }
        logger.exiting(getClass().getName(), "execute");
    }

    @Override
    public String toString() {
        return xstream.toXML(this);
    }


    public static class Spi implements CommandSpi {
        public String getCommandId() {
            return "tempFile";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Command for creating a temporary file";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, TempFileCommand.class);
            xstream.useAttributeFor("name", String.class);

            return xstream;
        }
    }

}
