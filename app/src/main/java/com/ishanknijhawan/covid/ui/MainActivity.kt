package com.ishanknijhawan.covid.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.ishanknijhawan.covid.R
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.coroutines.*

class MainActivity : AppCompatActivity() {

    @InternalCoroutinesApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tab_layout.setupWithViewPager(view_pager)

        val viewpageAdapter =
            ViewpageAdapter(
                supportFragmentManager,
                0
            )
        viewpageAdapter.addFragment(IndiaFragment(), "India")
        viewpageAdapter.addFragment(WorldFragment(), "World")
        view_pager.adapter = viewpageAdapter

//        listGlobal.visibility = View.GONE
//        progress_circular.visibility = View.GONE
//        list.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_header, list, false))
//        listGlobal.addHeaderView(LayoutInflater.from(this).inflate(R.layout.list_header2, list, false))
//
//        switch2.setOnClickListener {
//            if (switch2.isChecked){
//                cancel()
//                list.visibility = View.GONE
//                listGlobal.visibility = View.VISIBLE
//                progress_circular.visibility = View.VISIBLE
//                fetchResultsGlobal()
//            }
//            else {
//                cancel()
//                list.visibility = View.VISIBLE
//                listGlobal.visibility = View.GONE
//                //fetchResults()
//            }
//        }
//
//        //fetchResults()
//        swipeToRefresh.setOnRefreshListener {
//            if (switch2.isChecked)
//                fetchResultsGlobal()
//            //else
//                //fetchResults()
//        }
//        initWorker()
//        list.setOnScrollListener(object : AbsListView.OnScrollListener {
//            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
//            override fun onScroll(
//                view: AbsListView,
//                firstVisibleItem: Int,
//                visibleItemCount: Int,
//                totalItemCount: Int
//            ) {
//                if (list.getChildAt(0) != null) {
//                    swipeToRefresh.isEnabled = list.firstVisiblePosition === 0 && list.getChildAt(
//                        0
//                    ).getTop() === 0
//                }
//            }
//        })
//
//        listGlobal.setOnScrollListener(object : AbsListView.OnScrollListener {
//            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
//            override fun onScroll(
//                view: AbsListView,
//                firstVisibleItem: Int,
//                visibleItemCount: Int,
//                totalItemCount: Int
//            ) {
//                if (listGlobal.getChildAt(0) != null) {
//                    swipeToRefresh.isEnabled = list.firstVisiblePosition === 0 && listGlobal.getChildAt(
//                        0
//                    ).getTop() === 0
//                }
//            }
//        })
    }

//    private fun fetchResultsGlobal() {
//        GlobalScope.launch {
//            Log.d("tag2", "reaching here first")
//            val globalresponse = withContext(Dispatchers.IO) { GlobalClient.apiGlobal.clone().execute() }
//
//            if (globalresponse.isSuccessful){
//                Log.d("tag2", "reaching here")
//
//                swipeToRefresh.isRefreshing = false
//                val datag = Gson().fromJson(globalresponse.body?.string(), GlobalResponse::class.java)
//                //val datat = Gson().fromJson(globalresponse.body?.string(), ResponseTotal::class.java)
//                launch(Dispatchers.Main) {
//                    //bindCombinedData(data.statewise[0])
//                    progress_circular.visibility = View.GONE
//                    //val active = datat.global.totalConfirmed - datat.global.totalDeaths - datat.global.totalRecovered
//                    bindCountryWiseData(datag.countries.subList(0, datag.countries.size))
//
//                    var confirmed = 0
//                    var deaths = 0
//                    var active = 0
//                    var recovered = 0
//                    for (i in datag.countries.indices){
//                        confirmed += datag.countries[i].totalConfirmed
//                        deaths += datag.countries[i].totalDeaths
//                        recovered += datag.countries[i].totalRecovered
//                    }
//                    active = confirmed - deaths - recovered
//
//                    confirmedTv.text = confirmed.toString()
//                    activeTv.text = active.toString()
//                    recoveredTv.text = recovered.toString()
//                    deceasedTv.text = deaths.toString()
//                }
//            }
//        }
//    }
//
//    private fun fetchResults() {
//        GlobalScope.launch {
//            val response = withContext(Dispatchers.IO) { Client.api.clone().execute() }
//
//            if (response.isSuccessful) {
//                swipeToRefresh.isRefreshing = false
//                val data = Gson().fromJson(response.body?.string(), Response::class.java)
//                launch(Dispatchers.Main) {
//                    bindCombinedData(data.statewise[0])
//                    bindStateWiseData(data.statewise.subList(0, data.statewise.size))
//                }
//            }
//        }
//    }
//
//    private fun bindCountryWiseData(subList: List<Country>) {
//        val finalList = subList.toMutableList()
//        finalList.sortByDescending { it.totalConfirmed }
//        countryAdapter =
//            CountryAdapter(finalList)
//        listGlobal.adapter = countryAdapter
//    }
//
//    private fun bindStateWiseData(subList: List<StatewiseItem>) {
//        stateListAdapter =
//            StateListAdapter(subList)
//        list.adapter = stateListAdapter
//    }
//
//    private fun bindCombinedData(data: StatewiseItem) {
//        val lastUpdatedTime = data.lastupdatedtime
//        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
//        lastUpdatedTv.text = "Last Updated ${getTimeAgo(
//            simpleDateFormat.parse(lastUpdatedTime)
//        )}"
//
//        confirmedTv.text = data.confirmed
//        activeTv.text = data.active
//        recoveredTv.text = data.recovered
//        deceasedTv.text = data.deaths
//
//    }
//
//    @InternalCoroutinesApi
//    private fun initWorker() {
//        val constraints = Constraints.Builder()
//            .setRequiredNetworkType(NetworkType.CONNECTED)
//            .build()
//
//        val notificationWorkRequest =
//            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
//                .setConstraints(constraints)
//                .build()
//
//        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
//            "JOB_TAG",
//            ExistingPeriodicWorkPolicy.KEEP,
//            notificationWorkRequest
//        )
//    }
//
//    override fun onDestroy() {
//        super.onDestroy()
//        cancel()
//    }
//
//}
//
//fun getTimeAgo(past: Date): String {
//    val now = Date()
//    val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
//    val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
//    val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)
//
//    return when {
//        seconds < 60 -> {
//            "Few seconds ago"
//        }
//        minutes < 60 -> {
//            "$minutes minutes ago"
//        }
//        hours < 24 -> {
//            "$hours hour ${minutes % 60} min ago"
//        }
//        else -> {
//            SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
//        }
//    }

    class ViewpageAdapter(fm: FragmentManager,behavior: Int)
        : FragmentPagerAdapter(fm, behavior) {

        val fragments = arrayListOf<Fragment>()
        val fragmentTitle = arrayListOf<String>()

        fun addFragment(fragment: Fragment, title: String){
            fragments.add(fragment)
            fragmentTitle.add(title)
        }

        override fun getItem(position: Int) = fragments[position]

        override fun getCount() = fragments.size

        override fun getPageTitle(position: Int): CharSequence? {
            return fragmentTitle[position]
        }

    }
}
