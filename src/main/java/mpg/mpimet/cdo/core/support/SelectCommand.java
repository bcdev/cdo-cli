package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.core.CommandException;
import mpg.mpimet.cdo.core.CommandSpi;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Select command.
 * todo - API doc
 *
 * @author Ralf Quast
 * @version $Revision: 1.17 $ $Date: 2007-08-23 12:20:38 $
 */
@XStreamAlias("select")
public class SelectCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");

    private String name;
    private List<Case> caseList = new ArrayList<Case>();

    private Object readResolve() {
        if (caseList == null) {
            caseList = new ArrayList<Case>();
        }

        return this;
    }

    private SelectCommand() {
    }

    public String getName() {
        return name;
    }

    public Case[] getCases() {
        return caseList.toArray(new Case[caseList.size()]);
    }

    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }
        final String value = context.getProperty(name);

        if (value != null) {
            for (Case situation : caseList) {
                if (value.equals(context.resolve(situation.value))) {
                    if (logger.isLoggable(Level.FINE)) {
                        logger.fine(MessageFormat.format("executing case {0}", situation.value));
                    }
                    for (final Command command : situation.commandList) {
                        command.execute(context);
                    }

                    break;
                }
            }
        }
        logger.exiting(getClass().getName(), "execute");
    }


    @XStreamAlias("case")
    public static class Case {
        private String value;
        private List<Command> commandList = new ArrayList<Command>();

        private Object readResolve() {
            if (commandList == null) {
                commandList = new ArrayList<Command>();
            }

            return this;
        }

        private Case() {
        }
        
        public String getValue() {
            return value;
        }

        public Command[] getCommands() {
            return commandList.toArray(new Command[commandList.size()]);
        }
    }


    public static class Spi implements CommandSpi {
        public String getCommandId() {
            return "select";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Select command";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, SelectCommand.class);
            xstream.addImplicitCollection(SelectCommand.class, "caseList");
            xstream.useAttributeFor("name", String.class);
            xstream.addImplicitCollection(Case.class, "commandList");
            xstream.useAttributeFor("value", String.class);

            return xstream;
        }
    }

}
