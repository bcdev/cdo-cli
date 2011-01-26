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

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.Arrays;

/**
 * Main class.
 *
 * @author Ralf Quast
 * @version $Revision: 1.38 $ $Date: 2007-12-13 18:05:17 $
 */
public class Main {

    private static final String CDO_CLI_HOME = System.getenv("CDO_CLI_HOME");

    private static Logger logger;
    private static ConsoleHandler consoleHandler;

    static {
        logger = Logger.getLogger("mpg.mpimet.cdo");

        try {
            final FileHandler fileHandler = new FileHandler("cdo-cli.log");
            fileHandler.setLevel(Level.ALL);
            logger.addHandler(fileHandler);

            consoleHandler = new ConsoleHandler();
            consoleHandler.setLevel(Level.INFO);
            logger.addHandler(consoleHandler);

            // handlers are set, so
            logger.setUseParentHandlers(false);
            logger.setLevel(Level.INFO);
        } catch (Exception e) {
            // ignore
        }
    }

    public static void main(String[] args) {
        if (args.length != 0) {
            execute(args, new ErrorHandler() {
                public void warning(final Throwable t) {
                    logger.warning(t.getMessage());
                }

                public void error(final Throwable t) {
                    logger.severe(t.getMessage());
                    System.out.println(MessageFormat.format("ERROR: {0}", t.getMessage()));
                    System.exit(1);
                }
            });
        } else {
            printUsage();
        }
    }

    private static void execute(final String[] args, final ErrorHandler errorHandler) {
        final Context context = new Context();

        final File configFile = new File(CDO_CLI_HOME, "config.properties");
        try {
            context.loadProperties(configFile);
        } catch (IOException e) {
            errorHandler.error(e);
        }

        final Parser parser = new Parser(context);
        try {
            parser.parse(args);
        } catch (CliException e) {
            errorHandler.error(e);
        }
        try {
            final String logLevel = context.getProperty("log-level", Level.INFO.getName());
            logger.setLevel(Level.parse(logLevel.toUpperCase()));
            final String consoleLogLevel = context.getProperty("console-log-level", Level.INFO.getName());
            consoleHandler.setLevel(Level.parse(consoleLogLevel.toUpperCase()));
        } catch (IllegalArgumentException e) {
            // ignore
        }

        File outputDir = parser.getOutputDir();
        if (outputDir == null) {
            outputDir = new File(context.getProperty("output-directory", "."));
        }
        if (!outputDir.exists()) {
            if (!outputDir.mkdirs()) {
                errorHandler.error(
                        new IOException(MessageFormat.format("could not create output directory ''{0}''", outputDir)));
            }
        }

        final File[] inputFiles = parser.getInputFiles();
        final String[] outputFileNames = new String[inputFiles.length];

        Arrays.fill(outputFileNames, context.getProperty("ofile-name-pattern"));
        if (outputFileNames.length == 1) {
            if (parser.getOutputFile() != null) {
                outputFileNames[0] = parser.getOutputFile().getName();
            }
        }

        final double startTime = getTime();

        for (int i = 0; i < inputFiles.length; ++i) {
            final File inputFile = inputFiles[i];

            context.setProperty("ifile", inputFile.getPath());
            context.setProperty("ifile-basename", basename(inputFile));

            final File outputFile = new File(context.resolve(outputFileNames[i]));
            if (outputFile.isAbsolute()) {
                context.setProperty("ofile", outputFile.getPath());
            } else {
                context.setProperty("ofile", new File(outputDir, outputFile.getPath()).getPath());
            }

            if (logger.isLoggable(Level.INFO)) {
                logger.info(MessageFormat.format("processing file ''{0}''", context.getProperty("ifile")));
            }
            try {
                parser.getTask().execute(context);
            } catch (AssertionError e) {
                errorHandler.warning(e);
            } catch (Exception e) {
                errorHandler.error(e);
            }
            if (logger.isLoggable(Level.INFO)) {
                logger.info(MessageFormat.format("output written to file ''{0}''", context.getProperty("ofile")));
            }
        }

        System.out.println(MessageFormat.format(
                "task {0} completed in {1} minutes", parser.getTask().getTaskId(), getTime() - startTime));
    }

    private static double getTime() {
        return System.nanoTime() / 60000000000.0;
    }

    private static String basename(final File file) {
        final String name = file.getName();
        final int i = name.lastIndexOf(".");

        if (i != -1) {
            return name.substring(0, i);
        } else {
            return name;
        }
    }

    private static void printUsage() {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(Main.class.getResourceAsStream("usage.txt")));

        String line;
        try {
            while ((line = reader.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            // ignore
        } finally {
            try {
                reader.close();
            } catch (IOException e) {
                // ignore
            }
        }
    }

}
