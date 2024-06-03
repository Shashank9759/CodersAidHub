package com.community.codersaidhub.UI.fragments

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.community.codersaidhub.R
import com.community.codersaidhub.ViewModel.SharedViewModel
import com.community.codersaidhub.databinding.FragmentSearchBinding
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch


class SearchFragment : Fragment() {
   lateinit var binding: FragmentSearchBinding
    private lateinit var sharedViewModel: SharedViewModel
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding=FragmentSearchBinding.inflate(layoutInflater, container, false)


        sharedViewModel = ViewModelProvider(requireActivity()).get(SharedViewModel::class.java)

        // Observe changes in data and update UI accordingly
        sharedViewModel.data.observe(viewLifecycleOwner, { newData ->
            // Update UI with the new data
        })

         var isfragmentChanged=false
        CoroutineScope(Dispatchers.Main).launch {
            binding.searchUser.addTextChangedListener(object:TextWatcher{

                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {



                }

                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {

                    try{
                        if(!isfragmentChanged){
                            val childFragment = search_ResultProfile_Fragment()
                            childFragmentManager.beginTransaction()
                                .replace(R.id.searchFrameLayout,childFragment)
                                .commitNow()

                            isfragmentChanged=true


                        }
                        sendRealTimeData(s.toString())

                    }
                    catch (e:Exception){
                        Toast.makeText(requireContext(),"Error : $e",Toast.LENGTH_SHORT).show()
                    }




                }

                override fun afterTextChanged(s: Editable?) {



                }

            })


        }




        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        childFragmentManager.beginTransaction()
            .replace(R.id.searchFrameLayout, search_Post_Fragment())
            .commit()




    }

    private fun sendRealTimeData(data: String) {

        sharedViewModel.data.value = data
    }


}