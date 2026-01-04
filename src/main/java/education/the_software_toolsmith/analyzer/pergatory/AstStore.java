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


package education.the_software_toolsmith.analyzer.pergatory ;


import com.github.javaparser.ast.CompilationUnit ;
import com.github.javaparser.ast.Node ;
import com.github.javaparser.serialization.JavaParserJsonDeserializer ;
import com.github.javaparser.serialization.JavaParserJsonSerializer ;

import java.io.File ;
import java.io.IOException ;
import java.io.StringReader ;
import java.io.StringWriter ;
import java.nio.charset.StandardCharsets ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.util.Map ;

import jakarta.json.Json ;
import jakarta.json.JsonReader ;
import jakarta.json.stream.JsonGenerator ;
import jakarta.json.stream.JsonGeneratorFactory ;

/**
 * utilities to store and retrieve ASTs
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-14 Initial implementation based on code from ChatGPT 5.2
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class AstStore
    {

    // ---- deserialize from String ----
    public static CompilationUnit fromJsonString( final String jsonText )
        {

        final JavaParserJsonDeserializer deserializer = new JavaParserJsonDeserializer() ;

        try ( JsonReader reader = Json.createReader( new StringReader( jsonText ) ) )
            {
            final Node node = deserializer.deserializeObject( reader ) ;
            return (CompilationUnit) node ; // safe if you serialized a CompilationUnit
            }

        }


    public static CompilationUnit load( final File file ) throws IOException
        {

        return load( file.toPath() ) ;

        }


    public static CompilationUnit load( final Path file ) throws IOException
        {

        final String json = Files.readString( file, StandardCharsets.UTF_8 ) ;
        return fromJsonString( json ) ;

        }


    public static CompilationUnit load( final String filename ) throws IOException
        {

        return load( new File( filename ) ) ;

        }


    public static void save( final CompilationUnit cu,
                             final File file ) throws IOException
        {

        save( cu, file.toPath() ) ;

        }


    public static void save( final CompilationUnit cu,
                             final Path file ) throws IOException
        {

        Files.createDirectories( file.getParent() ) ;
        Files.writeString( file, serializeCu( cu ), StandardCharsets.UTF_8 ) ;

        }


    // ---- file helpers ----
    public static void save( final CompilationUnit cu,
                             final String fileName ) throws IOException
        {

        save( cu, new File( fileName ) ) ;

        }


    // ---- serialize to String ----
    public static String serializeCu( final CompilationUnit cu )
        {

        final JavaParserJsonSerializer serializer = new JavaParserJsonSerializer() ;

        final StringWriter out = new StringWriter() ;

        final JsonGeneratorFactory factory
                = Json.createGeneratorFactory( Map.of( JsonGenerator.PRETTY_PRINTING, true ) ) ;

        try ( JsonGenerator gen = factory.createGenerator( out ) )
            {
            serializer.serialize( cu, gen ) ;
            }

        return out.toString() ;

        }

    }   // end class AstStore
