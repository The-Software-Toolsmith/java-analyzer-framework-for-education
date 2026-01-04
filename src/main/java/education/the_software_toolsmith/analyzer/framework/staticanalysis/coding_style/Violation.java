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


package education.the_software_toolsmith.analyzer.framework.staticanalysis.coding_style ;

import com.puppycrawl.tools.checkstyle.api.AuditEvent ;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel ;

/**
 * representation of a checkstyle rule violation
 */
public class Violation
    {

    /** name of the file where the violation was detected */
    public final String file ;

    /** line number where the violation was detected */
    public final int line ;

    /** description of the violation */
    public final String message ;

    /** severity of the violation */
    public final SeverityLevel severity ;

    /** raw audit event */
    final AuditEvent event ;


    /**
     * populate the instance
     *
     * @param inFile
     *     name of the file where the violation was detected
     * @param onLine
     *     line number where the violation was detected
     * @param descriptiveMessage
     *     description of the violation
     * @param violationSeverity
     *     severity of the violation
     * @param auditEvent
     *     raw audit event
     */
    public Violation( final String inFile,
                      final int onLine,
                      final String descriptiveMessage,
                      final SeverityLevel violationSeverity,
                      final AuditEvent auditEvent )
        {

        this.file = inFile ;
        this.line = onLine ;
        this.message = descriptiveMessage ;
        this.severity = violationSeverity ;
        this.event = auditEvent ;

        }   // end constructor


    @Override
    public String toString()
        {

        return String.format( "%s:%,5d  %-7s  %s", this.file, this.line, this.severity, this.message ) ;

        }   // end toString()

    }   // end class Violation
