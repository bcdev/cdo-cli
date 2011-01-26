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
 * A service provider interface (SPI) for {@link Command}s.
 *
 * @author Norman Fomferra
 * @version $Revision: 1.12 $ $Date: 2007-12-13 10:49:29 $
 */
public interface CommandSpi extends XStreamConfigurative {
    String getCommandId();

    String getVersion();

    String getDescription();

    String getOriginator();
}
