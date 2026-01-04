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

import java.lang.reflect.Field ;
import java.lang.reflect.Method ;
import java.util.Collection ;
import java.util.Map ;

/**
 * Utilities to access and test characteristics of reference types, methods, fields.
 *
 * @author Dave Rosenberg
 *
 * @version 1.0 2020-08-08 Initial implementation - extracted from DMRJUnitTestsBase.java
 * @version 1.1 2020-09-13 enhance {@code invoke()} to check both defined and generic parameter
 *     types when searching for a matching method
 * @version 1.2 2020-10-19 add circular array retrieval
 * @version 1.3 2021-04-11 repackage as canned utility testing suite
 * @version 1.4 2021-04-18
 *     <ul>
 *     <li>add instantiate() methods
 *     <li>add invoke() convenience method
 *     <li>enhance error reporting in invoke() and instantiate() to include parameter types
 *     </ul>
 * @version 1.4.1 2021-06-19 reflect move of other classes from {@code ...testing.junit} package to
 *     {@code ...testing} package
 * @version 1.5 2025-03-17 track method renaming in {@code TestData.java}
 * @version 1.6 2025-03-18 enhance {@code getContentsOfChainBackedCollection()} to anticipate
 *     {@code ArrayIndexOutOfBoundsException} and report a more meaningful state exception
 * @version 1.7 2025-06-14
 *     <ul>
 *     <li>enhance/fix {@code isCollection()} to check {@code Collection} and {@code Map}
 *     <li>add {@code isBridge()}
 *     <li>move {@code ArrayDimensions()} and {@code arrayOf()} here from {@code TestClassDMRTests}
 *     <li>add {@code getChainAsArray()} and {@code getChainAsList()}
 *     <li>deprecate {@code getContentsOfChainBackedCollection()}
 *     </ul>
 * @version 1.7.1 2025-07-18 swap operands to '==' and '!=' when comparing against a constant so the
 *     constant is the left operand
 * @version 2.0 2025-07-19 move all other specialized functionality to separate classes
 */
public class Reflection
    {


    /*
     * constructors
     */


    /**
     * prevent instantiation
     *
     * @since 2.0
     */
    private Reflection()
        {

        // noop

        }   // end no-arg constructor


    /**
     * Determine the number of dimensions of an array
     *
     * @param field
     *     the field to test
     *
     * @return the field's number of dimensions
     *     <p>
     *     note: if {@code field} isn't an array, the number of dimensions is 0
     *
     * @since 1.7
     */
    public static int arrayDimensions( final Field field )
        {

        int dimensions = 0 ;

        if ( isArray( field ) )
            {
            dimensions++ ;

            Class<?> type = field.getType().getComponentType() ;

            while ( isArray( type ) )
                {
                dimensions++ ;

                type = type.getComponentType() ;
                }

            }

        return dimensions ;

        }   // end arrayDimensions()


    /**
     * Determine the type of the components of an array
     *
     * @param field
     *     the field to test
     *
     * @return the field's component type
     *     <p>
     *     note: if {@code field} isn't an array, {@code field}'s type is returned
     *
     * @since 1.7
     */
    public static Class<?> arrayOf( final Field field )
        {

        Class<?> type = typeOf( field ) ;

        while ( isArray( type ) )
            {
            type = type.getComponentType() ;
            }

        return type ;

        }   // end arrayOf()


    /**
     * Determine if a field (instance or class variable) is of an annotation type
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is of an annotation type; false otherwise
     */
    public static boolean isAnnotation( final Field field )
        {

        return typeOf( field ).isAnnotation() ;

        }   // end isAnnotation()


    /**
     * Determine if a class is an array
     *
     * @param aClass
     *     the class to test
     *
     * @return true if {@code aClass} is an array class; false otherwise
     */
    public static boolean isArray( final Class<?> aClass )
        {

        return aClass.isArray() ;

        }   // end isArray()


    /**
     * Determine if a field (instance or class variable) is of an array type
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is of an array class type; false otherwise
     */
    public static boolean isArray( final Field field )
        {

        return isArray( field.getType() ) ;

        }   // end isArray()


    /**
     * Determine is a method is a bridge method
     *
     * @param method
     *     the method to test
     *
     * @return {@code true} if {@code method} is a bridge method; {@code false} otherwise
     *
     * @since 1.0
     */
    public static boolean isBridge( final Method method )
        {

        return method.isBridge() ;

        }   // end isBridge()


    /**
     * Determine if a field (instance or class variable) is of a class reference type
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is of a class reference type; false otherwise
     */
    public static boolean isClassReference( final Field field )
        {

        return typeOf( field ).toString().startsWith( "class" ) ;

        }   // end isClassReference()


    /**
     * Determine if a field (instance or class variable) is of a class reference type which is a
     * collection ({@code implements java.util.Collection} or is a subclass of a class which does)
     * or is a Map (not a subclass of Collection)
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is of a collection type; false otherwise
     */
    public static boolean isCollection( final Field field )
        {

        final boolean isCollection = Collection.class.isAssignableFrom( field.getType() ) ;
        final boolean isMap = Map.class.isAssignableFrom( field.getType() ) ;

        return isCollection || isMap ;

        }   // end isCollection()


    /**
     * Determine if a field (instance or class variable) is of an enumeration type
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is of an enumeration type; false otherwise
     */
    public static boolean isEnumeration( final Field field )
        {

        return typeOf( field ).isEnum() ;

        }   // end isEnumeration()


    /**
     * Determine if a field (instance or class variable) is of an interface reference type
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is of an interface reference type; false otherwise
     */
    public static boolean isInterfaceReference( final Field field )
        {

        return typeOf( field ).isInterface() ;

        }   // end isInterfaceReference()


    /**
     * Determine if a field (instance or class variable) is of a primitive type
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is of a primitive type; false otherwise
     */
    public static boolean isPrimitive( final Field field )
        {

        return typeOf( field ).isPrimitive() ;

        }   // end isPrimitive()


    /**
     * Determine if a field is a class variable
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is a class variable; false otherwise
     */
    public static boolean isStatic( final Field field )
        {

        final String[] parts = field.toGenericString().split( " " ) ;
        return switch ( parts[ 0 ] )
            {
            case "private", "protected", "public"
                -> "static".equals( parts[ 1 ] ) ;
            default
                -> "static".equals( parts[ 0 ] ) ;
            } ;

        }   // end isStatic()


    /**
     * Determine if a field is of a primitive type
     *
     * @param field
     *     the field to test
     *
     * @return true if {@code field} is of a primitive type; false otherwise
     */
    public static Class<?> primitiveTypeOf( final Field field )
        {

        Class<?> type = typeOf( field ) ;

        while ( type.isArray() )
            {
            type = type.getComponentType() ;
            }

        if ( !type.isPrimitive() )
            {
            type = null ;
            }

        return type ;

        }   // end primitiveTypeOf()


    /**
     * Determine the class of a field
     *
     * @param field
     *     the field to test
     *
     * @return the field's class
     */
    public static Class<?> typeOf( final Field field )
        {

        Class<?> type ;

        if ( isArray( field ) )
            {
            type = field.getType().getComponentType() ;
            }
        else
            {
            type = field.getType() ;
            }

        return type ;

        }   // end typeOf()


    /**
     * Determine the visibility of a field
     *
     * @param field
     *     the field to test
     *
     * @return the field's visibility
     */
    public static String visibilityOf( final Field field )
        {

        final String[] parts = field.toGenericString().split( " " ) ;
        return switch ( parts[ 0 ] )
            {
            case "private", "protected", "public"
                -> parts[ 0 ] ;
            default
                -> "package" ;
            } ;

        }   // end visibilityOf()

    }   // end class Reflection