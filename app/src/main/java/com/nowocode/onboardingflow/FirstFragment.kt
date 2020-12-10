package com.nowocode.onboardingflow

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toolbar
import androidx.navigation.fragment.findNavController
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.nowocode.lib.OnboardingManager
import com.nowocode.lib.ui.model.MessagePosition

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        return inflater.inflate(R.layout.fragment_first, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        view.findViewById<Button>(R.id.button_first).setOnClickListener {
            findNavController().navigate(R.id.action_FirstFragment_to_SecondFragment)
        }

        OnboardingManager
            .instance(requireContext())
            .setActivity(requireActivity())
            .addOnboardingFeature(
                view.findViewById(R.id.textview_first),
                "Check that frigging cool text view! It's so cool!!!",
                null,
                MessagePosition.BOTTOM
            )
            .addOnboardingFeature(
                requireActivity().findViewById<FloatingActionButton>(R.id.button_first),
                "Click that button!",
                null,
                MessagePosition.TOP
            )
            .addOnboardingFeature(
                requireActivity().findViewById<FloatingActionButton>(R.id.fab),
                "Send an email or smth else!",
                null,
                MessagePosition.TOP
            )
            .addOnboardingFeature(
                requireActivity().findViewById<androidx.appcompat.widget.Toolbar>(
                    R.id.toolbar
                ), "Oh, select this amazing toolBAR", null, MessagePosition.BOTTOM
            )
            .start()
    }
}