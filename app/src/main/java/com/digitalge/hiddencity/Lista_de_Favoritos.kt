package com.digitalge.hiddencity


import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.AutocompleteSessionToken
import com.google.android.libraries.places.api.model.TypeFilter
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest
import com.google.android.libraries.places.api.net.PlacesClient

data class PlaceInfo(val name: String, val id: String)

class Lista_de_Favoritos: Fragment(R.layout.fragment_lista_de_favoritos) {
    private lateinit var placesClient: PlacesClient
    private lateinit var adapter: PlacesAdapter
    private lateinit var recyclerView: RecyclerView
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (!Places.isInitialized()) {
            Places.initialize(requireContext(), "AIzaSyBVi-bKsuRs9Av2eLSrAmGprQuxkUqt4Mk")
        }
        placesClient = Places.createClient(requireContext())

        val searchEditText = view.findViewById<EditText>(R.id.search_edit_text)
        recyclerView = view.findViewById(R.id.results_recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(context)
        adapter = PlacesAdapter(emptyList(), this::handlePlaceSelection)
        recyclerView.adapter = adapter

        searchEditText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
            override fun afterTextChanged(s: Editable?) {
                searchPlaces(s.toString())
            }
        })
    }

    private fun handlePlaceSelection(placeInfo: PlaceInfo) {
        val intent = Intent(context, DetalhesLocalActivity::class.java)
        Log.d("PlaceID Check", "Sending placeID: ${placeInfo.id}")
        intent.putExtra("place_id", placeInfo.id)
        startActivity(intent)
    }

    private fun searchPlaces(query: String) {
        val token = AutocompleteSessionToken.newInstance()
        val request = FindAutocompletePredictionsRequest.builder()
            .setTypeFilter(TypeFilter.ESTABLISHMENT)
            .setSessionToken(token)
            .setQuery(query)
            .build()

        placesClient.findAutocompletePredictions(request).addOnSuccessListener { response ->
            val results = response.autocompletePredictions.map {
                PlaceInfo(it.getPrimaryText(null).toString(), it.placeId)
            }
            adapter.updateData(results)
        }.addOnFailureListener { exception ->
            if (exception is ApiException) {
                Log.e("API Error", "Error fetching autocomplete predictions: ${exception.statusCode}")
            }
        }
    }
}