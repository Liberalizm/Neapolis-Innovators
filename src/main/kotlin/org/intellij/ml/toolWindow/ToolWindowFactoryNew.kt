package org.intellij.ml.toolWindow

import com.intellij.openapi.project.DumbAware
import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.components.JBLabel
import com.intellij.ui.components.JBPanel
import com.intellij.ui.components.JBScrollPane
import com.intellij.ui.content.ContentFactory
import javax.swing.*
import java.awt.Dimension
import org.json.JSONObject
import okhttp3.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import java.io.IOException
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.call.*
import kotlinx.coroutines.*

fun sendRequest(): String = runBlocking {
    val client = HttpClient(CIO)
    val response: HttpResponse = client.post("http://localhost:8080/post") {
        setBody("msg")
    }

    println(response.status)

    client.close()
    return@runBlocking response.status.toString()
}


class ToolWindowFactoryNew : ToolWindowFactory, DumbAware {
//    private val apiKey = "YOUR_OPENAI_API_KEY_HERE"
    private val apiKey = "YOUR ADVERTISEMENT HERE!"
    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val label = JLabel("Plugin Tool Window")

        val promptField = JTextField(10)
        val fixedSize = Dimension(200, 50)
        promptField.preferredSize = fixedSize
        promptField.minimumSize = fixedSize
        promptField.maximumSize = fixedSize

        val directoryPath = JTextField(10)
        directoryPath.preferredSize = fixedSize
        directoryPath.minimumSize = fixedSize
        directoryPath.maximumSize = fixedSize

        val responseArea = JTextArea(10, 30)
        val responseAreaSize = Dimension(500, 100)
        responseArea.lineWrap = true
        responseArea.wrapStyleWord = true
        responseArea.isEditable = false
        responseArea.autoscrolls = true

        responseArea.preferredSize = responseAreaSize
        responseArea.minimumSize = responseAreaSize
        val button = JButton("Send Query")

        button.addActionListener {
            val enteredText = promptField.text
            responseArea.text = "Sending query..."
            sendChatGPTQuery(enteredText, responseArea)
        }
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(label)
        panel.add(promptField)
        panel.add(directoryPath)
        panel.add(responseArea)
        panel.add(button)

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun sendChatGPTQuery(prompt: String, responseArea: JTextArea) {
        val client = OkHttpClient()

        val requestBody = """
            {
              "model": "gpt-3.5-turbo",
              "messages": [{"role": "user", "content": "$prompt"}]
            }
        """.trimIndent()

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(RequestBody.create("application/json; charset=utf-8".toMediaTypeOrNull(), requestBody))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                SwingUtilities.invokeLater {
                    responseArea.text = "Error: ${e.message}"
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        SwingUtilities.invokeLater {
                            responseArea.text = "Error: ${response.message}"
                        }
                    } else {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        val chatResponse = jsonResponse.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        SwingUtilities.invokeLater {
                            responseArea.text = chatResponse
                        }
                    }
                }
            }
        })
    }
    override fun shouldBeAvailable(project: Project) = true

}
