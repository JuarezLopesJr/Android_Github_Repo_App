package com.example.githubreposapp.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.githubreposapp.model.GithubService
import com.example.githubreposapp.model.GithubToken
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.observers.DisposableSingleObserver
import io.reactivex.schedulers.Schedulers

class MainViewModel : ViewModel() {
    /* variable to be able to finish RxJava methods when the viewmodel is destroyed,
    to avoid memory leaks */
    private val compositeDisposable = CompositeDisposable()

    val token = MutableLiveData<String>()
    val error = MutableLiveData<String>()

    fun getToken(clientId: String, clientSecret: String, code: String) {
        compositeDisposable.add(
            GithubService.getUnauthorizedApi().getAuthToken(clientId, clientSecret, code)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeWith(object : DisposableSingleObserver<GithubToken>() {
                    override fun onSuccess(t: GithubToken) {
                        token.value = t.accessToken
                    }

                    override fun onError(e: Throwable) {
                        e.printStackTrace()
                        error.value = "Can't load token"
                    }
                })
        )
    }

    /* avoiding memory leaks by destroying RxJava */
    override fun onCleared() {
        super.onCleared()
        compositeDisposable.clear()
    }
}