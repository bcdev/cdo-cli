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

import java.util.HashMap;
import java.util.Map;
import java.util.ServiceLoader;

/**
 * {@link Command} service provider interface (SPI) registry.
 *
 * @author Norman Fomferra
 * @version $Revision: 1.11 $ $Date: 2007-12-13 10:49:29 $
 */
public class CommandSpiRegistry {

    private final static CommandSpiRegistry instance;

    private Map<String, CommandSpi> spiMap;

    static {
        instance = new CommandSpiRegistry();
        instance.update();
    }

    public static CommandSpiRegistry getInstance() {
        return instance;
    }

    /**
     * Gets all SPIs.
     *
     * @return the SPIs, or an empty array
     */
    public CommandSpi[] getAll() {
        return spiMap.values().toArray(new CommandSpi[0]);
    }

    /**
     * Gets the SPI with the given identifier.
     *
     * @param commandId the identifier.
     * @return the SPI, or {@code null} if no such could be found
     */
    public CommandSpi lookUp(final String commandId) {
        return spiMap.get(commandId);
    }

    /**
     * Adds the given SPI to this registry.
     * An existing SPI with the same identifier will be replaced.
     *
     * @param spi the SPI.
     */
    public void register(final CommandSpi spi) {
        spiMap.put(spi.getCommandId(), spi);
    }

    /**
     * Updates this registry by scanning all JARs in the classpath for {@link CommandSpi}s
     * by looking up {@code META-INF/services/mpg.mpimet.cdo.core.CommandSpi}.
     */
    public void update() {
        final ServiceLoader<CommandSpi> serviceLoader = ServiceLoader.load(CommandSpi.class);
        serviceLoader.reload();
        for (final CommandSpi spi : serviceLoader) {
            register(spi);
        }
    }

    private CommandSpiRegistry() {
        spiMap = new HashMap<String, CommandSpi>(17);
    }
    
}
