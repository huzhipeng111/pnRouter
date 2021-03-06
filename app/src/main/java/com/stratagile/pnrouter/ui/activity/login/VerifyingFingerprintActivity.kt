package com.stratagile.pnrouter.ui.activity.login

import android.animation.ObjectAnimator
import android.app.Activity
import android.app.KeyguardManager
import android.content.Context
import android.content.Intent
import android.hardware.fingerprint.FingerprintManager
import android.os.*
import android.provider.Settings
import android.support.v7.app.AlertDialog
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.BuildConfig
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.fingerprint.CryptoObjectHelper
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.login.component.DaggerVerifyingFingerprintComponent
import com.stratagile.pnrouter.ui.activity.login.contract.VerifyingFingerprintContract
import com.stratagile.pnrouter.ui.activity.login.module.VerifyingFingerprintModule
import com.stratagile.pnrouter.ui.activity.login.presenter.VerifyingFingerprintPresenter
import com.tencent.bugly.crashreport.CrashReport
import kotlinx.android.synthetic.main.activity_fingerprint.*
import javax.inject.Inject
import android.support.v4.view.ViewCompat.getTranslationY
import com.google.gson.Gson
import com.socks.library.KLog
import com.stratagile.pnrouter.entity.CryptoBoxKeypair
import com.stratagile.pnrouter.entity.HttpData
import com.stratagile.pnrouter.entity.events.BackgroudEvent
import com.stratagile.pnrouter.ui.activity.main.LogActivity
import com.stratagile.pnrouter.utils.*
import com.stratagile.pnrouter.view.CommonDialog
import com.stratagile.pnrouter.view.SweetAlertDialog
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import org.libsodium.jni.Sodium
import java.util.ArrayList


/**
 * @author zl
 * @Package com.stratagile.pnrouter.ui.activity.login
 * @Description: $description
 * @date 2019/02/26 14:40:52
 */

class VerifyingFingerprintActivity : BaseActivity(), VerifyingFingerprintContract.View {

    @Inject
    internal lateinit var mPresenter: VerifyingFingerprintPresenter
    private var myAuthCallback: MyAuthCallback? = null
    private var cancellationSignal: CancellationSignal? = null
    private var handler: Handler? = null
    private var builderTips: AlertDialog? = null
    internal var finger: ImageView? = null
    var formatDialog: CommonDialog? = null

    val AuthenticationScreen = 3
    val SettingPassWord = 4

    var isLock = true

    var fingerprintManager: FingerprintManager? = null

    var hasFinger = false

    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        ConstantValue.isShowVerify = true
        super.onCreate(savedInstanceState)
    }

    override fun initView() {
        setContentView(R.layout.activity_fingerprint)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            window.decorView.systemUiVisibility = View.SYSTEM_UI_FLAG_VISIBLE//设置状态栏黑色字体
        }
        showViewNeedFront()
        var count = 0
        iv0.setOnClickListener {
            count++
        }
        funTv0.setOnClickListener {
            if (count == 2) {
                startActivity(Intent(this, LogActivity::class.java))
                count = 0
            } else {
                count = 0
            }
        }
    }

    override fun initData() {
        EventBus.getDefault().register(this)
        handler = object : Handler() {
            override fun handleMessage(msg: Message) {
                super.handleMessage(msg)
                when (msg.what) {
                    MyAuthCallback.MSG_AUTH_SUCCESS -> {
                        setResultInfo(R.string.fingerprint_success)
                        SpUtil.putBoolean(this@VerifyingFingerprintActivity, ConstantValue.isUnLock, true)
                        KLog.i("指纹验证成功")
                        formatDialog?.dismissWithAnimation()
                        hideFingerAnimationWithUnLock()
                        isLock = false
                        cancellationSignal = null
                        llNext.postDelayed({
                            finish()
                            overridePendingTransition(0, R.anim.activity_translate_out_1)
                        }, 700)
                    }
                    MyAuthCallback.MSG_AUTH_FAILED -> {
                        setResultInfo(R.string.fingerprint_not_recognized)
                        cancellationSignal = null
                    }
                    MyAuthCallback.MSG_AUTH_ERROR -> handleErrorCode(msg.arg1)
                    MyAuthCallback.MSG_AUTH_HELP -> handleHelpCode(msg.arg1)
                }
            }
        }
        llNext.setOnClickListener {
            if (isLock) {
                showDialog()
            }
        }
        KLog.i("进入验证页面")
        showDialog()
    }

    fun showFingerAnimation() {
        LogUtil.addLog("验证指纹..")
        KLog.i("验证指纹..")
        val curTranslationY = llLogo.getTranslationY()
        val animator = ObjectAnimator.ofFloat(llLogo, "translationY", curTranslationY, curTranslationY - resources.getDimension(R.dimen.x300))
        animator.setDuration(600)
        animator.start()
        if (hasFinger) {
            val curNexY = llNext.translationY
            var nextAnimator = ObjectAnimator.ofFloat(llNext, "translationY", curNexY, curNexY + resources.getDimension(R.dimen.x300))
            nextAnimator.setDuration(600)
            nextAnimator.start()
        }
    }

    fun hideFingerAnimationWithUnLock() {
        val curTranslationY = llLogo.getTranslationY()
        val animator = ObjectAnimator.ofFloat(llLogo, "translationY", curTranslationY, curTranslationY + resources.getDimension(R.dimen.x300))
        animator.setDuration(600)
        animator.start()
    }

    fun hideFingerAnimation() {
        val curTranslationY = llLogo.getTranslationY()
        val animator = ObjectAnimator.ofFloat(llLogo, "translationY", curTranslationY, curTranslationY + resources.getDimension(R.dimen.x300))
        animator.setDuration(600)
        animator.start()
        if (hasFinger) {
            val curNexY = llNext.translationY
            var nextAnimator = ObjectAnimator.ofFloat(llNext, "translationY", curNexY, curNexY - resources.getDimension(R.dimen.x300))
            nextAnimator.setDuration(600)
            nextAnimator.start()
        }
    }

    override fun onPause() {
        if (hasFinger) {
            finish()
        }
        super.onPause()
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBackGroud(backgroudEvent: BackgroudEvent) {
//        KLog.i("收到app到后台的消息")
//        finish()
    }

    override fun onStop() {
        super.onStop()
    }

    private fun showDialog() {
        if(BuildConfig.DEBUG)
        {
            return
        }
        if (!ConstantValue.loginOut && Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            // init fingerprint.
            try {
                if (fingerprintManager == null) {
                    fingerprintManager = AppConfig.instance.getSystemService(Context.FINGERPRINT_SERVICE) as FingerprintManager
                }
                /*if(!SpUtil.getString(this, ConstantValue.fingerPassWord, "").equals(""))
                {*/
                if (fingerprintManager != null && fingerprintManager!!.isHardwareDetected && fingerprintManager!!.hasEnrolledFingerprints()) {
                    try {
                        hasFinger = true
                        LogUtil.addLog("开始调用系统的指纹..")
                        KLog.i("开始调用系统的指纹..")
                        val view = View.inflate(this, R.layout.finger_dialog_layout, null)
                        formatDialog = CommonDialog(this)
                        formatDialog?.setCancelable(false)
                        val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容
                        val btn_cancel = view.findViewById<View>(R.id.btn_right) as Button//确定按钮
                        btn_cancel.visibility = View.VISIBLE
                        btn_cancel.setOnClickListener {
                            hideFingerAnimation()
                            formatDialog?.dismissWithAnimation()
//                            builderTips?.dismiss()
                            if (cancellationSignal != null) {
                                cancellationSignal?.cancel()
                                cancellationSignal = null
                            }

                        }
                        finger = view.findViewById<View>(R.id.finger) as ImageView
                        tvContent.setText(R.string.choose_finger_dialog_title)
                        val currentContext = this
                        val window = formatDialog?.window
                        window?.setBackgroundDrawableResource(android.R.color.transparent)
                        formatDialog?.setView(view)
                        formatDialog?.show()
                        llLogo.postDelayed({
                            myAuthCallback = MyAuthCallback(handler)
                            val cryptoObjectHelper = CryptoObjectHelper()
                            if (cancellationSignal == null) {
                                cancellationSignal = CancellationSignal()
                            }
                            fingerprintManager!!.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                    myAuthCallback, null)
                        }, 500)
                        fingerprintManager?.isHardwareDetected
                    } catch (e: Exception) {
                        LogUtil.addLog("调用系统指纹错误..")
                        KLog.i("调用系统指纹错误..")
                        try {
                            myAuthCallback = MyAuthCallback(handler)
                            val cryptoObjectHelper = CryptoObjectHelper()
                            if (cancellationSignal == null) {
                                cancellationSignal = CancellationSignal()
                            }
                            fingerprintManager!!.authenticate(cryptoObjectHelper.buildCryptoObject(), cancellationSignal, 0,
                                    myAuthCallback, null)
                            /* fingerprintManager.authenticate(cryptoObjectHelper.buildCryptoObject(), 0,
                                cancellationSignal, myAuthCallback, null);*/
                            val builder = AlertDialog.Builder(this)
                            val view = View.inflate(this, R.layout.finger_dialog_layout, null)
                            builder.setView(view)
                            builder.setCancelable(false)
                            val tvContent = view.findViewById<View>(R.id.tv_content) as TextView//输入内容
                            val btn_comfirm = view.findViewById<View>(R.id.btn_right) as Button//
                            btn_comfirm.setText(R.string.cancel_btn_dialog)
                            tvContent.setText(R.string.choose_finger_dialog_title)
                            val currentContext = this
                            builderTips = builder.create()
                            builderTips?.show()
                        } catch (er: Exception) {
                            er.printStackTrace()
                            builderTips?.dismiss()
                            toast(R.string.Fingerprint_init_failed_Try_again)
                        }

                    }

                } else {
                    hasFinger = false
                    var mKeyguardManager = getSystemService(Context.KEYGUARD_SERVICE) as KeyguardManager
                    if (!mKeyguardManager.isKeyguardSecure()) {
                        KLog.i("没有设置密码。。。。")
                        SpUtil.putString(this, ConstantValue.fingerPassWord, "")
                        val dialog = AlertDialog.Builder(this)
                        dialog.setMessage(R.string.No_fingerprints_do_you_want_to_set_them_up)
                        dialog.setCancelable(false)
                        dialog.setPositiveButton(android.R.string.ok
                        ) { dialog, which ->
                            var intent = Intent(Settings.ACTION_SECURITY_SETTINGS)
                            startActivityForResult(intent, SettingPassWord)
                        }
                        dialog.setNegativeButton(android.R.string.cancel
                        ) { dialog, which ->
                            hideFingerAnimation()
//                            finish();
//                            //android进程完美退出方法。
//                            var intent = Intent(Intent.ACTION_MAIN);
//                            intent.addCategory(Intent.CATEGORY_HOME);
//                            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
//                            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//
//                            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
//                            this.startActivity(intent);
//                            android.os.Process.killProcess(android.os.Process.myPid());
//                            //System.runFinalizersOnExit(true);
//                            System.exit(0);
                        }
                        dialog.create().show()

                    } else {
                        var intent = mKeyguardManager.createConfirmDeviceCredentialIntent(null, null);
                        if (intent != null) {
                            KLog.i("开始验证密码")
                            startActivityForResult(intent, AuthenticationScreen)
                        }
                    }
                }

            } catch (e: Exception) {
                SpUtil.putString(this, ConstantValue.fingerPassWord, "")
            }

        } else {
            SpUtil.putString(this, ConstantValue.fingerPassWord, "")
        }
        showFingerAnimation()
    }

    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        //super.onActivityResult(requestCode, resultCode, data)
        KLog.i("验证密码反悔了。")
        if (requestCode == AuthenticationScreen){
            if (resultCode == Activity.RESULT_OK) {
                handler!!.obtainMessage(MyAuthCallback.MSG_AUTH_SUCCESS).sendToTarget()
            } else {
                hideFingerAnimation()
                KLog.i("密码错误。。。。")
            }
        }
        if (requestCode == SettingPassWord){
            hideFingerAnimation()
        }
    }

    private fun handleErrorCode(code: Int) {
        when (code) {
            //case FingerprintManager.FINGERPRINT_ERROR_CANCELED:
            FingerprintManager.FINGERPRINT_ERROR_HW_UNAVAILABLE, FingerprintManager.FINGERPRINT_ERROR_LOCKOUT, FingerprintManager.FINGERPRINT_ERROR_NO_SPACE, FingerprintManager.FINGERPRINT_ERROR_TIMEOUT, FingerprintManager.FINGERPRINT_ERROR_UNABLE_TO_PROCESS -> {
                setResultInfo(R.string.ErrorHwUnavailable_warning)
            }
        }
    }

    override fun onDestroy() {
        ConstantValue.isShowVerify = false
        if (formatDialog != null && formatDialog!!.isShowing) {
            KLog.i("onDestroy")
            formatDialog?.dismiss()
            cancellationSignal?.cancel()
            cancellationSignal = null
        }
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }

    override fun onBackPressed() {

    }

    private fun handleHelpCode(code: Int) {
        when (code) {
            FingerprintManager.FINGERPRINT_ACQUIRED_GOOD, FingerprintManager.FINGERPRINT_ACQUIRED_IMAGER_DIRTY, FingerprintManager.FINGERPRINT_ACQUIRED_INSUFFICIENT, FingerprintManager.FINGERPRINT_ACQUIRED_PARTIAL, FingerprintManager.FINGERPRINT_ACQUIRED_TOO_FAST, FingerprintManager.FINGERPRINT_ACQUIRED_TOO_SLOW -> setResultInfo(R.string.AcquiredToSlow_warning)
        }
    }

    private fun setResultInfo(stringId: Int) {
        if (stringId == R.string.fingerprint_success) {
            finger?.setImageDrawable(resources.getDrawable(R.mipmap.icon_fingerprint_complete))
            setResult(RESULT_OK, intent)
            SpUtil.putString(this, ConstantValue.fingerPassWord, "888888")
            builderTips?.dismiss()
        } else {
            toast(stringId)
        }
    }

    override fun setupActivityComponent() {
        DaggerVerifyingFingerprintComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .verifyingFingerprintModule(VerifyingFingerprintModule(this))
                .build()
                .inject(this)
    }

    override fun setPresenter(presenter: VerifyingFingerprintContract.VerifyingFingerprintContractPresenter) {
        mPresenter = presenter as VerifyingFingerprintPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }

}