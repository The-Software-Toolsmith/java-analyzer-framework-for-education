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

import static education.the_software_toolsmith.analyzer.framework.static_analysis.style.CodeStyleAnalysisEventPaired.StartOrFinish.NOT_PAIRED ;

import com.puppycrawl.tools.checkstyle.api.AuditEvent ;

/**
 * representation of a checkstyle audit exception
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2026-01-11 extracted from base class
 */
public class CodeStyleAnalysisEventException extends CodeStyleAnalysisEventFile
    {
    
    /*
     * data fields
     */
    
    /** the exception (or other throwable) which occurred */
    private final Throwable thrown ;
    

    /*
     * constructors
     */

    
    /**
     * populate the instance
     *
     * @param auditEvent
     *     raw audit event
     * @param thrownObject
     *     the throwable object accompanying this failure
     */
    public CodeStyleAnalysisEventException( final AuditEvent auditEvent, final Throwable thrownObject )
        {
        
        super( auditEvent, NOT_PAIRED ) ;

        this.thrown = thrownObject ;

        }   // end constructor
    
    
    /*
     * getters
     */
    

    /**
     * retrieve the throwable instance
     * 
     * @return the instance of {@code Throwable}
     */
    public Throwable getException()
        {

        return this.thrown ;

        }   // end getException()
    

    /*
     * public API methods
     */


    @Override
    public String toString()
        {

        return String.format( "%s: %s", super.toString(), this.thrown.getMessage() ) ;

        }   // end toString()

    }   // end class CodeStyleAnalysisEventException
