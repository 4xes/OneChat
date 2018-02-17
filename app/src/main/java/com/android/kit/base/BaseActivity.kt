package com.android.kit.base

import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.AppCompatActivity
import android.view.MenuItem
import com.eor.onechat.R
import io.reactivex.debounceTakeFirst
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.subjects.PublishSubject
import java.util.concurrent.TimeUnit
import io.reactivex.Action


abstract class BaseActivity : AppCompatActivity() {
    private val subjectAction = PublishSubject.create<(Action)>()
    private var compositeDisposable: CompositeDisposable = CompositeDisposable()

    fun add(fragment: Fragment) {
        supportFragmentManager.beginTransaction().add(FRAME_ID, fragment).commit()
    }

    fun replace(fragment: Fragment) {
        supportFragmentManager.beginTransaction().replace(FRAME_ID, fragment).commit()
    }

    fun addBackStack(fragment: Fragment, name: String) {
        supportFragmentManager.beginTransaction().add(FRAME_ID, fragment, name).addToBackStack(name).commit()
    }

    fun currentFragment(): BaseFragment? = supportFragmentManager.findFragmentById(FRAME_ID) as BaseFragment

    fun resolveToolbar(fragment: BaseFragment) {
        if (fragment.getToolbar() != null) {
            setSupportActionBar(fragment.getToolbar())
        }

        val actionBar = supportActionBar
        if (actionBar != null) {
            if (fragment.isBack()) {
                actionBar.setDisplayHomeAsUpEnabled(true)
                actionBar.setDisplayUseLogoEnabled(false)
            }
            if (fragment.getTitle() != null) {
                actionBar.title = fragment.getTitle()
            }
        }
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        subjectAction.debounceTakeFirst(300L, TimeUnit.MILLISECONDS).subscribe({
            it.invoke()
        }, {
            it.printStackTrace()
        })
    }

    fun debounceAction(action: Action) {
        subjectAction.onNext(action)
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }

    override fun onOptionsItemSelected(item: MenuItem) = when (item.itemId) {
        android.R.id.home -> {
            onBackPressed()
            true
        }
        else -> super.onOptionsItemSelected(item)
    }

    companion object {
        const val FRAME_ID = R.id.frame
    }
}