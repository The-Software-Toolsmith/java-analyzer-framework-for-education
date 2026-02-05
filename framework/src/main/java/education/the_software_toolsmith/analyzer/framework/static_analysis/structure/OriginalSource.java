/* @formatter:off
 *
 * Copyright © 2025-2026 David M Rosenberg, The Software Toolsmith
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


package education.the_software_toolsmith.analyzer.framework.static_analysis.structure ;

import com.github.javaparser.Range ;
import com.github.javaparser.ast.body.CallableDeclaration ;

import java.util.List ;


/**
 * retrieves the source code for a method
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-26 Initial implementation based on code from ChatGPT 5.2
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class OriginalSource
    {

    public static String getOriginalMethodSource( final Result result,
                                                  final MethodKey methodKey,
                                                  final CodeSourceCategory codeSourceCategory )
        {

        final MethodInfo methodInfo = result.methods.get( codeSourceCategory ).get( methodKey ) ;
        final List<String> lines = result.sourceCode.get( codeSourceCategory ) ;

        CallableDeclaration<?> callableDeclaration  = null ;
        if ( methodInfo == null )
            {
            // typically because the method was deleted
            return String.format( "[source code is not available]%n" ) ;
            }

        callableDeclaration= methodInfo.getCallableDeclaration() ;

        if ( callableDeclaration.getRange().isEmpty() )
            {
            return String.format( "[source code is not available]%n" ) ;
            }

        final Range range = callableDeclaration.getRange().get() ;
        final int startLine = range.begin.line ;
        final int endLine = range.end.line ;


        final StringBuilder stringBuilder = new StringBuilder() ;

        for ( int line = startLine ; ( line <= endLine ) && ( line <= lines.size() ) ; line++ )
            {
            stringBuilder.append( String.format( "%,5d: %s%n", line, lines.get( line - 1 ) ) ) ;
            }

        return stringBuilder.toString() ;

        }   // end getOriginalMethodSource()

    }   // end class OriginalSource
