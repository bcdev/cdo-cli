/*
 * Copyright (C) 2007 by Brockmann Consult (info@brockmann-consult.de)
 *
 * This program is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by the
 * Free Software Foundation. This program is distributed in the hope it will
 * be useful, but WITHOUT ANY WARRANTY; without even the implied warranty
 * of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package mpg.mpimet.cdo.core;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.annotations.Annotations;
import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Task.
 *
 * @author Ralf Quast
 * @version $Revision: 1.23 $ $Date: 2007-12-13 10:44:18 $
 */
@XStreamAlias("task")
public class Task implements Command {

    private String taskId;
    private String version;
    private String description;
    private String originator;

    private Parameter[] parameters;
    private Command[] commands;

    private Object readResolve() {
        if (parameters == null) {
            parameters = new Parameter[0];
        }
        if (commands == null) {
            commands = new Command[0];
        }

        return this;
    }

    public final String getTaskId() {
        return taskId;
    }

    public final String getVersion() {
        return version;
    }

    public final String getDescription() {
        return description;
    }

    public final String getOriginator() {
        return originator;
    }

    public final Command[] getCommands() {
        return java.util.Arrays.copyOf(commands, commands.length);
    }

    public final Parameter[] getParameters() {
        return java.util.Arrays.copyOf(parameters, parameters.length);
    }

    public void execute(final CommandContext context) throws CommandException {
        for (final Command command : commands) {
            command.execute(context);
        }
    }


    public static class Spi implements XStreamConfigurative {
        public XStream configureXStream(final XStream xstream) {
            Annotations.configureAliases(xstream, Task.class);
            Annotations.configureAliases(xstream, Parameter.class);
            xstream.useAttributeFor("name", String.class);
            xstream.useAttributeFor("description", String.class);
            xstream.useAttributeFor("shortDescription", String.class);
            xstream.useAttributeFor("type", String.class);

            xstream.useAttributeFor("defaultValue", String.class);
            xstream.aliasAttribute("default", "defaultValue");

            xstream.useAttributeFor("valueSet", String.class);
            xstream.useAttributeFor("optional", String.class);

            return xstream;
        }
    }

}
