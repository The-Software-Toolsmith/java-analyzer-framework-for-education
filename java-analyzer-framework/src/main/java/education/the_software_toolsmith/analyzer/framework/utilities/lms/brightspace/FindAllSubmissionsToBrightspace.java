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


package education.the_software_toolsmith.analyzer.framework.utilities.lms.brightspace ;

import java.nio.file.Files ;
import java.nio.file.Path ;
import java.time.LocalDateTime ;
import java.util.HashMap ;
import java.util.List ;
import java.util.ListIterator ;
import java.util.Map ;
import java.util.Map.Entry ;
import java.util.stream.Collectors ;
import java.util.stream.Stream ;

/**
 * find all submission folders in the grading folders
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-15 Initial implementation
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt (nothing changed)
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class FindAllSubmissionsToBrightspace
    {

    public static enum IndividualOrGroup
        {

         I( "Individual" ),
         G( "Group" );


        private final String label ;


        private IndividualOrGroup( final String forDisplay )
            {

            this.label = forDisplay ;

            }


        @Override
        public String toString()
            {

            return this.label ;

            }

        }   // end enum IndividualOrGroup


    public record SubmissionInfo( String lastName,
                                  String firstName,
                                  int brightspaceIndividualId,
                                  int brightspaceGroupId,
                                  int brightspaceAssignmentId,
                                  String brightspaceSubmissionId,
                                  IndividualOrGroup individualOrGroup,
                                  String group,
                                  String submitter,
                                  LocalDateTime submissionTimestamp,
                                  Path submissionFolder )
        {}


    public static Map<String, SubmissionInfo> getSubmissionsInfo( final String baseFolderName )
        {

        // info extracted from submission folder
        final Map<String, SubmissionInfo> submissionsInfo ;


        String firstName = null ;
        String lastName = null ;

        int brightspaceIndividualId = -1 ;
        int brightspaceGroupId = -1 ;
        int brightspaceAssignmentId = -1 ;

        IndividualOrGroup individualOrGroup = null ;

        String group = null ;
        String submitter = null ;

        LocalDateTime whenSubmitted = null ;

        Path submissionFolder = null ;

        final Path baseFolderPath = Path.of( baseFolderName ) ;
        System.out.printf( "base folder: %s%n", baseFolderPath.toAbsolutePath().toString() ) ;


        List<Path> folders = null ;

        try ( Stream<Path> entries = Files.list( baseFolderPath ) )
            {
            folders = entries.filter( Files::isDirectory ) // Filter for only directories
                             .collect( Collectors.toList() ) ;
            }
        catch ( final Exception e )
            {
            // STUB: handle exception
            }

        if ( folders == null )
            {
            // didn't find anything
            return null ;
            }

        // we can size the map precisely
        submissionsInfo = new HashMap<>( folders.size() ) ;


        final ListIterator<Path> pathWalker = folders.listIterator() ;

        while ( pathWalker.hasNext() )
            {
            final Path nextPath = pathWalker.next() ;

            submissionFolder = nextPath.getFileName() ;
            final String submissionFolderName = submissionFolder.toString() ;


            final String[] folderNameParts = submissionFolderName.split( " - " ) ;
            // 3 pieces for individual: id, name, datetime; 4 for group: id, group, name, datetime


            final String brightspaceSubmissionId = folderNameParts[ 0 ].trim() ;
            final String[] submissionIdParts = brightspaceSubmissionId.split( "-" ) ;

            brightspaceIndividualId = Integer.parseInt( submissionIdParts[ 0 ].trim() ) ;
            brightspaceGroupId = brightspaceIndividualId ;  // same thing for now
            brightspaceAssignmentId = Integer.parseInt( submissionIdParts[ 1 ] ) ;

            whenSubmitted = BrightspaceUtilities.parseTime( folderNameParts[ folderNameParts.length - 1 ] )
                                           .orElseGet( null ) ;


            final String studentFullName = folderNameParts[ folderNameParts.length - 2 ].trim() ;

            if ( folderNameParts.length == 3 )
                {
                // individual
                individualOrGroup = IndividualOrGroup.I ;
                group = "n/a" ;
                submitter = folderNameParts[ 1 ] ;
                }
            else
                {
                // group
                individualOrGroup = IndividualOrGroup.G ;
                group = folderNameParts[ 1 ].trim() ;
                submitter = folderNameParts[ 2 ] ;
                }

            final String[] studentNameParts = studentFullName.split( "\\s+" ) ;

            // student info piece
            firstName = studentNameParts[ 0 ] ;

            if ( studentNameParts.length == 2 )
                {
                // student info piece
                lastName = studentNameParts[ 1 ] ;
                }
            else
                {
                final StringBuilder glueItTogether = new StringBuilder( studentNameParts[ 1 ] ) ;

                for ( int j = 2 ; j < studentNameParts.length ; j++ )
                    {
                    glueItTogether.append( " " ).append( studentNameParts[ j ] ) ;
                    }

                // student info piece
                lastName = glueItTogether.toString() ;
                }

            submissionsInfo.put( brightspaceSubmissionId,
                                 new SubmissionInfo( lastName,
                                                     firstName,
                                                     brightspaceIndividualId,
                                                     brightspaceGroupId,
                                                     brightspaceAssignmentId,
                                                     brightspaceSubmissionId,
                                                     individualOrGroup,
                                                     group,
                                                     submitter,
                                                     whenSubmitted,
                                                     submissionFolder ) ) ;
            }

        return submissionsInfo ;

        }   // end getSubmissionsInfo()


    public static Map<String, SubmissionInfo>
           submissionsInfoForAssignment( final Map<String, SubmissionInfo> rawSubmissionsInfo,
                                         final int assignmentIdOfInterest )
        {

        final Map<String, SubmissionInfo> filteredSubmissionInfo = new HashMap<>() ;

        for ( final Entry<String, SubmissionInfo> submissionEntry : rawSubmissionsInfo.entrySet() )
            {
            final SubmissionInfo submissionInfo = submissionEntry.getValue() ;

            if ( submissionInfo.brightspaceAssignmentId == assignmentIdOfInterest )
                {
                filteredSubmissionInfo.put( submissionEntry.getKey(), submissionInfo ) ;
                }

            }

        return filteredSubmissionInfo ;

        }   // end submissionsInfoForAssignment()


    public static Map<String, SubmissionInfo>
           submissionsInfoForGroup( final Map<String, SubmissionInfo> rawSubmissionsInfo,
                                    final int groupIdOfInterest )
        {

        final Map<String, SubmissionInfo> filteredSubmissionInfo = new HashMap<>() ;

        for ( final Entry<String, SubmissionInfo> submissionEntry : rawSubmissionsInfo.entrySet() )
            {
            final SubmissionInfo submissionInfo = submissionEntry.getValue() ;

            if ( ( submissionInfo.individualOrGroup == IndividualOrGroup.G )
                 && ( submissionInfo.brightspaceIndividualId == groupIdOfInterest ) )
                {
                filteredSubmissionInfo.put( submissionEntry.getKey(), submissionInfo ) ;
                }

            }

        return filteredSubmissionInfo ;

        }   // end submissionsInfoForIndividual()


    public static Map<String, SubmissionInfo>
           submissionsInfoForIndividual( final Map<String, SubmissionInfo> rawSubmissionsInfo,
                                         final int individualIdOfInterest )
        {

        final Map<String, SubmissionInfo> filteredSubmissionInfo = new HashMap<>() ;

        for ( final Entry<String, SubmissionInfo> submissionEntry : rawSubmissionsInfo.entrySet() )
            {
            final SubmissionInfo submissionInfo = submissionEntry.getValue() ;

            if ( ( submissionInfo.individualOrGroup == IndividualOrGroup.I )
                 && ( submissionInfo.brightspaceIndividualId == individualIdOfInterest ) )
                {
                filteredSubmissionInfo.put( submissionEntry.getKey(), submissionInfo ) ;
                }

            }

        return filteredSubmissionInfo ;

        }   // end submissionsInfoForIndividual()


    public static void main( final String[] args )
        {

        final String baseFolderName = "/temp/WIT/grading ~ 2025-3fa (2)/consolidated/to-grade" ;

        // info extracted from submission folder
        final Map<String, SubmissionInfo> submissionsInfo = getSubmissionsInfo( baseFolderName ) ;

        System.out.printf( "%n%,d submissions:%n%n", submissionsInfo.size() ) ;
        displayInfo( submissionsInfo ) ;

        int individualId = 14896 ;
        final Map<String, SubmissionInfo> selectedStudentSubmissionInfo
                = submissionsInfoForIndividual( submissionsInfo, individualId ) ;

        System.out.printf( "%n%n%,d submissions for individual %d:%n%n",
                           selectedStudentSubmissionInfo.size(),
                           individualId ) ;
        displayInfo( selectedStudentSubmissionInfo ) ;

        }    // end main()
    
    
    public static void displayInfo( final Map<String, SubmissionInfo> submissionsInfo )
        {
        
        for ( final Entry<String, SubmissionInfo> submissionEntry : submissionsInfo.entrySet() )
            {
            final SubmissionInfo submission = submissionEntry.getValue() ;
            System.out.printf( "%7d %7d %-15s .. %-20s .. %6s%s .. %ta %<tb %<te,%<tY %<tl:%<tM %<tp%n",
                               submission.brightspaceAssignmentId,
                               submission.brightspaceIndividualId,
                               submission.brightspaceSubmissionId,
                               String.format( "%s, %s", submission.lastName, submission.firstName ),
                               submission.individualOrGroup,
                               submission.individualOrGroup == IndividualOrGroup.I
                                       ? ""
                                       : String.format( " %-13s (%-20s)",
                                                        submission.group,
                                                        submission.submitter ),
                               submission.submissionTimestamp ) ;
            }

        }   // end displayInfo()
    

    }   // end class FindAllSubmissionsToBrightspace
