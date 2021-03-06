package com.stratagile.pnrouter.ui.activity.user

import android.content.Intent
import android.graphics.Bitmap
import android.os.Bundle
import android.os.Environment
import android.view.Menu
import android.view.MenuItem
import android.view.View
import cn.bingoogolapple.qrcode.core.BGAQRCodeUtil
import cn.bingoogolapple.qrcode.zxing.QRCodeEncoder
import com.pawegio.kandroid.longToast
import com.socks.library.KLog
import com.stratagile.pnrouter.R

import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.constant.UserDataManger
import com.stratagile.pnrouter.ui.activity.user.component.DaggerQRCodeComponent
import com.stratagile.pnrouter.ui.activity.user.component.DaggerQRFriendCodeComponent
import com.stratagile.pnrouter.ui.activity.user.contract.QRCodeContract
import com.stratagile.pnrouter.ui.activity.user.contract.QRFriendCodeContract
import com.stratagile.pnrouter.ui.activity.user.module.QRCodeModule
import com.stratagile.pnrouter.ui.activity.user.module.QRFriendCodeModule
import com.stratagile.pnrouter.ui.activity.user.presenter.QRCodePresenter
import com.stratagile.pnrouter.ui.activity.user.presenter.QRFriendCodePresenter
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CustomPopWindow
import kotlinx.android.synthetic.main.activity_qrcode.*
import java.io.File
import java.io.FileOutputStream

import javax.inject.Inject;
import kotlin.concurrent.thread

/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.user
 * @Description: $description
 * @date 2018/12/25 11:45:06
 */

class QRFriendCodeActivity : BaseActivity(), QRFriendCodeContract.View , View.OnClickListener{

    override fun onClick(v: View?) {
        when(v!!.id) {

        }
    }
    @Inject
    internal lateinit var mPresenter: QRFriendCodePresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_qrcode)
    }
    override fun initData() {
        var nickName = SpUtil.getString(this, ConstantValue.userFriendname, "")
        if ("".equals(nickName)) {
            title.text = getString(R.string.details)
        } else {
            title.text = nickName
        }
        var userId = SpUtil.getString(this, ConstantValue.userFriendId, "")
        tvShare.setOnClickListener {

            cardView.setDrawingCacheEnabled(true);
            cardView.buildDrawingCache();
            val bitmapPic = Bitmap.createBitmap(cardView.getDrawingCache())
            if(bitmapPic != null)
            {
                var dir = ConstantValue.localPath + "/RA/" + userId + ".jpg"
                var share_intent = Intent()
                share_intent.action = Intent.ACTION_SEND//设置分享行为
                share_intent.type = "image/*"  //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, ShareUtil.saveBitmap(this, bitmapPic,dir))
                share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "share")
                startActivity(share_intent)
            }
            //PopWindowUtil.showSharePopWindow(this, tvShare)

        }
        tvUserName.text = SpUtil.getString(this, ConstantValue.userFriendname, "")

        ivAvatar.setText(SpUtil.getString(this, ConstantValue.userFriendname, "")!!)
        //ivAvatar.setImageFile(SpUtil.getString(this, ConstantValue.userFriendname, "")!!)
        var fileBase58Name = Base58.encode( RxEncodeTool.base64Decode(UserDataManger.curreantfriendUserData.signPublicKey))+".jpg"
        ivAvatar.setImageFile(fileBase58Name)
        /*var CreateEnglishUserQRCode = ScanCodeTask(userId, ivQrCodeMy)
        CreateEnglishUserQRCode.execute()*/
        tvSaveToPhone.setOnClickListener {
            saveQrCodeToPhone()
        }
        Thread(Runnable() {
            run() {
                val selfNickNameBase64 = RxEncodeTool.base64Encode2String(nickName!!.toByteArray())
                var  bitmap: Bitmap =   QRCodeEncoder.syncEncodeQRCode("type_0,"+userId+","+selfNickNameBase64+","+ConstantValue.libsodiumpublicSignKey!!, BGAQRCodeUtil.dp2px(AppConfig.instance, 150f), AppConfig.instance.getResources().getColor(R.color.mainColor))
                runOnUiThread {
                    ivQrCodeMy.setImageBitmap(bitmap)
                }

            }
        }).start()
    }
    override fun onDestroy() {
        super.onDestroy()
        //CreateEnglishUserQRCode.cancel(true)
    }
    fun saveQrCodeToPhone() {
        showProgressDialog()
        thread {
            val dView = cardView
            dView.isDrawingCacheEnabled = true
            dView.buildDrawingCache()
            val bitmap = Bitmap.createBitmap(dView.drawingCache)
            if (bitmap != null) {
                try {
                    // 获取内置SD卡路径
                    val sdCardPath = Environment.getExternalStorageDirectory().getPath() + ConstantValue.localPath
                    // 图片文件路径
                    var userFriendname = SpUtil.getString(this, ConstantValue.userFriendname, "")
                    val filePath = sdCardPath + File.separator + userFriendname + ".png"
                    val file = File(filePath)
                    val os = FileOutputStream(file)
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, os)
                    os.flush()
                    os.close()
                    runOnUiThread {
                        closeProgressDialog()
                        longToast(getString(R.string.save_to_phone_success) + "\n" + filePath)
                    }
                    KLog.i("存储完成")
                } catch (e: Exception) {
                }

            }
        }
    }
    override fun setupActivityComponent() {
       DaggerQRFriendCodeComponent
               .builder()
               .appComponent((application as AppConfig).applicationComponent)
               .qRFriendCodeModule(QRFriendCodeModule(this))
               .build()
               .inject(this)
    }
    override fun setPresenter(presenter: QRFriendCodeContract.QRFriendCodeContractPresenter) {
            mPresenter = presenter as QRFriendCodePresenter
        }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.share) {
            cardView.setDrawingCacheEnabled(true);
            cardView.buildDrawingCache();
            var userId = FileUtil.getLocalUserData("userid")
            val bitmapPic = Bitmap.createBitmap(cardView.getDrawingCache())
            if(bitmapPic != null)
            {
                var dir = ConstantValue.localPath + "/RA/" + userId + ".jpg"
                var share_intent = Intent()
                share_intent.action = Intent.ACTION_SEND//设置分享行为
                share_intent.type = "image/*"  //设置分享内容的类型
                share_intent.putExtra(Intent.EXTRA_STREAM, ShareUtil.saveBitmap(this, bitmapPic,dir))
                share_intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
                //创建分享的Dialog
                share_intent = Intent.createChooser(share_intent, "share")
                startActivity(share_intent)
            }
            //PopWindowUtil.showSharePopWindow(this, tvShare)
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onBackPressed() {
        if (CustomPopWindow.onBackPressed()) {

        } else {
            super.onBackPressed()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.share_self, menu)
        return super.onCreateOptionsMenu(menu)
    }
}