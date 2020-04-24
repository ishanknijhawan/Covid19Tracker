package com.ishanknijhawan.covid.ui


import android.content.Context
import android.content.Context.INPUT_METHOD_SERVICE
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat.getSystemService
import androidx.core.view.contains
import androidx.core.view.size
import androidx.core.widget.NestedScrollView
import androidx.fragment.app.Fragment
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.ishanknijhawan.covid.R
import com.ishanknijhawan.covid.NotificationWorker
import com.ishanknijhawan.covid.adapter.StateListAdapter
import com.ishanknijhawan.covid.client.Client
import com.ishanknijhawan.covid.client.Response
import com.ishanknijhawan.covid.client.StatewiseItem
import kotlinx.android.synthetic.main.fragment_india.*
import kotlinx.coroutines.*
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEvent
import net.yslibrary.android.keyboardvisibilityevent.KeyboardVisibilityEventListener
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class IndiaFragment : Fragment() {
    lateinit var stateListAdapter: StateListAdapter
    lateinit var frConfirm: TextView
    lateinit var frRecover: TextView
    lateinit var frDeath: TextView
    lateinit var frDeltaConfirm: TextView
    lateinit var frDeltaRecover: TextView
    lateinit var frDeltaDeath: TextView
    lateinit var swipeToRefreshX: SwipeRefreshLayout
    lateinit var lastUpdated: TextView
    lateinit var listIndia: ListView
    lateinit var searchView: ImageButton
    lateinit var bottomSheet: NestedScrollView
    lateinit var bs: BottomSheetBehavior<NestedScrollView>
    lateinit var etsearch: EditText

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_india, container, false)
        val cardView2 = rootView.findViewById<CardView>(R.id.card2)
        val pb = rootView.findViewById<ProgressBar>(R.id.progress_circular2)
        listIndia = rootView.findViewById(R.id.list2)

        swipeToRefreshX = rootView.findViewById(R.id.swipeToRefresh2)

        frConfirm = rootView.findViewById(R.id.confirmedTv2)
        frRecover = rootView.findViewById(R.id.recoveredTv2)
        frDeath = rootView.findViewById(R.id.deceasedTv2)
        frDeltaConfirm = rootView.findViewById(R.id.deltaConfirmedTv2)
        frDeltaRecover = rootView.findViewById(R.id.deltaRecoveredTv2)
        frDeltaDeath = rootView.findViewById(R.id.deltaDeathsTv2)

        lastUpdated = rootView.findViewById(R.id.lastUpdatedTv2)
        searchView = rootView.findViewById(R.id.search_badge2)
        bottomSheet = rootView.findViewById(R.id.bottomSheet)
        etsearch = rootView.findViewById(R.id.etSearch)
        bs = BottomSheetBehavior.from(bottomSheet)

        cardView2.elevation = 16F
        pb.visibility = View.GONE
        listIndia.addHeaderView(
            LayoutInflater.from(requireContext()).inflate(
                R.layout.list_header,
                listIndia,
                false
            )
        )

        fetchResults()
        swipeToRefreshX.setOnRefreshListener {
            fetchResults()
        }
        initWorker()
        listIndia.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (listIndia.getChildAt(0) != null) {
                    swipeToRefreshX.isEnabled =
                        listIndia.firstVisiblePosition === 0 && listIndia.getChildAt(
                            0
                        ).getTop() === 0
                }
            }
        })

        return rootView
    }

    @InternalCoroutinesApi
    private fun initWorker() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val notificationWorkRequest =
            PeriodicWorkRequestBuilder<NotificationWorker>(1, TimeUnit.HOURS)
                .setConstraints(constraints)
                .build()

        WorkManager.getInstance(requireContext()).enqueueUniquePeriodicWork(
            "JOB_TAG",
            ExistingPeriodicWorkPolicy.KEEP,
            notificationWorkRequest
        )
    }

    private fun fetchResults() {
        GlobalScope.launch {
            val response = withContext(Dispatchers.IO) { Client.api.clone().execute() }

            if (response.isSuccessful) {
                swipeToRefreshX.isRefreshing = false
                val data = Gson().fromJson(response.body?.string(), Response::class.java)
                launch(Dispatchers.Main) {
                    bindCombinedData(data.statewise[0])

                    bindStateWiseData(data.statewise.subList(1, data.statewise.size))
                }
            }
        }
    }

    private fun bindStateWiseData(subList: List<StatewiseItem>) {
        stateListAdapter =
            StateListAdapter(subList)
        listIndia.adapter = stateListAdapter

        searchView.setOnClickListener {
            //bs.state = BottomSheetBehavior.STATE_EXPANDED
            etsearch.requestFocus()
            val imm =
                requireContext().getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager?
            imm!!.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0)
        }

//        KeyboardVisibilityEvent.setEventListener(requireActivity(),
//            object : KeyboardVisibilityEventListener{
//                override fun onVisibilityChanged(isOpen: Boolean) {
//                    bs.state = BottomSheetBehavior.STATE_COLLAPSED
//                    //Toast.makeText(requireContext(), "works", Toast.LENGTH_SHORT).show()
//                }
//
//            })

        val stringList = mutableListOf<String>()
        val searchList = mutableListOf<StatewiseItem>()

        for (i in subList.indices)
            stringList.add(subList[i].state!!.trim().toLowerCase(Locale.ENGLISH))

        Log.i("TAG", stringList.toString())

        etsearch.setOnFocusChangeListener { view, b ->
            if (b){
                bs.state = BottomSheetBehavior.STATE_EXPANDED
            }
            else {
                bs.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        etsearch.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length > 2){
                    searchList.clear()
                    for (i in stringList.indices){
                        if (stringList[i].contains(p0.toString().trim().toLowerCase(Locale.ENGLISH))){
                            searchList.add(subList[i])
                        }
                    }
                    stateListAdapter = StateListAdapter(searchList)
                    listIndia.adapter = stateListAdapter
                    Log.i("TAG", searchList.toString())
                }
                else {
                    stateListAdapter =
                        StateListAdapter(subList)
                    listIndia.adapter = stateListAdapter
                }
            }

        })
    }

    private fun bindCombinedData(data: StatewiseItem) {
        val lastUpdatedTime = data.lastupdatedtime
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm:ss")
        lastUpdated.text = "Last Updated ${getTimeAgo(
            simpleDateFormat.parse(lastUpdatedTime)
        )}"
        //🡡

        frConfirm.text = String.format("%,d",data.confirmed!!.toInt())
        frRecover.text = String.format("%,d",data.recovered!!.toInt())
        frDeath.text = String.format("%,d",data.deaths!!.toInt())

        frDeltaConfirm.text = "↑${String.format("%,d",data.deltaconfirmed!!.toInt())}"
        frDeltaRecover.text = "↑${String.format("%,d",data.deltarecovered!!.toInt())}"
        frDeltaDeath.text = "↑${String.format("%,d",data.deltadeaths!!.toInt())}"

    }


    fun getTimeAgo(past: Date): String {
        val now = Date()
        val seconds = TimeUnit.MILLISECONDS.toSeconds(now.time - past.time)
        val minutes = TimeUnit.MILLISECONDS.toMinutes(now.time - past.time)
        val hours = TimeUnit.MILLISECONDS.toHours(now.time - past.time)

        return when {
            seconds < 60 -> {
                "Few seconds ago"
            }
            minutes < 60 -> {
                "$minutes minutes ago"
            }
            hours < 24 -> {
                "$hours hour ${minutes % 60} min ago"
            }
            else -> {
                SimpleDateFormat("dd/MM/yy, hh:mm a").format(past).toString()
            }
        }

    }

}
