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

import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.DATA_REFERENCE_MARKER ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.FIELD_SEPARATOR ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.NEXT_REFERENCE_MARKER ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.NULL_REFERENCE_MARKER ;
import static edu.wit.scds.ds.buildingblocks.utilities.VisualizationSupport.PSEUDO_ADDRESS_PREFIX ;

import java.util.ArrayList ;
import java.util.HashSet ;
import java.util.LinkedList ;
import java.util.List ;

/**
 * Class {@code Node} provides the basis for singly-linked chain functionality<br>
 * based on material from Carrano and Henry's "Data Structures and Abstractions with Java", 4th edition and
 * from the JCL
 * <p>
 * Note: This enhanced implementation includes display methods for educational purposes.
 * </p>
 *
 * @author Frank M. Carrano
 * @author Timothy M. Henry
 *
 * @version 4.1
 *
 * @author David M Rosenberg
 *
 * @version 4.6 2020-01-28 extracted {@code Node} from {@code LinkedBag}
 * @version 4.7 2020-08-08 switched class from package-private to {@code public}
 * @version 4.8 2020-11-04 add {@code toString()} to aid debugging
 * @version 4.9 2021-06-09 add {@code chainToString()} to aid debugging
 * @version 4.10 2021-06-13
 *     <ul>
 *     <li>rename {@code id} and {@code nextId} to {@code pseudoAddress} and {@code nextPseudoAddress} to
 *     better reflect their purpose
 *     <li>add getter for {@code pseudoAddress}
 *     <li>add 1-arg convenience method {@code chainToString()}
 *     </ul>
 * @version 4.11 2021-09-27 enhance {@code toString()} to handle {@code null} {@code data}
 * @version 4.12 2021-10-24 add no-arg constructor
 * @version 4.13 2021-11-18
 *     <ul>
 *     <li>replace "null" pointer text with "&bullet;" in {@code toString()} and {@code chainToString()} for
 *     consistency with in-class demos
 *     <li>revise {@code toString()} to reduce space and better resemble an in-class diagram of a {@code Node}
 *     </ul>
 * @version 4.14 2022-09-29 minor cosmetic changes
 * @version 4.15 2022-10-30
 *     <ul>
 *     <li>minor cosmetic changes
 *     <li>change arrow to {@code data} references to better distinguish from {@code next} references
 *     </ul>
 * @version 4.16 2022-11-09
 *     <ul>
 *     <li>change displayed field divider from '|' to '¦'
 *     <li>make pseudo address prefix symbolic
 *     </ul>
 * @version 5.0 2024-02-15
 *     <ul>
 *     <li>rename getter/setter for next field to match field name
 *     <li>move expanded set of symbolic constants for debugging, etc. display to separate class
 *     </ul>
 * @version 5.1 2024-09-17
 *     <ul>
 *     <li>switch from local constants to {@code VisualizationSupport}
 *     <li>add data and next reference markers even if null to clarify which is which
 *     </ul>
 * @version 5.1 2024-10-06 replace literal right arrow (next reference) with symbolic equivalent in
 *     {@code chainToString()}
 * @version 5.2 2024-10-11 remove unnecessary casts
 * @version 5.3 2025-01-22 make the class {@code final}
 * @version 6.2 2025-01-26
 *     <ul>
 *     <li>add 1-arg constructor which takes a {@code Node} reference
 *     <li>rename the parameter for the 1-arg constructor which takes a data reference to be consistent with
 *     naming convention
 *     </ul>
 * @version 7.0 2025-01-28
 *     <ul>
 *     <li>add return prior values to setter methods
 *     <li>add {@code hasLoop()}
 *     <li>enhance {@code main()} to exercise new functionality
 *     </ul>
 * @version 7.1 2025-03-19 add {@code DEFAULT_INITIAL_PSEUDO_ADDRESS}, {@code resetNextPseudoAddress()} and
 *     {@code setNextPseudoAddress()} to support unit testing so the ids in corresponding {@code Node}s in
 *     user's chain match those in the expected/correct chain
 * @version 8.0 2025-05-20 and 2025-05-22
 *     <ul>
 *     <li>track several changes/enhancements from the basic {@code Node}
 *     <li>add return prior values to setter methods
 *     <li>merge changes/enhancements from another development thread
 *     </ul>
 * @version 8.1 2025-09-27 add various {@code toList()} and {@code toListOfNodes()}
 * @version 8.2 2025-11-14 remove unnecessary code remnant from copy/paste
 *
 * @param <T>
 *     placeholder for the class of item a {@code Node} can reference
 */
public final class Node<T>
    {

    /*
     * symbolic constants
     */


    /** default initial value for pseudo addresses */
    private static final int DEFAULT_INITIAL_PSEUDO_ADDRESS = 1 ;


    /*
     * static fields
     */


    /** values for pseudo addresses */
    private static int nextPseudoAddress = DEFAULT_INITIAL_PSEUDO_ADDRESS ;


    /*
     * instance state/data fields
     */


    /** aid to debugging and visualization */
    public final int pseudoAddress ;

    /** link to the next node in the chain */
    private Node<T> next ;

    /** reference to the entry */
    private T data ;


    /*
     * constructors
     */


    /**
     * Sets up a node: {@code data} and {@code next} pointers are set to {@code null}.
     */
    public Node()
        {

        this( null, null ) ;

        }   // end no-arg constructor


    /**
     * Sets up a node given supplied {@code data}; {@code next} pointer is set to {@code null}.
     *
     * @param initialData
     *     the information this node holds
     */
    public Node( final T initialData )
        {

        this( initialData, null ) ;

        }   // end 1-arg (data) constructor


    /**
     * Sets up a node given supplied {@code next}; {@code data} pointer is set to {@code null}.
     *
     * @param initialNext
     *     a reference to the next {@code Node} in the chain
     */
    public Node( final Node<T> initialNext )
        {

        this( null, initialNext ) ;

        }   // end 1-arg (next) constructor


    /**
     * Sets up a node given supplied {@code data} and {@code next} references.
     *
     * @param initialData
     *     the information this node holds
     * @param nextNode
     *     reference to the next node in the linked list
     */
    public Node( final T initialData,
                 final Node<T> nextNode )
        {

        // basic initialization
        this.data = initialData ;
        this.next = nextNode ;

        // enhanced initialization
        this.pseudoAddress = nextPseudoAddress++ ;

        }   // end 2-arg constructor


    /*
     * public API methods: component manipulations
     */


    /**
     * Retrieve the data referenced by this node
     *
     * @return a reference to the data stored in this node
     */
    public T getData()
        {

        return this.data ;

        }   // end getData()


    /**
     * Retrieve the {@code next} field
     *
     * @return reference to the next {@code Node} in the chain
     */
    public Node<T> getNext()
        {

        return this.next ;

        }   // end getNext()


    /**
     * Retrieve the {@code pseudoAddress} field
     *
     * @return this node's pseudoAddress
     */
    public int getPseudoAddress()
        {

        return this.pseudoAddress ;

        }   // end getPseudoAddress()


    /**
     * reset the next pseudo address number to its default initial value
     * <p>
     * this supports detailed logging with application's addresses matching the expected/correct {@code Node}s
     * addresses
     *
     * @return the previous value of nextPseudoAddress
     *
     * @since 5.3
     */
    public static int resetNextPseudoAddress()
        {

        return setNextPseudoAddress( DEFAULT_INITIAL_PSEUDO_ADDRESS ) ;

        }   // end resetNextPseudoAddress()


    /**
     * Points the {@code data} field at the supplied data
     *
     * @param newData
     *     reference to the data to store
     *
     * @return reference to the prior data stored in this node
     */
    public T setData( final T newData )
        {

        final T oldData = this.data ;
        this.data = newData ;

        return oldData ;

        }   // end setData()


    /**
     * specify the next pseudo address number
     * <p>
     * this supports detailed logging with application's addresses matching the expected/correct {@code Node}s
     * addresses
     *
     * @param newNextPseudoAddress
     *     the new value to be used as the nextPseudoAddress
     *
     * @return the previous value of nextPseudoAddress
     *
     * @since 5.3
     */
    public static int setNextPseudoAddress( final int newNextPseudoAddress )
        {

        if ( newNextPseudoAddress < DEFAULT_INITIAL_PSEUDO_ADDRESS )
            {
            throw new IllegalArgumentException( String.format( "%,d is not a valid pseudo address; minimum is %,d",
                                                               newNextPseudoAddress,
                                                               DEFAULT_INITIAL_PSEUDO_ADDRESS ) ) ;
            }

        final int previousPseudoAddress = Node.nextPseudoAddress ;
        Node.nextPseudoAddress = newNextPseudoAddress ;
        return previousPseudoAddress ;

        }   // end setNextPseudoAddress()


    /**
     * Set the {@code next} field
     *
     * @param nextNode
     *     another {@code Node} in the chain or {@code null} to indicate none
     *
     * @return reference to the prior next {@code Node} reference
     */
    public Node<T> setNext( final Node<T> nextNode )
        {

        final Node<T> oldNext = this.next ;

        this.next = nextNode ;

        return oldNext ;

        } // end setNext()


    /*
     * public API methods: debugging and visualization tools
     */


    /**
     * generates a {@code String} representation of a chain of {@code Node}s
     * <p>
     * this is a debugging aid
     *
     * @return a text representation of the chain of {@code Node}s
     */
    public String chainToString()
        {

        return chainToString( this ) ;

        }   // end no-arg instance chainToString()


    /**
     * generates a String representation of a chain of {@code Node}s
     * <p>
     * this is a debugging aid
     *
     * @param includePosition
     *     flag to include ({@code true}) or omit ({@code false}) each {@code Node}'s position starting with
     *     the first {@code Node} in the chain - each position is equivalent to the index in a corresponding
     *     array-backed collection
     *
     * @return a text representation of the chain of {@code Node}s
     */
    public String chainToString( final boolean includePosition )
        {

        return chainToString( this, includePosition ) ;

        }   // end 1-arg instance chainToString()


    /**
     * convenience method - generates a String representation of a chain of {@code Node}s
     * <p>
     * this is a debugging aid
     *
     * @param firstNode
     *     the starting point for the chain, typically {@code this.firstNode} (or similar) from the collection
     *
     * @return a text representation of the chain of {@code Node}s
     */
    public static String chainToString( final Node<?> firstNode )
        {

        return chainToString( firstNode, false ) ;

        }   // end 1-arg static chainToString()


    /**
     * generates a {@code String} representation of a chain of {@code Node}s
     * <p>
     * this is a debugging aid
     *
     * @param startNode
     *     the starting point for the chain, typically {@code this.firstNode} from the collection
     * @param includePosition
     *     flag to include ({@code true}) or omit ({@code false}) each {@code Node}'s position starting with
     *     the first {@code Node} in the chain - each position is equivalent to the index in a corresponding
     *     array-backed collection
     *
     * @return a text representation of the chain of {@code Node}s
     */
    public static String chainToString( final Node<?> startNode,
                                        final boolean includePosition )
        {

        if ( startNode == null )
            {
            return NULL_REFERENCE_MARKER ;
            }

        final StringBuilder result = new StringBuilder() ;

        final HashSet<Node<?>> nodesVisited = new HashSet<>() ;

        Node<?> currentNode = startNode ;
        int currentPosition = 0 ;

        // first node
        nodesVisited.add( currentNode ) ;

        if ( includePosition )
            {
            result.append( String.format( "[%,d] ", currentPosition ) ) ;
            currentPosition++ ;
            }

        result.append( currentNode.toString() ) ;

        // rest of chain
        currentNode = currentNode.getNext() ;

        while ( currentNode != null )
            {
            result.append( " " + NEXT_REFERENCE_MARKER + " " ) ;

            if ( ! nodesVisited.add( currentNode ) )
                {
                result.append( "loop detected in chain" ) ;

                break ;
                }

            if ( includePosition )
                {
                result.append( String.format( "[%,d] ", currentPosition ) ) ;
                currentPosition++ ;
                }

            result.append( currentNode.toString() ) ;

            currentNode = currentNode.getNext() ;
            }

        return result.toString() ;

        }   // end 2-arg static chainToString()


    /**
     * checks a chain of {@code Node}s for a loop
     * <p>
     * this is a debugging aid
     *
     * @return {@code true} if a loop is detected; {@code false} otherwise
     */
    public boolean hasLoop()
        {

        return hasLoop( this ) ;

        }   // end instance hasLoop()


    /**
     * checks a chain of {@code Node}s for a loop
     * <p>
     * this is a debugging aid
     *
     * @param startNode
     *     the starting point for the chain, typically {@code this.firstNode} from the collection
     *
     * @return {@code true} if a loop is detected; {@code false} otherwise
     */
    public static boolean hasLoop( final Node<?> startNode )
        {

        if ( startNode == null )
            {
            return false ;
            }

        final HashSet<Node<?>> nodesVisited = new HashSet<>() ;

        Node<?> currentNode = startNode ;

        while ( currentNode != null )
            {

            if ( ! nodesVisited.add( currentNode ) )
                {
                return true ;
                }

            currentNode = currentNode.getNext() ;
            }

        return false ;

        }   // end static hasLoop()


    /**
     * populates a {@code List} with the contents of this chain
     * <p>
     * this is a debugging aid
     *
     * @return the contents of the chain in the order in which they occur in the chain
     *
     * @throws IllegalStateException
     *     if a loop is detected in the chain
     *
     * @since 8.1
     */
    public List<T> toList() throws IllegalStateException
        {

        final List<T> contents = new LinkedList<>() ;

        toList( contents ) ;

        return contents ;

        }   // end no-arg instance toList()


    /**
     * populates a {@code List} with the contents of this chain
     * <p>
     * this is a debugging aid
     *
     * @param contents
     *     will contain the contents of the chain in the order in which they occur in the chain, excluding the
     *     duplicate if a loop is detected
     *     <p>
     *     the list is cleared before (re-)populating it
     *
     * @return the number of {@code Node}s visited, includes the looping {@code Node} if loop is detected
     *
     * @throws IllegalStateException
     *     if a loop is detected in the chain
     * @throws NullPointerException
     *     if {@code contents} is {@code null}
     *
     * @since 8.1
     */
    public int toList( final List<T> contents ) throws IllegalStateException, NullPointerException
        {

        if ( contents == null )
            {
            throw new NullPointerException( "no contents List provided" ) ;
            }

        int nodeCount = 0 ;

        contents.clear() ;

        final HashSet<Node<T>> nodesVisited = new HashSet<>() ;

        // traverse chain saving referenced data
        Node<T> currentNode = this ;
        Node<T> previousNode = this ;

        while ( currentNode != null )
            {
            nodeCount++ ;

            if ( ! nodesVisited.add( currentNode ) )
                {
                // can't get here first time through loop
                throw new IllegalStateException( String.format( "loop detected in chain: Node %s%,d references Node %s%,d",
                                                                PSEUDO_ADDRESS_PREFIX,
                                                                previousNode.pseudoAddress,
                                                                PSEUDO_ADDRESS_PREFIX,
                                                                currentNode.pseudoAddress ) ) ;
                }

            contents.add( currentNode.data ) ;

            previousNode = currentNode ;
            currentNode = currentNode.getNext() ;
            }

        return nodeCount ;

        }   // end 1-arg instance toList()


    /**
     * populates a {@code List} with the contents of a chain of {@code Node}s
     * <p>
     * this is a debugging aid
     *
     * @param <T>
     *     type placeholder for the contents of the chain
     * @param startNode
     *     the starting point for the chain, typically {@code this.firstNode} from the collection
     *
     * @return the contents of the chain in the order in which they occur in the chain or {@code null} if
     *     {@code startNode} is {@code null}
     *
     * @throws IllegalStateException
     *     if a loop is detected in the chain
     *
     * @since 8.1
     */
    public static <T> List<T> toList( final Node<T> startNode ) throws IllegalStateException
        {

        if ( startNode == null )
            {
            return null ;
            }

        final List<T> contents = new LinkedList<>() ;

        startNode.toList( contents ) ;

        return contents ;

        }   // end 1-arg static toList()


    /**
     * populates a {@code List} with the contents of a chain of {@code Node}s
     * <p>
     * this is a debugging aid
     *
     * @param <T>
     *     type placeholder for the contents of the chain
     * @param startNode
     *     the starting point for the chain, typically {@code this.firstNode} from the collection
     * @param contents
     *     will contain the contents of the chain in the order in which they occur in the chain, excluding the
     *     duplicate if a loop is detected; the list is cleared before (re-)populating it
     *
     * @return the number of {@code Node}s visited, includes the looping {@code Node} if loop is detected; -1
     *     if {@code startNode} is {@code null}
     *
     * @throws IllegalStateException
     *     if a loop is detected in the chain
     * @throws NullPointerException
     *     if {@code contents} is {@code null}
     *
     * @since 8.1
     */
    public static <T> int toList( final Node<T> startNode,
                                  final List<T> contents ) throws IllegalStateException, NullPointerException
        {

        if ( startNode == null )
            {
            return -1 ;
            }

        return startNode.toList( contents ) ;

        }   // end 2-arg static toList()


    /**
     * populates a {@code List} with the {@code Node}s in this chain
     * <p>
     * this is a debugging aid
     *
     * @return the {@code Node}s in the chain in the order in which they occur in the chain, excluding the
     *     duplicate if a loop is detected
     *
     * @throws IllegalStateException
     *     if a loop is detected in the chain
     *
     * @since 8.1
     */
    public List<Node<T>> toListOfNodes() throws IllegalStateException
        {

        final List<Node<T>> listOfNodes = new LinkedList<>() ;

        toListOfNodes( listOfNodes ) ;

        return listOfNodes ;

        }   // end no-arg instance toListOfNodes()


    /**
     * populates a {@code List} with the {@code Node}s in this chain
     * <p>
     * this is a debugging aid
     *
     * @param listOfNodes
     *     will contain the {@code Node}s in the chain in the order in which they occur in the chain,
     *     excluding the duplicate if a loop is detected
     *     <p>
     *     the list is cleared before (re-)populating it
     *
     * @return the number of {@code Node}s visited, includes the looping {@code Node} if loop is detected
     *
     * @throws IllegalStateException
     *     if a loop is detected in the chain
     * @throws NullPointerException
     *     if {@code contents} is {@code null}
     *
     * @since 8.1
     */
    public int toListOfNodes( final List<Node<T>> listOfNodes ) throws IllegalStateException,
                                                                NullPointerException
        {

        if ( listOfNodes == null )
            {
            throw new NullPointerException( "no listOfNodes List provided" ) ;
            }

        int nodeCount = 0 ;

        listOfNodes.clear() ;

        final HashSet<Node<T>> nodesVisited = new HashSet<>() ;

        // traverse chain saving referenced data
        Node<T> currentNode = this ;
        Node<T> previousNode = this ;

        while ( currentNode != null )
            {
            nodeCount++ ;

            if ( ! nodesVisited.add( currentNode ) )
                {
                // can't get here first time through loop
                throw new IllegalStateException( String.format( "loop detected in chain: Node %s%,d references Node %s%,d",
                                                                PSEUDO_ADDRESS_PREFIX,
                                                                previousNode.pseudoAddress,
                                                                PSEUDO_ADDRESS_PREFIX,
                                                                currentNode.pseudoAddress ) ) ;
                }

            listOfNodes.add( currentNode ) ;

            previousNode = currentNode ;
            currentNode = currentNode.getNext() ;
            }

        return nodeCount ;

        }   // end 1-arg instance toListOfNodes()


    /**
     * populates a {@code List} with the {@code Node}s of a chain
     * <p>
     * this is a debugging aid
     *
     * @param <T>
     *     type placeholder for the contents of the chain
     * @param startNode
     *     the starting point for the chain, typically {@code this.firstNode} from the collection
     *
     * @return the {@code Node}s in the chain in the order in which they occur in the chain, excluding the
     *     duplicate if a loop is detected; or {@code null} if {@code startNode} is {@code null}
     *
     * @throws IllegalStateException
     *     if a loop is detected in the chain
     *
     * @since 8.1
     */
    public static <T> List<Node<T>> toListOfNodes( final Node<T> startNode ) throws IllegalStateException
        {

        if ( startNode == null )
            {
            return null ;
            }

        final List<Node<T>> listOfNodes = new LinkedList<>() ;

        startNode.toListOfNodes( listOfNodes ) ;

        return listOfNodes ;

        }   // end 1-arg static toListOfNodes()


    /**
     * populates a {@code List} with the {@code Node}s of a chain
     * <p>
     * this is a debugging aid
     *
     * @param <T>
     *     type placeholder for the contents of the chain
     * @param startNode
     *     the starting point for the chain, typically {@code this.firstNode} from the collection
     * @param listOfNodes
     *     will contain the {@code Node}s in the chain in the order in which they occur in the chain,
     *     excluding the duplicate if a loop is detected
     *     <p>
     *     the list is cleared before (re-)populating it
     *
     * @return the number of {@code Node}s visited; or -1 if {@code startNode} is {@code null}
     *
     * @throws IllegalStateException
     *     if a loop is detected in the chain
     * @throws NullPointerException
     *     if {@code contents} is {@code null}
     *
     * @since 8.1
     */
    public static <T> int toListOfNodes( final Node<T> startNode,
                                         final List<Node<T>> listOfNodes ) throws IllegalStateException,
                                                                           NullPointerException
        {

        if ( startNode == null )
            {
            return -1 ;
            }

        return startNode.toListOfNodes( listOfNodes ) ;

        }   // end 2-arg static toListOfNodes()


    /**
     * {@code toString()} is generally not implemented
     * <p>
     * this is a debugging aid
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String toString()
        {

        return String.format( "%s%,d:[%s%s%s%s%s]",
                              PSEUDO_ADDRESS_PREFIX,
                              this.pseudoAddress,
                              DATA_REFERENCE_MARKER,
                              this.data == null
                                      ? NULL_REFERENCE_MARKER
                                      : this.data,
                              FIELD_SEPARATOR,
                              NEXT_REFERENCE_MARKER,
                              this.next == null
                                      ? NULL_REFERENCE_MARKER
                                      : String.format( "%s%,d",
                                                       PSEUDO_ADDRESS_PREFIX,
                                                       this.next.pseudoAddress ) ) ;

        }   // end toString()


    /**
     * test driver for {@code Node}
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        // construct a chain of Nodes
        Node<String> firstNode = null ;

        // to collect the contents of the chain
        List<String> contents = new ArrayList<>( 10 ) ;
        List<Node<String>> listOfNodes = new ArrayList<>( 10 ) ;
        System.out.printf( "no chain:%nhas loop: %b%nfirstNode: %s%n%n",
                           hasLoop( firstNode ),
                           chainToString( firstNode ) ) ;

        // first element
        firstNode = new Node<>() ;
        final Node<String> nullNode = firstNode ;    // for convenience to create a loop later

        System.out.printf( "added 'null' Node to the front:%nhas loop: %b%nfirstNode: %s%s%,d%n%s%n%n",
                           hasLoop( firstNode ),
                           NEXT_REFERENCE_MARKER,
                           PSEUDO_ADDRESS_PREFIX,
                           firstNode.getPseudoAddress(),
                           firstNode.chainToString() ) ;


        // second element
        firstNode = new Node<>( "A", firstNode ) ;

        System.out.printf( "added A to the front:%nhas loop: %b%nfirstNode: %s%s%,d%n%s%n%n",
                           hasLoop( firstNode ),
                           NEXT_REFERENCE_MARKER,
                           PSEUDO_ADDRESS_PREFIX,
                           firstNode.getPseudoAddress(),
                           firstNode.chainToString() ) ;


        // third element
        firstNode = new Node<>( "B", firstNode ) ;

        System.out.printf( "added B to the front:%nhas loop: %b%nfirstNode: %s%s%,d%n%s%n%n",
                           hasLoop( firstNode ),
                           NEXT_REFERENCE_MARKER,
                           PSEUDO_ADDRESS_PREFIX,
                           firstNode.getPseudoAddress(),
                           firstNode.chainToString() ) ;


        // fourth element
        firstNode = new Node<>( "C", firstNode ) ;
        final Node<String> cNode = firstNode ;    // for convenience to create another loop later

        System.out.printf( "added C to the front:%nhas loop: %b%nfirstNode: %s%s%,d%n%s%n%n",
                           hasLoop( firstNode ),
                           NEXT_REFERENCE_MARKER,
                           PSEUDO_ADDRESS_PREFIX,
                           firstNode.getPseudoAddress(),
                           firstNode.chainToString() ) ;


        // fifth element
        firstNode = new Node<>( "D", firstNode ) ;

        System.out.printf( "added D to the front:%nhas loop: %b%nfirstNode: %s%s%,d%n%s%n%n",
                           hasLoop( firstNode ),
                           NEXT_REFERENCE_MARKER,
                           PSEUDO_ADDRESS_PREFIX,
                           firstNode.getPseudoAddress(),
                           firstNode.chainToString() ) ;

        contents.add( "???" ) ;
        contents = firstNode.toList() ;
        System.out.printf( "firstNode.toList(): (%,d) %s%n%n", contents.size(), contents ) ;
        contents.add( "???" ) ;
        int itemCount = firstNode.toList( contents ) ;
        System.out.printf( "firstNode.toList(contents): %,d items; (%,d) %s%n%n",
                           itemCount,
                           contents.size(),
                           contents ) ;
        contents.add( "???" ) ;
        contents = toList( firstNode ) ;
        System.out.printf( "toList(firstNode): (%,d) %s%n%n", contents.size(), contents ) ;
        contents.add( "???" ) ;
        itemCount = toList( firstNode, contents ) ;
        System.out.printf( "toList(firstNode, contents): %,d items; (%,d) %s%n%n",
                           itemCount,
                           contents.size(),
                           contents ) ;

        itemCount = firstNode.toListOfNodes( listOfNodes ) ;
        System.out.printf( "firstNode.toListOfNodes(listOfNodes): %,d items; (%,d) %s%n%n",
                           itemCount,
                           listOfNodes.size(),
                           listOfNodes ) ;

        // create a loop
        nullNode.setNext( firstNode ) ;
        System.out.printf( "chain with loop:%nhas loop: %b%nfirstNode: %s%s%,d%n%s%n%n",
                           hasLoop( firstNode ),
                           NEXT_REFERENCE_MARKER,
                           PSEUDO_ADDRESS_PREFIX,
                           firstNode.getPseudoAddress(),
                           firstNode.chainToString() ) ;

        try
            {
            contents = new ArrayList<>( 10 ) ;
            contents.add( "???" ) ;
            itemCount = -2 ;

            itemCount = toList( firstNode, contents ) ;
            System.out.printf( "toList( firstNode, contents ): %,d items; (%,d) %s%n%n",
                               itemCount,
                               contents.size(),
                               contents ) ;
            System.out.printf( "uh, oh! shouldn't be here... 8~|%n%n" ) ;
            }
        catch ( final Exception e )
            {
            System.out.printf( "I'm ok.  Just got a(n) %s because %s,%n\tthat's alright 'cause I have (%,d) %s, anyway%n%n",
                               e.getClass().getSimpleName(),
                               e.getMessage(),
                               contents.size(),
                               contents ) ;
            }   // end try

        // create another loop
        nullNode.setNext( cNode ) ;
        System.out.printf( "chain with a different loop:%nhas loop: %b%nfirstNode: %s%s%,d%n%s%n%n",
                           hasLoop( firstNode ),
                           NEXT_REFERENCE_MARKER,
                           PSEUDO_ADDRESS_PREFIX,
                           firstNode.getPseudoAddress(),
                           firstNode.chainToString() ) ;

        try
            {
            contents.add( "???" ) ;
            contents = firstNode.toList() ;
            System.out.printf( "toList(): (%,d) %s%n%n", contents.size(), contents ) ;
            System.out.printf( "uh, oh! shouldn't be here... 8~|%n%n" ) ;
            }
        catch ( final Exception e )
            {
            System.out.printf( "I'm ok.  Just got a(n) %s because %s, that's all%n%n",
                               e.getClass().getSimpleName(),
                               e.getMessage() ) ;
            }   // end try

        // create one final loop
        nullNode.setNext( nullNode ) ;
        System.out.printf( "chain with last node looping back to itself:%nhas loop: %b%nfirstNode: %s%s%,d%n%s%n%n",
                           hasLoop( firstNode ),
                           NEXT_REFERENCE_MARKER,
                           PSEUDO_ADDRESS_PREFIX,
                           firstNode.getPseudoAddress(),
                           firstNode.chainToString() ) ;

        try
            {
            contents.add( "???" ) ;
            contents = firstNode.toList() ;
            System.out.printf( "toList(): (%,d) %s%n%n", contents.size(), contents ) ;
            System.out.printf( "uh, oh! shouldn't be here... 8~|%n%n" ) ;
            }
        catch ( final Exception e )
            {
            System.out.printf( "I'm ok.  Just got a(n) %s because %s, that's all%n%n",
                               e.getClass().getSimpleName(),
                               e.getMessage() ) ;
            }   // end try

        try
            {
            contents = new ArrayList<>( 10 ) ;
            itemCount = -2 ;

            itemCount = toList( firstNode, contents ) ;
            System.out.printf( "toList( firstNode, contents ): %,d items; (%,d) %s%n%n",
                               itemCount,
                               contents.size(),
                               contents ) ;
            System.out.printf( "uh, oh! shouldn't be here... 8~|%n%n" ) ;
            }
        catch ( final Exception e )
            {
            System.out.printf( "I'm ok.  Just got a(n) %s because %s,%n\tthat's alright 'cause I have (%,d) %s, anyway%n%n",
                               e.getClass().getSimpleName(),
                               e.getMessage(),
                               contents.size(),
                               contents ) ;
            }   // end try

        try
            {
            listOfNodes = new ArrayList<>( 10 ) ;
            itemCount = -2 ;

            itemCount = toListOfNodes( firstNode, listOfNodes ) ;
            System.out.printf( "toListOfNodes( firstNode, listOfNodes ): %,d items; (%,d) %s%n%n",
                               itemCount,
                               listOfNodes.size(),
                               listOfNodes ) ;
            System.out.printf( "uh, oh! shouldn't be here... 8~|%n%n" ) ;
            }
        catch ( final Exception e )
            {
            System.out.printf( "I'm ok.  Just got a(n) %s because %s,%n\tthat's alright 'cause I have (%,d) %s, anyway%n%n",
                               e.getClass().getSimpleName(),
                               e.getMessage(),
                               listOfNodes.size(),
                               listOfNodes ) ;
            }   // end try

        /* IN_PROCESS the remaining tests need to be fleshed out */
        try
            {
            System.out.printf( "1:%n" ) ;

            listOfNodes = toListOfNodes( (Node<String>) null ) ;

            System.out.printf( "null fN -> %s%n", listOfNodes ) ;
            }
        catch ( final Exception e )
            {
            e.printStackTrace() ;
            }   // end try

        try
            {
            System.out.printf( "2:%n" ) ;

            itemCount = toListOfNodes( firstNode, null ) ;
            }
        catch ( final Exception e )
            {
            e.printStackTrace() ;
            }   // end try

        try
            {
            System.out.printf( "3:%n" ) ;

            listOfNodes = new ArrayList<>( 10 ) ;

            itemCount = toListOfNodes( null, listOfNodes ) ;
            System.out.printf( "null lON -> (%,d) %s%n", itemCount, listOfNodes ) ;
            }
        catch ( final Exception e )
            {
            e.printStackTrace() ;
            }   // end try

        try
            {
            System.out.printf( "4:%n" ) ;

            itemCount = toListOfNodes( null, null ) ;
            System.out.printf( "null firstNode -> %,d items%n", itemCount ) ;
            }
        catch ( final Exception e )
            {
            e.printStackTrace() ;
            }   // end try

        }   // end main()

    }   // end class Node
