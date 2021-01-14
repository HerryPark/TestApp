package com.herry.test.app.dialog

import android.content.Context
import android.graphics.Color
import android.os.Bundle
import android.view.*
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.herry.libs.helper.ToastHelper
import com.herry.libs.nodeview.NodeForm
import com.herry.libs.nodeview.NodeHolder
import com.herry.libs.nodeview.model.NodeRoot
import com.herry.libs.nodeview.recycler.NodeRecyclerAdapter
import com.herry.libs.nodeview.recycler.NodeRecyclerForm
import com.herry.libs.util.ViewUtil.getColorDrawable
import com.herry.test.R
import com.herry.test.app.base.nav.BaseNavView
import com.herry.test.widget.Popup
import com.herry.test.widget.TitleBarForm


/**
 * Created by herry.park on 2020/06/11.
 **/
class AppDialogListFragment : BaseNavView<AppDialogListContract.View, AppDialogListContract.Presenter>(), AppDialogListContract.View {

    override fun onCreatePresenter(): AppDialogListContract.Presenter = AppDialogListPresenter()

    override fun onCreatePresenterView(): AppDialogListContract.View = this

    override val root: NodeRoot
        get() = adapter.root

    private val adapter: Adapter = Adapter()

    private var container: View? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        if (null == this.container) {
            this.container = inflater.inflate(R.layout.app_dialog_list_fragment, container, false)
            init(this.container)
        }
        return this.container
    }

    private fun init(view: View?) {
        view ?: return

        TitleBarForm(
            activity = requireActivity()
        ).apply {
            bindFormHolder(view.context, view.findViewById(R.id.app_dialog_list_fragment_title))
            bindFormModel(view.context, TitleBarForm.Model(title = "Test List"))
        }

        view.findViewById<RecyclerView>(R.id.app_dialog_list_fragment_list)?.apply {
            layoutManager = LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            setHasFixedSize(true)
            if (itemAnimator is SimpleItemAnimator) {
                (itemAnimator as SimpleItemAnimator).supportsChangeAnimations = false
            }
            adapter = this@AppDialogListFragment.adapter
        }
    }

    inner class Adapter: NodeRecyclerAdapter(::requireContext) {
        override fun onBindForms(list: MutableList<NodeForm<out NodeHolder, *>>) {
            list.add(TestItemForm())
        }
    }

    override fun onScreen(type: AppDialogListContract.TestItemType) {
        when (type) {
            AppDialogListContract.TestItemType.TITLE_MESSAGE_BUTTON_1 -> {
                Popup(requireActivity()).apply {
                    setTitle("title")
                    setMessage("message")
                    setPositiveButton("ok") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setNegativeButton("cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setNeutralButton("center") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
            AppDialogListContract.TestItemType.TITLE_MESSAGE_BUTTON_2 -> {
                Popup(requireActivity()).apply {
                    setTitle("title")
                    setMessage("message")
                    setNegativeButton("button1") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("button3") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
            AppDialogListContract.TestItemType.TITLE_MESSAGE_BUTTON_3 -> {
                Popup(requireActivity()).apply {
                    setTitle("title")
                    setMessage("message")
                    setNegativeButton("button1") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setNeutralButton("button2") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("button3") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
            AppDialogListContract.TestItemType.TITLE_LIST_BUTTON_2 -> {
                Popup(requireActivity()).apply {
                    setTitle("title")
                    setSingleChoiceItems(arrayOf("1", "2"), 0) { _, witch ->
                        ToastHelper.showToast(requireActivity(), "$witch")
                    }
                    setNegativeButton("button1") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("button3") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
            AppDialogListContract.TestItemType.MESSAGE_BUTTON_3 -> {
                Popup(requireActivity()).apply {
                    setMessage("message")
                    setNegativeButton("button1") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setNeutralButton("button2") { dialog, _ ->
                        dialog.dismiss()
                    }
                    setPositiveButton("button3") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
            AppDialogListContract.TestItemType.TITLE_VIEW -> {
                Popup(requireActivity()).apply {
                    setTitle("title")
                    setView(TextView(requireContext()).apply {
                        minimumHeight = 100
                        text = "set view"
                        setBackgroundColor(Color.argb(0xff, 0xff, 0, 0))
                    })
                }.show()
            }
            AppDialogListContract.TestItemType.VIEW -> {
                Popup(requireActivity()).apply {
                    setView(TextView(requireContext()).apply {
                        minimumHeight = 100
                        text = "set view"
                        setBackgroundColor(Color.argb(0xff, 0xff, 0, 0))
                    })
                }.show()
            }
            AppDialogListContract.TestItemType.VIEW_BUTTON_1 -> {
                Popup(requireActivity()).apply {
                    setView(TextView(requireContext()).apply {
                        minimumHeight = 100
                        text = "set view"
                        setBackgroundColor(Color.argb(0xff, 0xff, 0, 0))
                    })
                    setPositiveButton("button3") { dialog, _ ->
                        dialog.dismiss()
                    }
                }.show()
            }
            AppDialogListContract.TestItemType.CUSTOM_VIEW -> {
                Popup(requireActivity()).apply {
                    setCustomView(TextView(requireContext()).apply {
                        minimumHeight = 400
                        text = "custom view"
                        setBackgroundColor(Color.argb(0xff, 0xff, 0xff, 0))
                    })
                }.show()

            }
            AppDialogListContract.TestItemType.RESIZE_DIALOG -> {
                Popup(requireActivity()).apply {
                    setCustomView(TextView(requireContext()).apply {
                        gravity = Gravity.CENTER
                        text = "full size custom view"
                        setBackgroundColor(Color.argb(0xff, 0xff, 0xff, 0))
                    })
                    setBackgroundDrawable(getColorDrawable(context, android.R.color.transparent))
                    setDialogSize(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT, Gravity.TOP or Gravity.CENTER_HORIZONTAL)
                }.show()
            }
        }
    }

    private inner class TestItemForm : NodeForm<TestItemForm.Holder, AppDialogListContract.TestItemType>(Holder::class, AppDialogListContract.TestItemType::class) {
        inner class Holder(context: Context, view: View) : NodeHolder(context, view) {
            val title: TextView? = view.findViewById(R.id.main_test_item_title)
            init {
                view.setOnClickListener {
                    NodeRecyclerForm.getBindModel(this@TestItemForm, this@Holder)?.let {
                        presenter?.moveToScreen(it)
                    }
                }
            }
        }

        override fun onCreateHolder(context: Context, view: View): Holder = Holder(context, view)

        override fun onLayout(): Int = R.layout.main_test_item

        override fun onBindModel(context: Context, holder: TestItemForm.Holder, model: AppDialogListContract.TestItemType) {
            holder.title?.text = when (model) {
                AppDialogListContract.TestItemType.TITLE_MESSAGE_BUTTON_1 -> "TITLE_MESSAGE_BUTTON_1"
                AppDialogListContract.TestItemType.TITLE_MESSAGE_BUTTON_2 -> "TITLE_MESSAGE_BUTTON_2"
                AppDialogListContract.TestItemType.TITLE_MESSAGE_BUTTON_3 -> "TITLE_MESSAGE_BUTTON_3"
                AppDialogListContract.TestItemType.TITLE_LIST_BUTTON_2 -> "TITLE_LIST_BUTTON_2"
                AppDialogListContract.TestItemType.MESSAGE_BUTTON_3 -> "MESSAGE_BUTTON_3"
                AppDialogListContract.TestItemType.TITLE_VIEW -> "TITLE_VIEW"
                AppDialogListContract.TestItemType.VIEW -> "VIEW"
                AppDialogListContract.TestItemType.VIEW_BUTTON_1 -> "VIEW_BUTTON_1"
                AppDialogListContract.TestItemType.CUSTOM_VIEW -> "CUSTOM_VIEW"
                AppDialogListContract.TestItemType.RESIZE_DIALOG -> "RESIZE_DIALOG"
            }
        }
    }
}