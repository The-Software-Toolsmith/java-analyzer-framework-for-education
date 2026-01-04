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


package education.the_software_toolsmith.buildingblocks.basic ;

/**
 * Class {@code Node} provides the basis for singly-linked chain functionality<br>
 * based on material from Carrano and Henry's "Data Structures and Abstractions with Java", 4th edition and
 * from the JCL
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
 * @version 5.0 2022-09-29 Trimmed down to basic functionality
 * @version 6.0 2024-09-17 rename {@code get/setNextNode()} to {@code get/setNext()}
 * @version 6.1 2025-01-22 make the class final
 * @version 6.2 2025-01-26
 *     <ul>
 *     <li>add 1-arg constructor which takes a {@code Node} reference
 *     <li>rename the parameter for the 1-arg constructor which takes a data reference to be consistent with
 *     naming convention
 *     </ul>
 * @version 7.0 2025-01-28 add return prior values to setter methods
 *
 * @param <T>
 *     The class of item a {@code Node} can reference.
 */
public final class Node<T>
    {

    /*
     * symbolic constants
     */
    // none

    /*
     * data fields
     */
    /** reference to the entry */
    private T data ;
    /** link to the next node in the chain */
    private Node<T> next ;


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
     *
     * @since 6.2
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

        this.data = initialData ;
        this.next = nextNode ;

        }   // end 2-arg constructor


    /*
     * public API methods
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
     * Set the {@code next} field
     *
     * @param nextNode
     *     another {@code Node} in the chain or {@code null} to indicate none
     *
     * @return reference to the prior Node next reference
     */
    public Node<T> setNext( final Node<T> nextNode )
        {

        final Node<T> oldNext = this.next ;

        this.next = nextNode ;

        return oldNext ;

        } // end setNext()

    }   // end class Node
