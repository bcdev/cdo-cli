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


/**
 * A context for the execution of commands.
 *
 * @author Norman Fomferra
 * @author Ralf Quast
 * @version $Revision: 1.10 $ $Date: 2007-12-13 10:49:29 $
 */
public interface CommandContext {
    /**
     * Returns a resolved property value.
     *
     * @param key the property key.
     * @return the resolved property value, or <code>null</code> when the property is not set.
     */
    String getProperty(String key);

    /**
     * Returns a resolved property value.
     *
     * @param key          the property key.
     * @param defaultValue the value returned when the property is not set.
     * @return the resolved property value. If the property is not set the
     *         resolved default value is returned.
     */
    String getProperty(String key, String defaultValue);

    /**
     * Sets a property value.
     *
     * @param key   the property key.
     * @param value the property value.
     */
    void setProperty(String key, String value);

    /**
     * Replaces all occurences of <code>${key}</code> in a given string
     * with the value of the corresponding property.
     *
     * @param string the string.
     * @return the string with replacements made.
     */
    String resolve(String string);
}
