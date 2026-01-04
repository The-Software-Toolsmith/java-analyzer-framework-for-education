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


package education.the_software_toolsmith.analyzer.framework.utilities.lms.brightspace ;

import education.the_software_toolsmith.analyzer.framework.staticanalysis.tools.ExtractedSource ;

import static education.the_software_toolsmith.analyzer.framework.utilities.SharedState.* ;

import java.io.FileNotFoundException ;
import java.io.IOException ;
import java.io.InputStream ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.nio.file.StandardCopyOption ;
import java.util.ArrayList ;
import java.util.Comparator ;
import java.util.Enumeration ;
import java.util.List ;
import java.util.Locale ;
import java.util.Objects ;
import java.util.Optional ;
import java.util.zip.ZipEntry ;
import java.util.zip.ZipFile ;

/**
 * PLACEHOLDER
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-16 Initial implementation based on code from ChatGPT 5.2
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class BrightspaceZipExtract
    {

    // What you *want* them to have
    private static final String EXPECTED_SUFFIX = "/src/main/java/edu/wit/scds/ds/" + ADTPathSegment + "/" + className + ".java" ;


    private BrightspaceZipExtract()
        {}


    public static ExtractedSource extractADTFile(Path submissionFolder, Path tempDir)
            throws IOException
    {
        Objects.requireNonNull(submissionFolder);
        Objects.requireNonNull(tempDir);

        Path zipPath = findSingleZip(submissionFolder)
                .orElseThrow(() -> new FileNotFoundException("No .zip found in " + submissionFolder));

        Files.createDirectories(tempDir);

        try (ZipFile zip = new ZipFile(zipPath.toFile()))
        {
            // 1) exact expected location
            ZipEntry exact = findFirst(zip, name -> name.endsWith(EXPECTED_SUFFIX));
            if (exact != null)
            {
                return extractEntry(zip, exact, tempDir.resolve( className + ".java"));
            }

            // 2) fallback: any 'className'.java
            List<ZipEntry> matches = findAll(zip, name -> name.endsWith( "/" + className + ".java"));
            if (matches.isEmpty())
            {
                throw new FileNotFoundException( className + ".java not found inside " + zipPath);
            }

            List<ZipEntry> underMain = matches.stream()
                    .filter(e -> normalizedName(e).contains("/src/main/java/"))
                    .toList();

            if (underMain.size() == 1)
            {
                return extractEntry(zip, underMain.get(0), tempDir.resolve( className + ".java"));
            }

            // 3) ambiguous
            StringBuilder sb = new StringBuilder();
            sb.append("Ambiguous " + className + ".java in ").append(zipPath).append(System.lineSeparator());
            sb.append("Candidates:").append(System.lineSeparator());
            for (ZipEntry e : (underMain.isEmpty() ? matches : underMain))
            {
                sb.append("  - ").append(normalizedName(e)).append(System.lineSeparator());
            }
            sb.append("Ask student to resubmit with correct structure.");
            throw new IllegalStateException(sb.toString());
        }
    }

    // ----- helpers -----

    private static ExtractedSource extractEntry(ZipFile zip,
                                                ZipEntry entry,
                                                Path outFile) throws IOException
    {
        Files.createDirectories(outFile.getParent());
        try (InputStream in = zip.getInputStream(entry))
        {
            Files.copy(in, outFile, StandardCopyOption.REPLACE_EXISTING);
        }
        return new ExtractedSource(outFile, normalizedName(entry));
    }


    private static List<ZipEntry> findAll( final ZipFile zip,
                                           final java.util.function.Predicate<String> pred )
        {

        final List<ZipEntry> out = new ArrayList<>() ;
        final Enumeration<? extends ZipEntry> en = zip.entries() ;

        while ( en.hasMoreElements() )
            {
            final ZipEntry e = en.nextElement() ;

            if ( e.isDirectory() )
                {
                continue ;
                }

            final String name = normalizedName( e ) ;

            if ( pred.test( name ) )
                {
                out.add( e ) ;
                }

            }

        return out ;

        }


    private static ZipEntry findFirst( final ZipFile zip,
                                       final java.util.function.Predicate<String> pred )
        {

        final Enumeration<? extends ZipEntry> en = zip.entries() ;

        while ( en.hasMoreElements() )
            {
            final ZipEntry e = en.nextElement() ;

            if ( e.isDirectory() )
                {
                continue ;
                }

            final String name = normalizedName( e ) ;

            if ( pred.test( name ) )
                {
                return e ;
                }

            }

        return null ;

        }


    private static Optional<Path> findSingleZip( final Path folder ) throws IOException
        {

        try ( var s = Files.list( folder ) )
            {
            final List<Path> zips = s.filter( p -> p.getFileName()
                                                    .toString()
                                                    .toLowerCase( Locale.ROOT )
                                                    .endsWith( ".zip" ) )
                                     .toList() ;

            if ( zips.isEmpty() )
                {
                return Optional.empty() ;
                }

            if ( zips.size() == 1 )
                {
                return Optional.of( zips.get( 0 ) ) ;
                }

            // If they uploaded exactly one zip, this shouldn't happen; still pick the newest zip
            zips.sort( Comparator.comparingLong( p -> p.toFile().lastModified() ) ) ;
            return Optional.of( zips.get( zips.size() - 1 ) ) ;
            }

        }


    private static String normalizedName( final ZipEntry e )
        {

        return e.getName().replace( '\\', '/' ) ;

        }


    }   // end class BrightspaceZipExtract
