package com.stratagile.pnrouter.ui.activity.router

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import android.os.Bundle
import android.support.v4.view.LayoutInflaterCompat
import android.support.v4.view.LayoutInflaterFactory
import android.util.AttributeSet
import android.view.*
import android.widget.TextView
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.ui.activity.router.component.DaggerDiskConfigureComponent
import com.stratagile.pnrouter.ui.activity.router.contract.DiskConfigureContract
import com.stratagile.pnrouter.ui.activity.router.module.DiskConfigureModule
import com.stratagile.pnrouter.ui.activity.router.presenter.DiskConfigurePresenter
import kotlinx.android.synthetic.main.activity_disk_configure.*

import javax.inject.Inject;

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.router
 * @Description: $description
 * @date 2019/01/28 14:51:22
 */

class DiskConfigureActivity : BaseActivity(), DiskConfigureContract.View {

    @Inject
    internal lateinit var mPresenter: DiskConfigurePresenter
    var mode:String = "RAID1"

    override fun onCreate(savedInstanceState: Bundle?) {
        LayoutInflaterCompat.setFactory(LayoutInflater.from(this), LayoutInflaterFactory { parent, name, context, attrs ->
            if (name.equals("com.android.internal.view.menu.IconMenuItemView", ignoreCase = true) || name.equals("com.android.internal.view.menu.ActionMenuItemView", ignoreCase = true) || name.equals("android.support.v7.view.menu.ActionMenuItemView", ignoreCase = true)) {
                try {
                    val f = layoutInflater
                    val view = f.createView(name, null, attrs)
                    if (view is TextView) {
                        view.setTextColor(resources.getColor(R.color.mainColor))
                        view.isAllCaps = false
                    }
                    return@LayoutInflaterFactory view
                } catch (e: InflateException) {
                    e.printStackTrace()
                } catch (e: ClassNotFoundException) {
                    e.printStackTrace()
                }

            }
            null
        })
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        mode = "RAID1"
        setContentView(R.layout.activity_disk_configure)
        checkRaid1.visibility = View.VISIBLE
        checkBasic.visibility = View.INVISIBLE
        checkRaid0.visibility = View.INVISIBLE
        checkLvm.visibility = View.INVISIBLE
        checkAddToLvm.visibility = View.INVISIBLE
        checkAddToRaid1.visibility = View.INVISIBLE
        ivRaid1.setOnClickListener {
            if (detailRaid1.visibility == View.VISIBLE) {
                detailRaid1.visibility = View.GONE
                ivRaid1.setImageDrawable(resources.getDrawable(R.mipmap.arrow_down))
            } else {
                detailRaid1.visibility = View.VISIBLE
                ivRaid1.setImageDrawable(resources.getDrawable(R.mipmap.arrow_upper))
            }
        }
        ivBasic.setOnClickListener {
            if (detailBasic.visibility == View.VISIBLE) {
                detailBasic.visibility = View.GONE
                ivBasic.setImageDrawable(resources.getDrawable(R.mipmap.arrow_down))
            } else {
                detailBasic.visibility = View.VISIBLE
                ivBasic.setImageDrawable(resources.getDrawable(R.mipmap.arrow_upper))
            }
        }
        ivRaid0.setOnClickListener {
            if (detailRaid0.visibility == View.VISIBLE) {
                detailRaid0.visibility = View.GONE
                ivRaid0.setImageDrawable(resources.getDrawable(R.mipmap.arrow_down))
            } else {
                detailRaid0.visibility = View.VISIBLE
                ivRaid0.setImageDrawable(resources.getDrawable(R.mipmap.arrow_upper))
            }
        }
        ivLvm.setOnClickListener {
            if (detailLvm.visibility == View.VISIBLE) {
                detailLvm.visibility = View.GONE
                ivLvm.setImageDrawable(resources.getDrawable(R.mipmap.arrow_down))
            } else {
                detailLvm.visibility = View.VISIBLE
                ivLvm.setImageDrawable(resources.getDrawable(R.mipmap.arrow_upper))
            }
        }


        llRaid1.setOnClickListener {
            checkRaid1.visibility = View.VISIBLE
            checkBasic.visibility = View.INVISIBLE
            checkRaid0.visibility = View.INVISIBLE
            checkLvm.visibility = View.INVISIBLE
            checkAddToRaid1.visibility = View.INVISIBLE
            checkAddToLvm.visibility = View.INVISIBLE
            mode = "RAID1"
        }
        llBasic.setOnClickListener {
            checkRaid1.visibility = View.INVISIBLE
            checkBasic.visibility = View.VISIBLE
            checkRaid0.visibility = View.INVISIBLE
            checkLvm.visibility = View.INVISIBLE
            checkAddToRaid1.visibility = View.INVISIBLE
            checkAddToLvm.visibility = View.INVISIBLE
            mode = "BASIC"
        }
        llRaid0.setOnClickListener {
            checkRaid1.visibility = View.INVISIBLE
            checkBasic.visibility = View.INVISIBLE
            checkRaid0.visibility = View.VISIBLE
            checkLvm.visibility = View.INVISIBLE
            checkAddToRaid1.visibility = View.INVISIBLE
            checkAddToLvm.visibility = View.INVISIBLE
            mode = "RAID0"
        }
        llLvm.setOnClickListener {
            checkRaid1.visibility = View.INVISIBLE
            checkBasic.visibility = View.INVISIBLE
            checkRaid0.visibility = View.INVISIBLE
            checkLvm.visibility = View.VISIBLE
            checkAddToRaid1.visibility = View.INVISIBLE
            checkAddToLvm.visibility = View.INVISIBLE
            mode = ""
        }
        llAddToRaid1.setOnClickListener {
            checkRaid1.visibility = View.INVISIBLE
            checkBasic.visibility = View.INVISIBLE
            checkRaid0.visibility = View.INVISIBLE
            checkLvm.visibility = View.INVISIBLE
            checkAddToRaid1.visibility = View.VISIBLE
            checkAddToLvm.visibility = View.INVISIBLE
            mode = ""
        }
        llAddToLvm.setOnClickListener {
            checkRaid1.visibility = View.INVISIBLE
            checkBasic.visibility = View.INVISIBLE
            checkRaid0.visibility = View.INVISIBLE
            checkLvm.visibility = View.INVISIBLE
            checkAddToRaid1.visibility = View.INVISIBLE
            checkAddToLvm.visibility = View.VISIBLE
            mode = ""
        }
    }
    override fun initData() {
        title.text = "Configuration Disk"
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.confirm, menu)
        return true
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.confirm) {
            if(mode.equals("BASIC") || mode.equals("RAID0") || mode.equals("RAID1"))
            {
                val intent = Intent(this, DiskReconfigureActivity::class.java)
                intent.putExtra("Mode", mode)
                startActivityForResult(intent,1)
            }else{
                toast(getString(R.string.notsupported))
            }

        }
        return super.onOptionsItemSelected(item)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            var result = data!!.getStringExtra("result")
            if(result.equals("1"))
            {
                val intent = Intent()
                intent.putExtra("result", "1")
                setResult(RESULT_OK, intent)
                finish()
            }
        }
    }
    override fun setupActivityComponent() {
       DaggerDiskConfigureComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .diskConfigureModule(DiskConfigureModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: DiskConfigureContract.DiskConfigureContractPresenter) {
            mPresenter = presenter as DiskConfigurePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}