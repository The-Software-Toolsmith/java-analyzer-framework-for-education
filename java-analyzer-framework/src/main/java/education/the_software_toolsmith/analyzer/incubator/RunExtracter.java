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


package education.the_software_toolsmith.analyzer.incubator ;

import static education.the_software_toolsmith.analyzer.framework.utilities.SharedState.* ;
/**
 * temporary front-end for BatchRunner to get around command line parsing issues
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-16 Initial implementation
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class RunExtracter
    {

    public static void main( final String[] args ) throws Exception
        {

        final String[] argsForBR
                =
                { "C:\\temp\\WIT\\grading ~ 2025-3fa (2)\\consolidated\\to-grade", className + ".java",
                  "C:\\temp\\WIT\\grading ~ 2025-3fa (2)\\consolidated\\graded",
                  "C:\\OneDrive\\OneDrive - Wentworth Institute of Technology\\code\\WIT\\2025\\3fa\\grading-2\\autograder ~ all ~ 2025-12-16 Tu 1529\\" } ;
        Extracter.main( argsForBR ) ;

        }    // end main()

    }   // end class RunBatchRunner
