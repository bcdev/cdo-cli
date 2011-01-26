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

import junit.framework.TestCase;

import java.io.File;
import java.net.URL;
import java.net.URISyntaxException;

/**
 * Tests for class {@link Parser}.
 *
 * @author Ralf Quast
 * @version $Revision: 1.17 $ $Date: 2007-12-15 10:32:32 $
 */
public class ParserTest extends TestCase {

    private Context context;

    @Override
    protected void setUp() throws Exception {
        context = new Context();
    }

    @Override
    protected void tearDown() throws Exception {
        context = null;
    }

    public void testSomeArgs() throws CliException {
        final Parser parser = new Parser(context);

        parser.parse(new String[]{"test", "istep=hour", "ostep=year", "box=0.0,0.0,1.0,1.0", "in.nc"});
        assertEquals("test", parser.getTask().getTaskId());

        final File[] inputFileNames = parser.getInputFiles();
        assertEquals(1, inputFileNames.length);
        assertEquals("in.nc", inputFileNames[0].getPath());

        assertEquals("hour", context.getProperty("istep"));
        assertEquals("year", context.getProperty("ostep"));
        assertEquals("0.0,0.0,1.0,1.0", context.getProperty("box"));
        assertEquals("${ifile-basename}_test.nc", context.getProperty("ofile-name-pattern"));
    }

    public void testMoreArgs() throws CliException {
        final Parser parser = new Parser(context);

        parser.parse(new String[]{"-d", "out", "-o", "c.nc", "test", "istep=hour", "ostep=year",
                "box=0.0,0.0,1.0,1.0", "ofile-name-pattern=my-pattern", "a.nc", "b.nc"});
        assertEquals("test", parser.getTask().getTaskId());

        final File[] inputFileNames = parser.getInputFiles();
        assertEquals(2, inputFileNames.length);
        assertEquals("a.nc", inputFileNames[0].getPath());
        assertEquals("b.nc", inputFileNames[1].getPath());

        assertEquals("c.nc", parser.getOutputFile().getName());
        assertEquals("out", parser.getOutputDir().getPath());

        assertEquals("hour", context.getProperty("istep"));
        assertEquals("year", context.getProperty("ostep"));
        assertEquals("0.0,0.0,1.0,1.0", context.getProperty("box"));
        assertEquals("my-pattern", context.getProperty("ofile-name-pattern"));
    }

    public void testConfigFileOption() throws CliException, URISyntaxException {
        final Parser parser = new Parser(context);
        final File configFile = getResourceAsFile("config.properties");

        parser.parse(new String[]{"-c", configFile.getPath(), "test", "a.nc", "b.nc"});
        assertEquals("test", parser.getTask().getTaskId());

        final File[] inputFileNames = parser.getInputFiles();
        assertEquals(2, inputFileNames.length);
        assertEquals("a.nc", inputFileNames[0].getPath());
        assertEquals("b.nc", inputFileNames[1].getPath());

        assertEquals("my-output-dir", context.getProperty("output-directory"));

        assertEquals("hour", context.getProperty("istep"));
        assertEquals("year", context.getProperty("ostep"));
        assertEquals("0.0,0.0,1.0,1.0", context.getProperty("box"));
        assertEquals("my-pattern", context.getProperty("ofile-name-pattern"));
    }

    public void testParametersPresetInContext() throws CliException {
        context.setProperty("istep", "hour");
        context.setProperty("ostep", "year");
        context.setProperty("box", "0.0,0.0,1.0,1.0");
        context.setProperty("ofile-name-pattern", "my-pattern");

        final Parser parser = new Parser(context);
        parser.parse(new String[]{"test", "in.nc"});
        assertEquals("test", parser.getTask().getTaskId());

        final File[] inputFileNames = parser.getInputFiles();
        assertEquals(1, inputFileNames.length);
        assertEquals("in.nc", inputFileNames[0].getPath());

        assertEquals("hour", context.getProperty("istep"));
        assertEquals("year", context.getProperty("ostep"));
        assertEquals("0.0,0.0,1.0,1.0", context.getProperty("box"));
        assertEquals("my-pattern", context.getProperty("ofile-name-pattern"));
    }

    public void testUnknownTask() throws CliException {
        try {
            new Parser(new Context()).parse(new String[]{"unknown-task", "istep=hour", "ostep=year", "test.nc"});
        } catch (CliException e) {
            assertTrue(e.getMessage().startsWith("failed to read task"));
        }
    }

    public void testWrongParameters() throws CliException {
        try {
            new Parser(new Context()).parse(new String[]{"test", "wrong=hour", "ostep=year", "test.nc"});
        } catch (CliException e) {
            assertEquals("unknown parameter: wrong=hour", e.getMessage());
        }

        try {
            new Parser(new Context()).parse(new String[]{"test", "istep=wrong", "ostep=year", "test.nc"});
        } catch (CliException e) {
            assertEquals("invalid parameter: istep=wrong", e.getMessage());
        }

        try {
            new Parser(new Context()).parse(new String[]{"test", "istep=", "ostep=year", "test.nc"});
        } catch (CliException e) {
            assertEquals("no value for parameter istep", e.getMessage());
        }

        try {
            new Parser(new Context()).parse(new String[]{"test", "istep=hour", "test.nc"});
        } catch (CliException e) {
            assertEquals("missing parameter: ostep", e.getMessage());
        }
    }

    public void testMissingArgs() {
        try {
            new Parser(new Context()).parse(new String[0]);
            fail();
        } catch (CliException e) {
            assertEquals("missing argument: task", e.getMessage());
        }

        try {
            new Parser(new Context()).parse(new String[]{"test"});
            fail();
        } catch (CliException e) {
            assertEquals("missing argument: input file", e.getMessage());
        }
    }

    private static File getResourceAsFile(String name) throws URISyntaxException {
        final URL url = ParserTest.class.getResource(name);

        return new File(url.toURI());
    }
}
