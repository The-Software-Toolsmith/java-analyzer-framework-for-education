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


package education.the_software_toolsmith.buildingblocks.utilities ;

/**
 * The constants in this class support visualization and debugging tools, primarily in the {@code ...common}
 * package
 * <p>
 * Note: These enhancements are for educational purposes.
 * </p>
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2024-02-15 Initial implementation taken from {@code ...common.enhanced.Node}<br>
 * @version 1.1 2024-03-09 add {@code FIELD_SEPARATOR}
 * @version 1.2 2024-11-03 add ({@code BIDIRECTIONAL_SIBLING_REFERENCE_MARKER}
 * @version 1.3 2025-01-22 make the class final
 */
public final class VisualizationSupport
    {
    /*
     * symbolic constants for visualization of linked constructs
     */

    /** text to act as a field separator */
    public static final String FIELD_SEPARATOR = "¦" ;
    /** text to mark a pseudo address (to help distinguish it from an integer */
    public static final String PSEUDO_ADDRESS_PREFIX = "@" ;
    /** text to represent a {@code null} pointer */
    public static final String NULL_REFERENCE_MARKER = "•" ;    // "\u2022"
    /** text to represent a pointer to data */
    public static final String DATA_REFERENCE_MARKER = "↓" ;
    /** text to represent a pointer to the left data - for trees */
    public static final String LEFT_DATA_REFERENCE_MARKER = "↙" ;
    /** text to represent a pointer to the middle data - for trees */
    public static final String MIDDLE_DATA_REFERENCE_MARKER = "↓" ;
    /** text to represent a pointer to the right data - for trees */
    public static final String RIGHT_DATA_REFERENCE_MARKER = "↘" ;
    /** text to represent a pointer to the next {@code Node} */
    public static final String NEXT_REFERENCE_MARKER = "→" ;
    /** text to represent a pointer to the previous {@code Node} - for doubly linked chains */
    public static final String PREVIOUS_REFERENCE_MARKER = "←" ;
    /** text to represent a pointer to the left child {@code Node} - for trees */
    public static final String LEFT_CHILD_REFERENCE_MARKER = "↙" ;
    /** text to represent a pointer to the right child {@code Node} - for trees */
    public static final String RIGHT_CHILD_REFERENCE_MARKER = "↘" ;
    /** text to represent a pointer to the right sibling {@code Node} - for trees */
    public static final String RIGHT_SIBLING_REFERENCE_MARKER = "→" ;
    /** text to represent a pointer to the parent {@code Node} - for trees */
    public static final String PARENT_REFERENCE_MARKER = "↑" ;
    /** text to represent a pointer to the left child's parent {@code Node} - for trees */
    public static final String LEFT_CHILD_TO_PARENT_REFERENCE_MARKER = "↗" ;
    /** text to represent a pointer to the right child's parent {@code Node} - for trees */
    public static final String RIGHT_CHILD_TO_PARENT_REFERENCE_MARKER = "↖" ;
    /** text to represent bi-directional references between two sibling nodes */
    public static final String BIDIRECTIONAL_SIBLING_REFERENCE_MARKER = "←→" ;


    /*
     * data fields
     */
    // not applicable


    /*
     * constructors
     */
    /** prevent instantiation */
    private VisualizationSupport()
        {}


    /*
     * API methods
     */
    // not applicable


    /*
     * private utility methods
     */
    // not applicable

    }   // end class VisualizationSupport
