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


package education.the_software_toolsmith.analyzer.framework.utilities ;

import static education.the_software_toolsmith.analyzer.framework.utilities.SharedState.ADTPathSegment ;
import static education.the_software_toolsmith.analyzer.framework.utilities.SharedState.className ;

import java.io.FileNotFoundException ;
import java.io.IOException ;
import java.io.InputStream ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.nio.file.StandardCopyOption ;
import java.util.Enumeration ;
import java.util.Locale ;
import java.util.Optional ;
import java.util.zip.ZipEntry ;
import java.util.zip.ZipFile ;
import java.util.zip.ZipInputStream ;

/**
 * zip file utility methods
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-16 Initial implementation based on code from ChatGPT 5.2
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class ZipUtils
    {
    // DMR TODO remove specific path/files -> parameterize

    /** prevent instantiation */
    private ZipUtils()
        {}


    /**
     * PLACEHOLDER
     *
     * @param zipFile
     * @param destDir
     *
     * @throws IOException
     */
    public static void unzip( final Path zipFile,
                              final Path destDir ) throws IOException
        {

        Files.createDirectories( destDir ) ;

        try ( ZipInputStream zis = new ZipInputStream( Files.newInputStream( zipFile ) ) )
            {
            ZipEntry e ;

            while ( ( e = zis.getNextEntry() ) != null )
                {
                final String name = e.getName().replace( '\\', '/' ) ;

                if ( isNoiseZipPath( name ) )
                    {
                    zis.closeEntry() ;
                    continue ;
                    }

                // basic zip-slip guard
                final Path out = destDir.resolve( name ).normalize() ;

                if ( ! out.startsWith( destDir.normalize() ) )
                    {
                    throw new IOException( "Zip entry escapes dest dir: " + name ) ;
                    }

                if ( e.isDirectory() )
                    {
                    Files.createDirectories( out ) ;
                    }
                else
                    {
                    Files.createDirectories( out.getParent() ) ;

                    try
                        {
                        Files.copy( zis, out, StandardCopyOption.REPLACE_EXISTING ) ;
                        }
                    catch ( final IOException ioe )
                        {

                        if ( ( out.toString().length() <= 240 ) || out.toString().endsWith( ".java" ) )
                            {
                            throw ioe ; // important file or unknown failure
                            }

                        // log.printf("WARNING: skipped long path: %s%n", out);
                        /* DMR FIXME */ System.out.printf( "WARNING: skipped long path: %s%n", out ) ;
                        // keep going
                        }

                    }

                zis.closeEntry() ;
                }

            }

        }   // end unzip()


    /**
     * PLACEHOLDER
     *
     * @param name
     *
     * @return
     */
    private static boolean isNoiseZipPath( final String name )
        {

        // name is already normalized to forward slashes
        return name.startsWith( "target/" ) || name.contains( "/target/" ) || name.startsWith( "bin/" )
               || name.contains( "/bin/" ) || name.startsWith( "test-logs/" )
               || name.contains( "/test-logs/" ) || name.contains( "_MACOSX/" )
               || name.contains( ".DS_STORE" ) ;

        }   // end isNoiseZipPath()


    /**
     * PLACEHOLDER
     *
     * @param zipPath
     * @param tempDir
     *
     * @return
     *
     * @throws IOException
     */
    public static Path extractLinkedBagFromZip( final Path zipPath,
                                                final Path tempDir ) throws IOException
        {

        Files.createDirectories( tempDir ) ;

        try ( ZipFile zip = new ZipFile( zipPath.toFile() ) )
            {
            // Adjust if your path differs
//            final String wantedSuffix = "/src/main/java/edu/wit/scds/ds/bags/LinkedBag.java" ;
            final String wantedSuffix
                    = "/src/main/java/edu/wit/scds/ds/" + ADTPathSegment + "/" + className + ".java" ;

            ZipEntry match = null ;
            final Enumeration<? extends ZipEntry> e = zip.entries() ;

            while ( e.hasMoreElements() )
                {
                final ZipEntry ze = e.nextElement() ;
                final String name = ze.getName().replace( '\\', '/' ) ;

                if ( ! ze.isDirectory() && name.endsWith( wantedSuffix ) )
                    {
                    match = ze ;
                    break ;
                    }

                }

            if ( match == null )
                {
                throw new FileNotFoundException( className + ".java not found in " + zipPath ) ;
                }

            final Path out = tempDir.resolve( className + ".java" ) ;

            try ( InputStream in = zip.getInputStream( match ) )
                {
                Files.copy( in, out, StandardCopyOption.REPLACE_EXISTING ) ;
                }

            return out ;
            }

        }   // end extractLinkedBagFromZip()


    /**
     * PLACEHOLDER
     *
     * @param folder
     *
     * @return
     *
     * @throws IOException
     */
    public static Optional<Path> findFirstZip( final Path folder ) throws IOException
        {

        try ( var s = Files.list( folder ) )
            {
            return s.filter( p -> p.getFileName().toString().toLowerCase( Locale.ROOT ).endsWith( ".zip" ) )
                    .findFirst() ;
            }

        }   // end findFirstZip()

    }   // end class ZipUtils
