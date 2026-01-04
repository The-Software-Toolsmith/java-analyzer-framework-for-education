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


/**
 * defines testable operations
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-23 Initial implementation
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public enum Action
    {

    /** a call to a method or chained constructor */
    CALL
    , /** object instantiation */
    INSTANTIATE
    , /** assignment to a variable */
    SET_VALUE
    , /** use of the contents of a variable */
    GET_VALUE
    , /** create an entity such as a symbolic constant - excludes instantiation */
    DEFINE
    , /** discard an entity */
    UNDEFINE
    , /** an entity, typically a method which was not provided in the starter code is added in the student's code */
    ADD
    , /** an entity, typically a method which was provided in the starter code is removed in the student's code */
    DELETE
    , /** an entity, typically a method which was provided in the starter code is revised in the student's code */
    MODIFY
    , /** directive to make no changes, additions, or deletions to the target entity */
    LEAVE_AS_IS
    , /** specifies a kind of language construct */
    USE
    , /** the target is a try block */
    TRY
    , /** the target is a try with resources block */
    TRY_WITH_RESOURCES
    , /** the target is a specific exception (throwable) */
    THROW
    , /** handles a specific exception (throwable) */
    CATCH
    , /** the target references the corresponding TRY or TRY_WITH_RESOURCES */
    FINALLY ;


   @Override
   public String toString()
       {

       return this.name().toLowerCase().replace( '_', ' ' ) ;

       }   // end toString()


   public static void main( String[] args )
       {

       for ( Action type : Action.values() )
           {
           System.out.printf( "%s -> %s%n", type.name(), type.toString() ) ;
           }

       }    // end main()

    }   // end enum Action
