package com.android.kit.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.Toolbar
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.eor.onechat.R
import editor.video.motion.fast.slow.core.annotations.Back
import editor.video.motion.fast.slow.core.annotations.Layout
import editor.video.motion.fast.slow.core.annotations.Title
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable

open class BaseFragment : Fragment() {

    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    override fun onDestroyView() {
        super.onDestroyView()
        compositeDisposable.clear()
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(getLayoutId(), container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        (activity as BaseActivity).resolveToolbar(this)
    }

    fun getToolbar(): Toolbar? {
        return view?.findViewById(R.id.toolbar)
    }

    fun subscribe(disposable: Disposable) {
        compositeDisposable.add(disposable)
    }

    open fun getLayoutId(): Int {
        val layoutAnnotation = this.javaClass.annotations.find { it.annotationClass == Layout::class } as? Layout ?:
                throw IllegalStateException("Please override getLayoutId() or set @Layout annotation to Fragment")
        return layoutAnnotation.layout
    }

    open fun getTitle(): String? {
        val titleAnnotation = javaClass.annotations.find { it.annotationClass == Title::class } as? Title ?: return null
        return getString(titleAnnotation.title)
    }

    open fun isBack(): Boolean {
        return javaClass.isAnnotationPresent(Back::class.java)
    }
}