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


package education.the_software_toolsmith.analyzer.framework.utilities ;


/**
 * exception representing an operational failure of an analysis operation
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2026-01-11 Initial implementation
 */
public class AnalysisException extends RuntimeException
    {

    /** to support serialization */
    private static final long serialVersionUID = -2692191848559911464L ;


    /*
     * constructors
     */


    /**
     * set state with no message or cause provided
     */
    public AnalysisException()
        {

        super() ;

        }   // end no-arg constructor


    /**
     * set state with a provided message
     *
     * @param message
     *     description for this exception
     */
    public AnalysisException( final String message )
        {

        super( message ) ;

        }   // end 1-arg constructor with message


    /**
     * set state with a provided cause
     *
     * @param cause
     *     chained exception which resulted in this compliance exception
     */
    public AnalysisException( final Throwable cause )
        {

        super( cause ) ;

        }   // end 1-arg constructor with cause


    /**
     * set state with a provided message and cause
     *
     * @param message
     *     description for this exception
     * @param cause
     *     chained exception which resulted in this compliance exception
     */
    public AnalysisException( final String message,
                                final Throwable cause )
        {

        super( message, cause ) ;

        }   // end 2-arg constructor


    /**
     * set state with a specified message and cause plus additional flags
     *
     * @param message
     *     description for this exception
     * @param cause
     *     chained exception which resulted in this compliance exception
     * @param enableSuppression
     *     whether suppression is enabled (true) or disabled (false)
     * @param writableStackTrace
     *     whether the stack should be writable
     */
    public AnalysisException( final String message,
                                final Throwable cause,
                                final boolean enableSuppression,
                                final boolean writableStackTrace )
        {

        super( message, cause, enableSuppression, writableStackTrace ) ;

        }   // end 4-arg constructor

    }   // end class AnalysisException
