package com.aidanvii.github.features.listrepos.presentation

import android.os.Bundle
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aidanvii.github.features.listRepos.databinding.FragmentGithubReposListBinding
import com.aidanvii.github.features.listrepos.di.ListGithubReposModuleProvider
import com.aidanvii.toolbox.arch.viewmodel.ViewModelFactory
import com.aidanvii.toolbox.arch.viewmodel.addTypedFactory
import com.aidanvii.github.utils.appAs
import com.aidanvii.toolbox.arch.viewmodel.viewModelProvider

class GithubReposListFragment : Fragment() {

    val factory: ViewModelFactory by lazy {
        ViewModelFactory.Builder()
            .addTypedFactory(appAs<ListGithubReposModuleProvider>().listGithubReposModule.githubReposListViewModelFactory)
            .build()
    }

    private var binding: FragmentGithubReposListBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ) = FragmentGithubReposListBinding.inflate(inflater, container, true).run {
        binding = this
        root
    }

    override fun onStart() {
        super.onStart()
        binding?.viewModel = activity!!.viewModelProvider(factory)[GithubReposListViewModel::class.java].also {
            it.refresh()
        }
    }

    override fun onStop() {
        super.onStop()
        binding?.viewModel = null
    }

    override fun onDestroyView() {
        super.onDestroyView()
        binding = null
    }
}