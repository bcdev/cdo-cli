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
import junit.framework.TestCase;

/**
 * Tests for class {@link CommandSpiRegistry}.
 *
 * @author Norman Fomferra
 * @version $Revision: 1.17 $ $Date: 2007-12-13 10:49:30 $
 */
public class CommandSpiRegistryTest extends TestCase {

    public void testLookUp() {
        final CommandSpiRegistry reg = CommandSpiRegistry.getInstance();
        final CommandSpi spi = new MyCommand.MyCommandSpi();

        reg.register(spi);
        assertSame(spi, reg.lookUp("myCommand"));
    }

    public void testDefaultSpis() {
        final CommandSpiRegistry reg = CommandSpiRegistry.getInstance();

        assertNotNull(reg.lookUp("affixToVarNames"));
        assertNotNull(reg.lookUp("assertNonrotatedGrid"));
        assertNotNull(reg.lookUp("cdo"));
        assertNotNull(reg.lookUp("echo"));
        assertNotNull(reg.lookUp("parallel"));
        assertNotNull(reg.lookUp("property"));
        assertNotNull(reg.lookUp("select"));
        assertNotNull(reg.lookUp("tempFile"));
        assertNotNull(reg.lookUp("validateTimeSteps"));
    }


    private static class MyCommand implements Command {
        public void execute(final CommandContext commandContext) throws CommandException {
        }

        private static class MyCommandSpi implements CommandSpi {
            public String getCommandId() {
                return "myCommand";
            }

            public String getVersion() {
                return "1.0";
            }

            public String getDescription() {
                return "";
            }

            public String getOriginator() {
                return "";
            }

            public XStream configureXStream(final XStream xstream) {
                return xstream;
            }
        }
    }

}
