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

import com.puppycrawl.tools.checkstyle.api.SeverityLevel ;
import com.puppycrawl.tools.checkstyle.api.Violation ;

import java.util.Arrays ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Map ;
import java.util.TreeMap ;

/**
 * represents the result of running a checkstyle analysis
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-27 Initial implementation based on code from ChatGPT 5.2
 * @version 2.0 2026-01-10
 *     <ul>
 *     <li>track changes to other classes
 *     <li>rename from {@code Result} to {@code CodeStyleAnalysisResult} to distinguish it from other
 *     similarly named classes
 *     </ul>
 */
public class CodeStyleAnalysisResult
    {

    /**
     * flag where {@code true} indicates passed all compliance checks, or {@code false} otherwise
     */
    public boolean isCompliant ;

    /** the list of violations (if any) - should start and end with file-level events */
    public final List<CodeStyleAnalysisEvent> codeStyleAnalysisEvents ;

    /** counts of each violation severity level */
    public final int[] violationSeverityLevelCounters ;
    
    /** all violations organized by key */
    public final Map<String, List<Violation>> keyedViolations ;

    /** full text description of the analysis */
    public final StringBuilder report ;
    
    /** brief description of analysis */
    public final StringBuilder summary ;
    
    /** base directory - enables shortening file names in violation display */
    public String baseDirectory = null ;


    /**
     * set initial state to no events
     */
    public CodeStyleAnalysisResult()
        {

        this.isCompliant = true ;
        this.codeStyleAnalysisEvents = new LinkedList<>() ;
        this.violationSeverityLevelCounters = new int[ SeverityLevel.values().length ] ;    // all -> 0
        
        this.keyedViolations = new TreeMap<>() ;
        
        this.report = new StringBuilder() ;
        this.summary = new StringBuilder() ;

        }   // end constructor


    @Override
    public String toString()
        {

        return this.report.toString() ;
///* DEBUG */ return resultsToText() ;

        }   // end toString()
    

    /**
     * debugging aid: dumps audit event to text
     * 
     * @return a printable version of this for debugging
     */
    public String resultsToText()
        {

        return String.format( """
                              \t%s:
                              \tbase directory: %s
                              \tevents: %s
                              \tis compliant: %b
                              \treport: %s
                              \tsummary: %s
                              \tcounters: %s
                              """,
                              this.getClass().getSimpleName(),
                              this.baseDirectory,
                              this.codeStyleAnalysisEvents,
                              this.isCompliant,
                              this.report,
                              this.summary,
                              Arrays.toString( this.violationSeverityLevelCounters ) ) ;

        }   // end eventToText()

    }   // end class CodeStyleAnalysisResult
