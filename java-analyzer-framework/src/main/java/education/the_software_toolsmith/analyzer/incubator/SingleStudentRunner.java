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

import static org.junit.platform.engine.discovery.DiscoverySelectors.selectClass ;

import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;

import org.junit.platform.launcher.Launcher ;
import org.junit.platform.launcher.LauncherDiscoveryRequest ;
import org.junit.platform.launcher.core.LauncherDiscoveryRequestBuilder ;
import org.junit.platform.launcher.core.LauncherFactory ;
import org.junit.platform.launcher.listeners.SummaryGeneratingListener ;

import java.io.File ;
import java.io.IOException ;
import java.nio.charset.StandardCharsets ;
import java.nio.file.Files ;
import java.nio.file.Path ;
import java.nio.file.Paths ;
import java.nio.file.StandardCopyOption ;
import java.util.ArrayList ;
import java.util.HashMap ;
import java.util.List ;
import java.util.Locale ;
import java.util.Map ;

import javax.tools.Diagnostic ;
import javax.tools.DiagnosticCollector ;
import javax.tools.JavaCompiler ;
import javax.tools.JavaFileObject ;
import javax.tools.StandardJavaFileManager ;
import javax.tools.ToolProvider ;

/**
 * PLACEHOLDER
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-15 Initial implementation based on code from ChatGPT 5.2
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class SingleStudentRunner extends SharedState
    {

    // ===== KNOBS / DIALS =====

    // Where {className}.java should be copied into your project:
    private static final Path DEST_CLASS_REL ;
//            Paths.get("src/main/java/edu/wit/scds/ds/" + ADTPathSegment + "/" + className + ".java");
    static
        {
//        String src = Path.of( "./src" ).toAbsolutePath().normalize().toString() ;
        final String extracted = Path.of( "_project" ).toAbsolutePath().normalize().toString() ;
        final List<Path> pathsToSourceFile = findFiles( className + ".java", extracted ) ;

        if ( pathsToSourceFile.size() >= 1 )
            {
            System.out.printf( "*** found %s in %s%n", className + ".java", extracted ) ;
            // TODO warn if found more than 1
            DEST_CLASS_REL = pathsToSourceFile.getFirst() ;
            }
        else
//        if ( pathsToSourceFile.isEmpty() )
            {
            System.out.printf( "*** %s not found in %s%n", className + ".java", extracted ) ;
//            throw new FileNotFoundException( String.format( "looking for '%s' in '%s'", className + ".java", src ) ) ;
            DEST_CLASS_REL = null ;    // hopefully will cause something to blow up
            }

        }

    // Your JUnit test class:
    private static final String TEST_CLASS_FQCN
            = "edu.wit.scds.ds." + ADTPackageSegment + ".tests." + className + "DMRTests" ;

    // Your tool mains:
    private static final String CHECKSTYLE_TOOL_FQCN
            = "education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.ValidateCodingStyle" ;

    private static final String IMPL_TOOL_FQCN
            = "education.the_software_toolsmith.analyzer.framework.staticanalysis.compliance.ValidateImplementation" ;

    // The compiled class output location (standard Maven):
    private static final Path CLASSES_OUT_REL = Paths.get( "target/classes" ) ;
    @SuppressWarnings( "unused" )
    private static final Path TEST_CLASSES_OUT_REL = Paths.get( "target/test-classes" ) ;

    // =========================


    public static void main( final String[] args ) throws Exception
        {

        final Map<String, String> a = parseArgs( args ) ;

        final Path projectRoot = Paths.get( required( a, "--projectRoot" ) ).toAbsolutePath().normalize() ;
        final Path studentFile = Paths.get( required( a, "--studentFile" ) ).toAbsolutePath().normalize() ;
        final Path logFile = Paths.get( required( a, "--logFile" ) ).toAbsolutePath().normalize() ;
        final Path workDir = Paths.get( required( a, "--workDir" ) ).toAbsolutePath().normalize() ;

        Files.createDirectories( logFile.getParent() ) ;

        // Everything in this JVM goes to the log (BatchRunner already redirected output to logFile). Still,
        // we can write a header.
        System.out.println( "PROJECT: " + projectRoot ) ;
        System.out.println( "STUDENT: " + studentFile ) ;
        System.out.println( "WORKDIR: " + workDir ) ;
        System.out.println( "------------------------------------------------------------" ) ;

        // 1) Copy student file into project
        final Path destSource = projectRoot.resolve( DEST_CLASS_REL ) ;
        Files.createDirectories( destSource.getParent() ) ;
        Files.copy( studentFile, destSource, StandardCopyOption.REPLACE_EXISTING ) ;
        System.out.println( "Copied to: " + destSource ) ;

        // Optional isolation: delete old class so you can't accidentally use stale output
        final Path classClass
                = projectRoot.resolve( "target/classes" )
                             .resolve( "edu/wit/scds/ds/" + ADTPathSegment + "/" + className + ".class" ) ;
        Files.deleteIfExists( classClass ) ;


        // 2) Compile " + {className} + ".java (and anything it depends on in src/main/java) For safety,
        // compile the whole main source tree (slower but reliable). If you want, you can optimize later to
        // compile just {className} + deps.
        compileMainSources( projectRoot ) ;

        // 4) Run Checkstyle tool (catch so next step still runs)
        runMainSafely( CHECKSTYLE_TOOL_FQCN, new String[ 0 ] ) ;

        // 5) Run Implementation validator
        runMainSafely( IMPL_TOOL_FQCN, new String[ 0 ] ) ;

        // 3) Run JUnit
        runJUnitInProcess( TEST_CLASS_FQCN ) ;

        System.out.println( "DONE." ) ;

        }


    private static void compileMainSources( final Path projectRoot ) throws IOException
        {

        final JavaCompiler compiler = ToolProvider.getSystemJavaCompiler() ;

        if ( compiler == null )
            {
            throw new IllegalStateException( "No JavaCompiler. Run with a JDK, not a JRE." ) ;
            }

        final Path srcMain = projectRoot.resolve( "src/main/java" ) ;
        final Path outDir = projectRoot.resolve( CLASSES_OUT_REL ) ;

        Files.createDirectories( outDir ) ;

        // Collect all .java files under src/main/java
        final List<File> javaFiles = new ArrayList<>() ;

        try ( var stream = Files.walk( srcMain ) )
            {
            stream.filter( p -> p.toString().endsWith( ".java" ) )
                  .forEach( p -> javaFiles.add( p.toFile() ) ) ;
            }

        if ( javaFiles.isEmpty() )
            {
            throw new IllegalStateException( "No java files found under " + srcMain ) ;
            }

        // Classpath for compilation: - target/classes (for previously compiled classes) - plus dependencies
        // from current process classpath (when run via Maven exec, this includes deps)
        final String cp = System.getProperty( "java.class.path" ) ;

        // match your Parser language level; adjust if needed
        final List<String> options = List.of( "-d", outDir.toString(), "-cp", cp, "--release", "24" ) ;

        System.out.println( "Compiling " + javaFiles.size() + " source files..." ) ;
        final StandardJavaFileManager fm
                = compiler.getStandardFileManager( null, null, StandardCharsets.UTF_8 ) ;

        final Iterable<? extends JavaFileObject> units = fm.getJavaFileObjectsFromFiles( javaFiles ) ;

        final DiagnosticCollector<JavaFileObject> diags = new DiagnosticCollector<>() ;
        final boolean ok = compiler.getTask( null, fm, diags, options, null, units ).call() ;

        for ( final Diagnostic<? extends JavaFileObject> d : diags.getDiagnostics() )
            {
            System.out.println( formatDiag( d ) ) ;
            }

        if ( ! ok )
            {
            throw new IllegalStateException( "Compilation failed." ) ;
            }

        System.out.println( "Compilation OK." ) ;

        }


    private static String formatDiag( final Diagnostic<? extends JavaFileObject> d )
        {

        final String src = ( d.getSource() == null )
                ? "<no source>"
                : d.getSource().getName() ;
        return String.format( "%s:%d:%d: %s: %s",
                              src,
                              d.getLineNumber(),
                              d.getColumnNumber(),
                              d.getKind(),
                              d.getMessage( Locale.ROOT ) ) ;

        }


    private static Map<String, String> parseArgs( final String[] args )
        {

        final Map<String, String> m = new HashMap<>() ;

        for ( int i = 0 ; i < args.length ; i++ )
            {
            final String k = args[ i ] ;

            if ( k.startsWith( "--" ) && ( ( i + 1 ) < args.length ) )
                {
                i++ ;
                m.put( k, args[ i ] ) ;
                }

            }

        return m ;

        }


    private static String required( final Map<String, String> args,
                                    final String key )
        {

        final String v = args.get( key ) ;

        if ( ( v == null ) || v.isBlank() )
            {
            throw new IllegalArgumentException( "Missing arg " + key ) ;
            }

        return v ;

        }


    private static void runJUnitInProcess( final String testClassFqcn ) throws Exception
        {

        final Class<?> testClass = Class.forName( testClassFqcn ) ;

        final LauncherDiscoveryRequest request
                = LauncherDiscoveryRequestBuilder.request().selectors( selectClass( testClass ) ).build() ;

        final Launcher launcher = LauncherFactory.create() ;
        final SummaryGeneratingListener listener = new SummaryGeneratingListener() ;
        launcher.registerTestExecutionListeners( listener ) ;

        System.out.println( "Running JUnit: " + testClassFqcn ) ;
        launcher.execute( request ) ;

        final var summary = listener.getSummary() ;
        System.out.println( "JUnit found:   " + summary.getTestsFoundCount() ) ;
        System.out.println( "JUnit started: " + summary.getTestsStartedCount() ) ;
        System.out.println( "JUnit failed:  " + summary.getTestsFailedCount() ) ;
        System.out.println( "JUnit skipped: " + summary.getTestsSkippedCount() ) ;

        summary.getFailures().forEach( f ->
            {
            System.out.println( "FAILURE: " + f.getTestIdentifier().getDisplayName() ) ;
            f.getException().printStackTrace( System.out ) ;
            } ) ;

        }


    private static void runMainSafely( final String fqcn,
                                       final String[] toolArgs )
        {

        System.out.println( "Running tool: " + fqcn ) ;

        try
            {
            final Class<?> c = Class.forName( fqcn ) ;
            final var m = c.getMethod( "main", String[].class ) ;
            m.invoke( null, (Object) toolArgs ) ;
            }
        catch ( final Throwable t )
            {
            System.out.println( "Tool failed: " + fqcn ) ;
            t.printStackTrace( System.out ) ;
            }

        }

    }   // end class SingleStudentRunner
