package org.torc.robot2019.annotation_scanner.filters;

import java.lang.annotation.Annotation;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

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
 * Revision Num Date Who Rev 1 6/22/2019 Wyatt Ashley
 */

@SuppressWarnings("all")
public class AnnotationClassFilter {
    private List<Class> annotations = new ArrayList<>();
    private Collection<Class> classesWithAnnotations = new ArrayList<>();
    private ProjectClassCollector projectClassCollector;

    /**
     * This is were the processing happens so to restart make a new instance
     * 
     * @param _projectClassCollector A way to get classes
     * @param _annotations           What to scan for
     */
    public AnnotationClassFilter(ProjectClassCollector _projectClassCollector, List<Class> _annotations) {
        this.projectClassCollector = _projectClassCollector;
        this.annotations = _annotations;

        System.out.println("Amount of Annotations Checking for " + annotations.size());
        System.out.println("Checking " + projectClassCollector.getListClasses().size() + " Classes");
        for (Class a : this.annotations) {
            System.out.println("Checking Annotation -- " + a.getName());
            for (Class b : this.projectClassCollector.getListClasses()) {
                System.out.println("Checking Class -- " + b.getName());
        
                if (getAnnotation(b, a) != null){
                    this.classesWithAnnotations.add(b);
                    System.out.println();
                    System.out.println("Found Class With -- " + a.getName() + " -- On Class -- " + b.getName());
                    System.out.println();
                }
            }
        }
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

    /**
     * All Classes the have @ and an annotation
     * @return HashMap of Values
     */
    public Collection<Class> getClassesWithAnnotations() {
        return this.classesWithAnnotations;
    }

    /**
     * This is passed in and incase you need again
     * @return ProjectClassCollector given in constructor
     */
    public ProjectClassCollector getProjectClassCollector() {
        return this.projectClassCollector;
    }

    /**
     * Get Annotations looking for
     * @return A list of annotations
     */
    public List<Class> getAnnotations() {
        return this.annotations;
    }
}