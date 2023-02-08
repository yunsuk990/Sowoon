package com.example.sowoon

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sowoon.database.AppDatabase
import com.example.sowoon.databinding.FragmentSettingMyLikeBinding

class SettingMyLikeFragment : Fragment() {

    lateinit var binding: FragmentSettingMyLikeBinding
    lateinit var database: AppDatabase

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingMyLikeBinding.inflate(inflater, container, false)
        return binding.root

        database = AppDatabase.getInstance(requireContext())!!
    }

    private fun initGridView(){
        database.
        var adapter = MyLikeGVAdapter()
        binding.myLikeGv.adapter = adapter


    }

}