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


package education.the_software_toolsmith.analyzer.framework.staticanalysis.coding_style ;

import education.the_software_toolsmith.analyzer.framework.staticanalysis.tools.PathUtils ;
import education.the_software_toolsmith.analyzer.framework.utilities.SharedState ;

import com.puppycrawl.tools.checkstyle.Checker ;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader ;
import com.puppycrawl.tools.checkstyle.ConfigurationLoader.IgnoredModulesOptions ;
import com.puppycrawl.tools.checkstyle.PropertiesExpander ;
import com.puppycrawl.tools.checkstyle.api.AuditEvent ;
import com.puppycrawl.tools.checkstyle.api.AuditListener ;
import com.puppycrawl.tools.checkstyle.api.Configuration ;
import com.puppycrawl.tools.checkstyle.api.SeverityLevel ;

import org.xml.sax.InputSource ;

import java.io.File ;
import java.nio.file.Path ;
import java.util.ArrayList ;
import java.util.LinkedList ;
import java.util.List ;


/**
 * Simple helper to run Checkstyle programmatically on a set of .java files.
 * Requires Checkstyle on the classpath, e.g. Maven:
 *
 * <pre>
 * <dependency>
 *   <groupId>com.puppycrawl.tools</groupId>
 *   <artifactId>checkstyle</artifactId>
 *   <version>10.17.0</version>
 * </dependency>
 * </pre>
 *
 * @author David M Rosenberg from ChatGPT 5.1
 *
 * @version 1.0 2025-12-05 Initial implementation
 */
public final class CodingStyleComplianceChecker extends SharedState
    {

    /**
     * Run Checkstyle against the given Java source files using the supplied
     * Checkstyle configuration XML.
     *
     * @param checkstyleXml
     *     path to checkstyle.xml
     * @param javaFiles
     *     list of .java files to analyze
     *
     * @return Result containing violations, a compliance flag, and a text
     *     report
     *
     * @throws Exception
     *     if checkstyle fails catastrophically
     */
    public static Result runCheckstyle( Path checkstyleXml,
                                        List<Path> javaFiles )
            throws Exception
        {

        if ( javaFiles.size() == 0 )
            {
            return new Result() ;
            }

        // Load Checkstyle configuration
        InputSource inputSource = new InputSource( checkstyleXml.toUri().toString() ) ;
        Configuration configuration =
                ConfigurationLoader.loadConfiguration( inputSource,
                                                       new PropertiesExpander( System.getProperties() ),
                                                       IgnoredModulesOptions.OMIT ) ;


        Result result = new Result() ;

        StringBuilder report = result.report ;
        StringBuilder summary = result.summary ;
        
        List<Violation> violationsFound = result.violations ;
        
        
        AuditListener listener = new AuditListener()
            {

            @Override
            public void auditStarted( AuditEvent auditEvent )
                {}


            @Override
            public void auditFinished( AuditEvent auditEvent )
                {}


            @Override
            public void fileStarted( AuditEvent auditEvent )
                {}


            @Override
            public void fileFinished( AuditEvent auditEvent )
                {}


            @Override
            public void addError( AuditEvent auditEvent )
                {

/* DEBUG */ System.err.printf( "audit: %10s: %20s @ %,5d: %,5d%n",
                               auditEvent.getViolation().getSeverityLevel(),
                               auditEvent.getViolation().getKey(),
                               auditEvent.getViolation().getLineNo(),
                               auditEvent.getViolation().getTokenType() ) ;

                violationsFound.add( new Violation( auditEvent.getFileName(),
                                                    auditEvent.getLine(),
                                                    auditEvent.getMessage(),
                                                    auditEvent.getSeverityLevel(),
                                                    auditEvent ) ) ;

                }


            @Override
            public void addException( AuditEvent auditEvent,
                                      Throwable throwable )
                {

                // You could log or record exceptions here if desired
                }

            } ; // end AuditListener listener

        Checker checker = new Checker() ;

        result.baseDirectory = PathUtils.computeBaseDirFor( javaFiles ) ;

        checker.setBasedir( result.baseDirectory ) ;
        

        try
            {
            checker.setModuleClassLoader( Checker.class.getClassLoader() ) ;
            checker.addListener( listener ) ;
            checker.configure( configuration ) ;

            List<File> fileNames = new ArrayList<>() ;

            for ( Path path : javaFiles )
                {
                fileNames.add( path.toAbsolutePath().toFile() ) ;
                }

            checker.process( fileNames ) ;



            result.isCompliant = violationsFound.isEmpty() ;

            final String reportHeader = String.format( "Checkstyle Report:%n%n" ) ;
            final String noViolationsText = String.format( "  ✔ No style violations.%n" ) ;
            
            report.append( reportHeader ) ;
            summary.append( reportHeader ) ;

            if ( result.isCompliant )
                {
                report.append( noViolationsText ) ;
                summary.append( noViolationsText ) ;
                }
            else
                {

                for ( Violation violation : violationsFound )
                    {
                    report.append( String.format( "  %s%n", violation ) ) ;
                    
                    result.violationCounters[ violation.severity.ordinal() ]++ ;
                    }

                final String violationsHeader = String.format( "%n  %,d style violation%s:%n",
                                                               violationsFound.size(),
                                                               violationsFound.size() == 1
                                                               ? ""
                                                                       : "s" ) ;
                report.append( violationsHeader ) ;
                summary.append( violationsHeader ) ;


                final StringBuilder tallies = new StringBuilder() ;

                for ( SeverityLevel severityLevel : SeverityLevel.values() )
                    {
                    int index = severityLevel.ordinal() ;
                    tallies.append( String.format( "\t%5s %s violation%s%n",
                                                   result.violationCounters[ index ] == 0
                                                   ? "no"
                                                           : String.format( "%,5d",
                                                                            result.violationCounters[ index ] ),
                                                           severityLevel,
                                                           result.violationCounters[ index ] == 1
                                                           ? ""
                                                                   : "s" ) ) ;
                    }

                report.append( tallies ) ;
                summary.append( tallies ) ;
                }

            return result ;
            }
        catch ( Exception e )
            {
            e.printStackTrace() ;

            throw e ;   // re-thow the exception
            }

        }   // end runCheckstyle()


    /*
     * inner classes
     */
    // none

    }   // end class CodingStyleComplianceChecker
