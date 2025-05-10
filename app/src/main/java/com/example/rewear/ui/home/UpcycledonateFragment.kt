package com.example.rewear.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.rewear.R
import com.example.rewear.databinding.FragmentUpcycledonateBinding

class UpcycledonateFragment : Fragment() {

    private var _binding: FragmentUpcycledonateBinding? = null
    private val binding get() = _binding!!

    private lateinit var notificationAdapter: NotificationAdapter
    private lateinit var notificationList: List<Notifications>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentUpcycledonateBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        notificationList = listOf(
            Notifications(
                type = NotificationType.UPCYCLE,
                userName = "Lee",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            ),
            Notifications(
                type = NotificationType.UPCYCLE,
                userName = "Hayan",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            ),
            Notifications(
                type = NotificationType.UPCYCLE,
                userName = "hyo",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            ),
            Notifications(
                type = NotificationType.UPCYCLE,
                userName = "Lee",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            ),
            Notifications(
                type = NotificationType.UPCYCLE,
                userName = "Amy",
                R.drawable.cloth_example,
                title = "",
                message = "",
                timeAgo = "1m ago"
            )
        )
        notificationAdapter = NotificationAdapter(notificationList){notification ->
            if (isAdded && view != null) {
                findNavController().navigate(R.id.action_upcycledonate_to_donateReformSelection)
            }
        }
        binding.notificationUpcycleRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        binding.notificationUpcycleRecyclerView.adapter = notificationAdapter

        val divider=DividerItemDecoration(context,DividerItemDecoration.VERTICAL)
        ContextCompat.getDrawable(requireContext(),R.drawable.recycler_divider)?.let {
            divider.setDrawable(it)
        }
        binding.notificationUpcycleRecyclerView.addItemDecoration(divider)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}

