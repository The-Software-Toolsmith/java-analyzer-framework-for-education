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

import java.nio.file.Path ;
import java.time.LocalDateTime ;
import java.time.format.DateTimeFormatter ;
import java.time.format.DateTimeFormatterBuilder ;
import java.time.format.DateTimeParseException ;
import java.util.List ;
import java.util.Locale ;
import java.util.Optional ;


/**
 * PLACEHOLDER
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-16 Initial implementation based on code from ChatGPT 5.2
 * @version 2.0 2025-12-21
 *     <ul>
 *     <li>renamed class
 *     <li>moved parse() from BrightspaceTime here as parseTime()
 *     </ul>
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class BrightspaceUtilities
    {

    static final Locale LOCALE = Locale.US ;
    // Try multiple patterns; first match wins.
    static final List<DateTimeFormatter> FORMATTERS
            = List.of( new DateTimeFormatterBuilder().parseCaseInsensitive()
                                                     .appendPattern( "MMM d, uuuu hmm a" )   // 601 PM, 828 PM
                                                     .toFormatter( LOCALE ),

                       new DateTimeFormatterBuilder().parseCaseInsensitive()
                                                     .appendPattern( "MMM d, uuuu h:mm a" )  // 6:01 PM (just
                                                                                             // in case)
                                                     .toFormatter( LOCALE ) ) ;


    public static Optional<LocalDateTime> parseTime( final String s )
        {

        final String text = s.trim().replaceAll( "\\s+", " " ) ;

        for ( final DateTimeFormatter f : BrightspaceUtilities.FORMATTERS )
            {

            try
                {
                return Optional.of( LocalDateTime.parse( text, f ) ) ;
                }
            catch ( final DateTimeParseException ignored )
                {}

            }

        return Optional.empty() ;

        }   // end parseTime()


    public static SubmissionInfo decodeSubmissionFolderName( final Path folder )
        {

        final String name = folder.getFileName().toString() ;
        final String[] parts = name.split( "\\s+-\\s+" ) ;

        if ( parts.length < 3 )
            {
            throw new IllegalArgumentException( "Unexpected folder name: " + name ) ;
            }

        final String[] ids = parts[ 0 ].trim().split( "-" ) ;
        final String submitterName = parts[ parts.length - 2 ].trim() ;
        final String timestamp = parts[ parts.length - 1 ].trim() ;
        final String groupId = parts.length == 4
                ? parts[ 1 ].trim()
                : null ;

        final String courseUserId = ids[ 0 ].trim() ;
        final String assignmentId = ids[ 1 ].trim() ;

        final LocalDateTime dt = BrightspaceUtilities.parseTime( timestamp ).orElse( LocalDateTime.MIN ) ; // fallback
                                                                                                     // if it
        // ever fails

        return new SubmissionInfo( courseUserId, assignmentId, groupId, submitterName, dt, folder ) ;

        }   // end decodeSubmissionFolderName()

    }   // end class BrightspaceUtilities
