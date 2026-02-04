/* @formatter:off
 *
 * Copyright © 2025-2026 David M Rosenberg, The Software Toolsmith
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


package education.the_software_toolsmith.analyzer.framework.static_analysis.style ;

import com.puppycrawl.tools.checkstyle.api.AuditEvent ;

/**
 * representation of a checkstyle file audit start/end
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2026-01-11 extracted from base class
 */
public class CodeStyleAnalysisEventFile extends CodeStyleAnalysisEventPaired
    {

    /*
     * constructors
     */

    
    /**
     * populate the instance
     *
     * @param auditEvent
     *     raw audit event
     * @param tag
     *     indicates that this event marks the beginning or end of this file
     */
    public CodeStyleAnalysisEventFile( final AuditEvent auditEvent,
                                       final StartOrFinish tag )
        {

        super( auditEvent, tag ) ;

        }   // end constructor
    
    
    /*
     * getters
     */
    
    
    /**
     * retrieve the name of the file being checked
     * 
     * @return the file name
     */
    public String getFilename()
        {
        
        return super.event.getFileName() ;
        
        }   // end getFilename()
    
    
    /*
     * public API methods
     */
    
    
    @Override
    public String toString()
        {

        return String.format( "%s %s %s at %s",
                              super.startingOrFinished.toString(),
                              "file",
                              getFilename(),
                              super.getTimestampAsString() ) ;

        }   // end toString()

    }   // end class CodeStyleAnalysisEventFile
