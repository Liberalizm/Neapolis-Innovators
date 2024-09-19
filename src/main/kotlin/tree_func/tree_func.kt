package tree_func

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.LocalFileSystem
//import com.intellij.openapi.vfs.VirtualFile
import io.ktor.client.content.*
import java.nio.file.Paths
import com.intellij.openapi.project.ProjectManager
import com.intellij.openapi.project.ex.ProjectManagerEx
import java.io.File
import org.json.JSONArray
import org.json.JSONObject

class GetFilesInJson(private val directoryPath: String) {
    fun listFilesInDirectory(): List<String> {
        val directory = File(directoryPath)

        if (!directory.exists() || !directory.isDirectory) {
            return emptyList()
        }

        val fileList = mutableListOf<String>()
        collectFiles(directory, fileList)
        return fileList
    }

    private fun collectFiles(dir: File, fileList: MutableList<String>, parentPath: String = "") {
        val files = dir.listFiles() ?: return
        for (file in files) {
            val relativePath = if (parentPath.isEmpty()) file.name else "$parentPath/${file.name}"
            if (file.isFile) {
                fileList.add(relativePath)
            } else if (file.isDirectory) {
                collectFiles(file, fileList, relativePath)
            }
        }
    }
}
fun listFilesInDirectory(directoryPath: String): String {
    val directory = File(directoryPath)
    if (!directory.exists() || !directory.isDirectory) {
        return """{"message": "The specified path is not a valid directory."}"""
    }
    val fileList = mutableListOf<String>()
    collectFiles(directory, fileList)
    val formattedFiles = fileList.mapIndexed { index, filePath ->
        "${index + 1}. $filePath"
    }.joinToString("\n")

    return """{"message": "The project contains ${fileList.size} files:\n\n$formattedFiles"}"""
}

fun collectFiles(dir: File, fileList: MutableList<String>, parentPath: String = "") {
    val files = dir.listFiles() ?: return
    for (file in files) {
        val relativePath = if (parentPath.isEmpty()) file.name else "$parentPath/${file.name}"
        if (file.isFile) {
            fileList.add(relativePath)
        } else if (file.isDirectory) {
            collectFiles(file, fileList, relativePath)
        }
    }
}
