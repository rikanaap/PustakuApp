package com.example.pustaku.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import com.example.pustaku.R
import com.example.pustaku.databinding.PageMainBinding
import com.example.pustaku.fragment.main.AddFragment
import com.example.pustaku.fragment.main.FavoriteFragment
import com.example.pustaku.fragment.main.HomePage
import com.example.pustaku.fragment.main.MoreFragment

class PageMain : AppCompatActivity() {
    private lateinit var binding : PageMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = PageMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        replaceFragment(HomePage())

        binding.bottomNavigationBar.setOnItemSelectedListener {
            when(it.itemId){
                R.id.home -> replaceFragment(HomePage())
                R.id.favorite -> replaceFragment(FavoriteFragment())
                R.id.add -> replaceFragment(AddFragment())
                R.id.more -> replaceFragment(MoreFragment())
                else -> {}
            }
            true
        }

    }

    private fun replaceFragment(fragment : Fragment) {
        val fragmentManager = supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frameFragmentContainer, fragment)
        fragmentTransaction.commit()
    }
}