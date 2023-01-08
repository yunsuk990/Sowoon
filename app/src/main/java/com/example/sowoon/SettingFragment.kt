package com.example.sowoon

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.sowoon.databinding.FragmentSettingBinding

class SettingFragment : Fragment() {

    lateinit var binding: FragmentSettingBinding
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentSettingBinding.inflate(inflater, container, false)

        initClickListener()
        return binding.root
    }

    private fun initClickListener(){
        binding.settingLoginTv.setOnClickListener {
            startActivity(Intent(context, LoginActivity::class.java))
        }

        binding.settingSignupTv.setOnClickListener {
            startActivity(Intent(context, SignUpActivity::class.java))
        }
    }




}