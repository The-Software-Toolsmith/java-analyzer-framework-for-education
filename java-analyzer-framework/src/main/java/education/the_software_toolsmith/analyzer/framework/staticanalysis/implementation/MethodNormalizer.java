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

import static com.github.javaparser.printer.configuration.DefaultPrinterConfiguration.ConfigOption.PRINT_COMMENTS ;
import static com.github.javaparser.printer.configuration.DefaultPrinterConfiguration.ConfigOption.PRINT_JAVADOC ;

import com.github.javaparser.ast.body.CallableDeclaration ;
import com.github.javaparser.ast.body.ConstructorDeclaration ;
import com.github.javaparser.ast.body.MethodDeclaration ;
import com.github.javaparser.printer.DefaultPrettyPrinter ;
import com.github.javaparser.printer.configuration.DefaultConfigurationOption ;
import com.github.javaparser.printer.configuration.DefaultPrinterConfiguration ;

@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public class MethodNormalizer
    {

    private static final DefaultPrinterConfiguration printerConfiguration
            = new DefaultPrinterConfiguration() ;
    static
        {
        printerConfiguration.removeOption( new DefaultConfigurationOption( PRINT_COMMENTS ) )
                            .removeOption( new DefaultConfigurationOption( PRINT_JAVADOC ) ) ;
        }

    private static final DefaultPrettyPrinter printer = new DefaultPrettyPrinter( printerConfiguration ) ;


    public static String normalizedBody( final CallableDeclaration<?> callableDeclaration )
        {

        if ( callableDeclaration instanceof final MethodDeclaration methodDeclaration )
            {
            final String nearlyDone = methodDeclaration.getBody().map( body -> printer.print( body ) ).orElse( "" ) ;

            // 🔹 canonicalize whitespace
            return nearlyDone.replaceAll( "\\s+", " " ).trim() ;
            }

        if ( callableDeclaration instanceof final ConstructorDeclaration constructorDeclaration )
            {
            final String nearlyDone = printer.print( constructorDeclaration.getBody() ) ;

            // 🔹 canonicalize whitespace
            return nearlyDone.replaceAll( "\\s+", " " ).trim() ;
//            return constructorDeclaration.getBody().toString() ;
            }

        throw new IllegalStateException( String.format( "unexpected type of %s",
                                                        callableDeclaration.getClass().getSimpleName() ) ) ;

        }   // end normalizedBody()

    }   // end class MethodNormalizer
