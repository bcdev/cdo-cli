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
package mpg.mpimet.cdo.util;

/**
 * Resolver interface.
 *
 * @author Ralf Quast
 * @version $Revision: 1.6 $ $Date: 2007-12-13 10:49:30 $
 */
public interface Resolver {
    /**
     * Makes a two-pass replacement for property names found in a string.
     *
     * @param string the string.
     * @return the string with replacements made.
     */
    String resolve(String string);
}
