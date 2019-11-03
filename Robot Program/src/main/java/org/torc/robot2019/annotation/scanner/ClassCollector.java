package org.torc.robot2019.annotation.scanner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.jar.JarInputStream;

@SuppressWarnings("all")
public class ClassCollector {

    /**
     * This is to get all of the files in the JAR entry from here you must get the
     * name.
     * 
     * @param source This is the path jto the JAR file
     * @return Returns the List of Jar files
     * @throws IOException This returns if the JAR file cannot be found in the
     *                     location that is given
     */
    // public static List<JarEntry> getClassesFromJAR(String source) throws IOException {

    //     if (source == null) {
    //         System.out.println("The Source You Gave Is NULL");
    //     }
    //     source = "/home/lvuser/deploy";

    //     File file = new File(source);
    //     System.out.println("Source given is Directory" + file.isDirectory());
    //     System.out.println(file.list());

    //     System.out.println("Source of JAR File" + source);
    //     JarFile jarFile = new JarFile(source);
    //     System.out.println("Amount of JAR Entries" + Collections.list(jarFile.entries()).size());
    //     //JarFile jarFile = new JarFile(source);
    //     List<JarEntry> collection = Collections.list(jarFile.entries());
        
    //     jarFile.close();

    //     return collection;
    // }

    public static List<Class<?>> getCrunchifyClassNamesFromJar(String crunchifyJarName) {
        List<Class<?>> classesList = new ArrayList<>();

		try {
			JarInputStream crunchifyJarFile = new JarInputStream(new FileInputStream(crunchifyJarName));
			JarEntry crunchifyJar;
 
			while (true) {
				crunchifyJar = crunchifyJarFile.getNextJarEntry();
				if (crunchifyJar == null) {
					break;
				}
				if ((crunchifyJar.getName().endsWith(".class"))) {
					String className = crunchifyJar.getName().replaceAll("/", "\\.");
                    String myClass = className.substring(0, className.lastIndexOf('.'));
                    Class<?> myclass = Class.forName(myClass);
					classesList.add(myclass);
				}
			}
		} catch (Exception e) {
			System.out.println("Oops.. Encounter an issue while parsing jar" + e.toString());
		}
        return classesList;
    }
    
    // public static List<Class> fileListToClassList(List<JarEntry> names){
    //     List<Class> classList = new ArrayList<>();

    //     for (JarEntry a : names) {
    //         try {
    //             String c = a.getAbsolutePath().substring(0, (a.getAbsolutePath().length() - 6));// Get rid of .class
    //             String[] pathSplit = c.split("\\\\");
    //             String packageString = "";
                
    //             boolean proc = false;
    //             for (String b : pathSplit) {
    //                 if(proc){                        
    //                     packageString = packageString + "." + b;
    //                 }

    //                 if(b.contains("frc")){
    //                     proc = true;
    //                     packageString = "frc";
    //                 }
    //             }

    //             System.out.println(c);
    //             System.out.println("Looking for " + packageString);
    //             classList.add(Class.forName(packageString));
    //         } catch (ClassNotFoundException e) {
    //             e.printStackTrace();
    //         }
    //     }
        
    //     return classList;
    // }
    
    // public static List<File> getZIPEntryClasses(String source) {
    //     assert source != null;

    //     // File object 
    //     File maindir = new File(source); 
                   
    //     if(maindir.exists() && maindir.isDirectory()) { 
    //         // array for files and sub-directories  
    //         // of directory pointed by maindir 
    //         File arr[] = maindir.listFiles(); 
                      
    //         //System.out.println("**********************************************"); 
    //         //System.out.println("Files from main directory : " + maindir); 
    //         //System.out.println("**********************************************"); 
                      
    //         // Calling recursive method 
    //         RecursivePrint(arr,0,0);  

    //         System.out.println("Length of list of files: " + files.size());
    //     }  

    //     return files;
    // }

    // private static List<File> files = new ArrayList<>();
    
    // private static void RecursivePrint(File[] arr,int index,int level)  { 

    //     // terminate condition 
    //     if(index == arr.length) 
    //        return; 
           
    //     // for files 
    //     if(arr[index].isFile()) {
    //         //System.out.print("ProjectClassCollector found a file Named: ");
    //         //System.out.println(arr[index].getAbsolutePath()); 
            
    //         files.add(arr[index]);
    //     }

    //     // for sub-directories 
    //     else if(arr[index].isDirectory()) {               
    //         // recursion for sub-directories 
    //         RecursivePrint(arr[index].listFiles(), 0, level + 1); 
    //     } 
            
    //     // recursion for main directory 
    //     RecursivePrint(arr,++index, level); 
    // }

    public static List<Class> AnnotationFilter(Class _annotations, String source){
        List<Class> classesWithAnnotations = new ArrayList<>();

        for (Class b : getCrunchifyClassNamesFromJar(source)) {
            //System.out.println("Checking Class -- " + b.getName());
    
            if (getAnnotation(b, _annotations) != null){
                classesWithAnnotations.add(b);
                System.out.println();
                System.out.println("Found Class With -- " + _annotations.getName() + " -- On Class -- " + b.getCanonicalName());
                System.out.println();
            }
        }

        return classesWithAnnotations;
    }

    public static <T extends Annotation> T getAnnotation(Class<?> clazz, Class<T> annotationType) {
        T result = clazz.getAnnotation(annotationType);
        if (result == null) {
            Class<?> superclass = clazz.getSuperclass();
            if (superclass != null) {
                return getAnnotation(superclass, annotationType);
            } else {
                return null;
            }
        } else {
            return result;
        }
    }
}
