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


package education.the_software_toolsmith.analyzer.framework.staticanalysis.tools ;


import education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation.Requirement ;

import com.fasterxml.jackson.core.JsonProcessingException ;
import com.fasterxml.jackson.core.type.TypeReference ;
import com.fasterxml.jackson.databind.JsonMappingException ;
import com.fasterxml.jackson.databind.ObjectMapper ;

import java.io.File ;
import java.io.IOException ;
import java.nio.charset.StandardCharsets ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.util.List ;
import java.util.Map ;

/**
 * utilities to store and retrieve ASTs
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-24 initial implementation based on AstStore and code from ChatGPT 5.2
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class ImplementationComplianceStore
    {

    // ---- deserialize from String ----
    public static Map<String, List<Requirement>>
           fromJsonString( final String jsonText ) throws JsonMappingException, JsonProcessingException
        {

        final ObjectMapper mapper = new ObjectMapper() ;

        return mapper.readValue( jsonText, new TypeReference<>()
            {} ) ;

        }   // end fromJsonString()


    public static Map<String, List<Requirement>> load( final File file ) throws IOException
        {

        return load( file.toPath() ) ;

        }   // /end load() with File


    public static Map<String, List<Requirement>> load( final Path file ) throws IOException
        {

        final String json = Files.readString( file, StandardCharsets.UTF_8 ) ;
        return fromJsonString( json ) ;

        }   // end load() with Path


    public static Map<String, List<Requirement>> load( final String filename ) throws IOException
        {

        return load( new File( filename ) ) ;

        }   // end load() with String


    public static void save( final Map<String, List<Requirement>> mapOfRequirements,
                             final File file ) throws IOException
        {

        save( mapOfRequirements, file.toPath() ) ;

        }   // end save() with File


    public static void save( final Map<String, List<Requirement>> mapOfRequirements,
                             final Path file ) throws IOException
        {

        Files.createDirectories( file.getParent() ) ;
        Files.writeString( file, toJsonString( mapOfRequirements ), StandardCharsets.UTF_8 ) ;

        }   // end save() with Path


    // ---- file helpers ----
    public static void save( final Map<String, List<Requirement>> mapOfRequirements,
                             final String fileName ) throws IOException
        {

        save( mapOfRequirements, new File( fileName ) ) ;

        }   // end save() with String


    // ---- serialize to String ----
    public static String
           toJsonString( final Map<String,
                                   List<Requirement>> mapOfRequirements ) throws JsonProcessingException
        {

        final ObjectMapper mapper = new ObjectMapper() ;

        return mapper.writerWithDefaultPrettyPrinter().writeValueAsString( mapOfRequirements ) ;

        }   // end toJsonString()

    }   // end class ImplementationComplianceStore
