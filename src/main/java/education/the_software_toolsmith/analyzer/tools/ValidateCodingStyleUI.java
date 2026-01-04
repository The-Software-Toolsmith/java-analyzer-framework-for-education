/* @formatter:off
 *
 * Copyright © 2016-2025 David M Rosenberg, The Software Toolsmith
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * @formatter:on
 */


package education.the_software_toolsmith.analyzer.tools ;

import education.the_software_toolsmith.analyzer.framework.staticanalysis.coding_style.Result ;
import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;
import education.the_software_toolsmith.analyzer.framework.utilities.ui.FileSystemUI ;

import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.TestingBase.findFiles ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.coding_style.CodingStyleComplianceChecker.runCheckstyle ;
import static education.the_software_toolsmith.analyzer.framework.utilities.ui.FileSystemUI.forAny ;
import static education.the_software_toolsmith.analyzer.incubator.Extracter.findStudentFiles ;

import java.io.File ;
import java.nio.file.Path ;
import java.util.Arrays ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Scanner ;


/**
 * check student's implementation of an adt or application for compliance with specifications
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-27 Initial implementation - based on ValidateCodingStyle
 */
public final class ValidateCodingStyleUI extends SharedState
    {


    /**
     * test driver
     *
     * @param args
     *     -unused-
     *
     * @throws Exception
     *     if checkstyle fails catastrophically
     */
    public static void main( final String[] args ) throws Exception
        {
// DMR TODO prompt for location of checkstyle rules

// DMR TODO map results by filename/full - allow one pass across multiple submissions with distinct results

        final List<Path> pathsTocheckstyleRules = findFiles( "checkstyle.xml", "./config" ) ;
        final Path pathToCheckstyleRules = pathsTocheckstyleRules.getFirst().normalize() ;

        if ( pathsTocheckstyleRules.size() > 1 )
            {
            System.out.printf( "found %,d checkstyle.xml file(s):%n\t%s, using the first:%n\t%s%n%n",
                               pathsTocheckstyleRules.size(),
                               Arrays.toString( pathsTocheckstyleRules.toArray() ),
                               pathToCheckstyleRules ) ;
            }

        // let's see if we're compliant with our coding style
        final List<Path> sourcePaths = new LinkedList<>() ;

        final FileSystemUI sourceFilePicker = forAny() ;
        sourceFilePicker.setStartIn( Path.of( "./src/main/java" ) ) ;

        String filenameToLookFor = null ;

        try ( Scanner input = new Scanner( System.in ) )
            {
            System.out.printf( "What file(s) do you want to analyze [default is *.java, quit to exit]? " ) ;

            if ( input.hasNextLine() )
                {
                filenameToLookFor = input.nextLine() ;
                }

            }

        if ( "quit".equalsIgnoreCase( filenameToLookFor ) )
            {
            System.out.printf( "%ndone.%n" ) ;

            return ;
            }

        if ( ( filenameToLookFor == null ) || ( "".equals( filenameToLookFor ) ) )
            {
            filenameToLookFor = "*.java" ;
            }

        final String dialogWindowTitle = String.format( "Where should we look for %s?", filenameToLookFor ) ;

        while ( true )
            {
            sourceFilePicker.chooseMultiple( dialogWindowTitle, "Select" ) ;

            if ( sourceFilePicker.isEmpty() )
                {
                break ;
                }

            for ( final Path chosenPath : sourceFilePicker )
                {
                final File chosenFile = chosenPath.toFile() ;

                if ( chosenFile.isDirectory() )
                    {
                    // evaluate all java source files starting with the selected directory

                    final List<Path> foundJavaFiles = findStudentFiles( chosenPath, filenameToLookFor ) ;

                    if ( ! foundJavaFiles.isEmpty() )
                        {

                        for ( final Path selectedPath : foundJavaFiles )
                            {
                            sourcePaths.add( selectedPath.normalize().toAbsolutePath() ) ;
                            }

                        }

                    }
                else
                    {
                    sourcePaths.add( chosenPath ) ;
                    }

                }   // end for selecting files to analyze

            }   // end while

        System.out.printf( "%nsource file%s:%n",
                           sourcePaths.size() == 1
                                   ? ""
                                   : "s" ) ;

        for ( final Path sourcePath : sourcePaths )
            {
            System.out.printf( "%s%n", sourcePath ) ;
            }

        System.out.printf( "%n" ) ;

        try
            {
            Result result = null ;
            result = runCheckstyle( pathToCheckstyleRules, sourcePaths ) ;

            System.out.printf( "%s%n", result.summary.toString() ) ;
            System.out.printf( "%s%n", result.report.toString() ) ;
            }
        catch ( final Exception e )
            {
            System.err.printf( "%n%s%n", e.getMessage() ) ;

            throw e ;   // re-throw it
            }

        System.out.printf( "done.%n" ) ;

        }   // end main()

    }   // end class ValidateCodingStyle
