package org.intellij.ml.actions

import com.intellij.openapi.actionSystem.AnAction
import com.intellij.openapi.actionSystem.AnActionEvent
import com.intellij.openapi.wm.ToolWindowManager
import com.intellij.openapi.project.Project

class ShowToolWindowAction : AnAction() {

    override fun actionPerformed(e: AnActionEvent) {
        val project: Project? = e.project
        val toolWindow = ToolWindowManager.getInstance(project!!).getToolWindow("OSS LLMs")
        toolWindow?.show { println("Hello! Test!") }
    }
}
