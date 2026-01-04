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


package education.the_software_toolsmith.analyzer.incubator ;

import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;
import education.the_software_toolsmith.analyzer.pergatory.AstStore ;

import com.github.javaparser.ParserConfiguration ;
import com.github.javaparser.StaticJavaParser ;
import com.github.javaparser.ast.CompilationUnit ;

import java.io.File ;
import java.io.IOException ;

/**
 * test serialization and deserialization of JavaParser's CompilationUnits
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-14 Initial implementation
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt
 */
public final class TestSerialization extends SharedState
    {

    /**
     * test driver
     *
     * @param args
     *     -unused-
     *
     * @throws IOException
     *     for any file system error
     */
    public static void main( final String[] args ) throws IOException
        {

        final String solutionSourceFileName
                = "./to-grade/" + longAssignmentId + "/solution/" + className + ".java" ;
        final String solutionAstFileName
                = "./config/" + longAssignmentId + "/solution/" + className + ".ast" ;

        final String starterSourceFileName
                = "./to-grade/" + longAssignmentId + "/starter/" + className + ".java" ;
        final String starterAstFileName = "./config/" + longAssignmentId + "/starter/" + className + ".ast" ;

        // solution first
        StaticJavaParser.getParserConfiguration()
                        .setLanguageLevel( ParserConfiguration.LanguageLevel.JAVA_21 )
                        .setStoreTokens( true ) ;

        CompilationUnit compilationUnit = StaticJavaParser.parse( new File( solutionSourceFileName ) ) ;

        AstStore.save( compilationUnit, solutionAstFileName ) ;


        // then starter code
        compilationUnit = StaticJavaParser.parse( new File( starterSourceFileName ) ) ;

        AstStore.save( compilationUnit, starterAstFileName ) ;


        // ta-da
        System.out.printf( "done%n" ) ;

        }    // end main()

    }   // end class TestSerialization
