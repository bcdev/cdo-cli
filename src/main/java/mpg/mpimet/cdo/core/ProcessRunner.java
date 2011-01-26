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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Class for running a system process.
 *
 * @author Ralf Quast
 * @version $Revision: 1.7 $ $Date: 2007-12-13 10:44:18 $
 */
public class ProcessRunner {

    private static Logger logger = Logger.getLogger("mpg.mpimet.cdo.core");

    public static void execute(final String command) throws Exception {
        logger.entering(ProcessRunner.class.getName(), "execute");
        if (command == null) {
            throw new NullPointerException("command == null");
        }
        if (command.isEmpty()) {
            throw new IllegalArgumentException("command.isEmpty() == true");
        }
        if (logger.isLoggable(Level.FINE)) {
            logger.fine(MessageFormat.format("executing process <code>{0}</code>", command));
        }
        final Process process = Runtime.getRuntime().exec(command);

        if (logger.isLoggable(Level.INFO)) {
            final LoggingThread err = new LoggingThread(process.getErrorStream(), logger);
            final LoggingThread out = new LoggingThread(process.getInputStream(), logger);

            err.start();
            out.start();
        }
        if (process.waitFor() != 0) {
            throw new Exception(
                    MessageFormat.format("process <code>{0}</code> terminated with exit value {1}",
                            command, process.exitValue()));
        }
        logger.exiting(ProcessRunner.class.getName(), "execute");
    }

    private static class LoggingThread extends Thread {
        private final InputStream inputStream;

        private final Logger logger;

        public LoggingThread(final InputStream inputStream, final Logger logger) {
            this.inputStream = inputStream;
            this.logger = logger;
        }

        public void run() {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line;

            try {
                while ((line = bufferedReader.readLine()) != null) {
                    logger.info(line);
                }
            } catch (IOException e) {
                // ignore
            }
        }
    }

}
