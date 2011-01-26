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
package mpg.mpimet.cdo.cli;

import mpg.mpimet.cdo.core.CommandContext;
import mpg.mpimet.cdo.util.Resolver;
import mpg.mpimet.cdo.util.TemplateResolver;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Properties;

/**
 * Context.
 *
 * @author Ralf Quast
 * @version $Revision: 1.13 $ $Date: 2007-12-13 10:49:29 $
 */
class Context implements CommandContext {

    private Properties properties = new Properties();
    private Resolver resolver = new TemplateResolver(properties);

    public String getProperty(final String key) {
        return getProperty(key, null);
    }

    public String getProperty(final String key, final String defaultValue) {
        return resolve(properties.getProperty(key, defaultValue));
    }

    public String resolve(final String string) {
        return resolver.resolve(string);
    }

    public void setProperty(final String key, final String value) {
        properties.setProperty(key, value);
    }

    public void loadProperties(final File file) throws IOException {
        if (file != null) {
            final Reader reader = new FileReader(file);

            try {
                properties.load(reader);
            } finally {
                try {
                    reader.close();
                } catch (IOException e) {
                    // ignore
                }
            }
        }
    }

}
