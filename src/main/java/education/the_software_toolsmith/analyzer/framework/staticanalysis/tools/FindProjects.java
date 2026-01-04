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


package education.the_software_toolsmith.analyzer.framework.staticanalysis.tools ;

import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.TestingBase.* ;

import education.the_software_toolsmith.analyzer.framework.utilities.lms.brightspace.SubmissionInfo ;

import static education.the_software_toolsmith.analyzer.framework.utilities.lms.brightspace.BrightspaceUtilities.decodeSubmissionFolderName ;
import java.io.IOException ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.util.List ;
import java.util.NoSuchElementException ;

/**
 * PLACEHOLDER
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-17 Initial implementation
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class FindProjects
    {

    public static void standardizeProjectNames( final String startIn ) throws IOException
        {

        final List<Path> dotProjectPaths = findFiles( ".project", startIn ) ;
        System.out.printf( "%,d .projects found", dotProjectPaths.size() ) ;

        for ( final Path dotProjectPath : dotProjectPaths )
            {
            final List<String> dotProjectLines = Files.readAllLines( dotProjectPath ) ;

            for ( final String dotProjectLine : dotProjectLines )
                {

                if ( dotProjectLine.contains( "<name>" )
                     && ( dotProjectLine.contains( "username" ) || dotProjectLine.contains( "group 00" ) ) )
                    {
                    int i = 1 ;
                    String copyName ;

                    do
                        {
                        copyName = String.format( ".project-dmr-%03d", i ) ;
                        System.out.printf( "%s%n", copyName ) ;
                        i++ ;
                        }
                    while ( Files.exists( Path.of( copyName ) ) ) ;

                    final Path dotProjectCopyPath = dotProjectPath.getParent().resolve( copyName ).normalize() ;


                    Path projectPath = dotProjectPath.getParent() ;

                    while ( ! "_project".equals( projectPath.getFileName().toString() ) )
                        {
                        System.out.printf( "%n...%s%n", projectPath.getFileName() ) ;
                        projectPath = projectPath.getParent() ;

                        if ( projectPath.equals( projectPath.getRoot() ) )
                            {
                            throw new NoSuchElementException( String.format( "cannot find '_project' in path: %s",
                                                                             dotProjectPath ) ) ;
                            }

                        }

                    projectPath = projectPath.getParent() ;

                    System.out.printf( "%np: %s%n", projectPath.getFileName() ) ;

                    final SubmissionInfo aboutTheProject = decodeSubmissionFolderName( projectPath ) ;
                    System.out.printf( "decoded: %s%n", aboutTheProject ) ;

                    final String projectPathName = dotProjectPath.toString() ;
                    final String[] parts = projectPathName.split( "\\s-\\s" ) ;
                    System.out.printf( "%s%n%s%n%s%n%s%n",
                                       parts[ 1 ],
                                       dotProjectLine,
                                       projectPathName,
                                       dotProjectCopyPath ) ;
                    break ; // from inner for
                    }

                }   // end inner for: each line

            }   // end outer for: each path

        }    // end standardizeProjectNames()


    public static void main( final String[] args ) throws IOException
        {

        standardizeProjectNames( "/temp/WIT/grading ~ 2025-3fa (2)/consolidated/graded" ) ;

        }    // end main()

    }   // end class FindProjects
