package com.stratagile.pnrouter.utils

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.TextView
import androidx.core.widget.toast

import com.chad.library.adapter.base.BaseQuickAdapter
import com.pawegio.kandroid.toast
import com.stratagile.pnrouter.R
import com.stratagile.pnrouter.application.AppConfig
import com.stratagile.pnrouter.entity.ShareBean
import com.stratagile.pnrouter.ui.adapter.user.ShareSelfAdapter
import com.stratagile.pnrouter.view.CustomPopWindow

import java.util.ArrayList


/**
 * 作者：hu on 2017/6/8
 * 邮箱：365941593@qq.com
 * 描述：
 */

/**
 * 公共的popwindow弹出类。所有的popwindow都可以封装在这个类里边
 */
object PopWindowUtil {


    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    @JvmOverloads
    fun showPopWindow(activity: Activity, showView: View, clickListener: View.OnClickListener, tipContentStr: String, comfirmContent: String = "", cancalContent: String = "") {
        //        View maskView = LayoutInflater.from(activity).inflate(R.layout.confirm_cancal_layout, null);
        //        View contentView = maskView.findViewById(R.id.ll_popup);
        //        maskView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.fade_in));
        //        contentView.setAnimation(AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in));
        //        //对具体的view的事件的处理
        //        TextView confirm = (TextView) maskView.findViewById(R.id.bt_confirm);
        //        TextView cancal = (TextView) maskView.findViewById(R.id.bt_cancal);
        //        TextView tipContent = (TextView) maskView.findViewById(R.id.tip_content);
        //        if (!"".equals(comfirmContent)) {
        //            confirm.setText(comfirmContent);
        //        }
        //        if (!"".equals(cancalContent)) {
        //            cancal.setText(cancalContent);
        //        }
        //        tipContent.setText(tipContentStr);
        //        maskView.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                CustomPopWindow.onBackPressed();
        //            }
        //        });
        //        tipContent.setOnClickListener(clickListener);
        //        cancal.setOnClickListener(new View.OnClickListener() {
        //            @Override
        //            public void onClick(View view) {
        //                CustomPopWindow.onBackPressed();
        //            }
        //        });
        //
        //        confirm.setOnClickListener(clickListener);
        //
        //        new CustomPopWindow.PopupWindowBuilder(activity)
        //                .setView(maskView)
        //                .setClippingEnable(false)
        //                .setContenView(contentView)
        //                .setFocusable(false)
        //                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
        //                .create()
        //                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0);
    }

    /**
     * @param activity 上下文
     * @param showView 从activity中传进来的view,用于让popWindow附着的
     */
    fun showSharePopWindow(activity: Activity, showView: View) {
        val maskView = LayoutInflater.from(activity).inflate(R.layout.share_pop_layout, null)
        val contentView = maskView.findViewById<View>(R.id.ll_popup)
        maskView.animation = AnimationUtils.loadAnimation(activity, R.anim.fade_in)
        contentView.animation = AnimationUtils.loadAnimation(activity, R.anim.pop_manage_product_in)
        val recyclerView = contentView.findViewById<RecyclerView>(R.id.recyclerView)
        val linearLayoutManager = LinearLayoutManager(activity, RecyclerView.HORIZONTAL, false)
        recyclerView.layoutManager = linearLayoutManager
        val arrayList = ArrayList<ShareBean>()
        arrayList.add(ShareBean("icon_copylink", "Copy Link"))
        arrayList.add(ShareBean("icon_google", "Google"))
        arrayList.add(ShareBean("icon_twitter", "Twitter"))
        arrayList.add(ShareBean("icon_facebook", "Facebook"))
        arrayList.add(ShareBean("icon_linked", "LinkedIn"))
        val shareSelfAdapter = ShareSelfAdapter(arrayList)
        recyclerView.adapter = shareSelfAdapter
        shareSelfAdapter.onItemClickListener = BaseQuickAdapter.OnItemClickListener { adapter, view, position ->
            AppConfig.instance.toast(arrayList[position].name)
            when (arrayList[position].avatar) {
                "icon_copylink" -> {
                }
                "icon_google" -> {
                }
                "icon_twitter" -> {
                }
                "icon_facebook" -> {
                }
                "icon_linked" -> {
                }
                else -> {
                }
            }
        }
        //对具体的view的事件的处理
        maskView.setOnClickListener { CustomPopWindow.onBackPressed() }

        CustomPopWindow.PopupWindowBuilder(activity)
                .setView(maskView)
                .setClippingEnable(false)
                .setContenView(contentView)
                .setFocusable(false)
                .size(UIUtils.getDisplayWidth(activity), UIUtils.getDisplayHeigh(activity))
                .create()
                .showAtLocation(showView, Gravity.NO_GRAVITY, 0, 0)
    }
}
/**
 * @param activity 上下文
 * @param showView 从activity中传进来的view,用于让popWindow附着的
 */