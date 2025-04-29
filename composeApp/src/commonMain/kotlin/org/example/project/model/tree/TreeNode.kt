package org.example.project.model.tree

import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DriveFolderUpload
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.EditOff
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.minimumInteractiveComponentSize
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.FileKitMode
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.launch
import org.example.project.model.dto.FileDto
import java.awt.TextArea
import java.util.*

typealias Visitor<T> = (TreeNode<T>) -> Unit

@Suppress("UNCHECKED_CAST")
class TreeNode<T>(val value: T) {

    private val children: MutableList<TreeNode<T>> = mutableListOf()

    fun addChild(child: TreeNode<T>) {
        children.add(child)
    }

    @Composable
    fun forEachDepthFirst(visit: Visitor<T>) {
        visit(this)

        children.forEach { child ->
            child.forEachDepthFirst(visit)
        }
    }

    // Custom data travel for FileDto
    @Composable
    fun forEachDepthFirstText(spacer: String) {
        val nextSpacer = "$spacer  "
        val collapsedList = remember { mutableStateOf(mutableMapOf<TreeNode<T>, Boolean>()) }
        val collapsedOptions = remember { mutableStateOf(mutableMapOf<TreeNode<T>, Boolean>()) }
        val editMode = remember { mutableStateOf(false) }

        children.forEach { child ->
            if (child.value is FileDto) {
                val fileDto = child.value as FileDto
                val isDirectory = fileDto.fileType == "directory"
                val isCollapsed = collapsedList.value[child] ?: false
                val isCollapsedOptions = collapsedOptions.value[child] ?: false
                val scope = rememberCoroutineScope()
                val fileName = remember { mutableStateOf(fileDto.fileName ?: "") }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.padding(vertical = 2.dp)
                ) {
                    Spacer(modifier = Modifier.width(4.dp))
                    if (isDirectory) {
                        IconButton(
                            onClick = {
                                collapsedList.value = collapsedList.value.toMutableMap().apply {
                                    this[child] = !isCollapsed
                                }
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                                contentDescription = if (isCollapsed) "Expandir" else "Colapsar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }

                    Icon(
                        imageVector = when {
                            isDirectory -> Icons.Filled.Folder
                            else -> Icons.AutoMirrored.Filled.InsertDriveFile
                        },
                        contentDescription = null,
                        tint = if (isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(16.dp)
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    // File name
                    if (editMode.value) {
                        TextField(
                            value = fileName.value,
                            onValueChange = { fileName.value = it
                                              fileDto.fileName = it},
                            placeholder = { androidx.compose.material3.Text("File name can't be empty") },
                            singleLine = true,
                            isError = fileDto.fileName.isNullOrEmpty(),
                            colors = TextFieldDefaults.colors(
                                cursorColor = MaterialTheme.colorScheme.onSurface
                            ),
                            textStyle = MaterialTheme.typography.bodySmall.copy(
                                color = if (isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.wrapContentSize()
                        )
                    } else {
                        Text(
                            text = fileName.value,
                            style = MaterialTheme.typography.bodySmall,
                            color = if (isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Show extra options
                    IconButton(
                        onClick = {
                            collapsedOptions.value = collapsedOptions.value.toMutableMap().apply {
                                this[child] = !isCollapsedOptions
                            }
                        },
                        modifier = Modifier.size(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                            contentDescription = if (isCollapsed) "Expandir" else "Colapsar",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                    if (collapsedOptions.value[child] == true) {
                        // Directory options
                        if (isDirectory) {
                            // Add new directory to file system
                            IconButton(
                                onClick = {
                                    child.addChild(TreeNode(FileDto(fileName = "folder", fileType = "directory") as T))
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.DriveFolderUpload,
                                    contentDescription = if (isCollapsed) "Expandir" else "Colapsar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                            // Add new file
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val newFiles = FileKit.openFilePicker(
                                            title = "Select model files",
                                            mode = FileKitMode.Multiple()
                                        )
                                        newFiles?.forEach { file ->
                                            if (file.exists()) {
                                                val newFileDto = FileDto(
                                                    fileName = file.name,
                                                    fileType = "file"
                                                )
                                                child.addChild(TreeNode(newFileDto as T))
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier.size(24.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Filled.Add,
                                    contentDescription = if (isCollapsed) "Expandir" else "Colapsar",
                                    tint = MaterialTheme.colorScheme.primary,
                                    modifier = Modifier.size(16.dp)
                                )
                            }
                        }
                        // Rename file button
                        IconButton(
                            onClick = {
                                editMode.value = !editMode.value
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = if (!editMode.value) Icons.Filled.Edit else Icons.Filled.EditOff,
                                contentDescription = if (isCollapsed) "Expandir" else "Colapsar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        // Delete button
                        IconButton(
                            onClick = {
                                children.remove(child)
                            },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = if (isCollapsed) "Expandir" else "Colapsar",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                if (isDirectory && !isCollapsed) {
                    Column(
                        modifier = Modifier.padding(start = 12.dp)
                    ) {
                        child.forEachDepthFirstText(nextSpacer)
                    }
                }
            }
        }
    }


    @Composable
    fun forEachLevelFirst(visit: Visitor<T>) {
        visit(this)

        val queue = LinkedList<TreeNode<T>>()
        queue.add(this)
        while (queue.isNotEmpty()) {
            val node = queue.remove()
            visit(node)

            node.children.forEach { child ->
                queue.add(child)
            }
        }
    }

    @Composable
    fun search(value: T): TreeNode<T>? {
        var result: TreeNode<T>? = null

        forEachDepthFirst {
            if (it.value == value) {
                result = it
            }
        }

        return result
    }
}