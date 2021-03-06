package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.support.annotation.Nullable
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentPagerAdapter
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseFragment
import com.stratagile.pnrouter.ui.activity.main.component.DaggerFileComponent
import com.stratagile.pnrouter.ui.activity.main.contract.FileContract
import com.stratagile.pnrouter.ui.activity.main.module.FileModule
import com.stratagile.pnrouter.ui.activity.main.presenter.FilePresenter

import javax.inject.Inject;

import butterknife.ButterKnife;
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.ui.activity.conversation.FileListFragment
import com.stratagile.pnrouter.ui.activity.file.FileManagerActivity
import kotlinx.android.synthetic.main.fragment_file.*
import java.util.ArrayList

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 17:32:58
 */

class FileFragment : BaseFragment(), FileContract.View {

    @Inject
    lateinit internal var mPresenter: FilePresenter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        var view = inflater.inflate(R.layout.fragment_file, null);
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var titles = ArrayList<String>()
        titles.add("ALL Files")
       /* titles.add("Received Files")
        titles.add("Sent Files")*/
        viewPager.setAdapter(object : FragmentPagerAdapter(childFragmentManager) {
            override fun getItem(position: Int): Fragment {
                return FileListFragment()
            }

            override fun getCount(): Int {
                return titles.size
            }

            override fun getPageTitle(position: Int): CharSequence? {
                return titles.get(position)
            }
        })
        tabLayout.setupWithViewPager(viewPager)
        myFile.setOnClickListener {
            startActivity(Intent(activity!!, FileManagerActivity::class.java).putExtra("fileType", 0))
        }
        iShare.setOnClickListener {
            startActivity(Intent(activity!!, FileManagerActivity::class.java).putExtra("fileType", 1))
        }
        docReceived.setOnClickListener {
            startActivity(Intent(activity!!, FileManagerActivity::class.java).putExtra("fileType", 2))
        }
    }


    override fun setupFragmentComponent() {
        DaggerFileComponent
                .builder()
                .appComponent((activity!!.application as AppConfig).applicationComponent)
                .fileModule(FileModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: FileContract.FileContractPresenter) {
        mPresenter = presenter as FilePresenter
    }

    override fun initDataFromLocal() {

    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
}