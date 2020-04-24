package com.ishanknijhawan.covid.ui


import android.content.Context
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.widget.NestedScrollView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.work.*
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.gson.Gson
import com.ishanknijhawan.covid.NotificationWorker
import com.ishanknijhawan.covid.R
import com.ishanknijhawan.covid.adapter.CountryAdapter
import com.ishanknijhawan.covid.adapter.StateListAdapter
import com.ishanknijhawan.covid.client.*
import kotlinx.coroutines.*
import java.net.SocketTimeoutException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.TimeUnit


class WorldFragment : Fragment() {
    lateinit var countryAdapter: CountryAdapter
    lateinit var frConfirm: TextView
    lateinit var frRecover: TextView
    lateinit var frDeath: TextView
    lateinit var frDeltaConfirm: TextView
    lateinit var frDeltaRecover: TextView
    lateinit var frDeltaDeath: TextView
    lateinit var swipeToRefreshX: SwipeRefreshLayout
    lateinit var lastUpdated: TextView
    lateinit var listWorld: ListView
    lateinit var pb: ProgressBar
    lateinit var searchView2: ImageButton
    lateinit var bottomSheetW: NestedScrollView
    lateinit var bs2: BottomSheetBehavior<NestedScrollView>
    lateinit var etsearch2: EditText

    @InternalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_world, container, false)
        val cardView = rootView.findViewById<CardView>(R.id.card)
        pb = rootView.findViewById(R.id.progress_circular)
        listWorld = rootView.findViewById(R.id.list)

        swipeToRefreshX = rootView.findViewById(R.id.swipeToRefresh)

        frConfirm = rootView.findViewById(R.id.confirmedTv)
        frRecover = rootView.findViewById(R.id.recoveredTv)
        frDeath = rootView.findViewById(R.id.deceasedTv)
        frDeltaConfirm = rootView.findViewById(R.id.deltaConfirmedTv)
        frDeltaRecover = rootView.findViewById(R.id.deltaRecoveredTv)
        frDeltaDeath = rootView.findViewById(R.id.deltaDeathsTv)

        lastUpdated = rootView.findViewById(R.id.lastUpdatedTv)
        searchView2 = rootView.findViewById(R.id.search_badge)
        bottomSheetW = rootView.findViewById(R.id.bottomSheet2)
        etsearch2 = rootView.findViewById(R.id.etSearch2)
        bs2 = BottomSheetBehavior.from(bottomSheetW)

        cardView.elevation = 16F
        pb.visibility = View.VISIBLE
        listWorld.addHeaderView(
            LayoutInflater.from(requireContext()).inflate(
                R.layout.list_header2,
                listWorld,
                false
            )
        )

        fetchResults()
        swipeToRefreshX.setOnRefreshListener {
            fetchResults()
        }

        listWorld.setOnScrollListener(object : AbsListView.OnScrollListener {
            override fun onScrollStateChanged(view: AbsListView, scrollState: Int) {}
            override fun onScroll(
                view: AbsListView,
                firstVisibleItem: Int,
                visibleItemCount: Int,
                totalItemCount: Int
            ) {
                if (listWorld.getChildAt(0) != null) {
                    swipeToRefreshX.isEnabled =
                        listWorld.firstVisiblePosition === 0 && listWorld.getChildAt(
                            0
                        ).getTop() === 0
                }
            }
        })

        return rootView
    }

    private fun fetchResults() {
        try {
            GlobalScope.launch {
                val response = withContext(Dispatchers.IO) { GlobalClient.apiGlobal.clone().execute() }

                if (response.isSuccessful) {
                    swipeToRefreshX.isRefreshing = false
                    val data = Gson().fromJson(response.body?.string(), GlobalResponse::class.java)
                    launch(Dispatchers.Main) {

                        pb.visibility = View.GONE
                        var confirmed = 0
                        var deaths = 0
                        var active = 0
                        var recovered = 0

                        var dconfirmed = 0
                        var ddeaths = 0
                        var dactive = 0
                        var drecovered = 0

                        for (i in data.countries.indices) {
                            confirmed += data.countries[i].totalConfirmed
                            deaths += data.countries[i].totalDeaths
                            recovered += data.countries[i].totalRecovered

                            dconfirmed += data.countries[i].newConfirmed
                            ddeaths += data.countries[i].newDeaths
                            drecovered += data.countries[i].newRecovered
                        }
                        active = confirmed - deaths - recovered

                        frConfirm.text = String.format("%,d",confirmed)
                        frRecover.text = String.format("%,d",recovered)
                        frDeath.text = String.format("%,d",deaths)
                        frDeltaConfirm.text = "↑${String.format("%,d",dconfirmed)}"
                        frDeltaDeath.text = "↑${String.format("%,d",ddeaths)}"
                        frDeltaRecover.text = "↑${String.format("%,d",drecovered)}"

                        bindStateWiseData(data.countries.subList(0, data.countries.size))
                    }
                }
            }
        }
        catch (e: SocketTimeoutException){
            Toast.makeText(requireContext(),e.printStackTrace().toString(),Toast.LENGTH_LONG).show()
        }
    }

    private fun bindStateWiseData(subList: List<Country>) {
        val finalList = subList.toMutableList()
        finalList.sortByDescending { it.totalConfirmed }
        var date = finalList[0].date

        if (date.contains("T"))
            date = date.replace("T"," ")
        if (date.contains("Z"))
            date = date.replace("Z","")
        if (date.contains("-"))
            date = date.replace("-","/")

        //2020-04-24T19:35:39Z
        val simpleDateFormat = SimpleDateFormat("yyyy/MM/dd HH:mm:ss")
        lastUpdated.text = "Last Updated ${getTimeAgo(
            simpleDateFormat.parse(date)
        )}"
        countryAdapter = CountryAdapter(finalList)
        listWorld.adapter = countryAdapter

        searchView2.setOnClickListener {
            //bs.state = BottomSheetBehavior.STATE_EXPANDED
            etsearch2.requestFocus()
            val imm =
                requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager?
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
        val searchList = mutableListOf<Country>()

        for (i in subList.indices)
            stringList.add(subList[i].slug)

        Log.i("TAG", stringList.toString())

        etsearch2.setOnFocusChangeListener { view, b ->
            if (b){
                bs2.state = BottomSheetBehavior.STATE_EXPANDED
            }
            else {
                bs2.state = BottomSheetBehavior.STATE_COLLAPSED
            }
        }

        etsearch2.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {

            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (p0.toString().length > 2){
                    searchList.clear()
                    for (i in stringList.indices){
                        if (p0.toString().contains(" ")){
                            var temp = p0.toString()
                            temp = temp.replace(" ","-")
                            if (stringList[i].contains(temp.toLowerCase(Locale.ENGLISH))){
                                searchList.add(subList[i])
                            }
                        }
                        else if (stringList[i].contains(p0.toString().toLowerCase(Locale.ENGLISH))){
                            searchList.add(subList[i])
                        }
                    }
                    countryAdapter = CountryAdapter(searchList)
                    listWorld.adapter = countryAdapter
                    Log.i("TAG", searchList.toString())
                }
                else {
                    countryAdapter =
                        CountryAdapter(finalList)
                    listWorld.adapter = countryAdapter
                }
            }

        })

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