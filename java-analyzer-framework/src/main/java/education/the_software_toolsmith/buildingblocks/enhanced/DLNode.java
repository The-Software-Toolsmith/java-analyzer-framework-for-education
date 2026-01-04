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

import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.BIDIRECTIONAL_SIBLING_REFERENCE_MARKER ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.DATA_REFERENCE_MARKER ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.FIELD_SEPARATOR ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.NEXT_REFERENCE_MARKER ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.NULL_REFERENCE_MARKER ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.PREVIOUS_REFERENCE_MARKER ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.PSEUDO_ADDRESS_PREFIX ;

import java.util.HashSet ;

/**
 * Class {@code DLNode} provides the basis for doubly-linked chain functionality.
 * <p>
 * Extracted from {@code LinkedDeque.java}.
 * <p>
 * Note: This enhanced implementation includes display methods for educational purposes.
 *
 * @author Frank M. Carrano
 * @author Timothy M. Henry
 *
 * @version 5.0
 *
 * @author David M Rosenberg
 *
 * @version 5.1 2020-10-23 revise 1-arg constructor to invoke 3-arg constructor
 * @version 5.2 2021-07-11
 *     <ul>
 *     <li>add {@code toString()} and {@code chainToString()}s to aid debugging (borrowed from enhanced
 *     {@code Node.java})
 *     <li>add {@code displayTraversals()}
 *     <li>enhance testing in {@code main()} to demonstrate several common errors in doubly-linked chain
 *     construction/manipulation
 *     </ul>
 * @version 5.3 2021-10-24
 *     <ul>
 *     <li>rename {@code id} and {@code nextId} to {@code pseudoAddress} and {@code nextPseudoAddress} to
 *     better reflect their purpose
 *     <li>add getter for {@code pseudoAddress}
 *     <li>add 1-arg convenience method {@code chainToString()}
 *     <li>enhance {@code toString()} to handle {@code null} {@code data}
 *     </ul>
 * @version 5.4 2022-10-30
 *     <ul>
 *     <li>add no-arg constructor
 *     <li>replace "null" pointer text with "&bullet;" in {@code toString()} and {@code chainToString()} for
 *     consistency with in-class demos
 *     <li>revise {@code toString()} to reduce space and better resemble an in-class diagram of a
 *     {@code DLNode}
 *     <li>track updates to {@code Node} to more clearly reflect similarities and differences
 *     </ul>
 * @version 5.5 2022-11-09
 *     <ul>
 *     <li>change displayed field divider from '|' to '¦'
 *     <li>make pseudo address prefix symbolic
 *     </ul>
 *     <br>
 * @version 6.0 2024-03-19 Rename getter and setter methods to remove 'Node'
 * @version 6.0.1 2025-07-21 minor cosmetic changes
 *
 * @param <T>
 *     The class of item the {@code DLNode} will reference.
 */
public class DLNode<T>
    {

    /*
     * traversal directions used with chainToString()
     */
    /** traverse chain by following {@code next} references */
    public static final boolean FORWARD_TRAVERSAL = true ;
    /** traverse chain by following {@code previous} references */
    public static final boolean BACKWARD_TRAVERSAL = false ;


    /*
     * data fields
     */
    /** reference to the entry */
    private T data ;

    /** link to the next node in the chain */
    private DLNode<T> next ;

    /** link to the previous node in the chain */
    private DLNode<T> previous ;

    /** values for pseudo addresses */
    private static int nextPseudoAddress = 1 ;

    /** for educational purposes only as an aid to debugging and visualization */
    public final int pseudoAddress ;


    /**
     * Sets up a node: {@code previous}, {@code data}, and {@code next} pointers are set to {@code null}.
     */
    public DLNode()
        {

        this( null, null, null ) ;

        }   // end no-arg constructor


    /**
     * Set up a {@code DLNode} given supplied {@code data}; {@code next} and {@code previous} references are
     * set to {@code null}.
     *
     * @param dataPortion
     *     the information this node holds
     */
    public DLNode( final T dataPortion )
        {

        this( null, dataPortion, null ) ;

        } // end 1-arg constructor


    /**
     * Set up a node given supplied {@code data}, {@code next}, and {@code previous} references.
     *
     * @param previousNode
     *     reference to the previous node in the linked list
     * @param dataPortion
     *     the information this node holds
     * @param nextNode
     *     reference to the next node in the linked list
     */
    public DLNode( final DLNode<T> previousNode,
                   final T dataPortion,
                   final DLNode<T> nextNode )
        {

        this.data = dataPortion ;
        this.next = nextNode ;
        this.previous = previousNode ;

        this.pseudoAddress = nextPseudoAddress++ ;    // set the id/pseudo-address

        } // end 3-arg constructor


    /**
     * Retrieve the {@code data} referenced by this {@code DLNode}
     *
     * @return a reference to the data stored in this {@code DLNode}
     */
    public T getData()
        {

        return this.data ;

        } // end getData()


    /**
     * Retrieve the {@code next} field
     *
     * @return reference to the next {@code DLNode} in the chain
     */
    public DLNode<T> getNext()
        {

        return this.next ;

        } // end getNext()


    /**
     * Retrieve the {@code previous} field
     *
     * @return reference to the previous {@code DLNode} in the chain
     */
    public DLNode<T> getPrevious()
        {

        return this.previous ;

        } // end getPrevious()


    /**
     * Point the {@code data} field at the supplied data
     *
     * @param newData
     *     reference to the data to store
     */
    public void setData( final T newData )
        {

        this.data = newData ;

        } // end setData()


    /**
     * Set the {@code next} field
     *
     * @param nextNode
     *     another {@code DLNode} in the chain or {@code null} to indicate none
     */
    public void setNext( final DLNode<T> nextNode )
        {

        this.next = nextNode ;

        } // end setNext()


    /**
     * Set the {@code previous} field
     *
     * @param previousNode
     *     another {@code DLNode} in the chain or {@code null} to indicate none
     */
    public void setPrevious( final DLNode<T> previousNode )
        {

        this.previous = previousNode ;

        } // end setPrevious()


    /**
     * Retrieve the pseudoAddress field
     *
     * @return this {@code DLNode}'s pseudoAddress
     */
    public int getPseudoAddress()
        {

        return this.pseudoAddress ;

        }   // end getPseudoAddress()


    // toString() is generally not implemented - I provided it as a debugging aid
    @Override
    public String toString()
        {

        return String.format( "%s%,d:[%s%s%s%s%s]",
                              PSEUDO_ADDRESS_PREFIX,
                              this.pseudoAddress,
                              this.previous == null
                                      ? NULL_REFERENCE_MARKER
                                      : String.format( "%s%,d%s",
                                                       PSEUDO_ADDRESS_PREFIX,
                                                       this.previous.pseudoAddress,
                                                       PREVIOUS_REFERENCE_MARKER ),
                              FIELD_SEPARATOR,
                              this.data == null
                                      ? NULL_REFERENCE_MARKER
                                      : DATA_REFERENCE_MARKER + this.data,
                              FIELD_SEPARATOR,
                              this.next == null
                                      ? NULL_REFERENCE_MARKER
                                      : String.format( "%s%s%,d",
                                                       NEXT_REFERENCE_MARKER,
                                                       PSEUDO_ADDRESS_PREFIX,
                                                       this.next.pseudoAddress ) ) ;

        }   // end toString()


    /**
     * convenience method - generates a {@code String} representation of a chain of {@code DLNode}s traversed
     * along its {@code next} references without positions
     * <p>
     * I provided this as a debugging aid
     *
     * @param firstNode
     *     the starting point for the chain, typically {@code this.firstNode} (or similar) from the collection
     *
     * @return a text representation of the chain of {@code DLNode}s
     */
    public static String chainToString( final DLNode<?> firstNode )
        {

        return chainToString( firstNode, true, false ) ;

        }   // end 1-arg chainToString()


    /**
     * convenience method - generates a {@code String} representation of a chain of {@code DLNode}s traversed
     * via the specified (true: next; false: previous) references without positions
     * <p>
     * I provided this as a debugging aid
     *
     * @param firstNode
     *     the starting point for the chain, typically @code{this.firstNode} from the collection
     * @param forwardTraversal
     *     flag to traverse the chain by following next references (true) or previous references (false)
     *
     * @return a text representation of the chain of {@code DLNode}s
     */
    public static String chainToString( final DLNode<?> firstNode,
                                        final boolean forwardTraversal )
        {

        return chainToString( firstNode, forwardTraversal, false ) ;

        }   // end 2-arg chainToString()


    /**
     * generates a String representation of a chain of @code{DLNode}s
     * <p>
     * I provided this as a debugging aid
     * <p>
     * NOTE: traversing the chain backwards and including the positions counts the index starting with
     * startNode as zero (0) and subsequent nodes appear as negative indices
     *
     * @param startNode
     *     the starting point for the chain, typically {@code firstNode} from the collection if traversing
     *     along {@code this.next} references, otherwise {@code lastNode} if traversing along
     *     {@code this.previous} references
     * @param forwardTraversal
     *     flag to traverse the chain by following next references (true) or previous references (false)
     * @param includePosition
     *     flag to include (true) or omit (false) each DLNode's position starting with the first DLNode in the
     *     chain - each position is equivalent to the index in a corresponding array-backed collection<br>
     *     indices will be negative when {@code forwardTraversal} is {@code false}
     *
     * @return a text representation of the chain of {@code DLNode}s
     */
    public static String chainToString( final DLNode<?> startNode,
                                        final boolean forwardTraversal,
                                        final boolean includePosition )
        {

        if ( startNode == null )
            {
            return NULL_REFERENCE_MARKER ;
            }

        final StringBuilder result = new StringBuilder() ;

        final HashSet<Integer> nodesVisited = new HashSet<>() ;

        DLNode<?> currentNode = startNode ;
        int currentPosition = 0 ;

        // first node
        nodesVisited.add( currentNode.pseudoAddress ) ;

        if ( includePosition )
            {
            result.append( String.format( "[%,d] ",
                                          forwardTraversal
                                                  ? currentPosition++
                                                  : currentPosition-- ) ) ;
            }

        result.append( currentNode.toString() ) ;

        // rest of chain
        currentNode = forwardTraversal
                ? currentNode.next
                : currentNode.previous ;

        while ( currentNode != null )
            {

            if ( forwardTraversal )
                {
                result.append( " " + BIDIRECTIONAL_SIBLING_REFERENCE_MARKER + " " ) ;
                }
            else
                {
                result.insert( 0, " " + BIDIRECTIONAL_SIBLING_REFERENCE_MARKER + " " ) ;
                }

            if ( ! nodesVisited.add( currentNode.pseudoAddress ) )
                {
                result.insert( forwardTraversal
                        ? result.length()
                        : 0, "loop detected in chain" ) ;

                break ;
                }

            String positionText = null ;

            if ( includePosition )
                {
                positionText = String.format( "[%,d] ",
                                              forwardTraversal
                                                      ? currentPosition++
                                                      : currentPosition-- ) ;

                if ( forwardTraversal )
                    {
                    result.append( positionText ) ;
                    }

                }

            result.insert( forwardTraversal
                    ? result.length()
                    : 0, currentNode.toString() ) ;

            if ( includePosition && ! forwardTraversal )
                {
                result.insert( 0, positionText ) ;
                }

            // move to adjacent node in the specified direction
            currentNode = forwardTraversal
                    ? currentNode.next
                    : currentNode.previous ;
            }

        return result.toString() ;

        }   // end 3-arg chainToString()


    /**
     * (optional) test driver for {@code DLNode}
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        System.out.printf( "null DLNode: %s%n%n", new DLNode<>( null ) ) ;

        // construct a chain of DLNodes
        DLNode<String> firstNode = null ;
        DLNode<String> lastNode = null ;
        DLNode<String> newNode = null ;

        // -----

        System.out.printf( "empty chain:%n%n" ) ;

        displayTraversals( firstNode, lastNode ) ;


        // first/last element
        final DLNode<String> aNode = newNode = new DLNode<>( "A" ) ;
        firstNode = newNode ;
        lastNode = newNode ;

        System.out.printf( "instantiate first node: %s%n%n", newNode.toString() ) ;
        System.out.printf( "single node in chain:%n%n" ) ;

        displayTraversals( firstNode, lastNode ) ;


        // second element
        final DLNode<String> bNode = newNode = new DLNode<>( lastNode, "B", null ) ;
        lastNode.next = newNode ;
        lastNode = newNode ;

        System.out.printf( "instantiate second node at end of chain: %s%n%n", newNode.toString() ) ;

        displayTraversals( firstNode, lastNode ) ;


        // third element
        final DLNode<String> cNode = newNode = new DLNode<>( lastNode, "C", null ) ;
        lastNode.next = newNode ;
        lastNode = newNode ;

        System.out.printf( "instantiate third node at end of chain: %s%n%n", newNode.toString() ) ;

        displayTraversals( firstNode, lastNode ) ;


        // fourth element
        final DLNode<String> dNode = newNode = new DLNode<>( lastNode, "D", null ) ;
        lastNode.next = newNode ;
        lastNode = newNode ;

        System.out.printf( "instantiate fourth node at end of chain: %s%n%n", newNode.toString() ) ;

        displayTraversals( firstNode, lastNode ) ;


        // fifth element
        @SuppressWarnings( "unused" )
        final DLNode<String> eNode = newNode = new DLNode<>( null, "E", firstNode ) ;
        firstNode.previous = newNode ;
        firstNode = newNode ;

        System.out.printf( "instantiate fifth node at beginning of chain: %s%n%n", newNode.toString() ) ;

        displayTraversals( firstNode, lastNode ) ;


        // traversals from a non-terminal element
        System.out.printf( "same chain starting at third node:%n%n" ) ;

        displayTraversals( "cNode", cNode, "cNode", cNode ) ;


        // create a loop: last -> first
        dNode.next = firstNode ;

        System.out.printf( "same chain with last node's next field pointing at the first node (loop):%n%n" ) ;

        displayTraversals( firstNode, lastNode ) ;


        // create another loop
        cNode.next = aNode ;

        System.out.printf( "same chain with third node's next field pointing at the first node (loop):%n%n" ) ;

        displayTraversals( firstNode, lastNode ) ;


        // create yet another loop
        firstNode.previous = bNode ;

        System.out.printf( "same chain with first node's previous field pointing at the second node (loop):%n%n" ) ;

        displayTraversals( firstNode, lastNode ) ;


        // broken chain
        aNode.next = cNode ;

        System.out.printf( "same chain with first node's next field pointing at the third node (corrupted):%n%n" ) ;

        displayTraversals( firstNode, lastNode ) ;


        // fried chain
        System.out.printf( "seriously fried chain with each node's next and previous fields pointing at the following node:%n%n" ) ;
        aNode.next = aNode.previous = bNode ;
        bNode.next = bNode.previous = cNode ;
        cNode.next = cNode.previous = dNode ;
        dNode.next = dNode.previous = null ;

        displayTraversals( firstNode, lastNode ) ;

        // fried chain
        System.out.printf( "same chain with last node pointing at first node:%n%n" ) ;
        dNode.next = dNode.previous = aNode ;

        displayTraversals( firstNode, lastNode ) ;


        // same chain but starting traversals at third node
        System.out.printf( "same chain starting at the third node:%n%n" ) ;

        displayTraversals( "cNode", cNode, "cNode", cNode ) ;

        System.out.printf( "with positions:%n%n%s%n%s%n%n",
                           chainToString( firstNode, FORWARD_TRAVERSAL, true ),
                           chainToString( lastNode, BACKWARD_TRAVERSAL, true ) ) ;

        }   // end main()


    /**
     * Display forward and backward traversals of a chain given its first and last nodes
     *
     * @param firstNode
     *     first node (beginning of chain)
     * @param lastNode
     *     last node (end of chain)
     */
    private static void displayTraversals( final DLNode<?> firstNode,
                                           final DLNode<?> lastNode )
        {

        displayTraversals( "firstNode", firstNode, "lastNode", lastNode ) ;

        }   // end 2-arg displayTraversals()


    /**
     * Display forward and backward traversals of a chain given its first and last nodes and descriptive
     * labels
     *
     * @param firstNodeLabel
     *     name of first node
     * @param firstNode
     *     first node (beginning of chain)
     * @param lastNodeLabel
     *     name of the last node
     * @param lastNode
     *     last node (end of chain)
     */
    private static void displayTraversals( final String firstNodeLabel,
                                           final DLNode<?> firstNode,
                                           final String lastNodeLabel,
                                           final DLNode<?> lastNode )
        {

        System.out.printf( "%s (forward): %s%n%n",
                           firstNodeLabel,
                           chainToString( firstNode, FORWARD_TRAVERSAL ) ) ;
        System.out.printf( "%s (backward): %s%n%n----------%n%n",
                           lastNodeLabel,
                           chainToString( lastNode, BACKWARD_TRAVERSAL ) ) ;

        }   // end displayTraversals()

    }   // end class DLNode
