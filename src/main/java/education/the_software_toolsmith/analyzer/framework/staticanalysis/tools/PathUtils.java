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


import java.nio.file.Path ;
import java.util.Iterator ;
import java.util.List ;

/**
 * path-related utilities
 *
 * @author David M Rosenberg from ChatGPT 5.1
 *
 * @version 1.0 2025-12-06 Initial implementation
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt (nothing changed)
 */
public final class PathUtils
    {

    /** prevent instantiation */
    private PathUtils()
        {}


    /**
     * Computes the deepest common parent directory for the given paths.
     *
     * @param javaFiles
     *     the paths to analyze
     *
     * @return absolute normalized common directory as a String. If no common
     *     parent exists (e.g., different drives), returns the empty string.
     */
    public static String computeBaseDirFor( final List<Path> javaFiles )
        {

        if ( ( javaFiles == null ) || javaFiles.isEmpty() )
            {
            throw new IllegalArgumentException( "paths must not be null or empty" ) ;
            }

        if ( javaFiles.size() == 1 )
            {
            final Path javaFile = javaFiles.getFirst() ;
            final Path parent = normalize( javaFile.getParent() ) ;
            final String fileName = javaFile.getFileName().toString() ;

            if ( fileName.endsWith( ".java" ) )
                {
                return parent.toString() ;
                }

            }

        final Iterator<Path> pathIterator = javaFiles.iterator() ;
        Path common = normalize( pathIterator.next() ) ;

        while ( pathIterator.hasNext() )
            {
            common = commonParent( common, normalize( pathIterator.next() ) ) ;

            if ( common == null )
                {
                // no common parent — return empty
                return "" ;
                }

            }

        return common.toString() ;

        }   // end computeBaseDirFor()


    /**
     * normalize a path
     *
     * @param originalPath
     *     the original path
     *
     * @return the normalized path
     */
    private static Path normalize( final Path originalPath )
        {

        return originalPath.toAbsolutePath().normalize() ;

        }   // end normalize()


    /**
     * Returns the deepest common parent directory of two normalized absolute
     * paths.
     *
     * @param oneNormalizedPath
     *     one of two normalized paths
     * @param anotherNormalizedPath
     *     the second of two normalized paths
     *
     * @return {@code null} if there is no common parent (e.g., Windows C:\ vs
     *     D:\).
     */
    public static Path commonParent( final Path oneNormalizedPath,
                                     final Path anotherNormalizedPath )
        {

        // If drives/root don't match, no common parent (Windows case)
        if ( !oneNormalizedPath.getRoot().equals( anotherNormalizedPath.getRoot() ) )
            {
            return null ;
            }

        final int minCount =
                Math.min( oneNormalizedPath.getNameCount(), anotherNormalizedPath.getNameCount() ) ;
        int i = 0 ;

        while ( ( i < minCount )
                && oneNormalizedPath.getName( i ).equals( anotherNormalizedPath.getName( i ) ) )
            {
            i++ ;
            }

        if ( i == 0 )
            {
            // Only root is shared ("/" on Unix, "C:\" on Windows)
            return oneNormalizedPath.getRoot() ;
            }   // end commonParent()

        return oneNormalizedPath.getRoot().resolve( oneNormalizedPath.subpath( 0, i ) ) ;

        }

    }   // end class PathUtils
