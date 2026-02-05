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

import static education.the_software_toolsmith.analyzer.framework.static_analysis.style.CodeStyleAnalysisEventPaired.StartOrFinish.* ;

import com.puppycrawl.tools.checkstyle.api.AuditEvent ;
import com.puppycrawl.tools.checkstyle.api.TokenTypes ;
import com.puppycrawl.tools.checkstyle.api.Violation ;

/**
 * representation of a checkstyle file audit start/end
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2026-01-11 extracted from base class
 */
public class CodeStyleAnalysisEventError extends CodeStyleAnalysisEventFile
    {

    /*
     * constructors
     */

    /**
     * populate the instance
     *
     * @param auditEvent
     *     raw audit event
     */
    public CodeStyleAnalysisEventError( final AuditEvent auditEvent )
        {

        super( auditEvent, NOT_PAIRED ) ;

        }   // end constructor
    
    
    /*
     * getters
     */
    

    /**
     * retrieve the column character index where the error was detected
     * 
     * @return the column character index (0-based index) or 0 if not associated with a particular column or
     *     if the error isn't associated with a particular line
     */
    public int getColumnCharacterIndex()
        {

        return getViolation().getColumnCharIndex() ;

        }   // end getColumnCharacterIndex()


    /**
     * retrieve the column number where the error was detected
     * 
     * @return the column number (1-based including tab expansion) or 0 if not associated with a particular
     *     column or if the error isn't associated with a particular line
     */
    public int getColumnNumber()
        {

        return super.event.getColumn() ;

        }   // end getColumnNumber()

    
    /**
     * retrieve the key for the error
     * 
     * @return the key
     */
    public String getKey()
        {

        return getViolation().getKey() ;

        }   // end getKey()

    
    /**
     * retrieve the name of the module containing the source of the error
     * 
     * @return the name of the module
     */
    public String getModuleId()
        {

        return super.event.getModuleId() ;

        }   // end getModuleId()

    
    /**
     * retrieve the line number where the error was detected
     * 
     * @return the line number or 0 if not associated with a particular line
     */
    public int getLineNumber()
        {

        return super.event.getLine() ;

        }   // end getSeverity()

    
    /**
     * retrieve the name of the source containing the error
     * 
     * @return the name of the source
     */
    public String getSourceName()
        {

        return super.event.getSourceName() ;

        }   // end getSourceName()

    
    /**
     * retrieve the numeric token type constant of this error - See {@link TokenTypes}
     * 
     * @return the token type
     */
    public int getTokenType()
        {

        return getViolation().getTokenType() ;

        }   // end getTokenType()
    
    
    /**
     * retrieve the violation related to this error
     * 
     * @return the violation object
     */
    public Violation getViolation()
        {

        return super.event.getViolation() ;

        }   // end getViolation()
    

    /*
     * public API methods
     */


    @Override
    public String toString()
        {
        final String formatNothing = "" ;
        final String formatLineOnly = " (%2$,d)" ;
        final String formatLineAndColumn = " (%2$,d @ %3$,d)" ;
        
        String useFormat ;
        
        if ( getLineNumber() == 0 )
            {
            useFormat = formatNothing  ;
            }
        else if ( getColumnNumber() == 0 )
                {
                useFormat = formatLineOnly ; 
                }
            else
                {
                useFormat = formatLineAndColumn ; 
                }

        return String.format( "%1$s" + useFormat + ": severity level: %4$s; key: %5$s %6$s",
                              super.event.getFileName(),
                              getLineNumber(),
                              getColumnNumber(),
                              getSeverity(),
                              getKey(),
                              super.getMessage() ) ;

        }   // end toString()

    }   // end class CodeStyleAnalysisEventFile
