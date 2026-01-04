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


package education.the_software_toolsmith.analyzer.framework.dynamicanalysis ;

/**
 * unchecked exception to wrap lower-level exceptions during testing using exception chaining
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2020-09-13 Initial implementation
 * @version 1.1 2021-06-19 add no-arg and 1-arg String constructors
 */
public class TestingException extends RuntimeException
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
    public TestingException()
        {

        super() ;

        }   // end no-arg constructor


    /**
     * constructor with message
     *
     * @param message
     *     the message text associated with this exception
     */
    public TestingException( final String message )
        {

        super( message ) ;

        }   // end constructor with descriptive message


    /**
     * constructor with cause
     * 
     * @param cause
     *     the 'wrapped' exception
     */
    public TestingException( final Throwable cause )
        {

        super( cause ) ;

        }   // end 'simple wrapper' constructor without message


    /**
     * constructor with message and cause
     * 
     * @param message
     *     descriptive message related to the {@code cause}
     * @param cause
     *     the 'wrapped' exception
     */
    public TestingException( final String message, final Throwable cause )
        {

        super( message, cause ) ;

        }   // end 'wrapper' constructor with descriptive message

    }   // end class TestingException