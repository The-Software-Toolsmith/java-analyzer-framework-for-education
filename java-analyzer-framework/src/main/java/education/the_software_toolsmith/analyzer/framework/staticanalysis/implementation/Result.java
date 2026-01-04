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



package education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation;

import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.* ;

import java.nio.file.Path ;
import java.util.HashMap ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Map ;

/**
 * result of analyzing source code for compliance with a set of implementation rules
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-14 Initial implementation based on code from ChatGPT 5.2
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class Result
    {
    
    // for 'callback'
    public final ImplementationComplianceChecker checker ;

    public final Map<SourceCategory, Path> sourceCodePaths ;
    public final Map<SourceCategory, List<String>> sourceCode ;
    public final Map<SourceCategory, Map<MethodKey, MethodInfo>> methods ; 
    
    // map method signatures to method keys
    public final Map<SourceCategory, Map<String, MethodKey>> methodKeys ;

    public final Map<Action, List<MethodKey>> expectedChanges ;
    public final Map<Action, List<MethodKey>> actualChanges ;

    // method signatures
    public final List<String> passedInspection ;
    public final List<String> failedInspection ;
    public final List<String> notInspected ;

    public int testCount ;
    public int testsPassed ;

    public StringBuilder report ;


    public Result( final ImplementationComplianceChecker complianceChecker )
        {
        
        this.checker = complianceChecker ;

        this.sourceCodePaths = new HashMap<>() ;
        this.sourceCode = new HashMap<>() ;
        this.methods = new HashMap<>() ;
        this.methodKeys = new HashMap<>() ;

        
        this.expectedChanges = new HashMap<>() ;

        this.expectedChanges.put( ADD, new LinkedList<>() ) ;
        this.expectedChanges.put( MODIFY, new LinkedList<>() ) ;
        this.expectedChanges.put( DELETE, new LinkedList<>() ) ;
        this.expectedChanges.put( LEAVE_AS_IS, new LinkedList<>() ) ;

        
        this.actualChanges = new HashMap<>() ;

        this.actualChanges.put( ADD, new LinkedList<>() ) ;
        this.actualChanges.put( MODIFY, new LinkedList<>() ) ;
        this.actualChanges.put( DELETE, new LinkedList<>() ) ;
        this.actualChanges.put( LEAVE_AS_IS, new LinkedList<>() ) ;
        
        this.passedInspection = new LinkedList<>() ;
        this.failedInspection = new LinkedList<>() ;
        this.notInspected = new LinkedList<>() ;

        this.testCount = 0 ;
        this.testsPassed = 0 ;

        this.report = new StringBuilder() ;

        }   // end constructor


    @Override
    public String toString()
        {

        final StringBuilder methodsThatPassed = new StringBuilder() ;

        for ( final String methodSignature : this.passedInspection )
            {
            methodsThatPassed.append( String.format( "\t%s%n", methodSignature ) ) ;
            }

        final StringBuilder methodsThatFailed = new StringBuilder() ;

        for ( final String methodSignature : this.failedInspection )
            {
            methodsThatFailed.append( String.format( "\t%s%n", methodSignature ) ) ;
            }

        return String.format( """
                              Implementation Compliance Assessment Summary:

                              %s
                              Summary:

                              Passed:

                              %s

                              Failed:

                              %s

                              Passed %s implementation compliance test%s of %,d for %s

                              ==========

                              Note that this automated testing provides a reasonable assessment but
                              it might not be complete nor is its accuracy or completeness guaranteed

                              ==========

                              End of implementation compliance tests.
                              """,
                              this.report,
                              this.passedInspection.size() == 0
                                      ? "	none"
                                      : methodsThatPassed.toString(),
                              this.failedInspection.size() == 0
                                      ? "	none"
                                      : methodsThatFailed.toString(),
                              this.testsPassed == 0
                                      ? "no"
                                      : String.format( "%,d", this.testsPassed ),
                              this.testsPassed == 1
                                      ? ""
                                      : "s",
                              this.testCount,
                              this.checker.classToAssess ) ;

        }   // end toString()

    }   // end class Result
