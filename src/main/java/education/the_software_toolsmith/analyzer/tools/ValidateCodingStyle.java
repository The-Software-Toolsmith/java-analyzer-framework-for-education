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

import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.TestingBase.* ;


import education.the_software_toolsmith.analyzer.framework.staticanalysis.coding_style.Result ;
import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;

import static education.the_software_toolsmith.analyzer.framework.staticanalysis.coding_style.CodingStyleComplianceChecker.runCheckstyle ;

import java.nio.file.Path ;
import java.util.Arrays ;
import java.util.LinkedList ;
import java.util.List ;


/**
 * check student's implementation of an adt or application for compliance with specifications
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-06 Initial implementation
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt (nothing changed)
 */
public final class ValidateCodingStyle extends SharedState
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

        for ( final String[] findThis : new String[][] { { className + ".java",
                                                           "./src/main/java/edu/wit/scds/ds/" + ADTPathSegment + "/" } } )
            {

            for ( final Path found : findFiles( findThis[ 0 ], findThis[ 1 ] ) )
                {
                sourcePaths.add( found.normalize() ) ;
                }

            }

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

            System.out.printf( "%s%n", result.toString() ) ;
            }
        catch ( final Exception e )
            {
            System.err.printf( "%n%s%n", e.getMessage() ) ;

            throw e ;   // re-throw it
            }

        System.out.printf( "done.%n" ) ;

        }   // end main()

    }   // end class ValidateCodingStyle
