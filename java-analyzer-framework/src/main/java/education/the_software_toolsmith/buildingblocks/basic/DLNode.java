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
 * Class {@code DLNode} provides the basis for doubly-linked chain functionality.
 * <p>
 * Extracted from {@code LinkedDeque.java}.
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
 * @version 6.0 2022-10-30 Initial implementation based on enhanced {@code DLNode}
 * @version 6.1 2022-11-02 Remove unnecessary constants - remnants of visualization code<br>
 * @version 7.0 2024-03-19 Rename getter and setter methods to remove 'Node'
 * @version 7.0.1 2025-07-21 minor cosmetic changes
 *
 * @param <T>
 *     The class of item the {@code DLNode} will reference.
 */
public class DLNode<T>
    {

    /*
     * data fields
     */
    /** reference to the entry */
    private T data ;

    /** link to the next node in the chain */
    private DLNode<T> next ;

    /** link to the previous node in the chain */
    private DLNode<T> previous ;


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

    }   // end class DLNode
