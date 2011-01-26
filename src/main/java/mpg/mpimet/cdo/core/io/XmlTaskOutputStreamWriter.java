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

import com.thoughtworks.xstream.XStream;
import mpg.mpimet.cdo.core.CommandSpi;
import mpg.mpimet.cdo.core.CommandSpiRegistry;
import mpg.mpimet.cdo.core.Task;

import java.io.IOException;
import java.io.OutputStream;

/**
 * Writer for task XML output streams.
 *
 * @author Ralf Quast
 * @version $Revision: 1.3 $ $Date: 2007-12-13 10:44:18 $
 */
public class XmlTaskOutputStreamWriter implements TaskOutputStreamWriter {

    private static XStream xstream;

    public void write(final Task task, final OutputStream outputStream) throws IOException {
        if (xstream == null) {
            init();
        }
        try {
            xstream.toXML(task, outputStream);
        } catch (Exception e) {
            throw new IOException(e);
        }
    }

    private static void init() {
        xstream = new XStream();

        new Task.Spi().configureXStream(xstream);

        for (final CommandSpi spi : CommandSpiRegistry.getInstance().getAll()) {
            spi.configureXStream(xstream);
        }
    }

}
