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

import education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.Compliance ;

import static education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.Compliance.FAILED ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.Compliance.NOT_CHECKED ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.Compliance.PASSED ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.Compliance.UNKNOWN ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.DO_LEAVE_AS_IS ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.DO_NOT_ADD ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.DO_NOT_DELETE ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.DO_NOT_MODIFY ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.OPTIONALLY_ADD ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.OPTIONALLY_DELETE ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Behavior.OPTIONALLY_MODIFY ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.TargetType.CONSTRUCTOR ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.TargetType.GLOBALLY_APPLICABLE ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.TargetType.METHOD ;

import com.fasterxml.jackson.annotation.JsonCreator ;
import com.fasterxml.jackson.annotation.JsonProperty ;

import java.util.LinkedHashMap ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Map ;
import java.util.Objects ;


/**
 * represents a single compliance test element
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-26 Initial implementation
 * @version 2.0 2025-12-26 extensive restructuring
 * @version 2.1 2025-12-27
 *     <ul>
 *     <li>add component (action, enforcement type) constructors
 *     <li>add arguments and grading maps
 *     <li>add remaining getters
 *     </ul>
 */
public class Requirement
    {

    /*
     * constants
     */

    // common requirements

    /** rule: do not modify a method */
    public final static Requirement DO_NOT_MODIFY_METHOD = new Requirement( DO_NOT_MODIFY, METHOD ) ;
    /** rule: do not delete a method */
    public final static Requirement DO_NOT_DELETE_METHOD = new Requirement( DO_NOT_DELETE, METHOD ) ;
    /** rule: do not add a method */
    public final static Requirement DO_NOT_ADD_METHOD = new Requirement( DO_NOT_ADD, METHOD ) ;

    /** rule: optionally modify a method */
    public final static Requirement OPTIONALLY_MODIFY_METHOD = new Requirement( OPTIONALLY_MODIFY, METHOD ) ;
    /** rule: optionally delete a method */
    public final static Requirement OPTIONALLY_DELETE_METHOD = new Requirement( OPTIONALLY_DELETE, METHOD ) ;
    /** rule: optionally add a method */
    public final static Requirement OPTIONALLY_ADD_METHOD = new Requirement( OPTIONALLY_ADD, METHOD ) ;

    /** rule: do not modify a constructor */
    public final static Requirement DO_NOT_MODIFY_CONSTRUCTOR
            = new Requirement( DO_NOT_MODIFY, CONSTRUCTOR ) ;
    /** rule: do not delete a constructor */
    public final static Requirement DO_NOT_DELETE_CONSTRUCTOR
            = new Requirement( DO_NOT_DELETE, CONSTRUCTOR ) ;
    /** rule: do not add a constructor */
    public final static Requirement DO_NOT_ADD_CONSTRUCTOR = new Requirement( DO_NOT_ADD, CONSTRUCTOR ) ;

    /** rule: optionally modify a constructor */
    public final static Requirement OPTIONALLY_MODIFY_CONSTRUCTOR
            = new Requirement( OPTIONALLY_MODIFY, CONSTRUCTOR ) ;
    /** rule: optionally delete a constructor */
    public final static Requirement OPTIONALLY_DELETE_CONSTRUCTOR
            = new Requirement( OPTIONALLY_DELETE, CONSTRUCTOR ) ;
    /** rule: optionally add a constructor */
    public final static Requirement OPTIONALLY_ADD_CONSTRUCTOR
            = new Requirement( OPTIONALLY_ADD, CONSTRUCTOR ) ;

    /** rule: don't change anything */
    public final static Requirement DO_LEAVE_EVERYTHING_AS_IS
            = new Requirement( DO_LEAVE_AS_IS, GLOBALLY_APPLICABLE ) ;


    /*
     * static fields
     */
    private static boolean includeComplianceInToString ;
    private static boolean indentInToString ;
    static
        {
        includeComplianceInToString = true ;
        indentInToString = true ;
        }   // end static initializer


    /*
     * data fields
     */


    /*
     * the 'what's
     */

    /** represents the action and its applicability */
    protected final Behavior behavior ;

    /** represents the target's type or a placeholder when unneeded */
    protected final TargetType targetType ;

    /** the signature of the target method/constructor or a placeholder (null) when unneeded */
    protected final String target ;


    /*
     * status
     */

    /** the status of the test */
    protected Compliance compliance ;


    /*
     * customization
     */

    /** any arguments pertaining to assessment of a requirement */
    protected final Map<String, Object> arguments ;

    /** any additional data pertaining to grading of a requirement */
    protected final Map<String, Object> grading ;

    /** any additional data pertaining to assessment of a requirement */
    protected final Map<String, Object> extensions ;


    /*
     * constructors
     */


    /**
     * set initial state with specified behavior components (action, requirement type) and type of target
     * where no target specification is needed
     *
     * @param specifiedAction
     *     the action component of the behavior
     * @param specifiedRequirementType
     *     the enforcement component of the behavior
     * @param specifiedTargetType
     *     type of target
     */
    public Requirement( final Action specifiedAction,
                        final RequirementType specifiedRequirementType,
                        final TargetType specifiedTargetType )
        {

        this( new Behavior( specifiedRequirementType, specifiedAction ), specifiedTargetType, null ) ;

        }   // end basic component constructor


    /**
     * set initial state with specified behavior components (action, requirement type) and type of target
     * where no target specification is needed
     *
     * @param specifiedAction
     *     the action component of the behavior
     * @param specifiedRequirementType
     *     the enforcement component of the behavior
     * @param specifiedTargetType
     *     type of target
     * @param specifiedTarget
     *     target for the rule
     */
    public Requirement( final Action specifiedAction,
                        final RequirementType specifiedRequirementType,
                        final TargetType specifiedTargetType,
                        final String specifiedTarget )
        {

        this( new Behavior( specifiedRequirementType, specifiedAction ),
              specifiedTargetType,
              specifiedTarget ) ;

        }   // end full component constructor


    /**
     * set initial state with a specified behavior and type of target where no target specification is needed
     *
     * @param specifiedBehavior
     *     the action and enforcement
     * @param specifiedTargetType
     *     type of target
     */
    public Requirement( final Behavior specifiedBehavior,
                        final TargetType specifiedTargetType )
        {

        this( specifiedBehavior, specifiedTargetType, null ) ;

        }   // end minimal constructor


    /**
     * set initial state with specified behavior, type of target, and target
     *
     * @param specifiedBehavior
     *     the action and enforcement
     * @param specifiedTargetType
     *     type of target
     * @param specifiedTarget
     *     target for the rule
     */
    public Requirement( final Behavior specifiedBehavior,
                        final TargetType specifiedTargetType,
                        final String specifiedTarget )
        {

        this( specifiedBehavior, specifiedTargetType, specifiedTarget, null ) ;

        }   // end basic constructor


    /**
     * set initial state with full specifications
     *
     * @param specifiedBehavior
     *     the action and enforcement
     * @param specifiedTargetType
     *     type of target
     * @param specifiedTarget
     *     target for the rule
     * @param initialCompliance
     *     initial compliance
     */
    @JsonCreator
    public Requirement( @JsonProperty( "behavior" ) final Behavior specifiedBehavior,
                        @JsonProperty( "targetType" ) final TargetType specifiedTargetType,
                        @JsonProperty( "target" ) final String specifiedTarget,
                        @JsonProperty( "compliance" ) final Compliance initialCompliance )
        {

        Objects.requireNonNull( specifiedBehavior, "behaves" ) ;
        Objects.requireNonNull( specifiedTargetType, "typeOfTarget" ) ;

        // the 'what's
        this.behavior = specifiedBehavior ;

        this.targetType = specifiedTargetType ;
        this.target = specifiedTarget ;

        // status
        this.compliance = initialCompliance == null
                ? UNKNOWN
                : initialCompliance ;

        // FUTURE re-evaluate choice of map customization
        this.grading = new LinkedHashMap<>() ;
        this.arguments = new LinkedHashMap<>() ;
        this.extensions = new LinkedHashMap<>() ;

        }   // end full constructor


    /*
     * API methods
     */

    // simple getters


    /**
     * @return the behavior
     */
    public Behavior getBehavior()
        {

        return this.behavior ;

        }   // end getBehavior()


    /**
     * @return the action (from the behavior)
     */
    public Action getAction()
        {

        return this.behavior.action ;

        }   // end getAction()


    /**
     * @return the requirement type (from the behavior)
     */
    public RequirementType getRequirementType()
        {

        return this.behavior.requirementType ;

        }   // end getRequirementType()


    /**
     * determine whether there are arguments associated with this rule
     *
     * @return {@code true} if one or more arguments are in the map; {@code false} otherwise
     */
    public boolean hasArguments()
        {

        return this.arguments.size() > 0 ;

        }   // end hasArguments()


    /**
     * @return the arguments
     */
    public Map<String, Object> getArguments()
        {

        return this.arguments ;

        }   // end getArguments()


    /**
     * determine whether there are arguments associated with this rule
     *
     * @return {@code true} if one or more grading elements are in the map; {@code false} otherwise
     */
    public boolean hasGrading()
        {

        return this.grading.size() > 0 ;

        }   // end hasGrading()


    /**
     * @return the grading
     */
    public Map<String, Object> getGrading()
        {

        return this.grading ;

        }   // end getGrading()


    /**
     * determine whether there are extensions associated with this rule
     *
     * @return {@code true} if one or more extensions are in the map; {@code false} otherwise
     */
    public boolean hasExtensions()
        {

        return this.extensions.size() > 0 ;

        }   // end hasExtensions()


    /**
     * @return the extensions
     */
    public Map<String, Object> getExtensions()
        {

        return this.extensions ;

        }   // end getExtensions()


    // compliance


    /**
     * @return the compliance
     */
    public Compliance getCompliance()
        {

        return this.compliance ;

        }   // end getCompliance()


    /**
     * @param newCompliance
     *     the new, non-null compliance
     *
     * @return reference to this instance for fluent chaining
     */
    public Requirement setCompliance( final Compliance newCompliance )
        {

        Objects.requireNonNull( newCompliance, "newCompliance" ) ;

        this.compliance = newCompliance ;

        return this ;   // for fluent chaining

        }   // end setCompliance()


    /**
     * convenience method - set the compliance to failed
     *
     * @return reference to this instance for fluent chaining
     */
    public Requirement setFailed()
        {

        setCompliance( FAILED ) ;

        return this ;   // for fluent chaining

        }   // end setFailed()


    /**
     * convenience method - set the compliance to not checked
     *
     * @return reference to this instance for fluent chaining
     */
    public Requirement setNotChecked()
        {

        setCompliance( NOT_CHECKED ) ;

        return this ;   // for fluent chaining

        }   // end setNotChecked()


    /**
     * convenience method - set the compliance to passed
     *
     * @return reference to this instance for fluent chaining
     */
    public Requirement setPassed()
        {

        setCompliance( PASSED ) ;

        return this ;   // for fluent chaining

        }   // end setPassed()


    // target


    /**
     * determine whether there is a target associated with this rule
     *
     * @return {@code true} if a target was specified; {@code false} otherwise
     */
    public boolean hasTarget()
        {

        return this.target != null ;

        }   // end hasTarget()


    /**
     * @return the target entity or {@code null} if none/for a globally applicable rule
     */
    public String getTarget()
        {

        return this.target ;

        }   // end getTarget()


    /*
     * 'standard' API methods
     */


    @Override
    public boolean equals( final Object object )
        {

        if ( object == this )
            {
            return true ;
            }

        if ( object instanceof final Requirement otherRequirement )
            {
            return ( this.behavior.equals( otherRequirement.behavior ) )
                   && ( this.targetType == otherRequirement.targetType )
                   && ( this.target.equals( otherRequirement.target ) ) ;
            }

        return false ;

        }   // end equals()


    @Override
    public int hashCode()
        {

        return Objects.hash( this.behavior, this.targetType, this.target ) ;

        }   // end hashCode()


    @Override
    public String toString()
        {

        return String.format( "%s%s %s%s%s",
                              indentInToString
                                      ? "  "
                                      : "",
                              this.behavior,
                              this.targetType,
                              this.target == null
                                      ? ""
                                      : " " + this.target,
                              includeComplianceInToString
                                      ? String.format( " -> %s", this.compliance )
                                      : "" ) ;

        }   // end toString()


    /*
     * static API methods
     */


    /*
     * configure toString() behavior
     */


    // include compliance in toString() output
    /**
     * flip the switch to exclude the compliance description in the output from {@@code toString()}
     *
     * @return the prior setting where {@code true} indicates include it and {@code false} exclude it
     */
    public static boolean excludeComplianceInToString()
        {

        final boolean savedIncludeComplianceInToString = includeComplianceInToString ;

        includeComplianceInToString = false ;

        return savedIncludeComplianceInToString ;

        }   // end excludeComplianceInToString()


    /**
     * flip the switch to include the compliance description in the output from {@@code toString()}
     *
     * @return the prior setting where {@code true} indicates include it and {@code false} exclude it
     */
    public static boolean includeComplianceInToString()
        {

        final boolean savedIncludeComplianceInToString = includeComplianceInToString ;

        includeComplianceInToString = true ;

        return savedIncludeComplianceInToString ;

        }   // end includeComplianceInToString()


    /**
     * determine the current setting for inclusion of compliance in the output from {@code toString()}
     *
     * @return {@code true} indicates include it and {@code false} exclude it
     */
    public static boolean includingComplianceInToString()
        {

        return includeComplianceInToString ;

        }   // end includingComplianceInToString()


    /**
     * flip the switch to include the compliance description in the output from {@@code toString()}
     *
     * @param includeIt
     *     {@code true} indicates include it and {@code false} exclude it
     *
     * @return the prior setting
     */
    public static boolean setComplianceInToString( final boolean includeIt )
        {

        final boolean savedIncludeComplianceInToString = includeComplianceInToString ;

        includeComplianceInToString = includeIt ;

        return savedIncludeComplianceInToString ;

        }   // end setComplianceInToString()


// include indentation in toString() output


    /**
     * flip the switch to exclude the compliance description in the output from {@@code toString()}
     *
     * @return the prior setting where {@code true} indicates include it and {@code false} exclude it
     */
    public static boolean excludeIndentInToString()
        {

        final boolean savedIndentInToString = indentInToString ;

        indentInToString = false ;

        return savedIndentInToString ;

        }   // end excludeIndentationInToString()


    /**
     * flip the switch to include the compliance description in the output from {@@code toString()}
     *
     * @return the prior setting where {@code true} indicates include it and {@code false} exclude it
     */
    public static boolean includeIndentInToString()
        {

        final boolean savedIndentInToString = indentInToString ;

        includeComplianceInToString = true ;

        return savedIndentInToString ;

        }   // end includeIndentInToString()


    /**
     * determine the current setting for indentation in the output from {@code toString()}
     *
     * @return {@code true} indicates include it and {@code false} exclude it
     */
    public static boolean includingIndentInToString()
        {

        return indentInToString ;

        }   // end includingIndentInToString()


    /**
     * flip the switch to include indentation in the output from {@@code toString()}
     *
     * @param indentIt
     *     {@code true} indicates include it and {@code false} exclude it
     *
     * @return the prior setting
     */
    public static boolean setIndentInToString( final boolean indentIt )
        {

        final boolean savedIndentInToString = indentInToString ;

        indentInToString = indentIt ;

        return savedIndentInToString ;

        }   // end setIndentInToString()


    /*
     * testing and debugging
     */


    /**
     * test driver
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        final List<Requirement> requirements = new LinkedList<>() ;

        for ( final RequirementType typeOfRequirement : RequirementType.values() )
            {

            for ( final Action action : Action.values() )
                {
                final Behavior behavior = new Behavior( typeOfRequirement, action ) ;

                for ( final TargetType typeOfTarget : TargetType.values() )
                    {
                    requirements.add( new Requirement( behavior, typeOfTarget, "xyzzy" ) ) ;
                    }   // end for target type

                }   // end for action

            }   // end for requirement type

        // miscellaneous additional requirements
        requirements.add( new Requirement( DO_LEAVE_AS_IS, GLOBALLY_APPLICABLE, null, PASSED ) ) ;

        Behavior previousBehavior = requirements.getFirst().behavior ;

        for ( final Requirement requirement : requirements )
            {

            if ( ! requirement.behavior.equals( previousBehavior ) )
                {
                System.out.printf( "%n" ) ;

                previousBehavior = requirement.behavior ;
                }

            System.out.printf( "%s%n", requirement ) ;
            }

        }    // end main()

    }   // end class Requirement
