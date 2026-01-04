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

import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.TestingBase.* ;

import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;
import education.the_software_toolsmith.analyzer.framework.utilities.ZipUtils ;
import education.the_software_toolsmith.analyzer.framework.utilities.lms.brightspace.BrightspaceUtilities ;

import java.io.File ;
import java.io.FileNotFoundException ;
import java.io.IOException ;
import java.io.UncheckedIOException ;
import java.nio.file.FileSystems ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.nio.file.PathMatcher ;
import java.nio.file.Paths ;
import java.time.LocalDateTime ;
import java.util.ArrayList ;
import java.util.Comparator ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Map ;
import java.util.Optional ;
import java.util.stream.Collectors ;
import java.util.stream.Stream ;


/**
 * PLACEHOLDER
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-15 Initial implementation based on code from ChatGPT 5.2
 * @version 2.0 2025-12-17 first pass quick and dirty mods to handle any adt
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class BatchRunner extends SharedState
    {

    /**
     * <pre>
     *
     * Args:
     *   0 = student root folder (contains many student project folders or files)
     *   1 = glob or filename (e.g., "{className}.java" or "**<pre>
     * </pre>
     * 
     * /{className}.java") 2 = output logs folder 3 = project root (the project that contains your
     * tests/tools) Example: java ... BatchRunner C:\to-grade {className}.java C:\logs C:\autograder
     * </pre>
     */
    public static void main( final String[] args ) throws Exception
        {

        System.out.printf( "args  to main():%n" ) ;

        for ( final String arg : args )
            {
            System.out.printf( "%s%n", arg ) ;
            }

        System.out.printf( "-------------%n%n" ) ;

        if ( args.length < 4 )
            {
            System.err.println( "Usage: BatchRunner <studentRoot> <pattern> <logRoot> <projectRoot>" ) ;
            System.exit( 2 ) ;
            }

//        final Path studentRoot = Paths.get( args[ 0 ] ).toAbsolutePath().normalize() ;
        @SuppressWarnings( "unused" )
        final String pattern = args[ 1 ] ;
//        final Path logRoot = Paths.get( args[ 2 ] ).toAbsolutePath().normalize() ;
        final Path projectRoot = Paths.get( args[ 3 ] ).toAbsolutePath().normalize() ;
//
//        Files.createDirectories( logRoot ) ;

        final Path toGradeRoot = Paths.get( args[ 0 ] ) ;          // studentRoot
        final Path gradedRoot = toGradeRoot.getParent().resolve( "graded" ) ;

        Files.createDirectories( gradedRoot ) ;

//
//        final List<Path> studentFiles = findStudentFiles( toGradeRoot, pattern ) ;
//        studentFiles.sort( Comparator.comparing( p -> p.toString().toLowerCase( Locale.ROOT ) ) ) ;

        // find the most recent submission if a student or project has any resubmissions
        final Map<String, Submission> latest = new HashMap<>() ;

        try ( var stream = Files.list( toGradeRoot ) )
            {
            stream.filter( Files::isDirectory ).forEach( dir ->
                {
                parseSubmissionFolder( dir ).ifPresent( sub ->
                    {
                    final String key = sub.studentId() + "-" + sub.assignmentId() ;
                    latest.merge( key,
                                  sub,
                                  ( a,
                                    b ) -> b.submittedAt().isAfter( a.submittedAt() )
                                            ? b
                                            : a ) ;
                    } ) ;
                } ) ;
            }

        final int uniqueSubmissionCount = latest.size() ;
//        System.out.println( "Found " + studentFiles.size() + " student " + className + ".java files." ) ;
        System.out.println( "Found " + uniqueSubmissionCount + " distinct student submissions files." ) ;

        int submissionCounter = 0 ;

        for ( final Submission sub : latest.values() )
            {
            submissionCounter++ ;

            // Progress to real console
            System.out.println( "------------------------------------------------------------" ) ;

            System.out.printf( "%,d:%n", submissionCounter ) ;
            System.out.println( "Student: " + sub.studentId() + " " + sub.displayName() ) ;
            System.out.println( "Picked submission: " + sub.folder().getFileName() ) ;


            // create the graded/output folder with a folder name that matches the to-grade folder
            Path studentOutDir ;
            Path zip ;
            Path extractedProjectDir ;

            try
                {
                studentOutDir = gradedRoot.resolve( sub.folder().getFileName().toString() ) ;
                Files.createDirectories( studentOutDir ) ;


                // 1) locate zip
                zip = findZipInSubmissionFolder( sub.folder() ) ;


                // Progress to real console
                System.out.println( "Zip file used: " + zip.toString() ) ;


                // 2) unzip whole project
                extractedProjectDir = studentOutDir.resolve( "_project" ) ;


                // Progress to real console
                System.out.println( "Extracted to: " + extractedProjectDir.toString() ) ;

                // delete any previously unzipped stuff - leaves test-logs
                if ( Files.exists( extractedProjectDir ) )
                    {
                    deleteRecursively( extractedProjectDir ) ;
                    }

                ZipUtils.unzip( zip, extractedProjectDir ) ;
                }
            catch ( final Exception e )
                {
                System.out.printf( "processing student failed: s%n", e.getMessage() ) ;
                e.printStackTrace() ;

                System.out.printf( "aborting - no further processing for this submission%n" ) ;

                continue ;
                }

            // 3) locate {className}.java precisely (Eclipse structure)
            Path studentClass ;

            try
                {
                studentClass = findClassInExtractedProject( extractedProjectDir ) ;
                }
            catch ( final Exception e )
                {
                System.out.printf( "processing student failed: s%n", e.getMessage() ) ;
                e.printStackTrace() ;

                System.out.printf( "aborting - no further processing for this submission%n" ) ;

                continue ;
                }

            // Progress to real console
            System.out.println( "running student process" ) ;


            // 4) now run the normal pipeline
            final Path logFile = studentOutDir.resolve( "batch.log" ) ;

            int exit = -1 ;

            try
                {
//                exit = runOneStudentInFreshJvm(projectRoot, studentClass, logFile, studentOutDir.resolve("_project") );
                exit = runOneStudentInFreshJvm( projectRoot, studentClass, logFile, extractedProjectDir ) ;
                }
            catch ( final Exception e )
                {
                System.out.printf( "processing submission failed: s%n", e.getMessage() ) ;
                e.printStackTrace() ;
                }

            System.out.println( "Exit code: " + exit ) ;
            }

        System.out.printf( "%nfinished processing %,d of %,d submissions%n",
                           submissionCounter,
                           uniqueSubmissionCount ) ;

        }   // end main()


    record Submission( String studentId,
                       String assignmentId,
                       String displayName,
                       LocalDateTime submittedAt,
                       Path folder )
        {}


    static Optional<Submission> parseSubmissionFolder( final Path folder )
        {

        final String name = folder.getFileName().toString() ;
        final String[] parts = name.split( "\\s+-\\s+", 3 ) ;

        if ( parts.length != 3 )
            {
            return Optional.empty() ;
            }

        final String[] ids = parts[ 0 ].split( "-", 2 ) ;

        if ( ids.length != 2 )
            {
            return Optional.empty() ;
            }

        final String studentId = ids[ 0 ].trim() ;
        final String assignmentId = ids[ 1 ].trim() ;
        final String displayName = parts[ 1 ].trim() ;
        final String timeText = parts[ 2 ].trim() ;

        final Optional<LocalDateTime> dt = BrightspaceUtilities.parseTime( timeText ) ;

        if ( dt.isEmpty() )
            {
            return Optional.empty() ;
            }

        return Optional.of( new Submission( studentId, assignmentId, displayName, dt.get(), folder ) ) ;

        }


    static void deleteRecursively( final Path root ) throws IOException
        {

        try ( var walk = Files.walk( root ) )
            {
            walk.sorted( Comparator.reverseOrder() ).forEach( p ->
                {

                try
                    {
                    Files.delete( p ) ;
                    }
                catch ( final IOException e )
                    {
                    throw new UncheckedIOException( e ) ;
                    }

                } ) ;
            }

        }


    static Path findZipInSubmissionFolder( final Path submissionFolder ) throws IOException
        {

        try ( var s = Files.list( submissionFolder ) )
            {
            return s.filter( p -> p.getFileName().toString().toLowerCase().endsWith( ".zip" ) )
                    .findFirst()
                    .orElseThrow( () -> new FileNotFoundException( "No .zip in " + submissionFolder ) ) ;
            }

        }


    static Path findClassInExtractedProject( final Path projectRoot ) throws IOException
        {
//            Path expected = projectRoot.resolve("src/edu/wit/scds/ds/" + ADTPathSegment + "/" + className + ".java");

        final List<Path> pathsToSourceFile = findFiles( className + ".java", projectRoot.toString() ) ;

        if ( pathsToSourceFile.isEmpty() )
            {
            throw new FileNotFoundException( String.format( "looking for '%s' in '%s'",
                                                            className + ".java",
                                                            projectRoot.toString() ) ) ;
            }

        if ( pathsToSourceFile.size() >= 1 )
            {
            System.out.printf( "*** found %s in %s%n", className + ".java", projectRoot ) ;
            // TODO warn if found more than 1
            return pathsToSourceFile.getFirst() ;
            }

//            if (Files.isRegularFile(expected))
//            {
//                return expected;
//            }

        // fallback: any {className}.java under src/
        List<Path> matches ;

        try ( var w = Files.walk( projectRoot ) )
            {
//            matches = w.filter( Files::isRegularFile )
//                       .filter( p -> p.getFileName().toString().equals( className + ".java" ) )
//                       .toList() ;
            matches = w.filter( Files::isRegularFile ).filter( p ->
                {
                final String s = p.toString().replace( '\\', '/' ) ;
                return ! s.contains( "/target/" ) && ! s.contains( "/bin/" )
                       && ! s.contains( "/test-logs/" ) ;
                } ).filter( p -> ( className + ".java" ).equals( p.getFileName().toString() ) ).toList() ;
            }

        if ( matches.isEmpty() )
            {
            throw new FileNotFoundException( className + ".java not found under " + projectRoot ) ;
            }

        // prefer anything under /src/
        final List<Path> underSrc = matches.stream()
                                     .filter( p -> p.toString().replace( '\\', '/' ).contains( "/src/" ) )
                                     .toList() ;

        if ( underSrc.size() == 1 )
            {
            return underSrc.get( 0 ) ;
            }

        if ( matches.size() == 1 )
            {
            return matches.get( 0 ) ;
            }

        final StringBuilder sb
                = new StringBuilder( "Ambiguous " + className + ".java under " + projectRoot + "\n" ) ;

        for ( final Path p : ( underSrc.isEmpty()
                ? matches
                : underSrc ) )
            {
            sb.append( "  - " ).append( projectRoot.relativize( p ) ).append( "\n" ) ;
            }

        throw new IllegalStateException( sb.toString() ) ;

        }


    @SuppressWarnings( "unused" )
    private static List<Path> findStudentFiles( final Path root,
                                                final String pattern ) throws IOException
        {

        // Pattern options: - if pattern contains ** or *, treat as glob applied to file name/path - simplest:
        // pattern = className + ".java"
        final boolean looksGlob
                = pattern.contains( "*" ) || pattern.contains( "?" ) || pattern.contains( "{" ) ;

        try ( Stream<Path> s = Files.walk( root ) )
            {
            return s.filter( Files::isRegularFile ).filter( p ->
                {

                if ( ! looksGlob )
                    {
                    return p.getFileName().toString().equalsIgnoreCase( pattern ) ;
                    }

                // crude but effective: match on path string using PathMatcher with "glob:"
                final PathMatcher matcher = FileSystems.getDefault().getPathMatcher( "glob:" + pattern ) ;
                // match either filename or relative path
                return matcher.matches( p.getFileName() ) || matcher.matches( root.relativize( p ) ) ;
                } ).collect( Collectors.toList() ) ;
            }

        }


    @SuppressWarnings( "unused" )
    private static String makeStudentId( final Path studentRoot,
                                         final Path studentFile )
        {

        // A stable, filesystem-friendly id based on relative path
        String rel = studentRoot.relativize( studentFile ).toString() ;
        rel = rel.replace( '\\', '/' ) ;
        rel = rel.replace( '/', '_' ) ;
        rel = rel.replace( ':', '_' ) ;

        // chop off the filename if you want folder-only id rel = rel.replace(className + ".java", "");
        return rel.replaceAll( "[^A-Za-z0-9_.-]", "_" ) ;

        }


    static Path requireClass( final Path projectRoot ) throws IOException
        {
//            Path lb = projectRoot.resolve("src/edu/wit/scds/ds/" + ADTPathSegment + "/" + className + ".java");

//
//            if (!Files.isRegularFile(lb))
//            {
//                throw new FileNotFoundException(
//                    className + ".java not found at expected path: " + lb
//                );
//            }
//
//            return lb;

        final List<Path> pathsToSourceFile = findFiles( className + ".java", projectRoot.toString() ) ;

        if ( pathsToSourceFile.isEmpty() )
            {
            throw new FileNotFoundException( String.format( "looking for '%s' in '%s'",
                                                            className + ".java",
                                                            projectRoot.toString() ) ) ;
            }

        if ( pathsToSourceFile.size() >= 1 )
            {
            System.out.printf( "*** found %s in %s%n", className + ".java", projectRoot ) ;
            // TODO warn if found more than 1
            return pathsToSourceFile.getFirst() ;
            }

        return null ;   // can't get here

        }


    @SuppressWarnings( "unused" )
    private static String buildChildClasspath( final Path projectRoot )
        {

        final String base = System.getProperty( "java.class.path" ) ;

        final List<String> extra = List.of( projectRoot.resolve( "target/classes" ).toString(),
                                      projectRoot.resolve( "target/test-classes" ).toString(),
                                      projectRoot.resolve( "bin" ).toString(),       // legacy Eclipse
                                      projectRoot.resolve( "bin-test" ).toString()   // if you have it
        ) ;

        return base + File.pathSeparator + String.join( File.pathSeparator, extra ) ;

        }


    private static int runOneStudentInFreshJvm( final Path projectRoot,
                                                final Path studentClass,
                                                final Path logFile,
                                                final Path studentOutDir ) throws IOException,
                                                                           InterruptedException
        {

        final String javaExe = Paths.get( System.getProperty( "java.home" ), "bin", "java" ).toString() ;

//        // Use the *current* classpath so the child JVM can see your harness + test + tool classes. When you
//        // run this via Maven (exec plugin), the classpath is already correct.
//        final String classpath = System.getProperty( "java.class.path" ) ;

//        final Path proj = projectRoot.toAbsolutePath().normalize();
//
//        final String classpath =
//                System.getProperty("java.class.path")
//                + java.io.File.pathSeparator + proj.resolve("target/test-classes")
//                + java.io.File.pathSeparator + proj.resolve("target/classes");


        final List<String> cmd = new ArrayList<>() ;
        cmd.add( javaExe ) ;
//        cmd.add( "-cp" ) ;
//        cmd.add(buildChildClasspath(projectRoot));

        final String parentCp = System.getProperty( "java.class.path" ) ;

        final String childCp = projectRoot.resolve( "target/test-classes" ) + File.pathSeparator
                         + projectRoot.resolve( "target/classes" ) + File.pathSeparator + parentCp ;

        cmd.add( "-cp" ) ;
        cmd.add( childCp ) ;

//        cmd.add( classpath ) ;
        cmd.add( "education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.SingleStudentRunner" ) ;


        // Knobs/dials passed to child:
        cmd.add( "--projectRoot" ) ;
        cmd.add( projectRoot.toString() ) ;
        cmd.add( "--studentFile" ) ;
        cmd.add( studentClass.toString() ) ;
        cmd.add( "--logFile" ) ;
        cmd.add( logFile.toString() ) ;
        cmd.add( "--workDir" ) ;
        cmd.add( studentOutDir.toString() ) ;


        final ProcessBuilder pb = new ProcessBuilder( cmd ) ;
        pb.redirectErrorStream( true ) ;
        pb.redirectOutput( logFile.toFile() ) ;

        pb.directory( studentOutDir.toFile() ) ;

        // NEW: make the child's "." be the folder containing batch.log
        final Path workDir = logFile.toAbsolutePath().normalize().getParent() ;

        if ( workDir != null )
            {
            pb.directory( workDir.toFile() ) ;
            }

        return pb.start().waitFor() ;

        }

    }   // end class BatchRunner
