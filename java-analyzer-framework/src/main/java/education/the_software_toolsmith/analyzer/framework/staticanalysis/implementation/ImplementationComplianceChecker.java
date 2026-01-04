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

import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.TestingBase.* ;

import education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.ComplianceException ;
import education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.InvalidRequirementException ;
import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;

import static education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.Compliance.FAILED ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.Compliance.NOT_CHECKED ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.Compliance.PASSED ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.* ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.* ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Heuristics.* ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.OriginalSource.getOriginalMethodSource ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.RequirementType.* ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.SourceCategory.STARTER ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.SourceCategory.STUDENT_WORK ;

import com.github.javaparser.StaticJavaParser ;
import com.github.javaparser.ast.CompilationUnit ;
import com.github.javaparser.ast.body.CallableDeclaration ;
import com.github.javaparser.ast.body.ConstructorDeclaration ;
import com.github.javaparser.ast.body.MethodDeclaration ;

import java.nio.file.Files ;
import java.nio.file.Path ;
import java.util.Arrays ;
import java.util.HashMap ;
import java.util.LinkedHashMap ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Map ;
import java.util.Map.Entry ;
import java.util.Objects ;


/**
 * check student's implementation of an adt or application for compliance with specifications
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-07 Initial implementation
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class ImplementationComplianceChecker extends SharedState
    {

    /*
     * constants
     */


    /** the minimum number of columns for display of a requirement in the analysis portion of the report */
    private static final int REQUIREMENT_RULE_SPACE = 35 ;


    /*
     * data fields
     */

    public final String classToAssess ;
    public final String baseSearchLocation ;

    /** path to the starter project/assignment code */
    public Path starterCodePath ;

    /** path to the student's work */
    public Path studentWorkPath ;

    /** contains summary and detailed information about the results of the compliance tests */
    public Result result ;

    /** convenience reference to the result's report */
    public StringBuilder report ;

    /** all method signatures (text) collected from the student's work */
    public final List<String> studentWorkMethodSignatures ;

    /**
     * all requirements keyed by method signature (text) as specified by the test driver - mapped to a list of
     * requirements
     */
    public final Map<String, List<Requirement>> requirementsForEachMethod ;


    /*
     * constructors
     */


    /**
     * set initial state to hold the paths to the versions of the code we need to validate the implementation
     *
     * @param specifiedClassName
     *     name of the class to validate
     * @param specifiedBaseSearchLocation
     *     starting location for the implementations
     */
    public ImplementationComplianceChecker( final String specifiedClassName,
                                            final String specifiedBaseSearchLocation )
        {

        this( specifiedClassName, specifiedBaseSearchLocation, new LinkedHashMap<>() ) ;

        }   // end constructor


    /**
     * set initial state to hold the paths to the versions of the code we need to validate the implementation
     *
     * @param specifiedClassName
     *     name of the class to validate
     * @param specifiedBaseSearchLocation
     *     starting location for the implementations
     * @param methodRequirements
     *     map of method signature -> list of implementation requirements
     */
    public ImplementationComplianceChecker( final String specifiedClassName,
                                            final String specifiedBaseSearchLocation,
                                            final Map<String, List<Requirement>> methodRequirements )
        {

        this.classToAssess = specifiedClassName ;
        this.baseSearchLocation = specifiedBaseSearchLocation ;


        this.studentWorkPath = null ;
        this.starterCodePath = null ;

        this.studentWorkMethodSignatures = new LinkedList<>() ;

        this.result = new Result( this ) ;
        this.report = this.result.report ;

        findSourceCode() ;

        this.requirementsForEachMethod = methodRequirements ;

        }   // end constructor


    /**
     * analyze student source code for compliance with a set of supplied requirements
     *
     * @return result containing compliance status and a human-readable report
     *
     * @throws Exception
     *     from the file system, JavaParser or symbol solver, or an inconsistency we detect
     */
    public Result analyze() throws Exception
        {

        /*
         * get all code and parse for methods and constructors: starter, student work
         */
        collectCode() ;


        /*
         * determine the work that is expected
         */
        determineExpected() ;


        // handy short mnemonic references
        @SuppressWarnings( "unused" )
        final List<MethodKey> methodsToAdd = this.result.expectedChanges.get( ADD ) ;
        @SuppressWarnings( "unused" )
        final List<MethodKey> methodsToModify = this.result.expectedChanges.get( MODIFY ) ;
        @SuppressWarnings( "unused" )
        final List<MethodKey> methodsToDelete = this.result.expectedChanges.get( DELETE ) ;
        @SuppressWarnings( "unused" )
        final List<MethodKey> methodsToLeaveUnchanged = this.result.expectedChanges.get( LEAVE_AS_IS ) ;


        /*
         * determine the work that was done
         */
        determineActual() ;


        // handy short mnemonic references
        @SuppressWarnings( "unused" )
        final List<MethodKey> methodsAdded = this.result.actualChanges.get( ADD ) ;
        @SuppressWarnings( "unused" )
        final List<MethodKey> methodsModified = this.result.actualChanges.get( MODIFY ) ;
        @SuppressWarnings( "unused" )
        final List<MethodKey> methodsDeleted = this.result.actualChanges.get( DELETE ) ;
        @SuppressWarnings( "unused" )
        final List<MethodKey> methodsUnchanged = this.result.actualChanges.get( LEAVE_AS_IS ) ;


        /*
         * all method implementation compliance requirements
         */

        this.report.append( String.format( "%n==================%n" ) ) ;

        this.report.append( String.format( "%nCompliance Rules:%n" ) ) ;
        
        Requirement.excludeComplianceInToString() ;
        Requirement.excludeIndentInToString() ;

        for ( final String methodSignature : this.studentWorkMethodSignatures )
            {
            this.report.append( String.format( "%n%s%n%n", methodSignature ) ) ;

            final List<Requirement> requirements = this.requirementsForEachMethod.get( methodSignature ) ;

            if ( requirements == null )
                {
                this.report.append( String.format( "\tnone%n" ) ) ;

                continue ;
                }

            for ( final Requirement requirement : requirements )
                {
                this.report.append( String.format( "\t%s%n", requirement ) ) ;
                }

            }
        
        Requirement.includeIndentInToString() ;
        Requirement.includeComplianceInToString() ;

        this.report.append( String.format( "%n==================%n" ) ) ;


        /*
         * do the analysis
         */
        final String format = "%-" + REQUIREMENT_RULE_SPACE + "s:  %s%n" ;


        this.report.append( String.format( "%nAnalyzing:%n" ) ) ;

        for ( final String studentWorkMethodSignature : this.studentWorkMethodSignatures )
            {
            assessMethodCompliance( studentWorkMethodSignature, format ) ;
            }

        // the results of the analysis

        return this.result ;

        }   // end analyze()


    public void assessMethodCompliance( final String studentWorkMethodSignature,
                                        final String format )
        {

        final List<Requirement> requirements = this.requirementsForEachMethod.get( studentWorkMethodSignature ) ;

        int testCount = 0 ;
        int testsPassed = 0 ;

        boolean correctBehavior = true ;


        this.report.append( String.format( "%n%s%n%n", studentWorkMethodSignature ) ) ;

        final MethodKey studentWorkMethodKey
                = this.result.methodKeys.get( STUDENT_WORK ).get( studentWorkMethodSignature ) ;
        MethodInfo studentWorkMethodInfo = null ;
        CallableDeclaration<?> studentMethod = null ;

        if ( studentWorkMethodKey == null )
            {
            /*
             * method was deleted
             */
            
            // DMR TODO make sure state is properly set/updated

            }
        else
            {
            // get the method info and callable object if available
            studentWorkMethodInfo = studentWorkMethodKey.getMethodInfo() ;

            if ( studentWorkMethodInfo != null )
                {
                studentMethod = studentWorkMethodInfo.getCallableDeclaration() ;
                }

            }

        if ( requirements == null )
            {
            /*
             * method was added
             */
            
            this.report.append( String.format( "[no implementation compliance rules available]%n" ) ) ;

            // DMR TODO make sure state is properly set/updated
            
            }
        else
            {

            for ( final Requirement requirement : requirements )
                {
                testCount++ ;

                correctBehavior = switch ( requirement.behavior.action )
                    {
                    case CALL -> checkActionCall( requirement,
                                                  studentWorkMethodKey,
                                                  studentWorkMethodInfo,
                                                  studentMethod ) ;

                    case INSTANTIATE -> checkActionInstantiate( requirement,
                                                                studentWorkMethodKey,
                                                                studentWorkMethodInfo,
                                                                studentMethod ) ;

                    case SET_VALUE -> checkActionSetValue( requirement,
                                                           studentWorkMethodKey,
                                                           studentWorkMethodInfo,
                                                           studentMethod ) ;
                    case GET_VALUE -> checkActionGetValue( requirement,
                                                           studentWorkMethodKey,
                                                           studentWorkMethodInfo,
                                                           studentMethod ) ;

                    case DEFINE -> checkActionDefine( requirement,
                                                      studentWorkMethodKey,
                                                      studentWorkMethodInfo,
                                                      studentMethod ) ;
                    case UNDEFINE -> checkActionUndefine( requirement,
                                                          studentWorkMethodKey,
                                                          studentWorkMethodInfo,
                                                          studentMethod ) ;

                    case ADD -> checkActionAdd( requirement,
                                                studentWorkMethodKey,
                                                studentWorkMethodInfo,
                                                studentMethod ) ;
                    case DELETE -> checkActionDelete( requirement,
                                                      studentWorkMethodKey,
                                                      studentWorkMethodInfo,
                                                      studentMethod ) ;
                    case MODIFY -> checkActionModify( requirement,
                                                      studentWorkMethodKey,
                                                      studentWorkMethodInfo,
                                                      studentMethod ) ;

                    case LEAVE_AS_IS -> checkActionLeaveAsIs( requirement,
                                                              studentWorkMethodKey,
                                                              studentWorkMethodInfo,
                                                              studentMethod ) ;

                    case USE -> checkActionUse( requirement,
                                                studentWorkMethodKey,
                                                studentWorkMethodInfo,
                                                studentMethod ) ;

                    case TRY -> checkActionTry( requirement,
                                                studentWorkMethodKey,
                                                studentWorkMethodInfo,
                                                studentMethod ) ;
                    case TRY_WITH_RESOURCES -> checkActionTryWithResources( requirement,
                                                                            studentWorkMethodKey,
                                                                            studentWorkMethodInfo,
                                                                            studentMethod ) ;
                    case THROW -> checkActionThrow( requirement,
                                                    studentWorkMethodKey,
                                                    studentWorkMethodInfo,
                                                    studentMethod ) ;
                    case CATCH -> checkActionCatch( requirement,
                                                    studentWorkMethodKey,
                                                    studentWorkMethodInfo,
                                                    studentMethod ) ;
                    case FINALLY -> checkActionFinally( requirement,
                                                        studentWorkMethodKey,
                                                        studentWorkMethodInfo,
                                                        studentMethod ) ;

                    default ->
                        {
                        throw new IllegalStateException( String.format( "unexpected Action: %s",
                                                                        requirement.behavior.action ) ) ;
                        }
                    } ; // end switch

                if ( correctBehavior )
                    {
                    testsPassed++ ;

                    requirement.compliance = PASSED ;
                    }
                else
                    {
                    requirement.compliance = FAILED ;
                    }

                logComplianceCheck( this.report, format, requirement.toString(), correctBehavior ) ;
                }   // end for
            }   // end if/else

        
        /*
         * record the results
         */

        if ( testsPassed == testCount )
            {
            // fully compliant
            this.result.passedInspection.add( studentWorkMethodSignature ) ;

            if ( studentWorkMethodInfo != null )
                {
                studentWorkMethodInfo.setCompliance( PASSED ) ;
                }

            }
        else
            {
            // failed one or more compliance checks
            this.result.failedInspection.add( studentWorkMethodSignature ) ;

            if ( studentWorkMethodInfo != null )
                {
                studentWorkMethodInfo.setCompliance( FAILED ) ;
                }

            }

        this.report.append( String.format( "%nPassed %,d of %,d check%s%n%n",
                                           testsPassed,
                                           testCount,
                                           testCount == 1
                                                   ? ""
                                                   : "s" ) ) ;

        this.result.testCount += testCount ;
        this.result.testsPassed += testsPassed ;


        final String studentImplementation
                = getOriginalMethodSource( this.result, studentWorkMethodKey, STUDENT_WORK ) ;
        this.report.append( String.format( "%s%n", studentImplementation ) ) ;

        
        this.report.append( String.format( "==================%n" ) ) ;

        }   // end assessMethodImplementation()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionCall( final Requirement requirement,
                                    final MethodKey methodKey,
                                    final MethodInfo methodInfo,
                                    final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != CALL )
            {
            throw new InvalidRequirementException( String.format( "called with incompatible requirement - wrong action: %s",
                                                                  action ) ) ;
            }

        final boolean callsTheTarget = switch ( targetType )
            {
            case METHOD -> callsMethodWithSignature( studentMethod, target ) ;
            case CONSTRUCTOR -> callsConstructorWithSignature( studentMethod, target ) ;
            default -> throw new ComplianceException( String.format( "unexpected targetType: %s",
                                                                     targetType.getClass()
                                                                               .getSimpleName() ) ) ;
            } ;

        // match the actual behavior with the specified behavior
        return switch ( requirementType )
            {
            case MUST -> callsTheTarget ;
            case MUST_NOT -> ! callsTheTarget ;
            case OPTIONALLY -> true ;
            case REQUIRES,
                 PROHIBITS,
                 CUSTOM
                    -> throw new InvalidRequirementException( String.format( "%s is not an acceptable requirementType in this context",
                                                                             requirementType ) ) ;
            default -> throw new ComplianceException( String.format( "unexpected requirementType: %s",
                                                                     requirementType.getClass()
                                                                                    .getSimpleName() ) ) ;
            } ;

        }   // end checkActionCall()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionInstantiate( final Requirement requirement,
                                           final MethodKey methodKey,
                                           final MethodInfo methodInfo,
                                           final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;  // the specific constructor signature

        if ( action != INSTANTIATE )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

/* IN_PROCESS */
        final boolean callsTheTarget = instantiatesClassWithSignature( studentMethod, target ) ;

        // match the actual behavior with the specified behavior
        return switch ( requirementType )
            {
            case MUST -> callsTheTarget ;
            case MUST_NOT -> ! callsTheTarget ;
            case OPTIONALLY -> true ;
            case REQUIRES,
                 PROHIBITS,
                 CUSTOM
                    -> throw new InvalidRequirementException( String.format( "%s is not an acceptable requirementType in this context",
                                                                             requirementType ) ) ;
            default -> throw new ComplianceException( String.format( "unexpected requirementType: %s",
                                                                     requirementType.getClass()
                                                                                    .getSimpleName() ) ) ;
            } ;

        }   // end checkActionInstantiate()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionSetValue( final Requirement requirement,
                                        final MethodKey methodKey,
                                        final MethodInfo methodInfo,
                                        final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != SET_VALUE )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionSetValue()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionGetValue( final Requirement requirement,
                                        final MethodKey methodKey,
                                        final MethodInfo methodInfo,
                                        final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != GET_VALUE )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionGetValue()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionDefine( final Requirement requirement,
                                      final MethodKey methodKey,
                                      final MethodInfo methodInfo,
                                      final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != DEFINE )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionDefine()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionUndefine( final Requirement requirement,
                                        final MethodKey methodKey,
                                        final MethodInfo methodInfo,
                                        final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != UNDEFINE )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionUndefine()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionAdd( final Requirement requirement,
                                   final MethodKey methodKey,
                                   final MethodInfo methodInfo,
                                   final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != ADD )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionAdd()


    @SuppressWarnings( "unused" )
    public boolean checkActionDelete( final Requirement requirement,
                                      final MethodKey methodKey,
                                      final MethodInfo methodInfo,
                                      final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != DELETE )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        final boolean onDeletedList = isOnActualActionList( methodKey, action ) ;

        return switch ( requirementType )
            {
            case MUST,
                 REQUIRES -> onDeletedList ;
            case MUST_NOT,
                 PROHIBITS -> ! onDeletedList ;
            case OPTIONALLY,
                 CUSTOM -> true ;
            default -> throw new IllegalStateException( String.format( "unexpected requirementType: %s",
                                                                       requirementType.getClass()
                                                                                      .getSimpleName() ) ) ;
            } ;

        }   // end checkActionDelete()


    @SuppressWarnings( "unused" )
    public boolean checkActionModify( final Requirement requirement,
                                      final MethodKey methodKey,
                                      final MethodInfo methodInfo,
                                      final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != MODIFY )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        final boolean onDeletedList = isOnActualActionList( methodKey, action ) ;

        return switch ( requirementType )
            {
            case MUST,
                 REQUIRES -> onDeletedList ;
            case MUST_NOT,
                 PROHIBITS -> ! onDeletedList ;
            case OPTIONALLY,
                 CUSTOM -> true ;
            default -> throw new IllegalStateException( String.format( "unexpected requirementType: %s",
                                                                       requirementType.getClass()
                                                                                      .getSimpleName() ) ) ;
            } ;

        }   // end checkActionModify()


    @SuppressWarnings( "unused" )
    public boolean checkActionLeaveAsIs( final Requirement requirement,
                                         final MethodKey methodKey,
                                         final MethodInfo methodInfo,
                                         final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != LEAVE_AS_IS )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        final boolean wasModified = this.result.actualChanges.get( action ).contains( methodKey ) ; /* IN_PROCESS */

        final boolean onLeaveAsIsList = isOnActualActionList( methodKey, action ) ;

        return switch ( requirementType )
            {
            case MUST,
                 REQUIRES -> onLeaveAsIsList ;
            case MUST_NOT,
                 PROHIBITS -> ! onLeaveAsIsList ;
            case OPTIONALLY,
                 CUSTOM -> true ;
            default -> throw new IllegalStateException( String.format( "unexpected requirementType: %s",
                                                                       requirementType.getClass()
                                                                                      .getSimpleName() ) ) ;
            } ;

        }   // end checkActionLeaveAsIs()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionUse( final Requirement requirement,
                                   final MethodKey methodKey,
                                   final MethodInfo methodInfo,
                                   final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != USE )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionUse()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionThrow( final Requirement requirement,
                                     final MethodKey methodKey,
                                     final MethodInfo methodInfo,
                                     final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != THROW )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionThrow()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionCatch( final Requirement requirement,
                                     final MethodKey methodKey,
                                     final MethodInfo methodInfo,
                                     final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != CATCH )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionCatch()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionTry( final Requirement requirement,
                                   final MethodKey methodKey,
                                   final MethodInfo methodInfo,
                                   final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != CATCH )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionTry()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionTryWithResources( final Requirement requirement,
                                                final MethodKey methodKey,
                                                final MethodInfo methodInfo,
                                                final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != CATCH )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionTryWithResources()


    @SuppressWarnings( { "static-method", "unused" } )
    public boolean checkActionFinally( final Requirement requirement,
                                       final MethodKey methodKey,
                                       final MethodInfo methodInfo,
                                       final CallableDeclaration<?> studentMethod )
        {

        final Behavior behavior = requirement.behavior ;
        final RequirementType requirementType = behavior.requirementType ;
        final Action action = behavior.action ;
        final TargetType targetType = requirement.targetType ;
        final String target = requirement.target ;

        if ( action != CATCH )
            {
            throw new IllegalStateException( String.format( "called with incompatible requirement - wrong action: %s",
                                                            action ) ) ;
            }

        return false ;

        }   // end checkActionFinally()


    /**
     * check for a change on the relevant actual changes list
     *
     * @param methodKey
     *     the method to check
     * @param action
     *     the action to check
     *
     * @return {@code true} if the method is on the specified list, {@code false} otherwise
     */
    public boolean isOnActualActionList( final MethodKey methodKey,
                                         final Action action )
        {

        final List<MethodKey> actionList = this.result.actualChanges.get( action ) ;

        if ( actionList == null )
            {
            return false ;
            }

        return actionList.contains( methodKey ) ;

        }   // end isOnActualActionList()


    /**
     * check for a change on the relevant expected changes list
     *
     * @param methodKey
     *     the method to check
     * @param action
     *     the action to check
     *
     * @return {@code true} if the method is on the specified list, {@code false} otherwise
     */
    public boolean isOnExpectedActionList( final MethodKey methodKey,
                                           final Action action )
        {

        final List<MethodKey> actionList = this.result.expectedChanges.get( action ) ;

        if ( actionList == null )
            {
            return false ;
            }

        return actionList.contains( methodKey ) ;

        }   // end isOnExpectedActionList()


    /**
     * get all code and parse for methods and constructors: starter, solution, student work
     *
     * @throws Exception
     *     from the file system, JavaParser, or this utility
     */
    public void collectCode() throws Exception
        {

        this.result.sourceCodePaths.put( STARTER, this.starterCodePath ) ;
        this.result.sourceCodePaths.put( STUDENT_WORK, this.studentWorkPath ) ;

        this.report.append( String.format( """
                                           Starter code: %s
                                           Student work: %s
                                           """, this.result.sourceCodePaths.get( STARTER ),
                                           this.result.sourceCodePaths.get( STUDENT_WORK ) ) ) ;

        this.result.sourceCode.put( STARTER, Files.readAllLines( this.starterCodePath ) ) ;
        this.result.sourceCode.put( STUDENT_WORK, Files.readAllLines( this.studentWorkPath ) ) ;

        collectMethods( this.starterCodePath, STARTER ) ;
        collectMethods( this.studentWorkPath, STUDENT_WORK ) ;

        }   // end collectCode()


    /**
     * retrieve all methods from specified source code
     *
     * @param javaFilePath
     *     path to the source code
     *
     * @return a collection of methods
     *
     * @throws Exception
     *     any exception from JavaParser
     */
    public Map<MethodKey, MethodInfo> collectMethods( final Path javaFilePath,
                                                      final SourceCategory sourceCategory ) throws Exception
        {

        final CompilationUnit compilationUnit = StaticJavaParser.parse( javaFilePath ) ;

        return collectMethods( compilationUnit, sourceCategory ) ;

        }   // end collectMethods()


    /**
     * retrieve all methods from specified source code
     *
     * @param compilationUnit
     *     AST of the parsed code
     *
     * @return a collection of methods
     *
     * @throws Exception
     *     any exception from JavaParser
     */
    public Map<MethodKey, MethodInfo> collectMethods( final CompilationUnit compilationUnit,
                                                      final SourceCategory sourceCategory ) throws Exception
        {

/* 2xCk with anonymous class (or interface?) */
        final String sourceClassName = compilationUnit.getPrimaryTypeName().orElse( null ) ;

        final HashMap<MethodKey, MethodInfo> methods = new LinkedHashMap<>() ;
        this.result.methods.put( sourceCategory, methods ) ;

        final HashMap<String, MethodKey> methodKeys = new LinkedHashMap<>() ;
        this.result.methodKeys.put( sourceCategory, methodKeys ) ;

        compilationUnit.findAll( CallableDeclaration.class ).forEach( callableDeclaration ->
            {
            final MethodInfo methodInfo ;

            if ( callableDeclaration instanceof final MethodDeclaration methodDeclaration )
                {
                methodInfo = MethodInfo.fromMethod( sourceClassName, methodDeclaration ) ;
                }
            else if ( callableDeclaration instanceof final ConstructorDeclaration constructorDeclaration )
                {
                methodInfo = MethodInfo.fromConstructor( sourceClassName, constructorDeclaration ) ;
                }
            else
                {
                throw new IllegalStateException( String.format( "unexpected CallableDeclaration of type %s",
                                                                callableDeclaration.getClass()
                                                                                   .getSimpleName() ) ) ;
                }

            final MethodKey methodKey = MethodKey.from( methodInfo ) ;
            methods.put( methodKey, methodInfo ) ;
            methodKeys.put( methodInfo.getMethodSignature(), methodKey ) ;

            if ( ! this.studentWorkMethodSignatures.contains( methodInfo.getMethodSignature() ) )
                {
                this.studentWorkMethodSignatures.add( methodInfo.getMethodSignature() ) ;
                }

            } ) ;

        return methods ;

        }   // end collectMethods()


    /*
     * determine the work that was done
     */
    public void determineActual()
        {

        // handy short mnemonic references
        final List<MethodKey> methodsAdded = this.result.actualChanges.get( ADD ) ;
        final List<MethodKey> methodsModified = this.result.actualChanges.get( MODIFY ) ;
        final List<MethodKey> methodsDeleted = this.result.actualChanges.get( DELETE ) ;
        final List<MethodKey> methodsUnchanged = this.result.actualChanges.get( LEAVE_AS_IS ) ;

        // Detect methods that were changed or deleted
        for ( final var entry : this.result.methods.get( STARTER ).entrySet() )
            {
            final MethodKey methodKey = entry.getKey() ;
            final MethodInfo starterMethodInfo = entry.getValue() ;
            final CallableDeclaration<?> starterMethod = starterMethodInfo.getCallableDeclaration() ;
            final MethodInfo studentMethodInfo = this.result.methods.get( STUDENT_WORK ).get( methodKey ) ;

            if ( studentMethodInfo == null )
                {
                methodsDeleted.add( methodKey ) ;
                }
            else
                {
                final CallableDeclaration<?> studentMethod = studentMethodInfo.getCallableDeclaration() ;

                final String starterMethodBody = MethodNormalizer.normalizedBody( starterMethod ) ;
                final String studentMethodBody = MethodNormalizer.normalizedBody( studentMethod ) ;

                if ( ! starterMethodBody.equals( studentMethodBody ) )
                    {
                    methodsModified.add( methodKey ) ;
                    }
                else
                    {
                    methodsUnchanged.add( methodKey ) ;

                    studentMethodInfo.setCompliance( NOT_CHECKED ) ;
                    }

                }

            }   // end for

        // Detect methods that were added
        for ( final var entry : this.result.methods.get( STUDENT_WORK ).entrySet() )
            {
            final MethodKey methodKey = entry.getKey() ;
//        final CallableDeclaration<?> studentMethod = entry.getValue().getMethodOrConstructorDeclaration() ;
            final MethodInfo starterMethodInfo = this.result.methods.get( STARTER ).get( methodKey ) ;

            if ( starterMethodInfo == null )
                {
                methodsAdded.add( methodKey ) ;
                }

            }

        /*
         * display student's methods to check
         */

        this.report.append( String.format( "%n-----------------%n%nActual:%n" ) ) ;

        Requirement.excludeComplianceInToString() ;
        
        boolean sawOptionalActions = false ;
        
        sawOptionalActions |= reportSummaryListOfMethods( methodsAdded, "added" ) ;
        sawOptionalActions |= reportSummaryListOfMethods( methodsModified, "modified" ) ;
        sawOptionalActions |= reportSummaryListOfMethods( methodsDeleted, "deleted" ) ;
        sawOptionalActions |= reportSummaryListOfMethods( methodsUnchanged, "unchanged" ) ;

        if ( sawOptionalActions )
            {
            this.report.append( String.format( "%n* => optional action(s) specified%n" ) ) ;
            }
        
        Requirement.includeComplianceInToString() ;

        }   // end determineActual()


    /**
     * log a category of methods
     *
     * @param methodsToLog
     *     the list of zero or more methods
     * @param actionCategory
     *     the description for this group of methods
     *
     * @return whether any methods had optional actions
     */
    private boolean reportSummaryListOfMethods( final List<MethodKey> methodsToLog,
                                                final String actionCategory )
        {
        
        boolean sawOptionalActions = false ;

        this.report.append( String.format( "%n%s:%n%n", actionCategory ) ) ;

        if ( methodsToLog.size() == 0 )
            {
            this.report.append( String.format( "  none%n" ) ) ;
            }
        else
            {
            
            for ( final MethodKey key : methodsToLog )
                {
                List<Requirement> requirements = this.requirementsForEachMethod.get( key.toString() ) ;
                
                if ( requirements == null )
                    {
                    /*
                     * a method was added to the student's code that wasn't in the starter code
                     */
                    // DMR TODO test adding method in rules that isn't in the starter code
                    
                    // log it
                    this.report.append( String.format( "  %s%n", key ) ) ;
                    
                    
                    // DMR TODO make sure appropriate state is set
                    
                    
                    // for now, nothing else we can do with this student method     /* DMR FUTURE */
                    // good to implement soon
                    // class-level rules will be able to enforce added methods
                    
                    
                    // advance to the next method if any
                    break ;
                    }
                
                String qualifier = "  " ;
                
                boolean methodHasAnOptionalAction = false ;
                
                for ( Requirement requirement : requirements )
                    {
                    RequirementType enforcement = requirement.behavior.requirementType ;
                    if ( enforcement == RequirementType.OPTIONALLY )
                        {
                        methodHasAnOptionalAction = true ;
                        sawOptionalActions = true ;
                        
                        break ;
                        }
                    }
                
                if ( methodHasAnOptionalAction )
                    {
                    qualifier = "* " ;
                    }
                
                this.report.append( String.format( "%s%s%n", qualifier, key ) ) ;
                }

            }
        
        return sawOptionalActions ;

        }   // end reportSummaryActualChanges()


    /**
     * identify the work that is expected
     */
    public void determineExpected()
        {

        // handy short mnemonic references
        final List<MethodKey> methodsToAdd = this.result.expectedChanges.get( ADD ) ;
        final List<MethodKey> methodsToModify = this.result.expectedChanges.get( MODIFY ) ;
        final List<MethodKey> methodsToDelete = this.result.expectedChanges.get( DELETE ) ;
        final List<MethodKey> methodsToLeaveAsIs = this.result.expectedChanges.get( LEAVE_AS_IS ) ;


        /*
         * for each method for which there are rules/requirements, add their key to the list which best
         * describes our expectations
         */
        for ( final Entry<String, List<Requirement>> requirements : this.requirementsForEachMethod.entrySet() )
            {
            
            final MethodKey psuedoMethodKey = MethodKey.from( requirements.getKey() ) ;
            
            // default is to leave the method/constructor as-is
            Action action = LEAVE_AS_IS ;
            
            // set the action to the first non-optional, non-don't rule, if any
            for( Requirement requirement : requirements.getValue() )
                {
                Behavior behavior = requirement.behavior ;
                RequirementType requirementType = behavior.requirementType ;

                if ( ( requirementType != OPTIONALLY ) && ( requirementType != MUST_NOT )
                     && ( requirementType != PROHIBITS ) && ( ! behavior.equals( DO_LEAVE_AS_IS ) ) )
                    {
                    action = behavior.action ;
                    break ;
                    }

                }

            // add this method to the appropriate list according to the action
            switch ( action )
                {
                case ADD -> methodsToAdd.add( psuedoMethodKey ) ;
                case CALL,
                     INSTANTIATE,
                     GET_VALUE,
                     SET_VALUE,
                     MODIFY,
                     THROW,
                     CATCH,
                     DEFINE,
                     UNDEFINE,
                     USE -> methodsToModify.add( psuedoMethodKey ) ;
                case DELETE -> methodsToDelete.add( psuedoMethodKey ) ;
                case LEAVE_AS_IS -> methodsToLeaveAsIs.add( psuedoMethodKey ) ;
                default -> throw new IllegalStateException( String.format( "unexpected action: %s",
                                                                           action ) ) ;
                }

            /* IN_PROCESS - will this cause NPEs? */
            }   // end for method

        /*
         * list the methods we expect will be affected (added, modified, deleted, left unchanged)
         */

        this.report.append( String.format( "%n-----------------%n%nExpect:%n" ) ) ;

        Requirement.excludeComplianceInToString() ;
        
        boolean sawOptionalActions = false ;
        
        sawOptionalActions |= reportSummaryListOfMethods( methodsToAdd, "added" ) ;
        sawOptionalActions |= reportSummaryListOfMethods( methodsToModify, "modified" ) ;
        sawOptionalActions |= reportSummaryListOfMethods( methodsToDelete, "deleted" ) ;
        sawOptionalActions |= reportSummaryListOfMethods( methodsToLeaveAsIs, "unchanged" ) ;

        if ( sawOptionalActions )
            {
            this.report.append( String.format( "%n* => optional action(s) specified%n" ) ) ;
            }
        
        Requirement.includeComplianceInToString() ;

        }   // end determineExpected()


    /**
     * log the results of a specific compliance check
     *
     * @param report
     * @param format
     * @param description
     * @param isCorrect
     */
    public static void logComplianceCheck( final StringBuilder report,
                                           final String format,
                                           final String description,
                                           final boolean isCorrect )
        {

        report.append( String.format( format,
                                      description,
                                      isCorrect
                                              ? "✓ compliant"
                                              : "✗ non-compliant" ) ) ;

        }   // end logCompliance()


    /**
     * locate the code: starter, solution, student
     */
    private void findSourceCode()
        {

        final List<Path> files = findFiles( this.classToAssess, this.baseSearchLocation ) ;

        for ( final Path path : files )
            {

            for ( int i = 0 ; i < path.getNameCount() ; i++ )
                {

                switch ( path.getName( i ).toString() )
                    {
                    case "student" ->
                        {
                        // will locate student's submission below
                        break ;
                        }
                    case "starter" ->
                        {
                        this.starterCodePath = path ;
                        break ;
                        }
//                    case "solution" ->
//                        {
//                        this.solutionPath = path ;
//                        break ;
//                        }
                    default ->
                        {
                        continue ;
                        }
                    }   // end switch

                }   // end inner for

            }   // end outer for

        // locate sudent's implementation
        if ( this.studentWorkPath == null )
            {
            // see if we can locate the student's implementation under ./src and ./src/main/java
            final List<Path> studentWorkPaths
                    = findFiles( this.classToAssess, "./src/main/java/edu/wit/scds" ) ;
            studentWorkPaths.addAll( findFiles( this.classToAssess, "./src/edu/wit/scds" ) ) ;

            if ( studentWorkPaths.size() >= 1 )
                {

                if ( studentWorkPaths.size() > 1 )
                    {
                    System.out.printf( "multiple versions of the student's %s found:%n",
                                       this.classToAssess ) ;

                    for ( final Path path : studentWorkPaths )
                        {
                        System.out.printf( "\t%s%n", path.toString() ) ;
                        }

                    System.out.printf( "using first: %s%n", studentWorkPaths.getFirst().toString() ) ;
                    }

                this.studentWorkPath = studentWorkPaths.getFirst() ;
                }

            }

        // make sure we have all the required pieces
        boolean haveAllThePieces = true ;

        if ( this.starterCodePath == null )
            {
            haveAllThePieces = false ;
            System.out.printf( "%nstarter code not found%n" ) ;
            }

//        if ( this.solutionPath == null )
//            {
//            haveAllThePieces = false ;
//            System.out.printf( "%nsolution not found%n" ) ;
//            }

        if ( this.studentWorkPath == null )
            {
            haveAllThePieces = false ;
            System.out.printf( "%nstudent work not found%n" ) ;
            }

        if ( ! haveAllThePieces )
            {
            throw new ComplianceException( String.format( "%nunable to evaluate %s%n%naborting%n",
                                                          this.classToAssess ) ) ;
            }

        }   // end findSourceCode()


    /**
     * sets or updates the requirements for a method
     *
     * @param methodSignature
     *     identifies the specific method, including overridden methods
     * @param newRequirements
     *     zero or more requirements to apply to the specified method
     */
    public void setMethodRequirements( final String methodSignature,
                                       final Requirement... newRequirements )
        {

        Objects.requireNonNull( methodSignature, "methodSignature" ) ;
        Objects.requireNonNull( newRequirements, "newRequirements" ) ;

        for ( final Requirement newRequirement : newRequirements )
            {
            Objects.requireNonNull( newRequirement, "element in newRequirements[]" ) ;
            }

        // assertion: it's safe to access the arguments

        final List<Requirement> requirements
                = this.requirementsForEachMethod.getOrDefault( methodSignature, new LinkedList<>() ) ;
        requirements.addAll( Arrays.asList( newRequirements ) ) ;
        this.requirementsForEachMethod.put( methodSignature, requirements ) ;

        }   // end setMethodRequirements()


    /**
     * sets or updates the requirements for a method to none
     *
     * @param methodSignature
     *     identifies the specific method, including overridden methods
     */
    public void setMethodNoRequirements( final String methodSignature )
        {

        Objects.requireNonNull( methodSignature, "methodSignature" ) ;

        // assertion: it's safe to access the argument

        // we'll replace any existing list
        final List<Requirement> requirements = new LinkedList<>() ;
        this.requirementsForEachMethod.put( methodSignature, requirements ) ;

        }   // end setMethodNoRequirements()

    }   // end class ImplementationComplianceChecker
