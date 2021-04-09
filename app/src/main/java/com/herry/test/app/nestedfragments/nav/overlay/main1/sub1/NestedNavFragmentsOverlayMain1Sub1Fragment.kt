package com.herry.test.app.nestedfragments.nav.overlay.main1.sub1

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.ViewModelProvider
import com.herry.libs.util.AppUtil
import com.herry.libs.util.BundleUtil
import com.herry.libs.widget.extension.popToNavHost
import com.herry.test.app.base.nestednav.BaseNestedNavFragment
import com.herry.test.databinding.NestedNavFragmentsOverlayMain1Sub1FragmentBinding

class NestedNavFragmentsOverlayMain1Sub1Fragment : BaseNestedNavFragment() {
    private var _binding: NestedNavFragmentsOverlayMain1Sub1FragmentBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    private lateinit var viewModel: NestedNavFragmentsOverlayMain1Sub1ViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewModel = ViewModelProvider(this).get(NestedNavFragmentsOverlayMain1Sub1ViewModel::class.java)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (_binding == null) {
            _binding = NestedNavFragmentsOverlayMain1Sub1FragmentBinding.inflate(inflater, container, false)

            binding.nestedNavFragmentsOverlayMain1Sub1FragmentClose.setOnClickListener { view ->
                popToNavHost(BundleUtil.createNavigationBundle(false))
                AppUtil.pressBackKey(requireActivity())
            }
        }

        return binding.root
    }
}