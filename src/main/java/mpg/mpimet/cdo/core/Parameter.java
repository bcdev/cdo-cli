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

import com.thoughtworks.xstream.annotations.XStreamAlias;

/**
 * Task parameter.
 *
 * @author Ralf Quast
 * @version $Revision: 1.3 $ $Date: 2007-12-13 10:44:18 $
 */
@XStreamAlias("parameter")
public class Parameter {

    private String name;
    private String description;
    private String shortDescription;
    private String type;

    @XStreamAlias("default")
    private String defaultValue;

    private String valueSet;
    private boolean optional;

    private Object readResolve() {
        if (shortDescription == null) {
            shortDescription = description;
        }

        return this;
    }

    public final String getName() {
        return name;
    }

    public final String getDescription() {
        return description;
    }

    public final String getShortDescription() {
        return shortDescription;
    }

    public final String getType() {
        return type;
    }

    public final boolean hasDefaultValue() {
        return defaultValue != null;
    }

    public final String getDefaultValue() {
        return defaultValue;
    }

    public final String getValueSet() {
        return valueSet;
    }

    public final boolean isOptional() {
        return optional;
    }

    public String getValueSet(final CommandContext context) {
        final String vs = context.resolve(valueSet);

        if (vs != null && vs.contains(":")) {
            final String[] parts = vs.split(":");

            if (!parts[1].isEmpty()) {
                final int i = parts[0].indexOf(parts[1]) + 1;

                if (i > 0) {
                    return parts[0].substring(i + parts[1].length());
                }
            }

            return parts[0];
        }

        return vs;
    }

    public boolean validate(final String value, final CommandContext context) {
        if (value == null) {
            return hasDefaultValue() || isOptional();
        }

        if ("box".equals(type)) {
            final String[] parts = value.split(",");
            if (parts == null || parts.length != 4) {
                return false;
            }
            for (int i = 0; i < 2; ++i) {
                try {
                    final double lon = Double.parseDouble(parts[i]);
                    if (lon < -180.0 || lon > 180.0) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            for (int i = 2; i < 4; ++i) {
                try {
                    final double lat = Double.parseDouble(parts[i]);
                    if (lat < -90.0 || lat > 90.0) {
                        return false;
                    }
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            return true;
        }
        if ("fraction".equals(type)) {
            try {
                final double d = Double.parseDouble(value);
                if (d < 0.0 || d > 1.0) {
                    return false;
                }
            } catch (NumberFormatException e) {
                return false;
            }
            return true;
        }
        if (valueSet == null) {
            return true;
        }

        for (final String validValue : getValueSet(context).split(",")) {
            if (value.equals(validValue)) {
                return true;
            }
        }

        return false;
    }
    
}
