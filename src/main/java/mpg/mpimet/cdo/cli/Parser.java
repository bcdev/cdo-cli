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

import mpg.mpimet.cdo.core.Parameter;
import mpg.mpimet.cdo.core.Task;
import mpg.mpimet.cdo.core.io.XmlTaskFileReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

/**
 * Command line parser.
 *
 * @author Ralf Quast
 * @version $Revision: 1.18 $ $Date: 2007-12-13 10:44:18 $
 */
class Parser {

    private static final File TASK_DIR = new File(System.getenv("CDO_CLI_HOME"), "tasks");

    private Context context;
    private Task task;
    private List<File> inputFileList = new ArrayList<File>();
    private File outputDir;
    private File outputFile;

    public Parser(final Context context) {
        this.context = context;
    }

    public Task getTask() {
        return task;
    }

    public File[] getInputFiles() {
        return inputFileList.toArray(new File[inputFileList.size()]);
    }

    public File getOutputDir() {
        return outputDir;
    }

    public File getOutputFile() {
        return outputFile;
    }

    public void parse(final String[] args) throws CliException {
        boolean prompt = false;

        parsing:
        for (int i = 0; i < args.length; ++i) {
            final String arg = args[i];

            if (arg.startsWith("-")) {
                if (task != null) {
                    throw new CliException(MessageFormat.format("misplaced option: {0}", arg));
                }
                if (arg.equals("-c")) {
                    assertNextArgIsNotMissingAndNoOption(args, i, "no value for option -c");
                    readProperties(new File(args[++i]));
                    continue;
                }
                if (arg.equals("-d")) {
                    assertNextArgIsNotMissingAndNoOption(args, i, "no value for option -d");
                    outputDir = new File(args[++i]);
                    continue;
                }
                if (arg.equals("-o")) {
                    assertNextArgIsNotMissingAndNoOption(args, i, "no value for option -o");
                    outputFile = new File(args[++i]);
                    continue;
                }
                if (arg.equals("-p")) {
                    prompt = true;
                    continue;
                }
                throw new CliException(MessageFormat.format("unknown option: {0}", arg));
            } else {
                if (task == null) {
                    task = readTask(arg);
                    continue;
                }

                if (inputFileList.isEmpty()) {
                    if (arg.contains("=")) {
                        for (final Parameter parameter : task.getParameters()) {
                            final String name = parameter.getName();
                            if (arg.startsWith(name + "=")) {
                                final String value = parseParameter(arg,
                                        MessageFormat.format("no value for parameter {0}", name));
                                context.setProperty(name, value);

                                continue parsing;
                            }
                        }
                        throw new CliException(MessageFormat.format("unknown parameter: {0}", arg));
                    }
                }

                inputFileList.add(new File(arg));
            }
        }

        assertNotNull(task, "missing argument: task");
        assertNotEmpty(inputFileList, "missing argument: input file");

        for (final Parameter parameter : task.getParameters()) {
            final String name = parameter.getName();
            String value = context.getProperty(name);

            while (!parameter.validate(value, context)) {
                if (prompt) {
                    try {
                        value = prompt(parameter.getShortDescription(), parameter.getValueSet(context));
                    } catch (IOException e) {
                        // ignore;
                    }
                } else {
                    if (value == null) {
                        throw new CliException(MessageFormat.format("missing parameter: {0}", name));
                    } else {
                        throw new CliException(MessageFormat.format("invalid parameter: {0}={1}", name, value));
                    }
                }
            }
            if (value == null) {
                if (parameter.hasDefaultValue()) {
                    context.setProperty(name, parameter.getDefaultValue());
                }
            } else {
                context.setProperty(name, value);
            }
        }
    }

    private void readProperties(final File file) throws CliException {
        try {
            context.loadProperties(file);
        } catch (IOException e) {
            throw new CliException(MessageFormat.format("failed to read file {0}", e.getMessage()));
        }
    }

    private static Task readTask(final String name) throws CliException {
        final File file = new File(TASK_DIR, new StringBuilder(name).append(".xml").toString());

        try {
            return new XmlTaskFileReader().read(file);
        } catch (Exception e) {
            throw new CliException(MessageFormat.format("failed to read task {0}: {1}", name, e.getMessage()));
        }
    }

    private static String parseParameter(final String arg, final String message) throws CliException {
        final int i = arg.indexOf("=") + 1;

        if (i == 0 || i == arg.length()) {
            throw new CliException(message);
        }

        return arg.substring(i);
    }

    private static void assertNextArgIsNotMissingAndNoOption(String[] args, int pos, String message)
            throws CliException {
        if (pos >= args.length - 1 || args[pos + 1].startsWith("-")) {
            throw new CliException(message);
        }
    }

    private static void assertNotNull(final Object obj, final String message) throws CliException {
        if (obj == null) {
            throw new CliException(message);
        }
    }

    private static void assertNotEmpty(final List<?> list, final String message) throws CliException {
        assertNotNull(list, message);
        if (list.isEmpty()) {
            throw new CliException(message);
        }
    }

    private static String prompt(final String description, final String valueSet) throws IOException {
        if (valueSet == null) {
            System.out.println(MessageFormat.format("[input] {0}", description));
        } else {
            System.out.println(MessageFormat.format("[input] {0} ({1})", description, valueSet));
        }
        final BufferedReader reader = new BufferedReader(new InputStreamReader(System.in));

        return reader.readLine();
    }

}
