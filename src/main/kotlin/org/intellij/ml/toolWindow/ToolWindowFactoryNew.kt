package org.intellij.ml.toolWindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.content.ContentFactory
import javax.swing.JLabel
import javax.swing.JPanel


class ToolWindowFactoryNew : ToolWindowFactory, DumbAware {
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val label = JLabel("Hello, this is your Tool Window!")
        val panel = JPanel()
        panel.add(label)
        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }
    override fun shouldBeAvailable(project: Project) = true
}

//class LLMToolWindowFactory : ToolWindowFactory {
//
//    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
//        val llmToolWindow = LLMToolWindow()
//        val content = ContentFactory.getInstance().createContent(llmToolWindow.getContent(), null, false)
//        toolWindow.contentManager.addContent(content)
//    }
//
//    override fun shouldBeAvailable(project: Project) = true
//
//    class LLMToolWindow {
//        private val noLLM = "NO LLM found. ".trimMargin()
//
//        fun getContent() = JBPanel<JBPanel<*>>().apply {
//            layout = BoxLayout(this, BoxLayout.PAGE_AXIS)
//            val headerPanel = JPanel()
//            headerPanel.add(JBLabel(if (modelsList.isNotEmpty()) "Available models" else noLLM).apply {
//                font = font.deriveFont(Font.BOLD)
//            })
//            add(headerPanel)
//
//            if (modelsList.isNotEmpty()) {
//                val footerLabel = JBLabel("Current LLM: ${modelsList[currentModelIndex.get()]}")
//                modelsList.forEachIndexed { index, model ->
//                    add(JPanel().apply {
//                        add(JBLabel(model.capitalize()))
//                        add(JButton("Apply").apply {
//                            addActionListener {
//                                currentModelIndex.set(index)
//                                footerLabel.text = "Current LLM: ${modelsList[currentModelIndex.get()]}"
//                            }
//                        })
//                    })
//                }
//                add(JPanel().apply { add(footerLabel) })
//            }
//        }
//    }
//}