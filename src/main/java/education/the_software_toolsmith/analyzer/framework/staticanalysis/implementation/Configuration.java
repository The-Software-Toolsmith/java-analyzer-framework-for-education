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


import com.github.javaparser.ParserConfiguration ;
import com.github.javaparser.StaticJavaParser ;
import com.github.javaparser.symbolsolver.JavaSymbolSolver ;
import com.github.javaparser.symbolsolver.resolution.typesolvers.CombinedTypeSolver ;
import com.github.javaparser.symbolsolver.resolution.typesolvers.JavaParserTypeSolver ;
import com.github.javaparser.symbolsolver.resolution.typesolvers.ReflectionTypeSolver ;

import java.nio.file.Path ;
import java.util.Objects ;


/**
 * configuration-related components for JavaParser
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-15 Initial implementation based on code from ChatGPT 5.2
 */
public class Configuration
    {

    /*
     * public API methods
     */


    /**
     * configure the JavaParser ecosystem
     *
     * @param sourceRoot
     *     if provided, JavaParser will be configured; if an empty array then only for the JDK; if a non-empty
     *     array, one or more base source paths for JavaParser to use to locate non-JDK components
     */
    public static void configureSolver( final Path... sourceRoot )
        {
        
        Objects.requireNonNull( sourceRoot, "sourceRoot" ) ;
        
        final ParserConfiguration config
                = new ParserConfiguration().setLanguageLevel( ParserConfiguration.LanguageLevel.JAVA_21 ) ;

        final CombinedTypeSolver solver = new CombinedTypeSolver() ;

        // JDK
        solver.add( new ReflectionTypeSolver() ) ;

        // project sources(s)
        for ( final Path path : sourceRoot )
            {
            solver.add( new JavaParserTypeSolver( /* path */ path.toFile().isDirectory()
                    ? path
                    : path.getParent() ) ) ;
            }

        Path srcPath = Path.of( "./src" ) ;
        Path srcMainJavaPath = Path.of( "./src/main/java" ) ;
        
        // let the symbol resolver know about all code in this project
        solver.add( new JavaParserTypeSolver( srcPath ) );
        solver.add( new JavaParserTypeSolver( srcMainJavaPath ) );

        config.setSymbolResolver( new JavaSymbolSolver( solver ) ) ;

        StaticJavaParser.setConfiguration( config ) ;

        }   // end configureSolver()

    }   // end class Configuration
