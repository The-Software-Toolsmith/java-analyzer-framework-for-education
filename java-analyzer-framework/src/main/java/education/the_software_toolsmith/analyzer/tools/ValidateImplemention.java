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


package education.the_software_toolsmith.analyzer.tools ;

import education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Configuration ;
import education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.ImplementationComplianceChecker ;
import education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Requirement ;
import education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.TargetType ;

import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.* ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.* ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Requirement.* ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.TargetType.* ;
import static education.the_software_toolsmith.analyzer.framework.utilities.SharedState.className ;
import static education.the_software_toolsmith.analyzer.framework.utilities.SharedState.longAssignmentId ;


/**
 * check student's implementation of an adt or application for compliance with specifications
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-07 Initial implementation
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt
 * @version 3.0 2025-12-23 remove all validator functionality to new class ImplementationComplianceChecker
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class ValidateImplemention
    {

    /**
     * test driver
     *
     * @param args
     *     -unused-
     *
     * @throws Exception
     */
    public static void main( final String[] args ) throws Exception
        {

        final String classToAssess = className + ".java" ;
        final String baseSearchLocation = "./to-grade/" + longAssignmentId + "/" ;

        final ImplementationComplianceChecker checker
                = new ImplementationComplianceChecker( classToAssess, baseSearchLocation ) ;

        // configure JavaParser and symbol solver to find student's code
        Configuration.configureSolver( checker.studentWorkPath ) ;

        configureImplementationValidation( checker ) ;

        checker.analyze() ;

        System.out.printf( "%n%s%n", checker.result ) ;

        }   // end main()


    public static void configureImplementationValidation( final ImplementationComplianceChecker checker )
        {

        /*
         * requirements
         */

        final Requirement mustCallGetData = new Requirement( DO_CALL, METHOD, "getData()" ) ;
        final Requirement mustCallGetNext = new Requirement( DO_CALL, METHOD, "getNext()" ) ;

        final Requirement mustCallAddArrayOfT = new Requirement( DO_CALL, METHOD, "add(T[])" ) ;
        final Requirement mustNotCallAddArrayOfT = new Requirement( DO_NOT_CALL, METHOD, "add(T[])" ) ;

        final Requirement mustCallAddT = new Requirement( DO_CALL, METHOD, "add(T)" ) ;
        final Requirement mustNotCallAddT = new Requirement( DO_NOT_CALL, METHOD, "add(T)" ) ;
        final Requirement mustCallRemove = new Requirement( DO_CALL, METHOD, "remove(T)" ) ;

        final Requirement couldCallContains = new Requirement( OPTIONALLY_CALL, METHOD, "contains(T)" ) ;

        final Requirement mustCallToArray = new Requirement( DO_CALL, METHOD, "toArray()" ) ;
        final Requirement mustNotCallToArray = new Requirement( DO_NOT_CALL, METHOD, "toArray()" ) ;

        final Requirement mustCallNoArgConstructor = new Requirement( DO_CALL, CONSTRUCTOR, "LinkedBag()" ) ;

        final Requirement mustInstantiateNoArgConstructor
                = new Requirement( DO_INSTANTIATE_USING, CONSTRUCTOR, "LinkedBag()" ) ;
        final Requirement mustInstantiateCloningConstructor
                = new Requirement( DO_INSTANTIATE_USING, CONSTRUCTOR, "LinkedBag(BagInterface)" ) ;

        
        /*
         * global applicability
         */
        
        // no scope specified -> everywhere/anywhere
        
        // DMR IN_PROCESS TODO enhance the checker to support (1) annotations and (2) 'global' requirements
//        final Requirement mustNotUseSuppressWarnings
//                = new Requirement( DO_NOT_USE, ANNOTATION, "SuppressWarnings" ) ;
//        
//        final Requirement mustUseAuthorTags = new Requirement( DO_USE, ANNOTATION, "author" ) ;
//        mustUseAuthorTags.getArguments().put( "scope", "top level javadoc" ) ;
//        mustUseAuthorTags.getArguments().put( "minimum occurrences", 2 ) ;
//
//        final Requirement mustUseVersionTags = new Requirement( DO_USE, ANNOTATION, "version" ) ;
//        mustUseVersionTags.getArguments().put( "scope", "top level javadoc" ) ;
//        mustUseVersionTags.getArguments().put( "minimum_occurrences", 2 ) ;
//        
//        checker.setGlobalRequirements( "*", mustNotUseSuppressWarnings ) ;
//        checker.setGlobalRequirements( "*", mustUseAuthorTags ) ;
//        checker.setGlobalRequirements( "*", mustUseVersionTags ) ;
        
        /*
         * specific modifications
         */

        checker.setMethodRequirements( "LinkedBag(BagInterface)",
                                       mustCallNoArgConstructor,
                                       mustCallGetData,
                                       mustCallGetNext,
                                       mustCallAddT,
                                       mustCallToArray,
                                       mustCallAddArrayOfT ) ;
        checker.setMethodRequirements( "LinkedBag(T[])",
                                       mustCallNoArgConstructor,
                                       mustCallAddArrayOfT,
                                       mustNotCallAddT ) ;
        checker.setMethodRequirements( "difference(BagInterface)",
                                       mustInstantiateCloningConstructor,
                                       mustCallGetData,
                                       mustCallGetNext,
                                       mustCallRemove,
                                       mustCallToArray ) ;
        checker.setMethodRequirements( "intersection(BagInterface)",
                                       mustInstantiateNoArgConstructor,
                                       mustInstantiateCloningConstructor,
                                       mustCallGetData,
                                       couldCallContains,
                                       mustCallRemove,
                                       mustCallAddT,
                                       mustCallGetNext ) ;
        checker.setMethodRequirements( "union(BagInterface)",
                                       mustInstantiateCloningConstructor,
                                       mustCallGetNext,
                                       mustCallGetData,
                                       mustCallAddT,
                                       mustNotCallToArray,
                                       mustNotCallAddArrayOfT ) ;
        checker.setMethodRequirements( "add(T[])", mustCallAddT ) ;
        
        


        /*
         * no changes
         */

        checker.setMethodRequirements( "LinkedBag()", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "add(T)", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "clear()", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "contains(T)", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "getCurrentSize()", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "getFrequencyOf(T)", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "isEmpty()", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "remove()", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "remove(T)", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "toArray()", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "toString()", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "getReferenceTo(T)", DO_LEAVE_EVERYTHING_AS_IS ) ;
        checker.setMethodRequirements( "initializeState()", DO_LEAVE_EVERYTHING_AS_IS ) ;
        
        
        /*
         * optional changes
         */
        
        checker.setMethodRequirements( "main(String[])", OPTIONALLY_MODIFY_METHOD, DO_NOT_DELETE_METHOD ) ;
        checker.setMethodRequirements( "printStuff(BagInterface, BagInterface)",
                                       OPTIONALLY_MODIFY_METHOD,
                                       OPTIONALLY_DELETE_METHOD ) ;

        }    // end configureImplementationValidation()

    }   // end class ValidateImplemention
