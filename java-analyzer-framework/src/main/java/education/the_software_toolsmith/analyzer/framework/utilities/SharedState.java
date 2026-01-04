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


package education.the_software_toolsmith.analyzer.framework.utilities ;

/**
 * HACK to provide a source of 'stuff' for compliance tests
 * <p>don't do this for real<p>this is a lousy stop-gap implementation
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-17 Initial implementation (embarrassing)
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public abstract class SharedState
    {

    /*
     * constants
     */
    // initial values are null to cause NPE if not explicitly set
    
    /** name without file type */
    public static String className = null ;

    /** type and optionally category (adt|app) */
    public static String ADTPathSegment = null ;

    /** type and optionally category (adt|app) */
    public static String ADTPackageSegment = null ;

    /**
     * typically {lab|proj|project}-##[-#]<br>
     * where ## is a 2-digit assignment number with leading 0-fill<br>
     * and -# is an optional deliverable identifier
     */
    public static String longAssignmentId = null ;

    /** typically {l|p}-##[-#] where ## is a 2-digit assignment number with leading 0-fill<br>
     * where ## is a 2-digit assignment number with leading 0-fill<br>
     * and -# is an optional deliverable identifier */
    public static String shortAssignmentId = null ;

    static
        {
        doSetStrings() ;
        }
    
    public static void doSetStrings()
        {
        // @formatter:off
        setStrings( "LinkedBag", "bags", "bags", "lab-01", "l1" ) ;
//        setStrings( "LinkedStack", "stacks/adt", "stacks.adt", "lab-02", "l2" ) ;
//        setStrings( "InfixExpressionEvaluator", "stacks/app", "stacks.app", "lab-02", "l2" ) ;
//        setStrings( "ArrayQueue", "queues", "queues", "lab-03-2", "l3-2" ) ;
//        setStrings( "LList", "lists", "lists", "lab-04", "l4" ) ;
        // @formatter:on
        }


    public static void setStrings( String classNameText,
                                   String ADTPathSegmentText,
                                   String ADTPackageSegmentText,
                                   String longAssignmentIdText,
                                   String shortAssignmentIdText )
        {

        className = classNameText ;
        ADTPathSegment = ADTPathSegmentText ;
        ADTPackageSegment = ADTPackageSegmentText ;
        longAssignmentId = longAssignmentIdText ;
        shortAssignmentId = shortAssignmentIdText ;

//        System.out.printf( "setStrings():%n%s%n", output() ) ;
        
        }   // end setStrings()

    public static String output()
        {
        
        return String.format( """
                              =====================
                              %20s: %s
                              %20s: %s
                              %20s: %s
                              %20s: %s
                              %20s: %s
                              =====================
                              """,
                              "className", className,
                              "ADTPathSegment", ADTPathSegment,
                              "ADTPackageSegment", ADTPackageSegment,
                              "longAssignmentId", longAssignmentId,
                              "shortAssignmentId", shortAssignmentId ) ;
        
        }   // end toString()
    
    }   // end class SharedState
