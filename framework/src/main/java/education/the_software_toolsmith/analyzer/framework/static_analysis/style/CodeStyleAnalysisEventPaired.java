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
public abstract class CodeStyleAnalysisEventPaired extends CodeStyleAnalysisEvent
    {
    
    /*
     * utility types
     */
    
    
    /**
     * so we can distinguish between the starting and ending entries for paired events (audit, file)
     */
    public static enum StartOrFinish
        {

         /** starting entries for... */
         START( "starting" )

         /** finished entries for... */
         , FINISH( "finished" )
         
         /** for non-paired events */
         , NOT_PAIRED( "" )

        ;
        

        /*
         * data fields
         */


        /** text for display */
        private final String prettyDescription ;


        /*
         * constructors
         */


        /**
         * save the 'pretty' description
         * @param prettyText the pretty text
         */
        private StartOrFinish( final String prettyText )
            {

            this.prettyDescription = prettyText ;

            }   // end constructor
        
        
        /*
         * public API methods
         */
        
        @Override
        public String toString()
            {
        
            return this.prettyDescription ;
        
            }   // end toString()

        }   // end inner enum StartOrFinish

    
    /*
     * data fields
     */

    
    /** for paired events, tag as beginning or end */
    protected final StartOrFinish startingOrFinished ;


    /*
     * constructors
     */


    /**
     * populate the instance
     *
     * @param auditEvent
     *     raw audit event
     * @param tag
     *     indicates that this event marks the beginning or end of this paired event
     */
    public CodeStyleAnalysisEventPaired( final AuditEvent auditEvent,
                                         final StartOrFinish tag )
        {

        super( auditEvent ) ;
      
        this.startingOrFinished = tag ;

        }   // end constructor


    /*
     * getters
     */
    
    
    /**
     * retrieve the starting or finished tag
     * 
     * @return for paired events, the tag indicating starting or finished; for non-paired events, the tag indicating such
     */
    public StartOrFinish getStartingOrFinished()
        {
        
        return this.startingOrFinished ;
        
        }   // end getStartingOrFinished()

    }   // end class CodeStyleAnalysisEvent
