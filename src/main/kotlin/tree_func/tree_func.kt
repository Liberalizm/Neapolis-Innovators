package tree_func

import com.intellij.openapi.project.Project
import com.intellij.openapi.vfs.VirtualFile

// Function to print the file tree recursively
fun printFileTree(file: VirtualFile, indent: String = "") {
    // Print the current file or directory
    println("$indent${file.name}")

    // If it's a directory, recursively print its contents
    if (file.isDirectory) {
        for (child in file.children) {
            printFileTree(child, "$indent    ")  // Add indentation for child files
        }
    }
}

// Entry function to get the root of the project and print the file tree
fun printProjectFileTree(project: Project) {
    val baseDir: VirtualFile? = project.baseDir  // Get the base directory of the project
    if (baseDir != null) {
        println("Project: ${project.name}")
        printFileTree(baseDir)  // Start printing the file tree
    } else {
        println("No base directory found for project.")
    }
}
