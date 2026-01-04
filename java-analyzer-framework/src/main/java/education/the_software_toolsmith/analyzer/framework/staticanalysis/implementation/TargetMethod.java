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


package education.the_software_toolsmith.analyzer.framework.staticanalysis.implementation ;


/**
 * target type for methods
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-23 Initial implementation
 */
@SuppressWarnings( "javadoc" )  // DMR FUTURE add Javadoc comments
public final class TargetMethod extends Target
    {

    /*
     * data fields
     */

    public final String methodSignature ;


    /*
     * constructors
     */
    public TargetMethod( final String signature )
        {

        this.methodSignature = signature ;

        }   // end constructor


    /*
     * public API methods
     */
    
    
    /**
     * @return the method signature
     */
    public String getMethodSignature()
        {
        
        return this.methodSignature ;
        
        }   // end getMethodSignature()


    // delegate the rest to the methodSignature's class (String)


    @Override
    public boolean equals( final Object other )
        {

        return this.methodSignature.equals( other ) ;

        }   // end equals()


    @Override
    public int hashCode()
        {

        return this.methodSignature.hashCode() ;

        }   // end hashCode()


    @Override
    public String toString()
        {

        return this.methodSignature ;

        }   // end toString()

    }   // end class TargetMethod
