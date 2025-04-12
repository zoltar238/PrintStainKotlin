package org.example.project.model.tree

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import org.example.project.model.dto.FileDto
import java.util.*

typealias Visitor<T> = (TreeNode<T>) -> Unit

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
        children.forEach { child ->
            if (child.value is FileDto) {
                Text(text = "$spacer ${child.value.fileName!!}")
            }
            child.forEachDepthFirstText(nextSpacer)

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