package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.core.CommandException;
import mpg.mpimet.cdo.core.CommandSpi;
import ucar.nc2.Dimension;
import ucar.nc2.NetcdfFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command for validating time steps.
 *
 * @author Ralf Quast
 * @version $Revision: 1.7 $ $Date: 2007-08-03 16:39:47 $
 */
@XStreamAlias("validateTimeSteps")
public class ValidateTimeStepsCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");
    private static XStream xstream = new Spi().configureXStream(new XStream());

    private String name;
    private String ifile;
    private String istep;
    private String itime;
    private boolean integralMultipleAcceptable;

    private ValidateTimeStepsCommand() {
    }
    
    public String getName() {
        return name;
    }

    public String getIFile() {
        return ifile;
    }

    public String getIStep() {
        return istep;
    }

    public String getITime() {
        return itime;
    }

    public boolean isIntegralMultipleAcceptable() {
        return integralMultipleAcceptable;
    }

    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        try {
            performValidation(context);
        } catch (Exception e) {
            throw new CommandException(
                    MessageFormat.format("command {0} failed: {1}", this, e.getMessage()), e);
        }
        logger.exiting(getClass().getName(), "execute");
    }

    private void performValidation(final CommandContext context) throws Exception {
        logger.entering(ValidateTimeStepsCommand.class.getName(), "performValidation");

        final String ipath = context.resolve(ifile);
        final int itime = toHours(context.resolve(this.itime), context);
        final int istep = toHours(context.resolve(this.istep), context);

        final int count = readTimeStepCount(ipath);
        final int expectedCount = itime / istep;

        boolean accept;

        if (integralMultipleAcceptable) {
            accept = count % expectedCount == 0;
        } else {
            accept = count == expectedCount;
        }

        if (!accept) {
            if (logger.isLoggable(Level.WARNING)) {
                logger.warning(
                        MessageFormat.format("invalid number of time steps in file ''{0}'': number of time steps found: {1}, number of time steps expected: {2}",
                                ipath, count, expectedCount));
            }

            accept = "false".equals(context.getProperty("prompt"));

            if (!accept) {
                System.out.println(
                        MessageFormat.format("[warning] invalid number of time steps in file ''{0}''", ipath));
                System.out.println(
                        MessageFormat.format("[warning] number of time steps found:    {0}", count));
                System.out.println(
                        MessageFormat.format("[warning] number of time steps expected: {0}", expectedCount));

                final String answer = prompt(MessageFormat.format("[input] accept file ''{0}'' ([yes], no)", ipath));
                accept = "yes".equals(answer);

                if (logger.isLoggable(Level.FINE)) {
                    if (accept) {
                        logger.fine("file '" + ipath + "' accepted by user");
                    } else {
                        logger.fine("file '" + ipath + "' rejected by user");
                    }
                }
            }
        }

        context.setProperty(name, String.valueOf(accept));
        logger.exiting(ValidateTimeStepsCommand.class.getName(), "performValidation");
    }

    @Override
    public String toString() {
        return xstream.toXML(this);
    }

    private static int toHours(final String time, final CommandContext context) throws Exception {
        if (time.equals(context.getProperty("one-hour"))) {
            return 1;
        }
        if (time.equals(context.getProperty("three-hours"))) {
            return 3;
        }
        if (time.equals(context.getProperty("one-day"))) {
            return 24;
        }
        if (time.equals(context.getProperty("one-month"))) {
            return 730;
        }
        if (time.equals(context.getProperty("one-year"))) {
            return 8760;
        }
        if (time.equals(context.getProperty("ten-years"))) {
            return 87600;
        }
        if (time.equals(context.getProperty("thirty-years"))) {
            return 262800;
        }

        throw new Exception(MessageFormat.format("Time period ''{0}'' is not defined", time));
    }

    private static int readTimeStepCount(final String path) throws IOException {
        logger.entering(ValidateTimeStepsCommand.class.getName(), "readTimeStepCount");
        NetcdfFile ncfile = null;

        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(MessageFormat.format("opening file ''{0}''", path));
            }
            ncfile = NetcdfFile.open(path);

            final Dimension time = ncfile.findDimension("time");
            if (time == null) {
                throw new IOException(MessageFormat.format("file ''{0}'' has no time dimension", path));
            }

            return time.getLength();
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
            logger.exiting(ValidateTimeStepsCommand.class.getName(), "readTimeStepCount");
        }
    }

    private static String prompt(final String message) throws IOException {
        logger.entering(ValidateTimeStepsCommand.class.getName(), "prompt");
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));
        String answer;

        do {
            System.out.println(message);

            answer = reader.readLine();
            if (answer.isEmpty()) {
                answer = "yes";
            }
        } while (!("yes".equals(answer) || "no".equals(answer)));

        logger.exiting(ValidateTimeStepsCommand.class.getName(), "prompt");
        return answer;
    }


    public static class Spi implements CommandSpi {
        public String getCommandId() {
            return "validateTimeSteps";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Check time steps";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, ValidateTimeStepsCommand.class);
            xstream.useAttributeFor("name", String.class);
            xstream.useAttributeFor("ifile", String.class);
            xstream.useAttributeFor("istep", String.class);
            xstream.useAttributeFor("itime", String.class);
            xstream.useAttributeFor("integralMultipleAcceptable", boolean.class);

            return xstream;
        }
    }

}
