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

import com.github.javaparser.ast.NodeList ;
import com.github.javaparser.ast.body.CallableDeclaration ;
import com.github.javaparser.ast.body.ConstructorDeclaration ;
import com.github.javaparser.ast.body.MethodDeclaration ;
import com.github.javaparser.ast.stmt.Statement ;

import java.util.List ;

/**
 * represent the definition of a method or constructor
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-07 Initial implementation based on code provided by ChatGPT 5.1
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class MethodInfo
    {

    private final String methodName ;
    private final List<String> paramTypes ;
    private final String returnType ;  // may be null/empty for constructors
    private final NodeList<Statement> body ;
    private final boolean isConstructor ;
    private final CallableDeclaration<?> callableDeclaration ;
    private final String className ;    // may be null
    private final MethodKey methodKey ;

    private Compliance isCompliant ;


    /**
     * set initial state to represent a method or constructor
     */
    public MethodInfo( final String name,
                       final List<String> parameterTypes,
                       final String methodReturnType,
                       final NodeList<Statement> methodBody,
                       final boolean isAConstructor,
                       final CallableDeclaration<?> methodOrConstructor,
                       final String classNameText,
                       final Compliance isMethodCompliant )
        {

        this.methodName = name ;
        this.paramTypes = List.copyOf( parameterTypes ) ;
        this.returnType = methodReturnType ;
        this.body = methodBody ;
        this.isConstructor = isAConstructor ;
        this.callableDeclaration = methodOrConstructor ;
        this.className = classNameText ;
        this.isCompliant = isMethodCompliant ;

        this.methodKey = MethodKey.from( this ) ;

        }   // end constructor


    // equals/hashCode should use name + paramTypes + isConstructor
    @Override
    public boolean equals( final Object object )
        {

        if ( this == object )
            {
            return true ;
            }

        if ( object instanceof final MethodInfo otherMethodInfo )
            {
            return this.methodKey.equals( otherMethodInfo.methodKey ) ;
            }

        return false ;

        }   // end equals()


    /**
     * @return the body
     */
    public NodeList<Statement> getBody()
        {

        return this.body ;

        }   // end getBody()


    /**
     * @return the methodOrConstructorDeclaration
     */
    public CallableDeclaration<?> getCallableDeclaration()
        {

        return this.callableDeclaration ;

        }   // end getMethodOrConstructorDeclaration()


    /**
     * @return the className
     */
    public String getClassName()
        {

        return this.className ;

        }   // getClassName()


    /**
     * @return the compliance test result
     */
    public Compliance getCompliance()
        {

        return this.isCompliant ;

        }   // end getCompliance()


    /**
     * @return the isConstructor flag
     */
    public boolean getIsConstructor()
        {

        return this.isConstructor ;

        }   // end getIsConstructor()


    /**
     * @return the method key
     */
    public MethodKey getMethodKey()
        {

        return this.methodKey ;

        }   // end getMethodKey()


    /**
     * @return the method name
     */
    public String getMethodName()
        {

        return this.methodName ;

        }   // end getMethodName()


    public String getMethodSignature()
        {

        return this.callableDeclaration.getSignature().asString() ;

        }   // end getMethodSignature()


    /**
     * @return the paramTypes
     */
    public List<String> getParamTypes()
        {

        return this.paramTypes ;

        }   // end getParamTypes()


    /**
     * @return the returnType
     */
    public String getReturnType()
        {

        return this.returnType ;

        }   // end getReturnType()


    @Override
    public int hashCode()
        {

        return this.methodKey.hashCode() ;

        }   // end hashCode()


    /**
     * set the compliance test result
     *
     * @param newCompliance
     *     compliance test result
     *
     * @return the prior compliance value
     */
    public Compliance setCompliance( final Compliance newCompliance )
        {

        final Compliance savedCompliance = this.isCompliant ;
        this.isCompliant = newCompliance ;

        return savedCompliance ;

        }   // end setCompliance()


    @Override
    public String toString()
        {

        return String.format( "%s: [%s] %s %s%s; %s: %s",
                              this.isConstructor
                                      ? "c"
                                      : "m",
                              this.className,
                              this.returnType,
                              this.methodName,
                              this.paramTypes,
                              this.methodKey,
                              this.isCompliant ) ;

        }   // end toString()


    /**
     * PLACEHOLDER
     *
     * @param className
     * @param constructorDeclaration
     *
     * @return
     */
    public static MethodInfo fromConstructor( final String className,
                                              final ConstructorDeclaration constructorDeclaration )
        {

        return new MethodInfo( constructorDeclaration.getNameAsString(), // constructor name = class
                               // name
                               constructorDeclaration.getParameters()
                                                      .stream()
                                                      .map( parameters -> parameters.getType().asString() )
                                                      .toList(),
                               null,                 // or "void" or "" – but mark isConstructor=true
                               constructorDeclaration.getBody().getStatements(), // might throw
                               // NoSuchElementException
                               true,
                               constructorDeclaration,
                               className,
                               Compliance.UNKNOWN ) ;

        }   // end fromConstructor()


    /**
     * PLACEHOLDER
     *
     * @param className
     * @param methodDeclaration
     *
     * @return
     */
    public static MethodInfo fromMethod( final String className,
                                         final MethodDeclaration methodDeclaration )
        {

        return new MethodInfo( methodDeclaration.getNameAsString(),
                               methodDeclaration.getParameters()
                                                .stream()
                                                .map( parameters -> parameters.getType().asString() )
                                                .toList(),
                               methodDeclaration.getType().asString(),
                               methodDeclaration.getBody().get().getStatements(),
                               // might throw NoSuchElementException
                               false,
                               methodDeclaration,
                               className,
                               Compliance.UNKNOWN ) ;

        }   // end fromMethod()

    }   // end class MethodInfo
