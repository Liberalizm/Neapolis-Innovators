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
import org.jetbrains.io.response
import tree_func.GetFilesInJson
import org.json.JSONArray

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
            val enteredPrompt = promptField.text
            val enteredPath = directoryPath.text
            responseArea.text = "Sending query..."
            sendChatGPTQuery(enteredPrompt, enteredPath, responseArea)
        }
        val panel = JPanel()
        panel.layout = BoxLayout(panel, BoxLayout.Y_AXIS)
        panel.add(label)
        panel.add(descriptionPrompt)
        panel.add(promptField)
        panel.add(descriptionLabel)
        panel.add(directoryPath)
        panel.add(responseArea)
        panel.add(button)

        val contentFactory = ContentFactory.getInstance()
        val content = contentFactory.createContent(panel, "", false)
        toolWindow.contentManager.addContent(content)
    }

    private fun sendChatGPTQuery(prompt: String, dirPath: String, responseArea: JTextArea) {
        val allFiles = GetFilesInJson(dirPath).listFilesInDirectory()
        val systemMessageContent = """
        You are a helpful assistant. Your task is to take all files from the directory that are provided here: 
        ${allFiles.joinToString(", ", "[", "]")}. Given the user's prompt, help them find the most relevant file in this list of files.
    """.trimIndent()

        val requestBodyJson = JSONObject().apply {
            put("model", "gpt-3.5-turbo")
            put("messages", JSONArray().apply {
                put(JSONObject().apply {
                    put("role", "system")
                    put("content", systemMessageContent)
                })
                put(JSONObject().apply {
                    put("role", "user")
                    put("content", prompt)
                })
            })
        }

        // Create the request body
        val requestBody = RequestBody.create(
            "application/json; charset=utf-8".toMediaTypeOrNull(),
            requestBodyJson.toString()
        )

        val request = Request.Builder()
            .url("https://api.openai.com/v1/chat/completions")
            .header("Authorization", "Bearer $apiKey")
            .header("Content-Type", "application/json")
            .post(requestBody)
            .build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                SwingUtilities.invokeLater {
                    println("Error: ${e.message}")
                    responseArea.text = e.message
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!response.isSuccessful) {
                        SwingUtilities.invokeLater {
                            println("Error: ${response.message}")
                            responseArea.text = response.message
                        }
                    } else {
                        val responseBody = response.body?.string()
                        val jsonResponse = JSONObject(responseBody ?: "")
                        val chatResponse = jsonResponse.getJSONArray("choices")
                            .getJSONObject(0)
                            .getJSONObject("message")
                            .getString("content")
                        SwingUtilities.invokeLater {
                            println(chatResponse)
                            responseArea.text = chatResponse
                        }
                    }
                }
            }
        })
    }
    override fun shouldBeAvailable(project: Project) = true

}
