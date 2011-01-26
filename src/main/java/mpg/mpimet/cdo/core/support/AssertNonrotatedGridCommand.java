package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.core.CommandException;
import mpg.mpimet.cdo.core.CommandSpi;
import mpg.mpimet.cdo.core.Command;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command for asserting that a file has a nonrotated grid.
 *
 * @author Ralf Quast
 * @version $Revision: 1.6 $ $Date: 2007-08-03 16:39:47 $
 */
@XStreamAlias("assertNonrotatedGrid")
public class AssertNonrotatedGridCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");
    private static XStream xstream = new Spi().configureXStream(new XStream());

    private String ifile;

    private AssertNonrotatedGridCommand() {
    }
    
    public String getIFile() {
        return ifile;
    }

    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        try {
            performAssertion(context.resolve(ifile));
        } catch (Exception e) {
            throw new CommandException(
                    MessageFormat.format("command {0} failed: {1}", this, e.getMessage()), e);
        }
        logger.exiting(getClass().getName(), "execute");
    }

    private static void performAssertion(final String path) throws AssertionError, IOException {
        logger.entering(AssertNonrotatedGridCommand.class.getName(), "performAssertion");
        NetcdfFile ncfile = null;

        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(MessageFormat.format("opening file ''{0}''", path));
            }
            ncfile = NetcdfFile.open(path);
            final Variable rotatedPole = ncfile.findVariable("rotated_pole");

            if (rotatedPole == null) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(MessageFormat.format("file ''{0}'' has a nonrotated grid", path));
                }
            } else {
                throw new AssertionError(
                        MessageFormat.format("file ''{0}'' has a rotated grid", path));
            }
        } finally {
            try {
                if (ncfile != null) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(MessageFormat.format("closing file ''{0}''", path));
                    }
                    ncfile.close();
                }
            } catch (IOException e) {
                // ignore
            }
        }
        logger.exiting(AssertNonrotatedGridCommand.class.getName(), "performAssertion");
    }

    @Override
    public String toString() {
        return xstream.toXML(this);
    }


    public static class Spi implements CommandSpi {
        public String getCommandId() {
            return "assertNonrotatedGrid";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Assert that a file has a nonrotated grid";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, AssertNonrotatedGridCommand.class);
            xstream.useAttributeFor("ifile", String.class);

            return xstream;
        }
    }

}
