package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sowoon.databinding.FragmentMainBinding
import com.example.sowoon.databinding.FragmentShowBinding

class ShowFragment : Fragment() {

    lateinit var binding: FragmentShowBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentShowBinding.inflate(inflater, container, false)
        return binding.root
    }




}