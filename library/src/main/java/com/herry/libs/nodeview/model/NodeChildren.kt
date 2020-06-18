package com.herry.libs.nodeview.model

import kotlin.reflect.KClass




internal class NodeChildren(private val notify: (param: NodeNotifyParam, then: () -> Unit) -> Unit) {

    var viewCount = 0
        private set

    private val list = mutableListOf<Node<*>>()

    internal fun traversals(): Int {
        viewCount = 0
        for (childNode in list) {
            childNode.viewPosition = viewCount
            childNode.traversals()
            viewCount += childNode.getViewCount()
        }
        return viewCount
    }

    internal fun getCount(): Int = list.size

    internal fun get(index: Int): Node<*>? {
        return if (index in 0 until list.size) {
            list[index]
        } else
            null
    }

    internal fun setNodeId(root: NodeRoot) {
        for (node in list) {
            node.setNodeId(root)
        }
    }

    internal fun indexOf(node: Node<*>): Int {
        return list.indexOf(node)
    }

    internal fun clear() {
        if(viewCount > 0) {
            notify(NodeNotifyParam(NodeNotifyParam.STATE.REMOVE, 0, viewCount)) {
                for (node in list) {
                    node.parent = null
                }
                list.clear()
                viewCount = 0
            }
        } else {
            list.clear()
            viewCount = 0
        }
    }

    internal fun add(parent: Node<*>, nodes: MutableList<Node<*>>, position: Int = list.size) {
        val startNodePosition: Int
        val startViewPosition: Int
        if (position < list.size) {
            startNodePosition = position
            startViewPosition = list[startNodePosition].viewPosition
        } else {
            startNodePosition = list.size
            startViewPosition = viewCount
        }

        var addedViewCount = 0
        for (node in nodes) {
            node.parent = parent
            node.viewPosition =  startViewPosition + addedViewCount
            node.getRoot()?.let {
                node.setNodeId(it)
            }
            node.traversals()
            addedViewCount += node.getViewCount()
        }

        if (addedViewCount > 0) {
            notify(NodeNotifyParam(NodeNotifyParam.STATE.INSERT, startViewPosition, addedViewCount)) {
                list.addAll(startNodePosition, nodes)

                viewCount = startViewPosition + addedViewCount
                for(i in startNodePosition + nodes.size until list.size) {
                    list[i].viewPosition = viewCount
                    viewCount += list[i].getViewCount()
                }
            }
        } else {
            list.addAll(startNodePosition, nodes)
            traversals()
        }
    }

    internal fun remove(position: Int, count: Int) {
        var removedViewPosition = -1
        var removedViewCount = 0

        for (i: Int in 0 until count) {
            if(position in 0 until list.size) {
                if(removedViewPosition < 0) {
                    removedViewPosition = list[position + i].viewPosition
                }
                removedViewCount += list[position + i].getViewCount()
            }
        }

        if (removedViewPosition >= 0 && removedViewCount > 0) {
            notify(NodeNotifyParam(NodeNotifyParam.STATE.REMOVE, removedViewPosition, removedViewCount)) {
                for (i: Int in 0 until count) {
                    if(position in 0 until list.size) {
                        val node = list.removeAt(position)
                        node.parent = null
                    }
                }

                viewCount = removedViewPosition
                for(i in position until list.size) {
                    list[i].viewPosition = viewCount
                    viewCount += list[i].getViewCount()
                }
            }
        } else {
            for (i: Int in 0 until count) {
                if(position in 0 until list.size) {
                    val node = list.removeAt(position)
                    node.parent = null
                }
            }
        }
    }

    internal fun getNodePosition(viewPosition: Int): NodePosition? {
        var low = 0
        var high = getCount() - 1
        var mid = (low + high) / 2
        while (low <= high) {
            val node = get(mid)
            if (node == null) {
                high = (low + high) / 2
                mid = (low + high) / 2
                continue
            }

            val cPos = node.getViewPosition()
            val cCnt = node.getViewCount()
            if (cCnt <= 0) {
                mid++
                continue
            }

            if (cPos > viewPosition) {
                high = if (mid < high) mid - 1 else high - 1
            } else if (cPos + cCnt - 1 < viewPosition) {
                low = mid + 1
            } else {
                break
            }
            mid = (low + high) / 2
        }
        val node = get(mid)
        return if (node != null) {
            NodePosition.compose(mid,
                    node.getNodePosition(viewPosition - node.getViewPosition()))
        } else
            null
    }

    internal fun replace(parent: Node<*>, nodeChildren: NodeChildren) {
        val fromList = getIdList(list)
        val toList = getIdList(nodeChildren.list)
        var fromIndex = 0

        while (fromList.isNotEmpty() && toList.isNotEmpty()) {
            val fromId = fromList[0].id

            if(fromId.isNode && fromId.id == null) {
                remove(fromIndex, fromList.removeAt(0).size)
            } else {
                val containToIndex = containIdList(toList, fromId)
                when {
                    containToIndex < 0 -> remove(fromIndex, fromList.removeAt(0).size)
                    containToIndex > 0 -> {
                        val addList = mutableListOf<Node<*>>()
                        for (i in 0 until containToIndex) {
                            addList.addAll(toList.removeAt(0))
                        }
                        add(parent, addList, fromIndex)
                        fromIndex += addList.size
                    }
                    else -> {
                        val fromIdList = fromList.removeAt(0)
                        val toIdList = toList.removeAt(0)
                        var i = 0
                        while (i < fromIdList.size && i < toIdList.size) {
                            fromIdList[i].replace(toIdList[i])
                            i++
                        }

                        fromIndex += when {
                            i < fromIdList.size -> {
                                remove(fromIndex + i, fromIdList.size - i)
                                i
                            }
                            i < toIdList.size -> {
                                add(parent, toIdList.subList(i, toIdList.size), fromIndex + i)
                                toIdList.size
                            }
                            else -> {
                                i
                            }
                        }
                    }
                }
            }
        }

        if(fromList.isNotEmpty()) {
            var removeCount = 0
            for (fromIdList in fromList) {
                removeCount += fromIdList.size
            }
            remove(fromIndex, removeCount)
        }

        if(toList.isNotEmpty()) {
            val addList = mutableListOf<Node<*>>()
            for (toIdList in toList) {
                addList.addAll(toIdList)
            }
            add(parent, addList)
        }
    }

    private class IdList(val id: Id): MutableList<Node<*>> by ArrayList()

    private data class Id(
            val kClass: KClass<out Any>,
            val isNode: Boolean = false,
            val id: Any? = null
    )

    private fun getIdList(items: MutableList<Node<*>>): MutableList<IdList> {
        val idList = mutableListOf<IdList>()
        var list: IdList? = null
        for(item in items) {
            val id = generateId(item)
            when {
                list == null -> {
                    list = IdList(id)
                    list.add(item)
                }
                list.id == id -> list.add(item)
                else -> {
                    if(list.size > 0) {
                        idList.add(list)
                    }
                    list = IdList(id)
                    list.add(item)
                }
            }
        }
        list?.let {
            idList.add(it)
        }
        return idList
    }

    private fun generateId(node: Node<*>): Id {
        val model = node.model
        return if(model is INodeModelGroup) {
            Id(model::class, true, model.id)
        } else {
            Id(model::class)
        }
    }

    private fun containIdList(list: MutableList<IdList>, id: Id): Int {
        for(i in 0 until list.size) {
            if(list[i].id == id) {
                return i
            }
        }
        return -1
    }
}