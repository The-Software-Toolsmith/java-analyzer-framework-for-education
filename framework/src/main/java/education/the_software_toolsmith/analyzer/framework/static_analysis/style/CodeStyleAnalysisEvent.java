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
import com.puppycrawl.tools.checkstyle.api.SeverityLevel ;

import java.util.Date ;

/**
 * base class for representation of a checkstyle event
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-27 Initial implementation based on code from ChatGPT 5.2
 * @version 2.0 2026-01-11
 *     <ul>
 *     <li>rename from {@code Violation} to {@code CodeStyleAnalysisEvent}
 *     <li>revise to be the base class for all checkstyle events
 *     </ul>
 */
public abstract class CodeStyleAnalysisEvent
    {

    /*
     * data fields
     */

    /** raw audit event */
    protected final AuditEvent event ;

    /** timestamp */
    protected final Date timestamp ;


    /*
     * constructors
     */


    /**
     * populate the instance
     *
     * @param auditEvent
     *     raw audit event
     */
    public CodeStyleAnalysisEvent( final AuditEvent auditEvent )
        {

        this.event = auditEvent ;

        this.timestamp = new Date() ;

        }   // end constructor


    /*
     * getters
     */


    /**
     * retrieve the raw event
     *
     * @return the event
     */
    public AuditEvent getEvent()
        {

        return this.event ;

        }   // end getEvent()


    /**
     * retrieve the message for this event
     *
     * @return the message
     */
    public String getMessage()
        {

        return ( this.event.getViolation() == null )
                ? this.event.getSeverityLevel().toString()
                : this.event.getMessage() ;

        }   // end getMessage()


    /**
     * retrieve the severity associated with this event
     *
     * @return the severity level
     */
    public SeverityLevel getSeverity()
        {

        return this.event.getSeverityLevel() ;

        }   // end getSeverity()


    /**
     * retrieve the timestamp when this event occurred
     *
     * @return the timestamp
     */
    public Date getTimestamp()
        {

        return this.timestamp ;

        }   // end getTimestamp()


    /**
     * retrieve the timestamp when this event occurred as text
     *
     * @return the timestamp (Ddd Mmm d, yyyy h:mm:ss.mmm a/p)
     */
    public String getTimestampAsString()
        {

        return getTimestampAsString( "%1$ta %1$tb %1$td, %1$tY %1$tl:%1$tM:%1$tS.%1$tL %1$tp" ) ;

        }   // end getTimestampAsString()


    /**
     * retrieve the timestamp when this event occurred as text
     * 
     * @param format
     *     format specification to {@code String.format()} for the text version of the timestamp; all
     *     specifications should refer to the first (only) argument (%1$...)
     *
     * @return the timestamp according to the provided format
     */
    public String getTimestampAsString( final String format )
        {

        return String.format( format, this.timestamp ) ;

        }   // end getTimestampAsString()


    /*
     * public API methods
     */


    @Override
    public String toString()
        {

        return String.format( "%s",
                              ( this.event.getViolation() == null )
                                      ? this.event.getSeverityLevel()
                                      : this.event.getMessage() ) ;

        }   // end toString()

    }   // end class CodeStyleAnalysisEvent
