package com.example.customviewdemo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation.findNavController
import androidx.navigation.fragment.findNavController
import com.example.customviewdemo.databinding.FragmentClickBinding


class ClickFragment : Fragment() {

    private val viewModel: SimpleViewModel by activityViewModels() // shared viewmodel across the activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val binding = FragmentClickBinding.inflate(inflater, container, false)

        // click button to navigate
        binding.buttonDraw.setOnClickListener {
            findNavController().navigate(R.id.action_clickFragment_to_drawFragment)
        }


        // setup all three spinners
        setupSpinner(binding.spinnerColor, R.array.color_array)
        setupSpinner(binding.spinnerSize, R.array.size_array)
        setupSpinner(binding.spinnerShape, R.array.shape_array)

        // setup listerner for spinners
        setSpinnerListener(binding.spinnerColor) { viewModel.selectColor(it) }
        setSpinnerListener(binding.spinnerSize) { viewModel.selectSize(it) }
        setSpinnerListener(binding.spinnerShape) { viewModel.selectShape(it) }

        return binding.root
    }

    // setup adapter function
    private fun setupSpinner(spinner: Spinner, arrayResId: Int) {
        val adapter = ArrayAdapter.createFromResource(
            requireContext(), // get main activity from this fragment
            arrayResId, // array id from string.xml under values package
            android.R.layout.simple_spinner_item // layout for each item in spinner
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
    }

    // OnItemSelectedListener
    private fun setSpinnerListener(spinner: Spinner, onItemSelected: (String) -> Unit) {

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                val selectedItem = parent.getItemAtPosition(position).toString()
                onItemSelected(selectedItem)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }
}