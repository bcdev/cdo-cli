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

import junit.framework.TestCase;

import java.util.Properties;


/**
 * Unit tests for class {@link TemplateResolver}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.6 $ $Date: 2007-12-13 10:49:30 $
 */
public class TemplateResolverTest extends TestCase {

    public void testResolveNullString() {
        final Properties properties = new Properties();
        final TemplateResolver templateResolver = new TemplateResolver(properties);

        assertEquals(null,
                templateResolver.resolve(null));
    }

    public void testResolveUnknownProperties() {
        final Properties properties = new Properties();
        final TemplateResolver templateResolver = new TemplateResolver(properties);

        assertEquals("cat dog pig",
                templateResolver.resolve("cat dog pig"));

        assertEquals("cat dog ${foo}",
                templateResolver.resolve("cat dog ${foo}"));
    }

    public void testResolveKnownProperties() {
        final Properties properties = new Properties();
        final TemplateResolver templateResolver = new TemplateResolver(properties);
        properties.setProperty("foo", "bar");

        assertEquals("cat dog pig",
                templateResolver.resolve("cat dog pig"));

        assertEquals("cat dog bar",
                templateResolver.resolve("cat dog ${foo}"));
    }

    public void testResolvePropertiesWithNonWordCharacters() {
        final Properties properties = new Properties();
        final TemplateResolver templateResolver = new TemplateResolver(properties);
        properties.setProperty("foo.bar", "cow");
        properties.setProperty("foo-bar", "pig");

        assertEquals("cat dog cow",
                templateResolver.resolve("cat dog ${foo.bar}"));

        assertEquals("cat dog pig",
                templateResolver.resolve("cat dog ${foo-bar}"));
    }

    public void testResolvePropertySequence() {
        final Properties properties = new Properties();
        properties.setProperty("foo", "bar");

        assertEquals("barbar",
                new TemplateResolver(properties).resolve("${foo}${foo}"));
    }

    public void testResolveInvalidExpressions() {
        final Properties properties = new Properties();
        final TemplateResolver templateResolver = new TemplateResolver(properties);

        properties.setProperty("foo", "bar");

        assertEquals("$foo cat dog",
                templateResolver.resolve("$foo cat dog"));
        assertEquals("cat dog {foo}",
                templateResolver.resolve("cat dog {foo}"));
        assertEquals("cat dog ${{foo}",
                templateResolver.resolve("cat dog ${{foo}"));
        assertEquals("cat dog ${foo!}",
                templateResolver.resolve("cat dog ${foo!}"));
    }

    public void testResolveMutualDependencies() {
        final Properties properties = new Properties();
        final TemplateResolver templateResolver = new TemplateResolver(properties);

        properties.setProperty("bar", "${foo} ${cat}");
        properties.setProperty("foo", "${cat}");
        properties.setProperty("cat", "dog");

        assertEquals("dog pig cow",
                templateResolver.resolve("${foo} pig cow"));
        assertEquals("cow pig ${cat} dog",
                templateResolver.resolve("cow pig ${bar}"));
    }

    public void testResolveCyclicDependencies() {
        final Properties properties = new Properties();
        final TemplateResolver templateResolver = new TemplateResolver(properties);

        properties.setProperty("bar", "${foo}");
        properties.setProperty("foo", "${cat}");
        properties.setProperty("cat", "${foo}");

        assertEquals("${foo} pig cow",
                templateResolver.resolve("${foo} pig cow"));
        assertEquals("${foo} pig ${cat}",
                templateResolver.resolve("${foo} pig ${bar}"));
    }

}
