package com.close.hook.ads.ui.adapter

import android.annotation.SuppressLint
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.widget.ThemeUtils
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.close.hook.ads.R
import com.close.hook.ads.data.module.BlockedRequest
import java.text.SimpleDateFormat
import java.util.Date


class BlockedRequestsAdapter(
    private val context: Context,
) : RecyclerView.Adapter<BlockedRequestsAdapter.ViewHolder?>() {


    private val DIFF_CALLBACK: DiffUtil.ItemCallback<BlockedRequest> =
        object : DiffUtil.ItemCallback<BlockedRequest>() {
            override fun areItemsTheSame(oldItem: BlockedRequest, newItem: BlockedRequest): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }

            @SuppressLint("DiffUtilEquals")
            override fun areContentsTheSame(oldItem: BlockedRequest, newItem: BlockedRequest): Boolean {
                return oldItem.hashCode() == newItem.hashCode()
            }
        }

    private val differ: AsyncListDiffer<BlockedRequest> =
        AsyncListDiffer<BlockedRequest>(this, DIFF_CALLBACK)

    fun submitList(list: List<BlockedRequest?>?) {
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.blocked_request_item, parent, false)
        val viewHolder = ViewHolder(view)
        viewHolder.itemView.setOnLongClickListener {
            val clipboardManager =
                parent.context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            ClipData.newPlainText("c001apk text", viewHolder.request.text.toString())
                ?.let { clipboardManager.setPrimaryClip(it) }
            Toast.makeText(parent.context, "已复制: ${viewHolder.request.text}", Toast.LENGTH_SHORT)
                .show()
            true
        }
        return viewHolder
    }

    @SuppressLint("RestrictedApi")
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val request = differ.currentList[position]
        holder.appName.text = request.appName
        holder.request.text = request.request
        if (request.blockType =="all" && request.isBlocked){
            holder.request.setTextColor(ThemeUtils.getThemeAttrColor(context, com.google.android.material.R.attr.colorError))
        }else{
            holder.request.setTextColor(ThemeUtils.getThemeAttrColor(context, com.google.android.material.R.attr.colorControlNormal))
        }
        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
        holder.timestamp.text = sdf.format(Date(request.timestamp))
        holder.icon.setImageDrawable(getAppIcon(request.packageName))
    }

    private fun getAppIcon(packageName: String): Drawable? {
        try {
            val pm: PackageManager = context.packageManager
            val info = pm.getApplicationInfo(packageName, 0)
            return info.loadIcon(pm)
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return null
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var appName: TextView
        var request: TextView
        var timestamp: TextView
        var icon: ImageView

        init {
            appName = view.findViewById(R.id.app_name)
            request = view.findViewById(R.id.request)
            timestamp = view.findViewById(R.id.timestamp)
            icon = view.findViewById(R.id.icon)
        }
    }
}