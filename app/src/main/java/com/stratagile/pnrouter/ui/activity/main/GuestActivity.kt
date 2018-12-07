package com.stratagile.pnrouter.ui.activity.main

import android.app.Activity
import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.os.Handler
import android.os.Message
import android.support.v4.content.ContextCompat
import android.support.v4.view.ViewPager
import android.view.KeyEvent
import android.view.Window
import android.view.WindowManager
import android.widget.Toast
import chat.tox.antox.tox.MessageHelper
import chat.tox.antox.tox.ToxService
import chat.tox.antox.wrapper.FriendKey
import com.nightonke.wowoviewpager.Animation.ViewAnimation
import com.nightonke.wowoviewpager.Animation.WoWoPositionAnimation
import com.nightonke.wowoviewpager.Animation.WoWoTranslationAnimation
import com.nightonke.wowoviewpager.Enum.Ease
import com.nightonke.wowoviewpager.WoWoViewPagerAdapter
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.base.BaseActivity
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.data.service.MessageRetrievalService
import com.stratagile.pnrouter.data.web.PNRouterServiceMessageReceiver
import com.stratagile.pnrouter.db.RouterEntity
import com.stratagile.pnrouter.db.RouterEntityDao
import com.stratagile.pnrouter.entity.BaseData
import com.stratagile.pnrouter.entity.JRecoveryRsp
import com.stratagile.pnrouter.entity.MyRouter
import com.stratagile.pnrouter.entity.RecoveryReq
import com.stratagile.pnrouter.entity.events.ConnectStatus
import com.stratagile.pnrouter.fingerprint.MyAuthCallback
import com.stratagile.pnrouter.ui.activity.login.LoginActivityActivity
import com.stratagile.pnrouter.ui.activity.main.component.DaggerGuestComponent
import com.stratagile.pnrouter.ui.activity.main.contract.GuestContract
import com.stratagile.pnrouter.ui.activity.main.module.GuestModule
import com.stratagile.pnrouter.ui.activity.main.presenter.GuestPresenter
import com.stratagile.pnrouter.ui.activity.register.RegisterActivity
import com.stratagile.pnrouter.ui.activity.scan.ScanQrCodeActivity
import com.stratagile.pnrouter.utils.*
import events.ToxStatusEvent
import im.tox.tox4j.core.enums.ToxMessageType
import interfaceScala.InterfaceScaleUtil
import kotlinx.android.synthetic.main.activity_guest.*
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.util.ArrayList
import javax.inject.Inject

/**
 * @author hzp
 * @Package com.stratagile.pnrouter.ui.activity.main
 * @Description: $description
 * @date 2018/09/18 14:25:55
 */

class GuestActivity : BaseActivity(), GuestContract.View , PNRouterServiceMessageReceiver.RecoveryMessageCallback{

    @Inject
    internal lateinit var mPresenter: GuestPresenter

    private var animationAdded = false

    private var r: Int = 0

    private var handler: Handler? = null
    protected var screenW: Int = 0
    protected var screenH: Int = 0
    val REQUEST_SELECT_ROUTER = 2
    private var exitTime: Long = 0
    val REQUEST_SCAN_QRCODE = 1
    var isHasConnect = false
    override fun recoveryBack(recoveryRsp: JRecoveryRsp) {

        closeProgressDialog();
        /*FileUtil.saveUserId2Local(recoveryRsp.params!!.userId)
        var newRouterEntity = RouterEntity()
        newRouterEntity.routerId = recoveryRsp.params.routeId
        newRouterEntity.userSn = recoveryRsp.params.userSn
        newRouterEntity.username =String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
        newRouterEntity.userId = recoveryRsp.params.userId
        newRouterEntity.dataFileVersion = 1
        var localData: ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
        newRouterEntity.routerName = "Router " + (localData.size + 1)
        val myRouter = MyRouter()
        myRouter.setType(0)
        myRouter.setRouterEntity(newRouterEntity)
        LocalRouterUtils.insertLocalAssets(myRouter)
        LocalRouterUtils.updateGreanDaoFromLocal();*/
        when (recoveryRsp.params.retCode) {
            0 -> {
                val routerEntityList = AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.queryBuilder().where(RouterEntityDao.Properties.UserSn.eq(recoveryRsp.params.userSn)).list()
                if (routerEntityList != null && routerEntityList!!.size != 0) {

                }else{
                    var newRouterEntity = RouterEntity()
                    newRouterEntity.routerId = recoveryRsp.params.routeId
                    newRouterEntity.userSn = recoveryRsp.params.userSn
                    newRouterEntity.username = String(RxEncodeTool.base64Decode(recoveryRsp.params.nickName))
                    newRouterEntity.userId = recoveryRsp.params.userId
                    newRouterEntity.dataFileVersion = recoveryRsp.params.dataFileVersion
                    newRouterEntity.dataFilePay = ""
                    newRouterEntity.loginKey = ""
                    var localData: ArrayList<MyRouter> =  LocalRouterUtils.localAssetsList
                    newRouterEntity.routerName = "Router " + (localData.size + 1)
                    val myRouter = MyRouter()
                    myRouter.setType(0)
                    myRouter.setRouterEntity(newRouterEntity)
                    LocalRouterUtils.insertLocalAssets(myRouter)
                    AppConfig.instance.mDaoMaster!!.newSession().routerEntityDao.insert(newRouterEntity)
                }
                startActivity(Intent(this, LoginActivityActivity::class.java))
                finish()
            }
            1 -> {
                startActivity(Intent(this, RegisterActivity::class.java))
            }
            2 -> {
                runOnUiThread {
                    toast("error")
                }
            }
            3 -> {
                var intent = Intent(this, RegisterActivity::class.java)
                intent.putExtra("flag", 1)
                startActivity(intent)
            }
            4 -> {

            }
            else -> {
            }
        }
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        needFront = true
        window.requestFeature(Window.FEATURE_ACTION_BAR)
        window.setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN)
        super.onCreate(savedInstanceState)
    }

    protected fun fragmentNumber(): Int {
        return 3
    }

    protected fun fragmentColorsRes(): Array<Int> {
        return arrayOf(R.color.white, R.color.white, R.color.white)
    }
    override fun getScanPermissionSuccess() {
        showProgressDialog("wait...")
        val intent1 = Intent(this, ScanQrCodeActivity::class.java)
        startActivityForResult(intent1, REQUEST_SCAN_QRCODE)
    }
    override fun initView() {
        setContentView(R.layout.activity_guest)
        var wowoAdapter = WoWoViewPagerAdapter.builder()
                .fragmentManager(supportFragmentManager)
                .count(fragmentNumber())                       // Fragment Count
                .colorsRes(*fragmentColorsRes())                // Colors of fragments
                .build()
        wowo.setAdapter(wowoAdapter)
        screenW = UIUtils.getDisplayWidth(this)
        screenH = UIUtils.getDisplayHeigh(this)

        r = Math.sqrt((screenW * screenW + screenH * screenH).toDouble()).toInt() + 10

        wowo.addTemporarilyInvisibleViews(1, iv2, tvPage2)

        wowo.addOnPageChangeListener(object : ViewPager.OnPageChangeListener{

            override fun onPageScrollStateChanged(p0: Int) {

            }

            override fun onPageScrolled(p0: Int, p1: Float, p2: Int) {

            }

            override fun onPageSelected(p0: Int) {
                if (p0 == 2) {
                    tvNext.text = getString(R.string.QR_Code)
                    var drawable:Drawable = getResources()!!.getDrawable(R.mipmap.icon_little_scan)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                    tvNext.setCompoundDrawables(drawable,null,null,null)
                } else {
                    tvNext.text = resources.getString(R.string.next)
                    var drawable:Drawable = getResources()!!.getDrawable(R.mipmap.no)
                    drawable.setBounds(0, 0, drawable.getMinimumWidth(), drawable.getMinimumHeight())
                    tvNext.setCompoundDrawables(drawable,null,null,null)
                }
            }

        })
    }
    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            exitToast()
        }
        return false
    }

    fun exitToast(): Boolean {
        if (System.currentTimeMillis() - exitTime > 2000) {
            Toast.makeText(this, R.string.Press_again, Toast.LENGTH_SHORT)
                    .show()
            exitTime = System.currentTimeMillis()
        } else {

            MessageRetrievalService.registerActivityFinished(this)
            //android进程完美退出方法。
            var intent = Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            //intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            //让Activity的生命周期进入后台，否则在某些手机上即使sendSignal 3和9了，还是由于Activity的生命周期导致进程退出不了。除非调用了Activity.finish()
            this.startActivity(intent);
            android.os.Process.killProcess(android.os.Process.myPid());
            //System.runFinalizersOnExit(true);
            System.exit(0);
        }
        return false
    }
    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        addAnimations()
    }

    private fun addAnimations() {
        if (animationAdded) {
            return
        }
        animationAdded = true
        addBack()
        addIv0()
        addTvPage0()
        addTvPage0TestNet()

        addIv1()
        addTvPage1()

        addIv2()
        addTvPage2()
        addGotIt()

        addDot()

        wowo.ready()
    }

    protected fun color(colorRes: Int): Int {
        return ContextCompat.getColor(this, colorRes)
    }
    override fun onDestroy() {
        AppConfig.instance.messageReceiver?.recoveryBackListener = null
        EventBus.getDefault().unregister(this)
        super.onDestroy()
    }
    private fun addTvPage0TestNet() {
//        wowo.addAnimation(tvPage0TestNet)
//                .add(WoWoTranslationAnimation.builder().page(0)
//                        .fromX(0f).toX((-screenW).toFloat())
//                        .fromY(0f).toY(-600f).build())
    }

    private fun addTvPage0() {
        wowo.addAnimation(tvPage0)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(0f).toX(screenW.toFloat())
                        .fromY(0f).toY(500f).build())
    }


    private fun addIv0() {
        wowo.addAnimation(iv0)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(0f).toX((-screenW).toFloat())
                        .keepY(0f).toY(-500f).build())
    }

    private fun addTvPage1() {
        wowo.addAnimation(tvPage1)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX((-screenW).toFloat()).toX(0f)
                        .fromY(500f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX(screenW.toFloat())
                        .fromY(0f).toY(500f).build())
    }


    private fun addIv1() {
        wowo.addAnimation(iv1)
                .add(WoWoTranslationAnimation.builder().page(0)
                        .fromX(screenW.toFloat()).toX(0f)
                        .keepY(-500f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX((-screenW).toFloat())
                        .keepY(0f).toY(-500f).build())
    }

    private fun addTvPage2() {
        wowo.addAnimation(tvPage2)
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX((-screenW).toFloat()).toX(0f)
                        .fromY(500f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX(screenW.toFloat())
                        .fromY(0f).toY(500f).build())
    }


    private fun addIv2() {
        wowo.addAnimation(iv2)
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(screenW.toFloat()).toX(0f)
                        .keepY(-500f).toY(0f).build())
                .add(WoWoTranslationAnimation.builder().page(1)
                        .fromX(0f).toX((-screenW).toFloat())
                        .keepY(0f).toY(-500f).build())
    }

    private fun addGotIt() {
//        wowo.addAnimation(gotIt)
//                .add(WoWoTranslationAnimation.builder().page(1)
//                        .keepX(gotIt.getTranslationX())
//                        .fromY(screenH.toFloat()).toY(0f).ease(Ease.OutBack)
//                        .build())
//                .add(WoWoTranslationAnimation.builder().page(1)
//                        .keepX(0f)
//                        .fromY(0f).toY(screenH.toFloat())
//                        .ease(Ease.InCubic).sameEaseBack(false).build())
    }

    private fun addBack() {
        wowo.addAnimation(ivBack)
    }

    private fun addDot() {
        val viewAnimation = ViewAnimation(dot)
        viewAnimation.add(WoWoPositionAnimation.builder().page(0)
                .fromX( -resources.getDimension(R.dimen.x15) + dot.width).toX(dot.x)
                .keepY(0f)
                .ease(Ease.Linear).build())
        viewAnimation.add(WoWoPositionAnimation.builder().page(1)
                .fromX(dot.x).toX(dot.x + resources.getDimension(R.dimen.x20) + dot.width)
                .keepY(0f)
                .ease(Ease.Linear).build())
        wowo.addAnimation(viewAnimation)
    }

    override fun initData() {
        var this_ = this
        var isStartWebsocket = false
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
                                        if(ConstantValue.scanRouterId.equals(udpRouterArray[1]))
                                        {
                                            ConstantValue.currentRouterIp = udpRouterArray[0]
                                            ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                            break;
                                        }

                                    }
                                }
                                index ++
                            }
                            if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals("") && !isStartWebsocket)
                            {
                                ConstantValue.curreantNetworkType = "WIFI"
                                if(isHasConnect)
                                {
                                    AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                }else{
                                    AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                }
                                AppConfig.instance.messageReceiver!!.recoveryBackListener = this_
                                isStartWebsocket = true
                            }
                        }

                    }
                }
            }
        }
        EventBus.getDefault().register(this)
        SpUtil.putInt(this, ConstantValue.LOCALVERSIONCODE, VersionUtil.getAppVersionCode(this))
        tvNext.setOnClickListener {
            if (wowo.currentItem == 2) {
               /*if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                {
                    AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    AppConfig.instance.messageReceiver!!.recoveryBackListener = this

                }*/
                mPresenter.getScanPermission()
                //startActivity(Intent(this, LoginActivityActivity::class.java))
            } else {
                wowo.next()
            }
        }
    }

    override fun setupActivityComponent() {
        DaggerGuestComponent
                .builder()
                .appComponent((application as AppConfig).applicationComponent)
                .guestModule(GuestModule(this))
                .build()
                .inject(this)
    }
    override fun setPresenter(presenter: GuestContract.GuestContractPresenter) {
        mPresenter = presenter as GuestPresenter
    }

    override fun showProgressDialog() {
        progressDialog.show()
    }

    override fun closeProgressDialog() {
        progressDialog.hide()
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onWebSocketConnected(connectStatus: ConnectStatus) {
        when (connectStatus.status) {
            0 -> {
                isHasConnect = true
                var recovery = RecoveryReq( ConstantValue.currentRouterId, ConstantValue.currentRouterSN)
                AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(2,recovery))

            }
            1 -> {

            }
            2 -> {

            }
            3 -> {
                runOnUiThread {
                    closeProgressDialog()
                    toast(R.string.Network_error)
                }
            }
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onToxConnected(toxStatusEvent: ToxStatusEvent) {
        when (toxStatusEvent.status) {
            1 -> {
                ConstantValue.isToxConnected = true
                AppConfig.instance.getPNRouterServiceMessageToxReceiver()

                if(!ConstantValue.scanRouterId.equals(""))
                {
                    AppConfig.instance.messageReceiver!!.recoveryBackListener = this
                    InterfaceScaleUtil.addFriend(ConstantValue.scanRouterId,this)
                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN)
                    var baseData = BaseData(2,recovery)
                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                    var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                    MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                }
            }
        }

    }
    override fun onResume() {
        exitTime = System.currentTimeMillis() - 2001
        if(AppConfig.instance.messageReceiver != null)
            AppConfig.instance.messageReceiver!!.close()
        ConstantValue.isWebsocketConnected = false
        super.onResume()
    }
    override  fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_SCAN_QRCODE && resultCode == Activity.RESULT_OK) {
            var result = data!!.getStringExtra("result");
            try {
                var soureData:ByteArray =  AESCipher.aesDecryptByte(result,"welcometoqlc0101")
                val keyId:ByteArray = ByteArray(6) //密钥ID
                val RouterId:ByteArray = ByteArray(76) //路由器id
                val UserSn:ByteArray = ByteArray(32)  //用户SN
                System.arraycopy(soureData, 0, keyId, 0, 6)
                System.arraycopy(soureData, 6, RouterId, 0, 76)
                System.arraycopy(soureData, 82, UserSn, 0, 32)
                var keyIdStr = String(keyId)
                var RouterIdStr = String(RouterId)
                var UserSnStr = String(UserSn)
                ConstantValue.scanRouterId = RouterIdStr
                ConstantValue.scanRouterSN = UserSnStr
                if(RouterIdStr != null && !RouterIdStr.equals("")&& UserSnStr != null && !UserSnStr.equals(""))
                {
                    ConstantValue.currentRouterIp = ""
                    if(WiFiUtil.isWifiConnect())
                    {
                        var count =0;
                        KLog.i("测试计时器" + count)
                        Thread(Runnable() {
                            run() {

                                while (true)
                                {
                                    if(count >=3)
                                    {
                                        if(!ConstantValue.currentRouterIp.equals(""))
                                        {
                                            Thread.currentThread().interrupt(); //方法调用终止线程
                                            break;
                                        }else{
                                            var httpData = HttpClient.httpGet(ConstantValue.httpUrl + ConstantValue.scanRouterId)
                                            if(httpData.retCode == 0 && httpData.connStatus == 1)
                                            {
                                                ConstantValue.curreantNetworkType = "WIFI"
                                                ConstantValue.currentRouterIp = httpData.serverHost
                                                ConstantValue.port = ":"+httpData.serverPort.toString()
                                                ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                                                ConstantValue.currentRouterId = ConstantValue.scanRouterId
                                                ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                                                if(isHasConnect)
                                                {
                                                    AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                                                }else{
                                                    AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                                                }
                                                AppConfig.instance.messageReceiver!!.recoveryBackListener = this
                                                Thread.currentThread().interrupt() //方法调用终止线程
                                            }else{
                                                ConstantValue.curreantNetworkType = "TOX"
                                                if(!ConstantValue.isToxConnected)
                                                {
                                                    var intent = Intent(this, ToxService::class.java)
                                                    startService(intent)
                                                }else{
                                                    runOnUiThread {
                                                        showProgressDialog("wait...")
                                                    }
                                                    AppConfig.instance.messageReceiver!!.recoveryBackListener = this
                                                    InterfaceScaleUtil.addFriend(ConstantValue.scanRouterId,this)
                                                    var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN)
                                                    var baseData = BaseData(2,recovery)
                                                    var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                                    var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                                                    MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                                                }
                                                Thread.currentThread().interrupt(); //方法调用终止线程
                                            }
                                            break;
                                        }

                                    }
                                    count ++;
                                    MobileSocketClient.getInstance().init(handler,this)
                                    var toxIdMi = AESCipher.aesEncryptString(RouterIdStr,"slph\$%*&^@-78231")
                                    MobileSocketClient.getInstance().send("QLC"+toxIdMi)
                                    MobileSocketClient.getInstance().receive()
                                    KLog.i("测试计时器" + count)
                                    Thread.sleep(1000)
                                }

                            }
                        }).start()


                    }else{

                        var httpData = HttpClient.httpGet(ConstantValue.httpUrl + ConstantValue.scanRouterId)
                        if(httpData.retCode == 0 && httpData.connStatus == 1)
                        {
                            ConstantValue.curreantNetworkType = "WIFI"
                            ConstantValue.currentRouterIp = httpData.serverHost
                            ConstantValue.port = ":"+httpData.serverPort.toString()
                            ConstantValue.filePort = ":"+(httpData.serverPort +1).toString()
                            ConstantValue.currentRouterId = ConstantValue.scanRouterId
                            ConstantValue.currentRouterSN =  ConstantValue.scanRouterSN
                            if(isHasConnect)
                            {
                                AppConfig.instance.getPNRouterServiceMessageReceiver().reConnect()
                            }else{
                                AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                            }
                            AppConfig.instance.messageReceiver!!.recoveryBackListener = this
                        }else{
                            ConstantValue.curreantNetworkType = "TOX"
                            if(!ConstantValue.isToxConnected)
                            {
                                var intent = Intent(this, ToxService::class.java)
                                startService(intent)
                            }else{
                                runOnUiThread {
                                    showProgressDialog("wait...")
                                }
                                AppConfig.instance.messageReceiver!!.recoveryBackListener = this
                                InterfaceScaleUtil.addFriend(ConstantValue.scanRouterId,this)
                                var recovery = RecoveryReq( ConstantValue.scanRouterId, ConstantValue.scanRouterSN)
                                var baseData = BaseData(2,recovery)
                                var baseDataJson = baseData.baseDataToJson().replace("\\", "")
                                var friendKey: FriendKey = FriendKey(ConstantValue.scanRouterId.substring(0, 64))
                                MessageHelper.sendMessageFromKotlin(this, friendKey, baseDataJson, ToxMessageType.NORMAL)
                            }
                        }
                    }
                }else{
                    toast(R.string.code_error)
                }




              /*  for (data in ConstantValue.updRouterData)
                {
                    var key:String = data.key;
                    if(key.equals(RouterIdStr))
                    {
                        ConstantValue.currentRouterIp = ConstantValue.updRouterData.get(key)!!
                        ConstantValue.currentRouterId = RouterIdStr
                        ConstantValue.currentRouterSN = UserSnStr
                        break;
                    }
                }
                if(ConstantValue.currentRouterIp != null  && !ConstantValue.currentRouterIp.equals(""))
                {
                    AppConfig.instance.getPNRouterServiceMessageReceiver(true)
                    AppConfig.instance.messageReceiver!!.recoveryBackListener = this
                }*/
            }catch (e:Exception)
            {
                runOnUiThread {
                    toast(R.string.code_error)
                }
            }

        }else{
            runOnUiThread {
                closeProgressDialog()
            }

        }
    }
}