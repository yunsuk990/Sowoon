package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sowoon.databinding.FragmentSettingMyLikeBinding

class SettingMyLikeFragment : Fragment() {

    lateinit var binding: FragmentSettingMyLikeBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingMyLikeBinding.inflate(inflater, container, false)
        return binding.root
    }

    private fun initGridView(){
        var adapter = MyLikeGVAdapter()
        binding.myLikeGv.adapter = adapter
        binding.myLikeGv.layout

    }
}