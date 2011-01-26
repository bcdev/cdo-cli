package mpg.mpimet.cdo.core.support;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.core.CommandException;
import mpg.mpimet.cdo.core.CommandSpi;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Parallel command.
 *
 * @author Ralf Quast
 * @version $Revision: 1.9 $ $Date: 2007-08-03 10:39:37 $
 */
@XStreamAlias("parallel")
public class ParallelCommand implements Command {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core.support");

    private List<Command> commandList = new ArrayList<Command>();

    private Object readResolve() {
        if (commandList == null) {
            commandList = new ArrayList<Command>();
        }

        return this;
    }

    private ParallelCommand() {
    }

    public Command[] getCommands() {
        return commandList.toArray(new Command[commandList.size()]);
    }

    public void add(final Command command) {
        if (command == null) {
            throw new NullPointerException("command == null");
        }

        commandList.add(command);
    }

    /**
     * Executes the enclosed commands in parallel.
     *
     * @param context
     * @throws CommandException
     */
    public void execute(final CommandContext context) throws CommandException {
        logger.entering(getClass().getName(), "execute");
        if (context == null) {
            throw new NullPointerException("context == null");
        }

        for (final Command command : commandList) {
            // todo - execute in parallel
            command.execute(context);
        }
        logger.exiting(getClass().getName(), "execute");
    }


    public static class Spi implements CommandSpi {
        public String getCommandId() {
            return "parallel";
        }

        public String getVersion() {
            return "1.0";
        }

        public String getDescription() {
            return "Parallel command";
        }

        public String getOriginator() {
            return "BC";
        }

        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, ParallelCommand.class);
            xstream.addImplicitCollection(ParallelCommand.class, "commandList");

            return xstream;
        }
    }

}
