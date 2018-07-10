package com.aidanvii.github

import android.app.Application
import com.aidanvii.github.bindingadapters.DataBindingComponent
import com.aidanvii.github.bindingadapters.GlideImageViewBindingAdapters
import com.aidanvii.github.features.listrepos.di.ListGithubReposModule
import com.aidanvii.github.features.listrepos.di.ListGithubReposModuleProvider
import com.aidanvii.github.features.listrepos.di.GithubReposRepositoryModule
import com.aidanvii.github.utils.logger.AndroidLogger
import com.aidanvii.github.utils.logger.CompositeLogger

class App : Application(), ListGithubReposModuleProvider {

    override fun onCreate() {
        super.onCreate()
        DataBindingComponent(GlideImageViewBindingAdapters()).makeDefaultComponent()
        CompositeLogger += AndroidLogger("AIDAN")
    }

    override val listGithubReposModule by lazy {
        ListGithubReposModule(GithubReposRepositoryModule())
    }
}