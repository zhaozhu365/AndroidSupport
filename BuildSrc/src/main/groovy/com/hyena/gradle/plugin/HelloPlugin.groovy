package com.hyena.gradle.plugin

import org.gradle.api.Plugin
import org.gradle.api.Project

class HelloPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        println("======================")
        println("hello world!!!")
        println("======================")
    }

}