package com.stratagile.pnrouter.data.web

import android.util.Log
import com.google.gson.Gson
import com.hyphenate.chat.EMMessage
import com.hyphenate.easeui.utils.EaseImageUtils
import com.hyphenate.easeui.utils.PathUtils
import com.message.Message
import com.pawegio.kandroid.toast
import com.socks.library.KLog
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.constant.ConstantValue
import com.stratagile.pnrouter.db.MessageEntity
import com.stratagile.pnrouter.entity.*
import com.stratagile.pnrouter.entity.events.*
import com.stratagile.pnrouter.utils.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode
import java.io.File
import java.io.IOException
import java.util.*
import java.util.concurrent.atomic.AtomicReference
import javax.inject.Inject
import kotlin.collections.HashMap
import kotlin.concurrent.thread

class PNRouterServiceMessageSender @Inject constructor(pipe: Optional<SignalServiceMessagePipe>, private val eventListener: Optional<EventListener>) {
    private val pipe: AtomicReference<Optional<SignalServiceMessagePipe>>
    var javaObject = Object()
    val sendFileSizeMax = 1024 * 1024 * 2
    lateinit var msgHashMap: HashMap<String,Queue<BaseData>>
    lateinit var fileHashMap: HashMap<String,Queue<SendFileInfo>>
    lateinit var toSendChatMessage: Queue<BaseData>
    lateinit var toSendChatFileMessage: Queue<SendFileInfo>
    lateinit var toSendMessage: Queue<BaseData>
    lateinit var thread: Thread
    lateinit var sendMsgLocalMap: HashMap<String, Boolean>
    lateinit var sendFilePathMap : HashMap<String, String>
    lateinit var deleteFileMap : HashMap<String, Boolean>
    lateinit var sendFileFriendKeyMap : HashMap<String, String>
    lateinit var sendFileKeyByteMap : HashMap<String, String>
    lateinit var sendFileFriendKeyByteMap : HashMap<String, ByteArray>
    lateinit var sendFileMyKeyByteMap : HashMap<String, ByteArray>
    lateinit var sendFileResultMap:java.util.HashMap<String, Boolean>
    lateinit var sendFileNameMap:java.util.HashMap<String, String>
    lateinit var sendFileLastByteSizeMap:java.util.HashMap<String, Int>
    lateinit var sendFileLeftByteMap:java.util.HashMap<String, ByteArray>
    lateinit var sendMsgIdMap:java.util.HashMap<String, String>
    lateinit var receiveFileDataMap:java.util.HashMap<String, Message>
    lateinit var receiveToxFileDataMap:java.util.HashMap<String, Message>
    lateinit var receiveToxFileIdMap:java.util.HashMap<String, String>
    init {


        sendMsgLocalMap = HashMap<String, Boolean>()
        sendFilePathMap = HashMap<String, String>()
        deleteFileMap = HashMap<String, Boolean>()
        sendFileFriendKeyMap = HashMap<String, String>()
        sendFileKeyByteMap = HashMap<String, String>()
        sendFileFriendKeyByteMap = HashMap<String, ByteArray>()
        sendFileMyKeyByteMap = HashMap<String, ByteArray>()

        sendFileResultMap = java.util.HashMap<String, Boolean>()
        sendFileNameMap = java.util.HashMap<String, String>()
        sendFileLastByteSizeMap = java.util.HashMap<String, Int>()
        sendFileLeftByteMap = java.util.HashMap<String, ByteArray>()
        sendMsgIdMap = java.util.HashMap<String, String>()
        receiveFileDataMap = java.util.HashMap<String, Message>()
        receiveToxFileDataMap = java.util.HashMap<String, Message>()
        receiveToxFileIdMap = java.util.HashMap<String, String>()

        EventBus.getDefault().register(this)
        msgHashMap = HashMap<String,Queue<BaseData>>()
        fileHashMap = HashMap<String,Queue<SendFileInfo>>()
        toSendMessage = LinkedList()
        //toSendChatMessage = LinkedList()
        this.pipe = AtomicReference(pipe)
        initThread()
    }

    fun addDataFromSql(userId:String, BaseDataStr:String)
    {
        var gson = GsonUtil.getIntGson()
        val message = gson.fromJson(BaseDataStr, BaseData::class.java)
        if(msgHashMap.get(userId) == null)
        {
            msgHashMap.put(userId!!,LinkedList())
            toSendChatMessage = msgHashMap.get(userId!!) as Queue<BaseData>
        }else{
            toSendChatMessage = msgHashMap.get(userId!!) as Queue<BaseData>
        }
        toSendChatMessage.offer(message)
    }
    fun addFileDataFromSql(userId:String, sendFileInfo:SendFileInfo)
    {
        if(fileHashMap.get(userId) == null)
        {
            fileHashMap.put(userId!!,LinkedList())
            toSendChatFileMessage = fileHashMap.get(userId!!) as Queue<SendFileInfo>
        }else{
            toSendChatFileMessage = fileHashMap.get(userId!!) as Queue<SendFileInfo>
        }
        toSendChatFileMessage.offer(sendFileInfo)
    }
    fun send(message: BaseData){
        Log.i("sender", "添加")
        toSendMessage.offer(message)
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        /*if (thread.state == Thread.State.NEW) {
            thread.start()
        }*/
        sendOtherMessage()
//        javaObject.notifyAll()
//        return sendMessageTo()
    }
    fun sendChatMsg(message: BaseData){
        Log.i("sender", "添加")
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if(msgHashMap.get(userId) == null)
        {
            msgHashMap.put(userId!!,LinkedList())
            toSendChatMessage = msgHashMap.get(userId!!) as Queue<BaseData>
        }else{
            toSendChatMessage = msgHashMap.get(userId!!) as Queue<BaseData>
        }
        toSendChatMessage.offer(message)
        var gson = GsonUtil.getIntGson()
        val SendMsgReqV3 =  message.params as SendMsgReqV3
        var messageEntity  = MessageEntity()
        messageEntity.userId = userId;
        messageEntity.friendId = SendMsgReqV3.To
        messageEntity.sendTime = message.timestamp
        messageEntity.type = "0"
        messageEntity.msgId = message.msgid.toString()
        messageEntity.baseData = message.baseDataToJson().replace("\\", "")
        messageEntity.complete = false
        KLog.i("消息数据增加文本：userId："+userId +" friendId:"+SendMsgReqV3.To)
        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.insert(messageEntity)
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
        if(WiFiUtil.isNetworkConnected() && ConstantValue.logining)
        {
            sendChatMessage(true,false)
        }
//        javaObject.notifyAll()
//        return sendMessageTo()
    }
    fun sendFileMsg(message: SendFileInfo){

        Log.i("senderFile", "添加")
        val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
        if(fileHashMap.get(userId) == null)
        {
            fileHashMap.put(userId!!,LinkedList())
            toSendChatFileMessage = fileHashMap.get(userId!!) as Queue<SendFileInfo>
        }else{
            toSendChatFileMessage = fileHashMap.get(userId!!) as Queue<SendFileInfo>
        }
        toSendChatFileMessage.offer(message)

        var messageEntity  = MessageEntity()
        messageEntity.userId = userId;
        messageEntity.friendId = message.friendId
        messageEntity.sendTime = message.sendTime
        messageEntity.type = message.type
        messageEntity.msgId = message.msgId
        messageEntity.baseData = ""
        messageEntity.complete = false
        messageEntity.filePath = message.files_dir
        messageEntity.friendSignPublicKey = message.friendSignPublicKey
        messageEntity.friendMiPublicKey = message.friendMiPublicKey
        messageEntity.voiceTimeLen = message.voiceTimeLen
        KLog.i("消息数据增加文件：userId："+userId +" friendId:"+message.friendId)
        AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.insert(messageEntity)
        Log.i("sender_thread.state", (thread.state == Thread.State.NEW).toString())
        if (thread.state == Thread.State.NEW) {
            thread.start()
        }
        if(WiFiUtil.isNetworkConnected() && ConstantValue.logining)
        {
            sendChatFileMessage(true,false)
        }

    }
    /**
     * Send a read receipt for a received message.
     *
     * @param recipient The sender of the received message you're acknowledging.
     * @param message The read receipt to deliver.
     * @throws IOException
     * @throws UntrustedIdentityException
     */
//    fun sendReceipt(recipient: SignalServiceAddress, message: SignalServiceReceiptMessage) {
//        val content = createReceiptContent(message)
//        sendMessageTo(recipient, message.getWhen(), content, true)
//    }

    /**
     * Send a call setup message to a single recipient.
     *
     * @param recipient The message's destination.
     * @param message The call message.
     * @throws IOException
     */
//    fun sendCallMessage(recipient: SignalServiceAddress, message: SignalServiceCallMessage) {
//        val content = createCallContent(message)
//        sendMessageTo(recipient, System.currentTimeMillis(), content, true)
//    }

    @Synchronized
    fun  initThread() {
        thread =  thread(true, false, null, "senderThread") {
            while (true) {
                KLog.i("发送线程运行中等待。。。")
                Thread.sleep(10 * 1000)
                KLog.i( "发送线程运行中。。。")
                if(WiFiUtil.isNetworkConnected() && ConstantValue.logining && ConstantValue.curreantNetworkType.equals("WIFI"))
                {
                    sendChatMessage(false,false)
                    sendChatFileMessage(false,false)
                }else{
                    ConstantValue.sendFileMsgMap.clear()
                }
//               Log.i("sender", "线程运行中。。。")
            }
        }
        thread.name = "sendThread"
    }


    fun sendChatMessage(sendNow:Boolean,remove:Boolean) {
        try {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if(msgHashMap.get(userId!!) == null)
            {
                return
            }
            var toSendChatMessageQueue = msgHashMap.get(userId!!) as Queue<BaseData>
            if (toSendChatMessageQueue != null && toSendChatMessageQueue.isNotEmpty()){
                Log.i("sendChat_size", toSendChatMessageQueue.size.toString())
                if(sendNow)
                {
                    var message = BaseData()
                    if(remove)
                    {
                        message = toSendChatMessageQueue.poll()
                    }else{
                        message = toSendChatMessageQueue.peek()
                    }
                    Log.i("sendChat_message", message.baseDataToJson().replace("\\", ""))
                    LogUtil.addLog("发送信息：${message.baseDataToJson().replace("\\", "")}")
                    var reslut= pipe.get().get().webSocketConnection().send(message.baseDataToJson().replace("\\", ""))
                    LogUtil.addLog("发送结果：${reslut}")
                }else{
                    if(ConstantValue.logining)
                    {
                        Log.i("sendChat_size_Auto", toSendChatMessageQueue.size.toString())
                        for (item in toSendChatMessageQueue)
                        {
                            if(Calendar.getInstance().timeInMillis - item.timestamp!!.toLong() > 10 * 1000)
                            {
                                item.timestamp = Calendar.getInstance().timeInMillis.toString();
                                Log.i("sendChat_message_Thread", item.baseDataToJson().replace("\\", ""))
                                LogUtil.addLog("发送信息：${item.baseDataToJson().replace("\\", "")}")
                                var reslut= pipe.get().get().webSocketConnection().send(item.baseDataToJson().replace("\\", ""))
                                LogUtil.addLog("发送结果：${reslut}")
                            }
                        }
                    }

                }

            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
    fun sendChatFileMessage(sendNow:Boolean,remove:Boolean) {
        try {
            val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
            if(fileHashMap.get(userId!!) == null)
            {
                return
            }
            var toSendChatFileQueue = fileHashMap.get(userId!!) as Queue<SendFileInfo>
            if (toSendChatFileQueue != null && toSendChatFileQueue.isNotEmpty()){
                Log.i("sendFile_size", toSendChatFileQueue.size.toString())
                if(sendNow)
                {
                    var message = SendFileInfo()
                    if(remove)
                    {
                        message = toSendChatFileQueue.poll()
                    }else{
                        message = toSendChatFileQueue.peek()
                    }
                    when(message.type){
                        "1" ->
                        {
                            sendImageMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey)
                        }
                        "2" ->
                        {
                            sendVoiceMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey,message.voiceTimeLen)
                        }
                        "3" ->
                        {
                            sendVideoMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey)
                        }
                        "4" ->
                        {
                            sendFileMessage(message.userId,message.friendId,message.files_dir,message.msgId,message.friendSignPublicKey,message.friendMiPublicKey)
                        }
                    }
                }else{
                    if(ConstantValue.logining)
                    {
                        Log.i("sendFile_size_Auto", toSendChatFileQueue.size.toString())
                        for (item in toSendChatFileQueue)
                        {
                            if(ConstantValue.sendFileMsgTimeMap[item.msgId] != null)
                            {
                                if(Calendar.getInstance().timeInMillis - ConstantValue.sendFileMsgTimeMap[item.msgId]!!.toLong() > 2 * 60 * 1000)
                                {
                                    val message = EMMessage.createImageSendMessage(item.files_dir, true, item.friendId)
                                    ConstantValue.sendFileMsgMap[item.msgId] = message
                                }
                            }
                            /*if(Calendar.getInstance().timeInMillis - item.sendTime!!.toLong() > 30 * 1000)
                            {


                            }*/
                            item.sendTime = Calendar.getInstance().timeInMillis.toString();
                            when(item.type){
                                "1" ->
                                {
                                    sendImageMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey)
                                }
                                "2" ->
                                {
                                    sendVoiceMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey,item.voiceTimeLen)
                                }
                                "3" ->
                                {
                                    sendVideoMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey)
                                }
                                "4" ->
                                {
                                    sendFileMessage(item.userId,item.friendId,item.files_dir,item.msgId,item.friendSignPublicKey,item.friendMiPublicKey)
                                }
                            }
                        }
                    }

                }

            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
    fun sendOtherMessage() {
        try {
            if (toSendMessage != null && toSendMessage.isNotEmpty()){

                var message = toSendMessage.poll()
                Log.i("send", message.baseDataToJson().replace("\\", ""))
                LogUtil.addLog("发送信息：${message.baseDataToJson().replace("\\", "")}")
                var reslut= pipe.get().get().webSocketConnection().send(message.baseDataToJson().replace("\\", ""))
                LogUtil.addLog("发送结果：${reslut}")

            }
        }catch (e:Exception)
        {
            e.printStackTrace()
        }

    }
    interface EventListener {
        fun onSecurityEvent(address: SignalServiceAddress)
    }

    companion object {

        private val TAG = PNRouterServiceMessageSender::class.java.simpleName
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun deleteMsgEvent(deleteMsgEvent: DeleteMsgEvent) {
        deleteFileMap[deleteMsgEvent.msgId] = true
        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
        if(messageEntityList != null)
        {
            messageEntityList.forEach {
                if (it.msgId.equals(deleteMsgEvent.msgId)) {
                    AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                    KLog.i("消息数据删除")
                }
            }
        }
        var  toSendMessage = toSendChatFileMessage
        if(toSendMessage != null)
        {
            for (item in toSendMessage)
            {
                if(item.msgId.equals(deleteMsgEvent.msgId))
                {
                    toSendMessage.remove(item)
                    break
                }
            }
        }
    }
    /**
     * 开始发送文件
     * @param fileTransformEntity
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onBeginSendFile(fileTransformEntity: FileTransformEntity) {
        val EMMessage = ConstantValue.sendFileMsgMap[fileTransformEntity.toId] ?: return
        if(EMMessage.from == null)
        {
            return
        }
        if (fileTransformEntity.message == 0) {
            return
        }
        when (fileTransformEntity.message) {
            1 -> Thread(Runnable {
                try {
                    ConstantValue.sendFileMsgTimeMap[fileTransformEntity.toId] =  System.currentTimeMillis().toString()
                    val EMMessage = ConstantValue.sendFileMsgMap[fileTransformEntity.toId]
                    if(EMMessage!!.from == null)
                    {
                        return@Runnable
                    }
                    val filePath = sendFilePathMap[fileTransformEntity.toId] ?: return@Runnable
                    val fileName = filePath.substring(filePath.lastIndexOf("/") + 1)
                    val fileKey = sendFileKeyByteMap[fileTransformEntity.toId]
                    val SrcKey = sendFileMyKeyByteMap[fileTransformEntity.toId]
                    val DstKey = sendFileFriendKeyByteMap[fileTransformEntity.toId]
                    val file = File(filePath)
                    if (file.exists()) {
                        val fileSize = file.length()
                        val fileBuffer = FileUtil.file2Byte(filePath)
                        val fileId = (System.currentTimeMillis() / 1000).toInt()
                        var fileBufferMi = ByteArray(0)
                        try {
                            val miBegin = System.currentTimeMillis()
                            /*if(ConstantValue.INSTANCE.getEncryptionType().equals("1"))
                            {
                                fileBufferMi = LibsodiumUtil.INSTANCE.EncryptSendFile(fileBuffer,fileKey);
                            }else{
                                fileBufferMi = AESCipher.aesEncryptBytes(fileBuffer,fileKey.getBytes("UTF-8"));
                            }*/
                            fileBufferMi = AESCipher.aesEncryptBytes(fileBuffer, fileKey!!.toByteArray(charset("UTF-8")))
                            val miend = System.currentTimeMillis()
                            KLog.i("jiamiTime:" + (miend - miBegin) / 1000)

                            if (deleteFileMap[fileTransformEntity.toId] != null) {
                                sendFileByteData(fileBufferMi, fileName, EMMessage!!.getFrom(), EMMessage!!.getTo(), fileTransformEntity.toId, fileId, 1, fileKey, SrcKey!!, DstKey!!)
                            } else {
                                KLog.i("websocket文件发送前取消！")
                                val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                                EventBus.getDefault().post(FileTransformEntity(fileTransformEntity.toId, 4, "", wssUrl, "lws-pnr-bin"))
                                EventBus.getDefault().post(FileTransformStatus(fileTransformEntity.toId,"", EMMessage!!.getTo(),0))
                            }

                        } catch (e: Exception) {
                            val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                            EventBus.getDefault().post(FileTransformEntity(fileTransformEntity.toId, 4, "", wssUrl, "lws-pnr-bin"))
                        }

                    }
                } catch (e: Exception) {

                }
            }).start()
            2 -> {
            }
            3 -> {
            }
            else -> {
            }
        }
    }

    /**
     * 片段发送中
     * @param transformReceiverFileMessage
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    fun onSendFileing(transformReceiverFileMessage: TransformReceiverFileMessage) {
        val EMMessageData = ConstantValue.sendFileMsgMap[transformReceiverFileMessage.toId] ?: return
        if(EMMessageData.from == null)
        {
            return
        }
        ConstantValue.sendFileMsgTimeMap[transformReceiverFileMessage.toId] =  System.currentTimeMillis().toString()
        val retMsg = transformReceiverFileMessage.message
        val Action = ByteArray(4)
        val FileId = ByteArray(4)
        val LogId = ByteArray(4)
        val SegSeq = ByteArray(4)
        val CRC = ByteArray(2)
        val Code = ByteArray(2)
        val FromId = ByteArray(76)
        val ToId = ByteArray(76)
        System.arraycopy(retMsg, 0, Action, 0, 4)
        System.arraycopy(retMsg, 4, FileId, 0, 4)
        System.arraycopy(retMsg, 8, LogId, 0, 4)
        System.arraycopy(retMsg, 12, SegSeq, 0, 4)
        System.arraycopy(retMsg, 16, CRC, 0, 2)
        System.arraycopy(retMsg, 18, Code, 0, 2)
        System.arraycopy(retMsg, 20, FromId, 0, 76)
        System.arraycopy(retMsg, 97, ToId, 0, 76)
        val ActionResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(Action))
        val FileIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(FileId))
        val LogIdIdResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(LogId))
        val SegSeqResult = FormatTransfer.reverseInt(FormatTransfer.lBytesToInt(SegSeq))
        val CRCResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(CRC))
        val CodeResult = FormatTransfer.reverseShort(FormatTransfer.lBytesToShort(Code)).toInt()
        val FromIdResult = String(FromId)
        val ToIdResult = String(ToId)
        KLog.i("CodeResult:$CodeResult")
        val lastSendSize = sendFileLastByteSizeMap.get(FileIdResult.toString() + "")
        val fileBuffer = sendFileLeftByteMap.get(FileIdResult.toString() + "")
        val leftSize = fileBuffer!!.size - lastSendSize!!
        val msgId = sendMsgIdMap.get(FileIdResult.toString() + "")
        when (CodeResult) {
            0 -> {

                if (leftSize > 0)
                {
                    Thread(Runnable {
                        try {
                            val fileLeftBuffer = ByteArray(leftSize)
                            System.arraycopy(fileBuffer, sendFileSizeMax, fileLeftBuffer, 0, leftSize)
                            val fileName = sendFileNameMap.get(FileIdResult.toString() + "")
                            val fileKey = sendFileKeyByteMap[FileIdResult.toString() + ""]
                            val SrcKey = sendFileMyKeyByteMap[FileIdResult.toString() + ""]
                            val DstKey = sendFileFriendKeyByteMap[FileIdResult.toString() + ""]
                            if (deleteFileMap[msgId] != null) {
                                sendFileByteData(fileLeftBuffer, fileName!!, FromIdResult + "", ToIdResult + "", msgId!!, FileIdResult, SegSeqResult + 1, fileKey!!, SrcKey!!, DstKey!!)
                            } else {
                                KLog.i("websocket文件发送中取消！")
                                val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                                EventBus.getDefault().post(FileTransformEntity(msgId!!, 4, "", wssUrl, "lws-pnr-bin"))
                                EventBus.getDefault().post(FileTransformStatus(msgId!!,LogIdIdResult.toString(),ToIdResult, 0))
                                var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                                if(messageEntityList != null)
                                {
                                    messageEntityList.forEach {
                                        if (it.msgId.equals(msgId)) {
                                            AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                            KLog.i("消息数据删除")
                                        }
                                    }
                                }
                            }
                        } catch (e: Exception) {

                        }
                    }).start()

                } else {
                    if (deleteFileMap[msgId] != null) {
                        /*val EMMessage = EMClient.getInstance().chatManager().getMessage(msgId)
                        conversation.removeMessage(msgId)
                        EMMessage.msgId = LogIdIdResult.toString() + ""
                        EMMessage.isAcked = true
                        sendMessageTo(EMMessage)
                        conversation.updateMessage(EMMessage)*/
                        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                        if(messageEntityList != null)
                        {
                            messageEntityList.forEach {
                                if (it.msgId.equals(msgId)) {
                                    AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                    KLog.i("消息数据删除")
                                }
                            }
                        }
                        var  toSendMessage = toSendChatFileMessage
                        if(toSendMessage != null)
                        {
                            for (item in toSendMessage)
                            {
                                if(item.msgId.equals(msgId))
                                {
                                    toSendMessage.remove(item)
                                    break
                                }
                            }
                        }

                        EventBus.getDefault().post(FileTransformStatus(msgId!!, LogIdIdResult.toString(),ToIdResult,1))
                        KLog.i("websocket文件发送成功！")
                    } else {
                        val msgData = DelMsgReq(FromIdResult, ToIdResult, LogIdIdResult, "DelMsg")
                        AppConfig.instance.getPNRouterServiceMessageSender().send(BaseData(msgData))
                        var messageEntityList = AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.loadAll()
                        if(messageEntityList != null)
                        {
                            messageEntityList.forEach {
                                if (it.msgId.equals(msgId)) {
                                    AppConfig.instance.mDaoMaster!!.newSession().messageEntityDao.delete(it)
                                    KLog.i("消息数据删除")
                                }
                            }
                        }
                        EventBus.getDefault().post(FileTransformStatus(msgId!!,LogIdIdResult.toString(),ToIdResult, 0))
                        KLog.i("websocket文件发送成功后取消！")
                    }
                    val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                    EventBus.getDefault().post(FileTransformEntity(msgId!!, 4, "", wssUrl, "lws-pnr-bin"))


                }
            }
            else -> {
                EventBus.getDefault().post(FileTransformStatus(msgId!!,LogIdIdResult.toString(),ToIdResult, 0))
                val userId = SpUtil.getString(AppConfig.instance, ConstantValue.userId, "")
                SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + ToIdResult, "")
            }
        }
    }

    private fun sendFileByteData(fileLeftBuffer: ByteArray, fileName: String, From: String, To: String, msgId: String, fileId: Int, segSeq: Int, fileKey: String, SrcKey: ByteArray, DstKey: ByteArray) {
        val FriendPublicKey = sendFileFriendKeyMap[msgId]
        //String fileKey =  RxEncryptTool.generateAESKey();
        /*  byte[] my = RxEncodeTool.base64Decode(ConstantValue.INSTANCE.getPublicRAS());
        byte[] friend = RxEncodeTool.base64Decode(FriendPublicKey);
        byte[] SrcKey = new byte[256];
        byte[] DstKey = new byte[256];*/
        try {
            /*  SrcKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(fileKey.getBytes(),my));
            DstKey = RxEncodeTool.base64Encode( RxEncryptTool.encryptByPublicKey(fileKey.getBytes(),friend));*/
            KLog.i("发送中>>>刚调用From:$From  To:$To")
            val MsgType = fileName.substring(fileName.lastIndexOf(".") + 1)
            var action = 1
            when (MsgType) {
                "png", "jpg" -> action = 1
                "amr" -> action = 2
                "mp4" -> action = 4
                else -> action = 5
            }
            val sendFileData = SendFileData()
            val segSize = if (fileLeftBuffer.size > sendFileSizeMax) sendFileSizeMax else fileLeftBuffer.size
            sendFileData.magic = FormatTransfer.reverseInt(0x0dadc0de)
            sendFileData.action = FormatTransfer.reverseInt(action)
            sendFileData.segSize = FormatTransfer.reverseInt(segSize)
            val aa = FormatTransfer.reverseInt(9437440)
            sendFileData.segSeq = FormatTransfer.reverseInt(segSeq)
            var fileOffset = 0
            fileOffset = (segSeq - 1) * sendFileSizeMax
            sendFileData.fileOffset = FormatTransfer.reverseInt(fileOffset)
            sendFileData.fileId = FormatTransfer.reverseInt(fileId)
            sendFileData.crc = FormatTransfer.reverseShort(0.toShort())
            val segMore = if (fileLeftBuffer.size > sendFileSizeMax) 1 else 0
            sendFileData.segMore = segMore.toByte()
            sendFileData.cotinue = 0.toByte()
            //String strBase64 = RxEncodeTool.base64Encode2String(fileName.getBytes());
            val strBase58 = Base58.encode(fileName.toByteArray())
            sendFileData.fileName = strBase58.toByteArray()
            sendFileData.fromId = From.toByteArray()
            sendFileData.toId = To.toByteArray()
            sendFileData.srcKey = SrcKey
            sendFileData.dstKey = DstKey
            val content = ByteArray(sendFileSizeMax)
            System.arraycopy(fileLeftBuffer, 0, content, 0, segSize)
            sendFileData.content = content
            var sendData = sendFileData.toByteArray()
            //int newCRC = CRC16Util.getCRC(sendData,sendData.length);
            val newCRC = 1
            sendFileData.crc = FormatTransfer.reverseShort(newCRC.toShort())
            sendData = sendFileData.toByteArray()
            sendFileNameMap[fileId.toString() + ""] = fileName
            sendFileLastByteSizeMap[fileId.toString() + ""] = segSize
            sendFileLeftByteMap[fileId.toString() + ""] = fileLeftBuffer
            sendMsgIdMap[fileId.toString() + ""] = msgId
            sendFileKeyByteMap[fileId.toString() + ""] = fileKey
            sendFileMyKeyByteMap[fileId.toString() + ""] = SrcKey
            sendFileFriendKeyByteMap[fileId.toString() + ""] = DstKey
            //KLog.i("发送中>>>内容"+"content:"+aabb);
            KLog.i("发送中>>>" + "segMore:" + segMore + "  " + "segSize:" + segSize + "   " + "left:" + (fileLeftBuffer.size - segSize) + "  segSeq:" + segSeq + "  fileOffset:" + fileOffset + "  setSegSize:" + sendFileData.segSize + " CRC:" + newCRC)
            EventBus.getDefault().post(TransformFileMessage(msgId, sendData))

        } catch (e: Exception) {
            val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
            EventBus.getDefault().post(FileTransformEntity(msgId, 4, "", wssUrl, "lws-pnr-bin"))
        }

    }
    fun sendImageMessage(userId: String, friendId: String, files_dir: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+files_dir)
            return;
        }
        Thread(Runnable {
            try {
                val file = File(files_dir)
                val isHas = file.exists()
                if (isHas) {
                    val fileName = (System.currentTimeMillis() / 1000).toInt().toString() + "_" + files_dir.substring(files_dir.lastIndexOf("/") + 1)
                    val message = EMMessage.createImageSendMessage(files_dir, true, friendId)

                    message.from = userId
                    message.to = friendId
                    message.isDelivered = true
                    message.isAcked = false
                    message.isUnread = true

                    if (ConstantValue.curreantNetworkType == "WIFI") {
                        message.msgId = msgId
                        ConstantValue.sendFileMsgMap[msgId] = message
                        ConstantValue.sendFileMsgTimeMap[msgId] =  System.currentTimeMillis().toString()
                        sendMsgLocalMap.put(msgId, false)
                        sendFilePathMap.put(msgId, files_dir)
                        deleteFileMap.put(msgId, false)
                        sendFileFriendKeyMap.put(msgId, friendSignPublicKey)

                        val fileKey = RxEncryptTool.generateAESKey()
                        val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                        val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                        var SrcKey = ByteArray(256)
                        var DstKey = ByteArray(256)
                        try {
                            if (ConstantValue.encryptionType == "1") {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey))
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                            }
                            sendFileKeyByteMap.put(msgId, fileKey.substring(0, 16))
                            sendFileMyKeyByteMap.put(msgId, SrcKey)
                            sendFileFriendKeyByteMap.put(msgId, DstKey)
                        } catch (e: Exception) {
                            //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show();
                            return@Runnable
                        }

                        val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                        EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"))

                    }
                    val gson = Gson()
                    val Message = Message()
                    Message.msgType = 1
                    Message.fileName = fileName
                    Message.msg = ""
                    Message.from = userId
                    Message.to = friendId
                    Message.timeStatmp = System.currentTimeMillis()
                    Message.unReadCount = 0
                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)
                }

            } catch (e: Exception) {

            }
        }).start()

    }

    fun sendVoiceMessage(userId: String, friendId: String, files_dir: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String, length: Int) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+files_dir)
            return;
        }
        try {
            val file = File(files_dir)
            val isHas = file.exists()
            if (isHas) {
                val fileName = files_dir.substring(files_dir.lastIndexOf("/") + 1)
                val message = EMMessage.createVoiceSendMessage(files_dir, length, friendId)
                message.from = userId
                message.to = friendId
                message.isDelivered = true
                message.isAcked = false
                message.isUnread = true
                if (ConstantValue.curreantNetworkType == "WIFI") {
                    message.msgId = msgId
                    ConstantValue.sendFileMsgMap[msgId] = message
                    ConstantValue.sendFileMsgTimeMap[msgId] =  System.currentTimeMillis().toString()
                    sendMsgLocalMap[msgId] = false
                    sendFilePathMap[msgId] = files_dir
                    deleteFileMap[msgId] = false
                    sendFileFriendKeyMap[msgId] = friendSignPublicKey

                    val fileKey = RxEncryptTool.generateAESKey()
                    val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                    val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                    var SrcKey = ByteArray(256)
                    var DstKey = ByteArray(256)
                    try {

                        if (ConstantValue.encryptionType == "1") {
                            SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                            DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey))
                        } else {
                            SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                            DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                        }
                        sendFileKeyByteMap[msgId] = fileKey.substring(0, 16)
                        sendFileMyKeyByteMap[msgId] = SrcKey
                        sendFileFriendKeyByteMap[msgId] = DstKey
                    } catch (e: Exception) {
                        //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show()
                        return
                    }


                    val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                    EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"))
                }
                val gson = Gson()
                val Message = Message()
                Message.msgType = 2
                Message.fileName = fileName
                Message.msg = ""
                Message.from = userId
                Message.to = friendId
                Message.timeStatmp = System.currentTimeMillis()
                Message.unReadCount = 0
                val baseDataJson = gson.toJson(Message)
                SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)

            }
        } catch (e: Exception) {

        }

    }

    fun sendVideoMessage(userId: String, friendId: String, files_dir: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+files_dir)
            return;
        }
        Thread(Runnable {
            try {
                val file = File(files_dir)
                val isHas = file.exists()
                if (isHas) {
                    val videoFileName = files_dir.substring(files_dir.lastIndexOf("/") + 1)
                    val videoName = files_dir.substring(files_dir.lastIndexOf("/") + 1, files_dir.lastIndexOf(".") + 1)
                    val thumbPath = PathUtils.getInstance().imagePath.toString() + "/" + videoName + ".png"
                    val bitmap = EaseImageUtils.getVideoPhoto(files_dir)
                    val videoLength = EaseImageUtils.getVideoDuration(files_dir)
                    val message = EMMessage.createVideoSendMessage(files_dir, thumbPath, videoLength, friendId)

                    message.from = userId
                    message.to = friendId
                    message.isDelivered = true
                    message.isAcked = false
                    message.isUnread = true

                    if (ConstantValue.curreantNetworkType == "WIFI") {

                        message.msgId = msgId

                        ConstantValue.sendFileMsgMap[msgId] = message
                        ConstantValue.sendFileMsgTimeMap[msgId] =  System.currentTimeMillis().toString()
                        sendMsgLocalMap[msgId] = false
                        sendFilePathMap[msgId] = files_dir
                        deleteFileMap[msgId] = false
                        sendFileFriendKeyMap[msgId] = friendSignPublicKey

                        val fileKey = RxEncryptTool.generateAESKey()
                        val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                        val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                        var SrcKey = ByteArray(256)
                        var DstKey = ByteArray(256)
                        try {

                            if (ConstantValue.encryptionType == "1") {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey))
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                            }
                            sendFileKeyByteMap[msgId] = fileKey.substring(0, 16)
                            sendFileMyKeyByteMap[msgId] = SrcKey
                            sendFileFriendKeyByteMap[msgId] = DstKey
                        } catch (e: Exception) {
                            //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show()
                            return@Runnable
                        }

                        val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                        EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"))

                    }
                    val gson = Gson()
                    val Message = Message()
                    Message.msgType = 4
                    Message.fileName = videoFileName
                    Message.msg = ""
                    Message.from = userId
                    Message.to = friendId
                    Message.timeStatmp = System.currentTimeMillis()
                    Message.unReadCount = 0
                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)

                } else {
                    //Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show()
                }
            } catch (e: Exception) {

            }
        }).start()


    }

    fun sendFileMessage(userId: String, friendId: String, filePath: String, msgId: String, friendSignPublicKey: String, friendMiPublicKey: String) {
        val EMMessageData = ConstantValue.sendFileMsgMap[msgId]
        if(EMMessageData != null && !EMMessageData!!.from.equals(""))
        {
            KLog.i("检测到文件发送中:"+filePath)
            return;
        }
        Thread(Runnable {
            try {
                val file = File(filePath)
                val isHas = file.exists()
                if (isHas) {
                    val fileName = (System.currentTimeMillis() / 1000).toInt().toString() + "_" + filePath.substring(filePath.lastIndexOf("/") + 1)

                    val files_dir = PathUtils.getInstance().imagePath.toString() + "/" + fileName
                    val message = EMMessage.createFileSendMessage(filePath, friendId)

                    message.from = userId
                    message.to = friendId
                    message.isDelivered = true
                    message.isAcked = false
                    message.isUnread = true

                    if (ConstantValue.curreantNetworkType == "WIFI") {

                        message.msgId = msgId
                        ConstantValue.sendFileMsgMap[msgId] = message
                        ConstantValue.sendFileMsgTimeMap[msgId] =  System.currentTimeMillis().toString()
                        sendMsgLocalMap[msgId] = false
                        sendFilePathMap[msgId] = files_dir
                        deleteFileMap[msgId] = false
                        sendFileFriendKeyMap[msgId] = friendSignPublicKey

                        val fileKey = RxEncryptTool.generateAESKey()
                        val my = RxEncodeTool.base64Decode(ConstantValue.publicRAS)
                        val friend = RxEncodeTool.base64Decode(friendSignPublicKey)
                        var SrcKey = ByteArray(256)
                        var DstKey = ByteArray(256)
                        try {

                            if (ConstantValue.encryptionType == "1") {
                                SrcKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, ConstantValue.libsodiumpublicMiKey!!))
                                DstKey = RxEncodeTool.base64Encode(LibsodiumUtil.EncryptShareKey(fileKey, friendMiPublicKey))
                            } else {
                                SrcKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), my))
                                DstKey = RxEncodeTool.base64Encode(RxEncryptTool.encryptByPublicKey(fileKey.toByteArray(), friend))
                            }
                            sendFileKeyByteMap[msgId] = fileKey.substring(0, 16)
                            sendFileMyKeyByteMap[msgId] = SrcKey
                            sendFileFriendKeyByteMap[msgId] = DstKey
                        } catch (e: Exception) {
                            //Toast.makeText(getActivity(), R.string.Encryptionerror, Toast.LENGTH_SHORT).show()
                            return@Runnable
                        }

                        val wssUrl = "https://" + ConstantValue.currentIp + ConstantValue.filePort
                        EventBus.getDefault().post(FileTransformEntity(msgId, 0, "", wssUrl, "lws-pnr-bin"))
                    }
                    FileUtil.copySdcardFile(filePath, files_dir)
                    val gson = Gson()
                    val Message = Message()
                    Message.msgType = 5
                    Message.fileName = fileName
                    Message.msg = ""
                    Message.from = userId
                    Message.to = friendId
                    Message.timeStatmp = System.currentTimeMillis()
                    Message.unReadCount = 0
                    val baseDataJson = gson.toJson(Message)
                    SpUtil.putString(AppConfig.instance, ConstantValue.message + userId + "_" + friendId, baseDataJson)

                } else {
                    //Toast.makeText(getActivity(), R.string.nofile, Toast.LENGTH_SHORT).show()
                }

            } catch (e: Exception) {

            }
        }).start()
    }
}