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


package education.the_software_toolsmith.analyzer.framework.static_analysis.structure;

/**
 * symbolic keys into source maps
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-14 Initial implementation
 * @version 1.1 2026-01-10
 *     <ul>
 *     <li>add 'pretty' description
 *     <li>rename from {@code SourceCategory} to {@code CodeSourceCategory} to better reflect its purpose
 *     </ul>
 */
public enum CodeSourceCategory
    {

    // @formatter:off

    /** original/provided code */
    STARTER( "starter" )
    
    , /** expected/required resulting code */
    SOLUTION( "solution" )
    
    , /** source code which will be checked for compliance */
    TO_EVALUATE( "to evaluate" )
    
    ;

    // @formatter:on

    
    /*
     * data fields
     */
    
    /** 'pretty' description */
    public final String prettyDescription ;
    
    
    /*
     * constructors
     */
    
    
    /**
     * store the additional information about this state
     *
     * @param thePrettyDescription text for display
     */
    private CodeSourceCategory( final String thePrettyDescription )
        {
        
        this.prettyDescription = thePrettyDescription ;
        
        }   // end constructor
    
    
    /*
     * API methods
     */

    @Override
    public String toString()
        {

        return this.prettyDescription ;

        }   // end toString()

    
    /*
     * testing and debugging
     */
    

    /**
     * testing drive
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        for ( final CodeSourceCategory category : CodeSourceCategory.values() )
            {
            System.out.printf( "%s -> %s%n", category.name(), category.toString() ) ;
            }

        }    // end main()

    }   // end enum CodeSourceCategory
