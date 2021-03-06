@file:Suppress("unused")

package com.herry.libs.widget.extension

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.IdRes
import androidx.annotation.NavigationRes
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.setFragmentResultListener
import androidx.navigation.NavDestination
import androidx.navigation.NavDirections
import androidx.navigation.NavOptions
import androidx.navigation.Navigator
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.herry.libs.app.nav.NavBundleUtil

fun Fragment.getNavCurrentDestinationID(): Int = findNavController().currentDestination?.id ?: 0

fun Fragment.addNestedNavHostFragment(@IdRes containerViewId: Int, navHostFragment: NavHostFragment?, tag: String? = null, listener: ((requestKey: String, bundle: Bundle) -> Unit)? = null): Boolean {
    navHostFragment ?: return false
    childFragmentManager.beginTransaction()
        .replace(containerViewId, navHostFragment, tag)
        .setPrimaryNavigationFragment(navHostFragment) // this is the equivalent to app:defaultNavHost="true"
        .commit()

    if (listener != null) {
        childFragmentManager.setFragmentResultListener(navHostFragment.id.toString(), this, listener)
    }

    return true
}

fun Fragment.addNestedNavHostFragment(
    @IdRes containerViewId: Int,
    @NavigationRes graphResId: Int,
    startDestinationArgs: Bundle? = null,
    tag: String? = null,
    listener: ((requestKey: String, bundle: Bundle) -> Unit)? = null
): NavHostFragment {
    val navHostFragment = NavHostFragment.create(graphResId, startDestinationArgs)

    addNestedNavHostFragment(containerViewId, navHostFragment, tag, listener)

    return navHostFragment
}

fun NavHostFragment.setFragmentNotifyListener(listener: ((requestKey: String, bundle: Bundle) -> Unit)) {
    childFragmentManager.setFragmentResultListener(id.toString(), this, listener)
}

fun Fragment.findParentNavHostFragment(): NavHostFragment? {
    var parentFragment: Fragment? = this.parentFragment
    while (parentFragment != null) {
        if (parentFragment is NavHostFragment) {
            return parentFragment
        }

        parentFragment = parentFragment.parentFragment
    }

    return null
}

fun Fragment.findNestedNavHostFragment(@IdRes id: Int): NavHostFragment? {
    return childFragmentManager.findFragmentById(id) as? NavHostFragment
}

fun Fragment.isNestedNavHostFragment(): Boolean {
    if (this is NavHostFragment) {
        return this.findParentNavHostFragment() != null
    }

    return false
}

fun NavHostFragment.getCurrentFragment(): Fragment? {
    return childFragmentManager.primaryNavigationFragment
}

/**
 * Pop child fragment until destinationId
 */
fun Fragment.popTo(@IdRes destinationId: Int, bundle: Bundle) {
    findNavController().run {
        popBackStack(destinationId, true)
        notifyTo(destinationId, bundle)
    }
}

/**
 * Pop all child fragment and set to the NavHostFragment
 */
fun Fragment.popToNavHost(bundle: Bundle? = null) {
    val navHostFragment = if (this is NavHostFragment) {
        this
    } else {
        this.findParentNavHostFragment()
    } ?: return

    val destinationId = navHostFragment.findNavController().graph.startDestination

    findNavController().run {
        popBackStack(destinationId, false)
        notifyTo(destinationId, bundle ?: NavBundleUtil.createNavigationBundle(false))
    }
}

private fun Fragment.notifyTo(@IdRes destinationId: Int, bundle: Bundle) {
    setFragmentResult(destinationId.toString(), bundle)
}

/**
 * Sends data to NavHostFragment of this fragment
 */
fun Fragment.notifyToNavHost(bundle: Bundle) {
    val navHostFragment = this.findParentNavHostFragment()

    if (navHostFragment?.isCurrentStartDestinationFragment() == true) {
        notifyToParentNavHost(bundle)
    } else {
        Log.d("Herry", "notifyToNavHost id = ${navHostFragment?.id}")
        navHostFragment?.childFragmentManager?.setFragmentResult(
            navHostFragment.id.toString(), bundle
        )
    }
}

/**
 * Sends data to ParentNavHostFragment of the NavHostFragment of this fragment
 */
fun Fragment.notifyToParentNavHost(bundle: Bundle) {
    val navHostFragment = this.findParentNavHostFragment()

    navHostFragment?.parentFragment?.childFragmentManager?.setFragmentResult(
        navHostFragment.id.toString(), bundle
    )
}

/**
 * Sends data to current fragment from NavHostFragment
 */
fun NavHostFragment.notifyToCurrent(bundle: Bundle) {
    val currentDestinationId = findNavController().currentBackStackEntry?.destination?.id
    if (currentDestinationId != null) {
        notifyTo(currentDestinationId, bundle)
    }
}

/**
 * Changes screen to
 */
fun Fragment.navigate(@IdRes resId: Int, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(resId, null, onResult)
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle? = null, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(resId, args, null, onResult)
}

fun Fragment.navigate(@IdRes resId: Int, args: Bundle? = null, navOptions: NavOptions? = null, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(resId, args, navOptions, null, onResult)
}

/**
 * Navigate via the given [NavDirections]
 *
 * @param directions directions that describe this navigation operation
 */
fun Fragment.navigate(directions: NavDirections, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(directions.actionId, directions.arguments, onResult)
}

/**
 * Navigate via the given [NavDirections]
 *
 * @param directions directions that describe this navigation operation
 * @param navOptions special options for this navigation operation
 */
fun Fragment.navigate(directions: NavDirections, navOptions: NavOptions?, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(directions.actionId, directions.arguments, navOptions, onResult)
}

/**
 * Navigate via the given [NavDirections]
 *
 * @param directions directions that describe this navigation operation
 * @param navigatorExtras extras to pass to the [Navigator]
 */
fun Fragment.navigate(directions: NavDirections, navigatorExtras: Navigator.Extras, onResult: ((bundle: Bundle) -> Unit)? = null) {
    navigate(directions.actionId, directions.arguments, null, navigatorExtras, onResult)
}

/**
 * Navigate to a destination from the current navigation graph. This supports both navigating
 * via an {@link NavDestination#getAction(int) action} and directly navigating to a destination.
 *
 * @param resId an {@link NavDestination#getAction(int) action} id or a destination id to
 *              navigate to
 * @param args arguments to pass to the destination
 * @param navOptions special options for this navigation operation
 * @param navigatorExtras extras to pass to the Navigator
 */
fun Fragment.navigate(@IdRes resId: Int, args: Bundle?, navOptions: NavOptions?, navigatorExtras: Navigator.Extras?, onResult: ((bundle: Bundle) -> Unit)? = null) {
    findNavController().navigate(resId, args, navOptions, navigatorExtras)

    if (onResult != null) {
        val backStack = findNavController().currentBackStackEntry ?: return
        val navDestination: NavDestination = backStack.destination
        @IdRes val destId: Int = navDestination.id
        if (destId == 0) return

        val navHostFragment = this.findParentNavHostFragment()
        if (navHostFragment != null) {
            this.setFragmentResultListener(destId.toString()) { _: String, bundle: Bundle ->
                onResult(bundle)
            }
        }
    }
}

fun NavHostFragment.getFragmentByViewID(): Fragment? {
    return childFragmentManager.findFragmentById(this.id)
}

fun NavHostFragment.isCurrentStartDestinationFragment(): Boolean {
    val currentFragmentDestinationId = findNavController().currentBackStackEntry?.destination?.id ?: 0
    val parentStartFragmentDestinationId = findNavController().graph.startDestination

    return currentFragmentDestinationId != 0 && currentFragmentDestinationId == parentStartFragmentDestinationId
}

fun Fragment.isParentViewVisible() : Boolean {
    return isParentViewVisible(view?.parent as? View)
}

private fun isParentViewVisible(parentView: View?) : Boolean {
    parentView ?: return true
    if (parentView.isVisible) {
        return isParentViewVisible(parentView.parent as? View)
    }

    return false
}

fun Fragment.getNavHostFragment(): NavHostFragment? {
    val navHostFragment = this.findParentNavHostFragment()

    return if (navHostFragment?.isCurrentStartDestinationFragment() == true) {
        navHostFragment
    } else {
        null
    }
}

fun NavHostFragment.getBackEntryCounts(): Int = this.childFragmentManager.backStackEntryCount