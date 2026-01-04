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


package education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance ;


/**
 * represents compliance check status
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-14 Initial implementation
 */
public enum Compliance
    {
    // @formatter:off

    /** compliance checking status is not known */
    UNKNOWN

    , /** compliance was not checked */
    NOT_CHECKED

    , /** compliance check(s) completed successfully */
    PASSED

    , /** compliance check(s) completed unsuccessfully */
    FAILED

    , /** compliance checks are not applicable */
    NOT_APPLICABLE ;

    // @formatter:on


    @Override
    public String toString()
        {

        return name().toLowerCase().replace( '_', ' ' ) ;

        }   // end toString()


    /**
     * testing drive
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        for ( final Compliance type : Compliance.values() )
            {
            System.out.printf( "%s -> %s%n", type.name(), type.toString() ) ;
            }

        }    // end main()

    }   // end class Compliance
