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

import education.the_software_toolsmith.analyzer.framework.dynamicanalysis.TestingBase ;
import education.the_software_toolsmith.analyzer.framework.dynamicanalysis.TestingException ;

import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.Reflection.* ;
import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.ReflectReferenceTypes.* ;
import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.ReflectDataFields.* ;
import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.ReflectMethods.* ;
import static education.the_software_toolsmith.analyzer.framework.dynamicanalysis.ReflectBackingStores.* ;

import java.lang.annotation.Annotation ;
import java.lang.reflect.Field ;
import java.lang.reflect.Method ;
import java.lang.reflect.Modifier ;
import java.lang.reflect.Parameter ;
import java.util.List ;
import java.util.Arrays ;
import java.util.HashMap ;
import java.util.Map ;


/**
 * non-JUnit tests for TestClass
 * 
 * @author David M Rosenberg
 * 
 * @version 2.0 2025-06-14
 *     <ul>
 *     <li>unknown version and modification history
 *     <li>remove methods which are in our {@code Reflection} class
 *     <li>move {@code arrayDimension()} and {@code arrayOf()} to our {@code Reflection} class
 *     <li>remove all direct references to the TestClass class for consistency with actual tests
 *     <li>switch all {@code printx()} to {@code writeConsole()}
 *     <ul>
 *     <li>for consistency with actual tests
 *     <li>to direct output to the detailed log file
 *     </ul>
 *     <li>miscellaneous cleanup
 *     </ul>
 */
public class TestClassDMRTests extends TestingBase
	{

	/**
     * @param packageName name of test class' package
     * @param simpleClassName simple name of test class
     */
    public TestClassDMRTests( String packageName, String simpleClassName )
        {
        super( packageName, simpleClassName ) ;
        
        }   // end 1-arg constructor


    /**
     * test driver
     * 
     * @param args
     *     -unused-
     * 
     * @throws Throwable
     *     -catch-all - could be anything-
     * @throws ClassNotFoundException
     *     the test class couldn't be found
     * @throws TestingException
     *     wraps another (the original) exception
     */
    public static void main( String[] args )
        throws TestingException, ClassNotFoundException, Throwable
        {

        TestClassDMRTests tcdmrt = new TestClassDMRTests( "education.the_software_toolsmith.testing.framework.tests",
                                                          "TestClass" ) ;

		// instantiate the test class
        Object tc = instantiate( tcdmrt.testClass,
                                 new Class<?>[]
                                 { String.class },
                                 new Object[]
                                 { "sumthin's up" } ) ;
        tcdmrt.writeConsole( "tc:\n\t%s%n", tc.toString() ) ;

		Class< ? > testClassClass =	null ;
			testClassClass =		tc.getClass() ;
			tcdmrt.writeConsole( "Class.forName(): %s%n",
								testClassClass ) ;
		
			tcdmrt.writeConsole( "package: %s%n", testClassClass.getPackage() ) ;

			tcdmrt.writeConsole( "\n%s declared classes:%n", tcdmrt.testClassSimpleName ) ;
        Class<?>[] tcDeclaredClasses = testClassClass.getDeclaredClasses() ;

        for ( Class<?> tcDeclaredClass : tcDeclaredClasses )
            {
            tcdmrt.writeConsole( "\t%s%n", tcDeclaredClass.getName() ) ;
            tcdmrt.writeConsole( "\t%s%n", tcDeclaredClass.getSimpleName() ) ;
            }

        tcdmrt.writeConsole( "\n%s declared methods:%n", tcdmrt.testClassSimpleName ) ;
		Method[] tcDeclaredMethods = testClassClass.getDeclaredMethods() ;
		for ( Method tcDeclaredMethod : tcDeclaredMethods )
            {
            String methodName = tcDeclaredMethod.getName() ;
            tcdmrt.writeConsole( "\t%s%s%s%s%s%n",
                                 methodName,
                                 tcDeclaredMethod.isBridge()
                                     ? " {bridge}"
                                     : "",
                                 tcDeclaredMethod.isDefault()
                                     ? " {default}"
                                     : "",
                                 tcDeclaredMethod.isSynthetic()
                                     ? " {synthetic}"
                                     : "",
                                 tcDeclaredMethod.isVarArgs()
                                     ? " {varargs}"
                                     : "" ) ;

			int methodModifiers =	tcDeclaredMethod.getModifiers() ;
			tcdmrt.writeConsole( "\t\tmodifiers: %x: %s%n", 
			                   methodModifiers, 
			                   Modifier.toString( methodModifiers ) ) ;
			
			int methodParameterCount =	tcDeclaredMethod.getParameterCount() ;
			
			Class<?> declaringClass = tcDeclaredMethod.getDeclaringClass() ;
			tcdmrt.writeConsole( "\t\tfrom %s", declaringClass.getSimpleName() ) ;

			Class<?> superClass = declaringClass.getSuperclass() ;
			while ( superClass != null )
			    {
			    tcdmrt.writeConsole( " extends %s", superClass.getSimpleName() ) ;
			    
			    superClass = superClass.getSuperclass() ;
			    }
			Class<?>[] implementedInterfaces = declaringClass.getInterfaces() ;
			for ( Class<?> implementedInterface : implementedInterfaces )
			    {
			    tcdmrt.writeConsole( " implements %s", implementedInterface.getSimpleName() ) ;
                }
			
			tcdmrt.writeConsole( "%n" ) ;
			
			Parameter[] methodParameters =	tcDeclaredMethod.getParameters() ;
			tcdmrt.writeConsole( "\t\t%,d parameter%s: %s%n", 
			                   methodParameterCount,
			                   ( methodParameterCount == 1
			                   		? ""
			                   		: "s" ),
			                   Arrays.toString( methodParameters )
			                   ) ;
			
			Class<?>[] methodParameterTypes =	tcDeclaredMethod.getParameterTypes() ;
			tcdmrt.writeConsole( "\t\tparameter types: %s%n", 
			                   Arrays.toString( methodParameterTypes ) ) ;
			
			boolean methodTakesVarArgs =	tcDeclaredMethod.isVarArgs() ;
			tcdmrt.writeConsole( "\t\ttakes variable arguments: %b%n", 
			                   methodTakesVarArgs ) ;
			
			Class<?> methodReturnType =	tcDeclaredMethod.getReturnType() ;
			tcdmrt.writeConsole( "\t\treturns: %s%n", 
			                   methodReturnType ) ;
			
			Class<?>[] methodExceptionTypes =	tcDeclaredMethod.getExceptionTypes() ;
			tcdmrt.writeConsole( "\t\tthrows: %s%n", 
			                   Arrays.toString( methodExceptionTypes ) ) ;
			
			Annotation[] methodDeclaredAnnotations =	tcDeclaredMethod.getDeclaredAnnotations() ;
			tcdmrt.writeConsole( "\t\tannotations: %s%n", 
			                   Arrays.toString( methodDeclaredAnnotations ) ) ;
			
			tcdmrt.writeConsole( "%n" ) ;
			}

		tcdmrt.writeConsole( "\n%s declared fields:%n", tcdmrt.testClassSimpleName ) ;
		Field[] tcDeclaredFields =	testClassClass.getDeclaredFields() ;
		for ( Field tcDeclaredField : tcDeclaredFields )
			{
			try
				{
                tcDeclaredField.setAccessible( true ) ;

                tcdmrt.writeConsole( "\t%s: %s = %s%n",
                                     tcDeclaredField.getName(),
                                     tcDeclaredField.getType(),
                                     tcDeclaredField.get( tc ) ) ;
				}
			catch ( IllegalArgumentException |
			        IllegalAccessException e )
				{
				e.printStackTrace() ;
				}
			}

		// display the instance state
		tcdmrt.writeConsole( "\ninstance state:%n" ) ;
		for ( Field tcDeclaredField : tcDeclaredFields )
			{
			try
				{
                tcDeclaredField.setAccessible( true ) ;

                tcdmrt.writeConsole( "\t%s: %s = %s%n",
                                     tcDeclaredField.getName(),
                                     tcDeclaredField.getType(),
                                     tcDeclaredField.get( tc ) ) ;
                }
			catch ( IllegalArgumentException |
			        IllegalAccessException e )
				{
				e.printStackTrace() ;
				}
			}

        Object[][] strings = { { "eeeee", "dddd", "ccc", "bb", "a" },
                               { "bye", "bye", "love" } } ;

        invoke( tc.getClass(),
                tc,
                "buildChain",
                new Class<?>[]
                { strings[ 0 ].getClass() },
                new Object[]
                { strings[ 0 ] } ) ;

        tcdmrt.writeConsole( "%ntc:%n\t%s%n", tc.toString() ) ;


        invoke( tc.getClass(),
                tc,
                "buildChain",
                new Class<?>[]
                { strings[ 1 ].getClass() },
                new Object[]
                { strings[ 1 ] } ) ;

        tcdmrt.writeConsole( "tc:%n\t%s%n%n", tc.toString() ) ;

		Field[] testClassFields =	testClassClass.getDeclaredFields() ;
		Map<String, Field> tCFieldsMap = new HashMap<>() ;
		tcdmrt.writeConsole( "\ntestClassFields: %n" ) ;
		for ( Field field : testClassFields )
			{
			field.setAccessible( true ) ;
			tCFieldsMap.put( field.getName(),
			                 field ) ;
			tcdmrt.writeConsole( "\t%s: %s%n",
			                   field.getName(),
			                   tCFieldsMap.get( field.getName() ) ) ;
//			tcdmrt.writeConsole( "\t\ttoString(): %s%n", 
//			                   	field.toString() ) ;
//			tcdmrt.writeConsole( "\t\ttoGenericString(): %s%n", 
//			                   	field.toGenericString() ) ;
//			tcdmrt.writeConsole( "\t\tType: %s%n",
//			                   	field.getType().getName() ) ;
//			tcdmrt.writeConsole( "\t\tType: %s%n", 
//			                   	field.getType().getTypeName() ) ;
//			tcdmrt.writeConsole( "\t\tType: %s%n", 
//		                   	typeOf( field ) ) ;
//			tcdmrt.writeConsole( "\t\tType: %s%n", 
//		                   	primitiveTypeOf( field ) ) ;

			String visibilityOf =	visibilityOf( field ) ;
			boolean isStatic =		isStatic( field ) ;
			tcdmrt.writeConsole( "\t\t%s %s variable%n",
			                   visibilityOf, 
								( isStatic 
									? "class" 
									: "instance" ) 
								) ;
			
			boolean isPrimitive =	isPrimitive( field ) ;
			if ( isPrimitive )
				{
				tcdmrt.writeConsole( "\t\tprimitive: %s%n",
				                   primitiveTypeOf( field ) ) ;
				}
			
			boolean isArray =		isArray( field ) ;
			if ( isArray )
				{
				tcdmrt.writeConsole( "\t\t%,d-dimensional array of %s%n",
				                   arrayDimensions( field ),
				                   arrayOf( field ).getTypeName()
				                   ) ;
				}
			
			boolean isAnnotation =	isAnnotation( field ) ;
			if ( isAnnotation )
				{
				tcdmrt.writeConsole( "\t\tAnnotation%n" ) ;
				}


            boolean isReference =   isClassReference( field ) ;
            boolean isInterface =   isInterfaceReference( field ) ;

            if ( isReference || isInterface )
                {
                tcdmrt.writeConsole( "\t\t%s reference%n", ( isReference ? "class" : "interface" ) ) ;
                
                boolean isCollection =  isCollection( field ) ;
                if ( isCollection )
                    {
                    tcdmrt.writeConsole( "\t\t\tCollection%n" ) ;
                    }
                
                tcdmrt.writeConsole( "\t\t\t%s%n", typeOf( field ).getTypeName() ) ;
            
                }
			
            
			boolean isEnumeration =	isEnumeration( field ) ;
			if ( isEnumeration )
				{
				tcdmrt.writeConsole( "\t\tenumeration%n" ) ;
				}

			tcdmrt.writeConsole( "%n" ) ;
			}

		Field[] nodeFields =			retrieveNodeFields( tcdmrt.testClass ) ;
		Map<String, Field> nodeFieldsMap =	new HashMap<>() ;
		tcdmrt.writeConsole( "\nnodeFields: %n" ) ;
		for ( Field field : nodeFields )
            {
            field.setAccessible( true ) ;
            tcdmrt.writeConsole( "\t%s: %s%n\t\t%s%n",
                                 field.getName(),
                                 field.toGenericString(),
                                 typeOf( field ) ) ;
            nodeFieldsMap.put( field.getName(), field ) ;
            tcdmrt.writeConsole( "\t%s: %s%n",
                                 field.getName(),
                                 nodeFieldsMap.get( field.getName() ) ) ;
            }

        int numberOfEntries = getIntField( tc, "numberOfEntries" ) ;
        Object startNode = getReferenceField( tc, "firstNode" ) ;
        List<?> entries = getChainAsList( startNode ) ;

		tcdmrt.writeConsole( "\nnumberOfEntries: %,d%n", numberOfEntries ) ;
		tcdmrt.writeConsole( "entries: %s%n", entries ) ;
		tcdmrt.writeConsole( "# of entries == numberOfEntries: %b%n", 
							( entries.size() == numberOfEntries ) ) ;
		
		tcdmrt.writeConsole( "%n----------%n%ninvoking main():%n%n" ) ;

        invoke( tc.getClass(),
                tc,
                "main",
                new Class<?>[]
                { args.getClass() },
                new Object[] { args } ) ;

		tcdmrt.writeConsole( "%n----------%n%ndone.%n" ) ;
		tcdmrt.closeLog() ;
		
		}	// end main()


    /**
     * IN_PROCESS
     * 
     * @param testClass IN_PROCESS
     * @return IN_PROCESS
     *
     * @since 1.0
     */
	static Field[] retrieveTestClassFields( final Class<?> testClass )
		{

		return testClass.getDeclaredFields() ;

		}	// end retrieveTestClassFields()


	/**
	 * IN_PROCESS
	 * 
	 * @param testClass IN_PROCESS
	 * @return IN_PROCESS
	 *
	 * @since 1.0
	 */
	static Field[] retrieveNodeFields( final Class<?> testClass )
		{

        Class<?>[] testClasses = testClass.getDeclaredClasses() ;
        Class<?> nodeClass = null ;

        for ( Class<?> aClass : testClasses )
			{
			if ( aClass.getSimpleName().equals( "Node" ) )
				{
				nodeClass =				aClass ;
				break ;
				}
			}

		Field nodeDataField =			null ;
		Field nodeNextField =			null ;

		if ( nodeClass != null )
			{
			try
				{
				nodeDataField =			nodeClass.getDeclaredField( "data" ) ;
				nodeDataField.setAccessible( true ) ;

				nodeNextField =			nodeClass.getDeclaredField( "next" ) ;
				nodeNextField.setAccessible( true ) ;
				}
			catch ( NoSuchFieldException |
			        SecurityException e )
				{
				e.printStackTrace() ;
				}
			}

		return new Field[] { nodeDataField, nodeNextField } ;

		}	// end retrieveNodeFields()
	
	}	// end class TestClassDMRTests