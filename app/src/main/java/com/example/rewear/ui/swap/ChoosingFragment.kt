package com.example.rewear.ui.swap

import android.graphics.Rect
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.rewear.databinding.FragmentChoosingBinding
import com.example.rewear.R

class ChoosingFragment : Fragment() {

    private var _binding: FragmentChoosingBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentChoosingBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val fortestClickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_choosingFragment)
        }
        binding.BtnClose.setOnClickListener(fortestClickListener)

        binding.BtnClose.setOnClickListener {
            findNavController().popBackStack()
        }

        binding.btnDecline.setOnClickListener {
            findNavController().popBackStack()
        }

        val nextClickListener = View.OnClickListener {
            findNavController().navigate(R.id.action_choosingFragment_to_swapConfirmedFragment)
        }
        binding.btnNext.setOnClickListener(nextClickListener)

        val imageList = listOf(
            R.drawable.skirt,
            R.drawable.sweatpants,
            R.drawable.sweatshirt,
            R.drawable.padding,
        )

        val adapter = SwapClothingAdapter(imageList)

        binding.clothesRecyclerView.layoutManager = GridLayoutManager(requireContext(), 2)
        binding.clothesRecyclerView.addItemDecoration(SpaceItemDecoration(8, 8))
        binding.clothesRecyclerView.adapter = adapter
    }

    class SpaceItemDecoration(private val horizontalSpace: Int, private val verticalSpace: Int) :
        RecyclerView.ItemDecoration() {
        override fun getItemOffsets(
            outRect: Rect,
            view: View,
            parent: RecyclerView,
            state: RecyclerView.State
        ) {
            outRect.left = horizontalSpace
            outRect.right = horizontalSpace
            outRect.top = verticalSpace
            outRect.bottom = verticalSpace
        }
    }
}
