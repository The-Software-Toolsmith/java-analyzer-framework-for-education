/* @formatter:off
 *
 * Copyright © 2025-2026 David M Rosenberg, The Software Toolsmith
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


package education.the_software_toolsmith.analyzer.framework.static_analysis.style ;

import education.the_software_toolsmith.analyzer.framework.compliance.ComplianceException ;
import education.the_software_toolsmith.analyzer.framework.static_analysis.tools.PathUtils ;
import education.the_software_toolsmith.analyzer.framework.utilities.AnalysisException ;
import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;

import static education.the_software_toolsmith.analyzer.framework.static_analysis.style.CodeStyleAnalysisEventPaired.StartOrFinish.* ;

import com.puppycrawl.tools.checkstyle.Checker ;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader ;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader.IgnoredModulesOptions ;
import com.puppycrawl.tools.checkstyle.PropertiesExpander ;
import com.puppycrawl.tools.checkstyle.api.AuditEvent ;
import com.puppycrawl.tools.checkstyle.api.AuditListener ;
import com.puppycrawl.tools.checkstyle.api.Configuration ;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel ;
import com.puppycrawl.tools.checkstyle.api.Violation ;

import org.xml.sax.InputSource ;

import java.io.File ;
import java.nio.file.Path ;
import java.util.ArrayList ;
import java.util.Arrays ;
import java.util.LinkedHashMap ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.Map ;
import java.util.Map.Entry ;


/**
 * Simple helper to run Checkstyle programmatically on a set of .java files. Requires Checkstyle on the
 * classpath, e.g. Maven:
 *
 * <pre>
 * <dependency>
 *   <groupId>com.puppycrawl.tools</groupId>
 *   <artifactId>checkstyle</artifactId>
 *   <version>10.17.0</version>
 * </dependency>
 * </pre>
 *
 * @author David M Rosenberg based on code from ChatGPT 5.1
 *
 * @version 1.0 2025-12-05 Initial implementation
 * @version 2.0 2026-01-10
 *     <ul>
 *     <li>rename from {@code CodingStyleAnalyzer} to {@code CodeStyleAnalyzer}
 *     <li>rename {@code runCheckstyle()} to {@code analyze()} for consistency with other analyzers
 *     </ul>
 */
public final class CodeStyleAnalyzer extends SharedState
    {
    
    /*
     * constants
     */

    /** key into the results map for audit-level events */
    final public static String AUDIT_KEY = "!audit!" ;
    
    /** key into the results map for exception events */
    final public static String EXCEPTIONS_KEY = "!exceptions!" ;
    
    /** key into the results map for the complete analysis */
    final public static String EVERYTHING_KEY = "!everything!" ;
 
    
    /*
     * constructors
     */
    
    
    /** prevent instantiation */
    private CodeStyleAnalyzer()
        {}
    

    /**
     * Run Checkstyle against the given Java source files using the supplied Checkstyle configuration XML.
     *
     * @param checkstyleXml
     *     path to checkstyle.xml
     * @param javaFiles
     *     list of .java files to analyze
     *
     * @return results containing events, compliance flags, and text reports
     *     <p>
     *     keyed:
     *     <ul>
     *     <li>{@code AUDIT_KEY} → for high-level audit-related events
     *     <li>{@code EXCEPTIONS_KEY} → for exceptions which occurred during the analysis (only present if one or more exceptions were logged)
     *     <li>EVERYTHING_KEY → for all events logged
     *     <li>filename (class.java) → for all events related to analyzing a single source file
     *     </ul>
     *     <p>if no source files are supplied, returns {@code null}
     *
     * @throws Exception
     *     if checkstyle or our analyzer fails catastrophically
     */
    public static Map<String, CodeStyleAnalysisResult> analyze( Path checkstyleXml,
                                                                List<Path> javaFiles ) throws Exception
        {

        // do we have anything to do?
        if ( javaFiles.size() == 0 )
            {
            return null ;
            }

        // assertion: there's at least one source file to analyze
        
        // load Checkstyle configuration
        InputSource inputSource = new InputSource( checkstyleXml.toUri().toString() ) ;
        Configuration configuration =
                ConfigurationLoader.loadConfiguration( inputSource,
                                                       new PropertiesExpander( System.getProperties() ),
                                                       IgnoredModulesOptions.OMIT ) ;

        
        // create structures to collect audit information
        
        // split the audit information by source
        final Map<String, CodeStyleAnalysisResult> allAuditResults = new LinkedHashMap<>() ;
        
        // create 'standard' results structures
        final CodeStyleAnalysisResult everythingResults = new CodeStyleAnalysisResult() ;
        allAuditResults.put( EVERYTHING_KEY, everythingResults ) ;
        
        final CodeStyleAnalysisResult auditResults = new CodeStyleAnalysisResult() ;
        allAuditResults.put( AUDIT_KEY, auditResults ) ;
        
        final CodeStyleAnalysisResult exceptionsResults = new CodeStyleAnalysisResult() ;
        allAuditResults.put( EXCEPTIONS_KEY, exceptionsResults ) ;
        
        
        // shorthand references
        StringBuilder report = everythingResults.report ;
        StringBuilder summary = everythingResults.summary ;
        
        List<CodeStyleAnalysisEvent> eventsFound = everythingResults.codeStyleAnalysisEvents ;
        

        // record all auditable events
        AuditListener listener = new AuditListener()
            {

            @Override
            public void auditStarted( AuditEvent auditEvent )
                {
                eventsFound.add( new CodeStyleAnalysisEventAudit( auditEvent, START ) ) ;
                }


            @Override
            public void auditFinished( AuditEvent auditEvent )
                {

                eventsFound.add( new CodeStyleAnalysisEventAudit( auditEvent, FINISH ) ) ;

                }


            @Override
            public void fileStarted( AuditEvent auditEvent )
                {
                
                eventsFound.add( new CodeStyleAnalysisEventFile( auditEvent, START ) ) ;

                }
            

            @Override
            public void fileFinished( AuditEvent auditEvent )
                {

                eventsFound.add( new CodeStyleAnalysisEventFile( auditEvent, FINISH ) ) ;

                }


            @Override
            public void addError( AuditEvent auditEvent )
                {

                eventsFound.add( new CodeStyleAnalysisEventError( auditEvent ) ) ;

                }


            @Override
            public void addException( AuditEvent auditEvent,
                                      Throwable throwable )
                {

                eventsFound.add( new CodeStyleAnalysisEventException( auditEvent, throwable ) ) ;

                }

            } ; // end AuditListener listener


        // set up to check the file(s)
        Checker checker = new Checker() ;

        everythingResults.baseDirectory = PathUtils.computeBaseDirFor( javaFiles ) ;

        checker.setBasedir( everythingResults.baseDirectory ) ;
        

        // check them
        try
            {
            checker.setModuleClassLoader( Checker.class.getClassLoader() ) ;
            checker.addListener( listener ) ;
            checker.configure( configuration ) ;

            List<File> fileNames = new ArrayList<>( javaFiles.size() ) ;

            for ( Path path : javaFiles )
                {
                fileNames.add( path.toAbsolutePath().toFile() ) ;
                }

            
            // do the analysis
            checker.process( fileNames ) ;

            }
        catch ( Exception e )
            {
            // wrap and re-thow the exception
            throw new ComplianceException( "catastrophic failure running Checkstyle analysis", e ) ;
            }
/* DEBUG */System.out.printf( "allAuditResults:%n%s%n%n", allAuditResults ) ;
/* DEBUG */System.out.printf( "everythingResults:%n%s%n%n", everythingResults ) ;
/* DEBUG */System.out.printf( "auditResults:%n%s%n%n", auditResults ) ;
/* DEBUG */System.out.printf( "exceptionResults:%n%s%n%n", exceptionsResults ) ;
        
        // assertion: Checkstyle analysis completed successfully
        
        
        // split the events by file or category
        for ( CodeStyleAnalysisEvent event : eventsFound )
            {
            switch ( event )
                {
                case CodeStyleAnalysisEventException exceptionEvent ->
                    {
                    // use the local reference to the results structure for exceptions
                    
                    exceptionsResults.codeStyleAnalysisEvents.add( exceptionEvent ) ;
                    
                    final String entry = String.format( "%s%n", exceptionEvent.toString() ) ;
                    
                    exceptionsResults.report.append( entry ) ;
                    }

                case CodeStyleAnalysisEventError errorEvent ->
                    {
                    final String filename = errorEvent.getFilename() ;
                    
                    // retrieve the results structure for this file (should have handled fileStarted)
                    final CodeStyleAnalysisResult result = allAuditResults.get( filename ) ;
                    if ( result == null )
                        {
                        throw new AnalysisException( String.format( "failed to retrieve result structure for %s",
                                                                    filename ) ) ;
                        }
                    
                    result.codeStyleAnalysisEvents.add( errorEvent ) ;
                    
                    // add the violation to the map by key
                    final String key = errorEvent.getKey() ;
                    List<Violation> violations = result.keyedViolations.getOrDefault( key, new LinkedList<>() ) ;
                    violations.add( errorEvent.getViolation() ) ;
                    result.keyedViolations.put( key, violations ) ;
                    
                    
                    final String entry = String.format( "%s%n", errorEvent.toString() ) ;

                    result.report.append( entry ) ;
                    
                    result.violationSeverityLevelCounters[ errorEvent.getSeverity().ordinal() ]++ ;
                    
                    result.isCompliant = false ;
                    
                    allAuditResults.put( filename, result ) ;

                    }
                
                case CodeStyleAnalysisEventFile fileEvent ->
                    {
                    final String filename = fileEvent.getFilename() ;
                    
                    // create or retrieve the results structure for this file
                    final CodeStyleAnalysisResult result
                            = allAuditResults.getOrDefault( filename,
                                                        new CodeStyleAnalysisResult() ) ;
                    
                    result.codeStyleAnalysisEvents.add( fileEvent ) ;
                    
                    final String entry = String.format( "%n%s%n%n",  fileEvent.toString() ) ;
                    
                    result.report.append( entry ) ;
                    result.summary.append( entry ) ;
                    
                    allAuditResults.put( filename, result ) ;
                    }
                
                case CodeStyleAnalysisEventAudit auditEvent ->
                    {
                    // use the local reference to the results structure for the overall audit
                    
                    auditResults.codeStyleAnalysisEvents.add( auditEvent ) ;
                    
                    final String entry = String.format( "%n%s%n", auditEvent.toString() ) ;
                    
                    auditResults.report.append( entry ) ;
                    auditResults.summary.append( entry ) ;
                    }

                default -> throw new ComplianceException( String.format( "unexpected event type: '%s'",
                                                                         event.getClass()
                                                                              .getSimpleName() ) ) ;
                
                }   // end switch
            }   // end for all events found
        
        
        // display all violations grouped by key
/* DEBUG */ System.err.printf( "%n... 1 ...%n" ) ;
//        for ( )
        System.out.printf( "%n----------%nViolations by key for %s:%n%n", "???" ) ;
        for ( Entry<String, List<Violation>> entry : everythingResults.keyedViolations.entrySet() )
            {
            System.out.printf( "%s: %s%n", entry.getKey(), Arrays.toString( entry.getValue().toArray() ) ) ;
            }
        System.out.printf( "%n----------%n%n" ) ;
        
        /*
         * generate the report(s) and summary(s) - IN_PROCESS DMR should the report be all encompassing? probably
         * make it configurable - for now caller has control by giving us a single source file or potentially
         * multiple
         */
        

        // put the pieces together
        for ( String key : allAuditResults.keySet() )
            {
/* DEBUG */ System.err.printf( "%n... 2 ...%n" ) ;
            final CodeStyleAnalysisResult result = allAuditResults.get( key ) ;
            
/* DEBUG */ System.out.printf( "%n----------%n%s:%n%s%n----------%n%n", key, result ) ;
            }
        
//        for ( String key : )
        

/* DEBUG */ System.err.printf( "%n... 3 ...%n" ) ;
        final String reportHeader = String.format( "Code Style Analysis:%n%n" ) ;
        final String noViolationsText = String.format( "  ✔ No style violations.%n" ) ;

        report.append( reportHeader ) ;
        summary.append( reportHeader ) ;

        
        // if everything is compliant, indicate so and we're done
        if ( everythingResults.isCompliant )
            {

/* DEBUG */ System.err.printf( "%n... 4 ...%n" ) ;
            report.append( noViolationsText ) ;
            summary.append( noViolationsText ) ;

            return allAuditResults ;
            }


/* DEBUG */ System.err.printf( "%n... 5 ...%n" ) ;
        // there is/are compliance issues - build a report of them
        for ( CodeStyleAnalysisEvent codeStyleAnalysisEvents : eventsFound )
            {
            report.append( String.format( "  %s%n", codeStyleAnalysisEvents ) ) ;

            everythingResults.violationSeverityLevelCounters[ codeStyleAnalysisEvents.getSeverity().ordinal() ]++ ;
            }


/* DEBUG */ System.err.printf( "%n... 6 ...%n" ) ;        
        final String violationsHeader = String.format( "%n  %,d style violation%s:%n",
                                                       eventsFound.size(),
                                                       eventsFound.size() == 1
                                                               ? ""
                                                               : "s" ) ;
        report.append( violationsHeader ) ;
        summary.append( violationsHeader ) ;


        final StringBuilder tallies = new StringBuilder() ;

        for ( SeverityLevel severityLevel : SeverityLevel.values() )
            {

/* DEBUG */ System.err.printf( "%n... 7 ...%n" ) ;
            int index = severityLevel.ordinal() ;
            tallies.append( String.format( "\t%5s %s violation%s%n",
                                           everythingResults.violationSeverityLevelCounters[ index ]
                                                                     == 0
                                                                             ? "no"
                                                                             : String.format( "%,5d",
                                                                                              everythingResults.violationSeverityLevelCounters[ index ] ),
                                           severityLevel,
                                           everythingResults.violationSeverityLevelCounters[ index ]
                                                          == 1
                                                                  ? ""
                                                                  : "s" ) ) ;
            }

        report.append( tallies ) ;
        summary.append( tallies ) ;

        return allAuditResults ;

        }   // end analyze()
    

    /**
     * debugging aid: dumps audit event to text
     * 
     * @param auditEvent
     *     the audit event to display
     * 
     * @return a printable version of the supplied event
     */
    public static String eventToText( AuditEvent auditEvent )
        {

        return String.format( "\t%s:%n\tsource: %s%n\tfilename: %s%n\tviolation: %s%n",
                              auditEvent.getClass().getSimpleName(),
                              auditEvent.getSource(),
                              auditEvent.getFileName(),
                              auditEvent.getViolation() ) ;

        }   // end eventToText()


    /*
     * inner classes
     */
    // none

    }   // end class CodeStyleAnalyzer
