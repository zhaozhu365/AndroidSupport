package com.hyena.gradle.plugin

import com.android.build.api.transform.*
import com.android.build.gradle.internal.pipeline.TransformManager
import javassist.ClassPool
import javassist.CtClass
import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils

public class PreDexTransform extends Transform {

    @Override
    String getName() {
        return "preDex"
    }

    @Override
    Set<QualifiedContent.ContentType> getInputTypes() {
        return TransformManager.CONTENT_CLASS
    }

    @Override
    Set<QualifiedContent.Scope> getScopes() {
        return TransformManager.SCOPE_FULL_LIBRARY
    }

    @Override
    boolean isIncremental() {
        return false
    }

    @Override
    void transform(Context context, Collection<TransformInput> inputs,
                   Collection<TransformInput> referencedInputs,
                   TransformOutputProvider outputProvider,
                   boolean isIncremental) throws IOException, TransformException, InterruptedException {
//        super.transform(context, inputs, referencedInputs, outputProvider, isIncremental)
        for (int i = 0; i < inputs.size(); i++) {
            TransformInput input = inputs.getAt(i);
            if (input != null) {
                Collection<DirectoryInput> directoryInputs = input.directoryInputs;
                for (int j = 0; j < directoryInputs.size(); j++) {
                    DirectoryInput directoryInput = directoryInputs.getAt(j);
                    def dest = outputProvider.getContentLocation(directoryInput.name, directoryInput.contentTypes, directoryInput.scopes, Format.DIRECTORY)
                    println("directionInput-->$directoryInput.file.absolutePath")
                    println("directionOut-->$dest.absolutePath")
//                    FileUtils.copyDirectory(directoryInput.file, dest);
                    ClassPool classPool = ClassPool.getDefault();
                    classPool.appendClassPath(directoryInput.file.absolutePath);

                    copyDirectory(directoryInput.file, dest, classPool, directoryInput.file.absolutePath);
                }

                Collection<JarInput> jarInputs = input.jarInputs;
                for (int j = 0; j < jarInputs.size(); j++) {
                    JarInput jarInput = jarInputs.getAt(j);
                    println("jarInput-->$jarInput.file.absolutePath")
                    def jarName = jarInput.name;
                    def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath());
                    if (jarName.endsWith(".jar")) {
                        jarName = jarName.substring(0, jarName.length() - 4);
                    }
                    def dest = outputProvider.getContentLocation(jarName + md5Name, jarInput.contentTypes, jarInput.scopes, Format.JAR);
                    FileUtils.copyFile(jarInput.file, dest)
                }
            }
        }
    }

    void copyDirectory(File src, File dest, ClassPool classPool, String classPath) {
        if (src.isDirectory()) {
            File[] list = src.listFiles();
            for (int i = 0; i < list.length; i++) {
                File newPath = new File(src, list[i].getName());
                File newCopyPath = new File(dest, list[i].getName());
                copyDirectory(newPath, newCopyPath, classPool, classPath);
            }
        } else {
            copyFile(src, dest, classPool, classPath);
        }
    }

    void copyFile(File src, File dest, ClassPool classPool, String classPath) {
//        println(src.absolutePath)
//        println(classPath)
        String className = src.absolutePath.replace(classPath, "").replace(".class", "");
        className = className.substring(1, className.length()).replaceAll("/", ".");
        CtClass ctClass = classPool.getCtClass(className);
        if (className.contains("com.hyena.framework.app.fragment.BaseUIFragment")
                && !ctClass.isInterface()
                && !className.contains("\$")) {
            if (ctClass.isFrozen()) {
                ctClass.defrost();
            }
            println("classname-->$ctClass.name")
            def constructor = ctClass.getConstructors()[0];
            println("constructor-->$constructor.name")
            constructor.insertAfter("System.out.println(111);");
            FileUtils.copyFile(src, dest)
//            ctClass.writeFile(classPath);
        } else {
            FileUtils.copyFile(src, dest)
        }
    }
}