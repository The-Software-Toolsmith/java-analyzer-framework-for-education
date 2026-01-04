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


package education.the_software_toolsmith.analyzer_framework.dynamicanalysis.tests;

/**
 * runtime exception for testing the framework
 *
 * @author David M Rosenberg
 *
 * @version 1.0 Initial implementation
 */
public class TantrumException extends RuntimeException
    {

    /*
     * constants
     */


    /** Support serialization */
    private static final long serialVersionUID = 1L ;


    /*
     * constructors
     */


    /**
     * no-arg constructor
     */
    public TantrumException()
        {

        super() ;

        }   // end no-arg constructor


    /**
     * constructor with message
     *
     * @param message
     *     the message text associated with this exception
     */
    public TantrumException( final String message )
        {

        super( message ) ;

        }   // end constructor with descriptive message


    /**
     * @param cause
     *     the 'wrapped' exception
     */
    public TantrumException( final Throwable cause )
        {

        super( cause ) ;

        }   // end 'simple wrapper' constructor without message


    /**
     * @param message
     *     descriptive message related to the {@code cause}
     * @param cause
     *     the 'wrapped' exception
     */
    public TantrumException( final String message, final Throwable cause )
        {

        super( message, cause ) ;

        }   // end 'wrapper' constructor with descriptive message

    }   // end class TantrumException