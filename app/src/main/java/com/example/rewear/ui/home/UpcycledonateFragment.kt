package com.example.rewear.ui.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.rewear.R

class UpcycledonateFragment : Fragment() {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_upcycledonate, container, false)
    }

}

private fun newInstant():UpcycledonateFragment
{
    val args=Bundle()
    val frag=UpcycledonateFragment()
    frag.arguments=args
    return frag
}