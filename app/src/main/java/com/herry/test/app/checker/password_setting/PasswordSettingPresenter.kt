package com.herry.test.app.checker.password_setting

import com.herry.libs.nodeview.model.Node
import com.herry.libs.nodeview.model.NodeHelper
import com.herry.libs.nodeview.model.NodeModelGroup
import com.herry.libs.util.preferences.PreferenceUtil
import com.herry.test.sharedpref.SharedPrefKeys

/**
 * Created by herry.park on 2020/7/7
 **/
class PasswordSettingPresenter : PasswordSettingContract.Presenter() {
    private val passwordNodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()

    override fun onAttach(view: PasswordSettingContract.View) {
        super.onAttach(view)

        view.root.beginTransition()
        NodeHelper.addNode(view.root, passwordNodes)
        view.root.endTransition()
    }

    override fun onLaunched(view: PasswordSettingContract.View) {
        // sets list items
        display()
    }

    override fun refresh() {
    }

    private fun display() {
        view?.getViewContext() ?: return

        displayPassword()
    }

    private fun displayPassword() {
        this.passwordNodes.beginTransition()

        val nodes: Node<NodeModelGroup> = NodeHelper.createNodeGroup()
        NodeHelper.addModel(nodes, PasswordSettingContract.PasswordModel(
            PreferenceUtil.get(SharedPrefKeys.PASSWORD, "")
        ))
        NodeHelper.upSert(this.passwordNodes, nodes)
        this.passwordNodes.endTransition()
    }
}