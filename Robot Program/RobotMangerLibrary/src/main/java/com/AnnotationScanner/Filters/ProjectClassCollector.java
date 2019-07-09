package com.AnnotationScanner.Filters;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

/**
 * Copyright 2019 Wyatt Ashley
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 *
 * Revision Num Date Who Rev 1 6/22/2019 Wyatt Ashley Rev 2 7/1/2019 Wyatt
 * Ashley
 */

@SuppressWarnings("all")
public class ProjectClassCollector {
    public enum SourceCodeStorage {
        JAR("JAR"), DEX("DEX"), ZIP("ZIP");

        public String name = "";

        SourceCodeStorage(String _name) {
            this.name = _name;
        }

        public String toString() {
            return this.name;
        }
    }

    private boolean debug = true;
    private SourceCodeStorage sourceCodeStorage;
    private String sourceCodeLocation = "";
    private List<String> excludeList = new ArrayList<>();
    private List<String> listClassNames = new ArrayList<>();
    private List<Class> listClasses = new ArrayList<>();

    /**
     * This a source code collector that get all the classes from you project so you
     * can find all of your annotations
     * 
     * @param _sourceCodeStorage  This is what kind of file is made by the Java
     *                            Compiler
     * @param _sourceCodeLocation This is where the file the Java compiler made is
     *                            on the device
     */
    public ProjectClassCollector(SourceCodeStorage _sourceCodeStorage, String _sourceCodeLocation,
            List<String> _excludeList, boolean _debug) {
        this.sourceCodeStorage = _sourceCodeStorage;
        this.sourceCodeLocation = _sourceCodeLocation;
        this.excludeList = _excludeList;
        this.debug = _debug;

        this.listClassNames.clear();
        this.listClasses.clear();

        switch (this.sourceCodeStorage) {
        case JAR:
            try {
                this.listClassNames = getJARClassesString(this.sourceCodeLocation, this.excludeList, this.debug);
                this.listClasses = getJARClasses(this.listClassNames);
            } catch (IOException e) {
                System.out.println("ERROR: IOException when load JAR file from " + this.sourceCodeStorage);
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                System.out.println("ERROR: ClassNotFoundException when loading from JAR at " + this.sourceCodeStorage);
                System.out
                        .println("Note not Stopping for file not found because this is no reason to stop the program");
            }
            break;
        case DEX:
            try {
                this.listClassNames = this.getDEXEntryClasses();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
                System.out.println("DEX files are not supported yet please pick another file system");
                System.out.println("Sorry for the inconvenience :(");
            }
            break;
        case ZIP:
            this.listClassNames = getZIPClassesString(this.sourceCodeLocation, this.debug);
            this.listClasses = getZIPClasses(this.listClassNames);
            
            break;
        }
    }

    /**
     * Getting a list of all class objects with the names
     * 
     * @return A list of all classes in the project
     */
    public List<Class> getListClasses() {
        return this.listClasses;
    }

    /**
     * Getting a list of class names with package (com.example.exam.Main)
     * 
     * @return Gives a list of all class names
     */
    public List<String> getListClassNames() {
        return this.listClassNames;
    }

    /**
     * This iss to get all of the files in the JAR entry from here you must get the
     * name.
     * 
     * @param source This is the path jto the JAR file
     * @return Returns the List of Jar files
     * @throws IOException This returns if the JAR file cannot be found in the
     *                     location that is given
     */
    private List<JarEntry> getJAREntryClasses(String source) throws IOException {
        if (source == null) {
            System.out.println("The Source You Gave Is NULL");
        }

        JarFile jarFile = new JarFile(source);
        List<JarEntry> collection = Collections.list(jarFile.entries());

        jarFile.close();

        return collection;
    }

    /**
     * This list all of the files and get the name and adds them to a Array list
     * 
     * @param source This is where the JAR files is
     * @return Gives the List of all of the Classes Names
     * @throws IOException This is return if there is no JAR file found in the given
     *                     Location
     */
    private List<String> getJARClassesString(String source, List<String> exclude, boolean debug) throws IOException {
        List<String> classNamesString = new ArrayList<>();

        for (JarEntry jarEntry : getJAREntryClasses(source)) {
            for (String a : exclude) {
                if (!jarEntry.getName().contains(a)) {
                    classNamesString.add(jarEntry.getName());
                } else if (debug) {
                    System.out.println(
                            "JARClasses Processing Excluding Class " + jarEntry.getName() + "because of filter");
                }
            }
        }
        return classNamesString;
    }

    /**
     * Takes the source of the JAR file and then from there is makes a list and for
     * every name it adds an input the goes through and finds the class
     * 
     * @return Gives a list of classes in the project ClassNotFoundException when
     *         the class in the string is given is not addable
     */
    private List<Class> getJARClasses(List<String> names) throws ClassNotFoundException {
        List<Class> classList = new ArrayList<>();

        if (names != null) {
            for (String a : names) {
                classList.add(Class.forName(a));
            }
        } else {
            System.out.println("Did not give a List of names");
            throw new NullPointerException();
        }
        return classList;
    }

    /**
     * DEX files are for the Android system only so this is not supported yet and
     * this throws an unsupported error because the system does not know how to work
     * with this yet
     * 
     * @return This is a list of all classes in the DEX file
     * @throws UnsupportedEncodingException Right now this is not working and is an
     *                                      UnsupportedEncoding
     */
    private List<String> getDEXEntryClasses() throws UnsupportedEncodingException {
        List<String> returnList = new ArrayList<>();
        returnList = null;// This is a java library so you can not get the file currently

        if (returnList == null) {
            System.out.println(
                    "ERROR: No  OpModes will be loaded because currently this device does not support the DEX");
            System.out.println(
                    "File system by dalvik.system.DexFile this is an Android only feature and does not work on Java VM");
            throw new UnsupportedEncodingException();
        }

        return returnList;
    }

    private List<String> getZIPClassesString(String source, boolean debug) {
        List<String> classNamesString = new ArrayList<>();

        for (File jarEntry : getZIPEntryClasses(source)) {
            classNamesString.add(jarEntry.getAbsolutePath());
        }
        return classNamesString;
    }

    private List<Class> getZIPClasses(List<String> names) {
        List<Class> classList = new ArrayList<>();

        if (names != null) {
            for (String a : names) {
                try {
                    a = a.substring(0, a.length() - 6);//Get rid of .class
                    String[] pathSplit = a.split("\\\\");
                    String packageString = "";

                    boolean proc = false;
                    for (String b : pathSplit) {
                        if(proc){                        
                            packageString = packageString + "." + b;
                        }

                        if(b.contains("org")){
                            proc = true;
                            packageString = "org";
                        }
                    }

                    classList.add(Class.forName(packageString));
                } catch (ClassNotFoundException e) {
                    System.out.println("For Class Name Threw an Error when finding a class");
                    e.printStackTrace();
                }
            }
        } else {
            System.out.println("Did not give a List of names");
            throw new NullPointerException();
        }
        return classList;
    }

        /**
     * This is to get all Java files from a ZIP file. This does not work currently
     * @return A list of all class names
     * @throws UnsupportedEncodingException This is unsupported currently
     */
    private List<File> getZIPEntryClasses(String source) {
        assert source != null;

        // Provide full path for directory(change accordingly)   
        String maindirpath = source;
                  
        // File object 
        File maindir = new File(maindirpath); 
                   
        if(maindir.exists() && maindir.isDirectory()) { 
            // array for files and sub-directories  
            // of directory pointed by maindir 
            File arr[] = maindir.listFiles(); 
                      
            System.out.println("**********************************************"); 
            System.out.println("Files from main directory : " + maindir); 
            System.out.println("**********************************************"); 
                      
            // Calling recursive method 
            RecursivePrint(arr,0,0);  

            System.out.println("Length of list of files: " + files.size());
        }  

        if (files == null) {
            System.out.println("This is not supported currently because to is harder to program than the other two");
            System.out.println("JAR and DEX so if this is needed please let Wyatt know");
        }

        return files;
    }

    List<File> files = new ArrayList<>();
    
    public void RecursivePrint(File[] arr,int index,int level)  { 

        // terminate condition 
        if(index == arr.length) 
           return; 
           
        // for files 
        if(arr[index].isFile()) {
            System.out.print("ProjectClassCollector found a file Named: ");
            System.out.println(arr[index].getAbsolutePath()); 
            
            files.add(arr[index]);
        }

        // for sub-directories 
        else if(arr[index].isDirectory()) {               
            // recursion for sub-directories 
            RecursivePrint(arr[index].listFiles(), 0, level + 1); 
        } 
            
        // recursion for main directory 
        RecursivePrint(arr,++index, level); 
    } 
}