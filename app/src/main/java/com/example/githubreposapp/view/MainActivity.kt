package com.example.githubreposapp.view

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.example.githubreposapp.R
import com.example.githubreposapp.model.GithubPR
import com.example.githubreposapp.model.GithubRepo
import com.example.githubreposapp.viewmodel.MainViewModel
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    private val viewModel: MainViewModel by viewModels()

    var token: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        repositoriesSpinner.isEnabled = false
        repositoriesSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("No repositories available")
        )
        repositoriesSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Load PullRequests
                if (parent?.selectedItem is GithubRepo) {
                    val currentRepo = parent.selectedItem as GithubRepo
                    token?.let {
                        viewModel.onLoadPR(currentRepo.owner.login, currentRepo.name, it)
                    }
                }
            }
        }


        prsSpinner.isEnabled = false
        prsSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Please select repository")
        )
        prsSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                // Load comments
                if (parent?.selectedItem is GithubPR) {
                    val githubPR = parent.selectedItem as GithubPR
                    val currentRepo = parent.selectedItem as GithubRepo
                    token?.let {
                        viewModel.onLoadComments(
                            githubPR.user?.login,
                            currentRepo.name,
                            githubPR.number,
                            it
                        )
                    }
                }
            }
        }


        commentsSpinner.isEnabled = false
        commentsSpinner.adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_dropdown_item,
            arrayListOf("Please select PR")
        )


        observeViewModel()
    }

    private fun observeViewModel() {
        viewModel.token.observe(this, Observer {
            if (it.isNotEmpty()) {
                this.token = it
                loadReposButton.isEnabled = true
                Toast.makeText(this@MainActivity, "Auth successful", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this@MainActivity, "Auth failed", Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.repos.observe(this, Observer { reposList ->
            if (!reposList.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    reposList
                )

                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf("User has no repositories")
                )

                repositoriesSpinner.adapter = spinnerAdapter
                repositoriesSpinner.isEnabled = false
            }
        })

        viewModel.pullRequest.observe(this, Observer { prList ->
            if (!prList.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    prList
                )

                prsSpinner.adapter = spinnerAdapter
                prsSpinner.isEnabled = true
            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf("User has no pull requests")
                )

                prsSpinner.adapter = spinnerAdapter
                prsSpinner.isEnabled = false
            }
        })

        viewModel.comments.observe(this, Observer { comments ->
            if (!comments.isNullOrEmpty()) {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    comments
                )

                commentsSpinner.adapter = spinnerAdapter
                commentsSpinner.isEnabled = true
                commentET.isEnabled = true
                postCommentButton.isEnabled = true

            } else {
                val spinnerAdapter = ArrayAdapter(
                    this@MainActivity,
                    android.R.layout.simple_spinner_dropdown_item,
                    arrayListOf("PR has no comments")
                )

                commentsSpinner.adapter = spinnerAdapter
                commentsSpinner.isEnabled = false
                commentET.isEnabled = true
                postCommentButton.isEnabled = true
            }
        })

        viewModel.error.observe(this, Observer { message ->
            Toast.makeText(this@MainActivity, message, Toast.LENGTH_SHORT).show()
        })
    }

    /* passed in android:onClick="onAuthenticate" activity_main.xml */
    fun onAuthenticate(view: View) {
        val oauthUrl = getString(R.string.oauthUrl)
        val clientId = getString(R.string.clientId)
        val callbackUrl = getString(R.string.callbackUrl)
        /* open the default browser */
        val intent = Intent(
            Intent.ACTION_VIEW,
            Uri.parse("$oauthUrl?client_id=$clientId&scope=repo&redirect_uri=$callbackUrl")
        )
        startActivity(intent)
    }

    /* called after the oauth flow, this method will intercept the callback url call from github */
    override fun onResume() {
        super.onResume()
        val uri = intent.data
        val callbackUrl = getString(R.string.callbackUrl)
        if (uri != null && uri.toString().startsWith(callbackUrl)) {
            val code = uri.getQueryParameter("code")
            code?.let {
                val clientId = getString(R.string.clientId)
                val clientSecret = getString(R.string.clientSecret)
                viewModel.getToken(clientId, clientSecret, code)
            }
        }
    }

    fun onLoadRepos(view: View) {

        token?.let {
            viewModel.onLoadRepos(it)
        }
    }

    fun onPostComment(view: View) {

    }

}

