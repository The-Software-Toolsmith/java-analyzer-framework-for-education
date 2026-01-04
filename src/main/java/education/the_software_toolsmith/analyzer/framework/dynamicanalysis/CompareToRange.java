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


package education.the_software_toolsmith.analyzer.framework.dynamicanalysis ;

/**
 * Support {@code compareTo()} tests
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2021-06-20 Initial implementation - extracted from {@code TimeOfDayDMRTests.java}
 * @version 1.1 2023-10-19 rewrite {@code opposite()} to use {@code switch} expression instead of
 *     nested ternary conditional expression
 */
public enum CompareToRange
    {

    /** represents any value less than zero */
    NEGATIVE ( "<0" )

    ,
    /** represents the value zero */
    ZERO ( "0" )

    ,
    /** represents any value greater than zero */
    POSITIVE ( ">0" )

    ,
    /** represents any non-numeric value */
    NOT_APPLICABLE ( "n/a" );


    /** text to display for this range */
    private final String descriptiveText ;


    /**
     * configure the instance
     *
     * @param description
     *     text to display for this range
     *
     * @since 1.0
     */
    private CompareToRange( final String description )
        {

        this.descriptiveText = description ;

        }   // end constructor


    /**
     * Returns the opposite condition
     *
     * @return the opposite enum instance
     *
     * @since 1.0
     */
    public CompareToRange opposite()
        {

        return switch ( this )
            {
            case ZERO
                -> ZERO ;
            case NEGATIVE
                -> POSITIVE ;
            case POSITIVE
                -> NEGATIVE ;
            default
                -> NOT_APPLICABLE ;
            } ;

        }   // end opposite()


    @Override
    public String toString()
        {

        return this.descriptiveText ;

        }   // end toString()

    }   // end enum CompareToRange