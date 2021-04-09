package com.herry.test.app.list.endless

import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup

class EndlessListPresenter : EndlessListContract.Presenter() {

    private val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

    override fun onAttach(view: EndlessListContract.View) {
        super.onAttach(view)

        view.root.beginTransition()
        NodeHelper.addNode(view.root, nodes)
        view.root.endTransition()
    }

    override fun onLaunch(view: EndlessListContract.View, recreated: Boolean) {
        if (recreated) {
            return
        }

        loadList()
    }

    private fun createListItems(startIndex: Int, count: Int) : MutableList<EndlessListContract.ListItemData> {
        val items = mutableListOf<EndlessListContract.ListItemData>()
        for(index in startIndex until (startIndex + count)) {
            items.add(EndlessListContract.ListItemData("$index"))
        }

        return items
    }

    private fun loadList() {
        view?.getContext() ?: return

        this.nodes.beginTransition()

        val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

        NodeHelper.addModels(nodes, *createListItems(0, 50).toTypedArray())
        NodeHelper.upSert(this.nodes, nodes)

        this.nodes.endTransition()
    }

    override fun loadMore() {
        view?.getContext() ?: return

        this.nodes.beginTransition()

        val currentItemCount = this.nodes.getChildCount()
        NodeHelper.addModels(this.nodes, *createListItems(currentItemCount - 1, 30).toTypedArray())

        this.nodes.endTransition()
    }

}