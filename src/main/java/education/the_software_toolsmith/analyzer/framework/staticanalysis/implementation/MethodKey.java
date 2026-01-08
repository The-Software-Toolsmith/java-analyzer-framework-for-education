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

import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;

import java.util.Objects ;

/**
 * represents method information in a form appropriate for use as a key
 * <p>
 * assertion: for any well-formed source code a MethodKey should be unique
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-14 Initial implementation
 * @version 1.0.1 2025-12-27 add placeholder Javadoc comments
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class MethodKey extends SharedState
    {

    /** PLACEHOLDER */
    private final MethodInfo methodOrConstructorInfo ;

    /** PLACEHOLDER */
    private final String baseClassName ;
    /** PLACEHOLDER */
    private final String resolvedClassName ;
    /** PLACEHOLDER */
    private final boolean isConstructor ;
    /** PLACEHOLDER */
    private final String methodSignature ;

    /** PLACEHOLDER */
    private final String keyified ;


    /**
     * 
     * set initial state to valid empty PLACEHOLDER
     *
     * @param methodInfo
     */
    private MethodKey( final MethodInfo methodInfo )
        {

        this.methodOrConstructorInfo = methodInfo ;

        this.baseClassName = methodInfo.getClassName() ;    // may be null
        this.resolvedClassName = this.baseClassName ;   // may be null /* IN_PROCESS */
        this.isConstructor = this.methodOrConstructorInfo.getIsConstructor() ;
        this.methodSignature = methodInfo.getCallableDeclaration().getSignature().asString() ;

        /* IN_PROCESS switch to resolved name so will be unique */
        this.keyified = keyify() ;

        }   // end standard constructor
    
    
    /**
     * 
     * PLACEHOLDER
     * 
     * @return
     */
    private String keyify()
        {

        return this.resolvedClassName + ( this.isConstructor
                ? ":constructor:"
                : ":method:" )
               + this.methodSignature ;

        }   // end keyify()


    /**
     * 
     * set initial state to valid empty PLACEHOLDER
     *
     * @param method
     */
    private MethodKey ( final String method )
        {
        
        this.methodOrConstructorInfo = null ;
        this.baseClassName = SharedState.className ;
        this.resolvedClassName = this.baseClassName ;
        this.isConstructor = false ;
        this.methodSignature = method ;
        this.keyified = keyify() ;
        
        }   // end fabricated constructor


    @Override
    public boolean equals( final Object object )
        {

        if ( this == object )
            {
            return true ;
            }

        if ( ! ( object instanceof final MethodKey otherMethodKey ) )
            {
            // includes null
            return false ;
            }

        return this.keyified.equals( otherMethodKey.keyified ) ;

        }   // end equals()


    /**
     * @return the class name
     */
    public String getBaseClassName()
        {

        return this.baseClassName ;

        }   // end getClassName()


    /**
     * @return the keyified key
     */
    public String getKeyified()
        {

        return this.keyified ;

        }   // end getKeyified()
    
    
    
    /**
     * @return the method info associated with this method key
     */
    public MethodInfo getMethodInfo()
        {

        return this.methodOrConstructorInfo ;

        }   // end getMethodInfo()


    /**
     * @return the method name
     */
    public String getMethodName()
        {

        return this.methodOrConstructorInfo.getMethodName() ;

        }   // end getMethodName()


    /**
     * @return the method signature
     */
    public String getMethodSignature()
        {

        return this.methodSignature ;

        }   // end getMethodSignature()


    @Override
    public int hashCode()
        {

        return Objects.hashCode( this.keyified ) ;

        }   // end hashCode()


    @Override
    public String toString()
        {

        return this.methodSignature ;

        }   // end toString()


    /**
     * 
     * PLACEHOLDER
     * 
     * @param methodInfo
     * @return
     */
    public static MethodKey from( final MethodInfo methodInfo )
        {

        return new MethodKey( methodInfo ) ;

        }   // end from (MethodInfo)


    /**
     * 
     * PLACEHOLDER
     * 
     * @param methodName
     * @return
     */
    public static MethodKey from( final String methodName )
        {

        return new MethodKey( methodName ) ;

        }   // end from (String)


    /**
     * 
     * PLACEHOLDER
     * 
     * @param methodInfo
     * @return
     */
    public static String keyFor( final MethodInfo methodInfo )
        {

        return from( methodInfo ).keyified ;

        }   // end keyFor()

    }   // end class MethodKey
