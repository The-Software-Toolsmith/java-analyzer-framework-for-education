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

import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.ADD ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.CALL ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.DELETE ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.INSTANTIATE ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.LEAVE_AS_IS ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.MODIFY ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Action.USE ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.RequirementType.MUST ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.RequirementType.MUST_NOT ;
import static education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.RequirementType.OPTIONALLY ;

import com.fasterxml.jackson.annotation.JsonCreator ;
import com.fasterxml.jackson.annotation.JsonProperty ;

import java.util.LinkedList ;
import java.util.List ;
import java.util.Objects ;


/**
 * representation of a behavior
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-14 Initial implementation
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class Behavior
    {

    /*
     * constants
     */


    // common behaviors

    public final static Behavior DO_ADD = new Behavior( MUST, ADD ) ;
    public final static Behavior DO_NOT_ADD = new Behavior( MUST_NOT, ADD ) ;
    public final static Behavior OPTIONALLY_ADD = new Behavior( OPTIONALLY, ADD ) ;

    public final static Behavior DO_DELETE = new Behavior( MUST, DELETE ) ;
    public final static Behavior DO_NOT_DELETE = new Behavior( MUST_NOT, DELETE ) ;
    public final static Behavior OPTIONALLY_DELETE = new Behavior( OPTIONALLY, DELETE ) ;

    public final static Behavior DO_MODIFY = new Behavior( MUST, MODIFY ) ;
    public final static Behavior DO_NOT_MODIFY = new Behavior( MUST_NOT, MODIFY ) ;
    public final static Behavior OPTIONALLY_MODIFY = new Behavior( OPTIONALLY, MODIFY ) ;

    public final static Behavior DO_LEAVE_AS_IS = new Behavior( MUST, LEAVE_AS_IS ) ;
    public final static Behavior DO_NOT_LEAVE_AS_IS = new Behavior( MUST_NOT, LEAVE_AS_IS ) ;
    public final static Behavior OPTIONALLY_LEAVE_AS_IS = new Behavior( OPTIONALLY, LEAVE_AS_IS ) ;

    public final static Behavior DO_CALL = new Behavior( MUST, CALL ) ;
    public final static Behavior DO_NOT_CALL = new Behavior( MUST_NOT, CALL ) ;
    public final static Behavior OPTIONALLY_CALL = new Behavior( OPTIONALLY, CALL ) ;

    public final static Behavior DO_INSTANTIATE_USING = new Behavior( MUST, INSTANTIATE ) ;
    public final static Behavior DO_NOT_INSTANTIATE_USING = new Behavior( MUST_NOT, INSTANTIATE ) ;
    public final static Behavior OPTIONALLY_INSTANTIATE_USING = new Behavior( OPTIONALLY, INSTANTIATE ) ;

    public final static Behavior DO_USE = new Behavior( MUST, USE ) ;
    public final static Behavior DO_NOT_USE = new Behavior( MUST_NOT, USE ) ;
    public final static Behavior OPTIONALLY_USE = new Behavior( OPTIONALLY, USE ) ;


    /*
     * data fields
     */


    /** the action */
    public final Action action ;
    /** how the action should be enforced */
    public final RequirementType requirementType ;


    /*
     * constructors
     */


    /**
     * set state to combine an action and enforcement as a unit
     *
     * @param typeOfRequirement
     *     the kind of enforcement
     * @param whatToDo
     *     the action
     */
    @JsonCreator
    public Behavior( @JsonProperty( "requirementType" ) final RequirementType typeOfRequirement,
                     @JsonProperty( "action" ) final Action whatToDo )
        {

        Objects.requireNonNull( typeOfRequirement, "typeOfRequirement" ) ;
        Objects.requireNonNull( whatToDo, "whatToDo" ) ;


        this.action = whatToDo ;
        this.requirementType = typeOfRequirement ;

        }   // end complete constructor


    /*
     * API methods
     */


    @Override
    public boolean equals( final Object object )
        {

        if ( object == this )
            {
            return true ;
            }

        if ( object instanceof final Behavior otherRequirement )
            {
            return ( this.action == otherRequirement.action )
                   && ( this.requirementType == otherRequirement.requirementType ) ;
            }

        return false ;

        }   // end equals()


    @Override
    public int hashCode()
        {

        return Objects.hash( this.requirementType, this.action ) ;

        }   // end hashCode()


    @Override
    public String toString()
        {

        return String.format( "%s %s", this.requirementType, this.action ) ;

        }   // end toString()


    /*
     * testing and debugging
     */


    /**
     * testing driver
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        final List<Behavior> behaviors = new LinkedList<>() ;

        for ( final RequirementType typeOfRequirement : RequirementType.values() )
            {

            for ( final Action action : Action.values() )
                {
                behaviors.add( new Behavior( typeOfRequirement, action ) ) ;
                }

            }

        RequirementType previousRequirementType = behaviors.getFirst().requirementType ;

        for ( final Behavior behavior : behaviors )
            {

            if ( behavior.requirementType != previousRequirementType )
                {
                System.out.printf( "%n" ) ;
                }

            previousRequirementType = behavior.requirementType ;

            System.out.printf( "%s%n", behavior ) ;
            }

        }    // end main()

    }   // end class Requirement
