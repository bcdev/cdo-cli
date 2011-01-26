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
package mpg.mpimet.cdo.core.io;

import junit.framework.TestCase;
import mpg.mpimet.cdo.core.Command;
import mpg.mpimet.cdo.core.Task;
import mpg.mpimet.cdo.core.Parameter;
import mpg.mpimet.cdo.core.support.CdoCommand;
import mpg.mpimet.cdo.core.support.TempFileCommand;

import java.io.IOException;

/**
 * Tests for class {@link mpg.mpimet.cdo.core.io.XmlTaskInputStreamReader}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.8 $ $Date: 2007-12-13 10:44:18 $
 */
public class XmlTaskInputStreamReaderTest extends TestCase {

    public void testRead() throws IOException {
        final Task task = new XmlTaskInputStreamReader().read(getClass().getResourceAsStream("test.xml"));
        assertNotNull(task);

        final Parameter[] parameters = task.getParameters();
        assertEquals(4, parameters.length);

        assertEquals("ifile", parameters[0].getName());
        assertEquals("ofile", parameters[1].getName());
        assertEquals("istep", parameters[2].getName());
        assertEquals("ostep", parameters[3].getName());
        assertNull(parameters[0].getValueSet());
        assertNull(parameters[1].getValueSet());
        assertEquals("hour,day,month", parameters[2].getValueSet());
        assertEquals("day,month,year", parameters[3].getValueSet());

        final Command[] commands = task.getCommands();
        assertEquals(5, commands.length);

        assertTrue(commands[0] instanceof TempFileCommand);
        assertTrue(commands[1] instanceof TempFileCommand);
        assertTrue(commands[2] instanceof CdoCommand);
        assertTrue(commands[3] instanceof CdoCommand);
        assertTrue(commands[4] instanceof CdoCommand);
    }

}
