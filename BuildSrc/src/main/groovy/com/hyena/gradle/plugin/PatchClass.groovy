package com.hyena.gradle.plugin

import javassist.ClassPool
import javassist.CtClass

public class PatchClass {

    /**
     * 注入代码
     * @param buildDir 原路径
     * @param lib 目标路径
     */
    public static void process(String buildDir, String lib) {
//        println(lib)
        println("process")
        ClassPool classes = ClassPool.getDefault()
        classes.appendClassPath(buildDir)
//        classes.appendClassPath(lib)


        def file = new File(buildDir + "/main/com/hyena");
        List<File> fileList = new ArrayList<File>();
        iteratorFiles(fileList, file);

        for (int i = 0; i < fileList.size(); i++) {
            println(fileList.get(i).getAbsolutePath())
        }

//        //下面的操作比较容易理解,在将需要关联的类的构造方法中插入引用代码
//        CtClass c = classes.getCtClass("dodola.hotfix.BugClass")
//        if (c.isFrozen()) {
//            c.defrost()
//        }
//        println("====添加构造方法====")
//        def constructor = c.getConstructors()[0];
//        constructor.insertBefore("System.out.println(dodola.hackdex.AntilazyLoad.class);")
//        c.writeFile(buildDir)
//
//
//
//        CtClass c1 = classes.getCtClass("dodola.hotfix.LoadBugClass")
//        if (c1.isFrozen()) {
//            c1.defrost()
//        }
//        println("====添加构造方法====")
//        def constructor1 = c1.getConstructors()[0];
//        constructor1.insertBefore("System.out.println(dodola.hackdex.AntilazyLoad.class);")
//        c1.writeFile(buildDir)


    }

    private static void iteratorFiles(List<File> files, File root) {
        if (root.isDirectory()) {
            List<File> subFiles = root.listFiles();
            for (int i = 0; i < subFiles.size(); i++) {
                File subFile = subFiles.get(i);
                if (subFile.isDirectory()) {
                    iteratorFiles(files, subFile);
                } else {
                    if (subFile.getAbsolutePath().endsWith(".class"))
                        files.add(subFile);
                }
            }
        } else {
            if (root.getAbsolutePath().endsWith(".class"))
                files.add(root);
        }
    }

//    static void growl(String title, String message) {
//        def proc = ["osascript", "-e", "display notification \"${message}\" with title \"${title}\""].execute()
//        if (proc.waitFor() != 0) {
//            println "[WARNING] ${proc.err.text.trim()}"
//        }
//    }
}