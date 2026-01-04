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


package education.the_software_toolsmith.analyzer.framework.utilities.ui ;

import static javax.swing.JFileChooser.APPROVE_OPTION ;
import static javax.swing.JFileChooser.CANCEL_OPTION ;
import static javax.swing.JFileChooser.DIRECTORIES_ONLY ;
import static javax.swing.JFileChooser.ERROR_OPTION ;
import static javax.swing.JFileChooser.FILES_AND_DIRECTORIES ;
import static javax.swing.JFileChooser.FILES_ONLY ;
import static javax.swing.JFileChooser.OPEN_DIALOG ;
import static javax.swing.JFileChooser.SAVE_DIALOG ;

import java.io.File ;
import java.nio.file.Path ;
import java.util.ConcurrentModificationException ;
import java.util.LinkedList ;
import java.util.List ;
import java.util.ListIterator ;

import javax.swing.JFileChooser ;
import javax.swing.filechooser.FileFilter ;


/**
 * enable user to select files and directories graphically
 *
 * @author David M Rosenberg
 *
 * @version 1.0 2025-12-18 Initial implementation based upon FileChooserExample from the Java Tutorials
 */
public final class FileSystemUI implements Iterable<Path>
    {

    /*
     * constants
     */

    /** default value for: start-in path */
    public static final Path DEFAULT_START_IN = null ;
    /** default value for: operation */
    public static final OpenOrSave DEFAULT_OPEN_OR_SAVE = OpenOrSave.OPEN ;
    /** default value for: plurality */
    public static final OneOrMultiple DEFAULT_ONE_OR_MULTIPLE = OneOrMultiple.ONE ;
    /** default value for: item type */
    public static final FilesDirectoriesOrAny DEFAULT_FILES_DIRECTORIES_OR_ANY = FilesDirectoriesOrAny.FILES ;
    /** default value for: approve button text */
    public static final String DEFAULT_APPROVE_BUTTON_TEXT = "Select" ;
    /** default value for: switch to enable/disable display of hidden files */
    public static final boolean DEFAULT_SHOW_HIDDEN_FILES = false ;
    /** default value for: switch to enable/disable inclusion of *.* as a selection filter option */
    public static final boolean DEFAULT_ALLOW_ALL_FILE_TYPES_FILTER = true ;
    /** default value for: an application-specific file filter */
    public static final FileFilter DEFAULT_FILE_FILTER = null ;
    /** default value for: an application-specific file filter */
    public static final FileFilter DEFAULT_CHOOSABLE_FILE_FILTER = null ;


    /*
     * static fields
     */
    // none


    /*
     * data fields
     */

    // location
    private Path startIn ;

    // action
    private OpenOrSave openOrSave ;
    private OneOrMultiple oneOrMultiple ;
    private FilesDirectoriesOrAny filesDirectoriesOrAny ;

    // filtering
    private boolean showHiddenFiles ;
    private boolean allowAllFilesTypeFilter ;
    private FileFilter fileFilter ;
    private FileFilter choosableFileFilter ;

    // interface
    private String dialogTitle ;
    private String approveButtonText ;

    // swing state
    private final boolean isHeadless ;

    // swing components
    private final JFileChooser fileChooser ;

    // results
    private final List<Path> selectedItems ;

    private ListIterator<Path> selectedItemsIterator ;
    private boolean invalidateIterator ;    // support fail-fast iterator


    /*
     * constructors
     */


    /**
     * set initial state to select a single file starting in the user's Documents (Windows) or home (Unix)
     * directory
     */
    public FileSystemUI()
        {

        // determine operating environment
        this.isHeadless = java.awt.GraphicsEnvironment.isHeadless() ;

        // create a file chooser
        this.fileChooser = new JFileChooser() ;

        // someplace to store selected items
        this.selectedItems = new LinkedList<>() ;


        // finish setting initial (default) configuration
        initializeState() ;

        }   // end no-arg constructor


    /*
     * getters and setters
     */


    /**
     * @return the headless state:
     *     <ul>
     *     <li>{@code true} indicates that there are no user interface devices available
     *     <li>{@code false} indicates there is a viable user interface mechanism available
     *     </ul>
     */
    public boolean isHeadless()
        {

        return this.isHeadless ;

        }   // end isHeadless()


    /*
     * selection controls
     */


    /**
     * @return the mode: open or save
     */
    public OpenOrSave getOpenOrSave()
        {

        return this.openOrSave ;

        }   // end getOpenOrSave()


    /**
     * user will choose file(s) or directory(s) to open
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setForOpening()
        {

        // if switching from saving to opening, forget about the last choice to avoid confusion
        if ( this.openOrSave != OpenOrSave.OPEN )
            {
            // support fail-fast iterator
            modifiedState() ;

            // forget any previous choices
            clear() ;

            this.openOrSave = OpenOrSave.OPEN ;

            this.fileChooser.setDialogType( this.openOrSave.getDialogType() ) ;
            }

        return this ;   // for fluent chaining

        }   // end setForOpening()


    /**
     * user will choose a file or directory to save
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setForSaving()
        {

        // if switching from opening to saving, forget about the last choice to avoid confusion
        if ( this.openOrSave != OpenOrSave.SAVE )
            {
            // support fail-fast iterator
            modifiedState() ;

            // forget any previous choices
            clear() ;

            this.openOrSave = OpenOrSave.SAVE ;

            this.fileChooser.setDialogType( this.openOrSave.getDialogType() ) ;
            }

        return this ;   // for fluent chaining

        }   // end setForSaving()


    /**
     * cause the file filter drop-down list to include {@code "*.*"}
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI allowAllFilesTypeFilter()
        {

        this.allowAllFilesTypeFilter = true ;

        this.fileChooser.setAcceptAllFileFilterUsed( this.allowAllFilesTypeFilter ) ;

        return this ;   // for fluent chaining

        }   // end allowAllFilesTypeFilter()


    /**
     * PLACEHOLDER
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI disallowAllFilesTypeFilter()
        {

        this.allowAllFilesTypeFilter = false ;

        this.fileChooser.setAcceptAllFileFilterUsed( this.allowAllFilesTypeFilter ) ;

        return this ;   // for fluent chaining

        }   // end allowAllFilesTypeFilter()


    /**
     * @return the fileFilter
     */
    public FileFilter getChoosableFileFilter()
        {

        return this.choosableFileFilter ;

        }   // end getChoosableFileFilter()


    /**
     * @param newFileFilter
     *     the new fileFilter
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setChoosableFileFilter( final FileFilter newFileFilter )
        {

        this.fileFilter = newFileFilter ;
        this.fileChooser.addChoosableFileFilter( this.choosableFileFilter ) ;

        return this ;   // for fluent chaining

        }   // end setChoosableFileTypesFilter()


    /**
     * @return the fileFilter
     */
    public FileFilter getFileFilter()
        {

        return this.fileFilter ;

        }   // end getFileFilter()


    /**
     * @param newFileFilter
     *     the new fileFilter
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setFileFilter( final FileFilter newFileFilter )
        {

        this.fileFilter = newFileFilter ;
        this.fileChooser.setFileFilter( this.fileFilter ) ;

        return this ;   // for fluent chaining

        }   // end setFileFilter()


    /**
     * @return the filesDirectoriesOrAny
     */
    public FilesDirectoriesOrAny getChooseFilesDirectoriesOrAny()
        {

        return this.filesDirectoriesOrAny ;

        }   // end getChooseFilesDirectoriesOrAny()


    /**
     * @param newChooseFilesDirectoriesOrAny
     *     the filesDirectoriesOrAny to set
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI
           setChooseFilesDirectoriesOrAny( final FilesDirectoriesOrAny newChooseFilesDirectoriesOrAny )
        {

        this.filesDirectoriesOrAny = newChooseFilesDirectoriesOrAny ;

        this.fileChooser.setFileSelectionMode( this.filesDirectoriesOrAny.getJFileChooserMode() ) ;

        return this ;   // for fluent chaining

        }   // end setChooseFilesDirectoriesOrAny()


    /**
     * @return the showHiddenFiles
     */
    public boolean isShowHiddenFiles()
        {

        return this.showHiddenFiles ;

        }   // end isShowHiddenFiles()


    /**
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI hideHiddenFiles()
        {

        this.showHiddenFiles = false ;
        this.fileChooser.setFileHidingEnabled( ! this.showHiddenFiles ) ;

        return this ;   // for fluent chaining

        }   // end hideHiddenFiles()


    /**
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI showHiddenFiles()
        {

        this.showHiddenFiles = true ;
        this.fileChooser.setFileHidingEnabled( ! this.showHiddenFiles ) ;

        return this ;   // for fluent chaining

        }   // end showHiddenFiles()


    /**
     * @return the oneOrMultiple
     */
    public OneOrMultiple getChooseOneOrMultiple()
        {

        return this.oneOrMultiple ;

        }   // end getChooseOneOrMultiple()


    /**
     * @return true if user can select one or more files or directories; false otherwise
     */
    public boolean isChooseMultiple()
        {

        return this.oneOrMultiple == OneOrMultiple.MULTIPLE ;

        }   // end isChooseMultiple()


    /**
     * @return true if user can select a single file or directory; false otherwise
     */
    public boolean isChooseOne()
        {

        return this.oneOrMultiple == OneOrMultiple.ONE ;

        }   // end isChooseOne()


    /**
     * user can choose a single file or directory
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setChooseOne()
        {

        // if switching from choose multiple to choose one, forget about the last choice to avoid confusion
        if ( this.oneOrMultiple != OneOrMultiple.ONE )
            {
            // support fail-fast iterator
            modifiedState() ;

            // forget any previous choices
            clear() ;

            this.oneOrMultiple = OneOrMultiple.ONE ;

            this.fileChooser.setMultiSelectionEnabled( false ) ;
            }

        return this ;   // for fluent chaining

        }   // end setChooseOne()


    /**
     * user can choose one or more files or directories
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setChooseMultiple()
        {

        // if switching from choose one to choose multiple, forget about the last choice to avoid confusion
        if ( this.oneOrMultiple != OneOrMultiple.MULTIPLE )
            {
            // support fail-fast iterator
            modifiedState() ;

            // forget any previous choices
            clear() ;

            this.oneOrMultiple = OneOrMultiple.MULTIPLE ;

            this.fileChooser.setMultiSelectionEnabled( true ) ;
            }

        return this ;   // for fluent chaining

        }   // end setChooseMultiple()


    /*
     * interface controls
     */


    /**
     * @return the dialogTitle
     */
    public String getDialogTitle()
        {

        return this.dialogTitle ;

        }   // end getDialogTitle()


    /**
     * @param newDialogTitle
     *     the dialogTitle to set
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setDialogTitle( final String newDialogTitle )
        {

        this.dialogTitle = newDialogTitle ;

        this.fileChooser.setDialogTitle( this.dialogTitle ) ;

        return this ;   // for fluent chaining

        }   // end setDialogTitle()


    /**
     * @return the approveButtonText
     */
    public String getApproveButtonText()
        {

        return this.approveButtonText ;

        }   // end getApproveButtonText()


    /**
     * @param newApproveButtonText
     *     the approveButtonText to set
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setApproveButtonText( final String newApproveButtonText )
        {

        this.approveButtonText = newApproveButtonText ;
        this.fileChooser.setApproveButtonText( this.approveButtonText ) ;

        return this ;   // for fluent chaining

        }   // end setApproveButtonText()


    /*
     * location controls
     */


    /**
     * @return the startIn
     */
    public Path getStartIn()
        {

        return this.startIn ;

        }   // end getStartIn()


    /**
     * @param whereToStart
     *     the startIn to set
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI setStartIn( final Path whereToStart )
        {

        if ( whereToStart == null )
            {
            this.startIn = null ;
            this.fileChooser.setCurrentDirectory( null ) ;

            return this ;   // for fluent chaining
            }

        // assertion: we have a path

        // make sure the supplied path is a directory
        final File whereToStartFile = whereToStart.toFile() ;

        if ( ! whereToStartFile.isDirectory() )
            {
            throw new IllegalArgumentException( String.format( "must start in a directory but given %s",
                                                               whereToStart.toString() ) ) ;
            }

        // assertion: we have a directory

        this.startIn = whereToStart ;
        this.fileChooser.setCurrentDirectory( whereToStartFile ) ;

        return this ;   // for fluent chaining

        }   // end setStartIn()


    /*
     * convenience factories
     */


    /**
     * PLACEHOLDER
     *
     * @return reference to this instance for fluent chaining
     */
    public static FileSystemUI forAny()
        {

        final FileSystemUI newFileSystemUI = new FileSystemUI() ;

        newFileSystemUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.ANY ) ;

        return newFileSystemUI ;

        }   // end forAny()


    /**
     * PLACEHOLDER
     *
     * @return reference to this instance for fluent chaining
     */
    public static FileSystemUI forDirectories()
        {

        final FileSystemUI newFileSystemUI = new FileSystemUI() ;

        newFileSystemUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.DIRECTORIES ) ;

        return newFileSystemUI ;

        }   // end forDirectories()


    /**
     * PLACEHOLDER
     *
     * @return reference to this instance for fluent chaining
     */
    public static FileSystemUI forFiles()
        {

        final FileSystemUI newFileSystemUI = new FileSystemUI() ;

        newFileSystemUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.FILES ) ;

        return newFileSystemUI ;

        }   // end forFiles()


    /*
     * miscellaneous public methods
     */


    /**
     * 'forgets' most recent choice(s)
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI clear()
        {

        // support fail-fast iterator
        modifiedState() ;

        // only reset the selected item-related state
        this.selectedItems.clear() ;

        this.selectedItemsIterator = this.selectedItems.listIterator() ;

        return this ;   // for fluent chaining

        }   // end clear()


    /**
     * PLACEHOLDER
     *
     * @return {@code true} if there are no selected items; {@code false} otherwise
     */
    public boolean isEmpty()
        {

        return this.selectedItems.isEmpty() ;

        }   // end isEmpty()


    /**
     * PLACEHOLDER
     *
     * @return the number of selected items
     */
    public int itemCount()
        {

        return this.selectedItems.size() ;

        }   // end itemCount()


    @Override
    public ListIterator<Path> iterator()
        {

        return new SelectedItemsIterator() ;

        }   // end iterator()


    /**
     * set state back to defaults
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI reset()
        {

        // nearly complete reset
        initializeState() ;

        return this ;   // for fluent chaining

        }   // end reset()


    /**
     * Note: a utility class such as this typically doesn't provide a toString() method I included this for
     * educational purposes to aid your testing/debugging
     * <p>
     * {@inheritDoc}
     */
    @Override
    public String toString()
        {

        // @formatter:off
        return String.format( """

                              =====
                              start in: %s

                              open or save: %s
                              one or multiple: %s
                              files, directories, or any: %s
                                  dialog title: %s

                              approve button: %s

                              show hidden: %b
                              allow *.*: %b
                              file filter: %s
                              choosable file filter: %s

                              headless: %b

                              file chooser: %s

                              %,d selected item(s):
                                  %s

                              invalidateIterator: %b
                              =====

                              """,
                              this.startIn,

                              this.openOrSave,
                              this.oneOrMultiple,
                              this.filesDirectoriesOrAny,
                              this.dialogTitle,

                              this.approveButtonText,

                              this.showHiddenFiles,
                              this.allowAllFilesTypeFilter,
                              this.fileFilter,
                              this.choosableFileFilter,

                              this.isHeadless,

                              this.fileChooser,

                              this.selectedItems.size(), this.selectedItems,

                              this.invalidateIterator
                              ) ; // /* for debugging */
        // @formatter:on

        }   // end toString()


    /*
     * public API methods
     */


    /**
     * PLACEHOLDER
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI chooseMultiple()
        {

        return chooseMultiple( null, null ) ;

        }


    /**
     * PLACEHOLDER
     *
     * @param titleText
     *     text to display as the dialog window's title; default is used if null
     * @param selectButtonText
     *     text to display as the label for the 'approve' button; default is used if null
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI chooseMultiple( final String titleText,
                                        final String selectButtonText )
        {

        // support fail-fast iterator
        modifiedState() ;

        clear() ;

        setChooseMultiple() ;

        setDialogTitle( titleText == null
                ? constructDialogTitle()
                : titleText ) ;

        if ( selectButtonText != null )
            {
            setApproveButtonText( selectButtonText ) ;
            }

        final int result = this.fileChooser.showOpenDialog( null ) ;   // null = center on screen

        switch ( result )
            {
            case CANCEL_OPTION, // cancel button pressed
                 ERROR_OPTION   // dialog dismissed
            ->
                {
                return this ;   // for fluent chaining
                }

            case APPROVE_OPTION ->
                {
                final File[] chosenFiles = this.fileChooser.getSelectedFiles() ;

                // should never be none but just in case
                if ( chosenFiles.length == 0 )
                    {
                    throw new IllegalStateException( String.format( """
                                                                    showOpenDialog() returned APPROVE_OPTION but \
                                                                    getSelectedFile() returned null
                                                                    """ ) ) ;
                    }

                // assertion: we have one or more selected items

                for ( final File chosenFile : chosenFiles )
                    {
                    this.selectedItems.add( chosenFile.toPath() ) ;
                    }

                // remember our location for the next request
                setStartIn( this.fileChooser.getCurrentDirectory().toPath() ) ;

                return this ;   // for fluent chaining
                }

            default -> throw new IllegalStateException( String.format( "showOpenDialog() returned unrecognized value: %d",
                                                                       result ) ) ;
            }

        }   // end chooseMultiple()


    /**
     * PLACEHOLDER
     *
     * @return reference to this instance for fluent chaining
     */
    public FileSystemUI chooseOne()
        {

        // support fail-fast iterator
        modifiedState() ;

        clear() ;

        setDialogTitle( constructDialogTitle() ) ;

        setChooseOne() ;

        final int result = this.fileChooser.showOpenDialog( null ) ;   // null = center on screen

        switch ( result )
            {
            case CANCEL_OPTION, // cancel button pressed
                 ERROR_OPTION    // dialog dismissed
            ->
                {
                return this ;   // for fluent chaining
                }

            case APPROVE_OPTION ->
                {
                final File chosenFile = this.fileChooser.getSelectedFile() ;

                // should never be null but just in case
                if ( chosenFile == null )
                    {
                    throw new IllegalStateException( String.format( """
                                                                    showOpenDialog() returned APPROVE_OPTION but \
                                                                    getSelectedFile() returned null
                                                                    """ ) ) ;
                    }

                // assertion: we have a single selected item

                final Path chosenPath = chosenFile.toPath() ;

                this.selectedItems.add( chosenPath ) ;

                // remember our location for the next request
                setStartIn( this.fileChooser.getCurrentDirectory().toPath() ) ;

                return this ;   // for fluent chaining
                }

            default -> throw new IllegalStateException( String.format( "showOpenDialog() returned unrecognized value: %d",
                                                                       result ) ) ;
            }

        }   // end chooseOne()


    /*
     * results
     */


    /**
     * PLACEHOLDER
     *
     * @return the first or only selected item or {@code null} if none
     */
    public Path getFirstSelected()
        {

        return this.selectedItems.size() == 0
                ? null
                : this.selectedItems.getFirst() ;

        }   // end getFirstSelected()


    /**
     * PLACEHOLDER
     *
     * @return the last or only selected item or {@code null} if none
     */
    public Path getLastSelected()
        {

        return this.selectedItems.size() == 0
                ? null
                : this.selectedItems.getLast() ;

        }   // end getLastSelected()


    /**
     * PLACEHOLDER
     *
     * @return when iterating over the selected items, the next selected item if any or {@code null} if none
     */
    public Path getNextSelected()
        {

        return this.selectedItemsIterator.hasNext()
                ? this.selectedItemsIterator.next()
                : null ;

        }   // end getNextSelected()


    /**
     * PLACEHOLDER
     *
     * @return when iterating over the selected items, {@code true} if there's at least one more item
     *     available or {@code false} if none
     */
    public boolean getHasNextSelected()
        {

        return this.selectedItemsIterator.hasNext() ;

        }   // end getHasNextSelected()


    /**
     * PLACEHOLDER
     *
     * @return the first or only selected item or {@code null} if none
     */
    public Path getSelected()
        {

        return ( this.selectedItems.size() == 0 )
                ? null
                : this.selectedItems.getFirst() ;

        }   // end getSelected() first by default


    /**
     * PLACEHOLDER
     *
     * @param position
     *     the position in the list of selected items to retrieve
     *
     * @return the selected item at the specified position in the list of selected items
     *
     * @throws IndexOutOfBoundsException
     *     if {@code position} is less than zero or greater than or equal to the number of selected items
     */
    public Path getSelected( final int position ) throws IndexOutOfBoundsException
        {

        return this.selectedItems.get( position ) ;

        }   // end getSelected() by position


    /**
     * PLACEHOLDER
     *
     * @return a list of all selected items which may be empty<br>
     *     note that the returned list will not reflect any subsequent use of this FileSystemUI instance
     */
    public List<Path> getAllSelected()
        {

        return List.copyOf( this.selectedItems ) ;

        }   // end getAllSelected()


    /*
     * protected and private utility methods
     */


    /**
     * PLACEHOLDER
     *
     * @return generates the text for the dialog window title based on the operation (open/save), type
     *     (files/directories/any), and plurality (one/multiple)
     */
    private String constructDialogTitle()
        {

        return String.format( "Choose %s %s to %s",
                              this.oneOrMultiple == OneOrMultiple.ONE
                                      ? "one"
                                      : "one or more",
                              switch ( this.filesDirectoriesOrAny )
                                  {
                                  case FILES -> this.oneOrMultiple == OneOrMultiple.ONE
                                          ? "file"
                                          : "files" ;
                                  case DIRECTORIES -> this.oneOrMultiple == OneOrMultiple.ONE
                                          ? "directory"
                                          : "directories" ;
                                  case ANY -> this.oneOrMultiple == OneOrMultiple.ONE
                                          ? "directory or file"
                                          : "directories and/or files" ;
                                  },
                              this.openOrSave == OpenOrSave.OPEN
                                      ? "open"
                                      : "save" ) ;

        }   // end constructDialogTitle()


    /**
     * (re)set to default configuration
     * <ul>
     * <li>select for opening
     * <li>select files
     * <li>select a single file
     * <li>don't show hidden files
     * <li>no all file types (*.*)
     * <li>no custom filters
     * <li>start in default location (Documents on Windows; user home on Unix)
     * <li>default decorations
     * <li>nothing selected
     * </ul>
     *
     * @return reference to this instance for fluent chaining
     */
    private FileSystemUI initializeState()
        {

        // assertion: this.fileChooser references a valid JFileChooser instance

        // this.startIn: defaults: Windows: Documents; Unix: user's home directory if already selected
        // anything, start there
        if ( this.startIn == null )
            {
            setStartIn( DEFAULT_START_IN ) ;
            }

        // this.openOrSave
        if ( DEFAULT_OPEN_OR_SAVE == OpenOrSave.OPEN )
            {
            setForOpening() ;
            }
        else
            {
            setForSaving() ;
            }

        // this.chooseOneOrMultiple
        if ( DEFAULT_ONE_OR_MULTIPLE == OneOrMultiple.ONE )
            {
            setChooseOne() ;
            }
        else
            {
            setChooseMultiple() ;
            }

        // this.chooserMode
        setChooseFilesDirectoriesOrAny( DEFAULT_FILES_DIRECTORIES_OR_ANY ) ;

        // this.showHiddenFiles
        if ( DEFAULT_SHOW_HIDDEN_FILES )
            {
            showHiddenFiles() ;
            }
        else
            {
            hideHiddenFiles() ;
            }

        // this.allowAllFilesTypeFilter
        if ( DEFAULT_ALLOW_ALL_FILE_TYPES_FILTER )
            {
            allowAllFilesTypeFilter() ;
            }
        else
            {
            disallowAllFilesTypeFilter() ;
            }

        // this.fileFilter
        setFileFilter( DEFAULT_FILE_FILTER ) ;

        // this.choosableFileFilter
        setChoosableFileFilter( DEFAULT_CHOOSABLE_FILE_FILTER ) ;


        // this.dialogTitle
        setDialogTitle( constructDialogTitle() ) ;

        // this.approveButtonText
        setApproveButtonText( DEFAULT_APPROVE_BUTTON_TEXT ) ;


        // nothing selected yet
        this.selectedItems.clear() ;

        this.selectedItemsIterator = this.selectedItems.listIterator() ;


        // support fail-fast iterator
        modifiedState() ;

        return this ;   // for fluent chaining

        }   // end initializeState()


    /**
     * PLACEHOLDER
     *
     * @return reference to this instance for fluent chaining
     */
    private FileSystemUI modifiedState()
        {

        this.invalidateIterator = true ;

        return this ;   // for fluent chaining

        }   // end modifiedState()


    /*
     * testing and debugging
     */


    /**
     * test driver
     *
     * @param args
     *     -unused-
     */
    public static void main( final String[] args )
        {

        System.out.printf( "%n======================%n%n1 directory:%n%n" ) ;

        final FileSystemUI demoFSUI = demoOneDirectory( null ) ;

        System.out.printf( "%n======================%n%n1 file:%n%n" ) ;

        demoOneFile( demoFSUI ) ;

        System.out.printf( "%n======================%n%n1 whatever:%n%n" ) ;

        demoOneWhatever( demoFSUI ) ;

        System.out.printf( "%n======================%n%n1 or more directories:%n%n" ) ;

        demoMultipleDirectories( demoFSUI ) ;

        System.out.printf( "%n======================%n%n1 or more files:%n%n" ) ;

        demoMultipleFiles( demoFSUI ) ;

        System.out.printf( "%n======================%n%n1 or more whatevers:%n%n" ) ;

        demoMultipleWhatevers( demoFSUI ) ;

        System.out.printf( "%n======================%n%n%ndone.%n" ) ;

        }   // end main()


    /**
     * PLACEHOLDER
     *
     * @param suppliedFSUI
     *     a FileSystemUI instance to use for this demonstration; if null, a new instance will be created
     *
     * @return reference to the FileSystemUI instance used in this demonstration
     */
    public static FileSystemUI demoOneDirectory( final FileSystemUI suppliedFSUI )
        {

        FileSystemUI fsui ;

        if ( suppliedFSUI == null )
            {
            fsui = forDirectories() ;
            }
        else
            {
            fsui = suppliedFSUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.DIRECTORIES ) ;
            }

        fsui.chooseOne() ;

        final Path chosenPath = fsui.getSelected() ;

        if ( chosenPath == null )
            {
            System.out.printf( "No file selected%n" ) ;

            return fsui ;   // for fluent chaining
            }

        final File chosenFile = chosenPath.toFile() ;

        if ( ! chosenFile.isDirectory() )
            {
            System.out.printf( String.format( "...choosing directories but returned a file%n" ) ) ;
            }

        // assertion: we have a path to a single directory

        System.out.printf( "Selected:%n\t%s%n%n", chosenPath.toAbsolutePath() ) ;

        return fsui ;   // for fluent chaining

        }   // end demoOneDirectory()


    /**
     * PLACEHOLDER
     *
     * @param suppliedFSUI
     *     a FileSystemUI instance to use for this demonstration; if null, a new instance will be created
     *
     * @return reference to the FileSystemUI instance used in this demonstration
     */
    public static FileSystemUI demoOneFile( final FileSystemUI suppliedFSUI )
        {

        FileSystemUI fsui ;

        if ( suppliedFSUI == null )
            {
            fsui = forFiles() ;
            }
        else
            {
            fsui = suppliedFSUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.FILES ) ;
            }

        fsui.chooseOne() ;

        final Path chosenPath = fsui.getSelected() ;

        if ( chosenPath == null )
            {
            System.out.printf( "No file selected%n" ) ;

            return fsui ;   // for fluent chaining
            }

        final File chosenFile = chosenPath.toFile() ;

        if ( chosenFile.isDirectory() )
            {
            System.out.printf( String.format( "...choosing files but returned a directory%n" ) ) ;
            }

        // assertion: we have a path to a single file

        System.out.printf( "Selected:%n\t%s%n%n", chosenPath.toAbsolutePath() ) ;

        return fsui ;   // for fluent chaining

        }   // end demoOneFile()


    /**
     * PLACEHOLDER
     *
     * @param suppliedFSUI
     *     a FileSystemUI instance to use for this demonstration; if null, a new instance will be created
     *
     * @return reference to the FileSystemUI instance used in this demonstration
     */
    public static FileSystemUI demoOneWhatever( final FileSystemUI suppliedFSUI )
        {

        FileSystemUI fsui ;

        if ( suppliedFSUI == null )
            {
            fsui = forAny() ;
            }
        else
            {
            fsui = suppliedFSUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.ANY ) ;
            }

        fsui.chooseOne() ;

        final Path chosenPath = fsui.getSelected() ;

        if ( chosenPath == null )
            {
            System.out.printf( "No files or directories selected%n" ) ;

            return fsui ;   // for fluent chaining
            }

        // assertion: we have a path to a single file or directory

        System.out.printf( "Selected:%n\t%s%n%n", chosenPath.toAbsolutePath() ) ;

        return fsui ;   // for fluent chaining

        }   // end demoOneWhatever()


    /**
     * PLACEHOLDER
     *
     * @param suppliedFSUI
     *     a FileSystemUI instance to use for this demonstration; if null, a new instance will be created
     *
     * @return reference to the FileSystemUI instance used in this demonstration
     */
    public static FileSystemUI demoMultipleDirectories( final FileSystemUI suppliedFSUI )
        {

        FileSystemUI fsui ;

        if ( suppliedFSUI == null )
            {
            fsui = forDirectories() ;
            }
        else
            {
            fsui = suppliedFSUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.DIRECTORIES ) ;
            }

        fsui.chooseMultiple() ;

        final Path selectedPath = fsui.getSelected() ;

        if ( selectedPath == null )
            {
            System.out.printf( "No directories selected%n" ) ;

            return fsui ;   // for fluent chaining
            }

        for ( final Path chosenPath : fsui )
            {
            final File chosenFile = chosenPath.toFile() ;

            if ( ! chosenFile.isDirectory() )
                {
                System.out.printf( String.format( "...choosing directories but returned a file%n" ) ) ;
                }

            System.out.printf( "\t%s%n", chosenPath.toString() ) ;
            }

        return fsui ;   // for fluent chaining

        }   // end demoManyDirectories()


    /**
     * PLACEHOLDER
     *
     * @param suppliedFSUI
     *     a FileSystemUI instance to use for this demonstration; if null, a new instance will be created
     *
     * @return reference to the FileSystemUI instance used in this demonstration
     */
    public static FileSystemUI demoMultipleFiles( final FileSystemUI suppliedFSUI )
        {

        FileSystemUI fsui ;

        if ( suppliedFSUI == null )
            {
            fsui = forFiles() ;
            }
        else
            {
            fsui = suppliedFSUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.FILES ) ;
            }

        fsui.chooseMultiple() ;

        final Path selectedPath = fsui.getSelected() ;

        if ( selectedPath == null )
            {
            System.out.printf( "No files selected%n" ) ;

            return fsui ;   // for fluent chaining
            }

        System.out.printf( "Selected:%n%n" ) ;

        for ( final Path chosenPath : fsui )
            {
            final File chosenFile = chosenPath.toFile() ;

            if ( chosenFile.isDirectory() )
                {
                System.out.printf( String.format( "...choosing files but returned a directory%n" ) ) ;
                }

            System.out.printf( "\t%s%n", chosenPath.toString() ) ;
            }

        return fsui ;   // for fluent chaining

        }   // end demoManyFiles()


    /**
     * PLACEHOLDER
     *
     * @param suppliedFSUI
     *     a FileSystemUI instance to use for this demonstration; if null, a new instance will be created
     *
     * @return reference to the FileSystemUI instance used in this demonstration
     */
    public static FileSystemUI demoMultipleWhatevers( final FileSystemUI suppliedFSUI )
        {

        FileSystemUI fsui ;

        if ( suppliedFSUI == null )
            {
            fsui = forAny() ;
            }
        else
            {
            fsui = suppliedFSUI.setChooseFilesDirectoriesOrAny( FilesDirectoriesOrAny.ANY ) ;
            }

        fsui.chooseMultiple() ;

        final Path selectedPath = fsui.getSelected() ;

        if ( selectedPath == null )
            {
            System.out.printf( "No files or directories selected%n" ) ;

            return fsui ;   // for fluent chaining
            }

        System.out.printf( "Selected:%n%n" ) ;

        for ( final Path chosenPath : fsui )
            {
            System.out.printf( "\t%s%n", chosenPath.toString() ) ;
            }

        return fsui ;   // for fluent chaining

        }   // end demoManyWhatevers()


    /*
     * utility classes
     */


    /**
     * available categorical selection criteria
     */
    public static enum FilesDirectoriesOrAny
        {

         /** user can only choose file(s) */
         FILES,
         /** user can only choose directory(s) */
         DIRECTORIES,
         /** user can choose file(s) and/or directory(s) */
         ANY;


        /**
         * map our constants to the JFileChooser's equivalents
         *
         * @return the JFileChooser's equivalent symbolic constant
         */
        public int getJFileChooserMode()
            {

            return switch ( this )
                {
                case FILES -> FILES_ONLY ;
                case DIRECTORIES -> DIRECTORIES_ONLY ;
                case ANY -> FILES_AND_DIRECTORIES ;
                default -> throw new IllegalStateException( String.format( "invalid state; unrecognized FilesDirectoriesOrAny: %s",
                                                                           name() ) ) ;
                } ;

            }   // end getJFileChooserMode()


        @Override
        public String toString()
            {

            return switch ( this )
                {
                case FILES -> "only files" ;
                case DIRECTORIES -> "only directories" ;
                case ANY -> "files and/or directories" ;
                default -> throw new IllegalStateException( String.format( "invalid state; unrecognized FilesDirectoriesOrAny: %s",
                                                                           name() ) ) ;
                } ;

            }   // end toString()

        }   // end inner enum FilesDirectoriesOrAny


    /**
     * available selection pluralities
     */
    private static enum OneOrMultiple
        {

         /** PLACEHOLDER */
         ONE,
         /** PLACEHOLDER */
         MULTIPLE;


        @Override
        public String toString()
            {

            return switch ( this )
                {
                case ONE -> "just one" ;
                case MULTIPLE -> "one or more" ;
                default -> throw new IllegalStateException( String.format( "invalid state; unrecognized OneOrMultiple: %s",
                                                                           name() ) ) ;
                } ;

            }   // end toString()

        }   // end inner enum OneOrMultiple


    /**
     * available operating modes
     */
    private enum OpenOrSave
        {

         /** prompting for an open operation */
         OPEN,
         /** prompting for a save operation */
         SAVE;


        /**
         * map our constants to the JFileChooser's equivalents
         *
         * @return the JFileChooser's equivalent symbolic constant
         */
        public int getDialogType()
            {

            return switch ( this )
                {
                case OPEN -> OPEN_DIALOG ;
                case SAVE -> SAVE_DIALOG ;
                default -> throw new IllegalStateException( String.format( "invalid state; unrecognized OpenOrSave: %s",
                                                                           name() ) ) ;
                } ;

            }   // end getDialogType()


        @Override
        public String toString()
            {

            return switch ( this )
                {
                case OPEN -> "open operation" ;
                case SAVE -> "save operation" ;
                default -> throw new IllegalStateException( String.format( "invalid state; unrecognized FilesDirectoriesOrAny: %s",
                                                                           name() ) ) ;
                } ;

            }   // end toString()

        }   // end inner enum OpenOrSave


    /**
     * enable application to iterate over the user-selected items
     */
    private class SelectedItemsIterator implements ListIterator<Path>
        {

        /** we will use List's ListIterator into selectedItems */
        private final ListIterator<Path> listIterator ;


        /**
         * set initial state to valid with an iterator for the selectedItems list
         */
        private SelectedItemsIterator()
            {

            this.listIterator = FileSystemUI.this.selectedItems.listIterator() ;

            FileSystemUI.this.invalidateIterator = false ;

            }   // end no-arg constructor


        /*
         * utility methods
         */


        /**
         * support fail-fast determination (from our perspective)
         * <p>
         * if we do this correctly, List's iterator will never throw a {@link ConcurrentModificationException}
         */
        private void failFast()
            {

            if ( isInvalidated() )
                {
                throw new ConcurrentModificationException( String.format( "iterator invalidated by state change in FileSystemUI" ) ) ;
                }

            }   // end failFast()


        /**
         * test the validity of our iterator
         *
         * @return {@code true} indicates that this iterator cannot be used reliably; {@code false} indicates
         *     that this iterator can be used safely
         */
        private boolean isInvalidated()
            {

            return FileSystemUI.this.invalidateIterator ;

            }   // end isInvalidated()


        /*
         * public ListIterator methods
         */


        /*
         * retrieval methods
         */


        @Override
        public boolean hasNext()
            {

            failFast() ;

            return this.listIterator.hasNext() ;

            }   // end hasNext()


        @Override
        public Path next()
            {

            failFast() ;

            return this.listIterator.next() ;

            }   // end next()


        @Override
        public boolean hasPrevious()
            {

            failFast() ;

            return this.listIterator.hasPrevious() ;

            }   // end hasPrevious()


        @Override
        public Path previous()
            {

            failFast() ;

            return this.listIterator.previous() ;

            }   // end previous()


        @Override
        public int nextIndex()
            {

            failFast() ;

            return this.listIterator.nextIndex() ;

            }   // end nextIndex()


        @Override
        public int previousIndex()
            {

            failFast() ;

            return this.listIterator.previousIndex() ;

            }   // end previousIndex()


        /*
         * modification methods
         */


        @Override
        public void add( final Path e )
            {

            failFast() ;

            this.listIterator.add( e ) ;

            }   // end add()


        @Override
        public void remove()
            {

            failFast() ;

            this.listIterator.remove() ;

            }   // end remove()


        @Override
        public void set( final Path e )
            {

            failFast() ;

            this.listIterator.set( e ) ;

            }   // end set()

        }   // end inner class SelectedItemsIterator

    }   // end class FileSystemUI
