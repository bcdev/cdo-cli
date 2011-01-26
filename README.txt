CDO command line interface, version ${project.version}
December 14, 2007

=============================================================================
1. About this software
=============================================================================

The CDO Command Line Interface (CLI) is a command line tool which, for a
certain set of tasks, provides a simplified usage of the Climate Data
Operators (CDO) developed by the Max Planck Institute for Meteorology.

The CDO-CLI has been developed by Brockmann Consult under contract with the
M&D Group and is distributed under the GNU General Public License (GPL).

=============================================================================
2. System requirements
=============================================================================

Besides the Climate Data Operators this software requires Java Runtime
Environment 6 or later. To check the version of the installed JRE open
a terminal window and type:

    java -version

The output of this command should be similar to:

    java version "1.6.0"
    Java(TM) SE Runtime Environment ...
    Java HotSpot(TM) Client VM ...

If your system is not compliant, please consult your system administrator.
The latest Java software for Linux, Solaris and Windows is available for
download at

    http://java.sun.com/javase/downloads/

To install the Java software please follow the instructions.

=============================================================================
2. Installation
=============================================================================

Simply unpack the archive to your preferred location. The command line
tool resides in the directory

    ${install-dir}/cdo-cli-${project.version}/bin

To be able to run the command line tool from within any directory, you
have to append the directory path to the PATH environment variable

    PATH="$PATH:$HOME/cdo-cli-${project.version}/bin"; export PATH

In addition, you may have to edit the 'config.properties' which reside
in the 'cdo-cli-${project.version}' directory.  Be sure that the entry
defining the path of the CDO executable corresponds to the CDO actually
installed on your system.

=============================================================================
2. Usage
=============================================================================

Type 'cdo-cli' to obtain a usage message.  The output is placed in the
current working directory unless an output directory path is specified
by the '-d' option. If the output file name is unspecified or more than
a single input file path is given, the output file names are generated
automatically.

=============================================================================
4. Support
=============================================================================

For help on the installation and usage of this software please contact

Ralf Quast
Brockmann Consult
Max-Planck-Str 2
D-21502 Geesthacht

ralf.quast@brockmann-consult.de
