package org.example.project.model.tree

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.InsertDriveFile
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import io.github.vinceglb.filekit.FileKit
import io.github.vinceglb.filekit.dialogs.openFilePicker
import io.github.vinceglb.filekit.exists
import io.github.vinceglb.filekit.name
import kotlinx.coroutines.launch
import org.example.project.model.dto.FileDto
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

        children.forEach { child ->
            if (child.value is FileDto) {
                val fileDto = child.value as FileDto
                val isDirectory = fileDto.fileType == "directory"
                val isCollapsed = collapsedList.value[child] ?: false
                val isCollapsedOptions = collapsedOptions.value[child] ?: false
                val scope = rememberCoroutineScope()

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

                    Text(
                        text = fileDto.fileName ?: "",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (isDirectory) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                    )
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
                        if (isDirectory) {
                            IconButton(
                                onClick = {
                                    scope.launch {
                                        val newChild = FileKit.openFilePicker()
                                        if (newChild != null && newChild.exists()) {
                                            val newFileDto = FileDto(
                                                fileName = newChild.name,
                                                fileType = "file"
                                            )
                                            child.addChild(TreeNode(newFileDto as T))
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
                        IconButton(
                            onClick = {
                                collapsedList.value = collapsedList.value.toMutableMap().apply {
                                    this[child] = !isCollapsedOptions
                                }
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