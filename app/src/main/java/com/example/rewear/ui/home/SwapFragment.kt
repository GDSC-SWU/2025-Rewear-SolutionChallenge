package com.example.rewear.ui.home


import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.R
import com.example.rewear.databinding.FragmentSwapBinding

class SwapFragment : Fragment() {

    private var _binding: FragmentSwapBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationList: List<Notifications>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSwapBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationList = listOf(
            Notifications(
                type = NotificationType.SWAP_REQUEST,
                userName = "Lee",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            ),
            Notifications(
                type = NotificationType.SWAP_REQUEST,
                userName = "Hayan",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            ),
            Notifications(
                type = NotificationType.SWAP_SUCCESSFUL,
                userName = "hyo",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            ),
            Notifications(
                type = NotificationType.SWAP_SUCCESSFUL,
                userName = "Lee",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            ),
            Notifications(
                type = NotificationType.SWAP_FAILED,
                userName = "Amy",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            )
        )
        notificationAdapter = NotificationAdapter(notificationList)
        binding.notificationSwapRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationSwapRecyclerView.adapter = notificationAdapter

        val divider=DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(),R.drawable.recycler_divider)?.let {
            divider.setDrawable(it)
        }
        binding.notificationSwapRecyclerView.addItemDecoration(divider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

