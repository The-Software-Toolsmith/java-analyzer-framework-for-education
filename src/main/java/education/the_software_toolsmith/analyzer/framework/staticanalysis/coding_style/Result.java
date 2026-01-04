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

import com.puppycrawl.tools.checkstyle.api.SeverityLevel ;

import java.nio.file.Path ;
import java.util.LinkedList ;
import java.util.List ;

/**
 * represents the result of checkstyle-ing a single source file
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-27 Initial implementation based on code from ChatGPT 5.2
 */
public class Result
    {

    /**
     * flag where {@code true} indicates passed all compliance checks, or {@code false} otherwise
     */
    public boolean isCompliant ;

    /** the list of violations (if any) */
    public final List<Violation> violations ;

    /** counts of each violation severity level */
    public final int[] violationCounters ;

    /** text version of the violations */
    public final StringBuilder report ;
    
    /** brief description of analysis */
    public final StringBuilder summary ;
    
    /** base directory - enables shortening file names in violation display */
    public String baseDirectory = null ;


    /**
     * set initial state to no violations
     */
    public Result()
        {

        this.isCompliant = false ;
        this.violations = new LinkedList<>() ;
        this.violationCounters = new int[ SeverityLevel.values().length ] ;
        this.report = new StringBuilder() ;
        this.summary = new StringBuilder() ;

        }   // end constructor


    @Override
    public String toString()
        {

        return this.report.toString() ;

        }   // end toString()

    }   // end class Result
