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

import com.github.javaparser.ast.body.CallableDeclaration ;
import com.github.javaparser.ast.expr.MethodCallExpr ;
import com.github.javaparser.ast.expr.ObjectCreationExpr ;
import com.github.javaparser.ast.stmt.ExplicitConstructorInvocationStmt ;
import com.github.javaparser.resolution.UnsolvedSymbolException ;
import com.github.javaparser.resolution.declarations.ResolvedConstructorDeclaration ;
import com.github.javaparser.resolution.declarations.ResolvedMethodDeclaration ;

import java.util.List ;
import java.util.Objects ;

/**
 * determine Java language usage in a compilation unit
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-06 Initial implementation based on code from ChatGPT 5.1
 * @version 1.1 2025-12-07 create {@code usesGetNext} to replace {@code NodeTraversalDetector}
 * @version 1.2 2025-12-14
 *     <ul>
 *     <li>add {@code callsMethodNamed()} to start move toward integration with rest of framework
 *     <li>add Javadoc comments
 *     </ul>
 * @version 1.5 2025-12-15
 *     <ul>
 *     <li>implement matching a method invocation using the method's signature
 *     <li>add methods to return the number of times a method is called
 *     </ul>
 * @version 1.6 2025-12-25 add support for constructor chaining
 * @version 1.7 2025-12-26 add support for instantiation
 */
public class Heuristics
    {


    /**
     * determine if one constructor invokes another
     *
     * @param callableDeclaration
     *     the calling constructor
     * @param constructorSignature
     *     the signature of the constructor to call
     *
     * @return {@code true} if we found a match; {@code false} otherwise
     */
    public static boolean callsConstructorWithSignature( final CallableDeclaration<?> callableDeclaration,
                                                         final String constructorSignature )
        {

        Objects.requireNonNull( callableDeclaration, "callableDeclaration" ) ;
        Objects.requireNonNull( constructorSignature, "constructorSignature" ) ;

        final List<ExplicitConstructorInvocationStmt> explicitConstructorInvocationStatements
                = callableDeclaration.findAll( ExplicitConstructorInvocationStmt.class ) ;

        if ( explicitConstructorInvocationStatements.size() > 1 )
            {
            // can't have more than one chained constructor call
            throw new IllegalStateException( String.format( "multiple (%,d) chained constructor calls from %s to %s",
                                                            callableDeclaration.getSignature().toString(),
                                                            explicitConstructorInvocationStatements ) ) ;
            }

        if ( explicitConstructorInvocationStatements.size() == 0 )
            {
            // not found
            return false ;
            }

        // assertion: there is one chained constructor invocation

        try
            {
            final ResolvedConstructorDeclaration resolvedConstructorDeclaration
                    = explicitConstructorInvocationStatements.getFirst().resolve() ;

            if ( normalize( constructorSignature ).equals( normalize( resolvedConstructorDeclaration.getSignature() ) ) )
                {
                // found a match
                return true ;
                }

            }
        catch ( UnsolvedSymbolException
                | UnsupportedOperationException e )
            {
            }

        // not a match
        return false ;

        }   // end callsConstructorWithSignature()


    /**
     * determine if one method is invoked by another
     *
     * @param callableDeclaration
     *     the calling method
     * @param methodName
     *     the name of the method to call
     *
     * @return {@code true} if the named method is called at least once; {@code false} otherwise
     */
    public static boolean callsMethodNamed( final CallableDeclaration<?> callableDeclaration,
                                            final String methodName )
        {

        // the number of methods is likely to be small so checking the number of matches is acceptable
        return timesMethodNamedCalled( callableDeclaration, methodName ) >= 1 ;

        }   // end callsMethodNamed()


    /**
     * determine if one method is invoked by another
     *
     * @param callableDeclaration
     *     the calling method
     * @param methodSignature
     *     the signature of the method to call
     *
     * @return {@code true} if the named method is called at least once; {@code false} otherwise
     */
    public static boolean callsMethodWithSignature( final CallableDeclaration<?> callableDeclaration,
                                                    final String methodSignature )
        {

        // the number of methods is likely to be small so checking the number of matches is acceptable
        return timesMethodWithSignatureCalled( callableDeclaration, methodSignature ) >= 1 ;

        }   // end callsMethodWithSignature()


    /**
     * determine the number of times one method is invoked by another
     *
     * @param callableDeclaration
     *     the calling method
     * @param methodName
     *     the name of the method to call
     *
     * @return the number of times the named method is called
     */
    public static int timesMethodNamedCalled( final CallableDeclaration<?> callableDeclaration,
                                              final String methodName )
        {

        Objects.requireNonNull( callableDeclaration, "callableDeclaration" ) ;
        Objects.requireNonNull( methodName, "methodName" ) ;

        return callableDeclaration.findAll( MethodCallExpr.class,
                                            methodCallExpression -> methodName.equals( methodCallExpression.getNameAsString() ) )
                                  .size() ;

        }   // end timesMethodNamedCalled()


    /**
     * determine the number of times one method is invoked by another
     *
     * @param callableDeclaration
     *     the calling method
     * @param methodSignature
     *     the signature of the method to call
     *
     * @return the number of times the method with the specified signature is called
     */
    public static int timesMethodWithSignatureCalled( final CallableDeclaration<?> callableDeclaration,
                                                      final String methodSignature )
        {

        Objects.requireNonNull( callableDeclaration, "callableDeclaration" ) ;
        Objects.requireNonNull( methodSignature, "methodSignature" ) ;

        final String expectedMethodSignature = normalize( methodSignature ) ;

        return callableDeclaration.findAll( MethodCallExpr.class, methodCallExpression ->
            {
            ResolvedMethodDeclaration resolvedMethodCallExpression = null ;
            String actualMethodSignature = null ;

            try
                {
                resolvedMethodCallExpression = methodCallExpression.resolve() ;

                // Typically "add(T[])" / "add(Object[])" etc.
                actualMethodSignature = normalize( resolvedMethodCallExpression.getSignature() ) ;

                final boolean matches = expectedMethodSignature.equals( actualMethodSignature ) ;

                return matches ;
                }
            catch ( UnsolvedSymbolException
                    | UnsupportedOperationException e )
                {
                // resolution failed for this call; treat as not a match
                return false ;
                }

            } ).size() ;

        }   // end timesMethodWithSignatureCalled()
    

    /**
     * determine if one method instantiates a class using the constructor with a specific signature
     *
     * @param callableDeclaration
     *     the calling method
     * @param constructorSignature
     *     the signature of the constructor to use
     *
     * @return {@code true} if the named method is called at least once; {@code false} otherwise
     */
    public static boolean instantiatesClassWithSignature( final CallableDeclaration<?> callableDeclaration,
                                                          final String constructorSignature )
        {

        // the number of instantiations is likely to be small so checking the number of matches is acceptable
        return timesClassInstantiatedWithSignatureCalled( callableDeclaration, constructorSignature ) >= 1 ;

        }   // end instantiatesClassWithSignature()


    /**
     * determine the number of times a specific constructor is invoked
     *
     * @param callableDeclaration
     *     the calling method
     * @param constructorSignature
     *     the signature of the constructor to use
     *
     * @return the number of times the method with the specified signature is called
     */
    public static int
           timesClassInstantiatedWithSignatureCalled( final CallableDeclaration<?> callableDeclaration,
                                                      final String constructorSignature )
        {

        Objects.requireNonNull( callableDeclaration, "callableDeclaration" ) ;
        Objects.requireNonNull( constructorSignature, "constructorSignature" ) ;

        final String expectedMethodSignature = normalize( constructorSignature ) ;

        return callableDeclaration.findAll( ObjectCreationExpr.class, objectCreationExpression ->
            {
            ResolvedConstructorDeclaration resolvedConstructorDeclaration = null ;
            String actualConstructorSignature = null ;

            try
                {
                resolvedConstructorDeclaration = objectCreationExpression.resolve() ;

                actualConstructorSignature = normalize( resolvedConstructorDeclaration.getSignature() ) ;

                final boolean matches = expectedMethodSignature.equals( actualConstructorSignature ) ;

                return matches ;
                }
            catch ( UnsolvedSymbolException
                    | UnsupportedOperationException e )
                {
                // resolution failed for this call; treat as not a match
                return false ;
                }

            } ).size() ;

        }   // end timesObjectInstantiatedWithSignatureCalled()


    /**
     * massage a basic method signature string into a form appropriate for symbol solver so we can identify
     * method invocations of specific overloaded methods
     *
     * @param methodSignature
     *     the basic method signature
     *
     * @return normalized form of the method signature
     */
    private static String normalize( final String methodSignature )
        {

        // Keep it simple for your specific need: "add(T)" vs "add(T[])" and tolerate erasure by mapping any
        // non-array -> "*", any array -> "[]".
        final int leftParenIndex = methodSignature.indexOf( '(' ) ;
        final int rightParenIndex = methodSignature.lastIndexOf( ')' ) ;

        if ( ( leftParenIndex < 0 ) || ( rightParenIndex < leftParenIndex ) )
            {
            return methodSignature.trim() ;
            }

        String methodName = methodSignature.substring( 0, leftParenIndex ) ;
        final int endOfPackageIndex = methodName.lastIndexOf( '.' ) ;

        if ( endOfPackageIndex >= 0 )
            {
            methodName = methodName.substring( endOfPackageIndex + 1 ) ;
            }

        methodName = methodName.trim() ;

        final String parameterComponent
                = methodSignature.substring( leftParenIndex + 1, rightParenIndex ).trim() ;

        if ( parameterComponent.isEmpty() )
            {
            return methodName + "()" ;
            }

        final String[] parameters = parameterComponent.split( "\\s*,\\s*" ) ;
        final StringBuilder normalizedForm = new StringBuilder( methodName ).append( '(' ) ;

        for ( int i = 0 ; i < parameters.length ; i++ )
            {
            final String parameter = parameters[ i ].trim() ;
            normalizedForm.append( parameter.endsWith( "[]" )
                    ? "[]"
                    : "*" ) ;

            if ( ( i + 1 ) < parameters.length )
                {
                normalizedForm.append( ',' ) ;
                }

            }

        normalizedForm.append( ')' ) ;
        return normalizedForm.toString() ;

        }   // end normalize()

    }   // end class Heuristics
