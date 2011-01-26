package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.core.CommandException;
import mpg.mpimet.cdo.core.CommandSpi;
import ucar.nc2.Dimension;
import ucar.nc2.FileWriter;
import ucar.nc2.NetcdfFile;
import ucar.nc2.Variable;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Command for appending an affix to the names of all time series data sets.
 *
 * @author Ralf Quast
 * @version $Revision: 1.4 $ $Date: 2007-08-03 16:39:47 $
 */
@XStreamAlias("affixToVarNames")
public class AffixToVarNamesCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");
    private static XStream xstream = new Spi().configureXStream(new XStream());

    private String affix;
    private String ifile;
    private String ofile;

    private AffixToVarNamesCommand() {
    }

    public String getAffix() {
        return affix;
    }

    public String getIFile() {
        return ifile;
    }

    public String getOFile() {
        return ofile;
    }

    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        try {
            performAffixation(context.resolve(ifile),
                    context.resolve(ofile),
                    context.resolve(affix));
        } catch (Exception e) {
            throw new CommandException(
                    MessageFormat.format("command {0} failed: {1}", this, e.getMessage()), e);
        }
        logger.exiting(getClass().getName(), "execute");
    }

    @SuppressWarnings("unchecked")
    private static void performAffixation(final String ipath,
                                          final String opath,
                                          final String affix) throws IOException {
        logger.entering(AffixToVarNamesCommand.class.getName(), "performAffixation");

        NetcdfFile ifile = null;
        NetcdfFile ofile = null;

        try {
            if (logger.isLoggable(Level.FINE)) {
                logger.fine(MessageFormat.format("opening file ''{0}''", ipath));
            }
            ifile = NetcdfFile.open(ipath);
            final Dimension time = ifile.findDimension("time");

            if (time != null) {
                final List<Variable> variableList = ifile.getVariables();

                for (final Variable var : variableList) {
                    if (var.getRank() > 2) {
                        if (time.equals(var.getDimension(0))) {
                            final String iname = var.getShortName();
                            final String oname = new StringBuilder(iname).append(affix).toString();

                            if (logger.isLoggable(Level.FINE)) {
                                logger.fine(MessageFormat.format("renaming variable ''{0}'' into ''{1}''", iname, oname));
                            }
                            var.setName(oname);
                        }
                    }
                }
            } else {
                if (logger.isLoggable(Level.WARNING)) {
                    logger.warning("file '" + ipath + "' has no time dimension");
                }
            }

            if (logger.isLoggable(Level.FINE)) {
                logger.fine(MessageFormat.format("writing file ''{0}''", opath));
            }
            ofile = FileWriter.writeToFile(ifile, opath);
        } finally {
            try {
                if (ifile != null) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(MessageFormat.format("closing file ''{0}''", ipath));
                    }
                    ifile.close();
                }
            } catch (IOException e) {
                // ignore
            }
            if (ofile != null) {
                if (logger.isLoggable(Level.FINE)) {
                    logger.fine(MessageFormat.format("closing file ''{0}''", opath));
                }
                ofile.close();
            }
        }

        logger.exiting(AffixToVarNamesCommand.class.getName(), "performAffixation");
    }

    @Override
    public String toString() {
        return xstream.toXML(this);
    }


    public static class Spi implements CommandSpi {
        public String getCommandId() {
            return "affixToVarNames";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Appends an affix to the names of all time series data sets";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, AffixToVarNamesCommand.class);
            xstream.useAttributeFor("affix", String.class);
            xstream.useAttributeFor("ifile", String.class);
            xstream.useAttributeFor("ofile", String.class);

            return xstream;
        }
    }

}
