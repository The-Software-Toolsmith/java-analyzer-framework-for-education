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


package education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation ;


/**
 * represents the rule enforcement policy
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-14 Initial implementation
 * @version 2.0 2025-12-26 renamed from {@code RequirementType} to better reflect its purpose
 *
 */
public enum RequirementType
    {

    // @formatter:off
    
    /** (simple) action must occur */
     MUST
     , /** (simple) action must not occur */
     MUST_NOT
     
     , /** similar to MUST but implies impact beyond it (complex) */ 
     REQUIRES
     , /** similar to MUST_NOT but implies impact beyond it (complex) */
     PROHIBITS
     
     , /** ok for action to occur but not required */
     OPTIONALLY
     
     , /** intended to be an escape mechanism for extensions */
     CUSTOM ;

    // @formatter:on
    

    @Override
    public String toString()
        {

        return this.name().toLowerCase().replace( '_', ' ' ) ;

        }   // end toString()


    /**
     * testing driver
     *
     * @param args
     *     -unused-
     */
    public static void main( String[] args )
        {

        for ( RequirementType type : RequirementType.values() )
            {
            System.out.printf( "%s -> %s%n", type.name(), type.toString() ) ;
            }

        }    // end main()

    }   // end enum RequirementType
