package com.example.rewear.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.rewear.databinding.ItemNotificationBinding

class NotificationAdapter(private val notifications:List<Notifications>):
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {

    inner class NotificationViewHolder(val binding: ItemNotificationBinding):
    RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val binding=ItemNotificationBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        val notification=notifications[position]
        val b=holder.binding

        b.notificationImage.setImageResource(notification.imageResId)

        b.notificationTitle.text=String.format(notification.type.title,notification.userName)
        b.notificationMessage.text=String.format(notification.type.message,notification.userName)

        b.notificationTime.text=notification.timeAgo

        b.notificationUnreadDot.visibility= View.VISIBLE
    }

    override fun getItemCount(): Int {
        return notifications.size
    }
}