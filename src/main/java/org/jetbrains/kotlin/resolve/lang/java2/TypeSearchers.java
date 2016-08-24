/**
 * *****************************************************************************
 * Copyright 2000-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 ******************************************************************************
 */
package org.jetbrains.kotlin.resolve.lang.java2;

import javax.lang.model.type.ArrayType;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.type.WildcardType;
import org.jetbrains.kotlin.load.java.structure.JavaType;
import org.jetbrains.kotlin.resolve.lang.java.structure2.NetBeansJavaType;
import org.netbeans.api.java.source.CompilationController;
import org.netbeans.api.java.source.JavaSource.Phase;
import org.netbeans.api.java.source.Task;
import org.netbeans.api.java.source.TypeMirrorHandle;
import org.netbeans.api.project.Project;

/**
 *
 * @author Alexander.Baratynski
 */
public class TypeSearchers {
    
    public static class TypeNameSearcher implements Task<CompilationController> {

        private final TypeMirrorHandle handle;
        private String name = null;
        
        public TypeNameSearcher(TypeMirrorHandle handle) {
            this.handle = handle;
        }
        
        @Override
        public void run(CompilationController info) throws Exception {
            info.toPhase(Phase.RESOLVED);
            TypeMirror type = handle.resolve(info);
            if (type == null) {
                return;
            }
            
            name = type.toString();
        }
        
        public String getName() {
            return name;
        }
        
    }
    
    public static class BoundSearcher implements Task<CompilationController> {

        private final TypeMirrorHandle handle;
        private final Project project;
        private JavaType bound = null;
        
        public BoundSearcher(TypeMirrorHandle handle, Project project) {
            this.handle = handle;
            this.project = project;
        }
        
        @Override
        public void run(CompilationController info) throws Exception {
            info.toPhase(Phase.RESOLVED);
            TypeMirror type = handle.resolve(info);
            if (type == null) {
                return;
            }
            
            TypeMirror boundMirror = ((WildcardType) type).getSuperBound();
            bound = boundMirror != null ? NetBeansJavaType.create(
                    TypeMirrorHandle.create(boundMirror), project) : null;
        }
        
        public JavaType getBound() {
            return bound;
        }
        
    }
    
    public static class IsExtendsSearcher implements Task<CompilationController> {

        private final TypeMirrorHandle handle;
        private boolean isExtends = false;
        
        public IsExtendsSearcher(TypeMirrorHandle handle) {
            this.handle = handle;
        }
        
        @Override
        public void run(CompilationController info) throws Exception {
            info.toPhase(Phase.RESOLVED);
            TypeMirror type = handle.resolve(info);
            if (type == null) {
                return;
            }
            
            TypeMirror boundMirror = ((WildcardType) type).getExtendsBound();
            isExtends = boundMirror != null;
        }
        
        public boolean isExtends() {
            return isExtends;
        }
        
    }
    
    public static class ComponentTypeSearcher implements Task<CompilationController> {

        private final TypeMirrorHandle handle;
        private final Project project;
        private JavaType componentType = null;
        
        public ComponentTypeSearcher(TypeMirrorHandle handle, Project project) {
            this.handle = handle;
            this.project = project;
        }
        
        @Override
        public void run(CompilationController info) throws Exception {
            info.toPhase(Phase.RESOLVED);
            TypeMirror type = handle.resolve(info);
            if (type == null) {
                return;
            }
            
            TypeMirrorHandle componentTypeHandle = 
                    TypeMirrorHandle.create(((ArrayType) type).getComponentType());
            componentType = NetBeansJavaType.create(componentTypeHandle, project);
        }
        
        public JavaType getComponentType() {
            return componentType;
        }
        
    }
    
}
