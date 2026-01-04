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


package education.the_software_toolsmith.buildingblocks.enhanced ;

/**
 * demonstrate use of debugging features of enhanced Node
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-06-12 Initial implementation
 *
 * @since 1.0
 */
public class DemoEnhancedNode
    {

    /**
     * demo driver
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        // create a chain
        System.out.printf( "create a chain with 5 Nodes; data 1..5%n%n" ) ;
        Node<Integer> myChain = new Node<>( 5 ) ;
        myChain = new Node<>( 4, myChain ) ;
        myChain = new Node<>( 3, myChain ) ;
        myChain = new Node<>( 2, myChain ) ;
        myChain = new Node<>( 1, myChain ) ;

        // display the chain reference and contents
        System.out.printf( "myChain: %s%n", myChain ) ;
        System.out.printf( "myChain...: %s%n", Node.chainToString( myChain ) ) ;

        // create a loop in the chain then display its contents
        System.out.printf( "%ncreate a loop in the chain%n%n" ) ;
        myChain.getNext().getNext().setNext( myChain ) ;
        System.out.printf( "myChain...: %s%n", Node.chainToString( myChain ) ) ;

        }    // end main()

    }   // end class DemoEnhancedNode
