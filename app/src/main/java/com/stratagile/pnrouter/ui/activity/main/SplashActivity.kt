package com.stratagile.pnrouter.ui.activity.main

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.util.Log
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerSplashComponent
import com.stratagile.pnrouter.ui.activity.main.contract.SplashContract
import com.stratagile.pnrouter.ui.activity.main.module.SplashModule
import com.stratagile.pnrouter.ui.activity.main.presenter.SplashPresenter
import com.stratagile.pnrouter.utils.*
import javax.inject.Inject
import org.libsodium.jni.NaCl
import org.libsodium.jni.Sodium
import java.util.*

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/10 22:25:34
 */

class SplashActivity : BaseActivity(), SplashContract.View {
    private var handler: Handler? = null
    private var countDownTimerUtils: CountDownTimerUtils? = null
    override fun loginSuccees() {
        MobileSocketClient.getInstance().destroy()
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    override fun jumpToLogin() {
        MobileSocketClient.getInstance().destroy()
        startActivity(Intent(this, LoginActivityActivity::class.java))
        finish()
    }

    override fun jumpToGuest() {
        MobileSocketClient.getInstance().destroy()
        startActivity(Intent(this, GuestActivity::class.java))
        finish()
    }
    override fun exitApp() {
        finish()
        System.exit(0)
    }

    @Inject
    internal lateinit var mPresenter: SplashPresenter

    override fun onCreate(savedInstanceState: Bundle?) {
        var sodium: Sodium = NaCl.sodium()
        needFront = true
        AppConfig.instance.stopAllService()
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_splash)
    }
    override fun initData() {
        LogUtil.addLog("app version :"+BuildConfig.VERSION_NAME)
        var this_ = this
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MyAuthCallback.MSG_UPD_DATA -> {
                        var obj:String = msg.obj.toString()
                        if(!obj.equals(""))
                        {
                            var objArray = obj.split("##")
                            var index = 0;
                            for(item in objArray)
                            {
                                if(!item.equals(""))
                                {
                                    var udpData = AESCipher.aesDecryptString(objArray[index],"slph\$%*&^@-78231")
                                    var udpRouterArray = udpData.split(";")

                                    if(udpRouterArray.size > 1)
                                    {
                                        println("ipdizhi:"+udpRouterArray[1] +" ip: "+udpRouterArray[0])
                                        //ConstantValue.updRouterData.put(udpRouterArray[1],udpRouterArray[0])
                                        if( ConstantValue.currentRouterId.equals(udpRouterArray[1]))
                                        {
                                            ConstantValue.currentRouterIp = udpRouterArray[0]
                                            ConstantValue.port= ":18006"
                                            ConstantValue.filePort = ":18007"
                                            break;
                                        }

                                    }
                                }
                                index ++
                            }
                            if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                            {
                                ConstantValue.curreantNetworkType = "WIFI"
                            }
                        }

                    }
                }
            }
        }
        MobileSocketClient.getInstance().init(handler,this)
        mPresenter.getPermission()
       /* var aesKey = "0F578ED5897A958A"

        LogUtil.addLog("sendMsg aesKey:",aesKey)
        var my = RxEncodeTool.base64Decode("MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDuGX8sjCbr2W62ygQNIanARcsqo8tUwK3AXuIeRUGtkLVJ+1BhH19ibn0MF8SrIjh2+4ndMD54gszCMdNtMyb93fKJZ2xsdHNiE71vi5Ms1UPYFIC4oMSEfq8qhefMwCgIJZpLmTaDHjLyETfjZ0RmnvVXIIiieUC7vNfnGLz4zQIDAQAB")
        LogUtil.addLog("sendMsg myKey:","MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDuGX8sjCbr2W62ygQNIanARcsqo8tUwK3AXuIeRUGtkLVJ+1BhH19ibn0MF8SrIjh2+4ndMD54gszCMdNtMyb93fKJZ2xsdHNiE71vi5Ms1UPYFIC4oMSEfq8qhefMwCgIJZpLmTaDHjLyETfjZ0RmnvVXIIiieUC7vNfnGLz4zQIDAQAB")
        var friend = RxEncodeTool.base64Decode("nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6fLXHHG4HCmmXnrN6IjZJ2oRlnd7zfdFEfNZtCvuDWTt9ozDRJMHuxPwRbQFWrNmK9lP4wr8AxeGjh4cpSFvxiXnA3n0ea9yvrQe/ItbKIHcLjIUHUPi2DHoONpi4x3nbL+VrtEIZyyuiHKqaz3mc5wEKFKnU9yi88K1ecpmqUL5bQIDAQABn")
        LogUtil.addLog("sendMsg friendKey:","nMIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQC6fLXHHG4HCmmXnrN6IjZJ2oRlnd7zfdFEfNZtCvuDWTt9ozDRJMHuxPwRbQFWrNmK9lP4wr8AxeGjh4cpSFvxiXnA3n0ea9yvrQe/ItbKIHcLjIUHUPi2DHoONpi4x3nbL+VrtEIZyyuiHKqaz3mc5wEKFKnU9yi88K1ecpmqUL5bQIDAQABn")
        var SrcKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),my))
        LogUtil.addLog("sendMsg SrcKey:",SrcKey.toString())
        var aa = RxEncryptTool.encryptByPublicKey(aesKey.toByteArray(),friend)
        var DstKey = RxEncodeTool.base64Encode(aa)
        LogUtil.addLog("sendMsg DstKey:",SrcKey.toString())*/
        //mPresenter.getLastVersion()



        var dst_public_SignKey = ByteArray(32)
        var dst_private_Signkey = ByteArray(32)
        var crypto_box_keypair_result = Sodium.crypto_sign_keypair(dst_public_SignKey,dst_private_Signkey)


        var dst_public_MiKey = ByteArray(32)
        var dst_private_Mikey = ByteArray(32)
        var crypto_sign_ed25519_pk_to_curve25519_result = Sodium.crypto_sign_ed25519_pk_to_curve25519(dst_public_MiKey,dst_public_SignKey)

        var crypto_sign_ed25519_sk_to_curve25519_result = Sodium.crypto_sign_ed25519_sk_to_curve25519(dst_private_Mikey,dst_private_Signkey)

        var dst_public_KeyStr =  StringUitl.bytesToString(dst_public_SignKey)
        var dst_private_keyStr =  StringUitl.bytesToString(dst_private_Signkey)
        var src_seedbefore = "123456".toByteArray()
        var src_seed = ByteArray(32)
        Arrays.fill(src_seed,0)
        //System.arraycopy(src_seedbefore, 0, src_seed, 0, src_seedbefore.size)
        var dst_public_Key2 = ByteArray(32)
        var dst_private_key2= ByteArray(64)
        var crypto_sign_seed_keypair = Sodium.crypto_box_seed_keypair(dst_public_Key2,dst_private_key2,src_seed)

        var aaabb =  StringUitl.bytesToString(dst_public_Key2)
        var ccdd =  StringUitl.bytesToString(dst_private_key2)
        Log.i("dst_public_Key2",aaabb.toString())
        Log.i("dst_private_key2",ccdd.toString())
        var  aabb = RxEncodeTool.base64Encode2String(dst_public_SignKey)
        var dst_public_KeyFriendKuang = StringUitl.toBytes("39 98 b7 2 1 45 6d a8 bc 24 fd 8d 90 4d e 71 ba 8e 41 37 dd 9e 89 38 35 8c 3f 3 96 cd 87 1b")
        var dst_public_KeyFriend = ByteArray(32)
        var dst_private_key_KeyFriend = ByteArray(64)
        var crypto_box_keypair_resultFriend = Sodium.crypto_box_keypair(dst_public_KeyFriend,dst_private_key_KeyFriend)
        var dst_shared_key  = ByteArray(32)
        var crypto_box_beforenm_result = Sodium.crypto_box_beforenm(dst_shared_key,dst_public_KeyFriendKuang,dst_private_Signkey)
        var src_msg = "123456聚隆科技构建我国借我个偶就给我个饿哦go额外".toByteArray()

        val random = org.libsodium.jni.crypto.Random()
        var src_nonce =  random.randomBytes(24)

        var encrypted = LibsodiumUtil.encrypt_data_symmetric(src_msg,src_nonce,dst_shared_key)


        var dst_shared_keyOPEN  = ByteArray(32)
        var dst_private_keyOPEN =  StringUitl.toBytes("51 5f 70 d2 aa d4 11 e2 b8 3b 5d 45 be 83 d8 f0 42 b0 47 67 79 37 ca 5d 89 7b 3b 5c 73 8f 21 34")
        var crypto_box_beforenm_resultOPEN = Sodium.crypto_box_beforenm(dst_shared_keyOPEN,dst_public_SignKey,dst_private_keyOPEN)
        var souceStr  = LibsodiumUtil.decrypt_data_symmetric(encrypted,src_nonce,dst_shared_keyOPEN)

        var dst_shared_key2  = ByteArray(32)
        var crypto_box_beforenm_result2 = Sodium.crypto_box_beforenm(dst_shared_key2,ConstantValue.libsodiumpublicSignKey!!.toByteArray(),dst_private_Signkey)

        var encryptedStr = LibsodiumUtil.encrypt_data_symmetric_string("123456聚隆科技构建我国借我个偶就给我个饿哦go额外", String(src_nonce),StringUitl.bytes2HexString(dst_shared_key2))
        var souceStr2  = LibsodiumUtil.decrypt_data_symmetric_string(encryptedStr,String(src_nonce),StringUitl.bytes2HexString(dst_shared_key2))
        mPresenter.observeJump()
    }

    override fun setupActivityComponent() {
        DaggerSplashComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .splashModule(SplashModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: SplashContract.SplashContractPresenter) {
        mPresenter = presenter as SplashPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}