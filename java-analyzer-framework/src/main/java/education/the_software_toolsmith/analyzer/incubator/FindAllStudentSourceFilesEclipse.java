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


package education.the_software_toolsmith.analyzer.incubator ;

import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.TestingBase.* ;

import education.the_software_toolsmith.analyzer.framework.staticanalysis.tools.PathUtils ;

import static education.the_software_toolsmith.analyzer.framework.utilities.SharedState.* ;
import java.nio.file.Path ;
import java.util.ArrayList ;
import java.util.List ;
import java.util.ListIterator ;

/**
 * find all submission folders in the grading folders
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-15 Initial implementation
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class FindAllStudentSourceFilesEclipse
    {

    public static void main( final String[] args )
        {

        List<Path> paths = new ArrayList<>( 100 ) ;
        final List<String> folders = new ArrayList<>( 100 ) ;
        final List<Path> projects = new ArrayList<>( 100 ) ;
        final List<String> studentNames = new ArrayList<>( 100 ) ;

        final String hereYouAre = "./../" ;
        final Path youAreHere = Path.of( hereYouAre ) ;
        System.out.printf( "you are here: %s%n", youAreHere.toAbsolutePath().toString() ) ;

        paths = findFiles( className + ".java", hereYouAre ) ;

        System.out.printf( "%,d paths found%n", paths.size() ) ;

        final Path relative = Path.of( PathUtils.computeBaseDirFor( paths ) ) ;
        System.out.printf( "relative to: %s%n", relative ) ;


        final ListIterator<Path> pathWalker = paths.listIterator() ;

        while ( pathWalker.hasNext() )
            {
            final Path nextPath = pathWalker.next() ;

            final Path relativePath = nextPath.subpath( youAreHere.getNameCount(), nextPath.getNameCount() ) ;
            String relativePathText = relativePath.toString().toLowerCase() ;

            if ( relativePath.toString().startsWith( "autograder" ) )
                {
                pathWalker.remove() ;
                continue ;
                }

            final Path studentProjectFolder = relativePath.getName( 0 ) ;
//            pathWalker.set( studentProjectFolder ) ;
            projects.add( studentProjectFolder ) ;


            relativePathText = studentProjectFolder.toString().toLowerCase() ;
            folders.add( relativePathText ) ;
            final String[] folderNameParts = relativePathText.split( "\\~" ) ;


            int leftIndex = 0 ;
            int rightIndex = folderNameParts.length - 1 ;

            if ( "DS".equalsIgnoreCase( folderNameParts[ leftIndex ].trim() ) )
                {
                leftIndex++ ;
                }

            if ( "Lab 01".equalsIgnoreCase( folderNameParts[ leftIndex ].trim() ) )
                {
                leftIndex++ ;
                }

            if ( folderNameParts[ rightIndex ].trim().startsWith( "2025-" ) )
                {
                rightIndex-- ;
                }

            final StringBuilder studentNameParts = new StringBuilder() ;

            for ( int i = leftIndex ; i <= rightIndex ; i++ )
                {
                studentNameParts.append( folderNameParts[ i ].trim() ) ;

                if ( i < rightIndex )
                    {
                    studentNameParts.append( " " ) ;
                    }

                }

            String studentName = studentNameParts.toString() ;

            final int found = studentName.indexOf( " - DMR username".toLowerCase() ) ;

            if ( found > 0 )
                {
                studentName = studentName.substring( 0, found ) ;
                }

            studentNames.add( studentName ) ;
            }

        studentNames.sort( null ) ;
        folders.sort( null ) ;
        projects.sort( null ) ;
        paths.sort( null );

        for ( int i = 0 ; i < studentNames.size() ; i++ )
            {
            System.out.printf( "%-20s @ %s%n", studentNames.get( i ), paths.get( i ) ) ;
            }

        System.out.printf( "%n%,d paths remaining%n", folders.size() ) ;

        }	// end main()

    }   // end class FindAllStudentSourceFiles
