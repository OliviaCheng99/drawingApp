package com.example.drawApp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.coroutines.launch

class MomentsFragment: Fragment() {
    private val momentsViewModel: MomentsViewModel by viewModels()
    private lateinit var momentsAdapter: MomentsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
//        return ComposeView(requireContext()).apply{
//            setContent {
//                MaterialTheme{
//                    Text(text = "moments")
//                }
//            }
//
//        }

        val view = inflater.inflate(R.layout.fragment_moments, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.momentsRecyclerView)
        momentsAdapter = MomentsAdapter(requireContext(), emptyList()) // Initialize with an empty list
        recyclerView.adapter = momentsAdapter
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        momentsViewModel.sharedImages.observe(viewLifecycleOwner, Observer { images ->
            momentsAdapter.updateImages(images)
        })

        // Launch a coroutine to load shared images
        lifecycleScope.launch {
            momentsViewModel.loadSharedImages()
        }

        return view
    }

}